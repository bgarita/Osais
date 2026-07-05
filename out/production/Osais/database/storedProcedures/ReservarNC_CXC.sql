DROP PROCEDURE if EXISTS ReservarNC_CXC;

Delimiter $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `ReservarNC_CXC`(
	IN `pID` int(10),
	IN `pBodega` varchar(3),
	IN `pArtcode` varchar(20),
	IN `pFaccant` decimal(12,4),
	IN `pArtprec` decimal(12,2),
	IN `pFacpive` float,
	IN `pFacfech` datetime,
	IN `pVend` tinyint(3),
	IN `pTerr` tinyint(3),
	IN `pPrecio` tinyint(2),
	IN `pCodigoTC` char(3),
	IN `pTipoca` float,
	IN `pFacpdesc` FLOAT
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN

	# Bosco Garita Azofeifa 07/03/2010

	# Este SP reserva los artículos para las notas de crédito
	Declare pFaccantAnterior decimal(12,4);  
	Declare pFaccantActual   decimal(12,4);  
	Declare vArtcosp         decimal(14,4);  
	Declare vResultado       tinyInt(1);     
	Declare vErrorMessage    varchar(100);   
	Declare vFacimve         decimal(12,2);  
	Declare vFacdesc         decimal(12,2);  
	Declare vFacmont         decimal(12,2);  
	Declare vFacfepa         datetime;       
	Declare vFacfppago       datetime;       
	Declare vRedondear       bit;            
	Declare vRedondearA5     bit;       
	DECLARE vCodigotarifa	VARCHAR(3);
	DECLARE vCodigoCabys	VARCHAR(20);     


	-- Setear las variables para el control de errores
	Set vResultado    = 1;
	Set vErrorMessage = '';

	Set pFacfech   = IfNull(pFacfech,now());
	Set vFacfepa   = AddDate(pFacfech, interval 1 day);
	Set vFacfppago = vFacfepa;



	-- Verificar el redondeo de los decimales
	Set vRedondear =
		 Case When pCodigoTC = (Select codigoTC from config) then (Select redondear from config)
			  else 0
		 End;


	-- Verificar el redondeo a 5 y 10
	Set vRedondearA5 =
		 Case When pCodigoTC = (Select codigoTC from config) then (Select redond5 from config)
			  else 0
		 End;


	-- Obtener el costo promedio
	-- Set vArtcosp = (Select artcosp from inarticu where artcode = pArtcode);
	Select 
		artcosp,
		codigoTarifa,
		codigoCabys
	into vArtcosp, vCodigoTarifa, vCodigoCabys
	From inarticu
	where artcode = pArtcode;


	-- Sumar la cantidad registrada (tabla de trabajo) antes de este artículo
	Set pFaccantAnterior =
		(Select sum(faccant)
		 from wrk_fadetall
		 Where id = pID and Bodega = pBodega and artcode = pArtcode);

	  Set pFaccantAnterior = ifnull(pFaccantAnterior,0);
	  Set pFaccantActual   = pFaccantAnterior + pFaccant;

	-- Validar los negativos
	If pFaccantActual < 0 then
		 Set vResultado    = 0;
		 Set vErrorMessage = '[BD] La cantidad no puede quedar negativa';
	End if;


	-- Si el artículo ya existe se suma (o resta),
	-- caso contrario se agrega.

	If (Select count(*)
		   from wrk_fadetall
		   Where id = pID and Bodega = pBodega and artcode = pArtcode) > 0 then
		Update wrk_fadetall Set
			faccant   = pFaccantActual,
			artprec   = pArtprec,
			facmont   = pFaccantActual * pArtprec,
			facpive   = pFacpive,
			codigoTarifa = pCodigoTarifa,
			codigocabys = vCodigoCabys,
			artcosp   = vArtcosp
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
			facpdesc)
		Values (
			pID,
			pArtcode,
			pBodega,
			pFaccant,
			pArtprec,
			pFaccant * pArtprec,
			pFacpive,
			vCodigoTarifa,
			vCodigoCabys,
			vArtcosp,
			pFacpdesc);
	End if; 

	if row_count() = 0 then
		Set vResultado    = 0;
		Set vErrorMessage = '[BD] No se pudo actualizar la tabla de detalle (NC)';
	End if;

	-- Si se afectaron registros proceso el resto de los datos
	if vResultado > 0 then
		-- Calcular el imnpuesto y el descuento
		Update wrk_fadetall Set
			facdesc = facmont * (facpdesc/100),
			facimve = (facmont - facdesc) * (facpive/100)
		Where id = pID;

		-- Obtener los totales para el encabezado de la NC
		Set vFacimve = (Select sum(facimve) from wrk_fadetall Where id = pID);
		Set vFacdesc = (Select sum(facdesc) from wrk_fadetall Where id = pID);
		Set vFacmont = (Select sum(facmont) from wrk_fadetall Where id = pID);
		Set vFacmont = vFacmont - vFacdesc + vFacimve;

		-- Procesar los redondeos
		if vRedondear = 1 then
			Set vFacmont = Round(vFacmont,2);
		end if;

		if vRedondearA5 = 1 then
			Set vFacmont = RedondearA5(vFacmont);
		end if;

		Update wrk_faencabe Set
			facimve    = vFacimve,
			facdesc    = vFacdesc,
			facmont    = vFacmont,
			facfech    = pFacfech,
			facplazo   = 1,
			facfepa    = vFacfepa,
			vend       = pVend,
			terr       = pTerr,
			factipo    = 0,     
			chequeotar = '',
			facnpag    = 1,
			facdpago   = 1,
			facfppago  = vFacfppago,
			facmpag    = facmont / facnpag,
			facsald    = If(facplazo > 0, facmont, 0),
			precio     = pPrecio,
			codigoTC   = pCodigoTC,
			tipoca     = pTipoca
		Where id = pID;

	End if; -- if vResultado > 0

	Select vResultado, vErrorMessage;
END$$
Delimiter ;