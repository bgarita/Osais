DROP PROCEDURE if EXISTS RecalcularSaldoClientes_Cierre;

Delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `RecalcularSaldoClientes_Cierre`()
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN

    # Autor:        Bosco Garita 13/03/2011
    # Descripción:  Recalcular el saldo de los clientes para el cierre mensual 
    #               o para cualquier otro proceso que requiera el saldo a otra fecha que no sea la actual.
    #               Este proceso se basa en la tabla temporal tmp_faencabe que genera el SP CalcularCXC()
    #               Esta tabla no tiene nulos ni facturas de contado y viene calculada a una fecha específica
    #               que no es necesario conocer en este SP.

    Update inclient Set clisald = 0;

    Update inclient
    Set clisald = IfNull((Select sum(facsald * tipoca)
                         from tmp_faencabe  # En esta tabla no se consideran los registros nulos porque no los hay.
                         Where clicode = inclient.clicode 
                         and facsald <> 0),0);

END$$
Delimiter ;