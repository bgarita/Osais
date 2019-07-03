ALTER TABLE `LaFlor`.`hinclient` 
CHANGE COLUMN `idtipo` `idtipo` TINYINT(4) NOT NULL DEFAULT '1' 
	COMMENT 'Tipo de identificación: 1=Céd. Física, 2=Céd. Jurídica, 3=Documento de Identificación Migratorio para Extranjeros (DIMEX), 4=Número de Identificación Tributario Especial (NITE)' ;

ALTER TABLE `LaFlor`.`inclient` 
CHANGE COLUMN `idtipo` `idtipo` TINYINT(4) NOT NULL DEFAULT '1' 
	COMMENT 'Tipo de identificación: 1=Céd. Física, 2=Céd. Jurídica, 3=Documento de Identificación Migratorio para Extranjeros (DIMEX), 4=Número de Identificación Tributario Especial (NITE)' ;
