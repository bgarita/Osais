CREATE TABLE `coparametroser` (
	`parametro` INT NOT NULL AUTO_INCREMENT COMMENT 'Código de parámetro',
	`descrip` VARCHAR(60) NOT NULL COMMENT 'Descripción que se usará en el reporte',
	PRIMARY KEY (`parametro`)
)
COMMENT='Parámetros para el estado de resultados'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

INSERT INTO coparametroser (descrip)
VALUES 
	('Ingresos'),
	('Inventario inicial'),
	('Compras'),
	('Devoluciones sobre compras'),
	('Descuentos sobre compras'),
	('Inventario final*'),
	('Gastos de mercadeo y ventas'),
	('Gastos de administración y finanzas'),
	('Gastos Depreciación Amortización y Deterioro'),
	('Gastos de operación'),
	('Ingresos Financieros'),
	('Gastos Financieros'),
	('Participación en resultado de asociados'),
	('Otros Ingresos'),
	('Otros Gastos');


CREATE TABLE `cocuentaser` (
	`parametro` INT(11) NOT NULL COMMENT 'Viene de la tabla coparametroser',
	`mayor` VARCHAR(3) NOT NULL COMMENT 'Cuenta de mayor' COLLATE 'latin1_swedish_ci',
	`sub_cta` VARCHAR(3) NOT NULL COMMENT 'Sub cuenta' COLLATE 'latin1_swedish_ci',
	`sub_sub` VARCHAR(3) NOT NULL COMMENT 'Sub sub cuenta' COLLATE 'latin1_swedish_ci',
	`colect` VARCHAR(3) NOT NULL COMMENT 'Colectiva' COLLATE 'latin1_swedish_ci',
	`monto` DECIMAL(14,0) NOT NULL DEFAULT '0' COMMENT 'Ultimo monto calculado para esta cuenta',
	INDEX `FK_coparametroser` (`parametro`) USING BTREE,
	INDEX `FK_cocatalogo` (`mayor`, `sub_cta`, `sub_sub`, `colect`) USING BTREE,
	CONSTRAINT `FK_cocatalogo` FOREIGN KEY (`mayor`, `sub_cta`, `sub_sub`, `colect`) REFERENCES `laflor`.`cocatalogo` (`mayor`, `sub_cta`, `sub_sub`, `colect`) ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT `FK_coparametroser` FOREIGN KEY (`parametro`) REFERENCES `laflor`.`coparametroser` (`parametro`) ON UPDATE CASCADE ON DELETE RESTRICT
)
COMMENT='Cuentas incluidas en el estado de resultados'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

ALTER TABLE `cocuentaser`
	ADD PRIMARY KEY (`parametro`, `mayor`, `sub_cta`, `sub_sub`, `colect`);

