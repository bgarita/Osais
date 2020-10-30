drop procedure if exists `ReservarFactura`;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `ReservarFactura`(
	IN `pID` int(10),
	IN `pBodega` varchar(3),
	IN `pArtcode` varchar(20),
	IN `pReservado` decimal(12,4),
	IN `pArtprec` decimal(12,2),
	IN `pFacpive` float,
	IN `pFacfech` datetime,
	IN `pFacplazo` tinyint(3),
	IN `pVend` tinyint(3),
	IN `pTerr` tinyint(3),
	IN `pFactipo` tinyint(2),
	IN `pChequeoTar` varchar(45),
	IN `pFacnpag` smallint,
	IN `pPrecio` tinyint(2),
	IN `pCodigoTC` char(3),
	IN `pTipoca` float,
	IN `pAfectarRes` char(20)
)
BEGIN
	-- Autor: Bosco Garita Azofeifa
	-- Este sp suma o resta (según sea el valor recibido) a Faccant y a reservado en
	-- la tabla wrk_fadetall (detalle de la factura) y realiza la suma o resta en el
	-- campo artreserv de la tabla bodexis.  Esta tabla a su vez dispara un trigger
	-- que actualiza el campo artreserv en la tabla principal INARTICU.

	-- El precio unitario debe venir libre de impuesto y descuento porque así se
	-- debe guardar en la tabla fadetall.


	Declare vReservadoAnterior decimal(12,4);  
	Declare vReservadoActual   decimal(12,4);  
	Declare vDisponible        decimal(12,4);  
	Declare vArtcosp           decimal(14,4);  -- Costo promedio (moneda local)
	Declare vArtcost           decimal(14,4);  -- Costo standard (moneda local)
	Declare vResultado         tinyInt(1);     -- 1=Todo salió bien, 0=No se pudo reservar
	Declare vErrorMessage      varchar(100);   -- Contiene el mensaje de error cuando vResultado = 0
	Declare vFacimve           decimal(12,2);  -- Total impuestos
	Declare vFacdesc           decimal(12,2);  -- Total descuento
	Declare vFacmont           decimal(12,2);  -- Total monto bruto
	Declare vFacfepa           datetime;       
	Declare vFacdpago          smallInt;       
	Declare vFacfppago         datetime;       
	Declare vRedondear         bit;            
	Declare vRedondearA5       bit;            
	Declare vExist0            bit;            
	Declare vDispCompras       decimal(12,2);  
	DECLARE vCodigotarifa	  VARCHAR(3);
	DECLARE vCodigoCabys	  VARCHAR(20);


	Set vResultado    = 1;
	Set vErrorMessage = '';

	Set pFacfech     = IfNull(pFacfech,now());
	Set vFacfepa     = AddDate(pFacfech, interval pFacplazo day);
	Set vFacdpago    = 1;
	Set vFacfppago   = pFacfech;
	Set vDispCompras = 0;

  
	-- Determinar los redondeos.  Solo se redondea para la moneda local.
	Set vRedondear =
		 Case When pCodigoTC = (Select codigoTC
								from config) then (Select redondear from config)
			  else 0
		 End;
	Set vRedondearA5 =
		 Case When pCodigoTC = (Select codigoTC
								from config) then (Select redond5 from config)
			  else 0
		 End;

	Set vExist0 = (Select Exist0 from config); 

	If pFacplazo > 0 then
		If pFacnpag <= 0 then
			Set pFacnpag  = 1;
			Set vFacdpago = 1;
		Else
			Set vFacdpago = Round(pFacplazo / pFacnpag,0);

			Set vFacfppago = AddDate(pFacfech, interval vFacdpago day);
		End if;
	End if;

	-- Obtengo los costos estándard y promedio aplicando el tipo de cambio de la transacción.
	-- Set vArtcosp = (Select artcosp / pTipoca from inarticu where artcode = pArtcode);
	/*Select 
		artcosp / pTipoca,
		artcost / pTipoca
	From inarticu
	where artcode = pArtcode
	into vArtcosp, vArtcost;*/
	
	Select 
		artcosp / pTipoca,
		artcost / pTipoca,
		codigoTarifa,
		codigoCabys
	into vArtcosp, vArtcost, vCodigoTarifa, vCodigoCabys
	From inarticu
	where artcode = pArtcode;

	-- Obtengo la cantidad reservada anterior (si hay)
	Set vReservadoAnterior =
		(Select sum(faccant)
		 from wrk_fadetall
		 Where id = pID and Bodega = pBodega and artcode = pArtcode);

	Set vReservadoAnterior = ifnull(vReservadoAnterior,0);
	Set vReservadoActual   = vReservadoAnterior + pReservado;

	If vExist0 = 0 then
		Set vDisponible    = ConsultarExistenciaDisponible(pArtcode,pBodega);
	End if;

	-- Si la cantidad reservada viene negativa es porque el usuario decidió
	-- quitar algunas unidades del reservado en cuyo caso hay que verificar
	-- que el nuevo reservado no quede negativo.
	-- Si el registro no existe en la tabla pedidod entonces lo agrega.
	If vReservadoActual < 0 then
		Set vResultado    = 0;
		Set vErrorMessage = '[BD] La cantidad no puede quedar negativa';
	End if;

	# Bosco modificado 25/09/2011.
	# El artículo _NOINV no valida existencia.
	#If vResultado = 1 then
	If vResultado = 1 and pArtcode != '_NOINV' then
		-- Valido si el disponible cubre el nuevo reservado
		If vDisponible - pReservado < 0 and vExist0 = 0 and pAfectarRes = 'S' then
			Set vResultado    = 0;
			Set vErrorMessage = '[BD] El disponible para este artículo es insuficiente';
		end if;
	End if;

	If vResultado = 1 and pAfectarRes = 'S' then
		Update bodexis
		Set Artreserv = Artreserv + pReservado
		Where Artcode = pArtcode and Bodega = pBodega;

		if row_count() = 0 then
			Set vResultado = 0;
			Set vErrorMessage = '[BD] Ocurrió un error al intentar reservar la cantidad en bodega';
		End if;
	End if;

	-- Actualizo la tabla de facturas (detalle)
	If vResultado = 1 then
		If (Select count(*) from wrk_fadetall
				Where id = pID and Bodega = pBodega and artcode = pArtcode) > 0 then
			Update wrk_fadetall Set
			   faccant   	= vReservadoActual,
			   artprec   	= pArtprec,
			   facmont   	= vReservadoActual * pArtprec,
			   facpive   	= pFacpive,
			   codigoTarifa = vCodigoTarifa,
			   codigocabys = vCodigoCabys,
			   artcosp   	= vArtcosp,
			   artcost	= vArtcost
			Where id = pID and Bodega = pBodega and artcode = pArtcode;
		Else
			Insert into wrk_fadetall (
			  id,
			  artcode,
			  bodega,
			  faccant,
			  artprec,
			  facmont,
			  facpive,
			  codigoTarifa,
			  codigoCabys,
			  artcosp,
			  artcost)
			Values (
			  pID,
			  pArtcode,
			  pBodega,
			  pReservado,
			  pArtprec,
			  pReservado * pArtprec,
			  pFacpive,
			  vCodigoTarifa,
			  vCodigoCabys,
			  vArtcosp,
			  vArtcost);
		End if ; -- if-else

		if row_count() = 0 then
			Set vResultado    = 0;
			Set vErrorMessage = '[BD] No se pudo actualizar la tabla de facturas';
		End if;

		if vResultado > 0 then
			-- Recalcular el impuesto.
			-- Podría aplicarlo condicionado como está arriba pero de todas formas un pedido
			-- no llega a ser muy grande y es más seguro si se recalcula todo el pedido y no
			-- solo la línea que se solicita.
			Update wrk_fadetall Set
				facimve = (facmont - facdesc) * (facpive/100)
			Where id = pID;

			-- Recalcular el encabezado de la factura
			Set vFacimve = (Select sum(facimve) from wrk_fadetall Where id = pID);
			Set vFacdesc = (Select sum(facdesc) from wrk_fadetall Where id = pID);
			Set vFacmont = (Select sum(facmont) from wrk_fadetall Where id = pID);

			Set vFacmont = vFacmont - vFacdesc + vFacimve;

			If vRedondearA5 = 1 then
				Set vFacmont = RedondearA5(vFacmont);
			End if;

			Update wrk_faencabe Set
			  facimve    = vFacimve,
			  facdesc    = vFacdesc,
			  facmont    = vFacmont,
			  facfech    = pFacfech,
			  facplazo   = pFacplazo,
			  facfepa    = vFacfepa,
			  vend       = pVend,
			  terr       = pTerr,
			  factipo    = pFactipo,
			  chequeotar = pChequeotar,
			  facnpag    = pFacnpag,
			  facdpago   = vFacdpago,
			  facfppago  = vFacfppago,
			  facmpag    = facmont / facnpag,
			  facsald    = If(facplazo > 0, facmont, 0),
			  precio     = pPrecio,
			  codigoTC   = pCodigoTC,
			  tipoca     = pTipoca
			Where id = pID;
		End if; 

     
		If pFacplazo > 0 then
			Set vDispCompras =
				  (Select inclient.clilimit - inclient.clisald - wrk_faencabe.facsald
				   from wrk_faencabe
				   Inner Join inclient on wrk_faencabe.clicode = inclient.clicode
				   Where id = pID);

			If (vDispCompras) < 0 then
				Set vResultado    = 0;
				Set vErrorMessage = '[BD] Este artículo sobrepasa el disponible de compras del cliente';
			End if;
		End if; 
	End if;  -- if vResultado = 1

  -- Envío el resultado del proceso como un ResultSet
  Select vResultado, vErrorMessage;
END$$
DELIMITER ;
