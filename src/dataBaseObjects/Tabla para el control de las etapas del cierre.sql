CREATE TABLE `etapascierre` (
	`mes` SMALLINT UNSIGNED NOT NULL COMMENT 'Mes del cierre',
	`ano` SMALLINT UNSIGNED NOT NULL COMMENT 'Año del cierre',
	`usuario` VARCHAR(50) NOT NULL DEFAULT USER() COMMENT 'Usuario que ejecuta el proceso de cierre',
	`etapaconfirmada` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Número de etapa confirmada',
	`fecha` DATETIME NOT NULL DEFAULT SYSDATE() COMMENT 'Fecha y hora de finalizada la etapa',
	PRIMARY KEY (`mes`, `ano`, `etapaconfirmada`)
)
COMMENT='Información sobre las etapas del cierre mensual'
COLLATE='utf8_general_ci'
;