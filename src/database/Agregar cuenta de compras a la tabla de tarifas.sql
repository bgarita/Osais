-- Agregar la columna del impuesto soportado de compras
ALTER TABLE `tarifa_iva`
	CHANGE COLUMN `cuenta` `cuenta` VARCHAR(12) NOT NULL DEFAULT ' ' COMMENT 'Cuenta contable IVA ventas' COLLATE 'latin1_swedish_ci' AFTER `porcentaje`,
	ADD COLUMN `cuenta_c` VARCHAR(12) NOT NULL DEFAULT ' ' COMMENT 'Cuenta contable IVA compras' COLLATE 'latin1_swedish_ci' AFTER `cuenta`;

-- Eliminar los campos de impuestos de la tabla de configuración
ALTER TABLE `configcuentas`
	DROP COLUMN `impuesto_v`,
	DROP COLUMN `impuesto_c`;	
	
ALTER TABLE `faestadodocelect`
	ADD INDEX `Idx_estado` (`estado`);

ALTER TABLE `config`
	CHANGE COLUMN `genasienfac` `genasienfac` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Interface contable 1=Si, 0=No' AFTER `DistPago`;

-- Agrego los nuevos campos para separar los montos gravados de los exentos	
ALTER TABLE `cxpfacturas`
	CHANGE COLUMN `total_fac` `total_fac` DECIMAL(14,4) NOT NULL DEFAULT '0.0000' COMMENT 'Total = monto gravado + monto exento' AFTER `fecha_pag`,
	ADD COLUMN `monto_gra` DECIMAL(14,4) NOT NULL DEFAULT '0.0000' COMMENT 'Monto gravado' AFTER `total_fac`,
	ADD COLUMN `monto_exe` DECIMAL(14,4) NOT NULL DEFAULT '0.0000' COMMENT 'Monto exento' AFTER `monto_gra`;
	
-- Actualizar los datos para que haya congruencia ya que los datos que habían se tomaban como gravados únicamente.
UPDATE cxpfacturas
	SET monto_gra = total_fac;