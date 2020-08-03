CREATE TABLE `htarifa_iva` (
	`codigoTarifa` VARCHAR(3) NOT NULL COMMENT 'Código de tarifa' COLLATE 'latin1_swedish_ci',
	`descrip` VARCHAR(30) NOT NULL COMMENT 'Descripción de la tarifa' COLLATE 'latin1_swedish_ci',
	`porcentaje` FLOAT(12) NOT NULL DEFAULT '0' COMMENT 'Porcentaje de la tarifa',
	`periodo` DATETIME NOT NULL COMMENT 'Fecha y hora en que se realizó el cierre mensual',
	PRIMARY KEY (`codigoTarifa`) USING BTREE
)
COMMENT='Catálogo de impuestos según el Ministerio de Hacienda'
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
ROW_FORMAT=DYNAMIC
;

ALTER TABLE `htarifa_iva`
	DROP PRIMARY KEY,
	ADD PRIMARY KEY (`codigoTarifa`, `periodo`) USING BTREE;