
ALTER TABLE `hcoasientod`
	CHANGE COLUMN `db_cr` `db_cr` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '1=débito, 0=Crédito' AFTER `descrip`;