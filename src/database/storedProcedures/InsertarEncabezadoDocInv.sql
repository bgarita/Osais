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