USE `sai`;
DROP procedure IF EXISTS `ConsultarFacturasCliente`;

DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `ConsultarFacturasCliente`(
  IN  `pClicode`   int(10),
  IN  `pConSaldo`  tinyint(1)
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
        If(facnd = 0, 'FC','ND') as TipoDoc,    
        facfech,       
        facsald * tipoca as SaldoML             
    from faencabe
    Inner Join monedas on faencabe.codigoTC = monedas.codigo
    where clicode = pClicode
    and facnd <= 0     
    and If(pConSaldo, facsald > 0, facsald = facsald)
    and facestado = '' 
    Order by facfech,facnume;

END$$

DELIMITER ;