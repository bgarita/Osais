
-- Eliminar el campo de impuesto gobal
ALTER TABLE config
	DROP COLUMN facimpu;

-- Eliminar campos que ya no se están utilizando
ALTER TABLE inarticu
	DROP COLUMN artimpv,
	DROP COLUMN artusaIVG;
	
ALTER TABLE hinarticu
	DROP COLUMN artimpv,
	DROP COLUMN artusaIVG;
	
ALTER TABLE inarticu_sinc
	DROP COLUMN artimpv,
	DROP COLUMN artusaIVG;
	
ALTER TABLE faencabe
	DROP COLUMN facpive;
	
ALTER TABLE wrk_faencabe
	DROP COLUMN facpive;
	
ALTER TABLE `inarticu_sinc`
	CHANGE COLUMN `sinc` `sinc` VARCHAR(1) NOT NULL DEFAULT '' COLLATE 'latin1_swedish_ci' AFTER `codigoTarifa`;
