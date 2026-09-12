DROP TABLE if EXISTS cocuentasres;

delimiter $$
CREATE TABLE `cocuentasres` (
	`cuenta` VARCHAR(12) NOT NULL COLLATE 'latin1_swedish_ci',
	`user` CHAR(16) NOT NULL DEFAULT '' COLLATE 'latin1_swedish_ci',
	`recno` INT(11) NOT NULL AUTO_INCREMENT COMMENT 'Id único del registro',
	PRIMARY KEY (`recno`) USING BTREE,
	CONSTRAINT `FK_cocuentasres_usuario` FOREIGN KEY (`user`) REFERENCES `usuario` (`user`) ON UPDATE CASCADE
)
COMMENT='Cuentas restringidas'
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;$$
