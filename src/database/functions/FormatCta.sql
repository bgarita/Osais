Drop function if exists formatCta;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` FUNCTION `formatCta`(
	nomCta varchar(500), 	-- Nombre de la cuenta o texto a formatear
	nivel int, 				-- Nivel de cuenta
	formatoNombre tinyInt(1), -- ¿Debe tratarse como un nombre personal? 1=Si, 0=No
	indent INT) RETURNS varchar(500) CHARSET latin1
BEGIN
	-- Autor Bosco Garita, 11/09/2016
	/*
	* Tiene como objetivo poner en mayúscula las cuentas de mayor y en minúscula
	* las cuentas de movimiento.  Además crea una indentación de n caracteres
	* para las cuentas de movimientos.
	* Si la cuenta tiene formato de nombre entonces no se toca.
	*/

	if formatoNombre = 1 THEN
		return nomCta;
	end if ;

	if nivel = 0 THEN
		Set nomCta = Upper(nomCta);
    else 
		Set nomCta = Concat(upper(substring(nomCta,1,1)), Lower(substring(nomCta,2)));
		Set nomCta = Concat(lpad('',indent, ' '),nomCta);
	end if;

	RETURN nomCta;
END$$
DELIMITER ;
