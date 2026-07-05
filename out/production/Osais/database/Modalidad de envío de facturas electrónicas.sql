ALTER TABLE `config`
	ADD COLUMN `modoFacturaE` TINYINT(1) NOT NULL DEFAULT 1 
	COMMENT 'Modalidad de envío 1=Enviar XML, 2=Enviar datos' AFTER `enviarFacturaE`;
	
ALTER TABLE `config`
	CHANGE COLUMN `Empresa` `Empresa` VARCHAR(100) NOT NULL 
	COMMENT 'Nombre de la empresa' COLLATE 'latin1_swedish_ci' FIRST;