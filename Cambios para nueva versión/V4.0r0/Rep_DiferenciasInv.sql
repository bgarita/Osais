USE `LaFlor`;
DROP procedure IF EXISTS `Rep_DiferenciasInv`;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `Rep_DiferenciasInv`(
  IN  `pBodega`  varchar(3),
  IN  `pOrden`   tinyint(1)
)
BEGIN
    -- Autor: Bosco Garita Azofeifa
    
    
    
    Declare vEmpresa varchar(60);
    
    Select Empresa from config into vEmpresa;
    
    
    If pOrden is null or pOrden not between 0 and 2 then
        Set pOrden = 0;
    End if;
    
    
    Create temporary table tmp
        Select
            a.linea,
            a.artcode,
            b.artdesc,
            a.cantidad,
            a.artexis,
            a.cantidad - a.artexis as diferencia,
            (a.cantidad - a.artexis) * a.artcosp as artcosp,
            vEmpresa as Empresa
        From conteo a
        Inner join inarticu b on a.artcode = b.artcode
        Where bodega = pBodega and (a.cantidad - a.artexis) <> 0;
    
    
    Case pOrden
        When 0 then
        Select * from tmp order by linea;
        
        When 1 then
        Select * from tmp order by artcode;
        
        When 2 then
        Select * from tmp order by artdesc;
    End Case;
    
    
    Drop table tmp;
END$$
DELIMITER ;
