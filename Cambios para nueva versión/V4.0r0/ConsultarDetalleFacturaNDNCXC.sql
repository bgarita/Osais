use LaFlor;
Drop procedure ConsultarDetalleFacturaNDNCXC;

DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `ConsultarDetalleFacturaNDNCXC`(
  IN  `pFacnume`  int,
  IN  `pTipo`     tinyint
)
BEGIN
    -- Autor: Bosco Garita Azofeifa
    
    
    
    
    If pTipo is null or pTipo not between 1 and 3 then
        Set pTipo = 1;
    End if;
    
    Select 
        a.artcode,
        c.artdesc,
        a.bodega,
        a.faccant,
        a.artprec * b.tipoca as artprec,
        a.facmont * b.tipoca as facmont
    From fadetall a
    Inner join faencabe b on a.facnume = b.facnume and a.facnd = b.facnd
    Inner join inarticu c on a.artcode = c.artcode
    Where a.facnume = pFacnume and If(pTipo = 1, a.facnd <= 0, a.facnd > 0);
END$$
DELIMITER ;
