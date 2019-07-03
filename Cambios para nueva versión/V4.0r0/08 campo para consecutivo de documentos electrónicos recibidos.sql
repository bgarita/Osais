use LaFlor;
ALTER TABLE `config` 
ADD COLUMN `docElectProv` INT(11) NOT NULL DEFAULT 0 COMMENT 'Consecutivo de documentos electrónicos recibidos' AFTER `ndebElect`;
