drop procedure if exists AnularPagoCXP;

delimiter $
CREATE DEFINER=`root`@`localhost` PROCEDURE `AnularPagoCXP`(
    IN `pnRecnume` int,
    IN `pUsername` VARCHAR(50)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    # Autor : Bosco Garita Azofeifa 28/05/2012
    # Objet : Anular un recibo de cuentas por pagar.
    # Result: Devuelve un RS con dos campos vError y vMensajeError para indicar si hubo error o no.

    Declare vError tinyInt(1);
    Declare vMensajeError varchar(200);
    Declare vProcode varchar(15);

    Set vError = 0;
    Set vMensajeError = '';

    # Actualizar el estatus del recibo
    Update cxppage
    Set estado = 'A',
        userAnula = pUsername,
        fechaAnula = now()
    Where recnume = pnRecnume and estado = '';

    If Row_count() = 0 then
        Set vError = 1;
        Set vMensajeError = '[DB] Recibo no se encuentra o ya estaba anulado.';
    End if;

    # Revertir el proceso de aplicacion del recibo.
    # Las facturas y/o NC afectadas vuelven a su estado antes de ser afectadas por el recibo.
    If vError = 0 then
        Update cxpfacturas, cxppagd
        Set cxpfacturas.saldo = cxpfacturas.saldo + cxppagd.monto,
            cxpfacturas.abono_acum = cxpfacturas.abono_acum - cxppagd.monto
        Where cxppagd.recnume = pnRecnume
        and cxppagd.factura = cxpfacturas.Factura
        and cxppagd.tipo = cxpfacturas.tipo;

        # Verificar los registros afectados
        If Row_count() = 0 then
            Set vError = 1;
            Set vMensajeError = '[DB] Las facturas referidas por este recibo no pudieron ser encontradas.';
        End if;
    End if;

    If vError = 0 then
        # Si no hubo error actualizo el saldo del proveedor
        Set vProcode = (
            Select procode
            from cxppage
            Where recnume = pnRecnume
            Limit 1
        );

        Call RecalcularSaldoProveedores(vProcode);
    End if;

    Select vError as vError, vMensajeError as vMensajeError;

END$
delimiter ;