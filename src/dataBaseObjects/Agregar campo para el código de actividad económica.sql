ALTER TABLE `config`
	ADD COLUMN `codigoAtividadEconomica` VARCHAR(12) NOT NULL DEFAULT '0' 
	COMMENT 'Código de actividad económica según el Ministerio de Hacienda' COLLATE 'latin1_swedish_ci' AFTER `usarCabys`;
