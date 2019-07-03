Use LaFlor;
Drop procedure Rep_Facturacion;

DELIMITER $$
CREATE DEFINER=`bgarita`@`%` PROCEDURE `Rep_Facturacion`(
  IN  pFacfech1  datetime,
  IN  pFacfech2  datetime,
  IN  pVend1     tinyint(3),
  IN  pVend2     tinyint(3),
  IN  pUser       varchar(40),
  IN  pSoloForm  tinyint(1),
  IN  pExcServ   tinyint(1), 
  IN  pOrden     tinyint(2)
)
BEGIN
    Declare vEmpresa varchar(60);
	Declare vServicios double;

    Set vEmpresa = (Select empresa from config);
	
    
    If pFacfech1 is null then
        Set pFacfech1 = '1900-01-01';
    End if;
    If pFacfech2 is null then
        Set pFacfech2 = now();
    End if;

    If pVend1 = 0 or pVend1 is null then
        Set pVend1 = (Select min(vend) from vendedor);
    End if;
    If pVend2 = 0 or pVend2 is null then
        Set pVend2 = (Select max(vend) from vendedor);
    End if;

	if pUser > '' then
		Set pUser = Concat(trim(pUser), '@%');
	End if;

	

    
    Set pOrden = IfNull(pOrden,1);


    
    Select
        facnume,
        nombre as vendedor,     
        inclient.clidesc,
        formulario,
        If(facplazo > 0, 'CR', 'CO') as Tipo,
        facfech,
        If(facestado = ' ',
			(facmont - facimve + facdesc) * tipoca - If(pExcServ = 1,FacturacionServiciosF(a.facnume,a.facnd), 0), 0) as SubTotal,
        If(facestado = ' ',facdesc * tipoca,0) as Descuento,
        If(facestado = ' ',facimve * tipoca,0) as IV,
        If(facestado = ' ',facmont * tipoca - If(pExcServ = 1,FacturacionServiciosF(a.facnume,a.facnd), 0),0) as Total,
        If(facestado = ' ', ConsultarVentaExenta(facnume),0) * tipoca as Exento,
        If(facestado = 'A', 'Nula', ' ') as Estado,
        CASE  
             When factipo = 0 and facnume < 0 and facplazo = 0 and faccsfc = 1 then 'Efectivo'  
             When factipo = 0 or facplazo > 0 then 'Desconocido'
             When factipo = 1 then 'Efectivo'
             When factipo = 2 then 'Cheque'
             When factipo = 3 then 'Tarjeta'
             Else 'Error'
        END as FormaPago,
        precio,
		a.facfechac,
        vEmpresa as Empresa
    From faencabe a
    Inner join vendedor on a.vend    = vendedor.vend
    Inner join inclient on a.clicode = inclient.clicode
    Where facfech >= pFacfech1 and facfech <= pFacfech2
    and a.vend >= pVend1 and a.vend <= pVend2
    and If(pSoloForm = 0, a.formulario = a.formulario, a.formulario > 0)
    and a.facnd >= 0  
	and If(pUser = '', a.user = a.user, a.user like pUser)
    Order by Case pOrden 
                    When 1 then facnume
                    When 2 then vendedor
                    When 3 then clidesc
                    When 4 then formulario
                    When 5 then facfech
           End;
END$$
DELIMITER ;
