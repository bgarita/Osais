-- USE credito_dev;
        
-- call PRO_Agregar_ecred_documentos('2023-09-01','2023-09-30');

-- Script para conciliación



-- Obtener sugerencia para conciliar bancos vs sistema.
SELECT * 
FROM vsugerencia_conciliacion
WHERE banco_id = 3
AND ref_bancos = '400409645';

SELECT * 
FROM vsugerencia_conciliacion
ORDER BY 1,2


 select
        sugerencia0_.id_bancos as id_banco1_26_,
        sugerencia0_.banco_id as banco_id2_26_,
        sugerencia0_.banco_sistema_id as banco_si3_26_,
        sugerencia0_.descripcion_bancos as descripc4_26_,
        sugerencia0_.descripcion_sistema as descripc5_26_,
        sugerencia0_.doc_sistema as doc_sist6_26_,
        sugerencia0_.fecha_bancos as fecha_ba7_26_,
        sugerencia0_.fecha_sistema as fecha_si8_26_,
        sugerencia0_.id_sistema as id_siste9_26_,
        sugerencia0_.monto_bancos as monto_b10_26_,
        sugerencia0_.monto_sistema as monto_s11_26_,
        sugerencia0_.nombre_banco as nombre_12_26_,
        sugerencia0_.origen as origen13_26_,
        sugerencia0_.ref_bancos as ref_ban14_26_,
        sugerencia0_.ref_sistema as ref_sis15_26_,
        sugerencia0_.banco_sistema_nombre_banco as banco_s16_26_,
        sugerencia0_.tipo_documento as tipo_do17_26_ 
    from
        vsugerencia_conciliacion sugerencia0_ 
    where
        sugerencia0_.banco_id=3 
        and sugerencia0_.ref_bancos='400409645';

-- Conciliar bancos vs bancos
START TRANSACTION;
UPDATE documento_banco a, documento_banco b
	SET 
		a.ajuste = a.monto, a.justificacion = CONCAT('Movimiento entre cuentas (', a.id, '=', b.id, ')'), a.fecha_conciliado = DATE(NOW()), a.conciliado = 'S',
		b.ajuste = b.monto, b.justificacion = CONCAT('Movimiento entre cuentas (', b.id, '=', a.id, ')'), b.fecha_conciliado = DATE(NOW()), b.conciliado = 'S'
WHERE 
	a.referencia = b.referencia AND 
	a.banco_id = b.banco_id AND 
	a.fecha = b.fecha AND 
	a.monto = b.monto AND 
	a.tipo_documento != b.tipo_documento AND 
	a.conciliado = 'N' AND 
	b.conciliado = 'N';
	
SELECT * 
FROM documento_banco a, documento_banco b
WHERE 
	a.referencia = b.referencia AND 
	a.banco_id = b.banco_id AND 
	a.fecha = b.fecha AND 
	a.monto = b.monto AND 
	a.tipo_documento != b.tipo_documento AND 
	a.conciliado = 'S' AND 
	b.conciliado = 'S';
	
	-- ROLLBACK;