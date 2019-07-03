Use LaFlor;
Drop FUNCTION `ConsultarVentaExenta`;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` FUNCTION `ConsultarVentaExenta`(
  `pFacnume` int
) RETURNS double
BEGIN
  Declare vExento Double;
  Declare vCodigoTC varchar(3);
  Declare vRedond5 tinyInt(1);

  Set vCodigoTC = (Select codigoTC from config);
  Set vRedond5  = (Select Redond5 from config);

  Set vExento = IfNull((
    Select
      If(b.codigoTC = vCodigoTC and vRedond5 = 1,
         RedondearA5(SUM(a.facmont - a.facdesc)),
         SUM(a.facmont - a.facdesc))
    From fadetall a
    Inner Join faencabe b ON a.facnume = b.facnume and
                             a.facnd   = b.facnd
    Where a.facnume = pFacnume AND
	        a.facimve = 0 AND
          a.facnd  >= 0 AND 
          b.facestado = ''),0);

  Return vExento;
END$$
DELIMITER ;
