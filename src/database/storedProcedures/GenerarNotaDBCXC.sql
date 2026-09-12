drop procedure if exists GenerarNotaDBCXC;

delimiter $
CREATE DEFINER=`root`@`localhost` PROCEDURE `GenerarNotaDBCXC`(
    IN `pClicode` int,
    OUT `pSuccess` tinyint(1),
    OUT `pMensaje` varchar(800),
    IN `pVencido` double,
    IN `pUsuario` varchar(40)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    Declare vPrim_fecha datetime;
    Declare vUlt_fecha datetime;
    Declare vFacnume int;
    Declare vIntervalo smallInt(3);
    Declare vDiasG smallInt(3);
    Declare vMora float;
    Declare vIncrementoM float;
    Declare vVend smallInt(3);
    Declare vTerr smallInt(3);
    Declare vBodega char(3);
    Declare vCodigoTC char(3);
    Declare vTipoca float;
    Declare vFacmont double;
    Declare vNmeses smallInt;
    Declare vPrecio tinyInt(2);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN
        SHOW ERRORS;
        SET pMensaje = Concat('[DB] No se pudo generar ND para el cliente ', pClicode);
        SET pSuccess = 0;
    END;

    # Obtener los parametros para los calculos.
    SELECT
        ndeb + 1,      -- Consecutivo de NDs
        intervalo,     -- Intervalo de calculo (se calcula cada n dias)
        diasG,         -- Dias de gracia.
        bodega,        -- Bodega predeterminada
        codigoTC,      -- Codigo de tipo de cambio de la moneda local.
        mora,          -- Tasa de interes moratorio.
        incrementoM    -- Tasa de interes incremental.
    FROM config
    INTO
        vFacnume,
        vIntervalo,
        vDiasG,
        vBodega,
        vCodigoTC,
        vMora,
        vIncrementoM;

    # Obtener el TC de hoy.
    Set vTipoca = ConsultarTipoca(vCodigoTC, date(now()));

    # Obtener los parametros predeterminados para el cliente.
    SELECT vend, terr, cliprec
    FROM inclient
    WHERE clicode = pClicode
    INTO vVend, vTerr, vPrecio;
    Set pUsuario = Trim(pUsuario);

    # Obtener la primer y la ultima fecha de las NDs por interes moratorio para este cliente.
    SELECT
        Min(facfech),
        Max(facfech)
    FROM faencabe
    WHERE clicode = pClicode
      AND facnd < 0
      AND facestado = ''
      AND chequeotar = 'INTERESES MORATORIOS'
    INTO vPrim_fecha, vUlt_fecha;

    # Si no hay ninguna ND por interes moratorio o si ya se debe generar una nueva...
    IF vUlt_fecha is null OR (vUlt_fecha + INTERVAL vIntervalo + vDiasG DAY) <= DATE(now()) then
        Update config Set ndeb = vFacnume; -- Actualizar el consecutivo.

        # Calcular el monto de la ND.
        Case
            When vPrim_fecha is null then
                Set vFacmont = pVencido * (vMora / 100);

            When vPrim_fecha is not null and vPrim_fecha = vUlt_fecha then
                Set vFacmont = pVencido * ((vMora + vIncrementoM) / 100);

            When vPrim_fecha is not null and vPrim_fecha < vUlt_fecha then
                # Si el cliente es reincidente se castigan los meses de la mora.
                Set vNmeses = TIMESTAMPDIFF(MONTH, vPrim_fecha, vUlt_fecha) + 1;
                Set vFacmont = pVencido * ((vIncrementoM * vNmeses + vMora) / 100);
        End Case;

        # Insertar el registro de la ND.
        INSERT INTO `faencabe`(
            `facnume`,
            `clicode`,
            `chequeotar`,
            `vend`,
            `terr`,
            `facfech`,
            `facplazo`,
            `facmont`,
            `facfepa`,
            `facsald`,
            `facnd`,
            `user`,
            `precio`,
            `facfechac`,
            `codigoTC`,
            `tipoca`
        )
        VALUES(
            vFacnume,
            pClicode,
            'INTERESES MORATORIOS',
            vVend,
            vTerr,
            now() - INTERVAL 2 DAY,
            1,
            vFacmont,
            now() - INTERVAL 1 DAY,
            vFacmont,
            vFacnume * -1,
            pUsuario,
            vPrecio,
            now(),
            vCodigoTC,
            vTipoca
        );

        # Insertar el detalle de la ND.
        Insert into Fadetall(
            facnume,
            artcode,
            bodega,
            faccant,
            artprec,
            facmont,
            facnd
        )
        Values(
            vFacnume,
            '_NOINV',
            vBodega,
            1,
            vFacmont,
            vFacmont,
            vFacnume * -1
        );

        # Actualizar el saldo del cliente.
        Update inclient
        Set clisald = clisald + vFacmont
        Where clicode = pClicode;
    End If;

    SET pMensaje = '';
    SET pSuccess = 1;
END$
delimiter ;
