drop procedure if exists AplicarAjusteInventario;

delimiter $
CREATE DEFINER=`root`@`localhost` PROCEDURE `AplicarAjusteInventario`(
    IN `pBodega` varchar(3),
    IN `pMovdocu` varchar(10),
    IN `pMovfech` datetime,
    IN `pUsername` VARCHAR(50)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    # Autor: Bosco Garita A. 09/02/2011.
    # Objet: Aplicar ajuste de inventarios.
    # Este SP devuelve un Result Set con dos campos que indican si hubo error o no.
    # IMPORTANTE: Este SP debe correr dentro de una transaccion.

    Declare vError tinyInt(1);        -- 1=Hubo error, 0=No hubo error
    Declare vMensajeErr varchar(200); -- Mensaje de error o blanco si no hay error.
    Declare vHayEntradas tinyInt(1);  -- Determina si hay entradas por ajuste.
    Declare vHaySalidas tinyInt(1);   -- Determina si hay salidas por ajuste.
    Declare vMovdesc varchar(150);    -- Descripcion del movimiento.
    Declare vCodigoTC varchar(3);     -- Codigo de moneda local.

    Set vError = 0;
    Set vMensajeErr = '';
    Set vHayEntradas = 0;
    Set vHaySalidas = 0;
    Set vMovdesc = Concat('Ajuste de inventario al ', dtoc(pMovfech));
    Select CodigoTC from config into vCodigoTC;

    # Validaciones
    If pBodega is null or pBodega = '' then
        Set vError = 1;
        Set vMensajeErr = '[BD] El codigo de bodega es incorrecto.';
    End if;

    If vError = 0 and (pMovdocu is null or pMovdocu = '') then
        Set vError = 1;
        Set vMensajeErr = '[BD] El numero de documento es incorrecto.';
    End if;

    # Los valores del ultimo parametro se basan en la tabla INTIPOSDOC
    If vError = 0 and (ConsultarDocumento(pMovdocu, 'E', 11) or ConsultarDocumento(pMovdocu, 'S', 12)) then
        Set vError = 1;
        Set vMensajeErr = '[BD] El numero de documento ya existe, use otro.';
    End if;

    If vError = 0 and not PermitirFecha(pMovfech) then
        Set vError = 1;
        Set vMensajeErr = '[BD] No puede aplicar ajustes a un periodo cerrado, use una fecha moyor.';
    End if;
    # Fin de validaciones

    # Inicia proceso de aplicacion del ajuste
    If vError = 0 then
        # Actualizar tabla de existencias por bodega
        Update bodexis A, conteo B
        Set a.artexis = a.artexis + (b.cantidad - b.artexis)
        Where a.bodega = b.bodega
        and a.artcode = b.artcode
        and b.bodega = pBodega;

        # Deben procesarse por separado las entradas por ajuste de las salidas por ajuste.
        If Exists(Select 1 from conteo Where bodega = pBodega and (cantidad - artexis) > 0 limit 1) then
            Set vHayEntradas = 1;
        End if;

        If Exists(Select 1 from conteo Where bodega = pBodega and (cantidad - artexis) < 0 limit 1) then
            Set vHaySalidas = 1;
        End if;
    End if;

    # Insertar el encabezado para las entradas (si las hay) -- movtido = 11
    If vError = 0 and vHayEntradas then
        Call InsertarEncabezadoDocInv(
            pMovdocu, -- Documento
            'E',      -- Tipo de movimiento (E o S)
            'Ajuste', -- Orden de compra
            vMovdesc, -- Descripcion del movimiento
            pMovfech, -- Fecha del movimiento
            1,        -- Tipo de cambio
            11,       -- Tipo de documento
            ' ',      -- Persona que solicita (se usa en salidas)
            vCodigoTC,
            pUsername
        );

        If row_count() = 0 then
            Set vError = 1;
            Set vMensajeErr = '[BD] No se pudo insertar el encabezado de entradas por ajuste.';
        End if;
    End if;

    # Insertar el detalle de las entradas (si las hay) -- movtido = 11
    If vError = 0 and vHayEntradas then
        Insert into Inmovimd (
            Movdocu,
            Movtimo,
            Artcode,
            Bodega,
            Procode,
            Movcant,
            Movcoun,
            Artcosfob,
            Artprec,
            Facimve,
            Facdesc,
            Movtido,
            Centroc,
            Fechaven
        )
        Select
            pMovdocu,                  -- Documento
            'E',                       -- Tipo de movimiento
            a.artcode,                 -- Articulo
            a.bodega,                  -- Bodega
            '',                        -- Proveedor
            (a.cantidad - a.artexis),  -- Cantidad
            a.artcosp,                 -- Costo
            b.artcosFOB,               -- Costo FOB
            b.artpre1,                 -- Precio de venta # 1
            0,                         -- Impuesto de ventas
            0,                         -- Descuento
            11,                        -- Tipo de documento
            '',                        -- Centro de costo
            null                       -- Fecha de vencimiento
        From conteo A
        Inner join inarticu B on a.artcode = b.artcode
        Where a.bodega = pBodega and (a.cantidad - a.artexis) > 0;

        If row_count() = 0 then
            Set vError = 1;
            Set vMensajeErr = '[BD] No se pudo insertar el detalle de entradas por ajuste.';
        End if;
    End if;

    # Insertar el encabezado para las salidas (si las hay) -- movtido = 12
    If vError = 0 and vHaySalidas then
        Call InsertarEncabezadoDocInv(
            pMovdocu, -- Documento
            'S',      -- Tipo de movimiento (E o S)
            'Ajuste', -- Orden de compra
            vMovdesc, -- Descripcion del movimiento
            pMovfech, -- Fecha del movimiento
            1,        -- Tipo de cambio
            12,       -- Tipo de documento
            ' ',      -- Persona que solicita (se usa en salidas)
            vCodigoTC,
            pUsername
        );

        If row_count() = 0 then
            Set vError = 1;
            Set vMensajeErr = '[BD] No se pudo insertar el encabezado de salidas por ajuste.';
        End if;
    End if;

    # Insertar el detalle de las salidas (si las hay) -- movtido = 12
    If vError = 0 and vHaySalidas then
        Insert into Inmovimd (
            Movdocu,
            Movtimo,
            Artcode,
            Bodega,
            Procode,
            Movcant,
            Movcoun,
            Artcosfob,
            Artprec,
            Facimve,
            Facdesc,
            Movtido,
            Centroc,
            Fechaven
        )
        Select
            pMovdocu,                -- Documento
            'S',                     -- Tipo de movimiento
            a.artcode,               -- Articulo
            a.bodega,                -- Bodega
            '',                      -- Proveedor
            Abs(a.cantidad - a.artexis), -- Cantidad
            a.artcosp,               -- Costo
            b.artcosFOB,             -- Costo FOB
            b.artpre1,               -- Precio de venta # 1
            0,                       -- Impuesto de ventas
            0,                       -- Descuento
            12,                      -- Tipo de documento
            '',                      -- Centro de costo
            null                     -- Fecha de vencimiento
        From conteo A
        Inner join inarticu B on a.artcode = b.artcode
        Where a.bodega = pBodega and (a.cantidad - a.artexis) < 0;

        If row_count() = 0 then
            Set vError = 1;
            Set vMensajeErr = '[BD] No se pudo insertar el detalle de salidas por ajuste.';
        End if;
    End if;

    # Actualizar los campos userAplica y movdocu
    If vError = 0 then
        Update conteo
        Set userAplica = pUsername,
            movdocu = pMovdocu
        Where bodega = pBodega;

        If row_count() = 0 then
            Set vError = 1;
            Set vMensajeErr = '[BD] No se pudo actualizar la tabla de conteo fisico.';
        End if;
    End if;

    # Trasladar los datos aplicados al historico
    If vError = 0 then
        Insert into hconteo (
            bodega,
            artcode,
            cantidad,
            artexis,
            artcosp,
            fecha,
            userDigita,
            userAplica,
            movdocu,
            pordesc
        )
        Select
            bodega,
            artcode,
            cantidad,
            artexis,
            artcosp,
            fecha,
            userDigita,
            userAplica,
            movdocu,
            pordesc
        From Conteo
        Where bodega = pBodega;

        If row_count() = 0 then
            Set vError = 1;
            Set vMensajeErr = '[BD] No se pudo trasladar la tabla de conteo fisico al historico.';
        End if;
    End if;

    # Eliminar los registros trasladados
    Delete from conteo where bodega = pBodega;

    If row_count() = 0 then
        Set vError = 1;
        Set vMensajeErr = '[BD] No se pudieron eliminar los registros del conteo.';
    End if;

    Select vError as Error, vMensajeErr as MensajeErr;

END$
delimiter ;