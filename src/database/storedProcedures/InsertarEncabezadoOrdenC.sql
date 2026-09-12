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