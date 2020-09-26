/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50714
Source Host           : 127.0.0.1:3306
Source Database       : pos

Target Server Type    : MYSQL
Target Server Version : 50714
File Encoding         : 65001

Date: 2018-04-07 10:16:57
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for canton
-- ----------------------------
DROP TABLE IF EXISTS `canton`;
CREATE TABLE `canton` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idProvincia` int(1) unsigned zerofill NOT NULL,
  `codigo` int(2) unsigned zerofill NOT NULL,
  `canton` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of canton
-- ----------------------------
INSERT INTO `canton` VALUES ('1', '1', '01', 'San José');
INSERT INTO `canton` VALUES ('2', '1', '02', 'Escazú');
INSERT INTO `canton` VALUES ('3', '1', '03', 'Desamparados');
INSERT INTO `canton` VALUES ('4', '1', '04', 'Puriscal');
INSERT INTO `canton` VALUES ('5', '1', '05', 'Tarrazú');
INSERT INTO `canton` VALUES ('6', '1', '06', 'Aserrí');
INSERT INTO `canton` VALUES ('7', '1', '07', 'Mora');
INSERT INTO `canton` VALUES ('8', '1', '08', 'Goicoechea');
INSERT INTO `canton` VALUES ('9', '1', '09', 'Santa Ana');
INSERT INTO `canton` VALUES ('10', '1', '10', 'Alajuelita');
INSERT INTO `canton` VALUES ('11', '1', '11', 'Vásquez de Coronado');
INSERT INTO `canton` VALUES ('12', '1', '12', 'Acosta');
INSERT INTO `canton` VALUES ('13', '1', '13', 'Tibás');
INSERT INTO `canton` VALUES ('14', '1', '14', 'Moravia');
INSERT INTO `canton` VALUES ('15', '1', '15', 'Montes de Oca');
INSERT INTO `canton` VALUES ('16', '1', '16', 'Turrubares');
INSERT INTO `canton` VALUES ('17', '1', '17', 'Dota');
INSERT INTO `canton` VALUES ('18', '1', '18', 'Curridabat');
INSERT INTO `canton` VALUES ('19', '1', '19', 'Pérez Zeledón');
INSERT INTO `canton` VALUES ('20', '1', '20', 'León Cortéz Castro');
INSERT INTO `canton` VALUES ('21', '2', '01', 'Alajuela');
INSERT INTO `canton` VALUES ('22', '2', '02', 'San Ramón');
INSERT INTO `canton` VALUES ('23', '2', '03', 'Grecia');
INSERT INTO `canton` VALUES ('24', '2', '04', 'San Mateo');
INSERT INTO `canton` VALUES ('25', '2', '05', 'Atenas');
INSERT INTO `canton` VALUES ('26', '2', '06', 'Naranjo');
INSERT INTO `canton` VALUES ('27', '2', '07', 'Palmares');
INSERT INTO `canton` VALUES ('28', '2', '08', 'Poás');
INSERT INTO `canton` VALUES ('29', '2', '09', 'Orotina');
INSERT INTO `canton` VALUES ('30', '2', '10', 'San Carlos');
INSERT INTO `canton` VALUES ('31', '2', '11', 'Zarcero');
INSERT INTO `canton` VALUES ('32', '2', '12', 'Valverde Vega');
INSERT INTO `canton` VALUES ('33', '2', '13', 'Upala');
INSERT INTO `canton` VALUES ('34', '2', '14', 'Los Chiles');
INSERT INTO `canton` VALUES ('35', '2', '15', 'Guatuso');
INSERT INTO `canton` VALUES ('36', '3', '01', 'Cartago');
INSERT INTO `canton` VALUES ('37', '3', '02', 'Paraíso');
INSERT INTO `canton` VALUES ('38', '3', '03', 'La Unión');
INSERT INTO `canton` VALUES ('39', '3', '04', 'Jiménez');
INSERT INTO `canton` VALUES ('40', '3', '05', 'Turrialba');
INSERT INTO `canton` VALUES ('41', '3', '06', 'Alvarado');
INSERT INTO `canton` VALUES ('42', '3', '07', 'Oreamuno');
INSERT INTO `canton` VALUES ('43', '3', '08', 'El Guarco');
INSERT INTO `canton` VALUES ('44', '4', '01', 'Heredia');
INSERT INTO `canton` VALUES ('45', '4', '02', 'Barva');
INSERT INTO `canton` VALUES ('46', '4', '03', 'Santo Domingo');
INSERT INTO `canton` VALUES ('47', '4', '04', 'Santa Bárbara');
INSERT INTO `canton` VALUES ('48', '4', '05', 'San Rafaél');
INSERT INTO `canton` VALUES ('49', '4', '06', 'San Isidro');
INSERT INTO `canton` VALUES ('50', '4', '07', 'Belén');
INSERT INTO `canton` VALUES ('51', '4', '08', 'Flores');
INSERT INTO `canton` VALUES ('52', '4', '09', 'San Pablo');
INSERT INTO `canton` VALUES ('53', '4', '10', 'Sarapiquí');
INSERT INTO `canton` VALUES ('54', '5', '01', 'Liberia');
INSERT INTO `canton` VALUES ('55', '5', '02', 'Nicoya');
INSERT INTO `canton` VALUES ('56', '5', '03', 'Santa Cruz');
INSERT INTO `canton` VALUES ('57', '5', '04', 'Bagaces');
INSERT INTO `canton` VALUES ('58', '5', '05', 'Carrillo');
INSERT INTO `canton` VALUES ('59', '5', '06', 'Cañas');
INSERT INTO `canton` VALUES ('60', '5', '07', 'Abangáres');
INSERT INTO `canton` VALUES ('61', '5', '08', 'Tilarán');
INSERT INTO `canton` VALUES ('62', '5', '09', 'Nandayure');
INSERT INTO `canton` VALUES ('63', '5', '10', 'La Cruz');
INSERT INTO `canton` VALUES ('64', '5', '11', 'Hojancha');
INSERT INTO `canton` VALUES ('65', '6', '01', 'Puntarenas');
INSERT INTO `canton` VALUES ('66', '6', '02', 'Esparza');
INSERT INTO `canton` VALUES ('67', '6', '03', 'Buenos Aires');
INSERT INTO `canton` VALUES ('68', '6', '04', 'Montes de Oro');
INSERT INTO `canton` VALUES ('69', '6', '05', 'Osa');
INSERT INTO `canton` VALUES ('70', '6', '06', 'Aguirre');
INSERT INTO `canton` VALUES ('71', '6', '07', 'Golfito');
INSERT INTO `canton` VALUES ('72', '6', '08', 'Coto Brus');
INSERT INTO `canton` VALUES ('73', '6', '09', 'Parrita');
INSERT INTO `canton` VALUES ('74', '6', '10', 'Corredores');
INSERT INTO `canton` VALUES ('75', '6', '11', 'Garabito');
INSERT INTO `canton` VALUES ('76', '7', '01', 'Limón');
INSERT INTO `canton` VALUES ('77', '7', '02', 'Pococí');
INSERT INTO `canton` VALUES ('78', '7', '03', 'Siquirres');
INSERT INTO `canton` VALUES ('79', '7', '04', 'Talamanca');
INSERT INTO `canton` VALUES ('80', '7', '05', 'Matina');
INSERT INTO `canton` VALUES ('81', '7', '06', 'Guácimo');
