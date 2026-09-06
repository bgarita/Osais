DROP TABLE if EXISTS cabys;

CREATE TABLE `cabys` (
	`codigocabys` VARCHAR(20) NOT NULL COMMENT 'Código según MH' COLLATE 'latin1_swedish_ci',
	`descrip` VARCHAR(150) NOT NULL COMMENT 'Descripción del bien o servicio' COLLATE 'latin1_swedish_ci',
	`impuesto` FLOAT(12) NOT NULL DEFAULT '0' COMMENT 'Porcentaje IVA',
	PRIMARY KEY (`codigocabys`) USING BTREE
)
COMMENT='Catálogo de bienes y servicios según el Ministerio de Hacienda'
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;

DROP TABLE if EXISTS cabys_audit;

CREATE TABLE `cabys_audit` (
	`codigocabys_old` VARCHAR(20) NOT NULL COMMENT 'Código según MH' COLLATE 'latin1_swedish_ci',
	`codigocabys_new` VARCHAR(20) NOT NULL COMMENT 'Código según MH' COLLATE 'latin1_swedish_ci',
	`descrip_old` VARCHAR(150) NOT NULL COMMENT 'Descripción del bien o servicio' COLLATE 'latin1_swedish_ci',
	`descrip_new` VARCHAR(150) NOT NULL COMMENT 'Descripción del bien o servicio' COLLATE 'latin1_swedish_ci',
	`impuesto_old` FLOAT(12) NOT NULL DEFAULT '0' COMMENT 'Porcentaje IVA',
	`impuesto_new` FLOAT(12) NOT NULL DEFAULT '0' COMMENT 'Porcentaje IVA',
	`fecha` DATETIME NOT NULL DEFAULT NOW() COMMENT 'Fecha y hora de la acción',
	`accion` VARCHAR(10) NOT NULL DEFAULT '' COMMENT 'Update, Delete'
)
COMMENT='Tabla de auditoría para cabys'
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;


DROP TRIGGER if EXISTS trg_cabys_afeterupdate;

delimiter $$

CREATE TRIGGER `trg_cabys_afeterupdate` AFTER UPDATE ON `cabys` FOR EACH ROW BEGIN
	INSERT INTO cabys_audit (
		codigocabys_old,
		codigocabys_new,
		descrip_old,
		descrip_new,
		impuesto_old,
		impuesto_new,
		fecha,
		accion
		)
		VALUES (
		OLD.codigocabys,
		NEW.codigocabys,
		OLD.descrip,
		NEW.descrip,
		OLD.impuesto,
		NEW.impuesto,
		NOW(),
		'Update'
		);
END$$
delimiter ;



DROP TRIGGER if EXISTS trg_cabys_afeterdelete;

delimiter $$

CREATE TRIGGER `trg_cabys_afeterdelete` AFTER UPDATE ON `cabys` FOR EACH ROW BEGIN
	INSERT INTO cabys_audit (
		codigocabys_old,
		codigocabys_new,
		descrip_old,
		descrip_new,
		impuesto_old,
		impuesto_new,
		fecha,
		accion
		)
		VALUES (
		OLD.codigocabys,
		'',
		OLD.descrip,
		'',
		OLD.impuesto,
		0,
		NOW(),
		'Delete'
		);
END$$
delimiter ;


ALTER TABLE `cabys`
	CHANGE COLUMN `descrip` `descrip` VARCHAR(1000) NOT NULL COMMENT 'Descripción del bien o servicio' COLLATE 'latin1_swedish_ci' AFTER `codigocabys`;
	
	
ALTER TABLE `cabys_audit`
	CHANGE COLUMN `descrip_new` `descrip_new` VARCHAR(1000) NOT NULL COMMENT 'Descripción del bien o servicio' COLLATE 'latin1_swedish_ci';
ALTER TABLE `cabys_audit`
	CHANGE COLUMN `descrip_old` `descrip_old` VARCHAR(1000) NOT NULL COMMENT 'Descripción del bien o servicio' COLLATE 'latin1_swedish_ci';
	
-- Agregar un registro en blanco para la integridad referencia de inarticu
INSERT INTO cabys (codigocabys, descrip, impuesto) 
VALUES(' ','Producto CABYS no definido',-1)
ON DUPLICATE KEY UPDATE 
	descrip  = 'Producto CABYS no definido',
	impuesto = -1;


ALTER TABLE `inarticu`
	ADD COLUMN `codigocabys` VARCHAR(20) NOT NULL DEFAULT ' ' COMMENT 'Código del catálogo de bienes y servicios de Hacienda' COLLATE 'latin1_swedish_ci' AFTER `codigoTarifa`,
	ADD CONSTRAINT `FK_inarticu_cabys` FOREIGN KEY (`codigocabys`) REFERENCES `cabys` (`codigocabys`);
	
ALTER TABLE `hinarticu`
	ADD COLUMN `codigocabys` VARCHAR(20) NOT NULL DEFAULT ' ' COMMENT 'Código del catálogo de bienes y servicios de Hacienda' COLLATE 'latin1_swedish_ci',
	ADD CONSTRAINT `FK_hinarticu_cabys` FOREIGN KEY (`codigocabys`) REFERENCES `cabys` (`codigocabys`);
	
ALTER TABLE `inarticu_sinc`
	ADD COLUMN `codigocabys` VARCHAR(20) NOT NULL DEFAULT ' ' COMMENT 'Código del catálogo de bienes y servicios de Hacienda' COLLATE 'latin1_swedish_ci',
	ADD CONSTRAINT `FK_inarticu_sinc_cabys` FOREIGN KEY (`codigocabys`) REFERENCES `cabys` (`codigocabys`);
	
ALTER TABLE `fadetall`
	ADD COLUMN `codigocabys` VARCHAR(20) NOT NULL DEFAULT ' ' COMMENT 'Código del catálogo de bienes y servicios de Hacienda' COLLATE 'latin1_swedish_ci',
	ADD CONSTRAINT `FK_fadetall_cabys` FOREIGN KEY (`codigocabys`) REFERENCES `cabys` (`codigocabys`);
	
ALTER TABLE `wrk_fadetall`
	ADD COLUMN `codigocabys` VARCHAR(20) NOT NULL DEFAULT ' ' COMMENT 'Código del catálogo de bienes y servicios de Hacienda' COLLATE 'latin1_swedish_ci';
	
ALTER TABLE `config`
	ADD COLUMN `usarCabys` TINYINT(1) UNSIGNED NOT NULL DEFAULT '0' 
	COMMENT 'Le indica al sistema si debe usar el catálogo de bienes y servicios de Hacienda para obtener el IVA' AFTER `barrio`;
