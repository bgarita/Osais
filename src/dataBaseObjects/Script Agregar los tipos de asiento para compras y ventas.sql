-- Agregar los tipos de asiento para compras y ventas
ALTER TABLE `configcuentas`
	ADD COLUMN `tipo_comp_V` SMALLINT(6) NOT NULL DEFAULT '0' COMMENT 'Tipo de asiento para ventas' AFTER `mostrarfechaRep`,
	ADD COLUMN `tipo_comp_C` SMALLINT(6) NOT NULL DEFAULT '0' COMMENT 'Tipo de asiento para compras' AFTER `tipo_comp_V`;
