Use LaFlor;
Drop PROCEDURE `InsertarPagoCXC`;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarPagoCXC`(
  IN  `pRecnume`   int(10),
  IN  `pClicode`   int(10),
  IN  `pFecha`     datetime,
  IN  `pConcepto`  varchar(80),
  IN  `pMonto`     double,
  IN  `pCheque`    varchar(12),
  IN  `pBanco`     varchar(45),
  IN  `pCodigoTC`  varchar(3),
  IN  `pTipoca`    float
)
BEGIN
  

  Declare vHayError tinyInt(1);
  Declare vMensaje  varchar(200);

  Set vHayError = 0;
  Set vMensaje  = '';

  
  If Exists(Select recnume from pagos Where recnume = pRecnume) then
     Set vHayError = 1;
     Set vMensaje  = '[BD] Recibo ya existe.';
  End if;

  If vHayError = 0 then

     INSERT INTO pagos (
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
        tipoca)
    VALUES (
        pRecnume,
        pClicode,
        pFecha,
        pConcepto,
        pMonto,
        ' ',        
        Trim(user()),
        pCheque,
        pBanco,
        Now(),
        pCodigoTC,
        pTipoca);

    If Row_Count() <= 0 then
       Set vHayError = 1;
       Set vMensaje  = '[BD] No se pudo guardar el encabezado del recibo';
    End if;

  end if;

  Select vHayError as vHayError, vMensaje as vMensaje;
END$$
DELIMITER ;
