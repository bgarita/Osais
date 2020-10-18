ALTER TABLE `configcuentas`
	ADD COLUMN `mesCierreA` TINYINT(1) NOT NULL DEFAULT '12' 
	COMMENT 'Define el mes de cierre fiscal  (1=Enero, 2=Febrero...12=Diciembre9' AFTER `mesactual`;
	
ALTER TABLE `configcuentas`
	CHANGE COLUMN `mesactual` `mesactual` TINYINT(1) NOT NULL DEFAULT '12' 
	COMMENT 'Define el mes actual de proceco (1=Enero,2=Febrero...12=Diciembre)' AFTER `precierre`;
	
ALTER TABLE `configcuentas`
	CHANGE COLUMN `ctaCierre` `ctaCierre` VARCHAR(12) NOT NULL DEFAULT '' COMMENT 'Cuenta de cierre anual' COLLATE 'latin1_swedish_ci' AFTER `impuesto_c`,
	CHANGE COLUMN `añoactual` `añoactual` SMALLINT(6) NOT NULL DEFAULT '2010' COMMENT 'Define el año del ejercicio contable en curso' AFTER `mesCierreA`;	
	
	
ALTER TABLE `cocatalogo`
	CHANGE COLUMN `nivel` `nivel` SMALLINT NOT NULL DEFAULT 0 COMMENT '0=Cuenta de mayor, 1=Cuenta de movimientos' AFTER `nom_cta`,
	CHANGE COLUMN `tipo_cta` `tipo_cta` SMALLINT NOT NULL DEFAULT 1 COMMENT 'Tipo de cuenta (1=Activo, 2=Pasivo, 3=Capital, 4=Ingresos, 5=Gastos)' AFTER `nivel`,
	CHANGE COLUMN `nivelc` `nivelc` SMALLINT NOT NULL DEFAULT 1 COMMENT 'Nivel de cuenta' AFTER `cr_mes`,
	CHANGE COLUMN `nombre` `nombre` SMALLINT NOT NULL DEFAULT 0 COMMENT 'Formatear como nombre? (1=Si, 0=No)' AFTER `nivelc`,
	CHANGE COLUMN `activa` `activa` SMALLINT NOT NULL DEFAULT 1 COMMENT 'Indica si la cueta está activa o no (1=SI,0=No).' AFTER `fecha_c`;