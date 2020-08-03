/*
SELECT * FROM hinproved
WHERE YEAR(properi) = 2018
AND MONTH(properi) = 10;

Delete FROM hinproved
WHERE YEAR(properi) = 2018
AND MONTH(properi) = 10;

TRUNCATE TABLE htarifa_iva;


SELECT * FROM htarifa_iva;

*/


SET @pMes = 11;
SET @pAno = 2018;
SET @pError = 0;
SET @pMensajeErr = '';
SET @pEtapa = 8;

CALL `EjecutarCierreMensual`(
	@pMes,
	@pAno,
	@pErro,
	@pMensajeErr,
	@pEtapa
);

SELECT @pError, @pMensajeErr; 

