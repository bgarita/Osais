Drop procedure if EXISTS `ConsultarRegistrosCXC`;

DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `ConsultarRegistrosCXC`(
	IN `pDocumento` int,
	IN `pTipoDoc` tinyint
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    # Autor:	Bosco Garita 16/01/2011
    # Objetivo:	Generar un detalle de los registros afectados por el (o que afectan al)  
    #           documento que recibe por parámetro (Recibo, factura, ND,NC) 
    # Modif.:	Bosco Garita 18/11/2015, agrego el cliente


    # Valido el tipo de documento, asumo un valor default en caso de error (Recibo).
    # pTipoDoc: 1=Factura, 2=NC, 3=ND, 4=Recibo
    Set pTipoDoc = If(pTipoDoc is null or pTipoDoc not between 1 and 4, 4, pTipoDoc);

    # A manera de "PLUS" reconfiguro el número de documento cuando se trata de una NC
    If pTipodoc = 2 and pDocumento is not null and pDocumento > 0 then
    Set pDocumento = pDocumento *-1;
    End if;

    # Obtengo los datos de acuerdo con el tipo de documento.
    Case pTipoDoc
    When 1 then -- Facturas
        Select 
            'Recibos/NC que afectan a esta factura.' as mensaje,
            a.notanume as documento,
            a.notanume as aplicado,
            'NC' as tipo,
            a.monto * c.tipoca as montoML,
            d.simbolo,
            a.monto,
            dtoc(b.facfech) as fecha,
            c.tipoca,
            c.codigoTC,
            e.clidesc
        from notasd a
        Inner join faencabe b on a.notanume = b.facnume and b.facnd > 0 -- El encabezado de las NC también es faencabe
        Inner join faencabe c on a.facnume = c.facnume and a.facnd = c.facnd
        Inner join monedas  d on c.codigoTC = d.codigo
        Inner join inclient e on c.clicode = e.clicode
        Where a.facnume = pDocumento and a.facnd = 0 and b.facestado = ''
        Union all
        Select 
            'Recibos/NC que afectan a esta factura.' as mensaje,
            a.recnume as documento,
            a.recnume as aplicado,
            'R' as tipo,
            a.monto * c.tipoca as montoML,
            d.simbolo,
            a.monto,
            dtoc(b.fecha) as fecha,
            c.tipoca,
            c.codigoTC,
            e.clidesc
        from pagosd a
        Inner join pagos    b on a.recnume = b.recnume
        Inner join faencabe c on a.facnume = c.facnume and a.facnd = c.facnd
        Inner join monedas  d on c.codigoTC = d.codigo
        Inner join inclient e on c.clicode = e.clicode
        Where a.facnume = pDocumento and a.facnd = 0 and b.estado = ''
        Order by aplicado,tipo;

    When 2 then -- Notas de crédito.
        Select 
            'Facturas/ND afectadas por esta nota de crédito.' as mensaje,
            a.notanume as documento,
            a.facnume as aplicado,
            If(a.facnd = 0,'FAC','ND') as tipo,
            a.monto * c.tipoca as montoML,
            d.simbolo,
            a.monto,
            dtoc(b.facfech) as fecha,
            c.tipoca,
            c.codigoTC,
            e.clidesc
        from notasd a
        Inner join faencabe b on a.notanume = b.facnume and b.facnd > 0 -- El encabezado de las NC también es faencabe
        Inner join faencabe c on a.facnume = c.facnume and a.facnd = c.facnd
        Inner join monedas  d on c.codigoTC = d.codigo
        Inner join inclient e on c.clicode = e.clicode
        Where a.notanume = pDocumento and b.facestado = ''
        Order by aplicado,tipo;

    When 3 then -- Notas de débito
        Select 
            'Recibos/NC que afectan a esta nota de débito.' as mensaje,
            a.notanume as documento,
            a.notanume as aplicado,
            'NC' as tipo,
            a.monto * c.tipoca as montoML,
            d.simbolo,
            a.monto,
            dtoc(b.facfech) as fecha,
            c.tipoca,
            c.codigoTC,
            e.clidesc
        from notasd a
        Inner join faencabe b on a.notanume = b.facnume and b.facnd > 0 -- El encabezado de las NC también es faencabe
        Inner join faencabe c on a.facnume = c.facnume and a.facnd = c.facnd
        Inner join monedas  d on c.codigoTC = d.codigo
        Inner join inclient e on b.clicode = e.clicode
        Where a.facnume = pDocumento and a.facnd < 0 and b.facestado = ''
        Union all
        Select 
            'Recibos/NC que afectan a esta nota de débito.' as mensaje,
            a.recnume as documento,
            a.recnume as aplicado,
            'R' as tipo,
            a.monto * c.tipoca as montoML,
            d.simbolo,
            a.monto,
            dtoc(b.fecha) as fecha,
            c.tipoca,
            c.codigoTC,
            e.clidesc
        from pagosd a
        Inner join pagos    b on a.recnume = b.recnume
        Inner join faencabe c on a.facnume = c.facnume and a.facnd = c.facnd
        Inner join monedas  d on c.codigoTC = d.codigo
        Inner join inclient e on c.clicode = e.clicode
        Where a.facnume = pDocumento and a.facnd < 0 and b.estado = ''
        Order by aplicado,tipo;

    When 4 then -- Recibos
        Select 
            'Facturas/ND afectadas por este recibo.' as mensaje,
            a.recnume as documento,
            a.facnume as aplicado,
            If(c.facnd = 0,'FAC','ND') as tipo,
            a.monto * c.tipoca as montoML,
            d.simbolo,
            a.monto,
            dtoc(b.fecha) as fecha,
            c.tipoca,
            c.codigoTC,
            e.clidesc
        from pagosd a
        Inner join pagos    b on a.recnume = b.recnume
        Inner join faencabe c on a.facnume = c.facnume and a.facnd = c.facnd
        Inner join monedas  d on c.codigoTC = d.codigo
        Inner join inclient e on c.clicode = e.clicode
        Where a.recnume = pDocumento and b.estado = ''
        Order by aplicado,tipo;
    End Case;
END$$
DELIMITER ;