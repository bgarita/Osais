Use LaFlor;
Drop procedure RecalcularExistenciaArticulo;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `RecalcularExistenciaArticulo`(
  IN  `pArtcode`  varchar(20),
  IN  `pBodega`   varchar(3),
  IN  `pFecha`    datetime
)
BEGIN
    -- Autor: Bosco Garita Azofeifa
    
    
    Declare vPuntoP         datetime;
    Declare vMesCerrado     smallInt;
    Declare vAnoCerrado     int;
    Declare vUltimoCierre   datetime;
    Declare vHayError       SmallInt(1);
    Declare vErrorMessage   varchar(1000);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        RollBack;
        Set vHayError = 1;
        Set vErrorMessage = 
            Concat('[BD] No se pudo calcular el artículo ',
            pArtcode, ' para la bodega ', pBodega);
        Select vHayError as Error,vErrorMessage as ErrorMessage;
    END;
    
    Set vHayError = 0;
    Set vErrorMessage = '';
    
    
    Set pFecha = IfNull(pFecha,now());

    
    
    
    
    Select IfNull(mescerrado,1),IfNull(anocerrado,1900) from config into vMesCerrado, vAnoCerrado;
    Set vUltimoCierre = UltimoDiaDelMes(vMesCerrado,vAnoCerrado);

    START TRANSACTION;
    
    Set @vAnterior = 0;
    
    
    Select artexis from hbodexis
    Where artcode = pArtcode
    and bodega = pBodega
    and artperi = vUltimoCierre into @vAnterior;
    
    Set @vEntradas = 0;
    Set @vSalidas  = 0;

    SELECT
         Sum(If(inmovimd.movtimo = 'E',movcant,0)),
         Sum(If(inmovimd.movtimo = 'S',movcant,0))
    FROM inmovimd
    INNER JOIN inmovime ON inmovimd.movdocu = inmovime.movdocu
        AND inmovimd.movtimo = inmovime.movtimo
        AND inmovimd.movtido = inmovime.movtido
    WHERE artcode = pArtcode
    AND bodega = pBodega
    AND movfech > vUltimoCierre AND movfech <= pFecha
    AND (inmovime.estado IS NULL OR inmovime.estado = '')
    INTO @vEntradas,@vSalidas;
    
    Set @vAnterior = IfNull(@vAnterior, 0);
    Set @vEntradas = IfNull(@vEntradas, 0);
    Set @vSalidas  = IfNull(@vSalidas , 0);

    
    Update bodexis Set
        artexis = @vAnterior + @vEntradas - @vSalidas
    Where artcode = pArtcode
    and bodega = pBodega;

    COMMIT;
    Select vHayError as Error,vErrorMessage as ErrorMessage;
END$$
DELIMITER ;

Drop procedure RecalcularExistenciaArticuloSinPuntoPartid;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `RecalcularExistenciaArticuloSinPuntoPartid`(
  IN  `pArtcode`  varchar(20),
  IN  `pBodega`   varchar(3)
)
BEGIN
    
    -- Autor: Bosco Garita Azofeifa
    
    
    Set @vEntradas = 0;
    Set @vSalidas  = 0;

    SELECT
         Sum(If(inmovimd.movtimo = 'E',movcant,0)),
         Sum(If(inmovimd.movtimo = 'S',movcant,0))
    FROM inmovimd
    INNER JOIN inmovime ON inmovimd.movdocu = inmovime.movdocu
        AND inmovimd.movtimo = inmovime.movtimo
        AND inmovimd.movtido = inmovime.movtido
    WHERE artcode = pArtcode
    AND bodega = pBodega
    AND movfech <= now()
    AND (inmovime.estado IS NULL OR inmovime.estado = '')
    INTO @vEntradas,@vSalidas;
    
    Set @vEntradas = IfNull(@vEntradas, 0);
    Set @vSalidas  = IfNull(@vSalidas , 0);

    
    Update bodexis Set
        artexis = @vEntradas - @vSalidas
    Where artcode = pArtcode
    and bodega = pBodega;

END$$
DELIMITER ;

Drop procedure RecalcularExistencias;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `RecalcularExistencias`(
  IN  `pFecha`   datetime,
  IN  `pCierre`  tinyint(1)
)
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

    # El recorrido se hace registro por registro para evitar el bloqueo de toda la tabla
    While vRango1 <= vRango2 Do

        If not pCierre then
            START TRANSACTION;
        End if;
        
        Set @vAnterior = 0;
        
        # Obtener el saldo al último cierre.
        Select artexis from hbodexis
        Where artcode = substring(vRango1,1,Length(trim(artcode)))
        and bodega = substring(vRango1,Length(trim(artcode))+1,3)
        and artperi = vUltimoCierre into @vAnterior;
        
        Set @vEntradas = 0;
        Set @vSalidas  = 0;

		-- Bosco modificado 05/07/2015
		/*
        # Obtener la suma de entradas y salidas para el período solicitado.
        SELECT
             Sum(If(inmovimd.movtimo = 'E',movcant,0)),
             Sum(If(inmovimd.movtimo = 'S',movcant,0))
        FROM inmovimd
        INNER JOIN inmovime ON inmovimd.movdocu = inmovime.movdocu
            AND inmovimd.movtimo = inmovime.movtimo
            AND inmovimd.movtido = inmovime.movtido
        WHERE movfech > vUltimoCierre AND movfech <= pFecha
		and artcode = substring(vRango1,1,Length(trim(artcode)))
        AND bodega = substring(vRango1,Length(trim(artcode))+1,3)
        AND (inmovime.estado IS NULL OR inmovime.estado = '')
        INTO @vEntradas,@vSalidas;
		*/

		# Obtener la suma de entradas y salidas para el período solicitado.
        SELECT
             Sum(If(movtimo = 'E',movcant,0)),
             Sum(If(movtimo = 'S',movcant,0))
        FROM detMov
        WHERE artcode = substring(vRango1,1,Length(trim(artcode)))
        AND bodega = substring(vRango1,Length(trim(artcode))+1,3)
        INTO @vEntradas,@vSalidas;
		-- Fin Bosco modificado 05/07/2015
        

        Set @vAnterior = IfNull(@vAnterior, 0);
        Set @vEntradas = IfNull(@vEntradas, 0);
        Set @vSalidas  = IfNull(@vSalidas , 0);

        # Actualizar el registro
        Update bodexis Set
            artexis = @vAnterior + @vEntradas - @vSalidas
        Where artcode = substring(vRango1,1,Length(trim(artcode)))
        and bodega = substring(vRango1,Length(trim(artcode))+1,3);

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
DELIMITER ;


Drop procedure RecalcularReservado;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `RecalcularReservado`(
  IN `pArtcode` varchar(20)
)
BEGIN
    
    
    
    
    
    Update bodexis
        Set artreserv = IfNull((Select sum(reservado)
                                from pedidod
                                Where artcode = bodexis.artcode
                                and bodega = bodexis.bodega),0)
    Where artcode = If(pArtcode is null, artcode, pArtcode);

    
    Update bodexis
        Set artreserv = artreserv + IfNull((Select sum(faccant)
                                            from wrk_fadetall
                                            Where artcode = bodexis.artcode
                                            and bodega = bodexis.bodega),0)
    Where artcode = If(pArtcode is null, artcode, pArtcode);
    
    
    Update bodexis
        Set artreserv = artreserv + IfNull((Select sum(faccant)
                                            from pedidofd
                                            Where artcode = bodexis.artcode
                                            and bodega = bodexis.bodega),0)
    Where artcode = If(pArtcode is null, artcode, pArtcode);
    
    
    Update bodexis
        Set artreserv = artreserv + IfNull((Select sum(movcant)
                                            from salida
                                            Where artcode = bodexis.artcode
                                            and bodega = bodexis.bodega),0)
    Where artcode = If(pArtcode is null, artcode, pArtcode);
END$$
DELIMITER ;




