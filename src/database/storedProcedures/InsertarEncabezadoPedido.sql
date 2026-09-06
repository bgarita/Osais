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