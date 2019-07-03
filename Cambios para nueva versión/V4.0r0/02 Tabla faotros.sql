-- Tabla para los datos de Wallmart
use LaFlor;
CREATE TABLE `faotros` (
  `facnume` INT NOT NULL COMMENT 'Número de factura NC o ND',
  `facnd` INT NOT NULL COMMENT 'Indica si es factura, NC o ND (ver tabla faencabe)',
  `WMNumeroVendedor` VARCHAR(15) NOT NULL COMMENT 'Número de vendedor',
  `WMNumeroOrden` VARCHAR(15) NOT NULL COMMENT 'Número de orden',
  `WMEnviarGLN` VARCHAR(20) NOT NULL,
  `WMNumeroReclamo` VARCHAR(15) NOT NULL COMMENT 'Número de reclamo',
  `WMFechaReclamo` VARCHAR(10) NOT NULL COMMENT 'Fecha del reclamo',
  PRIMARY KEY (`facnume`, `facnd`),
  CONSTRAINT `fk_faotros_faencabe`
    FOREIGN KEY (`facnume` , `facnd`)
    REFERENCES `faencabe` (`facnume` , `facnd`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
