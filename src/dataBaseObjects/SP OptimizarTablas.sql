DROP PROCEDURE if EXISTS OptimizarTablas;

Delimiter $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `OptimizarTablas`()
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
	/*
	Autor:    Bosco Garita año 2010.
	Modificado: 12/03/2011, 
			  14/08/2011, 
			  06/05/2012, 
			  22/09/2013,
			  14/06/2014, 23/02/2015, 31/05/2015  Bosco Garita
	El comando OPTIMIZE hace lo mismo que ALTER TABLE .. ENGINGE = 'InnoDB' 
	pero despliega Result Sets lo cual no es muy conveniente en algunos casos.  
	Por esa razón se cambio por ALTER TABLE.
	En ambos casos la tabla se vuelve a crear y también los índices y los FK, 
	luego agrega los datos.
	06/05/2012 Se agregaron las tablas de CXP
	22/09/2013 Se agregaron algunas tablas de contabilidad
	14/06/2014 Se agregaron las tablas de órdenes de compra
	31/05/2015 Se agregaron las tabla de cajas
	*/
	
	ALTER TABLE artprov  		ENGINE = 'InnoDB'; 
	ALTER TABLE autoriz  		ENGINE = 'InnoDB'; 
	ALTER TABLE babanco  		ENGINE = 'InnoDB'; 
	ALTER TABLE bodexis  		ENGINE = 'InnoDB';
	ALTER TABLE cadesglocem  	ENGINE = 'InnoDB'; 
	ALTER TABLE cadesglocem2 	ENGINE = 'InnoDB'; 
	ALTER TABLE caja    		ENGINE = 'InnoDB'; 
	ALTER TABLE cajero  		ENGINE = 'InnoDB'; 
	ALTER TABLE casaldo 		ENGINE = 'InnoDB'; 
	ALTER TABLE catransa    		ENGINE = 'InnoDB';
	ALTER TABLE centrocosto 		ENGINE = 'InnoDB';
	ALTER TABLE coasientod  		ENGINE = 'InnoDB';
	ALTER TABLE coasientoe  		ENGINE = 'InnoDB'; 
	ALTER TABLE cocatalogo  		ENGINE = 'InnoDB';
	ALTER TABLE coconsecutivo 	ENGINE = 'InnoDB';
	ALTER TABLE comordencomprad 	ENGINE = 'InnoDB';
	ALTER TABLE comOrdenCompraDDel ENGINE = 'InnoDB';
	ALTER TABLE comordencomprae 	ENGINE = 'InnoDB'; 
	ALTER TABLE comOrdenCompraEDel ENGINE = 'InnoDB';
	ALTER TABLE config 			ENGINE = 'InnoDB';
	ALTER TABLE configcuentas 	ENGINE = 'InnoDB';
	ALTER TABLE conteo   		ENGINE = 'InnoDB';
	ALTER TABLE coperiodoco 		ENGINE = 'InnoDB';
	ALTER TABLE cotipasient 		ENGINE = 'InnoDB';
	ALTER TABLE cxcotros 		ENGINE = 'InnoDB';
	ALTER TABLE cxpfacturas 		ENGINE = 'InnoDB'; 
	ALTER TABLE notasd   		ENGINE = 'InnoDB';
	ALTER TABLE cxppagd  		ENGINE = 'InnoDB'; 
	ALTER TABLE cxppage  		ENGINE = 'InnoDB';
	ALTER TABLE faclientescontado  ENGINE = 'InnoDB';
	ALTER TABLE fadetall 		ENGINE = 'InnoDB';
	ALTER TABLE faencabe 		ENGINE = 'InnoDB';
	ALTER TABLE faexpress 		ENGINE = 'InnoDB';
	ALTER TABLE fatext   		ENGINE = 'InnoDB';
	ALTER TABLE inarticu 		ENGINE = 'InnoDB';
	ALTER TABLE inarticu_sinc 	ENGINE = 'InnoDB';
	ALTER TABLE inclient 		ENGINE = 'InnoDB';
	ALTER TABLE inconsecutivo 	ENGINE = 'InnoDB';
	ALTER TABLE infamily   		ENGINE = 'InnoDB';
	ALTER TABLE inmovimd   		ENGINE = 'InnoDB';
	ALTER TABLE inmovime   		ENGINE = 'InnoDB';
	ALTER TABLE inproved   		ENGINE = 'InnoDB'; 
	ALTER TABLE inservice  		ENGINE = 'InnoDB';
	ALTER TABLE intiposdoc 		ENGINE = 'InnoDB';
	ALTER TABLE liquidaciondiaria ENGINE = 'InnoDB';
	ALTER TABLE modulo   		ENGINE = 'InnoDB';
	ALTER TABLE monedas  		ENGINE = 'InnoDB';
	ALTER TABLE notasd   		ENGINE = 'InnoDB';
	ALTER TABLE pagarescxc 		ENGINE = 'InnoDB';
	ALTER TABLE pagos    		ENGINE = 'InnoDB';
	ALTER TABLE pagosd   		ENGINE = 'InnoDB';
	ALTER TABLE paramusuario 	ENGINE = 'InnoDB';
	ALTER TABLE pedidod  		ENGINE = 'InnoDB';
	ALTER TABLE pedidoe  		ENGINE = 'InnoDB';
	ALTER TABLE pedidofd 		ENGINE = 'InnoDB';
	ALTER TABLE pedidofe 		ENGINE = 'InnoDB';
	ALTER TABLE prodiavisita 	ENGINE = 'InnoDB';
	ALTER TABLE programa  		ENGINE = 'InnoDB';
	ALTER TABLE salida    		ENGINE = 'InnoDB';
	ALTER TABLE tarjeta   		ENGINE = 'InnoDB';
	ALTER TABLE territor  		ENGINE = 'InnoDB';
	ALTER TABLE tipocambio 		ENGINE = 'InnoDB';
	ALTER TABLE usuario   		ENGINE = 'InnoDB';
	ALTER TABLE vendedor  		ENGINE = 'InnoDB';
	ALTER TABLE venexclu  		ENGINE = 'InnoDB';
	ALTER TABLE wrk_docinvd  	ENGINE = 'InnoDB';
	ALTER TABLE wrk_docinve  	ENGINE = 'InnoDB';
	ALTER TABLE wrk_fadetall 	ENGINE = 'InnoDB';
	ALTER TABLE wrk_faencabe 	ENGINE = 'InnoDB';
	ALTER TABLE tarifa_iva		ENGINE = 'InnoDB';
	ALTER TABLE saisystem.notificado ENGINE = 'InnoDB'; 
END$$

Delimiter ;