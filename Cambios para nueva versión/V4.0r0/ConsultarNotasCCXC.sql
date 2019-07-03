Use LaFlor;
Drop PROCEDURE `ConsultarNotasCCXC`;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `ConsultarNotasCCXC`(
  IN `pFacnume` int(10)
)
BEGIN
  
  
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
