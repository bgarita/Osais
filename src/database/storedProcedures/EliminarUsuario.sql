DROP PROCEDURE if EXISTS EliminarUsuario;

delimiter $
CREATE DEFINER=`root`@`localhost` PROCEDURE `EliminarUsuario`(
	IN `pUser` char(16)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
	DECLARE records INT;
	
	SELECT COUNT(*) INTO records FROM catransa WHERE cajero = pUser;
  	IF records = 0 THEN
  		DELETE FROM cajero WHERE user = pUser;
  	END IF;
  	
  	DELETE FROM usuario WHERE user = pUser;
END$
delimiter ;