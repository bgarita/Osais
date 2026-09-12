CREATE TABLE `fecontrol` (
	`cantidad` INT(11) NOT NULL COMMENT 'Conteo de documentos enviados',
	`permitidos` INT(11) NOT NULL DEFAULT -1 COMMENT '-1 = Sin límite de envíos',
	`tolerancia` INT(11) NOT NULL DEFAULT '5' COMMENT 'Envíos permitidos después del límite'
)
COMMENT='Control de documentos electrónicos enviados'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
