
/**
 * Author:  bgarita
 * Created: Jul 22, 2020
 */

ALTER TABLE `faestadodocelect`
    CHANGE COLUMN `descrip` `descrip` VARCHAR(5000) NOT NULL DEFAULT ' ' 
    COMMENT 'Texto que describe el estado de documento electrónico según el Ministerio de Hacienda' 
    COLLATE 'utf8_general_ci' AFTER `estado`;

ALTER TABLE `hbodexis`
	DROP INDEX `FK_Bodegas`;

ALTER TABLE `hbodexis`
	DROP INDEX `FK_hbodexis_inarticu`;

ALTER TABLE `hbodexis`
	DROP INDEX `FK_Hinarticu`,
	DROP FOREIGN KEY `FK_hbodexis_hinarticu`;