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