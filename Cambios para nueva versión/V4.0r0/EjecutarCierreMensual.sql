USE `LaFlor`;
DROP procedure IF EXISTS `EjecutarCierreMensual`;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `EjecutarCierreMensual`(
  IN   `pMes`         tinyint(2),		-- Mes
  IN   `pAno`         smallint(4),		-- Año
  OUT  `pError`       tinyint(1),		-- 1=Error, 0=No hay error
  OUT  `pMensajeErr`  varchar(1000),	-- Mensaje del error
  IN   `pEtapa`  	  tinyInt(2)		-- Número de etapa
)
BEGIN
    # Autor:    Bosco Garita 23/02/2011.
    # Descrip:  Ejecutar el cierre mensual.
    #           Este proceso copia los maestros de ARTÍCULOS (hinarticu), EXISTENCIAS (hbodexis), CLIENTES (hinclient) 
    #           Y PROVEEDORES (hinproved) a las tablas históricas.  De esa forma se conservan los saldos y los estados
    #           de las tablas más importantes.
    #           Además de copiar los registros de las tablas maestras también establece el mes cerrado para
    #           que no se puedan registrar más movimientos en ese período.
    # Devuelve: Dos variables; una que indica si hubo error (pError) y la otra con el mensaje del error (pMensajeErr)
    # NOTA:     1. Este SP debe correr dentro de una transacción.
    #           2. Antes de correr el proceso debe asegurarse de que los saldos están calculados al período que se va a cerrar.
	# Bosco modificado 31/10/2013. Cambio el campo procueco por mayor, sub_cta, sub_sub y colect
	#			en las tabla históricas de clientes y proveedores.
	# Bosco modificado 01/07/2015. Quito el campo divisita de las tablas de proveedores.
	# Bosco modificado 10/07/2015. Agrego el control por etapas.
	# Bosco modificado 17/07/2018. Agrego el traslado del campo idcliente en las tablas inclient - hinclient
    
    Declare vMesCerrado tinyInt;
    Declare vAnoCerrado int;
    Declare vPrimerDiaCerrado datetime;
    Declare vPrimerDiaaCerrar datetime;
    Declare vUltimoDiaMes datetime;
	Declare vRegistrosAf int; -- Bosco agregado 24/02/2013
    
    # Establezco el valor default para el control de errores
    Set pError = 0;
    Set pMensajeErr = '';
    
	# Las etapas van de uno a donce por ahora 10/07/2015
	If pEtapa is null or pEtapa not between 1 and 15 then
		Set pError = 1;
        Set pMensajeErr = '[BD] El númerod de etapas debe ir entre 1-12. Proceso cancelado.';
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
        Select IfNull(mescerrado,1),IfNull(anocerrado,1900) from config into vMesCerrado, vAnoCerrado;
		If vAnoCerrado = 1900 then
			Select min(year(movfech)) from inmovime into vAnoCerrado;
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
			Set pMensajeErr = 
				'[BD] Hay registros de facturación que no están en inventarios. Proceso cancelado.';
		End if;
	End if;
    
	# ------------------ PRIMERA ETAPA ------------------
	-- Bosco agregado 24/02/2013.Bosco agregado 24/02/2013.
	# Verifico que todas las facturas tengan su respectivo detalle.
	If not pError and pEtapa = 1 then
		Call EncabezadoFacturasVsDetalle (vRegistrosAf);
		Set pError = If(vRegistrosAf > 0, 1, 0);
		If pError then
			Set pMensajeErr = '[BD] Hay facturas sin detalle. Proceso cancelado.';
		End if;
	End if;
    

	# ------------------ SEGUNDA ETAPA ------------------
	# Verifico que todos los documentos de inventario tengan su respectivo detalle.
	If not pError and pEtapa = 1 then
		Call EncabezadoInvVsDetalle (vRegistrosAf);
		Set pError = If(vRegistrosAf > 0, 1, 0);
		If pError then
			Set pMensajeErr = '[BD] Hay documentos de inventario sin detalle. Proceso cancelado.';
		End if;
	End if;
	-- Fin Bosco agregado 24/02/2013.



	# Fecha para el cierre (incluye hora :23:59:59)
	Set vUltimoDiaMes = UltimoDiaDelMes(pMes,pAno);



    # Incia proceso revisión de datos
    If not pError then
		# ------------------ TERCERA ETAPA ------------------
		If pEtapa = 3 then
			Call EliminarInconsistencias();
		End if;
        
		
		# ------------------ CUARTA ETAPA ------------------
		If pEtapa = 4 then
			# Por ahora el reservado será actual, no es a la fecha de cierre necesariamente.
			Call RecalcularReservado(null);
		End if;
        

		# ------------------ QUINTA ETAPA ------------------
		If pEtapa = 5 then
			# Establecer el saldo de los registros de CXC (fact, nc, nd) a la fecha de cierre.
			# Este proceso genera una tabla temporal (tmp_faencabe) con el estado (saldo) de las fact, NC y ND
			# a la fecha que se le indique.  En este caso la fecha de cierre.
			Call CalcularCXC(vUltimoDiaMes);
        End if;


		# ------------------ SEXTA ETAPA ------------------
		If pEtapa = 6 then
			# Establecer el saldo de los clientes a la fecha de cierre. Este proceso usa la tabla creada por el 
			# SP CalcularCXC() para establecer el saldo de los clientes a la fecha que se le pida.
			Call RecalcularSaldoClientes_Cierre();
		End if;
        

		# ------------------ SEPTIMA ETAPA ------------------
		If pEtapa = 7 then
			# Recalcular las existencias a la fecha de cierre. El segundo parámetro indica que es cierre.
			Call RecalcularExistencias(vUltimoDiaMes,1);
			
			# Elimino la tabla temporal
			Drop temporary table If Exists tmp_faencabe;
		End if;
    End if;
    # Fin de revisión de datos
    


    # ==========================================================================================================
	# ------------------ OCTAVA ETAPA ------------------
    # Inicia proceso de copiado a las tablas históricas
    
    If not pError AND pEtapa = 8 then
        
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
            `artimpv`, `artexis`, `artreserv`,
            `transito`,`otroc`,   `altarot`,
            `vinternet`,`artusaIVG`,`artObse`,
            `artFoto`,  `artperi`)
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
            artimpv,   artexis,   artreserv,
            transito,  otroc,     altarot,
            vinternet, artusaIVG, artObse,
            artFoto,   vUltimoDiaMes
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
			`cliperi`,   `mayor`, 	   `sub_cta`,
			`sub_sub`, 	 `colect`,		`idcliente`)
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
			sub_sub, 	colect,		idcliente
        From inclient;

        # Guardo los datos de los proveedores
        INSERT INTO `hinproved`(
            `procode`,
            `prodesc`,
            `prodir`,
            `protel1`,
            `protel2`,
            `profax`,
            `proapar`,
            `pronac`,
            `profeuc`,
            `promouc`,
            `prosald`,
            `proplaz`,
            `mayor`, 	   
			`sub_cta`,
			`sub_sub`, 	 
			`colect`,
            `procueba`,
            `properi`)
        Select
            procode,
            prodesc,
            prodir,
            protel1,
            protel2,
            profax,
            proapar,
            pronac,
            profeuc,
            promouc,
            prosald,
            proplaz,
            mayor, 	   
			sub_cta,
			sub_sub, 	 
			colect,
            procueba,
            vUltimoDiaMes
        From inproved;
    End if; -- If not pError AND  pEtapa = 8
    
    # ==========================================================================================================
    # Finaliza proceso de copiado a las tablas históricas
    
    
    # Inicia proceso de marcar los registros como cerrados y cálculo de saldos
    If not pError then

		# ------------------ NOVENA ETAPA ------------------
		If pEtapa = 9 then
			# Marcar las facturas, NC y ND con saldo cero como cerradas.
			# Estos registros tienen el saldo actual, no el de la fecha de cierre necesariamente.
			# Habrá que considerar si es necesario tomar el valor de tmp_faencabe y de esa forma 
			# afectar faencabe con los valores de cierre.
			Update faencabe
			Set facCerrado = 'S'
			Where facsald = 0 and facCerrado = 'N' and facfech <= vUltimoDiaMes ;
		End if;

		# ------------------ DÉCIMA ETAPA ------------------
		If pEtapa = 10 then
			
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
					   Where facnume = inmovime.movdocu
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
		End if;

		# ------------------ ONCEAVA ETAPA ------------------
		If pEtapa = 11 then
			# Marcar todos los movimientos de inventarios como cerrados.
			Update inmovime
			Inner join intiposdoc on intiposdoc.Movtido = inmovime.Movtido
				Set movCerrado = 'S'
			Where movCerrado = 'N'
			and intiposdoc.Modulo = 'INV'
			and movfech <= vUltimoDiaMes;
		End if;

		# ------------------ DOCEAVA ETAPA ------------------
		If pEtapa = 12 then
			
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
		End if; -- if pEtapa = 12


		# ------------------ TRECEAVA ETAPA ------------------
        IF pEtapa = 13 then
			# Recalcular el saldo de las facturas. Este proceso no es indispensable pero preferible.
			Call RecalcularSaldoFacturas();
		End if;


        # ------------------ CATORCEAVA ETAPA ------------------
		IF pEtapa = 14 then
			# Recalcular el saldo de todos los clientes.  Este proceso SI es indispensable.
			Call RecalcularSaldoClientes(null); -- El saldo de los clientes depende del saldo de las facturas
		End if;


		# ------------------ QUINCEAVA ETAPA ------------------
		IF pEtapa = 15 then
			# Recalcular las existencias a hoy.
			Call RecalcularExistencias(now(),1);
			# Establecer la fecha de cierre en bodegas
			Update bodegas Set cerrada = vUltimoDiaMes Where cerrada is null or cerrada < vUltimoDiaMes;
			
			# Establecer el período cerrado en la tabla config.  También se guarda la fecha y hora actual.
			Update config Set mescerrado = pMes, anocerrado = pAno, cierre = now();
		End if;
    End if;  -- If not pError
    
END$$
DELIMITER ;
