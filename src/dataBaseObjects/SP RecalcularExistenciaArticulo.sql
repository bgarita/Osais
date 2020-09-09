
DROP PROCEDURE if EXISTS RecalcularExistenciaArticulo;

Delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `RecalcularExistenciaArticulo`(
	IN `pArtcode` varchar(20),
	IN `pBodega` varchar(3),
	IN `pFecha` datetime
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN

    -- Autor: Bosco Garita Azofeifa
    
    -- NOTA: No se recomienda correr este SP contra todo el catálogo de artículos ya que se vuelve muy lento.
    
    
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
    

    Select 
    		IfNull(mescerrado,1),
		IfNull(anocerrado,1900) 
    from config 
    into vMesCerrado, vAnoCerrado;

    Set vUltimoCierre = UltimoDiaDelMes(vMesCerrado,vAnoCerrado);


    Set @vAnterior = 0;

    Select artexis from hbodexis
    Where bodega = pBodega
    and artcode = pArtcode
    and artperi = vUltimoCierre into @vAnterior;

    Set @vEntradas = 0;
    Set @vSalidas  = 0;

    START TRANSACTION;
    
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

Delimiter ;