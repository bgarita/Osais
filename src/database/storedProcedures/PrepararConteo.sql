drop procedure if exists PrepararConteo;
delimiter $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `PrepararConteo`(
    IN `pBodega` varchar(3),
    IN `pPordesc` tinyint(1),
    IN `pRegenerar` tinyint(1),
    IN `pValorar` tinyint(1),
    IN `pLocaliz1` varchar(7),
    IN `pLocaliz2` varchar(7),
    IN `pUsuario` varchar(40)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    # Autor: Bosco Garita 22/01/2011.
    # Objet: Generar la tabla de conteo con las existencias actuales.
    # Devuelve: Un result set con el numero de registros generados.
    # Modificador por: Bosco Garita 20/12/2015.
    # Se agregaron pLocaliz1 y pLocaliz2 para permitir inventarios por localizacion.

    Set pPordesc = IfNull(pPordesc, 0);
    Set pRegenerar = IfNull(pRegenerar, 0); -- 0=Actualiza los datos, 1=Los sobreescribe
    Set pUsuario = Trim(IfNull(pUsuario, ''));

    -- 0=Costo promedio, 1=Precio1, 2=Precio2, 3=Precio3, 4=Precio4, 5=Precio5
    If pValorar is null or pValorar not between 0 and 5 then
        Set pValorar = 0;
    End if;

    Set pLocaliz1 = IfNull(pLocaliz1, '');

    If pLocaliz2 is null or pLocaliz1 = '' then
        Select max(localiz)
        From bodexis
        Where bodega = pBodega
        Into pLocaliz2;
    End if;

    If pRegenerar = 1 then -- Generar conteo nuevo
        Delete from conteo
        Where bodega = pBodega;

        Insert into conteo (
            bodega,
            artcode,
            cantidad,
            artexis,
            artcosp,
            fecha,
            userDigita,
            userAplica,
            movdocu,
            pordesc
        )
        Select
            a.bodega,
            a.artcode,
            0,
            a.artexis,
            Case pValorar
                When 0 then b.artcosp
                When 1 then b.artpre1
                When 2 then b.artpre2
                When 3 then b.artpre3
                When 4 then b.artpre4
                Else b.artpre5
            End,
            now(),
            pUsuario,
            '',
            '',
            pPordesc
        From bodexis a
        Inner join inarticu b on a.artcode = b.artcode
        Where a.bodega = pBodega
          and a.localiz between pLocaliz1 and pLocaliz2;
    Else -- Actualizar el conteo existente
        Update conteo, bodexis, inarticu b
        Set conteo.artexis = bodexis.artexis,
            conteo.artcosp = Case pValorar
                When 0 then b.artcosp
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
        Insert into conteo (
            bodega,
            artcode,
            cantidad,
            artexis,
            artcosp,
            fecha,
            userDigita,
            userAplica,
            movdocu,
            pordesc
        )
        Select
            a.bodega,
            a.artcode,
            0,
            a.artexis,
            Case pValorar
                When 0 then b.artcosp
                When 1 then b.artpre1
                When 2 then b.artpre2
                When 3 then b.artpre3
                When 4 then b.artpre4
                Else b.artpre5
            End,
            now(),
            pUsuario,
            '',
            '',
            pPordesc
        From bodexis a
        Inner join inarticu b on a.artcode = b.artcode
        Where a.bodega = pBodega
          and a.localiz between pLocaliz1 and pLocaliz2
          and not Exists (
              Select c.artcode
              From conteo c
              Where c.bodega = a.bodega
                and c.artcode = a.artcode
          );

        # Actualizar el campo de orden y el usuario
        Update conteo
        Set pordesc = pPordesc,
            userDigita = pUsuario
        Where bodega = pBodega;
    End if;

    # Contar los registros generados
    Select count(*) as registros
    From conteo
    Where bodega = pBodega;
END$$
delimiter ;