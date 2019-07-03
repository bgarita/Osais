
DELIMITER $$
CREATE DEFINER=`bgarita`@`%` PROCEDURE `Rep_PagosCXP`(
  IN  `pFecha1`    datetime,
  IN  `pFecha2`    datetime,
  IN  `pProcode1`  varchar(15),
  IN  `pProcode2`  varchar(15),
  IN  `pOrden`     tinyint
)
BEGIN
  -- Autor: Bosco Garita Azofeifa
  Declare vEmpresa varchar(60);

  
  
  If pOrden is null or pOrden > 3 then
    Set pOrden = 1;
  End if;

  Set pFecha1 = IfNull(pFecha1,'1900-01-01');
  Set pFecha2 = IfNull(pFecha2,Date(Now()));

  
  
  Set pFecha1 = date(pFecha1);
  Set pFecha2 = date(pFecha2);

  
  If pProcode1 = '' then
    Set pProcode1 = (Select min(procode) from inproved);
  End if;
  If pProcode2 = '' then
    Set pProcode2 = (Select max(procode) from inproved);
  End if;

  
  Select Empresa from config into vEmpresa;

  
  Create Temporary Table Pagos_cxp
  SELECT
    vEmpresa as Empresa,
    a.procode,
    b.prodesc,
    a.recnume,
    a.concepto,
    a.fecha,
    a.monto * a.tipoca as monto,
    If(a.Estado = 'A','Nulo','') as estado
  From cxppage a
  Inner join inproved b on a.procode = b.procode
  Where date(a.fecha) between pFecha1 and pFecha2
  and a.procode between pProcode1 and pProcode2;


  Case When pOrden = 0 Then
       Select * from Pagos_cxp order by prodesc;

       When pOrden = 1 Then
       Select * from Pagos_cxp order by recnume;

       When pOrden = 2 Then
       Select * from Pagos_cxp order by fecha;

       When pOrden = 3 Then
       Select * from Pagos_cxp order by monto desc;
  End Case;
  Drop table Pagos_cxp;
END$$
DELIMITER ;
