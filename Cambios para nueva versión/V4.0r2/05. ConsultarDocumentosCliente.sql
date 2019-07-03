USE `sai`;
DROP procedure IF EXISTS `ConsultarDocumentosCliente`;

DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `ConsultarDocumentosCliente`(
  IN `pClicode` int(10)
)
BEGIN
    -- Autor: Bosco Garita Azofeifa
    
    Select
        facnume,
        dtoc(facfech) as fecha,
        facplazo,
        facmont * tipoca as MontoML,            
        facsald,
        codigoTC,
        Trim(descrip) as Moneda,
        tipoca,
        If(facnd = 0, 'FC',If(facnd > 0,'NC','ND')) as TipoDoc,
        facfech,       
        facsald * tipoca as SaldoML             
    from faencabe
    Inner Join monedas on faencabe.codigoTC = monedas.codigo
    where clicode = pClicode
    and facestado = '' 
    Order by facfech,facnume;

END$$

DELIMITER ;