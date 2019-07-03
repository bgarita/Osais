use LaFlor;
Drop PROCEDURE `InsertarEncabezadoNC_CXC`;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarEncabezadoNC_CXC`(
  IN  `pFacnume`   int(10),
  IN  `pClicode`   int(10),
  IN  `pVend`      tinyint(3),
  IN  `pTerr`      tinyint(3),
  IN  `pFacfech`   datetime,
  IN  `pFacplazo`  tinyint(3),
  IN  `pPrecio`    tinyint(3),
  IN  `pFacpive`   float
)
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
  Set pFacpive  = IfNull(pFacpive,Case When (Select exento
                                             from inclient
                                             Where clicode = pClicode) = 1 then (Select facimpu from config)
                                       Else 0 End);

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
    user,
    codigoTC,
    tipoca,
    facfepa,
    facfechaC,
    facpive,
    facnd)
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
    pFacpive,
    pFacnume * -1);

END$$
DELIMITER ;
