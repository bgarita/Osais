drop procedure if exists AnularFacNCNDCXC;

delimiter $
CREATE DEFINER=`root`@`localhost` PROCEDURE `AnularFacNCNDCXC`(
    IN `pnFacnume` int,
    IN `pnFacnd` int,
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

    Set vClicode = (
        Select clicode
        from faencabe
        Where facnume = pnFacnume and facnd = pnFacnd and facestado = ''
    );

    If vClicode is null then
        Set vError = 1;
        Set vMensajeError = '[DB]El registro no existe en el periodo actual o ya esta anulado';
    End if;

    If vError = 0 and pnFacnume > 0 and Exists(
        Select pagosd.facnume
        from pagosd
        Inner join pagos on pagosd.recnume = pagos.recnume
        Where pagosd.facnume = pnFacnume
        and facnd = pnFacnd
        and pagos.estado = ''
    ) then
        Set vError = 1;
        Set vMensajeError = '[DB]Hay recibos aplicados a esta Fact/ND. Debe anularlos primero.';
    End if;

    If vError = 0 and pnFacnume > 0 and Exists(
        Select facnume
        from notasd
        Where facnume = pnFacnume
        and facnd = pnFacnd
    ) then
        Set vError = 1;
        Set vMensajeError = '[DB]Hay NCs aplicadas a esta Fact/ND. Debe anularlas primero.';
    End if;

    If pnFacnume < 0 then
        Update faencabe, notasd
        Set faencabe.facsald = faencabe.facsald + IfNull(notasd.monto, 0)
        Where faencabe.facnume = notasd.facnume
        and faencabe.facnd = notasd.facnd
        and notasd.notanume = pnFacnume;

        Update faencabe
        Set facsald = facsald - IfNull((
            Select sum(monto)
            from notasd
            Where notanume = faencabe.facnume
        ), 0)
        Where facnume = pnFacnume and facnd = Abs(pnFacnume);

        Delete from notasd Where notanume = pnFacnume;
    End if;

    Update faencabe
    Set facestado = 'A',
        userAnula = pUsername,
        fechaAnula = now()
    Where facnume = pnFacnume
    and facnd = pnFacnd;

    If Row_count() <> 1 then
        Set vError = 1;
        Set vMensajeError = '[DB]Hay una incongruencia en la tabla de encabezados de facturas.';
    End if;

    If vError = 0 then
        Call RecalcularSaldoClientes(vClicode);
    End if;

    If vError = 0 and pnFacnd >= 0 then
        Call AnularDocInv(
            Abs(pnFacnume),
            If(pnFacnd = 0, 'S', 'E'),
            If(pnFacnd = 0, 8, 4),
            'CXC',
            pUsername
        );
    End if;

    If vError = 1 or pnFacnd < 0 then
        Select vError as vError, vMensajeError as vMensajeError;
    End if;
END$
delimiter ;
