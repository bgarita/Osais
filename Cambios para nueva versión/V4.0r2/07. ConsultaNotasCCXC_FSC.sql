
DROP procedure IF EXISTS `ConsultarNotasCCXC_FSC`;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `ConsultarNotasCCXC_FSC`(
  IN `pFacnume` int(10)
)
BEGIN
	-- Autor: Bosco Garita Azofeifa, 16/12/2018
	-- Obtener todas las notas de crédito sobre facturas de contado que aún no han sido
	-- enviadas a Hacienda.
	Select
		ABS(faencabe.facnume)  as facnume,
		inclient.clidesc,
		Dtoc(faencabe.facfech) as fecha,
		ABS(faencabe.facsald)  as facsald,
		faencabe.codigoTC,
		Trim(monedas.descrip)  as Moneda,
		faencabe.tipoca,
		faencabe.facfech,       
		ABS(faencabe.facsald) * faencabe.tipoca as SaldoML,
		faencabe.clicode
	from faencabe
	Inner Join monedas  on faencabe.codigoTC = monedas.codigo
	Inner join inclient on faencabe.clicode  = inclient.clicode
	where faencabe.facnume = If(Abs(pFacnume) = 0, faencabe.facnume, pFacnume)
	and facnd > 0  
	and facestado = '' 
	and claveHacienda = ''
	and facsald = 0
	and faccsfc = 1
	and not exists(	Select notanume 
					from notasd 
					Where Notanume = faencabe.facnume 
					and facnd = faencabe.facnd)
	Order by facfech,facnume;

END$$
DELIMITER ;




DROP procedure IF EXISTS `ConsultarNotasCCXC`;

DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `ConsultarNotasCCXC`(
  IN `pFacnume` int(10)
)
BEGIN
  -- Autor: Bosco Garita Azofeifa
  
	Select
		ABS(faencabe.facnume)  as facnume,
		inclient.clidesc,
		Dtoc(faencabe.facfech) as fecha,
		ABS(faencabe.facsald)  as facsald,
		faencabe.codigoTC,
		Trim(monedas.descrip)  as Moneda,
		faencabe.tipoca,
		faencabe.facfech,       
		ABS(faencabe.facsald) * faencabe.tipoca as SaldoML,
		faencabe.clicode
	from faencabe
	Inner Join monedas  on faencabe.codigoTC = monedas.codigo
	Inner join inclient on faencabe.clicode  = inclient.clicode
	where faencabe.facnume = If(Abs(pFacnume) = 0, faencabe.facnume, pFacnume)
	and facnd > 0     
	and facsald < 0
	and facestado = '' 
	Order by facfech,facnume;

END$$

DELIMITER ;





DROP procedure IF EXISTS `ConsultarFacturasCliente2`;

DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `ConsultarFacturasCliente2`(
  IN  `pClicode`   int(10)
)
BEGIN
    -- Autor: Bosco Garita Azofeifa, 16/12/2018
    -- Traer todas las facturas de contado que ya fueron enviadas a Hacienda

    Select
        facnume,
        dtoc(facfech) as fecha,
        facplazo,
        facmont * tipoca as MontoML,            
        facsald,
        codigoTC,
        Trim(descrip) as Moneda,
        tipoca,
        'FC' as TipoDoc,    
        facfech,       
        facsald * tipoca as SaldoML             
    from faencabe
    Inner Join monedas on faencabe.codigoTC = monedas.codigo
    where clicode = pClicode
    and facnd = 0     
    and facsald = 0
	and facplazo = 0
    and facestado = '' 
	and claveHacienda > ''
    Order by facfech,facnume;

END$$

DELIMITER ;
;




DROP procedure IF EXISTS `ImprimirNotaCXC`;

DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `ImprimirNotaCXC`(
  IN `pNotanume` int
)
BEGIN
    # Autor:    Bosco Garita 11/02/2012.
    # Descrip.: Genera la información necesaria para imprimir una nota de crédito.
    
    Declare vcEmpresa  varchar(60);
    Declare vcTelefono varchar(30);
    Declare vcCedulaJu varchar(50);
    Declare vcDireccio varchar(200);
    Declare vcTimbre   varchar(40);
    Declare vnRedond   tinyInt(1);
    
    # Bosco agregado 12/03/2012.
    # Determinar si la nota de crédito aplica sobre facturas de contado.
    Declare vnSobreC   tinyInt(1);
    If Exists(Select 1 from faencabe 
              Where facnume = pNotanume and facnd = ABS(pNotanume)
              and faccsfc = 1) then
        Set vnSobreC = 1;
    End if;
    
    Set vnSobreC = IfNull(vnSobreC,0);
    # Fin Bosco agregado 12/03/2012.

    Set vcEmpresa  = (Select empresa from config);
    Set vcTelefono = (Select Concat('TELÉFONO: ', telefono1) from config);
    Set vcCedulaJu = (Select Concat('CÉDULA JURÍDICA : ', cedulajur) from config);
    Set vcDireccio = (Select Direccion from config);
    
    # Bosco modificado 12/03/2012.
    # Ahora se manda el select a una tabla temporal.
    # Pero también hay que decidir de donde vienen los datos.  Si es una nota de crédito
    # normal los datos vendrán del detalle de notas aplicadas.  Si se trata de una nota
    # de crédito sobre facturas de contado entonces viene del encabezado nada más.

	-- Bosco modificado 17/12/2018
	-- Debido a que la facturación electrónica exige una referencia en las NC
	-- el proceso se modificó en el sistema de manera que las NC sobre contado
	-- también se muestren en la tabla notasd (con saldo cero). Esto hace que
	-- tanto las NC normales como las NC sobre facturas de contado sigan el mismo
	-- proceso de impresión.
    -- If vnSobreC = 0 then
        Create temporary table Notacredito
            Select
                vcEmpresa  as empresa,
                vcTelefono as telefono,
                vcCedulaJu as cedulajur,
                vcDireccio as Direccion,
                notasd.notanume,
                faencabe.facfech,
                faencabe.codigoTC,
                faencabe.facmont,
                faencabe.tipoca,
                monedas.descrip,
                monedas.simbolo,
                faencabe.clicode,
                inclient.clidesc,
                notasd.facnume,
                notasd.facnd,
                If(notasd.facnd = 0, 'Factura','Nota débito') as TipoDoc,
                notasd.monto as MontoAp,
                monedas.simbolo as simboloF,
                inclient.clisald,
                inclient.clisald + ABS(faencabe.facmont) as SaldoAnt,
				If(vnSobreC = 1,'S','N') as SobreContado,	-- Bosco agregado 17/12/2018
				faencabe.claveHacienda,
				faencabe.consHacienda,
				inclient.clitel1,
				inclient.clidir
            From notasd
            Inner Join faencabe on notasd.notanume   = faencabe.facnume
            Inner Join inclient on faencabe.clicode  = inclient.clicode
            Inner Join monedas  on faencabe.codigoTC = monedas.codigo
            Where notasd.notanume = pNotanume and faencabe.facnd = abs(pNotanume);
    /*Else
        Create temporary table Notacredito
            Select
                vcEmpresa  as empresa,
                vcTelefono as telefono,
                vcCedulaJu as cedulajur,
                vcDireccio as Direccion,
                pNotanume as notanume,
                faencabe.facfech,
                faencabe.codigoTC,
                faencabe.facmont,
                faencabe.tipoca,
                monedas.descrip,
                monedas.simbolo,
                faencabe.clicode,
                inclient.clidesc,
                0 as facnume,
                0 as facnd,
                'Factura' as TipoDoc,
                faencabe.facmont as MontoAp,
                monedas.simbolo as simboloF,
                inclient.clisald,
                inclient.clisald + ABS(faencabe.facmont) as SaldoAnt
            From faencabe
            Inner Join inclient on faencabe.clicode  = inclient.clicode
            Inner Join monedas  on faencabe.codigoTC = monedas.codigo
            Where faencabe.facnume = pNotanume and faencabe.facnd = abs(pNotanume);
    End if; */
    
    Select * from Notacredito;
    Drop table Notacredito;
END$$

DELIMITER ;

