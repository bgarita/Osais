use LaFlor;
Drop PROCEDURE `InsertarTipoca`;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarTipoca`(
  IN  `pCodigo`  varchar(3),
  IN  `pFecha`   datetime,
  IN  `pTipoca`  float
)
BEGIN
	-- Autor: Bosco Garita Azofeifa
	Declare vnConsecutivo int;
  
	If (ConsultarTipoca(pCodigo, pFecha) is null) then
		Set vnConsecutivo = (Select max(nConsecutivo) from tipocambio) + 1; 
		Set vnConsecutivo = IfNull(vnConsecutivo,1);
		Insert into tipocambio (codigo, fecha, tipoca, nConsecutivo)
		values(pCodigo, pFecha, pTipoca, vnConsecutivo);
	end if;
END$$
DELIMITER ;

