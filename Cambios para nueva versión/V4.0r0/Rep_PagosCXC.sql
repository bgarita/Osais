Use LaFlor;
Drop procedure Rep_PagosCXC;

DELIMITER $$
CREATE DEFINER=`bgarita`@`%` PROCEDURE `Rep_PagosCXC`(
  IN  `pFecha1`    datetime,
  IN  `pFecha2`    datetime,
  IN  `pClicode1`  int,
  IN  `pClicode2`  int,
  IN  `pOrden`     tinyint
)
BEGIN
  
  Declare vEmpresa varchar(60);

  
  
  If pOrden is null or pOrden > 3 then
    Set pOrden = 1;
  End if;

  Set pFecha1 = IfNull(pFecha1,'1900-01-01');
  Set pFecha2 = IfNull(pFecha2,Date(Now()));

  
  
  Set pFecha1 = date(pFecha1);
  Set pFecha2 = date(pFecha2);

  
  If pClicode1 = 0 then
    Set pClicode1 = (Select min(clicode) from inclient);
  End if;
  If pClicode2 = 0 then
    Set pClicode2 = (Select max(clicode) from inclient);
  End if;

  
  Select Empresa from config into vEmpresa;

  
  Create Temporary Table Pagos_cxc
  SELECT
    vEmpresa as Empresa,
    a.clicode,
    b.clidesc,
    a.recnume,
    a.concepto,
    a.fecha,
    a.monto * a.tipoca as monto,
    If(a.Estado = 'A','Nulo','') as estado
  From pagos a
  Inner join inclient b on a.clicode = b.clicode
  Where a.fecha between pFecha1 and pFecha2
  and a.clicode between pClicode1 and pClicode2;


  Case When pOrden = 0 Then
       Select * from Pagos_cxc order by clidesc;

       When pOrden = 1 Then
       Select * from Pagos_cxc order by recnume;

       When pOrden = 2 Then
       Select * from Pagos_cxc order by fecha;

       When pOrden = 3 Then
       Select * from Pagos_cxc order by monto desc;
  End Case;
  Drop table Pagos_cxc;
END$$
DELIMITER ;
