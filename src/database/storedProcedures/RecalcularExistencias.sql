
DROP PROCEDURE if EXISTS RecalcularExistencias;

Delimiter $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `RecalcularExistencias`(
	IN `pFecha` datetime,
	IN `pCierre` tinyint(1)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN

    # Autor:    Bosco Garita año 2010
    # Objet:    Recalcular las existencias a una fecha dada
    #           Cuando es invocado con un 1 en el parámetro pCierre no se usa el Start transaction ni el commit
    #           ya que el proceso que lo invoca debe hacerlo dentro de una transacción.  Se usa de esta manera
    #           porque cuando no es cierre es necesario que no bloquee por mucho tiempo pero cuando es cierre
    #           es todo lo contrario.  Más bien en ese momento no debe haber nadie emitiendo movimientos.
               

    Declare vRango1   varchar(23);    
    Declare vRango2   varchar(23);
    Declare vPuntoP   datetime;
    Declare vMesCerrado smallInt;
    Declare vAnoCerrado int;
    Declare vUltimoCierre datetime;
    

    # Establecer los valores default para los parámetros
    Set pFecha = IfNull(pFecha,now());
    
    If pCierre is null or pCierre not in (0,1) then
        Set pCierre = 0;
    End if;


    # Establecer el rango de artículos
    Set vRango1 = (Select Min(Concat(artcode,bodega)) from bodexis);
    Set vRango2 = (Select Max(Concat(artcode,bodega)) from bodexis);


    # Establecer el punto de partida
    Select IfNull(mescerrado,1),IfNull(anocerrado,1900) from config into vMesCerrado, vAnoCerrado;
    Set vUltimoCierre = UltimoDiaDelMes(vMesCerrado,vAnoCerrado);

	-- Bosco agregado 05/07/2015
	# Obtener el set de registros de movimientos que será consultado durante el ciclo.
	Create temporary table detMov(
		SELECT
			 inmovimd.movtimo,
			 inmovimd.movtido,
			 inmovimd.movcant,
			 inmovimd.artcode,
			 inmovimd.bodega
		FROM inmovimd
		INNER JOIN inmovime ON inmovimd.movdocu = inmovime.movdocu
			AND inmovimd.movtimo = inmovime.movtimo
			AND inmovimd.movtido = inmovime.movtido
		WHERE movfech > vUltimoCierre AND movfech <= pFecha
		AND (inmovime.estado IS NULL OR inmovime.estado = ''));

	CREATE INDEX ix_detMov ON detMov (artcode,bodega);
	-- Fin Bosco agregado 05/07/2015

    # El recorrido se hace registro por registro para evitar el bloqueo de toda la tabla.
    -- Uso mi técnica personal de recorrido por mínimos (Bosco).
    While vRango1 <= vRango2 Do
    
        If not pCierre then

            START TRANSACTION;

        End if;

        Set @vAnterior = 0;

        # Obtener el saldo al último cierre.
        Select artexis from hbodexis
        Where bodega = substring(vRango1,Length(trim(artcode))+1,3)
        and artcode = substring(vRango1,1,Length(trim(artcode)))
        and artperi = vUltimoCierre into @vAnterior;

        Set @vEntradas = 0;
        Set @vSalidas  = 0;


		# Obtener la suma de entradas y salidas para el período solicitado.
        SELECT
             Sum(If(movtimo = 'E',movcant,0)),
             Sum(If(movtimo = 'S',movcant,0))
        FROM detMov
        WHERE artcode = substring(vRango1,1,Length(trim(artcode)))
        AND bodega = substring(vRango1,Length(trim(artcode))+1,3)
        INTO @vEntradas,@vSalidas;

        Set @vAnterior = IfNull(@vAnterior, 0);
        Set @vEntradas = IfNull(@vEntradas, 0);
        Set @vSalidas  = IfNull(@vSalidas , 0);

        # Actualizar el registro
	   Update bodexis Set
            artexis = @vAnterior + @vEntradas - @vSalidas
        Where bodega = substring(vRango1,Length(trim(artcode))+1,3)
        and artcode = substring(vRango1,1,Length(trim(artcode)));

        If not pCierre then

            COMMIT;

        End if;

        # Siguiente registro
        Set vRango1 = ( Select Min(Concat(artcode,bodega))
                        from bodexis
                        Where Concat(artcode,bodega) > vRango1);

    End While;

	Drop temporary table If Exists detMov;

END$$
Delimiter ;