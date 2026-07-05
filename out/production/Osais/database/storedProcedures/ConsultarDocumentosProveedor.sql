drop procedure if exists `ConsultarDocumentosProveedor`;

delimiter $
CREATE DEFINER=`root`@`localhost` PROCEDURE `ConsultarDocumentosProveedor`(
	IN `pProcode` varchar(15)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
	# Autor: Bosco Garita 20/04/2014
	# Consultar todas FAC, NC y ND (no nulas) de un proveedor.


    Select
        factura,
        dtoc(fecha_fac) as fecha,
        vence_en,
        total_fac * tipoca as MontoML, -- Monto moneda local
        saldo,
        codigoTC,
        Trim(descrip) as Moneda,
        tipoca,
        tipo as TipoDoc,
        fecha_fac,       
        saldo * tipoca as SaldoML    -- Saldo moneda local
    from cxpfacturas
    Inner Join monedas on cxpfacturas.codigoTC = monedas.codigo
    where procode = pProcode
    Order by fecha_fac,factura;
END$
delimiter ;