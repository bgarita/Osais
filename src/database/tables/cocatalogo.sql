CREATE TABLE `cocatalogo` (
	`mayor` VARCHAR(3) NOT NULL COMMENT 'Cuenta mayor' COLLATE 'latin1_swedish_ci',
	`sub_cta` VARCHAR(3) NOT NULL COMMENT 'Sub cuenta' COLLATE 'latin1_swedish_ci',
	`sub_sub` VARCHAR(3) NOT NULL COMMENT 'Sub subcuenta' COLLATE 'latin1_swedish_ci',
	`colect` VARCHAR(3) NOT NULL COMMENT 'Colectiva' COLLATE 'latin1_swedish_ci',
	`nom_cta` VARCHAR(60) NOT NULL COMMENT 'Nombre de la cuenta' COLLATE 'latin1_swedish_ci',
	`nivel` SMALLINT(6) NOT NULL DEFAULT '0' COMMENT '0=Cuenta de mayor, 1=Cuenta de movimientos',
	`tipo_cta` SMALLINT(6) NOT NULL DEFAULT '1' COMMENT 'Tipo de cuenta (1=Activo, 2=Pasivo, 3=Capital, 4=Ingresos, 5=Gastos)',
	`fecha_upd` DATETIME NOT NULL DEFAULT '2013-08-10 00:00:00' COMMENT 'Fecha de actualización',
	`ano_anter` DECIMAL(24,4) NOT NULL DEFAULT '0.0000' COMMENT 'Saldo del periodo anterior',
	`db_fecha` DECIMAL(20,4) NOT NULL DEFAULT '0.0000' COMMENT 'Débitos del periodo actual',
	`cr_fecha` DECIMAL(20,4) NOT NULL DEFAULT '0.0000' COMMENT 'Créditos del periodo actual',
	`db_mes` DECIMAL(18,4) NOT NULL DEFAULT '0.0000' COMMENT 'Débitos del mes actual',
	`cr_mes` DECIMAL(18,4) NOT NULL DEFAULT '0.0000' COMMENT 'Créditos del mes actual',
	`nivelc` SMALLINT(6) NOT NULL DEFAULT '1' COMMENT 'Nivel de cuenta',
	`nombre` SMALLINT(6) NOT NULL DEFAULT '0' COMMENT 'Formatear como nombre? (1=Si, 0=No)',
	`fecha_c` DATETIME NOT NULL DEFAULT '2013-08-10 00:00:00' COMMENT 'Fecha de creación de la cuenta',
	`activa` SMALLINT(6) NOT NULL DEFAULT '1' COMMENT 'Indica si la cueta está activa o no (1=SI,0=No).',
	PRIMARY KEY (`mayor`, `sub_cta`, `sub_sub`, `colect`) USING BTREE,
	INDEX `nom_cta_idx` (`nom_cta`) USING BTREE
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;
