Use LaFlor;
Drop FUNCTION `ConsecutivoReciboCXC`;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` FUNCTION `ConsecutivoReciboCXC`() RETURNS int(10)
BEGIN
	
    Declare vRecnume int;

    
    Set vRecnume = (Select recnume + 1 from config);

    Set vRecnume = If(vRecnume is null or vRecnume = 0, 1, vRecnume);

    
    If Exists(Select recnume from pagos Where recnume = vRecnume) then
        Set vRecnume = (Select max(recnume) from pagos) + 1;
    End if;

    Return vRecnume;
END$$
DELIMITER ;
