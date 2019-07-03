Use LaFlor;
Drop procedure Rep_CXC;

DELIMITER $$
CREATE DEFINER=`bgarita`@`%` PROCEDURE `Rep_CXC`(
  IN  `pFacfech1`  datetime,
  IN  `pFacfech2`  datetime,
  IN  `pVend1`     smallint,
  IN  `pVend2`     smallint,
  IN  `pResumido`  tinyint,
  IN  `pOrden`     tinyint
)
BEGIN
  
  Declare vEmpresa varchar(60);

  
  If pOrden is null or pOrden > 3 then
    Set pOrden = 3;
  End if;

  
  Set pFacfech1 = IfNull(pFacfech1,'1900-01-01');
  Set pFacfech2 = IfNull(pFacfech2,Date(Now()));

  
  
  Set pFacfech1 = date(pFacfech1);
  Set pFacfech2 = date(pFacfech2);

  
  If pVend1 = 0 then
    Set pVend1 = (Select min(vend) from vendedor);
  End if;
  If pVend2 = 0 then
    Set pVend2 = (Select max(vend) from vendedor);
  End if;

  
  Select Empresa from config into vEmpresa;

  
  Create Temporary Table cxc
      SELECT
        vEmpresa as Empresa,
        a.clicode,
        b.clidesc,
        a.facnume,
        If(a.facnd < 0,'ND','F') as tipo,
        a.facfech,
        a.facmont * a.tipoca as facmont,
        a.facsald * a.tipoca as facsald,
        a.facplazo,
        a.facfepa as Vence,
        DateDiff(Now(),a.facfech) as Dias
      FROM faencabe a
      INNER JOIN inclient b on a.clicode = b.clicode
      WHERE a.facfech BETWEEN pFacfech1 and pFacfech2
      and a.facsald <> 0
      AND a.vend BETWEEN pVend1 and pVend2
      AND a.facnd <= 0
      AND a.facestado = '';


  If pResumido = 0 then
    Case When pOrden = 0 Then
         Select * from cxc order by clidesc;

         When pOrden = 1 Then
         Select * from cxc order by facfech;

         When pOrden = 2 Then
         Select * from cxc order by facsald desc;

         When pOrden = 3 Then
         Select * from cxc order by facnume;
    End Case;
  Else
    Select
      Empresa,
      clicode,
      clidesc,
      sum(facmont) as facmont,
      sum(facsald) as facsald
    From cxc
    Group by clicode
    Order by facsald desc;
  End if;
  Drop table cxc;
END$$
DELIMITER ;
