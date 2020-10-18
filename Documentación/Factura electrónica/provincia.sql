/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50714
Source Host           : 127.0.0.1:3306
Source Database       : pos

Target Server Type    : MYSQL
Target Server Version : 50714
File Encoding         : 65001

Date: 2018-04-07 10:16:47
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for provincia
-- ----------------------------
DROP TABLE IF EXISTS `provincia`;
CREATE TABLE `provincia` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `codigo` int(2) DEFAULT NULL,
  `provincia` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of provincia
-- ----------------------------
INSERT INTO `provincia` VALUES ('1', '1', 'San José');
INSERT INTO `provincia` VALUES ('2', '2', 'Alajuela');
INSERT INTO `provincia` VALUES ('3', '3', 'Cartago');
INSERT INTO `provincia` VALUES ('4', '4', 'Heredia');
INSERT INTO `provincia` VALUES ('5', '5', 'Guanacaste');
INSERT INTO `provincia` VALUES ('6', '6', 'Puntarenas');
INSERT INTO `provincia` VALUES ('7', '7', 'Limón');
