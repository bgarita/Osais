DROP PROCEDURE if EXISTS CalcularCXC;

Delimiter $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `CalcularCXC`(
	IN `pFecha` datetime
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    -- Autor: Bosco Garita Azofeifa

    Declare vMonto double;
    

    Drop temporary table If Exists tmp_faencabe;

    CREATE TEMPORARY TABLE tmp_faencabe
        SELECT
            `faencabe`.`facnume`,
            `faencabe`.`clicode`,
            `faencabe`.`facfech`,
            `faencabe`.`facplazo`,
            `faencabe`.`facimve`,
            `faencabe`.`facdesc`,
            `faencabe`.`facmont`,
            `faencabe`.`facpago`,
            `faencabe`.`facsald`,
            `faencabe`.`facestado`,
            `faencabe`.`facnd`,
            `faencabe`.`user`,
            `faencabe`.`facfechac`,
            `faencabe`.`codigoTC`,
            `faencabe`.`tipoca`,
            `faencabe`.`facCerrado`
        FROM faencabe
        WHERE facfech <= pFecha AND facplazo > 0 AND facestado = '';
        
    Update tmp_faencabe Set Facsald = Facmont, facpago = 0;

    Update tmp_faencabe 
    Set facpago = AplicadoAFactura_Hasta(facnume,facnd,pFecha), facsald = facmont - facpago
    Where facnume > 0;

    Update tmp_faencabe 
    Set facpago = AplicadoANotaC_Hasta(facnume,facnd,pFecha), facsald = facmont + facpago
    Where facnume < 0;

    Delete from tmp_faencabe Where abs(facsald) = 0;

END$$

Delimiter ;