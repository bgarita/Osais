USE `LaFlor`;
DROP function IF EXISTS `ConsultarDocumento`;

DELIMITER $$

CREATE DEFINER=`root`@`localhost` FUNCTION `ConsultarDocumento`(
  `pcMovdocu`  varchar(10),
  `pcMovtimo`  char(20),
  `pnMovtido`  smallint(3)
) RETURNS tinyint(1)
BEGIN
    -- Autor: Bosco Garita Azofeifa
    
    
  
    Declare vnExiste TinyInt(1);

    Set vnExiste = 
        If(Exists(  Select movdocu from inmovime
                    Where movdocu = pcMovdocu and movtimo = pcMovtimo
                    and movtido = pnMovtido), 1, 0);

    Return vnExiste;
END$$

DELIMITER ;

