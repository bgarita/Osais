use LaFlor;
Drop procedure ConsultarFacturasProveedor;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `ConsultarFacturasProveedor`(
  IN  `pProcode`   varchar(15),
  IN  `pConSaldo`  tinyint(1)
)
BEGIN
    -- Autor: Bosco Garita Azofeifa
    
    
    
    Select
        factura,
        dtoc(fecha_fac) as fecha,
        vence_en,
        total_fac * tipoca as MontoML, 
        saldo,
        codigoTC,
        Trim(descrip) as Moneda,
        tipoca,
        tipo as TipoDoc,
        fecha_fac,       
        saldo * tipoca as SaldoML    
    from cxpfacturas
    Inner Join monedas on cxpfacturas.codigoTC = monedas.codigo
    where procode = pProcode
    and tipo in('FAC','NCR')    
    and If(pConSaldo, saldo > 0, saldo = saldo)
    Order by fecha_fac,factura;
END$$
DELIMITER ;
