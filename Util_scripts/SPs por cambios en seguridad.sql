-- Estos cambios deben ejecutarse en las base de datos de Ingrid y de Ana

USE c0001;

Drop procedure if exists AnularDocInv;
delimiter $
CREATE DEFINER=`root`@`localhost` PROCEDURE `AnularDocInv`(
	IN `pcMovodocu` varchar(10),
	IN `pcMovtimo` char(1),
	IN `pnMovtido` smallint(3),
	IN `pcModulo` varchar(3),
	IN `pUsername` VARCHAR(50)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    # Autor: Bosco Garita A.
    # Objet: Anular un documento de inventario
    # Modif: Bosco Garita A. 07/05/2012
    # Agrego revisión de la tabla de facturas de proveedor para quitar la referencia si existe.
    # También incluyo la revisión del campo movCerrado (no se permite anular un documento que
    # se encuentra en un periodo cerrado.
    # Bosco Garita A. 23/02/2013
    # Agrego control para anulación de ajustes de inventario.

    Declare vError tinyInt(1);
    Declare vMensajeError varchar(300);
    Declare vMovtido SMALLINT(3);
    Declare vMovtimo CHAR(1);
    Declare vRegistros smallint; -- Bosco agregado 23/02/2013

    # Definir los tipos de movimiento cuando se anula un movimiento inter-bodega
    # o cuando se anula un ajuste.
    Case
        -- Movimiento Inter-bodega (5=Entrada, 10=Salida)
        When pnMovtido = 5 then
            Set vMovtido = 10;
            Set vMovtimo = 'S';

        When pnMovtido = 10 then
            Set vMovtido = 5;
            Set vMovtimo = 'E';

        -- Movimiento por ajuste (11=Entrada, 12=Salida)
        When pnMovtido = 11 then
            Set vMovtido = 12;
            Set vMovtimo = 'S';

        When pnMovtido = 12 then
            Set vMovtido = 11;
            Set vMovtimo = 'E';

        -- Si no se trata de un movimiento doble entonces vMovtido debe ser 0
        Else
            Set vMovtido = 0;
    End Case;

    Set vError = 0;
    Set vMensajeError = '';

    # Bosco modificado 10/05/2012.
    # Cambio la sintaxis para una mejor comprensión.
    # Set vError = If((Select modulo from intiposdoc where movtido = pnMovtido) = pcModulo,0,1);
    # Set vMensajeError = If(vError = 1,Concat('[BD] (Inv) No puede anular este documento desde ',pcModulo),'');

    # Validar si se puede anular este documento desde el módulo que lo ejecuta.
    If not Exists(Select modulo from intiposdoc where movtido = pnMovtido) then
        Set vError = 1;
        Set vMensajeError = Concat('[BD] (Inv) No puede anular este documento desde ',pcModulo);
    End if;

    # Fin Bosco modificado 10/05/2012.

    # Bosco agregado 23/02/2013.
    # Se usa para controlar el row_count cuando se trata de anular un ajuste.
    Set vRegistros = 0;
    # Fin Bosco agregado 23/02/2013.

    If vError = 0 then
        Update inmovime
        Set
            estado = 'A',
            userAnula = pUsername,
            fechaAnula = now()
        Where movdocu = pcMovodocu
          and movtimo = pcMovtimo
          and movtido = pnMovtido
          and (estado is null or estado = '')
          and movCerrado = 'N'; -- Bosco agregado 07/05/2012.

        Set vRegistros = row_count();

        If vRegistros = 0 then
            /*
            Bosco 05/07/2015.
            Al incluir este Warning en el mensaje es para que algunos procesos
            sigan adelante aun cuando este proceso dé error, como por ejemplo
            cuando se anula una factura. Esto se permite porque puede suceder
            que la factura se anule justamente porque no aparece en inventarios.
            */
            Set vError = 1;
            Set vMensajeError =
                '[BD] (Inv) (Warning) Documento no existe, ya está anulado o se encuentra en un período cerrado';

            If pcMovtimo in (11,12) then
                Set vError = 0;
                Set vMensajeError = '';
            End if;
        End if;
    End if;

    # Si se trata de algún movimiento doble (Inter-bodega o ajuste) entonces correrá este if.
    If vError = 0 and vMovtido > 0 then
        Update inmovime
        Set
            estado = 'A',
            userAnula = pUsername,
            fechaAnula = now()
        Where movdocu = pcMovodocu
          and movtimo = vMovtimo
          and movtido = vMovtido
          and (estado is null or estado = '')
          and movCerrado = 'N'; -- Bosco agregado 07/05/2012.

        Set vRegistros = vRegistros + row_count();

        If vRegistros = 0 then
            Set vError = 1;
            Set vMensajeError =
                '[BD1] (Inv) (Warning) Documento no existe, ya está anulado o se encuentra en un período cerrado';
        End if;
    End if;

    If vError = 0 then
        Update bodexis, inmovimd
        Set artexis = artexis + If(inmovimd.movtimo = 'E', -inmovimd.movcant, inmovimd.movcant)
        Where inmovimd.movdocu = pcMovodocu
          and inmovimd.movtimo = pcMovtimo
          and inmovimd.movtido = pnMovtido
          and bodexis.artcode = inmovimd.artcode
          and bodexis.bodega = inmovimd.bodega;

        Set vRegistros = row_count();

        If vRegistros = 0 then
            Set vError = 1;
            Set vMensajeError = '[BD] (Inv) No se pudieron actualizar las existencias';
        End if;
    End if;

    # Si se trata de algún movimiento doble (Inter-bodega o ajuste) entonces correrá este if.
    If vError = 0 and vMovtido > 0 then
        Update bodexis, inmovimd
        Set artexis = artexis + If(inmovimd.movtimo = 'E', -inmovimd.movcant, inmovimd.movcant)
        Where inmovimd.movdocu = pcMovodocu
          and inmovimd.movtimo = vMovtimo
          and inmovimd.movtido = vMovtido
          and bodexis.artcode = inmovimd.artcode
          and bodexis.bodega = inmovimd.bodega;

        Set vRegistros = vRegistros + row_count();

        If vRegistros = 0 then
            Set vError = 1;
            Set vMensajeError = '[BD1] (Inv) No se pudieron actualizar las existencias';
        End if;
    End if;

    # Bosco comenta 10/05/2012.
    # Aquí no se hace ningún update sobre la tabla INARTICU debido a que existe un TRIGGER
    # en la tabla BODEXIS que se encarga de hacerlo. Ya está comprobado que lo hace bien.

    # Bosco agregado 25/12/2011.
    # Si el documento anulado es una entrada entonces es preciso recalcular el costo promedio.
    If vError = 0 and pcMovtimo = 'E' then
        # Inicializar la variable para el contador de registros.
        Set @Recno := 0;

        # Cargo los artículos del documento anulado para recalcular el costo promedio.
        Create temporary table tmp_anularDoc
        Select
            artcode,
            (@Recno := (@Recno + 1)) as Recno
        from inmovimd
        Inner join inmovime
            on inmovimd.movdocu = inmovime.movdocu
           and inmovimd.movtido = inmovime.movtido
           and inmovimd.movtimo = inmovime.movtimo
        Where inmovimd.movdocu = pcMovodocu
          and inmovimd.movtimo = pcMovtimo
          and inmovimd.movtido = pnMovtido
          and inmovime.estado = 'A';

        # Inicializo las variables para el recorrido.
        Set @UltReg := @Recno;
        Set @Recno := 1;

        # Recorro la tabla temporal recalculando los costos.
        While @Recno <= @UltReg Do
            Call CalcularCostosInv(
                (Select artcode from tmp_anularDoc Where Recno = @Recno)
            );
            Set @Recno := @Recno + 1;
        End While;

        # Bosco agregado 26/03/2013.
        -- Elimino la tabla temporal.
        Drop table tmp_anularDoc;
        # Fin Bosco agregado 26/03/2013.
    End if;
    # Fin Bosco agregado 25/12/2011.

    # Bosco agregado 07/05/2012.
    # Si existe una referencia en la tabla de facturas de compra entonces la elimino.
    Update cxpfacturas
    Set refinv = ''
    Where refinv = pcMovodocu;

    # No hago la revisión de los registros afectados porque podría no haber ninguno.
    # Fin Bosco agregado 07/05/2012.

    Select
        vError as vError,
        vMensajeError as vMensajeError;
END$
delimiter ;


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


drop procedure if exists ConteoSelectivo;

delimiter $
CREATE DEFINER=`root`@`localhost` PROCEDURE `ConteoSelectivo`(
    IN `pBodega` varchar(3),
    IN `pUsername` VARCHAR(50)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    # Autor: Bosco Garita 05/02/2011.
    # Objet: Preparar un conteo selectivo. Respeta cantidades digitadas previamente.
    # Devuelve: Numero de registros afectados.

    Update conteo
    Set InUseByUser = pUsername,
        cantidad = If(cantidad = 0, artexis, cantidad)
    Where bodega = pBodega;

END$
delimiter ;


drop procedure if exists EjecutarCierreMensual;

delimiter $
CREATE DEFINER=`root`@`localhost` PROCEDURE `EjecutarCierreMensual`(
	IN `pMes` tinyint(2),
	IN `pAno` SMALLINT(4),
	OUT `pError` TINYINT(1),
	OUT `pMensajeErr` VARCHAR(1000),
	IN `pEtapa` INT,
	IN `pUsuario` varchar(40)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN

    #	Autor:    Bosco Garita 23/02/2011.
    #	Descrip:  Ejecutar el cierre mensual de todos los módulos menos el de contabilidad.
    #	Este proceso copia los maestros de:
    #    	- ARTÍCULOS (hinarticu)
    #           - EXISTENCIAS (hbodexis)
    #           - CLIENTES (hinclient)
    #	        - PROVEEDORES (hinproved)
    #           - IMPUESTOS (HTARIFA_IVA)
    #   a las tablas históricas.  De esa forma se conservan los saldos y los estados
    #	       de las tablas más importantes.
    #	       Además de copiar los registros de las tablas maestras también establece el mes cerrado para
    #	       que no se puedan registrar más movimientos en ese período.

    #	Devuelve: Dos variables; una que indica si hubo error (pError) y la otra con el mensaje del error (pMensajeErr)
    #	NOTA:     1. Este SP debe correr dentro de una transacción.
    #	          2. Antes de correr el proceso debe asegurarse de que los saldos están calculados al período que se va a cerrar.

    #   Bosco modificado 31/10/2013. Cambio el campo procueco por mayor, sub_cta, sub_sub y colect
    #   en las tabla históricas de clientes y proveedores.
    #   Bosco modificado 01/07/2015. Quito el campo divisita de las tablas de proveedores.
    #   Bosco modificado 10/07/2015. Agrego el control por etapas.
    #   Bosco modificado 17/07/2018. Agrego el traslado del campo idcliente en las tablas inclient - hinclient
    #   Bosco modificado 26/06/2019. Agrego el traslado de varios campos nuevos desde inproved hacia hinproved
    #   Bosco modificaro 25/06/2020. Agrego la tabla de histórico de impuestos y el control de etapas del cierre.

    Declare vMesCerrado tinyInt;
    Declare vAnoCerrado int;
    Declare vPrimerDiaCerrado datetime;
    Declare vPrimerDiaaCerrar datetime;
    Declare vUltimoDiaMes datetime;
    Declare vRegistrosAf int; -- Bosco agregado 24/02/2013
    DECLARE vEtapaConfirmada SMALLINT;
    DECLARE vUsuario VARCHAR(40);


    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        GET DIAGNOSTICS CONDITION 1 @sqlstate = RETURNED_SQLSTATE, @errno = MYSQL_ERRNO, @text = MESSAGE_TEXT;
        SET @full_error = CONCAT("ERROR ", @errno, " (", @sqlstate, "): ", @text);
        ROLLBACK;
        SET pError = 1;
        SET pMensajeErr = CONCAT('[BD] Ocurrió un error en la etapa ', pEtapa, ' del cierre. La etapa fue revertida. ' , @full_error);
    END;

    -- Determino cuál fue la última etapa confirmada para este cierre
    SELECT etapaconfirmada FROM etapascierre WHERE mes = pMes AND ano = pAno INTO vEtapaConfirmada;


    -- Si la etapa no existe...
    if vEtapaConfirmada IS NULL then
        SET vEtapaConfirmada = 0;
    END if;

    -- Establezco el valor default para el control de errores
    Set pError = 0;
    Set pMensajeErr = '';
    Set vUsuario = IfNull(Trim(pUsuario), Trim(USER()));

    -- Las etapas van de uno a doce por ahora 10/07/2015
    If pEtapa is null or pEtapa not between 1 and 15 then
        Set pError = 1;
        Set pMensajeErr = '[BD] El número de etapas debe ir entre 1-15. Proceso cancelado.';
    End if;

    # Validar el mes y el año de cierre
    # El mes debe estar entre 1-12.  El período de cierre no puede ser inferior al que ya está registrado
    # en la tabla config.
    If not pError and (pMes is null or pMes not between 1 and 12) then
        Set pError = 1;
        Set pMensajeErr = '[BD] El mes a cerrar debe ser entre 1-12. Proceso cancelado.';
    End if;

    # Se toma el mes y año del último período cerrado y se concatenan para formar una fecha, el último día.
    # Luego se hace lo mismo con el mes y año que se intenta cerrar y se realizan las siguientes validaciones:
    # 1) La fecha de cierre no puede ser inferior a la del último cierre.
    # 2) Entre el período cerrado y el que se va a cerrar no puede haber más de 360 días.

    If not pError then
        Select IfNull(mescerrado,1), IfNull(anocerrado,1900) from config into vMesCerrado, vAnoCerrado;

        If vAnoCerrado = 1900 then
            Select vAnoCerrado = min(year(movfech)) from inmovime;
        End if;

        Set vPrimerDiaCerrado = Concat(Cast(vAnoCerrado as char(4)),'-',Cast(vMesCerrado as char(2)),'-01 00:00:00');
        Set vPrimerDiaaCerrar = Concat(Cast(pAno        as char(4)),'-',Cast(pMes        as char(2)),'-01 00:00:00');

        If not pError and vPrimerDiaaCerrar <= vPrimerDiaCerrado then
            Set pError = 1;
            Set pMensajeErr = '[BD] El mes a cerrar ya está cerrado. Proceso cancelado.';
        End if;

        If not pError && DateDiff(vPrimerDiaaCerrar,vPrimerDiaCerrado) > 360 and vAnoCerrado <> 1900 then
            Set pError = 1;
            Set pMensajeErr = '[BD] No se permite períodos de más de 360 días. Proceso cancelado.';
        End if;
    End if;

    Set vRegistrosAf = 0; -- Bosco agregado 24/02/2013

    # Verifico que todas las facturas hayan pasado a inventarios.
    If not pError then
        Call FacturacionVsInventario (vRegistrosAf);
        Set pError = If(vRegistrosAf > 0, 1, 0);

        If pError then
            Set pMensajeErr = '[BD] Hay registros de facturación que no están en inventarios. Proceso cancelado.';
        End if;
    End if;


    # ------------------ PRIMERA ETAPA ------------------
    # Bosco agregado 24/02/2013.
    # Verifico que todas las facturas tengan su respectivo detalle.
    If not pError and pEtapa = 1 and vEtapaConfirmada = 0 then
        Call EncabezadoFacturasVsDetalle (vRegistrosAf);
        Set pError = If(vRegistrosAf > 0, 1, 0);
        If pError then
            Set pMensajeErr = '[BD] Hay facturas sin detalle. Proceso cancelado.';
        End if;

        If NOT pError then
            START TRANSACTION;
            INSERT INTO etapascierre (mes, ano, etapaconfirmada, usuario)
            VALUES(pMes, pAno, vEtapaConfirmada + 1, vUsuario);
            COMMIT;
        END if;
    End if;

    # ------------------ SEGUNDA ETAPA ------------------
    # Verifico que todos los documentos de inventario tengan su respectivo detalle.
    If not pError and pEtapa = 2 and vEtapaConfirmada = 1 then
        Call EncabezadoInvVsDetalle (vRegistrosAf);
        Set pError = If(vRegistrosAf > 0, 1, 0);
        If pError then
            Set pMensajeErr = '[BD] Hay documentos de inventario sin detalle. Proceso cancelado.';
        End if;

        If NOT pError then
            START TRANSACTION;
            UPDATE etapascierre
                SET etapaconfirmada = vEtapaConfirmada + 1, usuario = vUsuario, fecha = SYSDATE()
            WHERE mes = pMes AND ano = pAno;
            COMMIT;
        END if;
    End if;

    -- Fin Bosco agregado 24/02/2013.

    # Fecha para el cierre (incluye hora :23:59:59)
    Set vUltimoDiaMes = UltimoDiaDelMes(pMes, pAno);

    # Inicia proceso de revisión de datos
    If not pError then

        # ------------------ TERCERA ETAPA ------------------
        If pEtapa = 3 and vEtapaConfirmada = 2 then
            START TRANSACTION;
            Call EliminarInconsistencias();
            UPDATE etapascierre
                    SET etapaconfirmada = vEtapaConfirmada + 1, usuario = vUsuario, fecha = SYSDATE()
            WHERE mes = pMes AND ano = pAno;
            COMMIT;
        End if;

        # ------------------ CUARTA ETAPA ------------------
        If pEtapa = 4 and vEtapaConfirmada = 3 then
            START TRANSACTION;
            # Por ahora el reservado será actual, no es a la fecha de cierre necesariamente.
            Call RecalcularReservado(null);
            UPDATE etapascierre
                    SET etapaconfirmada = vEtapaConfirmada + 1, usuario = vUsuario, fecha = SYSDATE()
            WHERE mes = pMes AND ano = pAno;
            COMMIT;
        End if;

        # ------------------ QUINTA ETAPA ------------------
        If pEtapa = 5 and vEtapaConfirmada = 4 then
            START TRANSACTION;
            # Establecer el saldo de los registros de CXC (fact, nc, nd) a la fecha de cierre.
            # Este proceso genera una tabla temporal (tmp_faencabe) con el estado a la fecha indicada.
            Call CalcularCXC(vUltimoDiaMes);
            UPDATE etapascierre
                SET etapaconfirmada = vEtapaConfirmada + 1, usuario = vUsuario, fecha = SYSDATE()
            WHERE mes = pMes AND ano = pAno;
            COMMIT;
        End if;

        # ------------------ SEXTA ETAPA ------------------
        If pEtapa = 6 and vEtapaConfirmada = 5 then
            START TRANSACTION;
            # Establecer el saldo de los clientes a la fecha de cierre.
            # Usa la tabla creada por CalcularCXC().
            Call RecalcularSaldoClientes_Cierre();
            UPDATE etapascierre
                SET etapaconfirmada = vEtapaConfirmada + 1, usuario = vUsuario, fecha = SYSDATE()
            WHERE mes = pMes AND ano = pAno;
            COMMIT;
        End if;

        # ------------------ SEPTIMA ETAPA ------------------
        If pEtapa = 7 and vEtapaConfirmada = 6 then
            START TRANSACTION;
            # Recalcular las existencias a la fecha de cierre. El segundo parámetro indica que es cierre.
            Call RecalcularExistencias(vUltimoDiaMes, 1);
            # Elimino la tabla temporal
            Drop temporary table If Exists tmp_faencabe;
            UPDATE etapascierre
                SET etapaconfirmada = vEtapaConfirmada + 1, usuario = vUsuario, fecha = SYSDATE()
            WHERE mes = pMes AND ano = pAno;
            COMMIT;
        End if;

    End if; -- # Fin de revisión de datos

    # ==========================================================================================================

    # ------------------ OCTAVA ETAPA ------------------
    # Inicia proceso de copiado a las tablas históricas
    If not pError AND pEtapa = 8 and vEtapaConfirmada = 7 then
        START TRANSACTION;

        # Guardo las existencias y el estado de los artículos de inventario
        INSERT INTO `hinarticu`(
            `artcode`, `artdesc`, `barcode`,
            `artfam`,  `artcosd`, `artcost`,
            `artcosp`, `artcosa`, `artcosfob`,
            `artpre1`, `artgan1`, `artpre2`,
            `artgan2`, `artpre3`, `artgan3`,
            `artpre4`, `artgan4`, `artpre5`,
            `artgan5`, `procode`, `artmaxi`,
            `artmini`, `artiseg`, `artdurp`,
            `artfech`, `artfeuc`, `artfeus`,
            `artexis`, `artreserv`,
            `transito`, `otroc`,  `altarot`,
            `vinternet`, `artObse`,
            `artFoto`, `artperi`, `codigoTarifa`)
        SELECT
            artcode,   artdesc,   barcode,
            artfam,    artcosd,   artcost,
            artcosp,   artcosa,   artcosfob,
            artpre1,   artgan1,   artpre2,
            artgan2,   artpre3,   artgan3,
            artpre4,   artgan4,   artpre5,
            artgan5,   procode,   artmaxi,
            artmini,   artiseg,   artdurp,
            artfech,   artfeuc,   artfeus,
            artexis,   artreserv,
            transito,  otroc,     altarot,
            vinternet, artObse,
            artFoto,   vUltimoDiaMes, codigoTarifa
        FROM inarticu;

        # Guardo las existencias por bodega
        INSERT INTO `hbodexis`(
            `bodega`,
            `artcode`,
            `artexis`,
            `artreserv`,
            `minimo`,
            `artperi`)
        Select
            bodega,
            artcode,
            artexis,
            artreserv,
            minimo,
            vUltimoDiaMes
        From bodexis;

        # Guardo los clientes, sus saldos y demás características
        INSERT INTO `hinclient`(
            `clicode`,    `clidesc`,    `clidir`,
            `clitel1`,    `clitel2`,    `clitel3`,
            `clifax`,     `cliapar`,    `clinaci`,
            `clisald`,    `cliprec`,    `clilimit`,
            `terr`,       `vend`,       `clasif`,
            `cliplaz`,    `exento`,     `clifeuc`,
            `encomienda`, `direncom`,   `facconiv`,
            `clinpag`,    `clicelu`,    `cliemail`,
            `clireor`,    `igsitcred`,  `credcerrado`,
            `diatramite`, `horatramite`,`diapago`,
            `horapago`,   `clicueba`,   `cligenerico`,
            `cliperi`,    `mayor`,      `sub_cta`,
            `sub_sub`,    `colect`,     `idcliente`,
            `idtipo`)
        Select
            clicode,    clidesc,    clidir,
            clitel1,    clitel2,    clitel3,
            clifax,     cliapar,    clinaci,
            clisald,    cliprec,    clilimit,
            terr,       vend,       clasif,
            cliplaz,    exento,     clifeuc,
            encomienda, direncom,   facconiv,
            clinpag,    clicelu,    cliemail,
            clireor,    igsitcred,  credcerrado,
            diatramite, horatramite,diapago,
            horapago,   clicueba,   cligenerico,
            vUltimoDiaMes, mayor,  sub_cta,
            sub_sub,    colect,     idcliente,
            idtipo
        From inclient;

        # Guardo los datos de los proveedores
        INSERT INTO `hinproved`(
            `procode`,
            `prodesc`,
            `prodir`,
            `protel1`,
            `protel2`,
            `profax`,
            `proapar`,
            `pronac`,
            `profeuc`,
            `promouc`,
            `prosald`,
            `proplaz`,
            `procueba`,
            `mayor`,
            `sub_cta`,
            `sub_sub`,
            `colect`,
            `email`,
            `idProv`,
            `idTipo`,
            `provincia`,
            `canton`,
            `distrito`,
            `properi`)
        Select
            procode,
            prodesc,
            prodir,
            protel1,
            protel2,
            profax,
            proapar,
            pronac,
            profeuc,
            promouc,
            prosald,
            proplaz,
            procueba,
            mayor,
            sub_cta,
            sub_sub,
            colect,
            email,
            idProv,
            idTipo,
            provincia,
            canton,
            distrito,
            vUltimoDiaMes
        From inproved;

        -- Histórico de tarifas según el Ministerio de Hacienda
        INSERT INTO htarifa_iva (codigoTarifa, descrip, porcentaje, periodo)
        SELECT codigoTarifa, descrip, porcentaje, vUltimoDiaMes
        FROM tarifa_iva;

        UPDATE etapascierre
            SET etapaconfirmada = vEtapaConfirmada + 1, usuario = vUsuario, fecha = NOW()
        WHERE mes = pMes AND ano = pAno;
        COMMIT;
    End if; -- If not pError AND pEtapa = 8

    # ==========================================================================================================
    # Finaliza proceso de copiado a las tablas históricas

    # Inicia proceso de marcar los registros como cerrados y cálculo de saldos
    If not pError then

        # ------------------ NOVENA ETAPA ------------------
        If pEtapa = 9 and vEtapaConfirmada = 8 then
            START TRANSACTION;
            # Marcar las facturas, NC y ND con saldo cero como cerradas.
            # Estos registros tienen el saldo actual, no el de la fecha de cierre necesariamente.
            Update faencabe
            Set facCerrado = 'S'
            Where facsald = 0 and facCerrado = 'N' and facfech <= vUltimoDiaMes;

            UPDATE etapascierre
                SET etapaconfirmada = vEtapaConfirmada + 1, usuario = vUsuario, fecha = SYSDATE()
            WHERE mes = pMes AND ano = pAno;
            COMMIT;
        End if;

        # ------------------ DÉCIMA ETAPA ------------------
        If pEtapa = 10 and vEtapaConfirmada = 9 then
            START TRANSACTION;
            # Marcar todos los registros de CXC en inventarios como cerrados (facturas).
            Update inmovime
            Inner join intiposdoc on intiposdoc.Movtido = inmovime.Movtido
                Set movCerrado = 'S'
            Where movCerrado = 'N'
            and movfech <= vUltimoDiaMes and intiposdoc.Modulo = 'CXC'
            and Exists(
                Select facnume from faencabe
                Where cast(facnume AS char(10)) = inmovime.movdocu
                and facnd = 0 -- Facturas
                and facCerrado = 'S'
                and facfech <= vUltimoDiaMes
            );

            # Marcar todos los registros de CXC en inventarios como cerrados (NC).
            Update inmovime
            Inner join intiposdoc on intiposdoc.Movtido = inmovime.Movtido
                Set movCerrado = 'S'
            Where movCerrado = 'N'
            and movfech <= vUltimoDiaMes and intiposdoc.Modulo = 'CXC'
            and Exists(
                Select facnume from faencabe
                Where facnume < 0
                and facnd > 0 -- Notas de crédito
                and facCerrado = 'S'
                and Cast(Abs(facnume) as char(10)) = inmovime.movdocu
                and facfech <= vUltimoDiaMes
            );

            UPDATE etapascierre
                SET etapaconfirmada = vEtapaConfirmada + 1, usuario = vUsuario, fecha = SYSDATE()
            WHERE mes = pMes AND ano = pAno;
            COMMIT;
        End if;

        # ------------------ ONCEAVA ETAPA ------------------
        If pEtapa = 11 and vEtapaConfirmada = 10 then
            START TRANSACTION;
            # Marcar todos los movimientos de inventarios como cerrados.
            Update inmovime
                Inner join intiposdoc on intiposdoc.Movtido = inmovime.Movtido
                Set movCerrado = 'S'
            Where movCerrado = 'N'
            and intiposdoc.Modulo = 'INV'
            and movfech <= vUltimoDiaMes;

            UPDATE etapascierre
                SET etapaconfirmada = vEtapaConfirmada + 1, usuario = vUsuario, fecha = SYSDATE()
            WHERE mes = pMes AND ano = pAno;
            COMMIT;
        End if;

        # ------------------ DOCEAVA ETAPA ------------------
        If pEtapa = 12 and vEtapaConfirmada = 11 then
            START TRANSACTION;
            # Marcar los recibos de CXC como cerrados.
            Update pagos
            Set cerrado = 'S'
            Where cerrado = 'N' and fecha <= vUltimoDiaMes;

            -- Bosco agregado 14/03/2013
            # Marcar los recibos de CXP como cerrados.
            Update cxppage
            Set cerrado = 'S'
            Where cerrado = 'N' and fecha <= vUltimoDiaMes;

            # Marcar las facturas, NC y ND con saldo cero como cerradas.
            Update cxpfacturas
            Set Cerrado = 'S'
            Where Cerrado = 'N' and fecha_fac <= vUltimoDiaMes and saldo = 0;
            -- Fin Bosco agregado 14/03/2013

            UPDATE etapascierre
                SET etapaconfirmada = vEtapaConfirmada + 1, usuario = vUsuario, fecha = SYSDATE()
            WHERE mes = pMes AND ano = pAno;
            COMMIT;
        End if; -- if pEtapa = 12

        # ------------------ TRECEAVA ETAPA ------------------
        If pEtapa = 13 and vEtapaConfirmada = 12 then
            START TRANSACTION;
            # Recalcular el saldo de las facturas. No es indispensable pero preferible.
            Call RecalcularSaldoFacturas();

            UPDATE etapascierre
                    SET etapaconfirmada = vEtapaConfirmada + 1, usuario = vUsuario, fecha = SYSDATE()
            WHERE mes = pMes AND ano = pAno;
            COMMIT;
        End if;

        # ------------------ CATORCEAVA ETAPA ------------------
        If pEtapa = 14 and vEtapaConfirmada = 13 then
            START TRANSACTION;
            # Recalcular el saldo de todos los clientes. Este proceso SI es indispensable.
            Call RecalcularSaldoClientes(null); -- El saldo de los clientes depende del saldo de las facturas.

            UPDATE etapascierre
                    SET etapaconfirmada = vEtapaConfirmada + 1, usuario = vUsuario, fecha = SYSDATE()
            WHERE mes = pMes AND ano = pAno;
            COMMIT;
        End if;

        # ------------------ QUINCEAVA ETAPA ------------------
        If pEtapa = 15 and vEtapaConfirmada = 14 then
            START TRANSACTION;
            # Recalcular las existencias a hoy.
            Call RecalcularExistencias(now(), 1);

            # Establecer la fecha de cierre en bodegas.
            Update bodegas Set cerrada = vUltimoDiaMes Where cerrada is null or cerrada < vUltimoDiaMes;

            # Establecer el período cerrado en la tabla config.
            Update config Set mescerrado = pMes, anocerrado = pAno, cierre = now();

            UPDATE etapascierre
                    SET etapaconfirmada = vEtapaConfirmada + 1, usuario = vUsuario, fecha = SYSDATE()
            WHERE mes = pMes AND ano = pAno;
            COMMIT;
        End if;

    End if; -- If not pError

    -- Si hubo error se revierte la etapa
    If pError then
        ROLLBACK;
    END if;
END$
delimiter ;


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


drop procedure if exists InsertarEncabezadoDocInv;
delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarEncabezadoDocInv`(
	IN `pMovdocu` varchar(10),
	IN `pMovtimo` char(20),
	IN `pMovorco` varchar(10),
	IN `pMovdesc` varchar(150),
	IN `pMovfech` date,
	IN `pTipoca` float,
	IN `pMovtido` smallint(3),
	IN `pMovsolic` varchar(30),
	IN `pCodigoTC` varchar(3),
	IN `pUsername` VARCHAR(50)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    Declare vContinuar tinyint(1);

    Set vContinuar = 1;

    If (PermitirFecha(pMovfech) = 0) then
        Set vContinuar = 0;
End if;

    If (vContinuar = 1 and ConsultarDocumento(pMovdocu, pMovtimo, pMovtido) = 1) then
        Set vContinuar = 0;
End if;

    If vContinuar = 1 then
        Insert into inmovime (
            movdocu,
            movtimo,
            movorco,
            Movdesc,
            movfech,
            tipoca,
            user,
            movtido,
            movsolic,
            movfechac,
            codigoTC
        )
        Values (
            pMovdocu,
            pMovtimo,
            pmovorco,
            pMovdesc,
            pMovfech,
            pTipoca,
            pUsername,
            pMovtido,
            pMovsolic,
            now(),
            pCodigoTC
        );
End if;
END$$
delimiter ;


drop procedure if exists InsertarEncabezadoFactura;

delimiter $
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarEncabezadoFactura`(
    IN `pFacnume` int(10),
    IN `pClicode` int(10),
    IN `pVend` tinyint(3),
    IN `pTerr` tinyint(3),
    IN `pFacfech` datetime,
    IN `pFacplazo` tinyint(3),
    IN `pPrecio` tinyint(3),
    IN `pUsuario` varchar(40)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    -- Autor: Bosco Garita Azofeifa
    Declare vCodigoTC char(3);
    Declare vTipoca float;
    Declare vFacfepa datetime;

    Set pVend = IfNull(pVend, (Select vend from inclient where clicode = pClicode));
    Set pTerr = IfNull(pTerr, (Select terr from inclient where clicode = pClicode));
    Set pFacfech = IfNull(pFacfech, now());
    Set pPrecio = IfNull(pPrecio, (Select cliprec from inclient where clicode = pClicode));
    Set pFacplazo = IfNull(pFacplazo, (Select cliplaz from inclient where clicode = pClicode));
    Set pUsuario = Trim(IfNull(pUsuario, ''));

    Set vFacfepa = AddDate(pFacfech, interval pFacplazo day);
    Set vCodigoTC = (Select CodigoTC from config);
    Set vTipoca = ConsultarTipoca(vCodigoTC, pFacfech);

    If vTipoca is null then
        Set vTipoca = 1;
    End if;

    Insert into wrk_faencabe (
        facnume,
        clicode,
        vend,
        terr,
        facfech,
        facplazo,
        precio,
        user,
        codigoTC,
        tipoca,
        facfepa,
        facfechaC,
        facestado
    )
    Values (
        pFacnume,
        pClicode,
        pVend,
        pTerr,
        pFacfech,
        pFacplazo,
        pPrecio,
        pUsuario,
        vCodigoTC,
        vTipoca,
        vFacfepa,
        now(),
        ''
    );
END$
delimiter ;


drop procedure if exists `InsertarEncabezadoNC_CXC`;

delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarEncabezadoNC_CXC`(
    IN `pFacnume` int(10),
    IN `pClicode` int(10),
    IN `pVend` tinyint(3),
    IN `pTerr` tinyint(3),
    IN `pFacfech` datetime,
    IN `pFacplazo` tinyint(3),
    IN `pPrecio` tinyint(3),
    IN `pUsuario` varchar(40)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    Declare vCodigoTC char(3);
    Declare vTipoca float;
    Declare vFacfepa datetime;

    If pFacnume > 0 then
        Set pFacnume = (pFacnume * -1);
    End if;

    Set pVend = IfNull(pVend, (Select vend from inclient where clicode = pClicode));
    Set pTerr = IfNull(pTerr, (Select terr from inclient where clicode = pClicode));
    Set pFacfech = IfNull(pFacfech, now());
    Set pPrecio = IfNull(pPrecio, (Select cliprec from inclient where clicode = pClicode));
    Set pFacplazo = IfNull(pFacplazo, (Select cliplaz from inclient where clicode = pClicode));
    Set pUsuario = Trim(IfNull(pUsuario, ''));

    Set vFacfepa = AddDate(pFacfech, interval pFacplazo day);
    Set vCodigoTC = (Select CodigoTC from config);
    Set vTipoca = ConsultarTipoca(vCodigoTC, curdate());

    Insert into wrk_faencabe (
        facnume,
        clicode,
        vend,
        terr,
        facfech,
        facplazo,
        precio,
        facestado,
        user,
        codigoTC,
        tipoca,
        facfepa,
        facfechaC,
        facnd
    )
    Values (
        pFacnume,
        pClicode,
        pVend,
        pTerr,
        pFacfech,
        pFacplazo,
        pPrecio,
        ' ',
        pUsuario,
        vCodigoTC,
        vTipoca,
        vFacfepa,
        now(),
        pFacnume * -1
    );
END$$
delimiter ;



drop procedure if exists InsertarEncabezadoOrdenC;

delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarEncabezadoOrdenC`(
    IN `pMovorco` varchar(10),
    IN `pMovdesc` varchar(150),
    IN `pMovfech` date,
    IN `pTipoca` float,
    IN `pMovtido` smallint(3),
    IN `pCodigoTC` varchar(3),
    IN `pProcode` varchar(15),
    IN `pUsuario` varchar(40)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    Declare vContinuar tinyint(1);

    Set vContinuar = 1;
    Set pUsuario = Trim(IfNull(pUsuario, ''));

    If PermitirFecha(pMovfech) = 0 then
        Set vContinuar = 0;
    End if;

    -- Validar si el documento ya existe.
    If (Select count(movorco) from comOrdenCompraE where movorco = pMovorco) > 0 then
        Set vContinuar = 0;
    End if;

    If vContinuar = 1 then
        Insert into comOrdenCompraE (
            movorco,
            Movdesc,
            movfech,
            tipoca,
            user,
            movtido,
            movfechac,
            codigoTC,
            procode
        )
        Values (
            pMovorco,
            pMovdesc,
            pMovfech,
            pTipoca,
            pUsuario,
            pMovtido,
            now(),
            pCodigoTC,
            pProcode
        );
    End if;
END$$
delimiter ;


drop procedure if exists InsertarEncabezadoPedido;

delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarEncabezadoPedido`(
    IN `pFacnume` int(10),
    IN `pClicode` int(10),
    IN `pUsuario` varchar(40)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    Declare vVend tinyint(3);
    Declare vTerr tinyint(3);
    Declare vFacfech datetime;
    Declare vFacplazo tinyint(3);
    Declare vPrecio tinyint(3);
    Declare vFacivi tinyint(1);

    Set vVend = (Select vend from inclient where clicode = pClicode);
    Set vTerr = (Select terr from inclient where clicode = pClicode);
    Set vFacfech = now();
    Set vPrecio = (Select cliprec from inclient where clicode = pClicode);
    Set vFacplazo = (Select cliplaz from inclient where clicode = pClicode);
    Set pUsuario = Trim(IfNull(pUsuario, ''));
    Set vFacivi = (Select usarivi from config);

    Insert into pedidoe (
        facnume,
        clicode,
        vend,
        terr,
        facfech,
        facplazo,
        precio,
        user,
        facivi
    )
    Values (
        pFacnume,
        pClicode,
        vVend,
        vTerr,
        vFacfech,
        vFacplazo,
        vPrecio,
        pUsuario,
        vFacivi
    );
END$$
delimiter ;


drop procedure if exists InsertarNDCXC;

delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarNDCXC`(
    IN `pfacnume` int(10),
    IN `pclicode` int(10),
    IN `pfacfech` datetime,
    IN `pfacmont` double,
    IN `pReferencia` varchar(10),
    IN `pcodigoTC` char(3),
    IN `ptipoca` float,
    IN `pOrdenc` varchar(10),
    IN `pUsuario` varchar(40)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    -- Autor: Bosco Garita Azofeifa
    Declare vHayError tinyint(1);
    Declare vMensaje varchar(200);
    Declare vVend tinyint(3);
    Declare vTerr tinyint(3);
    Declare vBodega char(3);

    Set vHayError = 0;
    Set vMensaje = '';
    Set pUsuario = Trim(IfNull(pUsuario, ''));

    If Exists (Select facnume from faencabe where facnume = pFacnume and facnd = (pFacnume * -1)) then
        Set vHayError = 1;
        Set vMensaje = '[BD] Nota de debito ya existe.';
    End if;

    If not Exists (Select artcode from inarticu where artcode = '_NOINV') then
        Set vHayError = 1;
        Set vMensaje = '[BD] El articulo _NOINV debe estar creado.';
    End if;

    If vHayError = 0 then
        Set vVend = (Select vend from inclient where clicode = pClicode);
        Set vTerr = (Select terr from inclient where clicode = pClicode);
        Set vBodega = (Select bodega from config);

        Insert into faencabe (
            facnume,
            clicode,
            vend,
            terr,
            facfech,
            facplazo,
            facmont,
            facfepa,
            facsald,
            facnd,
            user,
            referencia,
            precio,
            facfechac,
            codigoTC,
            tipoca,
            ordenc
        )
        Values (
            pFacnume,
            pClicode,
            vVend,
            vTerr,
            pFacfech,
            1,
            pFacmont,
            pFacfech,
            pFacmont,
            (pFacnume * -1),
            pUsuario,
            pReferencia,
            1,
            now(),
            pCodigoTC,
            pTipoca,
            pOrdenc
        );

        If Row_Count() <= 0 then
            Set vHayError = 1;
            Set vMensaje = '[BD] No se pudo guardar el encabezado de la ND';
        End if;

        If vHayError = 0 then
            Insert into fadetall (
                facnume,
                artcode,
                bodega,
                faccant,
                artprec,
                facmont,
                facnd
            )
            Values (
                pFacnume,
                '_NOINV',
                vBodega,
                1,
                pFacmont,
                pFacmont,
                pFacnume * -1
            );

            If Row_Count() <= 0 then
                Set vHayError = 1;
                Set vMensaje = '[BD] No se pudo guardar el detalle de la ND';
            End if;
        End if;
    End if;

    Select vHayError as vHayError, vMensaje as vMensaje;
END$$
delimiter ;


drop procedure if exists InsertarPagoCXC;
delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarPagoCXC`(
    IN `pRecnume` int(10),
    IN `pClicode` int(10),
    IN `pFecha` datetime,
    IN `pConcepto` varchar(80),
    IN `pMonto` double,
    IN `pCheque` varchar(12),
    IN `pBanco` varchar(45),
    IN `pCodigoTC` varchar(3),
    IN `pTipoca` float,
    IN `pUsuario` varchar(40)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    Declare vHayError tinyint(1);
    Declare vMensaje varchar(200);

    Set vHayError = 0;
    Set vMensaje = '';
    Set pUsuario = Trim(IfNull(pUsuario, ''));

    If Exists (Select recnume from pagos where recnume = pRecnume) then
        Set vHayError = 1;
        Set vMensaje = '[BD] Recibo ya existe.';
    End if;

    If vHayError = 0 then
        Insert into pagos (
            recnume,
            clicode,
            fecha,
            concepto,
            monto,
            estado,
            user,
            cheque,
            banco,
            fechaC,
            codigoTC,
            tipoca
        )
        Values (
            pRecnume,
            pClicode,
            pFecha,
            pConcepto,
            pMonto,
            ' ',
            pUsuario,
            pCheque,
            pBanco,
            now(),
            pCodigoTC,
            pTipoca
        );

        If Row_Count() <= 0 then
            Set vHayError = 1;
            Set vMensaje = '[BD] No se pudo guardar el encabezado del recibo';
        End if;
    End if;

    Select vHayError as vHayError, vMensaje as vMensaje;
END$$
delimiter ;


drop procedure if exists InsertarPagoCXP;
delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarPagoCXP`(
    IN `pRecnume` int(10),
    IN `pProcode` varchar(15),
    IN `pFecha` datetime,
    IN `pConcepto` varchar(80),
    IN `pMonto` double,
    IN `pCheque` varchar(12),
    IN `pCodigoTC` varchar(3),
    IN `pTipoca` float,
    IN `pUsuario` varchar(40)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    # Autor Bosco Garita 28/04/2012.
    # Objetivo: Insertar el encabezado de un recibo de cuentas por pagar.
    # Este SP siempre devolvera un RS con el resultado:
    # Select vHayError as vHayError, vMensaje as vMensaje.

    Declare vHayError tinyint(1);
    Declare vMensaje varchar(200);

    Set vHayError = 0;
    Set vMensaje = '';
    Set pUsuario = Trim(IfNull(pUsuario, ''));

    If Exists (Select recnume from cxppage where recnume = pRecnume) then
        Set vHayError = 1;
        Set vMensaje = '[BD] Recibo ya existe.';
    End if;

    If vHayError = 0 then
        Insert into cxppage (
            recnume,
            procode,
            fecha,
            concepto,
            monto,
            estado,
            user,
            cheque,
            fechaC,
            codigoTC,
            tipoca
        )
        Values (
            pRecnume,
            pProcode,
            pFecha,
            pConcepto,
            pMonto,
            ' ',
            pUsuario,
            pCheque,
            now(),
            pCodigoTC,
            pTipoca
        );

        If Row_Count() <= 0 then
            Set vHayError = 1;
            Set vMensaje = '[BD] No se pudo guardar el encabezado del recibo';
        End if;
    End if;

    Select vHayError as vHayError, vMensaje as vMensaje;
END$$
delimiter ;


drop procedure if exists PrepararConteo;
delimiter $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `PrepararConteo`(
    IN `pBodega` varchar(3),
    IN `pPordesc` tinyint(1),
    IN `pRegenerar` tinyint(1),
    IN `pValorar` tinyint(1),
    IN `pLocaliz1` varchar(7),
    IN `pLocaliz2` varchar(7),
    IN `pUsuario` varchar(40)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    # Autor: Bosco Garita 22/01/2011.
    # Objet: Generar la tabla de conteo con las existencias actuales.
    # Devuelve: Un result set con el numero de registros generados.
    # Modificador por: Bosco Garita 20/12/2015.
    # Se agregaron pLocaliz1 y pLocaliz2 para permitir inventarios por localizacion.

    Set pPordesc = IfNull(pPordesc, 0);
    Set pRegenerar = IfNull(pRegenerar, 0); -- 0=Actualiza los datos, 1=Los sobreescribe
    Set pUsuario = Trim(IfNull(pUsuario, ''));

    -- 0=Costo promedio, 1=Precio1, 2=Precio2, 3=Precio3, 4=Precio4, 5=Precio5
    If pValorar is null or pValorar not between 0 and 5 then
        Set pValorar = 0;
    End if;

    Set pLocaliz1 = IfNull(pLocaliz1, '');

    If pLocaliz2 is null or pLocaliz1 = '' then
        Select max(localiz)
        From bodexis
        Where bodega = pBodega
        Into pLocaliz2;
    End if;

    If pRegenerar = 1 then -- Generar conteo nuevo
        Delete from conteo
        Where bodega = pBodega;

        Insert into conteo (
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
            a.bodega,
            a.artcode,
            0,
            a.artexis,
            Case pValorar
                When 0 then b.artcosp
                When 1 then b.artpre1
                When 2 then b.artpre2
                When 3 then b.artpre3
                When 4 then b.artpre4
                Else b.artpre5
            End,
            now(),
            pUsuario,
            '',
            '',
            pPordesc
        From bodexis a
        Inner join inarticu b on a.artcode = b.artcode
        Where a.bodega = pBodega
          and a.localiz between pLocaliz1 and pLocaliz2;
    Else -- Actualizar el conteo existente
        Update conteo, bodexis, inarticu b
        Set conteo.artexis = bodexis.artexis,
            conteo.artcosp = Case pValorar
                When 0 then b.artcosp
                When 1 then b.artpre1
                When 2 then b.artpre2
                When 3 then b.artpre3
                When 4 then b.artpre4
                Else b.artpre5
            End
        Where conteo.bodega = pBodega
          and conteo.bodega = bodexis.bodega
          and conteo.artcode = bodexis.artcode
          and conteo.artcode = b.artcode;

        # Agregar los registros que no existen
        Insert into conteo (
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
            a.bodega,
            a.artcode,
            0,
            a.artexis,
            Case pValorar
                When 0 then b.artcosp
                When 1 then b.artpre1
                When 2 then b.artpre2
                When 3 then b.artpre3
                When 4 then b.artpre4
                Else b.artpre5
            End,
            now(),
            pUsuario,
            '',
            '',
            pPordesc
        From bodexis a
        Inner join inarticu b on a.artcode = b.artcode
        Where a.bodega = pBodega
          and a.localiz between pLocaliz1 and pLocaliz2
          and not Exists (
              Select c.artcode
              From conteo c
              Where c.bodega = a.bodega
                and c.artcode = a.artcode
          );

        # Actualizar el campo de orden y el usuario
        Update conteo
        Set pordesc = pPordesc,
            userDigita = pUsuario
        Where bodega = pBodega;
    End if;

    # Contar los registros generados
    Select count(*) as registros
    From conteo
    Where bodega = pBodega;
END$$
delimiter ;


