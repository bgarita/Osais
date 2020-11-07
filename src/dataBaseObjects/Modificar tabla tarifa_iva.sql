-- Agrego la cuenta para generar los asientos de facturación
ALTER TABLE `tarifa_iva`
	ADD COLUMN `cuenta` VARCHAR(12) NOT NULL DEFAULT ' ' 
	COMMENT 'Cuenta contable' COLLATE 'latin1_swedish_ci' AFTER `porcentaje`;