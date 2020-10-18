ALTER TABLE `coasientoe`
	CHANGE COLUMN `asientoAnulado` `asientoAnulado` VARCHAR(10) NOT NULL DEFAULT '' 
	COMMENT 'Contiene el número de asiento al cual está anulando' COLLATE 'latin1_swedish_ci' AFTER `enviado`;
	
	
ALTER TABLE `hcoasientoe`
	CHANGE COLUMN `asientoAnulado` `asientoAnulado` VARCHAR(10) NOT NULL DEFAULT '' 
	COMMENT 'Contiene el número de asiento al cual está anulando' COLLATE 'latin1_swedish_ci' AFTER `enviado`;

ALTER TABLE `hcoasientod`
	DROP FOREIGN KEY `fk_hcoasientod_hcoasientoe`;
		
ALTER TABLE `hcoasientoe`
	DROP PRIMARY KEY;

ALTER TABLE `hcoasientoe`
	ADD INDEX `Idx_hcoasientoe_no_comprob_tipo_comp` (`no_comprob`, `tipo_comp`);