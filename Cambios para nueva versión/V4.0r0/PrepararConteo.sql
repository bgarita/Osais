USE `LaFlor`;
DROP procedure IF EXISTS `PrepararConteo`;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `PrepararConteo`(
	IN  `pBodega`     varchar(3), -- Bodega
	IN  `pPordesc`    tinyint(1), -- Indica si se ordenará por descripción (1=Si,0=No)
	IN  `pRegenerar`  tinyint(1), -- Indica si la tabla se regenerará o no (1=Si,0=No). Regenerar significa sobreescribir
	IN  `pValorar`    tinyint(1), -- 0=Costo promedio, 1=Precio1, 2=Precio2, 3=Precio3, 4=Precio4, 5=Precio5
	IN  `pLocaliz1`   varchar(7), -- Rango 1 de localizaciones (Bosco agregado 20/12/2015)
	IN  `pLocaliz2`   varchar(7)  -- Rango 2 de localizaciones (Bosco agregado 20/12/2015)
)
BEGIN
    # Autor:    Bosco Garita 22/01/2011.
    # Objet:    Generar la tabla de conteo con las existencias actuales.
    # Devuelve: Un result set con el número de registros generados. 

	# Modificador por: Bosco Garita 20/12/2015. 
	#			Se le agregan los parámetros pLocaliz1 y pLocaliz2 que permitirán realizar
	#			inventarios por localización.
    
    # Establezco los valores por defecto
    Set pPordesc   = IfNull(pPordesc,0);
    Set pRegenerar = Ifnull(pRegenerar,0); -- 0=Actualiza los datos, 1=Los sobreescribe
    
    -- 0=Costo promedio, 1=Precio1, 2=Precio2, 3=Precio3, 4=Precio4, 5=Precio5
    If pValorar is null or pValorar not between 0 and 5 then
        Set pValorar = 0;
    End if;

	Set pLocaliz1 = IfNull(pLocaliz1,'');

	If pLocaliz2 is null or pLocaliz1 = '' Then
		Select max(localiz) from bodexis Where bodega = pBodega into pLocaliz2;
	End if;
    
    If pRegenerar = 1 then  -- Generar conteo nuevo
    
        Delete from conteo Where bodega = pBodega;
        
        INSERT INTO `conteo`
            (`bodega`,
            `artcode`,
            `cantidad`,
            `artexis`,
            `artcosp`,
            `fecha`,
            `userDigita`,
            `userAplica`,
            `movdocu`,
            `pordesc`)
            Select
                a.bodega,
                a.artcode,
                0,
                a.artexis,
                Case pValorar When 0 then b.artcosp
                              When 1 then b.artpre1
                              When 2 then b.artpre2
                              When 3 then b.artpre3
                              When 4 then b.artpre4
                              Else b.artpre5
                End,
                now(),
                user(),
                '',
                '',
                pPordesc
            From bodexis a
            Inner join inarticu b on a.artcode = b.artcode
            Where a.bodega = pBodega
			and a.localiz between pLocaliz1 and pLocaliz2;
    Else -- Actualizar el conteo existente
        Update conteo,bodexis,inarticu b
        Set conteo.artexis = bodexis.artexis,
        conteo.artcosp = 
                Case pValorar When 0 then b.artcosp
                              When 1 then b.artpre1
                              When 2 then b.artpre2
                              When 3 then b.artpre3
                              When 4 then b.artpre4
                              Else b.artpre5
                End
        Where conteo.bodega = pBodega
        and conteo.bodega = bodexis.bodega
        and conteo.artcode = bodexis.artcode
        and conteo.artcode = b.artcode;
        
        # Agregar los registros que no existen
        INSERT INTO `conteo`
            (`bodega`,
            `artcode`,
            `cantidad`,
            `artexis`,
            `artcosp`,
            `fecha`,
            `userDigita`,
            `userAplica`,
            `movdocu`,
            `pordesc`)
            Select
                a.bodega,
                a.artcode,
                0,
                a.artexis,
                Case pValorar When 0 then b.artcosp
                              When 1 then b.artpre1
                              When 2 then b.artpre2
                              When 3 then b.artpre3
                              When 4 then b.artpre4
                              Else b.artpre5
                End,
                now(),
                user(),
                '',
                '',
                pPordesc
            From bodexis a
            Inner join inarticu b on a.artcode = b.artcode
            Where a.bodega = pBodega
			and a.localiz between pLocaliz1 and pLocaliz2
            and not Exists(
                        Select c.artcode from conteo c
                        Where c.bodega = a.bodega and c.artcode = a.artcode);
        
        # Actualizar el campo de orden y el usuario
        Update conteo
			Set pordesc = pPordesc, userDigita = user()
        Where bodega = pBodega;
        
    End if;
    
    # Contar los registros generados
    Select count(*) as registros from conteo Where bodega = pBodega;

END$$
DELIMITER ;
