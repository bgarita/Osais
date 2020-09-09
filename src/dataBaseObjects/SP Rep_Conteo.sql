
DROP PROCEDURE if EXISTS Rep_Conteo;

Delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `Rep_Conteo`(
	IN `pRangos` tinyint(1),
	IN `pBodega` char(3),
	IN `pLinea1` int,
	IN `pLinea2` int,
	IN `pOrden` tinyint
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    -- Autor: Bosco Garita Azofeifa
    
    Declare vEmpresa varchar(60);
    
    If pRangos = 0 then 
        Select Empresa from config into vEmpresa;
        
        
        If pLinea1 is null then
            Select min(linea) from conteo where bodega = pBodega into pLinea1;
        End if;
        
        If pLinea2 is null or pLinea2 < pLinea1 then
            Select max(linea) from conteo where bodega = pBodega into pLinea2;
        End if;
        
        
        Select pordesc from conteo Where bodega = pBodega limit 1 into pOrden;
        
        Select conteo.*, inarticu.artdesc, bodegas.descrip, vEmpresa as Empresa
        from conteo 
        Inner join inarticu on conteo.artcode = inarticu.artcode
        Inner join bodegas  on conteo.bodega  = bodegas.bodega
        Where conteo.bodega = pBodega
        and linea between pLinea1 and pLinea2
        Order by If(pOrden = 1,inarticu.artdesc,conteo.artcode);
    Else 
        Select min(linea) from conteo where bodega = pBodega into pLinea1;
        Select max(linea) from conteo where bodega = pBodega into pLinea2;
        
        Select pLinea1 as linea1, pLinea2 as linea2;
    End if;
END$$

Delimiter ;