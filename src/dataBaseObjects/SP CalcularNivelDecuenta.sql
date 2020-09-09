DROP PROCEDURE if EXISTS calcularNivelDeCuenta;

Delimiter $$

CREATE PROCEDURE `calcularNivelDeCuenta`()
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT 'Calcular el campo nivelc en base al contenido de los campos de la cuenta'
BEGIN
	/*
	Creado por Bosco Garita, 26/12/2016
	Hasta ahora solo se usa manualmente para recalcular el valor del campo Nivelc end la tabla cocatalogo.
	*/
	
	UPDATE cocatalogo
	SET nivelC =
		If(sub_cta  = '000' AND sub_sub  = '000' AND colect  = '000', 1,
		If(sub_cta != '000' AND sub_sub  = '000' AND colect  = '000', 2,
		If(sub_cta != '000' AND sub_sub != '000' AND colect  = '000', 3,
		If(sub_cta != '000' AND sub_sub != '000' AND colect != '000', 4, 0))));
END$$

delimiter ;