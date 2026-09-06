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