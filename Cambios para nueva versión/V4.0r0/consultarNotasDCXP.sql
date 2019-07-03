Use LaFlor;
Drop procedure consultarNotasDCXP;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `consultarNotasDCXP`(
  IN `pFactura` varchar(10)
)
BEGIN
     -- Autor: Bosco Garita Azofeifa
     
  
     Select
           cxpfacturas.factura,
           inproved.prodesc,
           Dtoc(cxpfacturas.fecha_fac) as fecha,
           ABS(cxpfacturas.saldo) as saldo,
           cxpfacturas.codigoTC,
           Trim(monedas.descrip) as Moneda,
           cxpfacturas.tipoca,
           cxpfacturas.fecha_fac,       
           ABS(cxpfacturas.saldo) * cxpfacturas.tipoca as SaldoML,
           cxpfacturas.procode
     from cxpfacturas
     Inner Join monedas on cxpfacturas.codigoTC = monedas.codigo
     Inner join inproved on cxpfacturas.procode = inproved.procode
     where cxpfacturas.factura = If(pFactura is null, cxpfacturas.factura, pFactura)
     and tipo = 'NDB'     
     and saldo < 0
     Order by fecha_fac,factura;

END$$
DELIMITER ;
