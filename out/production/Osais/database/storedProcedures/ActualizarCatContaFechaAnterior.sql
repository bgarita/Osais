Drop procedure if EXISTS `ActualizarCatContaFechaAnterior`;

Delimiter $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `ActualizarCatContaFechaAnterior`(
	IN `dFecha` DATETIME 
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
  	/*
  	Traer los saldos de un mes cerrado al mes actual.
  	Autor: Bosco Garita, 17/10/2020
  	*/
	UPDATE cocatalogo, hcocatalogo
		SET cocatalogo.ano_anter = hcocatalogo.ano_anter,
		    cocatalogo.cr_fecha  = hcocatalogo.cr_fecha,
		    cocatalogo.db_fecha  = hcocatalogo.db_fecha,
		    cocatalogo.cr_mes    = hcocatalogo.cr_mes,
		    cocatalogo.db_mes    = hcocatalogo.db_mes
	WHERE cocatalogo.mayor = hcocatalogo.mayor 
	AND cocatalogo.sub_cta = hcocatalogo.sub_cta
	AND cocatalogo.sub_sub = hcocatalogo.sub_sub 
	AND cocatalogo.colect  = hcocatalogo.colect
	AND hcocatalogo.fecha_cierre = dFecha;
END$$
delimiter ;