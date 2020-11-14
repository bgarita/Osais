ALTER TABLE `coasientod`
	CHANGE COLUMN `db_cr` `db_cr` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '1=débito, 0=Crédito' AFTER `descrip`;
	
ALTER TABLE `configcuentas`
	ADD COLUMN `tipo_comp_P` SMALLINT(6) NOT NULL DEFAULT '0' COMMENT 'Tipo de asiento para pagos CXC' AFTER `tipo_comp_C`;