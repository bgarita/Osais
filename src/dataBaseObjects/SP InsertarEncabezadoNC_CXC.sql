Drop procedure if EXISTS `InsertarEncabezadoNC_CXC`;

Delimiter $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarEncabezadoNC_CXC`(
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
  Declare vUser     varchar(40); 
  Declare vCodigoTC char(3);     
  Declare vTipoca   float;       
  Declare vFacfepa  datetime;    

  If pFacnume > 0 then
    Set pFacnume = (pFacnume * -1);
  End if;

  Set pVend     = IfNull(pVend,(Select vend from inclient where clicode = pClicode));
  Set pTerr     = IfNull(pTerr,(Select terr from inclient where clicode = pClicode));
  Set pFacfech  = IfNull(pFacfech,now());
  Set pPrecio   = IfNull(pPrecio,(Select cliprec from inclient where clicode = pClicode));
  Set pFacplazo = IfNull(pFacplazo,(Select cliplaz from inclient where clicode = pClicode));
  Set vFacfepa  = AddDate(pFacfech, interval pFacplazo day);
  Set vUser     = Trim(user());
  Set vCodigoTC = (Select CodigoTC from config);
  Set vTipoca   = ConsultarTipoca(vCodigoTC,curdate());

  Insert into wrk_faencabe (
    facnume,
    clicode,
    vend,
    terr,
    facfech,
    facplazo,
    precio,
    facestado,
    user,
    codigoTC,
    tipoca,
    facfepa,
    facfechaC,
    facnd)
  Values (
    pFacnume,
    pClicode,
    pVend,
    pTerr,
    pFacfech,
    pFacplazo,
    pPrecio,
    ' ',
    vUser,
    vCodigoTC,
    vTipoca,
    vFacfepa,
    now(),
    pFacnume * -1);
END$$
Delimiter ;