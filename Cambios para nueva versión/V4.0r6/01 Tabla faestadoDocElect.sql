-- Crear la tabla que contendrá la información sobre los envíos de XMLs a Hacienda
use LaFlor;

CREATE TABLE `faestadoDocElect` (
    `facnume` INT NOT NULL COMMENT 'Número de documento (FAC, NCR, NDB)',
    `facnd` INT NOT NULL COMMENT 'Número de NC o ND (las facturas aparecerán con un cero, las NC con un número positivo y las ND con un número negativo)',
    `xmlFile` VARCHAR(70) NOT NULL DEFAULT ' ' COMMENT 'Nombre del archivo xml (solo el nombre, no la ruta)',
    `estado` TINYINT NOT NULL COMMENT 'Código de estado reportado por el Ministerio de Hacienda para los documentos electrónicos',
    `descrip` VARCHAR(400) NOT NULL DEFAULT ' ' COMMENT 'Texto que describe el estado de documento electrónico según el Ministerio de Hacienda',
    `informado` CHAR(1) NOT NULL DEFAULT 'N' COMMENT '(S/N) indica que el estado fue informado al usuario o no.',
    `correo` VARCHAR(55) NOT NULL DEFAULT ' ' COMMENT 'Dirección de correo electrónico a la que fue enviada la notificación.',
    `fecha` DATETIME NOT NULL COMMENT 'Fecha y hora en que fue enviada la notificación.',
    PRIMARY KEY (`facnume` , `facnd`),
    CONSTRAINT `fk_faestadoDocElect_faencabe` FOREIGN KEY (`facnume` , `facnd`)
        REFERENCES `faencabe` (`facnume` , `facnd`)
        ON DELETE CASCADE ON UPDATE CASCADE
);


ALTER TABLE `usuario` 
ADD COLUMN `notifxmlfe` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Notificar sobre los estados de los xmls enviados a Hacienda.' AFTER `ultimaClave`,
ADD COLUMN `invervalo4` SMALLINT(5) NOT NULL DEFAULT 15 COMMENT 'Intervalo de notificaciones sobre los estados de los xmls enviados a Hacienda.' AFTER `notifxmlfe`;

ALTER TABLE `LaFlor`.`usuario` 
CHANGE COLUMN `invervalo4` `intervalo4` SMALLINT(5) NOT NULL DEFAULT '15' COMMENT 'Intervalo de notificaciones sobre los estados de los xmls enviados a Hacienda.' ;


ALTER TABLE `saisystem`.`notificacion` 
CHANGE COLUMN `tema` `tema` TINYINT(4) NOT NULL COMMENT '1=Mínimos de inventario, 2=Facturas por vencer de CXC, 3=Factutas por vecer de CXP, 4=Estado de los xml enviados a Hacienda' ;

Insert into `saisystem`.`notificacion`
	Select 4, 
		'Estado de los xml enviados a Hacienda',
		4;


ALTER TABLE `saisystem`.`notificado` 
CHANGE COLUMN `mensaje` `mensaje` VARCHAR(1000) NOT NULL COMMENT 'Texto de la notificación' ;



-- Modificar los proceso de creación y actualización de usuarios
DROP procedure IF EXISTS `InsertarUsuario`;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarUsuario`(
  IN  `pUser`          char(16),
  IN  `pNivel`         smallint(5),
  IN  `pN1`            tinyint(1),
  IN  `pN2`            tinyint(1),
  IN  `pN3`            tinyint(1),
  IN  `pFacturas`      tinyint(1),
  IN  `pN5`            tinyint(1),
  IN  `pN6`            tinyint(1),
  IN  `pN7`            tinyint(1),
  IN  `pN8`            tinyint(1),
  IN  `pN9`            tinyint(1),
  IN  `pPrecios`       tinyint(1),
  IN  `pDevoluciones`  tinyint(1),
  IN  `pDescuentos`    tinyint(1),
  IN  `pMaxDesc`       decimal,
  IN  `pNotifCompra`   tinyint(1),
  IN  `pIntervalo1`    smallInt,   -- Bosco agregado 28/07/2013
  IN  `pNotifFactcxc`  tinyint(1), -- Bosco agregado 28/07/2013
  IN  `pIntervalo2`    smallInt,   -- Bosco agregado 28/07/2013
  IN  `pNotifFactcxp`  tinyint(1), -- Bosco agregado 28/07/2013
  IN  `pIntervalo3`    smallInt,   -- Bosco agregado 28/07/2013
  IN  `pNotifxmlfe`    tinyint(1), -- Bosco agregado 23/12/2018
  IN  `pIntervalo4`    smallInt,   -- Bosco agregado 23/12/2018
  IN  `pActivo`        char(1),
  IN  `pUltimaClave`   datetime,
  IN  `pFirmas`        tinyint(1)
)
BEGIN
    Declare vExisteEnBD tinyint(1);
    Declare vExisteEnOSAIS tinyint(1);
    Declare vMensaje varchar(40);

    Set vExisteEnBD    = Exists(Select 1 from vistausuarios where user = pUser);
    Set vExisteEnOSAIS = Exists(Select 1 from usuario where user = pUser);

    If not vExisteEnBD then
       Set vMensaje = '[BD] Usuario no existe en MySQL';
    Else
       if vExisteEnOSAIS then
          Set vMensaje = '[BD] Usuario ya existe';
       End if;
    End if;


    If vMensaje is null then
       Insert into usuario (
          User,
          Nivel,
          N1,
          N2,
          N3,
          Facturas,
          N5,
          N6,
          N7,
          N8,
          N9,
          Precios,
          Devoluciones,
          Descuentos,
          MaxDesc,
          NotifCompra,
		  Intervalo1,   -- Bosco agregado 28/07/2013
		  NotifFactcxc, -- Bosco agregado 28/07/2013
		  Intervalo2,   -- Bosco agregado 28/07/2013
		  NotifFactcxp, -- Bosco agregado 28/07/2013
		  Intervalo3,   -- Bosco agregado 28/07/2013
		  notifxmlfe, 	-- Bosco agregado 23/12/2018
		  Intervalo4,   -- Bosco agregado 23/12/2018
          Firmas )
       values(
          pUser,
          pNivel,
          pN1,
          pN2,
          pN3,
          pFacturas,
          pN5,
          pN6,
          pN7,
          pN8,
          pN9,
          pPrecios,
          pDevoluciones,
          pDescuentos,
          pMaxDesc,
          pNotifCompra,
		  pIntervalo1,   -- Bosco agregado 28/07/2013
		  pNotifFactcxc, -- Bosco agregado 28/07/2013
		  pIntervalo2,   -- Bosco agregado 28/07/2013
		  pNotifFactcxp, -- Bosco agregado 28/07/2013
		  pIntervalo3,   -- Bosco agregado 28/07/2013
		  pNotifxmlfe, 	 -- Bosco agregado 23/12/2018
		  pIntervalo4,   -- Bosco agregado 23/12/2018
          pFirmas );
		
		-- Si el usuario no existe en la base de datos del sistema
		-- lo agrego.
		if not Exists(Select user from saisystem.usuario
					  Where user = pUser) then
			Insert into saisystem.usuario (
			  User,
			  activo,       -- Bosco agregado 06/11/2011
			  ultimaClave)  -- Bosco agregado 06/11/2011
			Values(pUser, pActivo, pUltimaClave);
		End if;
    else
       Select vMensaje;
    end if;
END$$

DELIMITER ;


ALTER TABLE `faestadoDocElect` 
ADD COLUMN `referencia` INT NOT NULL DEFAULT 0 COMMENT 'Número de referencia en Hacienda' AFTER `fecha`;


ALTER TABLE `faestadoDocElect` 
ADD COLUMN `xmlFirmado` VARCHAR(70) NOT NULL DEFAULT ' ' COMMENT 'Nombre del archivo xml firmado' AFTER `referencia`,
ADD COLUMN `xmlEnviado` CHAR(1) NOT NULL DEFAULT 'N' COMMENT 'Indica si el xml fue enviado o no (S/N)' AFTER `xmlFirmado`,
ADD COLUMN `fechaEnviado` DATETIME NULL COMMENT 'Fecha y hora en que se envió (al destinatario)' AFTER `xmlEnviado`,
ADD COLUMN `emailDestino` VARCHAR(45) NOT NULL DEFAULT ' ' COMMENT 'Correo electrónico al que se envió el xml' AFTER `fechaEnviado`;


-- Agregar el trigger que se encargará de registrar los documentos electrónicos para el control del estado.
DELIMITER $$

DROP TRIGGER IF EXISTS faencabe_faestadoDocElect$$
CREATE TRIGGER `faencabe_faestadoDocElect` AFTER INSERT ON `faencabe` FOR EACH ROW
Insert into faestadoDocElect (
	facnume,
	facnd,
	xmlFile,
	estado,
	descrip,
	informado,
	correo,
	fecha,
	referencia,
	xmlFirmado,		-- Nombre del archivo xml firmado (sin la ruta)
	xmlEnviado,		-- Indica si el xml fue enviado por correo o no 
	fechaEnviado,	-- Fecha en que se envió por correo
	emailDestino)	-- Correo al que se envió
VALUES(
	new.facnume,
	new.facnd,
	concat(new.facnume, '.xml'),
	100, -- Indica que aún no ha sido enviado a Hacienda
	'Pendiente envío al Ministerio de Hacienda',
	'N',
	' ',
	now(),
	0,	-- Indica que aún no existe la referencia de Hacienda
	' ',
	'N', -- Indica si el xml fue enviado por correo o no 
	NULL,
	' '
)
$$
DELIMITER ;


