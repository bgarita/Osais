drop procedure if exists AnularPagoCXC;

delimiter $
CREATE DEFINER=`root`@`localhost` PROCEDURE `AnularPagoCXC`(
    IN `pnRecnume` int,
    IN `pUsername` VARCHAR(50)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    Declare vError tinyInt(1);
    Declare vMensajeError varchar(200);
    Declare vClicode int;

    Set vError = 0;
    Set vMensajeError = '';

    Update pagos
    Set estado = 'A',
        userAnula = pUsername,
        fechaAnula = now()
    Where recnume = pnRecnume and estado = '';

    If Row_count() = 0 then
        Set vError = 1;
        Set vMensajeError = '[DB] Recibo no se encuentra o ya estaba anulado.';
    End if;

    If vError = 0 then
        Update faencabe, pagosd
        Set faencabe.facsald = faencabe.facsald + pagosd.monto
        Where pagosd.recnume = pnRecnume
        and faencabe.facnume = pagosd.facnume
        and faencabe.facnd = pagosd.facnd;

        If Row_count() = 0 then
            Set vError = 1;
            Set vMensajeError = '[DB] Las facturas referidas por este recibo no pudieron ser encontradas.';
        End if;
    End if;

    If vError = 0 then
        Set vClicode = (
            Select clicode
            from pagos
            Where recnume = pnRecnume
            Limit 1
        );

        Call RecalcularSaldoClientes(vClicode);
    End if;

    Select vError as vError, vMensajeError as vMensajeError;

END$
delimiter ;