drop function if not exists `GetDBUser`

delimiter $
CREATE DEFINER=`root`@`localhost` FUNCTION `GetDBUser`()
RETURNS varchar(50) CHARSET latin1 COLLATE latin1_swedish_ci
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT 'Devuelve el string del usuario registrado en Osais antes de la arroba.'
BEGIN
    Declare vcUser varchar(50);
    Set vcUser = trim(user());

    If not Exists(Select user from usuario Where user = vcUser) then
        Set vcUser = Substring(user() FROM 1 FOR position('@' in user())-1);
        If not Exists(Select user from usuario Where user = vcUser) then
           Set vcUser = '';
        End if;
    End if;

    Return vcUser;
END$
delimiter ;