ALTER TABLE `configcuentas`
	ADD COLUMN `mesCierreA` TINYINT(1) NOT NULL DEFAULT '12' 
	COMMENT 'Define el mes de cierre fiscal  (1=Enero, 2=Febrero...12=Diciembre9' AFTER `mesactual`;
	
ALTER TABLE `configcuentas`
	CHANGE COLUMN `mesactual` `mesactual` TINYINT(1) NOT NULL DEFAULT '12' 
	COMMENT 'Define el mes actual de proceco (1=Enero,2=Febrero...12=Diciembre)' AFTER `precierre`;
	
ALTER TABLE `configcuentas`
	CHANGE COLUMN `ctaCierre` `ctaCierre` VARCHAR(12) NOT NULL DEFAULT '' COMMENT 'Cuenta de cierre anual' COLLATE 'latin1_swedish_ci' AFTER `impuesto_c`,
	CHANGE COLUMN `añoactual` `añoactual` SMALLINT(6) NOT NULL DEFAULT '2010' COMMENT 'Define el año del ejercicio contable en curso' AFTER `mesCierreA`;	