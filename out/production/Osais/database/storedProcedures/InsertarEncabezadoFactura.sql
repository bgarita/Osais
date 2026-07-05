Drop procedure if EXISTS `InsertarEncabezadoFactura`;

DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarEncabezadoFactura`(
	IN `pFacnume` int(10),
	IN `pClicode` int(10),
	IN `pVend` tinyint(3),
	IN `pTerr` tinyint(3),
	IN `pFacfech` datetime,
	IN `pFacplazo` tinyint(3),
	IN `pPrecio` tinyint(3)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN

	-- Autor: Bosco Garita Azofeifa

	Declare vUser     varchar(40); 
	Declare vCodigoTC char(3);     
	Declare vTipoca   float;       
	Declare vFacfepa  datetime;    


	Set pVend     = IfNull(pVend,(Select vend from inclient where clicode = pClicode));
	Set pTerr     = IfNull(pTerr,(Select terr from inclient where clicode = pClicode));
	Set pFacfech  = IfNull(pFacfech,now());
	Set pPrecio   = IfNull(pPrecio,(Select cliprec from inclient where clicode = pClicode));
	Set pFacplazo = IfNull(pFacplazo,(Select cliplaz from inclient where clicode = pClicode));

	Set vFacfepa  = AddDate(pFacfech, interval pFacplazo day);
	Set vUser     = Trim(user());
	Set vCodigoTC = (Select CodigoTC from config);
	Set vTipoca   = ConsultarTipoca(vCodigoTC,pFacfech);

	if vTipoca is null then
		Set vTipoca = 1;
	End if;

	Insert into wrk_faencabe (
		facnume,
		clicode,
		vend,
		terr,
		facfech,
		facplazo,
		precio,
		user,
		codigoTC,
		tipoca,
		facfepa,
		facfechaC,
		facestado) 
	Values (
		pFacnume,
		pClicode,
		pVend,
		pTerr,
		pFacfech,
		pFacplazo,
		pPrecio,
		vUser,
		vCodigoTC,
		vTipoca,
		vFacfepa,
		now(),
		'');
END$$
DELIMITER ;