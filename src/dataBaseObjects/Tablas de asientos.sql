ALTER TABLE `coasientoe`
	CHANGE COLUMN `asientoAnulado` `asientoAnulado` VARCHAR(10) NOT NULL DEFAULT '' 
	COMMENT 'Contiene el número de asiento al cual está anulando' COLLATE 'latin1_swedish_ci' AFTER `enviado`;
	
	
ALTER TABLE `hcoasientoe`
	CHANGE COLUMN `asientoAnulado` `asientoAnulado` VARCHAR(10) NOT NULL DEFAULT '' 
	COMMENT 'Contiene el número de asiento al cual está anulando' COLLATE 'latin1_swedish_ci' AFTER `enviado`;
