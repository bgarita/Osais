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