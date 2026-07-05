-- Vista vistaconsecutivoasientos
CREATE OR REPLACE VIEW vistaconsecutivoasientos AS 
SELECT 
	`coasientoe`.`no_comprob` AS `no_comprob`,
	`coasientoe`.`tipo_comp` AS `tipo_comp` 
FROM `coasientoe` 
UNION 
SELECT 
	`hcoasientoe`.`no_comprob` AS `no_comprob`,
	`hcoasientoe`.`tipo_comp` AS `tipo_comp` 
FROM `hcoasientoe`;