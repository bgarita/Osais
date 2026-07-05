DROP TABLE if EXISTS cxpfacturasd;

delimiter $$
CREATE TABLE `cxpfacturasd` (
	`factura` VARCHAR(10) NOT NULL COMMENT 'Número de factura, NC o ND' COLLATE 'latin1_swedish_ci',
	`tipo` VARCHAR(3) NOT NULL COMMENT 'Tipo de documento (FAC, NCR, NDB)' COLLATE 'latin1_swedish_ci',
	`procode` VARCHAR(15) NOT NULL COMMENT 'Código de proveedor' COLLATE 'latin1_swedish_ci',
	`codigoTarifa` VARCHAR(3) NOT NULL COMMENT 'Código de tarifa o impuesto según Ministerio de Hacienda' COLLATE 'latin1_swedish_ci',
	`facpive` FLOAT NOT NULL DEFAULT 0 COMMENT 'Porcentaje de impuesto',
	`facimve` DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT 'Monto del impuesto',
	PRIMARY KEY (`factura`, `tipo`, `procode`),
	CONSTRAINT `FK_cxpfacturasd_tarifa_iva` FOREIGN KEY (`codigoTarifa`) REFERENCES `tarifa_iva` (`codigoTarifa`) ON UPDATE CASCADE
)
COMMENT='Detalle de impuestos de los documentos de compras'
COLLATE='utf8_general_ci'
;$$

delimiter ;

ALTER TABLE `cxpfacturasd`
	ADD CONSTRAINT `FK_cxpfacturasd_cxpfacturas` FOREIGN KEY (`factura`, `tipo`, `procode`) REFERENCES `cxpfacturas` (`Factura`, `tipo`, `procode`) ON UPDATE CASCADE;

ALTER TABLE `cxpfacturas`
	ADD COLUMN `tipo_comp` TINYINT(2) NOT NULL DEFAULT '0' COMMENT 'Tipo de asiento' AFTER `consHacienda`,
	ADD COLUMN `no_comprob` VARCHAR(10) NOT NULL DEFAULT ' ' COMMENT 'Número de asiento' COLLATE 'latin1_swedish_ci' AFTER `tipo_comp`;
	
ALTER TABLE `cxpfacturasd`
	DROP FOREIGN KEY `FK_cxpfacturasd_cxpfacturas`;

ALTER TABLE `cxpfacturasd`
	DROP PRIMARY KEY;
	
ALTER TABLE `cxpfacturasd`
	ADD CONSTRAINT `FK_cxpfacturasd_cxpfacturas` FOREIGN KEY (`factura`, `tipo`, `procode`) REFERENCES `cxpfacturas` (`Factura`, `tipo`, `procode`) ON UPDATE CASCADE;
	
ALTER TABLE `cxpfacturasd`
	DROP FOREIGN KEY `FK_cxpfacturasd_cxpfacturas`;
ALTER TABLE `cxpfacturasd`
	ADD CONSTRAINT `FK_cxpfacturasd_cxpfacturas` FOREIGN KEY (`factura`, `tipo`, `procode`) REFERENCES `laflor`.`cxpfacturas` (`Factura`, `tipo`, `procode`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE `configcuentas`
	ADD COLUMN `tipo_comp_PP` SMALLINT(6) NOT NULL DEFAULT '0' COMMENT 'Tipo de asiento para pagos CXP' AFTER `tipo_comp_P`;
	
INSERT INTO `intiposdoc` (`Movtido`, `Descrip`, `Modulo`, `ReqProveed`, `afectaMinimos`) VALUES ('15', 'Recibos CXP', 'CXP', '1', '0');

ALTER TABLE `cxppage`
	ADD COLUMN `no_comprob` VARCHAR(10) NOT NULL DEFAULT ' ' COMMENT 'Número de asiento' COLLATE 'latin1_swedish_ci' AFTER `tipopago`,
	ADD COLUMN `tipo_comp` TINYINT NOT NULL DEFAULT '0' COMMENT 'Tipo de asiento' AFTER `no_comprob`;
	
UPDATE cxpfacturas
	SET tipoca = 1
WHERE tipoca = 0;