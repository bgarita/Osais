Use LaFlor;
Drop table comordencomprad;
Drop table comordencompraddel;
Drop table comordencomprae;
Drop table comordencompraedel;

CREATE TABLE `comOrdenCompraE` (
  `movorco` varchar(10) NOT NULL COMMENT 'Orden de compra',
  `Movdesc` varchar(150) DEFAULT NULL COMMENT 'Descripción del movimiento',
  `movfech` date NOT NULL COMMENT 'Fecha del movimiento',
  `tipoca` float NOT NULL DEFAULT '1' COMMENT 'Tipo de cambio',
  `user` varchar(40) NOT NULL COMMENT 'Usuario',
  `movtido` smallint(3) unsigned NOT NULL COMMENT 'Ver tabla INTIPOSDOC',
  `movfechac` datetime NOT NULL COMMENT 'Fecha y hora del sistema',
  `codigoTC` varchar(3) DEFAULT NULL COMMENT 'Código del tipo del cambio',
  `procode` varchar(15) DEFAULT NULL COMMENT 'Código de proveedor',
  `movcerr` varchar(1) DEFAULT 'N' COMMENT 'Orden de compra cerrada (S/N)',
  `movdocu` varchar(10) NOT NULL DEFAULT '' COMMENT 'Documento de entrada en inventarios',
  PRIMARY KEY (`movorco`),
  KEY `fk_comOrdenCompraE_monedas_idx` (`codigoTC`),
  KEY `fk_comOrdenCompraE_inproved_idx` (`procode`),
  CONSTRAINT `fk_comOrdenCompraE_inproved` FOREIGN KEY (`procode`) REFERENCES `inproved` (`procode`) ON UPDATE CASCADE,
  CONSTRAINT `fk_comOrdenCompraE_monedas` FOREIGN KEY (`codigoTC`) REFERENCES `monedas` (`codigo`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `comOrdenCompraD` (
  `movorco` varchar(10) NOT NULL COMMENT 'Número de documento',
  `artcode` varchar(20) NOT NULL,
  `bodega` varchar(3) NOT NULL,
  `movcant` decimal(14,4) unsigned NOT NULL DEFAULT '0.0000' COMMENT 'Cantidad a pedir',
  `artcosfob` decimal(14,4) unsigned NOT NULL DEFAULT '0.0000',
  `artcost` decimal(14,4) NOT NULL DEFAULT '0.0000' COMMENT 'Costo actual',
  `movreci` decimal(12,4) DEFAULT '0.0000' COMMENT 'Cantidad recibida',
  KEY `fk_comOrdenCompraD_comOrdendComparaE_idx` (`movorco`),
  KEY `fk_comOrdenCompraD_bodexis_idx` (`bodega`,`artcode`),
  CONSTRAINT `fk_comOrdenCompraD_bodexis` FOREIGN KEY (`bodega`, `artcode`) REFERENCES `bodexis` (`bodega`, `artcode`) ON UPDATE CASCADE,
  CONSTRAINT `fk_comOrdenCompraD_comOrdendComparaE` FOREIGN KEY (`movorco`) REFERENCES `comOrdenCompraE` (`movorco`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `comOrdenCompraDDel` (
  `movorco` varchar(10) NOT NULL COMMENT 'Número de documento',
  `artcode` varchar(20) NOT NULL COMMENT 'Código de artículo',
  `bodega` varchar(3) NOT NULL COMMENT 'Código de bodega',
  `movcant` decimal(14,4) unsigned NOT NULL DEFAULT '0.0000' COMMENT 'Cantidad a pedir',
  `artcosfob` decimal(14,4) unsigned NOT NULL DEFAULT '0.0000',
  `artcost` decimal(14,4) NOT NULL DEFAULT '0.0000' COMMENT 'Costo actual',
  `movreci` decimal(12,4) DEFAULT '0.0000' COMMENT 'Cantidad recibida',
  KEY `fk_comOrdenCompraDDel_comOrdendComparaEDel_idx` (`movorco`),
  KEY `fk_comOrdenCompraDDel_bodexis_idx` (`bodega`,`artcode`),
  CONSTRAINT `fk_comOrdenCompraDDel_bodexis` FOREIGN KEY (`bodega`, `artcode`) REFERENCES `bodexis` (`bodega`, `artcode`) ON UPDATE CASCADE,
  CONSTRAINT `fk_comOrdenCompraDDel_comOrdendComparaEDel` FOREIGN KEY (`movorco`) REFERENCES `comOrdenCompraEDel` (`movorco`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `comOrdenCompraEDel` (
  `movorco` varchar(10) NOT NULL COMMENT 'Orden de compra',
  `Movdesc` varchar(150) DEFAULT NULL COMMENT 'Descripción del movimiento',
  `movfech` date NOT NULL COMMENT 'Fecha del movimiento',
  `tipoca` float NOT NULL DEFAULT '1' COMMENT 'Tipo de cambio',
  `user` varchar(40) NOT NULL COMMENT 'Usuario',
  `movtido` smallint(3) unsigned NOT NULL COMMENT 'Ver tabla INTIPOSDOC',
  `movfechac` datetime NOT NULL COMMENT 'Fecha y hora del sistema',
  `codigoTC` varchar(3) DEFAULT NULL COMMENT 'Código del tipo del cambio',
  `procode` varchar(15) DEFAULT NULL COMMENT 'Código de proveedor',
  `movcerr` varchar(1) DEFAULT 'N' COMMENT 'Orden de compra cerrada (S/N)',
  `movdocu` varchar(10) NOT NULL DEFAULT '' COMMENT 'Documento de entrada en inventarios',
  `fechabo` datetime NOT NULL COMMENT 'Fecha y hora de borrado',
  `userbo` varchar(40) NOT NULL COMMENT 'Usuario que realizó el borrado',
  PRIMARY KEY (`movorco`),
  KEY `fk_comOrdenCompraEDel_monedas_idx` (`codigoTC`),
  KEY `fk_comOrdenCompraEDel_inproved_idx` (`procode`),
  CONSTRAINT `fk_comOrdenCompraEDel_inproved` FOREIGN KEY (`procode`) REFERENCES `inproved` (`procode`) ON UPDATE CASCADE,
  CONSTRAINT `fk_comOrdenCompraEDel_monedas` FOREIGN KEY (`codigoTC`) REFERENCES `monedas` (`codigo`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



