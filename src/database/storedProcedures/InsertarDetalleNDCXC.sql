drop procedure if exists InsertarDetalleNDCXP;

delimiter $
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarDetalleNDCXP`(
    IN `pNotanume` varchar(10),
    IN `pFactura` varchar(10),
    IN `pTipo` varchar(3),
    IN `pMonto` decimal(12,2),
    IN `pSaldo` decimal(12,2),
    IN `pFecha` datetime,
    IN `pProcode` varchar(15),
    IN `pUsuario` varchar(40)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    # Autor: Bosco Garita 05/05/2012.
    # Objet: Guardar el detalle de aplicacion de una nota de debito de cuentas por pagar.
    # Devuelve un ResultSet indicando si hubo error y caso de haberlo el mensaje de error.
    # Hasta hoy no se guardan registros anulados en la tabla de cxpfacturas y por lo tanto
    # no se hace ninguna revision.

    Declare vError tinyInt;
    Declare vErrorMesage varchar(500);
    Declare vSaldo Decimal(12,2); -- Saldo de la factura o nota de credito.
    Declare vNotaTipo varchar(3);
    Declare vRegistros int; -- Numero de registros afectados.

    Set vError = 0;
    Set vErrorMesage = '';
    Set pUsuario = Trim(pUsuario);
    Set vNotaTipo = 'NDB';

    # Validar el saldo de la factura o nota de credito que se afectara.
    Set vSaldo = (
        Select saldo
        From cxpfacturas
        Where factura = pFactura and tipo = pTipo
    );

    If vSaldo <> pSaldo then
        Set vError = 1;
        Set vErrorMesage = Concat(
            '[DB] La factura/NC # ',
            pFactura,
            '  ya no tiene el mismo saldo. Este es ahora ',
            vSaldo
        );
    End if;

    # Insertar el registro en la tabla de detalle de notas aplicadas.
    If vError = 0 then
        Insert into cxpnotasd(
            Notanume,
            factura,
            tipo,
            monto,
            user,
            fechaAp,
            NotaTipo,
            procode
        ) -- Se usa para identificar la llave en facturas
        Values(
            pNotanume,
            pFactura,
            pTipo,
            pMonto,
            pUsuario,
            now(),
            vNotaTipo,
            pProcode
        );
    End if;

    # No se hace una revision para determinar si se inserto o no porque si no se inserta es porque
    # ocurrio un error y de ser asi la ejecucion no continua.

    # Actualizar la factura.
    Update cxpfacturas
    Set
        saldo = saldo - pMonto,
        abono_acum = abono_acum + pMonto,
        fec_ult_ab = If(fec_ult_ab is null or Date(fec_ult_ab) < Date(pFecha), Date(pFecha), fec_ult_ab)
    Where factura = pFactura and tipo = pTipo and procode = pProcode;

    Set vRegistros = Row_count();

    # Verificar si el registro fue afectado o no.
    If vRegistros <> 1 then
        Set vError = 1;
        Set vErrorMesage = Concat(
            '[DB]Se produjo un error al intentar aplicar la factura (NC) N. ',
            pFactura,
            '. Se espera afectar 1 registro y se afecto ',
            vRegistros
        );
    End if;

    If vError = 0 then
        # Actualizar la nota de debito.
        Update cxpfacturas
        Set saldo = saldo + pMonto
        Where factura = pNotanume and tipo = vNotaTipo and procode = pProcode;

        Set vRegistros = Row_count();

        # Verificar si el registro fue afectado o no.
        If vRegistros <> 1 then
            Set vError = 1;
            Set vErrorMesage = Concat(
                '[DB] Se produjo un error al intentar aplicar la nota N. ',
                pNotanume,
                '. Se espera afectar 1 registro y se afecto ',
                vRegistros
            );
        End if;
    End if;

    # Enviar al cliente el resultado de la corrida.
    Select vError as vError, vErrorMesage as vErrorMesage;
END$
delimiter ;