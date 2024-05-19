-- --------------------------------------------------------
-- Host:                         DESKTOP-T8NBJH5
-- Versión del servidor:         11.0.2-MariaDB - mariadb.org binary distribution
-- SO del servidor:              Win64
-- HeidiSQL Versión:             12.5.0.6677
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

USE credito_dev;

-- Volcando estructura para tabla ecred.documento_banco
CREATE TABLE IF NOT EXISTS `documento_banco` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `referencia` varchar(30) NOT NULL DEFAULT '' COMMENT 'Referencia del banco',
  `tipo_documento` varchar(2) NOT NULL DEFAULT '' COMMENT 'DB=débito, CR=crédito',
  `fecha` date NOT NULL,
  `banco_id` int(11) NOT NULL DEFAULT -1,
  `monto` decimal(20,6) NOT NULL DEFAULT 0.000000,
  `ajuste` decimal(20,6) NOT NULL DEFAULT 0.000000 COMMENT 'Monto de ajuste para casos especiales',
  `justificacion` varchar(500) NOT NULL DEFAULT '' COMMENT 'Texto que justifica el motivo del ajuste',
  `conciliado` char(1) NOT NULL DEFAULT 'N',
  `conciliado_con` int(11) NOT NULL DEFAULT 0 COMMENT 'Identificador único en la tabla de documento_sistema',
  `fecha_conciliado` date DEFAULT NULL,
  `descripcion` varchar(500) NOT NULL DEFAULT ' ' COMMENT 'Descripción del movimiento según el banco',
  PRIMARY KEY (`id`),
  KEY `FK_banco` (`banco_id`),
  CONSTRAINT `validar_conciliado` CHECK (`conciliado` in ('S','N')),
  CONSTRAINT `validar_tipo_documento` CHECK (`tipo_documento` in ('CR','DB'))
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Documentos registrados en los bancos';

-- Volcando datos para la tabla ecred.documento_banco: ~106 rows (aproximadamente)
INSERT INTO `documento_banco` (`id`, `referencia`, `tipo_documento`, `fecha`, `banco_id`, `monto`, `ajuste`, `justificacion`, `conciliado`, `conciliado_con`, `fecha_conciliado`, `descripcion`) VALUES
	(1, '819146904', 'CR', '2023-07-15', 3, 4766.270000, 0.000000, '', 'N', 0, NULL, 'PAGO INTERES'),
	(2, '360270550', 'CR', '2023-07-20', 3, 339321.100000, 0.000000, '', 'N', 0, NULL, 'DR/CR LINEA SINPE (2023072010424010088102231 - Pago varios creditos)'),
	(3, '406406062', 'CR', '2023-07-08', 3, 9266.150000, 0.000000, '', 'N', 0, NULL, 'TEF DE: 703618348'),
	(4, '73169669', 'CR', '2023-07-31', 3, 123.600000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(5, '49320', 'DB', '2023-07-01', 3, 1938.000000, 0.000000, '', 'N', 0, NULL, 'SEGURO CPVC28596'),
	(6, '70106637', 'CR', '2023-07-01', 3, 42.040000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(7, '70297139', 'CR', '2023-07-02', 3, 42.040000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(8, '70394821', 'CR', '2023-07-03', 3, 42.040000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(9, '70487392', 'CR', '2023-07-04', 3, 42.040000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(10, '70579534', 'CR', '2023-07-05', 3, 42.040000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(11, '70680536', 'CR', '2023-07-06', 3, 42.040000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(12, '70785567', 'CR', '2023-07-07', 3, 42.040000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(13, '406408360', 'DB', '2023-07-08', 3, 632118.730000, 0.000000, '', 'N', 0, NULL, 'TEF A : 703618348'),
	(14, '70881580', 'CR', '2023-07-08', 3, 38.580000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(15, '70974599', 'CR', '2023-07-09', 3, 38.580000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(16, '71078564', 'CR', '2023-07-10', 3, 38.580000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(17, '71172140', 'CR', '2023-07-11', 3, 38.580000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(18, '71269263', 'CR', '2023-07-12', 3, 38.580000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(19, '71394194', 'CR', '2023-07-13', 3, 38.580000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(20, '71409805', 'CR', '2023-07-14', 3, 38.580000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(21, '71511295', 'CR', '2023-07-15', 3, 38.580000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(22, '71600830', 'CR', '2023-07-16', 3, 38.580000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(23, '71793628', 'CR', '2023-07-17', 3, 38.580000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(24, '71890478', 'CR', '2023-07-18', 3, 38.580000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(25, '71982123', 'CR', '2023-07-19', 3, 38.580000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(26, '666488685', 'DB', '2023-07-20', 3, 339321.100000, 0.000000, '', 'N', 0, NULL, 'DTR:Pago_varios_creditos'),
	(27, '72085688', 'CR', '2023-07-20', 3, 36.720000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(28, '72189288', 'CR', '2023-07-21', 3, 36.720000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(29, '39847', 'DB', '2023-07-22', 3, 601463.650000, 0.000000, '', 'N', 0, NULL, 'PAGO 4027-51**-****-5147'),
	(30, '72285284', 'CR', '2023-07-22', 3, 33.430000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(31, '72379599', 'CR', '2023-07-23', 3, 33.430000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(32, '72473757', 'CR', '2023-07-24', 3, 33.430000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(33, '960449862', 'CR', '2023-07-25', 3, 1294909.540000, 0.000000, '', 'N', 0, NULL, 'CD SINPE THREE M GLOBAL SERVI'),
	(34, '72576367', 'CR', '2023-07-25', 3, 40.530000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(35, '72676967', 'CR', '2023-07-26', 3, 40.530000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(36, '72781545', 'CR', '2023-07-27', 3, 40.530000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(37, '72806743', 'CR', '2023-07-28', 3, 40.530000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(38, '72908045', 'CR', '2023-07-29', 3, 40.530000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(39, '73002598', 'CR', '2023-07-30', 3, 40.530000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(40, '73118070', 'CR', '2023-07-31', 3, 40.530000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(41, '958408028', 'DB', '2023-07-01', 3, 14865.000000, 0.000000, '', 'N', 0, NULL, 'SINPE MOVIL Medismart______'),
	(42, '406401139', 'DB', '2023-07-01', 3, 16000.000000, 0.000000, '', 'N', 0, NULL, 'TEF A : 946094901'),
	(43, '400401799', 'CR', '2023-07-02', 3, 100034.000000, 0.000000, '', 'N', 0, NULL, 'Pago mensual vehículo'),
	(44, '406405086', 'DB', '2023-07-02', 3, 12475.200000, 0.000000, '', 'N', 0, NULL, 'TEF A : 927713552'),
	(45, '958402168', 'DB', '2023-07-08', 3, 60000.000000, 0.000000, '', 'N', 0, NULL, 'SINPE MOVIL Prestamo_personal_'),
	(46, '958883535', 'DB', '2023-07-14', 3, 500.000000, 0.000000, '', 'N', 0, NULL, 'SINPE MOVIL Prueba_Kash____'),
	(47, '966420953', 'DB', '2023-07-14', 3, 35000.000000, 0.000000, '', 'N', 0, NULL, 'SINPE MOVIL Prestamo_person'),
	(48, '958451623', 'DB', '2023-07-15', 3, 70000.000000, 0.000000, '', 'N', 0, NULL, 'SINPE MOVIL Pago_muebles___'),
	(49, '39757', 'DB', '2023-07-22', 3, 27398.650000, 0.000000, '', 'N', 0, NULL, 'PAGO 3777-11**-****-4806'),
	(50, '39763', 'DB', '2023-07-22', 3, 2488.800000, 0.000000, '', 'N', 0, NULL, 'PAGO 3777-11**-****-7791'),
	(51, '39760', 'DB', '2023-07-22', 3, 9249.240000, 0.000000, '', 'N', 0, NULL, 'PAGO 4027-51**-****-5147'),
	(52, '958403633', 'DB', '2023-07-25', 3, 40000.000000, 0.000000, '', 'N', 0, NULL, 'SINPE MOVIL Prestamo_person'),
	(53, '110802479', 'CR', '2023-07-31', 3, 250000.000000, 0.000000, '', 'N', 0, NULL, 'DEP_ATM_12333              SJO'),
	(54, '406405056', 'DB', '2023-07-31', 3, 12430.000000, 0.000000, '', 'N', 0, NULL, 'TEF A : 927713552'),
	(55, '73176448', 'CR', '2023-07-31', 3, 52.950000, 0.000000, '', 'N', 0, NULL, 'INTERESES'),
	(56, '55814625', 'CR', '2023-07-31', 1, 54600.000000, 0.000000, '', 'N', 0, NULL, 'TRANSFERENCIA SINPE/RETANA GONZALEZ JOSE'),
	(57, '95362817', 'CR', '2023-07-31', 1, 28264.000000, 0.000000, '', 'N', 0, NULL, 'OPERACI N 101/CORDOBA CARRANZA AND'),
	(58, '95205427', 'CR', '2023-07-31', 1, 30000.000000, 0.000000, '', 'N', 0, NULL, 'EUGENIA SOTO SANDOVA/CHAVES SOTO FABIO AN'),
	(59, '15204524', 'CR', '2023-07-31', 1, 100000.000000, 0.000000, '', 'N', 0, NULL, 'GARITA AZOFEIFA JESU 31/152-BCR   '),
	(60, '55578727', 'CR', '2023-07-31', 1, 45620.000000, 0.000000, '', 'N', 0, NULL, 'OPERACION102/MARIA SANCHEZ MESEN'),
	(61, '50932126', 'CR', '2023-07-31', 1, 37824.000000, 0.000000, '', 'N', 0, NULL, 'SIN DESCRIPCION/OLMAN CAMPOS VALVERD'),
	(62, '14788733', 'DB', '2023-07-31', 1, 100000.000000, 0.000000, '', 'N', 0, NULL, 'PAGO REC TELED 10007771/BOSCO GARITA AZOFEIF'),
	(63, '57364184', 'CR', '2023-07-31', 1, 51000.000000, 0.000000, '', 'N', 0, NULL, 'TRANSFERENCIA POR BN CELULAR/ESMERALDA ROSALES BA'),
	(64, '92394317', 'CR', '2023-07-31', 1, 20000.000000, 0.000000, '', 'N', 0, NULL, 'PR STAMO/CARLOS ENRIQUE SANCH'),
	(65, '52350601', 'CR', '2023-07-31', 1, 70000.000000, 0.000000, '', 'N', 0, NULL, 'MARIA ROSA MESEN COT/RETANA GONZALEZ JOSE'),
	(66, '94627667', 'CR', '2023-07-31', 1, 8214.000000, 0.000000, '', 'N', 0, NULL, 'ZOM/YENORY PORTUGUEZ MON'),
	(67, '52461927', 'CR', '2023-07-31', 1, 5000.000000, 0.000000, '', 'N', 0, NULL, 'RETIRO/MARIA LUISA LOREDO R'),
	(68, '94394935', 'DB', '2023-07-31', 1, 25000.000000, 0.000000, '', 'N', 0, NULL, 'PAGO REC TELED 10007771/BOSCO GARITA AZOFEIF'),
	(69, '94394462', 'DB', '2023-07-31', 1, 25000.000000, 0.000000, '', 'N', 0, NULL, 'PAGO REC TELED 10007771/BOSCO GARITA AZOFEIF'),
	(70, '51169988', 'CR', '2023-07-28', 1, 50000.000000, 0.000000, '', 'N', 0, NULL, 'PAGOJOSE/MARIA SANCHEZ MESEN'),
	(71, '51158714', 'CR', '2023-07-28', 1, 15895.000000, 0.000000, '', 'N', 0, NULL, 'SIN DESCRIPCION/MARIA SANCHEZ MESEN'),
	(72, '55589402', 'CR', '2023-07-26', 1, 85595.000000, 0.000000, '', 'N', 0, NULL, 'PRICESMART PENDIENTE/AGUERO NAVARRO EDWIN'),
	(73, '98293252', 'DB', '2023-07-26', 1, 25000.000000, 0.000000, '', 'N', 0, NULL, 'PAGO REC TELED 10007771/BOSCO GARITA AZOFEIF'),
	(74, '55888367', 'CR', '2023-07-25', 1, 95000.000000, 0.000000, '', 'N', 0, NULL, 'PRESTAMOOLMANGRAND/MARIA SANCHEZ MESEN'),
	(75, '94013179', 'DB', '2023-07-21', 1, 25000.000000, 0.000000, '', 'N', 0, NULL, 'PAGO REC TELED 10007771/BOSCO GARITA AZOFEIF'),
	(76, '94010867', 'DB', '2023-07-21', 1, 25000.000000, 0.000000, '', 'N', 0, NULL, 'PAGO REC TELED 10007771/BOSCO GARITA AZOFEIF'),
	(77, '94009852', 'DB', '2023-07-21', 1, 25000.000000, 0.000000, '', 'N', 0, NULL, 'PAGO REC TELED 10007771/BOSCO GARITA AZOFEIF'),
	(78, '54079866', 'CR', '2023-07-18', 1, 75000.000000, 0.000000, '', 'N', 0, NULL, 'SIN DESCRIPCION/OLMAN CAMPOS VALVERD'),
	(79, '51469331', 'CR', '2023-07-18', 1, 18520.000000, 0.000000, '', 'N', 0, NULL, 'SIN DESCRIPCION/OLMAN CAMPOS VALVERD'),
	(80, '90480066', 'DB', '2023-07-18', 1, 25000.000000, 0.000000, '', 'N', 0, NULL, 'PAGO REC TELED 10007771/BOSCO GARITA AZOFEIF'),
	(81, '90479280', 'DB', '2023-07-18', 1, 25000.000000, 0.000000, '', 'N', 0, NULL, 'PAGO REC TELED 10007771/BOSCO GARITA AZOFEIF'),
	(82, '25151203', 'CR', '2023-07-17', 1, 82700.000000, 0.000000, '', 'N', 0, NULL, 'PAGO 16 07 2023/SINDY SANCHEZ'),
	(83, '58267433', 'CR', '2023-07-17', 1, 5000.000000, 0.000000, '', 'N', 0, NULL, 'RETIRO/MARIA LUISA LOREDO R'),
	(84, '51589188', 'CR', '2023-07-14', 1, 26815.000000, 0.000000, '', 'N', 0, NULL, 'PRESTAMORAPIDO110/MARIA SANCHEZ MESEN'),
	(85, '50514768', 'CR', '2023-07-14', 1, 500.000000, 0.000000, '', 'N', 0, NULL, 'PRUEBA KASH/BOSCO EVELIO GARITA '),
	(86, '90126023', 'DB', '2023-07-10', 1, 25000.000000, 0.000000, '', 'N', 0, NULL, 'PAGO REC TELED 10007771/BOSCO GARITA AZOFEIF'),
	(87, '97888019', 'DB', '2023-07-05', 1, 25000.000000, 0.000000, '', 'N', 0, NULL, 'PAGO REC TELED 10007771/BOSCO GARITA AZOFEIF'),
	(88, '97887690', 'DB', '2023-07-05', 1, 25000.000000, 0.000000, '', 'N', 0, NULL, 'PAGO REC TELED 10007771/BOSCO GARITA AZOFEIF'),
	(89, '58988267', 'CR', '2023-07-05', 1, 10000.000000, 0.000000, '', 'N', 0, NULL, 'PARA EL RETIRO/MARIA LUISA LOREDO R'),
	(90, '96598536', 'CR', '2023-07-04', 1, 230.000000, 0.000000, '', 'N', 0, NULL, 'PAGO DE OPERACI N/CORDOBA CARRANZA AND'),
	(91, '94525641', 'DB', '2023-07-04', 1, 133500.000000, 0.000000, '', 'N', 0, NULL, 'PAGO CEL/BNCR'),
	(92, '95929599', 'CR', '2023-07-04', 1, 90000.000000, 0.000000, '', 'N', 0, NULL, 'RECIBOS MAR A/MONGE VILLALOBOS JEA'),
	(93, '95821638', 'CR', '2023-07-03', 1, 200.000000, 0.000000, '', 'N', 0, NULL, 'PAGO DE OPERACI N/CORDOBA CARRANZA AND'),
	(94, '94686062', 'DB', '2023-07-03', 1, 25000.000000, 0.000000, '', 'N', 0, NULL, 'PAGO REC TELED 10007771/BOSCO GARITA AZOFEIF'),
	(95, '15443576', 'DB', '2023-07-03', 1, 100000.000000, 0.000000, '', 'N', 0, NULL, 'PAGO REC TELED 10007771/BOSCO GARITA AZOFEIF'),
	(96, '15442104', 'DB', '2023-07-03', 1, 13545.210000, 0.000000, '', 'N', 0, NULL, 'PAGO MSJ 107660219/BOSCO GARITA AZOFEIF'),
	(97, '15441159', 'DB', '2023-07-03', 1, 33887.700000, 0.000000, '', 'N', 0, NULL, 'PAGO MSJ 302880569/BOSCO GARITA AZOFEIF'),
	(98, '93107479', 'DB', '2023-07-03', 1, 42000.000000, 0.000000, '', 'N', 0, NULL, 'PAGOS VARIOS/MONGE VILLALOBOS JEA'),
	(99, '58460618', 'CR', '2023-07-03', 1, 8000.000000, 0.000000, '', 'N', 0, NULL, 'PARA LA HERMANA/CASCANTE MORALES OLG'),
	(100, '23879470', 'CR', '2023-07-03', 1, 151000.000000, 0.000000, '', 'N', 0, NULL, 'PRESTAMO/SINDY SANCHEZ'),
	(101, '14029209', 'DB', '2023-07-03', 1, 12000.000000, 0.000000, '', 'N', 0, NULL, 'PAGO SEGUN ESTADO DE CUENTA/COOPECAJA R.L. CTA.C'),
	(102, '53660842', 'CR', '2023-07-03', 1, 38000.000000, 0.000000, '', 'N', 0, NULL, 'SIN DESCRIPCION/OLMAN CAMPOS VALVERD'),
	(103, '56311423', 'CR', '2023-07-03', 1, 1000.000000, 0.000000, '', 'N', 0, NULL, 'TRANSFERENCIA SINPE/RETANA GONZALEZ JOSE'),
	(104, '52645378', 'CR', '2023-07-03', 1, 600.000000, 0.000000, '', 'N', 0, NULL, 'PAGOPRESTAMO/MARIA SANCHEZ MESEN'),
	(105, '52639432', 'CR', '2023-07-03', 1, 46000.000000, 0.000000, '', 'N', 0, NULL, 'PAGOPRESTAMO/MARIA SANCHEZ MESEN'),
	(106, '774240', 'CR', '2023-07-03', 1, 748.650000, 0.000000, '', 'N', 0, NULL, 'INTERESES GANADOS EN SU CUENTA DE AHORRO/BNCR');

-- Volcando estructura para tabla ecred.documento_sistema
CREATE TABLE IF NOT EXISTS `documento_sistema` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `documento` varchar(15) NOT NULL DEFAULT '',
  `tipo_documento` varchar(2) NOT NULL DEFAULT '' COMMENT 'DB=débito, CR=crédito',
  `fecha` date NOT NULL,
  `banco_id` int(11) NOT NULL DEFAULT -1,
  `referencia` varchar(30) NOT NULL DEFAULT '' COMMENT 'Referencia del banco',
  `monto` decimal(20,6) NOT NULL DEFAULT 0.000000,
  `ajuste` decimal(20,6) NOT NULL DEFAULT 0.000000 COMMENT 'Monto de ajuste para casos especiales',
  `justificacion` varchar(500) NOT NULL DEFAULT '' COMMENT 'Texto que justifica el motivo del ajuste',
  `conciliado` char(1) NOT NULL DEFAULT 'N',
  `origen` varchar(20) NOT NULL DEFAULT '' COMMENT 'Opciones: Pago crédito, Pago ahorro, Desembolso, etc',
  `conciliado_con` int(11) NOT NULL DEFAULT 0 COMMENT 'Identificador único en la tabla de documento_banco',
  `fecha_conciliado` date DEFAULT NULL,
  `descripcion` varchar(500) NOT NULL DEFAULT ' ' COMMENT 'Normalmente viene el nombre completo del cliente',
  PRIMARY KEY (`id`),
  KEY `FK_banco` (`banco_id`),
  CONSTRAINT `validar_origen` CHECK (`origen` in ('Pago crédito','Pago ahorro','Desembolso')),
  CONSTRAINT `validar_conciliado` CHECK (`conciliado` in ('S','N')),
  CONSTRAINT `validar_tipo_documento` CHECK (`tipo_documento` in ('CR','DB'))
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Documentos del sistema';

-- Volcando datos para la tabla ecred.documento_sistema: ~13 rows (aproximadamente)
INSERT INTO `documento_sistema` (`id`, `documento`, `tipo_documento`, `fecha`, `banco_id`, `referencia`, `monto`, `ajuste`, `justificacion`, `conciliado`, `origen`, `conciliado_con`, `fecha_conciliado`, `descripcion`) VALUES
	(19, '103', 'CR', '2023-03-21', 2, '654657', 25109.000000, 0.000000, '', 'N', 'Pago crédito', 0, '2023-09-18', 'Evelio Garita Salgado'),
	(20, '104', 'CR', '2023-03-21', 4, 'N/A', 52586.000000, 0.000000, '', 'N', 'Pago crédito', 0, '2023-09-18', 'Geanina Monge Villalobos'),
	(21, '83', 'CR', '2023-01-02', 2, 'N/A', 76797.960000, 0.000000, '', 'N', 'Pago crédito', 0, '2023-09-18', 'Guisselle Monge Villalobos'),
	(22, '86', 'CR', '2023-02-04', 1, 'N/A', 25000.000000, 0.000000, '', 'N', 'Pago crédito', 0, '2023-09-18', 'Katherin Campos Sánchez'),
	(23, '87', 'CR', '2023-02-04', 1, 'N/A', 4446.780000, 0.000000, '', 'N', 'Pago crédito', 0, '2023-09-18', 'Katherin Campos Sánchez'),
	(24, '88', 'CR', '2023-02-04', 1, 'N/A', 29615.440000, 0.000000, '', 'N', 'Pago crédito', 0, '2023-09-18', 'Katherin Campos Sánchez'),
	(25, '89', 'CR', '2023-02-04', 4, 'N/A', 250000.000000, 0.000000, '', 'N', 'Pago crédito', 0, '2023-09-18', 'Bosco Garita Azofeifa'),
	(26, '90', 'CR', '2023-02-04', 1, 'N/A', 350000.000000, 0.000000, '', 'N', 'Pago crédito', 0, '2023-09-18', 'Isobelina Carranza Suárez'),
	(27, '91', 'CR', '2023-02-04', 1, 'N/A', 2939.870000, 0.000000, '', 'N', 'Pago crédito', 0, '2023-09-18', 'Geanina Monge Villalobos'),
	(28, '92', 'CR', '2023-02-04', 2, 'N/A', 106015.040000, 0.000000, '', 'N', 'Pago crédito', 0, '2023-09-18', 'Rosa Mesén Coto'),
	(29, '3195', 'DB', '2023-03-18', 1, '92', 50000.000000, 0.000000, '', 'N', 'Desembolso', 0, '2023-09-18', 'Bosco Garita Azofeifa'),
	(30, '3197', 'DB', '2023-03-21', 2, '74', 70000.000000, 0.000000, '', 'N', 'Desembolso', 0, '2023-09-18', 'Evelio Garita Salgado'),
	(31, '43', 'CR', '2023-01-28', 3, '1234567', 10000.000000, 0.000000, '', 'N', 'Pago ahorro', 0, '2023-09-18', 'Bosco Garita Azofeifa');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
