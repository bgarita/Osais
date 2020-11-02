DROP FUNCTION if EXISTS `ConsultarUltimoTipocambio`;

delimiter $

CREATE DEFINER=`root`@`localhost` FUNCTION `ConsultarUltimoTipocambio`(
	`pCodigo` varchar(3)
)
RETURNS float
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT 'Obtener el último tipo de cambio para una moneda específica'
BEGIN
	-- Autor: Bosco Garita.
	-- Monto del tipo de cambio para moneda específica.
RETURN (
	SELECT tipoca
	FROM tipocambio
	WHERE nConsecutivo = (
		SELECT MAX(nConsecutivo)
		FROM tipocambio
		WHERE codigo = pCodigo)
	); 
END$
delimiter ;