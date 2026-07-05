CREATE DEFINER=`root`@`localhost` PROCEDURE `FacturacionVsInventario`(
	OUT `pHayIncongruencias` tinyint(1)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
	# Autor:    Bosco Garita 17/02/2011.
	# Descrip:  Verificar si existen facturas o NC que no estén en la tabla de movimientos de inventario.
	#           Esto no debería suceder pero si por alguna razón sucede el sistema advertirá.
	#           Este SP también se usa como condición para realizar el cierre mensual.
	# Devuelve: Un Result Set con los documentos que no existen en inventarios. Dentro del RS también
	#           irá una columna llamada rownum con el número consecutivo de los registros que puede
	#           servir para ver cuántos registros fueron procesados.  Pero además este SP también
	#           retorna este número de registros mediante el parámetro de salida pHayIncongruencias
	# Acción:   El DBA deberá crear un Script que actualice las tablas INMOVIME e INMOVIMD
	# 
	# Las notas de débito no juegan en esta revisión porque no son documentos de inventario.
	# Modificado por: Bosco Garita Azofeifa 14/04/2013.  
	# Agrego código para que no se revisen los registros anulados.
	# Modificado por: Bosco Garita Azofeifa 05/07/2015
	# Creo un tabla temporal con los registros que se necesitan únicamente.
	# Luego esta tabla la uso en vez de faencabe.

	Create temporary table mov(
	SELECT movdocu + 0 as movdocu FROM inmovime 
                    INNER JOIN intiposdoc ON inmovime.movtido = intiposdoc.movtido
                    WHERE movtimo = 'S'
					AND movCerrado = 'N'
                    AND intiposdoc.modulo = 'CXC' AND EntradaSalida = 'S');

	CREATE INDEX ix_mov ON mov (movdocu);

    	SELECT @rownum:=@rownum+1 rownum, t.* FROM (SELECT @rownum:=0) r, (

    	# Revisar las facturas del periodo actual

	SELECT 
        facnume,
        dtoc(facfech) AS facfech,
        facmont,
        facplazo,
        'Factura' AS tipo
    	FROM faencabe
    	WHERE facnume > 0 AND facnd = 0
    	AND facCerrado = 'N'
	and facestado = ''
    	AND NOT EXISTS( SELECT movdocu FROM mov 
                    WHERE movdocu = faencabe.facnume 
					)
    	UNION ALL
    	# Revisar las notas de crédito del periodo actual
    	SELECT 
        facnume,
        dtoc(facfech) AS facfech,
        facmont,
        facplazo,
        'Nota C' AS tipo 
    	FROM faencabe
    	WHERE facnume < 0 AND facnd > 0
    	AND facCerrado = 'N'
	and facestado = ''	-- Bosco agregado 14/04/2013
	AND NOT EXISTS( SELECT movdocu FROM inmovime 
               INNER JOIN intiposdoc ON inmovime.movtido = intiposdoc.movtido
                    WHERE movdocu = ABS(faencabe.facnume)
                    AND intiposdoc.modulo = 'CXC' and EntradaSalida = 'E')) t;

    	# Verificar si se procesaron registros
	SET pHayIncongruencias = @rownum > 0;
	
	Drop table mov;
END