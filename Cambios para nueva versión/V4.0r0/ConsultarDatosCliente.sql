use LaFlor;
Drop procedure ConsultarDatosCliente;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `ConsultarDatosCliente`(
  IN `pClicode` int(10)
)
BEGIN
    /*
    Autor: Bosco Garita
    Consultar los datos más relevantes de un cliente para efectos de
    validación.
	*/
    Declare vMontoVencido  decimal(12,2); 
    Declare vMontoUltComp  decimal(12,2); 
    Declare vContadoMes    decimal(12,2); 
    Declare vFacsald       decimal(12,2);
	Declare vSaldoOtrasCXC decimal(12,2);


    Set vMontoVencido = ConsultarMontoVencidoCXC(pClicode,0);

    Set vMontoUltComp = IfNull((
        Select facmont * tipoca
        From faencabe
        Where clicode = pClicode and facnume > 0 and facnd = 0 and facestado <> 'A'
        Order by facfech desc
        Limit 1),0);

    
    Select sum(facsald * tipoca) from faencabe 
    Where clicode = pClicode 
    and facnume > 0
    and facestado <> 'A' and facsald > 0
    into vFacsald;
    
    Set vFacsald = IfNull(vFacsald,0);

	-- Calcular el monto en otras cuentas por cobrar
	Select 
		sum(o.montocxc - o.montorecibido) as saldoOtros
	from cxcotros o
	Inner join faencabe f on o.facnume = f.facnume and f.facnd = 0
	Inner join inclient i on f.clicode = i.clicode
	Where f.clicode = pClicode
	into vSaldoOtrasCXC;
    
	Set vSaldoOtrasCXC = IfNull(vSaldoOtrasCXC,0);
    
    Select
        clidesc,  
        clifeuc,  
        clitel1,  
        clicelu,  
        clilimit, 
        cliprec,  
        clisald,  
        exento,   
        vend,     
        terr,     
        cliplaz,  
        clireor,  
        clinpag,  
        vMontoVencido as Vencido,  
        vMontoUltComp as MontoUC,  
        IgSitCred,   
        CredCerrado, 
        IfNull(DateDiff(now(),clifeuc),0) as DiasUC, 
		cligenerico, 
        (Select count(clicode) from pedidoe where facnume = inclient.clicode) as pedidos,
        vFacsald as facsald,
		vSaldoOtrasCXC as saldoOtros
    from inclient
    Where clicode = pClicode;

END$$
DELIMITER ;

