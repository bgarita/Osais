
DROP PROCEDURE if EXISTS EjecutarCierreMensual;

Delimiter $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `EjecutarCierreMensual`(
	IN `pMes` tinyint(2),
	IN `pAno` SMALLINT(4),
	OUT `pError` TINYINT(1),
	OUT `pMensajeErr` VARCHAR(1000),
	IN `pEtapa` INT
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
	/*
    	Autor:    Bosco Garita 23/02/2011.
    	Descrip:  Ejecutar el cierre mensual.
    	       Este proceso copia los maestros de:
	    	  - ARTÍCULOS (hinarticu)
		  - EXISTENCIAS (hbodexis)
		  - CLIENTES (hinclient) 
    	       - PROVEEDORES (hinproved)
	    	  - IMPUESTOS (HTARIFA_IVA)
		  a las tablas históricas.  De esa forma se conservan los saldos y los estados
    	       de las tablas más importantes.
    	       Además de copiar los registros de las tablas maestras también establece el mes cerrado para
    	       que no se puedan registrar más movimientos en ese período.
    	Devuelve: Dos variables; una que indica si hubo error (pError) y la otra con el mensaje del error (pMensajeErr)
    	NOTA:     1. Este SP debe correr dentro de una transacción.
    	          2. Antes de correr el proceso debe asegurarse de que los saldos están calculados al período que se va a cerrar.
	Bosco modificado 31/10/2013. Cambio el campo procueco por mayor, sub_cta, sub_sub y colect
	en las tabla históricas de clientes y proveedores.
	Bosco modificado 01/07/2015. Quito el campo divisita de las tablas de proveedores.
	Bosco modificado 10/07/2015. Agrego el control por etapas.
	Bosco modificado 17/07/2018. Agrego el traslado del campo idcliente en las tablas inclient - hinclient
	Bosco modificado 26/06/2019. Agrego el traslado de varios campos nuevos desde inproved hacia hinproved
	Bosco modificaro 25/06/2020. Agrego la tabla de histórico de impuestos y el control de etapas del cierre.
    */
    
    	
	
    	Declare vMesCerrado tinyInt;
    	Declare vAnoCerrado int;
    	Declare vPrimerDiaCerrado datetime;
    	Declare vPrimerDiaaCerrar datetime;
    	Declare vUltimoDiaMes datetime;
	Declare vRegistrosAf int; -- Bosco agregado 24/02/2013
	DECLARE vEtapaConfirmada SMALLINT;
	
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
		GET DIAGNOSTICS CONDITION 1 @sqlstate = RETURNED_SQLSTATE, @errno = MYSQL_ERRNO, @text = MESSAGE_TEXT;
		SET @full_error = CONCAT("ERROR ", @errno, " (", @sqlstate, "): ", @text);
	    	ROLLBACK;
		SET pError = 1;
		SET pMensajeErr = 
		    		CONCAT('[BD] Ocurrió un error en la etapa ', pEtapa, ' del cierre. La etapa fue revertida. ' , @full_error);
	END;
	
	-- Determino cuál fue la última etapa confirmada para este cierre
	SELECT etapaconfirmada FROM etapascierre WHERE mes = pMes AND ano = pAno INTO vEtapaConfirmada;
	
	
	-- Si la etapa no existe...
	if vEtapaConfirmada IS NULL then
		SET vEtapaConfirmada = 0;
	END if;
	    
    	-- Establezco el valor default para el control de errores
    	Set pError = 0;
    	Set pMensajeErr = '';
    	
	-- Las etapas van de uno a donce por ahora 10/07/2015
	If pEtapa is null or pEtapa not between 1 and 15 then
		Set pError = 1;
   		Set pMensajeErr = '[BD] El número de etapas debe ir entre 1-15. Proceso cancelado.';
	End if;
	
	
    	# Validar el mes y el año de cierre
    	# El mes debe estar entre 1-12.  El período de cierre no puede ser inferior al que ya está registrado
    	# en la tabla config.
    	If not pError and (pMes is null or pMes not between 1 and 12) then
		Set pError = 1;
		Set pMensajeErr = '[BD] El mes a cerrar debe ser entre 1-12. Proceso cancelado.';
    	End if;
    	
    	# Se toma el mes y año del último período cerrado y se concatenan para formar una fecha, el último día.
    	# Luego se hace lo mismo con el mes y año que se intenta cerrar y se realizan las siguientes validaciones:
    	# 1) La fecha de cierre no puede ser inferior a la del último cierre.
    	# 2) Entre el período cerrado y el que se va a cerrar no puede haber más de 360 días.
    	If not pError then
    	
    		Select IfNull(mescerrado,1), IfNull(anocerrado,1900) from config into vMesCerrado, vAnoCerrado;
		
		
		If vAnoCerrado = 1900 then
			Select vAnoCerrado = min(year(movfech)) from inmovime;
		End if;
         
        	Set vPrimerDiaCerrado = Concat(Cast(vAnoCerrado as char(4)),'-',Cast(vMesCerrado as char(2)),'-01 00:00:00');
        	Set vPrimerDiaaCerrar = Concat(Cast(pAno        as char(4)),'-',Cast(pMes        as char(2)),'-01 00:00:00');
        
        	If not pError and vPrimerDiaaCerrar <= vPrimerDiaCerrado then
            	Set pError = 1;
            	Set pMensajeErr = '[BD] El mes a cerrar ya está cerrado. Proceso cancelado.';
        	End if;
        
        	If not pError && DateDiff(vPrimerDiaaCerrar,vPrimerDiaCerrado) > 360 and vAnoCerrado <> 1900 then
	     	Set pError = 1;
	          Set pMensajeErr = '[BD] No se permite períodos de más de 360 días. Proceso cancelado.';
        	End if;
    	End if;
	
	Set vRegistrosAf = 0; -- Bosco agregado 24/02/2013

	# Verifico que todas las facturas hayan pasado a inventarios.
	If not pError then
		Call FacturacionVsInventario (vRegistrosAf);
		Set pError = If(vRegistrosAf > 0, 1, 0);
		
		If pError then
			Set pMensajeErr = '[BD] Hay registros de facturación que no están en inventarios. Proceso cancelado.';
		End if;
	End if;
    
	# ------------------ PRIMERA ETAPA ------------------
	-- Bosco agregado 24/02/2013.Bosco agregado 24/02/2013.
	# Verifico que todas las facturas tengan su respectivo detalle.
	If not pError and pEtapa = 1 and vEtapaConfirmada = 0 then
		
		Call EncabezadoFacturasVsDetalle (vRegistrosAf);
		Set pError = If(vRegistrosAf > 0, 1, 0);
		If pError then
			Set pMensajeErr = '[BD] Hay facturas sin detalle. Proceso cancelado.';
		End if;
		
		If NOT pError then
			START TRANSACTION;
			INSERT INTO etapascierre (mes, ano, etapaconfirmada, usuario)
			VALUES(pMes, pAno, vEtapaConfirmada + 1, USER());
			COMMIT;
		END if;
		
	End if;
    

	# ------------------ SEGUNDA ETAPA ------------------
	# Verifico que todos los documentos de inventario tengan su respectivo detalle.
	If not pError and pEtapa = 2 and vEtapaConfirmada = 1 then
	
		Call EncabezadoInvVsDetalle (vRegistrosAf);
		Set pError = If(vRegistrosAf > 0, 1, 0);
		If pError then
			Set pMensajeErr = '[BD] Hay documentos de inventario sin detalle. Proceso cancelado.';
		End if;
		
		If NOT pError then
			START TRANSACTION;
			UPDATE etapascierre
				SET etapaconfirmada = vEtapaConfirmada + 1, usuario = USER(), fecha = SYSDATE()
			WHERE mes = pMes AND ano = pAno;
			COMMIT;
		END if;
		
	End if;
	-- Fin Bosco agregado 24/02/2013.


	# Fecha para el cierre (incluye hora :23:59:59)
	Set vUltimoDiaMes = UltimoDiaDelMes(pMes, pAno);


    	# Incia proceso revisión de datos
    	If not pError then
    	
		# ------------------ TERCERA ETAPA ------------------
		If pEtapa = 3 and vEtapaConfirmada = 2 then
		
			START TRANSACTION;
			Call EliminarInconsistencias();
				UPDATE etapascierre
				SET etapaconfirmada = vEtapaConfirmada + 1, usuario = USER(), fecha = SYSDATE()
			WHERE mes = pMes AND ano = pAno;
			COMMIT;
			
		End if;
        
		
		# ------------------ CUARTA ETAPA ------------------
		If pEtapa = 4 and vEtapaConfirmada = 3 then
		
			START TRANSACTION;
			# Por ahora el reservado será actual, no es a la fecha de cierre necesariamente.
			Call RecalcularReservado(null);
				UPDATE etapascierre
				SET etapaconfirmada = vEtapaConfirmada + 1, usuario = USER(), fecha = SYSDATE()
			WHERE mes = pMes AND ano = pAno;
			COMMIT;
			
		End if;
        
	
		# ------------------ QUINTA ETAPA ------------------
		If pEtapa = 5 and vEtapaConfirmada = 4 then
		
			START TRANSACTION;
			# Establecer el saldo de los registros de CXC (fact, nc, nd) a la fecha de cierre.
			# Este proceso genera una tabla temporal (tmp_faencabe) con el estado (saldo) de las fact, NC y ND
			# a la fecha que se le indique.  En este caso la fecha de cierre.
			Call CalcularCXC(vUltimoDiaMes);
				UPDATE etapascierre
				SET etapaconfirmada = vEtapaConfirmada + 1, usuario = USER(), fecha = NOW()
			WHERE mes = pMes AND ano = pAno;
			COMMIT;
			
        	End if;
	
	
		# ------------------ SEXTA ETAPA ------------------
		If pEtapa = 6 and vEtapaConfirmada = 5 then
		
			START TRANSACTION;
			# Establecer el saldo de los clientes a la fecha de cierre. Este proceso usa la tabla creada por el 
			# SP CalcularCXC() para establecer el saldo de los clientes a la fecha que se le pida.
			Call RecalcularSaldoClientes_Cierre();
				UPDATE etapascierre
				SET etapaconfirmada = vEtapaConfirmada + 1, usuario = USER(), fecha = SYSDATE()
			WHERE mes = pMes AND ano = pAno;
			COMMIT;
			
		End if;
        

		# ------------------ SEPTIMA ETAPA ------------------
		If pEtapa = 7 and vEtapaConfirmada = 6 then
		
			START TRANSACTION;
			# Recalcular las existencias a la fecha de cierre. El segundo parámetro indica que es cierre.
			Call RecalcularExistencias(vUltimoDiaMes,1);
			
			# Elimino la tabla temporal
			Drop temporary table If Exists tmp_faencabe;
				UPDATE etapascierre
				SET etapaconfirmada = vEtapaConfirmada + 1, usuario = USER(), fecha = SYSDATE()
			WHERE mes = pMes AND ano = pAno;
			COMMIT;
			
		End if;
    	End if;
    	# Fin de revisión de datos
    	
	
	
    	# ==========================================================================================================
	# ------------------ OCTAVA ETAPA ------------------
    	# Inicia proceso de copiado a las tablas históricas
    	
    	If not pError AND pEtapa = 8 and vEtapaConfirmada = 7 then
        
        START TRANSACTION;
        
        # Guardo las existencias y el estado de los artículos de inventario
        INSERT INTO `hinarticu`(
            `artcode`, `artdesc`, `barcode`,
            `artfam`,  `artcosd`, `artcost`,
            `artcosp`, `artcosa`, `artcosfob`,
            `artpre1`, `artgan1`, `artpre2`,
            `artgan2`, `artpre3`, `artgan3`,
            `artpre4`, `artgan4`, `artpre5`,
            `artgan5`, `procode`, `artmaxi`,
            `artmini`, `artiseg`, `artdurp`,
            `artfech`, `artfeuc`, `artfeus`,
            `artexis`, `artreserv`,
            `transito`,`otroc`,   `altarot`,
            `vinternet`,`artObse`,
            `artFoto`,`artperi`,`codigoTarifa`)
        SELECT
            artcode,   artdesc,   barcode,   
            artfam,    artcosd,   artcost,
            artcosp,   artcosa,   artcosfob,
            artpre1,   artgan1,   artpre2,
            artgan2,   artpre3,   artgan3,
            artpre4,   artgan4,   artpre5,
            artgan5,   procode,   artmaxi,
            artmini,   artiseg,   artdurp,
            artfech,   artfeuc,   artfeus,
            artexis,   artreserv,
            transito,  otroc,     altarot,
            vinternet, artObse,
            artFoto,   vUltimoDiaMes, codigoTarifa
        FROM inarticu;
        
        # Guardo las existencias por bodega
        INSERT INTO `hbodexis`(
            `bodega`,
            `artcode`,
            `artexis`,
            `artreserv`,
            `minimo`,
            `artperi`)
        Select 
            bodega,
            artcode,
            artexis,
            artreserv,
            minimo,
            vUltimoDiaMes
        From bodexis;

        # Guardo los clientes, sus saldos y demás características
        INSERT INTO `hinclient`(
          	`clicode`,   `clidesc`,    `clidir`,
			`clitel1`,   `clitel2`,    `clitel3`,
			`clifax`,    `cliapar`,    `clinaci`,
			`clisald`,   `cliprec`,    `clilimit`,
			`terr`,      `vend`,       `clasif`,
			`cliplaz`,   `exento`,     `clifeuc`,
			`encomienda`,`direncom`,   `facconiv`,
			`clinpag`,   `clicelu`,    `cliemail`,
			`clireor`,   `igsitcred`,  `credcerrado`,
			`diatramite`,`horatramite`,`diapago`,
			`horapago`,  `clicueba`,   `cligenerico`,
			`cliperi`,   `mayor`,  	`sub_cta`,
			`sub_sub`, `colect`, 	`idcliente`,
			`idtipo`)
        Select 
			clicode,    clidesc,    clidir,
			clitel1,    clitel2,    clitel3,
			clifax,     cliapar,    clinaci,
			clisald,    cliprec,    clilimit,
			terr,       vend,       clasif,
			cliplaz,    exento,     clifeuc,
			encomienda, direncom,   facconiv,
			clinpag,    clicelu,    cliemail,
			clireor,    igsitcred,  credcerrado,
			diatramite, horatramite,diapago,
			horapago,   clicueba,   cligenerico,
			vUltimoDiaMes,mayor, 	sub_cta,    
			sub_sub, 	colect,		idcliente,
			idtipo
        From inclient;

        # Guardo los datos de los proveedores
        INSERT INTO `hinproved`(
			`procode`,
			`prodesc`,
			`prodir` ,
			`protel1`,
			`protel2`,
			`profax` ,
			`proapar`,
			`pronac` ,
			`profeuc`,
			`promouc`,
			`prosald`,
			`proplaz`,
			`procueba`,
			`mayor`  , 	   
			`sub_cta`,
			`sub_sub`, 	 
			`colect` ,
			`email`  ,
			`idProv` ,
			`idTipo` ,
			`provincia`,
			`canton`  ,
			`distrito`,
			`properi`
			)
        Select
			procode,
			prodesc,
			prodir ,
			protel1,
			protel2,
			profax ,
			proapar,
			pronac ,
			profeuc,
			promouc,
			prosald,
			proplaz,
			procueba,
			mayor  , 	   
			sub_cta,
			sub_sub, 	 
			colect ,
			email  ,
			idProv ,
			idTipo ,
			provincia,
			canton ,
			distrito,
			vUltimoDiaMes
        From inproved;
        
        -- Histórico de tarifas según el Ministerio de Hacienda
        INSERT INTO htarifa_iva (codigoTarifa, descrip, porcentaje, periodo)
        		SELECT codigoTarifa, descrip, porcentaje, vUltimoDiaMes
        		FROM tarifa_iva;
        	
	   UPDATE etapascierre
			SET etapaconfirmada = vEtapaConfirmada + 1, usuario = USER(), fecha = NOW()
	   WHERE mes = pMes AND ano = pAno;
	   COMMIT;
        	
    End if; -- If not pError AND  pEtapa = 8
    
    # ==========================================================================================================
    # Finaliza proceso de copiado a las tablas históricas
    
    
    # Inicia proceso de marcar los registros como cerrados y cálculo de saldos
    If not pError then

		# ------------------ NOVENA ETAPA ------------------
		If pEtapa = 9 and vEtapaConfirmada = 8 then
		
			START TRANSACTION;
			
			# Marcar las facturas, NC y ND con saldo cero como cerradas.
			# Estos registros tienen el saldo actual, no el de la fecha de cierre necesariamente.
			# Habrá que considerar si es necesario tomar el valor de tmp_faencabe y de esa forma 
			# afectar faencabe con los valores de cierre.
			Update faencabe
			Set facCerrado = 'S'
			Where facsald = 0 and facCerrado = 'N' and facfech <= vUltimoDiaMes ;
			
			UPDATE etapascierre
			SET etapaconfirmada = vEtapaConfirmada + 1, usuario = USER(), fecha = SYSDATE()
		     WHERE mes = pMes AND ano = pAno;
		     COMMIT;
			
		End if;

		# ------------------ DÉCIMA ETAPA ------------------
		If pEtapa = 10 and vEtapaConfirmada = 9 then
		
			START TRANSACTION;
			
			# Podría contar aquí los registros que no sean de ND para luego compararlos contra los que 
			# se actualizan en INMOVIME pero parece no ser necesario porque hay un proceso previo que
			# verifica la integridad de los documentos de CXC contra inventarios se llama FacturacionVsInventario()
			# Select count(facnume) from faencabe where facCerrado = 'S' and facnd >= 0;
			
			# Marcar todos los registros de CXC en inventarios como cerrados
			Update inmovime
			Inner join intiposdoc on intiposdoc.Movtido = inmovime.Movtido
				Set movCerrado = 'S'
			Where movCerrado = 'N' 
			and movfech <= vUltimoDiaMes and intiposdoc.Modulo = 'CXC'
			# Verificar que las facturas existan en faencabe.  Las ND no forman parte de los movimientos de inv.
			and Exists(Select facnume from faencabe
					   Where cast(facnume AS char(10)) = inmovime.movdocu
					   and facnd = 0 -- Facturas
					   and facCerrado = 'S'
					   and facfech <= vUltimoDiaMes);

			Update inmovime
			Inner join intiposdoc on intiposdoc.Movtido = inmovime.Movtido
				Set movCerrado = 'S'
			Where movCerrado = 'N' 
			and movfech <= vUltimoDiaMes and intiposdoc.Modulo = 'CXC'
			# Verificar que las NC existan en faencabe.  Las ND no forman parte de los movimientos de inv.
			and Exists(Select facnume from faencabe
					   Where facnume < 0
					   and facnd > 0 -- Notas de crédito
					   and facCerrado = 'S' 
					   and Cast(Abs(facnume) as char(10)) = inmovime.movdocu
					   and facfech <= vUltimoDiaMes);
					   
			UPDATE etapascierre
			SET etapaconfirmada = vEtapaConfirmada + 1, usuario = USER(), fecha = SYSDATE()
		     WHERE mes = pMes AND ano = pAno;
		     COMMIT;
		     
		End if;

		# ------------------ ONCEAVA ETAPA ------------------
		If pEtapa = 11 and vEtapaConfirmada = 10 then
		
			START TRANSACTION;
			
			# Marcar todos los movimientos de inventarios como cerrados.
			Update inmovime
			Inner join intiposdoc on intiposdoc.Movtido = inmovime.Movtido
				Set movCerrado = 'S'
			Where movCerrado = 'N'
			and intiposdoc.Modulo = 'INV'
			and movfech <= vUltimoDiaMes;
			
			UPDATE etapascierre
			SET etapaconfirmada = vEtapaConfirmada + 1, usuario = USER(), fecha = SYSDATE()
		     WHERE mes = pMes AND ano = pAno;
		     COMMIT;
			
		End if;

		# ------------------ DOCEAVA ETAPA ------------------
		If pEtapa = 12 and vEtapaConfirmada = 11 then
		
			START TRANSACTION;
			
			# Marcar los recibos de CXC como cerrados
			Update pagos
			Set cerrado = 'S' 
			Where cerrado = 'N' and fecha <= vUltimoDiaMes;
			
			-- Bosco agregado 14/03/2013
			# Marcar los recibos de CXC como cerrados
			Update cxppage
			Set cerrado = 'S' 
			Where cerrado = 'N' and fecha <= vUltimoDiaMes;

			# Marcar las facturas, NC y ND con saldo cero como cerradas.
			# Estos registros tienen el saldo actual.
			Update cxpfacturas
			Set Cerrado = 'S'
			Where Cerrado = 'N' and fecha_fac <= vUltimoDiaMes and saldo = 0;
			-- Fin Bosco agregado 14/03/2013
			
			UPDATE etapascierre
			SET etapaconfirmada = vEtapaConfirmada + 1, usuario = USER(), fecha = SYSDATE()
		     WHERE mes = pMes AND ano = pAno;
		     COMMIT;
			
		End if; -- if pEtapa = 12


		# ------------------ TRECEAVA ETAPA ------------------
        	IF pEtapa = 13 and vEtapaConfirmada = 12 then
        	
        		START TRANSACTION;
			# Recalcular el saldo de las facturas. Este proceso no es indispensable pero preferible.
			Call RecalcularSaldoFacturas();
			
			UPDATE etapascierre
			SET etapaconfirmada = vEtapaConfirmada + 1, usuario = USER(), fecha = SYSDATE()
		     WHERE mes = pMes AND ano = pAno;
		     COMMIT;
			
		End if;


        	# ------------------ CATORCEAVA ETAPA ------------------
		IF pEtapa = 14 and vEtapaConfirmada = 13 then
		
			START TRANSACTION;
			# Recalcular el saldo de todos los clientes.  Este proceso SI es indispensable.
			Call RecalcularSaldoClientes(null); -- El saldo de los clientes depende del saldo de las facturas
			
			UPDATE etapascierre
			SET etapaconfirmada = vEtapaConfirmada + 1, usuario = USER(), fecha = SYSDATE()
		     WHERE mes = pMes AND ano = pAno;
		     COMMIT;
			
		End if;


		# ------------------ QUINCEAVA ETAPA ------------------
		IF pEtapa = 15 and vEtapaConfirmada = 14 then
		
			START TRANSACTION;
			# Recalcular las existencias a hoy.
			Call RecalcularExistencias(now(),1);
			# Establecer la fecha de cierre en bodegas
			Update bodegas Set cerrada = vUltimoDiaMes Where cerrada is null or cerrada < vUltimoDiaMes;
			
			# Establecer el período cerrado en la tabla config.  También se guarda la fecha y hora actual.
			Update config Set mescerrado = pMes, anocerrado = pAno, cierre = now();
			
			UPDATE etapascierre
			SET etapaconfirmada = vEtapaConfirmada + 1, usuario = USER(), fecha = SYSDATE()
		     WHERE mes = pMes AND ano = pAno;
		     COMMIT;
			
		End if;
    End if;  -- If not pError
    
    -- Si hubo error se revierte la etapa
    If pError then
    		ROLLBACK;
    END if;
    
END$$

delimiter ;