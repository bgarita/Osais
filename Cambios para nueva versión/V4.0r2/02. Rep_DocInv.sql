USE `LaFlor`;
DROP procedure IF EXISTS `Rep_DocInv`;

DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `Rep_DocInv`(
  IN  `pMovdocu`  varchar(10),
  IN  `pMovtimo`  char(1),
  IN  `pMovtido`  tinyint(2)
)
BEGIN
    # Autor:    Bosco Garita A. 12/02/2011.
    # Objet:    Generar un Result Set con los datos de un documento (para impresión de documentos)
    #           Si el parámetro recibido en pMovtimo = 'A' o viene nulo se trata de un Ajuste
	#           Modificado por Bosco Garita 25/04/2015
	#			Determina si el documento es de CXC y si es así trae el nombre de cliente 
    
    Declare vEmpresa varchar(60); 
	Declare vClidesc varchar(50);

    Select Empresa from config into vEmpresa;

	-- Determinar si el documento es de CXC para traer el nombre del cliente
    Set vClidesc = '';
	If Exists(Select descrip from intiposdoc 
			  Where movtido = pMovtido and modulo = 'CXC') then
		if pMovtimo = 'E' then
			Set pMovdocu = Abs(pMovdocu); 
		End if;
		Select clidesc from faencabe, inclient
		Where facnume = pMovdocu
		and If(pMovtimo = 'S', facnd = 0, facnd > 0)
		and faencabe.clicode = inclient.clicode
		limit 1
		Into vClidesc;
		-- Select vClidesc;
	End if;


	-- Detalle del movimiento
    Select         
        a.movdocu,
        a.movtimo,
        a.artcode,
        b.barcode,   
        b.artdesc,   
        a.bodega,    
        a.movcant,
        a.movcant * a.artprec as PrecioT,
        a.movcant * a.movcoun as CostoT,
        a.movcoun,   
        a.artprec,   
        a.facimve,   
        a.facdesc,   
        c.tipoca,    
        dtoc(c.movfech) as movfech,
        c.user,      
        c.movdesc,   
        d.descrip,
        e.simbolo,
        c.estado,
		c.movorco,
		vClidesc as cliente,
        vEmpresa as Empresa , pMovdocu
    From inmovimd a 
    Inner join inarticu b on a.artcode = b.artcode 
    Inner join inmovime c on a.movdocu = c.movdocu                      
    and a.movtimo = c.movtimo                      
    and a.movtido = c.movtido 
    Inner join intiposdoc d on a.movtido = d.movtido 
    Inner join monedas    e on c.codigoTC = e.codigo
    Where a.movdocu = pMovdocu
	and a.movtimo = pMovtimo
    and a.movtido = pMovtido;
END$$

DELIMITER ;