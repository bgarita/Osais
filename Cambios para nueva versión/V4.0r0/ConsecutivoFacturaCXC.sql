use LaFlor;
Drop FUNCTION ConsecutivoFacturaCXC;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` FUNCTION `ConsecutivoFacturaCXC`(
  `pNtipo` tinyint(1)
) RETURNS int(10)
BEGIN
    -- Autor: Bosco Garita Azofeifa
    
    
    Declare vFacnume int;

    
    Set vFacnume = Case When pNtipo = 1 then (Select facnume from config)
                        When pNtipo = 2 then (Select ncred   from config)
                        When pNtipo = 3 then (Select ndeb    from config)
                   End;

    Set vFacnume = If(vFacnume is null or vFacnume = 0, 1, vFacnume);

    Set vFacnume = If(pNtipo = 2, vFacnume * -1, vFacnume);

    
    CASE pNtipo
      WHEN 1 THEN 
         If Exists(Select facnume from faencabe Where facnume = vFacnume and facnd = 0) then
           Set vFacnume = (Select max(facnume) from faencabe Where facnd = 0) + 1;
         End if;
      WHEN 2 THEN 
         If Exists(Select facnume from faencabe Where facnume = vFacnume and facnd > 0) then
           Set vFacnume = (Select min(facnume) from faencabe Where facnd > 0) - 1;
         End if;
      WHEN 3 THEN 
         If Exists(Select facnume from faencabe Where facnume = vFacnume and facnd = vFacnume * -1) then
           Set vFacnume = (Select max(facnume) from faencabe Where facnd < 0) + 1;
         End if;

    END CASE;


    Return vFacnume;
END$$
DELIMITER ;
