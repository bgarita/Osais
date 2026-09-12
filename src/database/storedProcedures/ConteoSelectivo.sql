drop procedure if exists ConteoSelectivo;

delimiter $
CREATE DEFINER=`root`@`localhost` PROCEDURE `ConteoSelectivo`(
    IN `pBodega` varchar(3),
    IN `pUsername` VARCHAR(50)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    # Autor: Bosco Garita 05/02/2011.
    # Objet: Preparar un conteo selectivo. Respeta cantidades digitadas previamente.
    # Devuelve: Numero de registros afectados.

    Update conteo
    Set InUseByUser = pUsername,
        cantidad = If(cantidad = 0, artexis, cantidad)
    Where bodega = pBodega;

END$
delimiter ;