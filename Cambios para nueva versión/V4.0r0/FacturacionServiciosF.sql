Use LaFlor;
Drop FUNCTION `FacturacionServiciosF`;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` FUNCTION `FacturacionServiciosF`(
	pFacnume Int,	
	pFacnd Int) RETURNS double
BEGIN
	-- Autor: Bosco Garita Azofeifa
    
	
    

	Declare vMonto Double;

	Select 
		Sum(fadetall.facmont * faencabe.tipoca) as monto
	from fadetall 
	Inner join inservice on fadetall.artcode = inservice.artcode
	Inner join faencabe on fadetall.facnume = faencabe.facnume and fadetall.facnd = faencabe.facnd
	Where faencabe.facnume = pFacnume
	and faencabe.facnd = pFacnd
	and faencabe.facestado = ''
	Into vMonto;

	Set vMonto = IfNull(vMonto,0);

	RETURN vMonto;
END$$
DELIMITER ;
