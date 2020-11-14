DROP PROCEDURE if EXISTS `ImprimirFacturaoND`;

Delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `ImprimirFacturaoND`(
	IN `pnfacnume` int,
	IN `pnfacnd` int
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT 'Author: Bosco Garita'
BEGIN
	-- Autor: Bosco Garita Azofeifa
  	Declare vcEmpresa  varchar(60);
	Declare vcTelefono varchar(30);
	Declare vcCedulaJu varchar(50);
	Declare vcDireccio varchar(200);
	Declare vcTimbre   varchar(40);
	Declare vnRedond   tinyInt(1);
	Declare vcFactext  varchar(1000);
	Declare vnFacligenerico tinyInt(1); 	-- Bosco agregado 28/05/2013.
	Declare vcClidesc  varchar(50);	  	-- Bosco agregado 28/05/2013.
	
	Set vcEmpresa  = (Select empresa from config);
	Set vcTelefono = (Select Concat('TELÉFONO: ', telefono1) from config);
	Set vcCedulaJu = (Select Concat('CÉDULA JURÍDICA : ', cedulajur) from config);
	Set vcDireccio = (Select Direccion from config);
	Set vnRedond   = (Select redondear from config);
	Set vcFactext  = IfNull((Select factext from fatext where facnume = pnfacnume),'');
	
	# Si se trata de un cliente genérico entonces el nombre hay que tomarlo de
	# la tabla faclientescontado.
	SET vnFacligenerico = (
		SELECT vnFacligenerico
		FROM inclient, faencabe
		Where inclient.clicode = faencabe.clicode 
		and facnume = pnfacnume and facnd = pnfacnd);
	
	If vnFacligenerico = 1 then
		SET vcClidesc = (
			SELECT clidesc
			from faclientescontado 
			Where facnume = pnFacnume and facnd = pnFacnd);
	End if;
	
	-- Agrego los campos (de Hacienda), consecutivo y clave numérica.
	-- Tipo de pago (0 = Desconocido, 1 = Efectivo, 2 = cheque, 3 = tarjeta, 4 = Transferencia)
	Select
		vcEmpresa  as empresa,
		vcTelefono as telefono,
		vcCedulaJu as cedulajur,
		vcDireccio as Direccion,
		monedas.simbolo,
		faencabe.facplazo,
		If(faencabe.facplazo = 0,'CONTADO',Concat('CREDITO A ',Cast(faencabe.facplazo as char(3)),' DIAS')) as condiciones,
		CASE faencabe.factipo
			When 2 then Concat('Cheque # ' ,faencabe.chequeotar,' ',(Select descrip from babanco where idbanco   = faencabe.idbanco)) 
			When 3 then Concat('Tarjeta # ',faencabe.chequeotar,' ',(Select descrip from tarjeta where idtarjeta = faencabe.idtarjeta))
			When 4 then Concat('Transf.#'  ,faencabe.chequeotar,' ',(Select descrip from babanco where idbanco   = faencabe.idbanco))
			Else ''
		End as chequeotar,
		If(vnRedond = 1,Round(fadetall.artprec,0),fadetall.artprec) as artprec,
		If(vnRedond = 1,Round(fadetall.facmont,0),fadetall.facmont) as totalB,
		Cast(If(vnRedond = 1,
		               Round((fadetall.facmont + (fadetall.facmont * fadetall.facpive / 100 ))/fadetall.faccant,0),
		               (fadetall.facmont + (fadetall.facmont * fadetall.facpive / 100 )) / fadetall.faccant) as decimal(14,2)) as facmont,
		Cast(If(vnRedond = 1,
		               Round(fadetall.facmont + (fadetall.facmont * fadetall.facpive / 100 ),0),
		               fadetall.facmont + (fadetall.facmont * fadetall.facpive / 100 )) as decimal(14,2)) as total,
		Cast(If(fadetall.facpive > 0,  If(vnRedond = 1, Round(fadetall.facmont,0), fadetall.facmont), 0) as Decimal(14,2)) as totalGrav, -- Bosco 01/02/2020
		Cast(If(fadetall.facpive <= 0, If(vnRedond = 1, Round(fadetall.facmont,0), fadetall.facmont), 0) as Decimal(14,2)) as totalExen, -- Bosco 01/02/2020
		faencabe.facfech,
		faencabe.clicode,
		If(vnRedond = 1,Round(faencabe.facdesc,0),faencabe.facdesc) as facdesc,
		If(vnRedond = 1,Round(faencabe.facimve,0),faencabe.facimve) as facimve,
		fadetall.facpive,
		fadetall.facpdesc,
		faencabe.facmont as Totalf,
		fadetall.facnume,
		fadetall.faccant,
		If(Trim(inarticu.barcode) = '', inarticu.artcode, inarticu.barcode) as codigo,
		Trim(inarticu.artdesc) as artdesc,
		Trim(If(vnFacligenerico = 1, vcClidesc, inclient.clidesc)) as clidesc,
		inclient.clitel1,
		Trim(inclient.clidir) as clidir,
		Trim(vendedor.nombre) as nombre,
		vcFactext as factext,
		If(faencabe.facnpag >  1,Concat('1er fecha/venc.: ',Dtoc(faencabe.facfech + INTERVAL faencabe.facdpago     DAY)),' ') as PrimerPago,
		If(faencabe.facnpag >= 2,Concat('2da fecha/venc.: ',Dtoc(faencabe.facfech + INTERVAL faencabe.facdpago * 2 DAY)),' ') as SegundoPago,
		If(faencabe.facnpag >= 3,Concat('3ra fecha/venc.: ',Dtoc(faencabe.facfech + INTERVAL faencabe.facdpago * 3 DAY)),' ') as TercerPago,
		faencabe.facnpag,
		faencabe.facmont/faencabe.facnpag as MontoCadaPago,
		vcTimbre as timbre,
		faencabe.facMonExp,
		faencabe.consHacienda,
		faencabe.claveHacienda
	from fadetall
	inner join faencabe on fadetall.facnume = faencabe.facnume and fadetall.facnd = faencabe.facnd
	Inner join inarticu on fadetall.artcode = inarticu.artcode
	Inner join inclient on faencabe.clicode = inclient.clicode
	Inner join vendedor on faencabe.vend    = vendedor.vend
	Inner join monedas  on faencabe.codigoTC = monedas.codigo
	Where fadetall.facnume = pnfacnume and fadetall.facnd = pnfacnd;
END$$

Delimiter ;

/*
SELECT * FROM faencabe Where facnume = 999679059 and facnd = 0;
SELECT * FROM fadetall Where facnume = 999679059 and facnd = 0;

CALL ImprimirFacturaoND(999679059, 0);
*/