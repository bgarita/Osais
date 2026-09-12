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