DROP PROCEDURE if EXISTS ReabrirPeriodos;

Delimiter $$

CREATE PROCEDURE `ReabrirPeriodos`(
	IN `yearx` INT,
	IN `monthx` INT
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT 'Re-abrir periodos contables'
BEGIN
	/*
	Autor: Bosco Garita Azofeifa, 01/01/2021
	Descrip: Reabrir periodos contables cerrados.
	Importante: Cuando se abre un periodo que está n periodos antes del actual, todos los n periodos quedarán automáticamente abiertos.
			  Es responsabilidad del contador utilizar este proceso ya que se modificarán datos de informes ya emitidos.
	*/
	
	DECLARE vError INT;
	DECLARE vMensajeErr VARCHAR(5000);
	DECLARE vEtapa INT;
	
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
		GET DIAGNOSTICS CONDITION 1 @sqlstate = RETURNED_SQLSTATE, @errno = MYSQL_ERRNO, @text = MESSAGE_TEXT;
		SET @full_error = CONCAT("ERROR ", @errno, " (", @sqlstate, "): ", @text);
	    	ROLLBACK;
		SET vError = 1;
		SET vMensajeErr = 
		    		CONCAT('[BD] Ocurrió un error en la etapa ', vEtapa, ' del proceso. ' , @full_error);
		SELECT 
			vError AS err,
			vMensajeErr AS msg;
	END;
	
	SET vError = 0;
	SET vMensajeErr = '';
	SET vEtapa = 1;		-- Respaldo de tablas
	
	-- Validar que el periodo solicitado exista.
	if not exists(SELECT descrip FROM coperiodoco WHERE año = YEARx AND mes = MONTHx AND cerrado = 1) then 
		SET vError = 1;
		SET vMensajeErr = '[BD] El periodo solicitado no existe o no está cerrado.';
	END if;
	
	-- Validar que todas las cuentas del catálogo cerrado estén en el catálogo actual.
	if vError = 0 and EXISTS(
				SELECT h.nom_cta
				FROM hcocatalogo h
				LEFT JOIN cocatalogo c ON 
					h.mayor = c.mayor
					AND h.sub_cta = c.sub_cta
					AND h.sub_sub = c.sub_sub
					AND h.colect = c.colect
				WHERE YEAR(h.fecha_cierre) = yearx AND MONTH(h.fecha_cierre) = monthx AND c.mayor IS NULL) then 
		SET vError = 1;
		SET vMensajeErr = '[BD] Existen cuentas que ya no están en el catálogo actual.';
	END if;
	
	-- Crear una copia del periodo actual de varias tablas (eliminar si existe)
	if vError = 0 then
		DROP TABLE if EXISTS cocatalogo_bk;
		CREATE TABLE cocatalogo_bk AS SELECT * FROM cocatalogo;
		
		DROP TABLE if EXISTS coperiodoco_bk;
		CREATE TABLE coperiodoco_bk AS SELECT * FROM coperiodoco;
		
		DROP TABLE if EXISTS coasientoe_bk;
		CREATE TABLE coasientoe_bk AS SELECT * FROM coasientoe;
		
		DROP TABLE if EXISTS coasientod_bk;
		CREATE TABLE coasientod_bk AS SELECT * FROM coasientod;
		
		DROP TABLE if EXISTS hcoasientoe_bk;
		CREATE TABLE hcoasientoe_bk AS SELECT * FROM hcoasientoe;
		
		DROP TABLE if EXISTS hcoasientod_bk;
		CREATE TABLE hcoasientod_bk AS SELECT * FROM hcoasientod;
		
		DROP TABLE if EXISTS cotipasient_bk;
		CREATE TABLE cotipasient_bk AS SELECT * FROM cotipasient;
	END if;
	
	
	START TRANSACTION;
	
	-- Si no hay error continúo con la siguiente etapa
	if vError = 0 then
		SET vEtapa = 2;		-- Marcar los periodos como abiertos
		
		UPDATE coperiodoco 
			SET cerrado = 0
		WHERE (año > YEARx) OR (año = YEARx AND mes >= MONTHx);
	END if;
	
	
	-- Si no hay error continúo con la siguiente etapa
	if vError = 0 then
		SET vEtapa = 3;		-- Trasladar los movimientos de los históricos a las tablas actuales.
		
		-- Se ejecuta este proceso antes que el traslado del catálogo para que el mismo motor determine
		-- si existen cuentas manipuladas, es decir que se hayan cambiado en el histórico arbitrariamente.
		
		/*
		Pasos:
			1. Insertar los registros de hcoasientod a coasientod y de hcoasientoe a coasientoe filtrando las fechas.
			2. Eliminar los registros de hcoasientod y hcoasientoe filtrando las fechas.
		*/
		
		-- Trasladar todos los asientos mayores o iguales al periodo solicitado (exepto los de cierre anual).
		INSERT INTO coasientoe (
			no_comprob,
			fecha_comp,
			no_refer,
			tipo_comp,
			descrip,
			usuario,
			periodo,
			modulo,
			documento,
			movtido,
			enviado,
			asientoAnulado
			)
			SELECT 
				no_comprob,
				fecha_comp,
				no_refer,
				tipo_comp,
				descrip,
				usuario,
				periodo,
				modulo,
				documento,
				movtido,
				enviado,
				asientoAnulado
			FROM hcoasientoe
			WHERE (YEAR(fecha_comp) > YEARx) OR (YEAR(fecha_comp) = yearx and MONTH(fecha_comp) >= MONTHx
			AND no_comprob <> '99999'
			AND tipo_comp <> 99);
			
		INSERT INTO coasientod (	
			 no_comprob,
			 tipo_comp,
			 descrip,
			 db_cr,
			 monto,
			 mayor,
			 sub_cta,
			 sub_sub,
			 colect,
			 idReg
			)
			SELECT 
				 d.no_comprob,
				 d.tipo_comp,
				 d.descrip,
				 d.db_cr,
				 d.monto,
				 d.mayor,
				 d.sub_cta,
				 d.sub_sub,
				 d.colect,
				 d.idReg
			FROM hcoasientod d
			INNER JOIN hcoasientoe e ON d.no_comprob = e.no_comprob AND d.tipo_comp = e.tipo_comp
			WHERE (YEAR(e.fecha_comp) > YEARx) OR (YEAR(e.fecha_comp) = yearx and MONTH(e.fecha_comp) >= MONTHx
			AND e.no_comprob <> '99999'
			AND e.tipo_comp <> 99);
			
		-- Eliminar todos los datos de los registros trasladados y también los que correspnden a cierre anual (si existen).
		DELETE hcoasientod.*, hcoasientoe.*
		FROM hcoasientod, hcoasientoe
		WHERE hcoasientod.no_comprob = hcoasientoe.no_comprob AND hcoasientod.tipo_comp = hcoasientoe.tipo_comp
		AND (YEAR(hcoasientoe.fecha_comp) > YEARx) OR (YEAR(hcoasientoe.fecha_comp) = yearx and MONTH(hcoasientoe.fecha_comp) >= MONTHx);
		
		DELETE FROM hcoasientoe
		WHERE YEAR(fecha_comp) > YEARx OR (YEAR(fecha_comp) = YEARx and MONTH(fecha_comp) >= MONTHx);
		
	END if;
	
	
	-- Si no hay error continúo con la siguiente etapa
	if vError = 0 then
		SET vEtapa = 4;		-- Establecer el catálogo
		/*
		Pasos:
			1. Reemplazar todos los registros del catálogo con la información del histórico (todos los campos exepto las cuentas).
			2. Eliminar del catálogo histórico todos los registros de periodos mayores o iguales al solicitado.
		*/
		UPDATE cocatalogo c, hcocatalogo h
		SET c.nom_cta = h.nom_cta,
			c.nivel = h.nivel,
			c.tipo_cta = h.tipo_cta,
			c.fecha_upd = h.fecha_upd,
			c.ano_anter = h.ano_anter,
			c.db_fecha = h.db_fecha,
			c.cr_fecha = h.cr_fecha,
			c.db_mes = h.db_mes,
			c.cr_mes = h.cr_mes,
			c.nivelc = h.nivelc,
			c.nom_cta = h.nombre,
			c.fecha_c = h.fecha_c,
			c.activa = h.activa
		WHERE h.mayor = c.mayor
		AND h.sub_cta = c.sub_cta
		AND h.sub_sub = c.sub_sub
		AND h.colect  = c.colect
		AND YEAR(h.fecha_cierre) = yearx
		AND MONTH(h.fecha_cierre) = monthx;
		
		DELETE FROM hcocatalogo
		WHERE YEAR(fecha_cierre) = yearx
		AND MONTH(fecha_cierre) = monthx;
	END if;
	
	
	if vError = 0 then
		SET vEtapa = 5;		-- Establecer el nuevo periodo
		UPDATE configcuentas SET mesactual = monthx, añoactual = yearx;
	END if;
	
	-- Si todo sale bien la variable vError tendrá un cero y la variable vMensajeErr estará vacía
	if vError = 1 then
		ROLLBACK;
	ELSE 
		COMMIT;
	END if;
	
	SELECT 
		vError AS err,
		vMensajeErr AS msg;
	
END$$

Delimiter ;