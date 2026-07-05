Drop procedure if exists `RecalcularSaldoProveedores`;
DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `RecalcularSaldoProveedores`(
  IN `pcProcode` varchar(15)
)
BEGIN
    # Autor:        Bosco Garita 22/04/2012.
    # Descripci_n:  Recalcular el saldo de los proveedores, el monto acumulado de abonos
    #               y la fecha del último abono.  Este monto y esta fecha aplica únicamente
    #               para los registros de facturas de cr_dito o notas de cr_dito.
    #               En la tabla de proveedores tambi_n se establece el monto y la fecha
    #               de la _ltima compra.
    # NOTA:         Debe correr dentro de una transacci_n.

  Declare vAbono_acum decimal(14,4);

  # Primero recalculo el saldo de las facturas y notas de crédito 
  # y luego el saldo del proveedor.

    
  If pcProcode is null then 
	 # Recalcular todos los proveedores
	
     # Establecer el acumulado de abonos y la fecha del _ltimo abono.
     Update cxpfacturas Set 
        abono_acum = ifNull(AplicadoAFacturaCXP(factura,tipo,procode) / tipoca,0), -- Viene en moneda local y se convierte a la moneda del registro.
        fec_ult_ab = FechaUltAbFacturaCXP(factura,tipo)
     Where tipo in ('FAC','NCR')
     and vence_en > 0
	 and cerrado = 'N'; -- Bosco agregado 14/03/2013

     # Recalcular el saldo los registros en la tabla cxpfacturas
     Update cxpfacturas Set 
        saldo = total_fac - abono_acum
     Where vence_en > 0
	 and cerrado = 'N'; -- Bosco agregado 14/03/2013

     # Recalcular el saldo y la fecha de la _ltima compra de los proveedores
     Update inproved Set prosald = 0, profeuc = null;

     Update inproved
       Set prosald = IfNull((Select sum(saldo * tipoca)
                             from cxpfacturas
                             Where procode = inproved.procode 
                             and saldo <> 0),0);

     Update inproved
       Set profeuc = (Select max(fecha_fac)
                      from cxpfacturas
                      where procode = inproved.procode
                      and tipo = 'FAC');

  Else 
	 # Recalcular un solo proveedor
	 
     # Establecer el acumulado de abonos y la fecha del _ltimo abono.
	 Update cxpfacturas Set 
		abono_acum = IfNull(AplicadoAFacturaCXP(factura,tipo,procode) / tipoca,0), -- Viene en moneda local y se convierte a la moneda del registro.
		fec_ult_ab = FechaUltAbFacturaCXP(factura,tipo)
     Where procode = pcProcode 
     and tipo in ('FAC','NCR')
     and vence_en > 0
	 and cerrado = 'N'; -- Bosco agregado 14/03/2013

     # Recalcular el saldo los registros en la tabla cxpfacturas
     Update cxpfacturas Set 
        saldo = total_fac - abono_acum
     Where procode = pcProcode 
     and vence_en > 0
	 and cerrado = 'N'; -- Bosco agregado 14/03/2013

     # Recalcular el saldo y la fecha de la _ltima compra del proveedor
     Update inproved Set prosald = 0, profeuc = null Where procode = pcProcode;

     Update inproved
       Set prosald = IfNull((Select sum(saldo * tipoca)
                             from cxpfacturas
                             Where procode = inproved.procode
                             and saldo <> 0),0)
     Where procode = pcProcode;
     
     Update inproved
       Set profeuc = (Select max(fecha_fac)
                      from cxpfacturas
                      where procode = inproved.procode
                      and tipo = 'FAC')
     Where procode = pcProcode;
  End if;

END$$
DELIMITER ;