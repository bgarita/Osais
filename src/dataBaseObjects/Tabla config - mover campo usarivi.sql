
/**
 * Author:  bgarita
 * Created: Jul 3, 2020
 */
-- Se modifica el lugar donde va este campo para que su significado sea más congruente.
ALTER TABLE `config`
	CHANGE COLUMN `usarivi` `usarivi` TINYINT(1) UNSIGNED NOT NULL DEFAULT '1' 
        COMMENT 'Usar precios con impuesto incluido (0=No, 1=Si)' AFTER `ndeb`;