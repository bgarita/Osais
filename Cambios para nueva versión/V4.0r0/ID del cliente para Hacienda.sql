-- Agregar la identificación del cliente para reportar a Hacienda.
ALTER TABLE `LaFlor`.`inclient` 
ADD COLUMN `idcliente` VARCHAR(20) NOT NULL COMMENT 'Identificación del cliente; puede ser cédula, pasaporte o cualquier otro documento oficial.' AFTER `colect`;

ALTER TABLE `LaFlor`.`hinclient` 
ADD COLUMN `idcliente` VARCHAR(20) NOT NULL COMMENT 'Identificación del cliente; puede ser cédula, pasaporte o cualquier otro documento oficial.' AFTER `colect`;

ALTER TABLE `LaFlor`.`inclient` 
ADD COLUMN `idtipo` tinyInt NOT NULL default 1 
	COMMENT 'Tipo de identificación: 1=Céd. Física, 2=Céd. Jurídica, 3=Número de Identificación Tributario Especial (NITE), 4=Documento de Identificación Migratorio para Extranjeros (DIMEX)' ;

ALTER TABLE `LaFlor`.`hinclient` 
ADD COLUMN `idtipo` tinyInt NOT NULL default 1 
	COMMENT 'Tipo de identificación: 1=Céd. Física, 2=Céd. Jurídica, 3=Número de Identificación Tributario Especial (NITE), 4=Documento de Identificación Migratorio para Extranjeros (DIMEX)' ;

-- Agregar el campo de referencia que envía Hacienda para la factura electrónica
ALTER TABLE `LaFlor`.`faencabe` 
ADD COLUMN `refHacienda` varchar(20) NOT NULL default '' COMMENT 'Número de referencia de la factura registrada en Hacienda' ;

-- Agregar el campo para el número de referencia (clave) que se generó (documento electrónico).
ALTER TABLE `LaFlor`.`faencabe` 
ADD COLUMN `claveHacienda` VARCHAR(50) NOT NULL DEFAULT '' COMMENT 'Número de referencia enviado a Hacienda' AFTER `refHacienda`;


-- Agregar los campos de los consecutivos de los documentos electrónicos
ALTER TABLE `LaFlor`.`config` 
ADD COLUMN `facElect` INT NOT NULL DEFAULT 0 AFTER `sincronizarTablas`,
ADD COLUMN `ncredElect` INT NOT NULL DEFAULT 0 AFTER `facElect`,
ADD COLUMN `ndebElect` INT NOT NULL DEFAULT 0 AFTER `ncredElect`;

ALTER TABLE `LaFlor`.`config` 
ADD COLUMN `correoE` varchar(80) NOT NULL default '' COMMENT 'Correo electrónico' ;

ALTER TABLE `LaFlor`.`config` 
ADD COLUMN `tipoID` tinyint(1) NOT NULL default 2 COMMENT 'Tipo de identificación' ;

ALTER TABLE `LaFlor`.`config` 
ADD COLUMN `provincia` tinyint(2) NOT NULL default '0' COMMENT 'Provincia',
ADD COLUMN `canton` tinyint(2) NOT NULL default '0' COMMENT 'Cantón',
ADD COLUMN `distrito` tinyint(2) NOT NULL default '0' COMMENT 'Distrito',
ADD COLUMN `barrio` tinyint(2) NOT NULL default '0' COMMENT 'Barrio';


USE `LaFlor`;
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

	IN  `pidcliente`     varchar(20)
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
	  idcliente)
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
	  pIDcliente);
  Else
    Select '[BD] El registro ya existe';
  End if;
END$$

DELIMITER ;

