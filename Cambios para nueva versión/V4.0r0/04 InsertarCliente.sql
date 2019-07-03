USE `sai`;
DROP procedure IF EXISTS `InsertarCliente`;

DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarCliente`(
	IN  `pClicode`      int(10),
	IN  `pClidesc`      varchar(50),
	IN  `pClidir`       varchar(200),
	IN  `pClitel1`      varchar(11),
	IN  `pClitel2`      varchar(11),
	IN  `pClitel3`      varchar(11),
	IN  `pClifax`       varchar(11),
	IN  `pCliapar`      varchar(10),
	IN  `pClinaci`      tinyint(1),
	IN  `pCliprec`      tinyint(2),
	IN  `pClilimit`     decimal(12,2),
	IN  `pTerr`         tinyint(3),
	IN  `pVend`         tinyint(3),
	IN  `pClasif`       tinyint(2),
	IN  `pCliplaz`      smallint(4),
	IN  `pExento`       tinyint(1),
	IN  `pEncomienda`   tinyint(1),
	IN  `pDirencom`     varchar(200),
	IN  `pFacconiv`     tinyint(1),
	IN  `pClinpag`      tinyint(2),
	IN  `pClicelu`      varchar(11),
	IN  `pCliemail`     varchar(50),
	IN  `pClireor`      tinyint(1),
	IN  `pIgsitcred`    tinyint(1),
	IN  `pCredcerrado`  tinyint(1),
	IN  `pdiatramite`   tinyint(2),
	IN  `pHoratramite`  varchar(5),
	IN  `pDiapago`      tinyint(2),
	IN  `pHorapago`     varchar(5),

	IN  `pmayor`        varchar(3),
	IN  `psub_cta`      varchar(3),
	IN  `psub_sub`      varchar(3),
	IN  `pcolect`       varchar(3),

	IN  `pClicueba`     varchar(20),
	IN  `pCligenerico`  tinyint(1),

	IN  `pidcliente`     varchar(20),
	IN  `pidtipo`     tinyInt(4)
)
BEGIN
  
  If (ConsultarCliente(pClicode) is null) then
    Insert into inclient (
      clicode    ,
      clidesc    ,
      clidir     ,
      clitel1    ,
      clitel2    ,
      clitel3    ,
      clifax     ,
      cliapar    ,
      clinaci    ,
      cliprec    ,
      clilimit   ,
      terr       ,
      vend       ,
      clasif     ,
      cliplaz    ,
      exento     ,
      encomienda ,
      direncom   ,
      facconiv   ,
      clinpag    ,
      clicelu    ,
      cliemail   ,
      clireor    ,
      igsitcred  ,
      credcerrado,
      diatramite ,
      horatramite,
      diapago    ,
      horapago   ,
      
	  mayor,sub_cta,sub_sub,colect,
	  
      clicueba   ,
	  cligenerico,
	  idcliente,
	  idtipo)
    Values (
      pClicode   ,
      pClidesc   ,
      pClidir    ,
      pClitel1   ,
      pClitel2   ,
      pClitel3   ,
      pClifax    ,
      pCliapar   ,
      pClinaci   ,
      pCliprec   ,
      pClilimit  ,
      pTerr      ,
      pVend      ,
      pClasif    ,
      pCliplaz   ,
      pExento    ,
      pEncomienda,
      pDirencom  ,
      pFacconiv  ,
      pClinpag   ,
      pClicelu   ,
      pCliemail  ,
      pClireor   ,
      pIgsitcred ,
      pCredcerrado,
      pdiatramite ,
      pHoratramite,
      pDiapago    ,
      pHorapago   ,
      
	  pMayor,pSub_cta,pSub_sub,pColect,
	  
      pClicueba   ,
	  pCligenerico,
	  pIDcliente,
	  pidtipo);
  Else
    Select '[BD] El registro ya existe';
  End if;
END$$

DELIMITER ;

