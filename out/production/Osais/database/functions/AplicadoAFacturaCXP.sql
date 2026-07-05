Drop function if exists `AplicadoAFacturaCXP`;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` FUNCTION `AplicadoAFacturaCXP`(`pFactura`  varchar(10),
  `pTipo`     varchar(3),
  `pProcode`  varchar(15)
) RETURNS double
BEGIN
    # Autor:    Bosco Garita 05/05/2012
    # Objet:    Obtener el monto aplicado a una factura/NC (Cuentas por pagar)
    #           El resultado siempre ser_ en moneda local.

    Declare vRecibos Double;
    Declare vNotasD  Double;

    # Sumar el monto de los recibos
    Set vRecibos = 
        IfNull((Select sum(cxppagd.monto * cxppage.tipoca) 
                from cxppagd, cxppage
                Where cxppagd.factura = pFactura and cxppagd.tipo = pTipo 
                and cxppagd.recnume = cxppage.recnume
                and cxppage.estado = ''),0);

    # Sumar el monto de las notas de débito
	/* 
	Bosco modificado 28/04/2013. 
	Utilizo el campo procode para identificar los registros.  Esto se debe a que
	en la tabla facturas la llave está compuesta por factura, tipo, procode.

    Set vNotasD = 
        IfNull((Select sum(cxpnotasd.monto * cxpfacturas.tipoca) 
                from cxpnotasd, cxpfacturas
                Where cxpnotasd.factura = pFactura and cxpnotasd.tipo = pTipo 
                and cxpnotasd.notanume = cxpfacturas.factura
                and cxpnotasd.NotaTipo = cxpfacturas.tipo),0);
	*/

	# Está pendiente localizar el tipo de cambio de la Nota de débito en la tabla facturas 28/04/2013
	# y pasar esta función a Windows
	/*Set vNotasD = 
        IfNull((Select sum(cxpnotasd.monto * cxpfacturas.tipoca) 
                from cxpnotasd, cxpfacturas
                Where cxpnotasd.factura = pFactura and cxpnotasd.tipo = pTipo 
				and cxpfacturas.factura = pFactura and cxpfacturas.tipo = pTipo
                and cxpnotasd.procode = cxpfacturas.procode),0);*/
	Set vNotasD =
		IfNull((Select 
				sum(cxpnotasd.monto * c.tipoca) 
			from cxpnotasd
			-- Este primer join es para la factura aplicada
			Inner join cxpfacturas on cxpnotasd.factura = cxpfacturas.factura
								  and cxpnotasd.tipo = cxpfacturas.tipo
								  and cxpnotasd.procode = cxpfacturas.procode
			-- Este segundo join es para la nota a aplicar, para el tipo de cambio
			Inner join cxpfacturas c on cxpnotasd.notanume = c.factura
								  and cxpnotasd.notatipo = c.tipo
								  and cxpnotasd.procode = c.procode
			Where cxpnotasd.factura = pFactura
			and cxpnotasd.tipo = pTipo 
			and cxpnotasd.procode = pProcode),0);
	/* Fin Bosco modificado 28/04/2013 */

    Return vRecibos + vNotasD;
END$$
DELIMITER ;
