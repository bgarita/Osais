Drop procedure if exists InsertarDetalleReciboCXP;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarDetalleReciboCXP`(
  IN  `pRecnume`  int,
  IN  `pFactura`  varchar(10),
  IN  `pTipo`     varchar(3),
  IN  `pMonto`    double,
  IN  `pSaldo`    double,
  IN  `pFecha`    datetime,
  IN  `pProcode`  varchar(15)
)
BEGIN
    Declare vError tinyInt;
    Declare vErrorMessage varchar(500);
    Declare vSaldo Double;

    # Este SP siempre devolver_ un Select con el resultado de la corrida.
    # Si hubo error la variable vError valdr_ 1 y vErrorMessage tendrá la descripción
    # del error.

    Set vError = 0;
    Set vErrorMessage = '';

    -- Select * From cxpfacturas 
	-- where factura = pfactura and tipo = pTipo ;

    # Determinar si el saldo de la factura es el mismo que viene por par_metro.
	Select saldo From cxpfacturas 
	where factura = pfactura and tipo = pTipo and procode = pProcode
	Into vSaldo;

    # Se usa 0.009 como rango de tolerancia fijo pero habr_ que ponerlo en una
    # tabla de par_metros.
    If vSaldo <> pSaldo and Abs(vSaldo - pSaldo) > 0.009 then
        Set vError = 1;
        Set vErrorMessage =
            Concat('[BD] La factura/NC # ',pFactura, 
                   '  ya no tiene el mismo saldo. Este es ahora ', 
                   vSaldo, ' y viene ', pSaldo, ' (Dif ',Abs(vSaldo - pSaldo), ')');
    End if;

    # Agregar el registro en el detalle de recibos.
    If vError = 0 then
        Insert into cxppagd(
            recnume,
            factura,
            tipo,
            monto)
        Values(
            pRecnume,
            pFactura,
            pTipo,
            pMonto);
    End if;

   # Actualizar el registro en cxpfacturas.
    Update cxpfacturas Set
      saldo = saldo - pMonto,
      abono_acum = abono_acum + pMonto,
      fec_ult_ab = If(fec_ult_ab is null or Date(fec_ult_ab) < Date(pFecha), Date(pFecha),fec_ult_ab)
    Where factura = pFactura and tipo = pTipo and procode = pProcode;

    # Si no se afect_ ning_n registro...
    If Row_count() <> 1 then
        # ... seteo el error.
        Set vError = 1;
        Set vErrorMessage = 
            Concat('[BD] Se produjo un error al intentar aplicar la factura # ',
            pFactura, '. Se espera afectar 1 registro y se afectó ', Row_count());
    End if;

    Select vError as vError,vErrorMessage as vErrorMessage;
END$$
DELIMITER ;