drop procedure if exists InsertarDetalleNCCXC;

delimiter $
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarDetalleNCCXC`(
    IN `pNotanume` int,
    IN `pFacnume` int,
    IN `pFacnd` int,
    IN `pMonto` decimal(12,2),
    IN `pFacsald` decimal(12,2),
    IN `pUsuario` varchar(40)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    Declare vError tinyInt;
    Declare vErrorMesage varchar(500);
    Declare vFacsald Decimal(12,2);
    Declare vFacestado char(1);

    Set vError = 0;
    Set vErrorMesage = '';
    Set pUsuario = Trim(pUsuario);

    Set pNotanume = If(pNotanume > 0, pNotanume * -1, pNotanume);

    Set vFacsald = (
        Select facsald
        From faencabe
        Where facnume = pFacnume and facnd = pFacnd
    );
    Set vFacestado = (
        Select facestado
        From faencabe
        Where facnume = pFacnume and facnd = pFacnd
    );

    If vFacestado <> '' then
        Set vError = 1;
        Set vErrorMesage = Concat('[DB] La factura/ND # ', pFacnume, '  Esta anulada.');
    End if;

    If vError = 0 and vFacsald <> pFacsald then
        Set vError = 1;
        Set vErrorMesage = Concat(
            '[DB] La factura/ND # ',
            pFacnume,
            '  ya no tiene el mismo saldo. Este es ahora ',
            vFacsald
        );
    End if;

    If vError = 0 then
        Insert into notasd(
            notanume,
            facnume,
            facnd,
            monto,
            user,
            fechaAp
        )
        Values(
            pNotanume,
            pFacnume,
            pFacnd,
            pMonto,
            pUsuario,
            now()
        );
    End if;

    Update faencabe
    Set facsald = facsald - pMonto
    Where facnume = pFacnume and facnd = pFacnd;

    If Row_count() <> 1 then
        Set vError = 1;
        Set vErrorMesage = Concat(
            '[DB]Se produjo un error al intentar aplicar la factura # ',
            pFacnume,
            '. Se espera afectar 1 registro y se afecto ',
            Row_count()
        );
    End if;

    If vError = 0 then
        Update faencabe
        Set facsald = facsald + pMonto
        Where facnume = pNotanume and facnd = Abs(pNotanume);

        If Row_count() <> 1 then
            Set vError = 1;
            Set vErrorMesage = Concat(
                '[DB]Se produjo un error al intentar aplicar la nota',
                pNotanume,
                '. Se espera afectar 1 registro y se afecto ',
                Row_count()
            );
        End if;
    End if;

    Select vError as vError, vErrorMesage as vErrorMesage;
END$
delimiter ;