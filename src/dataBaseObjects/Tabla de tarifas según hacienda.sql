/**
 * Author:  bgarita
 * Created: Jul 17, 2020
 */

--Use laflor;

-- Crear la tabla de tarifas según el Ministerio de Hacienda.
CREATE TABLE `tarifa_iva` (
	`codigoTarifa` VARCHAR(3) NOT NULL COMMENT 'Código de tarifa',
	`descrip` VARCHAR(30) NOT NULL COMMENT 'Descripción de la tarifa',
	`porcentaje` FLOAT(12) NOT NULL DEFAULT '0' COMMENT 'Porcentaje de la tarifa',
	PRIMARY KEY (`codigoTarifa`) USING BTREE
)
COMMENT='Catálogo de impuestos según el Ministerio de Hacienda'
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB;

-- Agrear los datos
INSERT INTO `tarifa_iva` (`codigoTarifa`, `descrip`) VALUES ('01', 'Tarifa 0% (Exento)');
INSERT INTO `tarifa_iva` (`codigoTarifa`, `descrip`, `porcentaje`) VALUES ('02', 'Tarifa reducida 1%', '1');
INSERT INTO `tarifa_iva` (`codigoTarifa`, `descrip`, `porcentaje`) VALUES ('03', 'Tarifa reducida 2%', '2');
INSERT INTO `tarifa_iva` (`codigoTarifa`, `descrip`, `porcentaje`) VALUES ('04', 'Tarifa reducida 4%', '4');
INSERT INTO `tarifa_iva` (`codigoTarifa`, `descrip`) VALUES ('05', 'Transitorio 0%');
INSERT INTO `tarifa_iva` (`codigoTarifa`, `descrip`, `porcentaje`) VALUES ('06', 'Transitorio 4% ', '4');
INSERT INTO `tarifa_iva` (`codigoTarifa`, `descrip`, `porcentaje`) VALUES ('07', 'Transitorio 8%', '8');
INSERT INTO `tarifa_iva` (`codigoTarifa`, `descrip`, `porcentaje`) VALUES ('08', 'Tarifa general 13%', '13');


-- Agregar el nuevo campo en el catálogo de artículos y otras tablas y relacionarlas con el catálogo de tarifas
ALTER TABLE inarticu
	ADD codigoTarifa VARCHAR(3) NOT NULL DEFAULT '01' comment 'Código de tarifa según Hacienda';
ALTER TABLE `inarticu`
	ADD CONSTRAINT `FK_inarticu_tarifa_iva` FOREIGN KEY (`codigoTarifa`) REFERENCES `tarifa_iva` (`codigoTarifa`) ON UPDATE CASCADE;
	
ALTER TABLE hinarticu
	ADD codigoTarifa VARCHAR(3) NOT NULL DEFAULT '01' comment 'Código de tarifa según Hacienda';
ALTER TABLE `hinarticu`
	ADD CONSTRAINT `FK_hinarticu_tarifa_iva` FOREIGN KEY (`codigoTarifa`) REFERENCES `tarifa_iva` (`codigoTarifa`) ON UPDATE CASCADE;

ALTER TABLE inarticu_sinc
	ADD codigoTarifa VARCHAR(3) NOT NULL DEFAULT '01' comment 'Código de tarifa según Hacienda';
ALTER TABLE `inarticu_sinc`
	ADD CONSTRAINT `FK_inarticu_sinc_tarifa_iva` FOREIGN KEY (`codigoTarifa`) REFERENCES `tarifa_iva` (`codigoTarifa`) ON UPDATE CASCADE;

ALTER TABLE fadetall
	ADD codigoTarifa VARCHAR(3) NOT NULL DEFAULT '01' comment 'Código de tarifa según Hacienda';
ALTER TABLE `fadetall`
	ADD CONSTRAINT `FK_fadetall_tarifa_iva` FOREIGN KEY (`codigoTarifa`) REFERENCES `tarifa_iva` (`codigoTarifa`) ON UPDATE CASCADE;
	
ALTER TABLE wrk_fadetall
	ADD codigoTarifa VARCHAR(3) NOT NULL DEFAULT '01' comment 'Código de tarifa según Hacienda';
ALTER TABLE `wrk_fadetall`
	ADD CONSTRAINT `FK_wrk_fadetall_tarifa_iva` FOREIGN KEY (`codigoTarifa`) REFERENCES `tarifa_iva` (`codigoTarifa`) ON UPDATE CASCADE;
	
ALTER TABLE pedidod
	ADD codigoTarifa VARCHAR(3) NOT NULL DEFAULT '01' comment 'Código de tarifa según Hacienda';
ALTER TABLE `pedidod`
	ADD CONSTRAINT `FK_pedidod_tarifa_iva` FOREIGN KEY (`codigoTarifa`) REFERENCES `tarifa_iva` (`codigoTarifa`) ON UPDATE CASCADE;
	
ALTER TABLE pedidofd
	ADD codigoTarifa VARCHAR(3) NOT NULL DEFAULT '01' comment 'Código de tarifa según Hacienda';
ALTER TABLE `pedidofd`
	ADD CONSTRAINT `FK_pedidofd_tarifa_iva` FOREIGN KEY (`codigoTarifa`) REFERENCES `tarifa_iva` (`codigoTarifa`) ON UPDATE CASCADE;
	
ALTER TABLE salida
	ADD codigoTarifa VARCHAR(3) NOT NULL DEFAULT '01' comment 'Código de tarifa según Hacienda';
ALTER TABLE `salida`
	ADD CONSTRAINT `FK_salida_tarifa_iva` FOREIGN KEY (`codigoTarifa`) REFERENCES `tarifa_iva` (`codigoTarifa`) ON UPDATE CASCADE;

-- Agregar la opción de impuestos
INSERT INTO `programa` (`programa`, `descrip`) VALUES ('Impuestos', 'Mantenimiento de impuestos');
