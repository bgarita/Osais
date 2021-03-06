/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50714
Source Host           : 127.0.0.1:3306
Source Database       : pos

Target Server Type    : MYSQL
Target Server Version : 50714
File Encoding         : 65001

Date: 2018-04-07 10:17:08
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for distrito
-- ----------------------------
DROP TABLE IF EXISTS `distrito`;
CREATE TABLE `distrito` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idCanton` int(11) unsigned zerofill DEFAULT NULL,
  `codigo` int(2) unsigned zerofill DEFAULT NULL,
  `distrito` varchar(70) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=476 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of distrito
-- ----------------------------
INSERT INTO `distrito` VALUES ('1', '00000000001', '01', 'CARMEN');
INSERT INTO `distrito` VALUES ('2', '00000000001', '02', 'MERCED');
INSERT INTO `distrito` VALUES ('3', '00000000001', '03', 'HOSPITAL');
INSERT INTO `distrito` VALUES ('4', '00000000001', '04', 'CATEDRAL');
INSERT INTO `distrito` VALUES ('5', '00000000001', '05', 'ZAPOTE');
INSERT INTO `distrito` VALUES ('6', '00000000001', '06', 'SAN FRANCISCO DE DOS RÍOS');
INSERT INTO `distrito` VALUES ('7', '00000000001', '07', 'URUCA');
INSERT INTO `distrito` VALUES ('8', '00000000001', '08', 'MATA REDONDA');
INSERT INTO `distrito` VALUES ('9', '00000000001', '09', 'PAVAS');
INSERT INTO `distrito` VALUES ('10', '00000000001', '10', 'HATILLO');
INSERT INTO `distrito` VALUES ('11', '00000000001', '11', 'SAN SEBASTIÁN');
INSERT INTO `distrito` VALUES ('12', '00000000002', '01', 'ESCAZÚ');
INSERT INTO `distrito` VALUES ('13', '00000000002', '02', 'SAN ANTONIO');
INSERT INTO `distrito` VALUES ('14', '00000000002', '03', 'SAN RAFAEL');
INSERT INTO `distrito` VALUES ('15', '00000000003', '01', 'DESAMPARADOS');
INSERT INTO `distrito` VALUES ('16', '00000000003', '02', 'SAN MIGUEL');
INSERT INTO `distrito` VALUES ('17', '00000000003', '03', 'SAN JUAN DE DIOS');
INSERT INTO `distrito` VALUES ('18', '00000000003', '04', 'SAN RAFAEL ARRIBA');
INSERT INTO `distrito` VALUES ('19', '00000000003', '05', 'SAN ANTONIO');
INSERT INTO `distrito` VALUES ('20', '00000000003', '06', 'FRAILES');
INSERT INTO `distrito` VALUES ('21', '00000000003', '07', 'PATARRÁ');
INSERT INTO `distrito` VALUES ('22', '00000000003', '08', 'SAN CRISTÓBAL');
INSERT INTO `distrito` VALUES ('23', '00000000003', '09', 'ROSARIO');
INSERT INTO `distrito` VALUES ('24', '00000000003', '10', 'DAMAS');
INSERT INTO `distrito` VALUES ('25', '00000000003', '11', 'SAN RAFAEL ABAJO');
INSERT INTO `distrito` VALUES ('26', '00000000003', '12', 'GRAVILIAS');
INSERT INTO `distrito` VALUES ('27', '00000000003', '13', 'LOS GUIDO');
INSERT INTO `distrito` VALUES ('28', '00000000004', '01', 'SANTIAGO');
INSERT INTO `distrito` VALUES ('29', '00000000004', '02', 'MERCEDES SUR');
INSERT INTO `distrito` VALUES ('30', '00000000004', '03', 'BARBACOAS');
INSERT INTO `distrito` VALUES ('31', '00000000004', '04', 'GRIFO ALTO');
INSERT INTO `distrito` VALUES ('32', '00000000004', '05', 'SAN RAFAEL');
INSERT INTO `distrito` VALUES ('33', '00000000004', '06', 'CANDELARITA');
INSERT INTO `distrito` VALUES ('34', '00000000004', '07', 'DESAMPARADITOS');
INSERT INTO `distrito` VALUES ('35', '00000000004', '08', 'SAN ANTONIO');
INSERT INTO `distrito` VALUES ('36', '00000000004', '09', 'CHIRES');
INSERT INTO `distrito` VALUES ('37', '00000000005', '01', 'SAN MARCOS');
INSERT INTO `distrito` VALUES ('38', '00000000005', '02', 'SAN LORENZO');
INSERT INTO `distrito` VALUES ('39', '00000000005', '03', 'SAN CARLOS');
INSERT INTO `distrito` VALUES ('40', '00000000006', '01', 'ASERRI');
INSERT INTO `distrito` VALUES ('41', '00000000006', '02', 'TARBACA');
INSERT INTO `distrito` VALUES ('42', '00000000006', '03', 'VUELTA DE JORCO');
INSERT INTO `distrito` VALUES ('43', '00000000006', '04', 'SAN GABRIEL');
INSERT INTO `distrito` VALUES ('44', '00000000006', '05', 'LEGUA');
INSERT INTO `distrito` VALUES ('45', '00000000006', '06', 'MONTERREY');
INSERT INTO `distrito` VALUES ('46', '00000000006', '07', 'SALITRILLOS');
INSERT INTO `distrito` VALUES ('47', '00000000007', '01', 'COLÓN');
INSERT INTO `distrito` VALUES ('48', '00000000007', '02', 'GUAYABO');
INSERT INTO `distrito` VALUES ('49', '00000000007', '03', 'TABARCIA');
INSERT INTO `distrito` VALUES ('50', '00000000007', '04', 'PIEDRAS NEGRAS');
INSERT INTO `distrito` VALUES ('51', '00000000007', '05', 'PICAGRES');
INSERT INTO `distrito` VALUES ('52', '00000000007', '06', 'JARIS');
INSERT INTO `distrito` VALUES ('53', '00000000007', '07', 'QUITIRRISI');
INSERT INTO `distrito` VALUES ('54', '00000000008', '01', 'GUADALUPE');
INSERT INTO `distrito` VALUES ('55', '00000000008', '02', 'SAN FRANCISCO');
INSERT INTO `distrito` VALUES ('56', '00000000008', '03', 'CALLE BLANCOS');
INSERT INTO `distrito` VALUES ('57', '00000000008', '04', 'MATA DE PLÁTANO');
INSERT INTO `distrito` VALUES ('58', '00000000008', '05', 'IPÍS');
INSERT INTO `distrito` VALUES ('59', '00000000008', '06', 'RANCHO REDONDO');
INSERT INTO `distrito` VALUES ('60', '00000000008', '07', 'PURRAL');
INSERT INTO `distrito` VALUES ('61', '00000000009', '01', 'SANTA ANA');
INSERT INTO `distrito` VALUES ('62', '00000000009', '02', 'SALITRAL');
INSERT INTO `distrito` VALUES ('63', '00000000009', '03', 'POZOS');
INSERT INTO `distrito` VALUES ('64', '00000000009', '04', 'URUCA');
INSERT INTO `distrito` VALUES ('65', '00000000009', '05', 'PIEDADES');
INSERT INTO `distrito` VALUES ('66', '00000000009', '06', 'BRASIL');
INSERT INTO `distrito` VALUES ('67', '00000000010', '01', 'ALAJUELITA');
INSERT INTO `distrito` VALUES ('68', '00000000010', '02', 'SAN JOSECITO');
INSERT INTO `distrito` VALUES ('69', '00000000010', '03', 'SAN ANTONIO');
INSERT INTO `distrito` VALUES ('70', '00000000010', '04', 'CONCEPCIÓN');
INSERT INTO `distrito` VALUES ('71', '00000000010', '05', 'SAN FELIPE');
INSERT INTO `distrito` VALUES ('72', '00000000011', '01', 'SAN ISIDRO');
INSERT INTO `distrito` VALUES ('73', '00000000011', '02', 'SAN RAFAEL');
INSERT INTO `distrito` VALUES ('74', '00000000011', '03', 'DULCE NOMBRE DE JESÚS');
INSERT INTO `distrito` VALUES ('75', '00000000011', '04', 'PATALILLO');
INSERT INTO `distrito` VALUES ('76', '00000000011', '05', 'CASCAJAL');
INSERT INTO `distrito` VALUES ('77', '00000000012', '01', 'SAN IGNACIO');
INSERT INTO `distrito` VALUES ('78', '00000000012', '02', 'GUAITIL Villa');
INSERT INTO `distrito` VALUES ('79', '00000000012', '03', 'PALMICHAL');
INSERT INTO `distrito` VALUES ('80', '00000000012', '04', 'CANGREJAL');
INSERT INTO `distrito` VALUES ('81', '00000000012', '05', 'SABANILLAS');
INSERT INTO `distrito` VALUES ('82', '00000000013', '01', 'SAN JUAN');
INSERT INTO `distrito` VALUES ('83', '00000000013', '02', 'CINCO ESQUINAS');
INSERT INTO `distrito` VALUES ('84', '00000000013', '03', 'ANSELMO LLORENTE');
INSERT INTO `distrito` VALUES ('85', '00000000013', '04', 'LEON XIII');
INSERT INTO `distrito` VALUES ('86', '00000000013', '05', 'COLIMA');
INSERT INTO `distrito` VALUES ('87', '00000000014', '01', 'SAN VICENTE');
INSERT INTO `distrito` VALUES ('88', '00000000014', '02', 'SAN JERÓNIMO');
INSERT INTO `distrito` VALUES ('89', '00000000014', '03', 'LA TRINIDAD');
INSERT INTO `distrito` VALUES ('90', '00000000015', '01', 'SAN PEDRO');
INSERT INTO `distrito` VALUES ('91', '00000000015', '02', 'SABANILLA');
INSERT INTO `distrito` VALUES ('92', '00000000015', '03', 'MERCEDES');
INSERT INTO `distrito` VALUES ('93', '00000000015', '04', 'SAN RAFAEL');
INSERT INTO `distrito` VALUES ('94', '00000000016', '01', 'SAN PABLO');
INSERT INTO `distrito` VALUES ('95', '00000000016', '02', 'SAN PEDRO');
INSERT INTO `distrito` VALUES ('96', '00000000016', '03', 'SAN JUAN DE MATA');
INSERT INTO `distrito` VALUES ('97', '00000000016', '04', 'SAN LUIS');
INSERT INTO `distrito` VALUES ('98', '00000000016', '05', 'CARARA');
INSERT INTO `distrito` VALUES ('99', '00000000017', '01', 'SANTA MARÍA');
INSERT INTO `distrito` VALUES ('100', '00000000017', '02', 'JARDÍN');
INSERT INTO `distrito` VALUES ('101', '00000000017', '03', 'COPEY');
INSERT INTO `distrito` VALUES ('102', '00000000018', '01', 'CURRIDABAT');
INSERT INTO `distrito` VALUES ('103', '00000000018', '02', 'GRANADILLA');
INSERT INTO `distrito` VALUES ('104', '00000000018', '03', 'SÁNCHEZ');
INSERT INTO `distrito` VALUES ('105', '00000000018', '04', 'TIRRASES');
INSERT INTO `distrito` VALUES ('106', '00000000019', '01', 'SAN ISIDRO DE EL GENERAL');
INSERT INTO `distrito` VALUES ('107', '00000000019', '02', 'EL GENERAL');
INSERT INTO `distrito` VALUES ('108', '00000000019', '03', 'DANIEL FLORES');
INSERT INTO `distrito` VALUES ('109', '00000000019', '04', 'RIVAS');
INSERT INTO `distrito` VALUES ('110', '00000000019', '05', 'SAN PEDRO');
INSERT INTO `distrito` VALUES ('111', '00000000019', '06', 'PLATANARES');
INSERT INTO `distrito` VALUES ('112', '00000000019', '07', 'PEJIBAYE');
INSERT INTO `distrito` VALUES ('113', '00000000019', '08', 'CAJÓN');
INSERT INTO `distrito` VALUES ('114', '00000000019', '09', 'BARÚ');
INSERT INTO `distrito` VALUES ('115', '00000000019', '10', 'RÍO NUEVO');
INSERT INTO `distrito` VALUES ('116', '00000000019', '11', 'PÁRAMO');
INSERT INTO `distrito` VALUES ('117', '00000000020', '01', 'SAN PABLO');
INSERT INTO `distrito` VALUES ('118', '00000000020', '02', 'SAN ANDRÉS');
INSERT INTO `distrito` VALUES ('119', '00000000020', '03', 'LLANO BONITO');
INSERT INTO `distrito` VALUES ('120', '00000000020', '04', 'SAN ISIDRO');
INSERT INTO `distrito` VALUES ('121', '00000000020', '05', 'SANTA CRUZ');
INSERT INTO `distrito` VALUES ('122', '00000000020', '06', 'SAN ANTONIO');
INSERT INTO `distrito` VALUES ('123', '00000000021', '01', 'ALAJUELA');
INSERT INTO `distrito` VALUES ('124', '00000000021', '02', 'SAN JOSÉ');
INSERT INTO `distrito` VALUES ('125', '00000000021', '03', 'CARRIZAL');
INSERT INTO `distrito` VALUES ('126', '00000000021', '04', 'SAN ANTONIO');
INSERT INTO `distrito` VALUES ('127', '00000000021', '05', 'GUÁCIMA');
INSERT INTO `distrito` VALUES ('128', '00000000021', '06', 'SAN ISIDRO');
INSERT INTO `distrito` VALUES ('129', '00000000021', '07', 'SABANILLA');
INSERT INTO `distrito` VALUES ('130', '00000000021', '08', 'SAN RAFAEL');
INSERT INTO `distrito` VALUES ('131', '00000000021', '09', 'RÍO SEGUNDO');
INSERT INTO `distrito` VALUES ('132', '00000000021', '10', 'DESAMPARADOS');
INSERT INTO `distrito` VALUES ('133', '00000000021', '11', 'TURRÚCARES');
INSERT INTO `distrito` VALUES ('134', '00000000021', '12', 'TAMBOR');
INSERT INTO `distrito` VALUES ('135', '00000000021', '13', 'GARITA');
INSERT INTO `distrito` VALUES ('136', '00000000021', '14', 'SARAPIQUÍ');
INSERT INTO `distrito` VALUES ('137', '00000000022', '01', 'SAN RAMÓN');
INSERT INTO `distrito` VALUES ('138', '00000000022', '02', 'SANTIAGO');
INSERT INTO `distrito` VALUES ('139', '00000000022', '03', 'SAN JUAN');
INSERT INTO `distrito` VALUES ('140', '00000000022', '04', 'PIEDADES NORTE');
INSERT INTO `distrito` VALUES ('141', '00000000022', '05', 'PIEDADES SUR');
INSERT INTO `distrito` VALUES ('142', '00000000022', '06', 'SAN RAFAEL');
INSERT INTO `distrito` VALUES ('143', '00000000022', '07', 'SAN ISIDRO');
INSERT INTO `distrito` VALUES ('144', '00000000022', '08', 'ÁNGELES');
INSERT INTO `distrito` VALUES ('145', '00000000022', '09', 'ALFARO');
INSERT INTO `distrito` VALUES ('146', '00000000022', '10', 'VOLIO');
INSERT INTO `distrito` VALUES ('147', '00000000022', '11', 'CONCEPCIÓN');
INSERT INTO `distrito` VALUES ('148', '00000000022', '12', 'ZAPOTAL');
INSERT INTO `distrito` VALUES ('149', '00000000022', '13', 'PEÑAS BLANCAS');
INSERT INTO `distrito` VALUES ('150', '00000000023', '01', 'GRECIA');
INSERT INTO `distrito` VALUES ('151', '00000000023', '02', 'SAN ISIDRO');
INSERT INTO `distrito` VALUES ('152', '00000000023', '03', 'SAN JOSÉ');
INSERT INTO `distrito` VALUES ('153', '00000000023', '04', 'SAN ROQUE');
INSERT INTO `distrito` VALUES ('154', '00000000023', '05', 'TACARES');
INSERT INTO `distrito` VALUES ('155', '00000000023', '06', 'RÍO CUARTO');
INSERT INTO `distrito` VALUES ('156', '00000000023', '07', 'PUENTE DE PIEDRA');
INSERT INTO `distrito` VALUES ('157', '00000000023', '08', 'BOLÍVAR');
INSERT INTO `distrito` VALUES ('158', '00000000024', '01', 'SAN MATEO');
INSERT INTO `distrito` VALUES ('159', '00000000024', '02', 'DESMONTE');
INSERT INTO `distrito` VALUES ('160', '00000000024', '03', 'JESÚS MARÍA');
INSERT INTO `distrito` VALUES ('161', '00000000024', '04', 'LABRADOR');
INSERT INTO `distrito` VALUES ('162', '00000000025', '01', 'ATENAS');
INSERT INTO `distrito` VALUES ('163', '00000000025', '02', 'JESÚS');
INSERT INTO `distrito` VALUES ('164', '00000000025', '03', 'MERCEDES');
INSERT INTO `distrito` VALUES ('165', '00000000025', '04', 'SAN ISIDRO');
INSERT INTO `distrito` VALUES ('166', '00000000025', '05', 'CONCEPCIÓN');
INSERT INTO `distrito` VALUES ('167', '00000000025', '06', 'SAN JOSE');
INSERT INTO `distrito` VALUES ('168', '00000000025', '07', 'SANTA EULALIA');
INSERT INTO `distrito` VALUES ('169', '00000000025', '08', 'ESCOBAL');
INSERT INTO `distrito` VALUES ('170', '00000000026', '01', 'NARANJO');
INSERT INTO `distrito` VALUES ('171', '00000000026', '02', 'SAN MIGUEL');
INSERT INTO `distrito` VALUES ('172', '00000000026', '03', 'SAN JOSÉ');
INSERT INTO `distrito` VALUES ('173', '00000000026', '04', 'CIRRÍ SUR');
INSERT INTO `distrito` VALUES ('174', '00000000026', '05', 'SAN JERÓNIMO');
INSERT INTO `distrito` VALUES ('175', '00000000026', '06', 'SAN JUAN');
INSERT INTO `distrito` VALUES ('176', '00000000026', '07', 'EL ROSARIO');
INSERT INTO `distrito` VALUES ('177', '00000000026', '08', 'PALMITOS');
INSERT INTO `distrito` VALUES ('178', '00000000027', '01', 'PALMARES');
INSERT INTO `distrito` VALUES ('179', '00000000027', '02', 'ZARAGOZA');
INSERT INTO `distrito` VALUES ('180', '00000000027', '03', 'BUENOS AIRES');
INSERT INTO `distrito` VALUES ('181', '00000000027', '04', 'SANTIAGO');
INSERT INTO `distrito` VALUES ('182', '00000000027', '05', 'CANDELARIA');
INSERT INTO `distrito` VALUES ('183', '00000000027', '06', 'ESQUÍPULAS');
INSERT INTO `distrito` VALUES ('184', '00000000027', '07', 'LA GRANJA');
INSERT INTO `distrito` VALUES ('185', '00000000028', '01', 'SAN PEDRO');
INSERT INTO `distrito` VALUES ('186', '00000000028', '02', 'SAN JUAN');
INSERT INTO `distrito` VALUES ('187', '00000000028', '03', 'SAN RAFAEL');
INSERT INTO `distrito` VALUES ('188', '00000000028', '04', 'CARRILLOS');
INSERT INTO `distrito` VALUES ('189', '00000000028', '05', 'SABANA REDONDA');
INSERT INTO `distrito` VALUES ('190', '00000000029', '01', 'OROTINA');
INSERT INTO `distrito` VALUES ('191', '00000000029', '02', 'EL MASTATE');
INSERT INTO `distrito` VALUES ('192', '00000000029', '03', 'HACIENDA VIEJA');
INSERT INTO `distrito` VALUES ('193', '00000000029', '04', 'COYOLAR');
INSERT INTO `distrito` VALUES ('194', '00000000029', '05', 'LA CEIBA');
INSERT INTO `distrito` VALUES ('195', '00000000030', '01', 'QUESADA');
INSERT INTO `distrito` VALUES ('196', '00000000030', '02', 'FLORENCIA');
INSERT INTO `distrito` VALUES ('197', '00000000030', '03', 'BUENAVISTA');
INSERT INTO `distrito` VALUES ('198', '00000000030', '04', 'AGUAS ZARCAS');
INSERT INTO `distrito` VALUES ('199', '00000000030', '05', 'VENECIA');
INSERT INTO `distrito` VALUES ('200', '00000000030', '06', 'PITAL');
INSERT INTO `distrito` VALUES ('201', '00000000030', '07', 'LA FORTUNA');
INSERT INTO `distrito` VALUES ('202', '00000000030', '08', 'LA TIGRA');
INSERT INTO `distrito` VALUES ('203', '00000000030', '09', 'LA PALMERA');
INSERT INTO `distrito` VALUES ('204', '00000000030', '10', 'VENADO');
INSERT INTO `distrito` VALUES ('205', '00000000030', '11', 'CUTRIS');
INSERT INTO `distrito` VALUES ('206', '00000000030', '12', 'MONTERREY');
INSERT INTO `distrito` VALUES ('207', '00000000030', '13', 'POCOSOL');
INSERT INTO `distrito` VALUES ('208', '00000000031', '01', 'ZARCERO');
INSERT INTO `distrito` VALUES ('209', '00000000031', '02', 'LAGUNA');
INSERT INTO `distrito` VALUES ('210', '00000000031', '04', 'GUADALUPE');
INSERT INTO `distrito` VALUES ('211', '00000000031', '05', 'PALMIRA');
INSERT INTO `distrito` VALUES ('212', '00000000031', '06', 'ZAPOTE');
INSERT INTO `distrito` VALUES ('213', '00000000031', '07', 'BRISAS');
INSERT INTO `distrito` VALUES ('214', '00000000032', '01', 'SARCHÍ NORTE');
INSERT INTO `distrito` VALUES ('215', '00000000032', '02', 'SARCHÍ SUR');
INSERT INTO `distrito` VALUES ('216', '00000000032', '03', 'TORO AMARILLO');
INSERT INTO `distrito` VALUES ('217', '00000000032', '04', 'SAN PEDRO');
INSERT INTO `distrito` VALUES ('218', '00000000032', '05', 'RODRÍGUEZ');
INSERT INTO `distrito` VALUES ('219', '00000000033', '01', 'UPALA');
INSERT INTO `distrito` VALUES ('220', '00000000033', '02', 'AGUAS CLARAS');
INSERT INTO `distrito` VALUES ('221', '00000000033', '03', 'SAN JOSÉ o PIZOTE');
INSERT INTO `distrito` VALUES ('222', '00000000033', '04', 'BIJAGUA');
INSERT INTO `distrito` VALUES ('223', '00000000033', '05', 'DELICIAS');
INSERT INTO `distrito` VALUES ('224', '00000000033', '06', 'DOS RÍOS');
INSERT INTO `distrito` VALUES ('225', '00000000033', '07', 'YOLILLAL');
INSERT INTO `distrito` VALUES ('226', '00000000033', '08', 'CANALETE');
INSERT INTO `distrito` VALUES ('227', '00000000034', '01', 'LOS CHILES');
INSERT INTO `distrito` VALUES ('228', '00000000034', '02', 'CAÑO NEGRO');
INSERT INTO `distrito` VALUES ('229', '00000000034', '03', 'EL AMPARO');
INSERT INTO `distrito` VALUES ('230', '00000000034', '04', 'SAN JORGE');
INSERT INTO `distrito` VALUES ('231', '00000000035', '02', 'BUENAVISTA');
INSERT INTO `distrito` VALUES ('232', '00000000035', '03', 'COTE');
INSERT INTO `distrito` VALUES ('233', '00000000035', '04', 'KATIRA');
INSERT INTO `distrito` VALUES ('234', '00000000036', '01', 'ORIENTAL');
INSERT INTO `distrito` VALUES ('235', '00000000036', '02', 'OCCIDENTAL');
INSERT INTO `distrito` VALUES ('236', '00000000036', '03', 'CARMEN');
INSERT INTO `distrito` VALUES ('237', '00000000036', '04', 'SAN NICOLÁS');
INSERT INTO `distrito` VALUES ('238', '00000000036', '05', 'AGUACALIENTE o SAN FRANCISCO');
INSERT INTO `distrito` VALUES ('239', '00000000036', '06', 'GUADALUPE o ARENILLA');
INSERT INTO `distrito` VALUES ('240', '00000000036', '07', 'CORRALILLO');
INSERT INTO `distrito` VALUES ('241', '00000000036', '08', 'TIERRA BLANCA');
INSERT INTO `distrito` VALUES ('242', '00000000036', '09', 'DULCE NOMBRE');
INSERT INTO `distrito` VALUES ('243', '00000000036', '10', 'LLANO GRANDE');
INSERT INTO `distrito` VALUES ('244', '00000000036', '11', 'QUEBRADILLA');
INSERT INTO `distrito` VALUES ('245', '00000000037', '01', 'PARAÍSO');
INSERT INTO `distrito` VALUES ('246', '00000000037', '02', 'SANTIAGO');
INSERT INTO `distrito` VALUES ('247', '00000000037', '03', 'OROSI');
INSERT INTO `distrito` VALUES ('248', '00000000037', '04', 'CACHÍ');
INSERT INTO `distrito` VALUES ('249', '00000000037', '05', 'LLANOS DE SANTA LUCÍA');
INSERT INTO `distrito` VALUES ('250', '00000000038', '01', 'TRES RÍOS');
INSERT INTO `distrito` VALUES ('251', '00000000038', '02', 'SAN DIEGO');
INSERT INTO `distrito` VALUES ('252', '00000000038', '03', 'SAN JUAN');
INSERT INTO `distrito` VALUES ('253', '00000000038', '04', 'SAN RAFAEL');
INSERT INTO `distrito` VALUES ('254', '00000000038', '05', 'CONCEPCIÓN');
INSERT INTO `distrito` VALUES ('255', '00000000038', '06', 'DULCE NOMBRE');
INSERT INTO `distrito` VALUES ('256', '00000000038', '07', 'SAN RAMÓN');
INSERT INTO `distrito` VALUES ('257', '00000000038', '08', 'RÍO AZUL');
INSERT INTO `distrito` VALUES ('258', '00000000039', '01', 'JUAN VIÑAS');
INSERT INTO `distrito` VALUES ('259', '00000000039', '02', 'TUCURRIQUE');
INSERT INTO `distrito` VALUES ('260', '00000000039', '03', 'PEJIBAYE');
INSERT INTO `distrito` VALUES ('261', '00000000040', '01', 'TURRIALBA');
INSERT INTO `distrito` VALUES ('262', '00000000040', '02', 'LA SUIZA');
INSERT INTO `distrito` VALUES ('263', '00000000040', '03', 'PERALTA');
INSERT INTO `distrito` VALUES ('264', '00000000040', '04', 'SANTA CRUZ');
INSERT INTO `distrito` VALUES ('265', '00000000040', '05', 'SANTA TERESITA');
INSERT INTO `distrito` VALUES ('266', '00000000040', '06', 'PAVONES');
INSERT INTO `distrito` VALUES ('267', '00000000040', '07', 'TUIS');
INSERT INTO `distrito` VALUES ('268', '00000000040', '08', 'TAYUTIC');
INSERT INTO `distrito` VALUES ('269', '00000000040', '09', 'SANTA ROSA');
INSERT INTO `distrito` VALUES ('270', '00000000040', '10', 'TRES EQUIS');
INSERT INTO `distrito` VALUES ('271', '00000000040', '11', 'LA ISABEL');
INSERT INTO `distrito` VALUES ('272', '00000000040', '12', 'CHIRRIPÓ');
INSERT INTO `distrito` VALUES ('273', '00000000041', '01', 'PACAYAS');
INSERT INTO `distrito` VALUES ('274', '00000000041', '02', 'CERVANTES');
INSERT INTO `distrito` VALUES ('275', '00000000041', '03', 'CAPELLADES');
INSERT INTO `distrito` VALUES ('276', '00000000042', '01', 'SAN RAFAEL');
INSERT INTO `distrito` VALUES ('277', '00000000042', '02', 'COT');
INSERT INTO `distrito` VALUES ('278', '00000000042', '03', 'POTRERO CERRADO');
INSERT INTO `distrito` VALUES ('279', '00000000042', '04', 'CIPRESES');
INSERT INTO `distrito` VALUES ('280', '00000000042', '05', 'SANTA ROSA');
INSERT INTO `distrito` VALUES ('281', '00000000043', '01', 'EL TEJAR');
INSERT INTO `distrito` VALUES ('282', '00000000043', '02', 'SAN ISIDRO');
INSERT INTO `distrito` VALUES ('283', '00000000043', '03', 'TOBOSI');
INSERT INTO `distrito` VALUES ('284', '00000000043', '04', 'PATIO DE AGUA');
INSERT INTO `distrito` VALUES ('285', '00000000044', '01', 'HEREDIA');
INSERT INTO `distrito` VALUES ('286', '00000000044', '02', 'MERCEDES');
INSERT INTO `distrito` VALUES ('287', '00000000044', '03', 'SAN FRANCISCO');
INSERT INTO `distrito` VALUES ('288', '00000000044', '04', 'ULLOA');
INSERT INTO `distrito` VALUES ('289', '00000000044', '05', 'VARABLANCA');
INSERT INTO `distrito` VALUES ('290', '00000000045', '01', 'BARVA');
INSERT INTO `distrito` VALUES ('291', '00000000045', '02', 'SAN PEDRO');
INSERT INTO `distrito` VALUES ('292', '00000000045', '03', 'SAN PABLO');
INSERT INTO `distrito` VALUES ('293', '00000000045', '04', 'SAN ROQUE');
INSERT INTO `distrito` VALUES ('294', '00000000045', '05', 'SANTA LUCÍA');
INSERT INTO `distrito` VALUES ('295', '00000000045', '06', 'SAN JOSÉ DE LA MONTAÑA');
INSERT INTO `distrito` VALUES ('296', '00000000046', '02', 'SAN VICENTE');
INSERT INTO `distrito` VALUES ('297', '00000000046', '03', 'SAN MIGUEL');
INSERT INTO `distrito` VALUES ('298', '00000000046', '04', 'PARACITO');
INSERT INTO `distrito` VALUES ('299', '00000000046', '05', 'SANTO TOMÁS');
INSERT INTO `distrito` VALUES ('300', '00000000046', '06', 'SANTA ROSA');
INSERT INTO `distrito` VALUES ('301', '00000000046', '07', 'TURES');
INSERT INTO `distrito` VALUES ('302', '00000000046', '08', 'PARÁ');
INSERT INTO `distrito` VALUES ('303', '00000000047', '01', 'SANTA BÁRBARA');
INSERT INTO `distrito` VALUES ('304', '00000000047', '02', 'SAN PEDRO');
INSERT INTO `distrito` VALUES ('305', '00000000047', '03', 'SAN JUAN');
INSERT INTO `distrito` VALUES ('306', '00000000047', '04', 'JESÚS');
INSERT INTO `distrito` VALUES ('307', '00000000047', '05', 'SANTO DOMINGO');
INSERT INTO `distrito` VALUES ('308', '00000000047', '06', 'PURABÁ');
INSERT INTO `distrito` VALUES ('309', '00000000048', '01', 'SAN RAFAEL');
INSERT INTO `distrito` VALUES ('310', '00000000048', '02', 'SAN JOSECITO');
INSERT INTO `distrito` VALUES ('311', '00000000048', '03', 'SANTIAGO');
INSERT INTO `distrito` VALUES ('312', '00000000048', '04', 'ÁNGELES');
INSERT INTO `distrito` VALUES ('313', '00000000048', '05', 'CONCEPCIÓN');
INSERT INTO `distrito` VALUES ('314', '00000000049', '01', 'SAN ISIDRO');
INSERT INTO `distrito` VALUES ('315', '00000000049', '02', 'SAN JOSÉ');
INSERT INTO `distrito` VALUES ('316', '00000000049', '03', 'CONCEPCIÓN');
INSERT INTO `distrito` VALUES ('317', '00000000049', '04', 'SAN FRANCISCO');
INSERT INTO `distrito` VALUES ('318', '00000000050', '01', 'SAN ANTONIO');
INSERT INTO `distrito` VALUES ('319', '00000000050', '02', 'LA RIBERA');
INSERT INTO `distrito` VALUES ('320', '00000000050', '03', 'LA ASUNCIÓN');
INSERT INTO `distrito` VALUES ('321', '00000000051', '01', 'SAN JOAQUÍN');
INSERT INTO `distrito` VALUES ('322', '00000000051', '02', 'BARRANTES');
INSERT INTO `distrito` VALUES ('323', '00000000051', '03', 'LLORENTE');
INSERT INTO `distrito` VALUES ('324', '00000000052', '01', 'SAN PABLO');
INSERT INTO `distrito` VALUES ('325', '00000000053', '01', 'PUERTO VIEJO');
INSERT INTO `distrito` VALUES ('326', '00000000053', '02', 'LA VIRGEN');
INSERT INTO `distrito` VALUES ('327', '00000000053', '03', 'LAS HORQUETAS');
INSERT INTO `distrito` VALUES ('328', '00000000053', '04', 'LLANURAS DEL GASPAR');
INSERT INTO `distrito` VALUES ('329', '00000000053', '05', 'CUREÑA');
INSERT INTO `distrito` VALUES ('330', '00000000054', '01', 'LIBERIA');
INSERT INTO `distrito` VALUES ('331', '00000000054', '02', 'CAÑAS DULCES');
INSERT INTO `distrito` VALUES ('332', '00000000054', '03', 'MAYORGA');
INSERT INTO `distrito` VALUES ('333', '00000000054', '04', 'NACASCOLO');
INSERT INTO `distrito` VALUES ('334', '00000000054', '05', 'CURUBANDÉ');
INSERT INTO `distrito` VALUES ('335', '00000000055', '01', 'NICOYA');
INSERT INTO `distrito` VALUES ('336', '00000000055', '02', 'MANSIÓN');
INSERT INTO `distrito` VALUES ('337', '00000000055', '03', 'SAN ANTONIO');
INSERT INTO `distrito` VALUES ('338', '00000000055', '04', 'QUEBRADA HONDA');
INSERT INTO `distrito` VALUES ('339', '00000000055', '05', 'SÁMARA');
INSERT INTO `distrito` VALUES ('340', '00000000055', '06', 'NOSARA');
INSERT INTO `distrito` VALUES ('341', '00000000055', '07', 'BELÉN DE NOSARITA');
INSERT INTO `distrito` VALUES ('342', '00000000056', '01', 'SANTA CRUZ');
INSERT INTO `distrito` VALUES ('343', '00000000056', '02', 'BOLSÓN');
INSERT INTO `distrito` VALUES ('344', '00000000056', '03', 'VEINTISIETE DE ABRIL');
INSERT INTO `distrito` VALUES ('345', '00000000056', '04', 'TEMPATE');
INSERT INTO `distrito` VALUES ('346', '00000000056', '05', 'CARTAGENA');
INSERT INTO `distrito` VALUES ('347', '00000000056', '06', 'CUAJINIQUIL');
INSERT INTO `distrito` VALUES ('348', '00000000056', '07', 'DIRIÁ');
INSERT INTO `distrito` VALUES ('349', '00000000056', '08', 'CABO VELAS');
INSERT INTO `distrito` VALUES ('350', '00000000056', '09', 'TAMARINDO');
INSERT INTO `distrito` VALUES ('351', '00000000057', '01', 'BAGACES');
INSERT INTO `distrito` VALUES ('352', '00000000057', '02', 'LA FORTUNA');
INSERT INTO `distrito` VALUES ('353', '00000000057', '03', 'MOGOTE');
INSERT INTO `distrito` VALUES ('354', '00000000057', '04', 'RÍO NARANJO');
INSERT INTO `distrito` VALUES ('355', '00000000058', '01', 'FILADELFIA');
INSERT INTO `distrito` VALUES ('356', '00000000058', '02', 'PALMIRA');
INSERT INTO `distrito` VALUES ('357', '00000000058', '03', 'SARDINAL');
INSERT INTO `distrito` VALUES ('358', '00000000058', '04', 'BELÉN');
INSERT INTO `distrito` VALUES ('359', '00000000059', '01', 'CAÑAS');
INSERT INTO `distrito` VALUES ('360', '00000000059', '02', 'PALMIRA');
INSERT INTO `distrito` VALUES ('361', '00000000059', '03', 'SAN MIGUEL');
INSERT INTO `distrito` VALUES ('362', '00000000059', '04', 'BEBEDERO');
INSERT INTO `distrito` VALUES ('363', '00000000059', '05', 'POROZAL');
INSERT INTO `distrito` VALUES ('364', '00000000060', '01', 'LAS JUNTAS');
INSERT INTO `distrito` VALUES ('365', '00000000060', '02', 'SIERRA');
INSERT INTO `distrito` VALUES ('366', '00000000060', '03', 'SAN JUAN');
INSERT INTO `distrito` VALUES ('367', '00000000060', '04', 'COLORADO');
INSERT INTO `distrito` VALUES ('368', '00000000061', '01', 'TILARÁN');
INSERT INTO `distrito` VALUES ('369', '00000000061', '02', 'QUEBRADA GRANDE');
INSERT INTO `distrito` VALUES ('370', '00000000061', '03', 'TRONADORA');
INSERT INTO `distrito` VALUES ('371', '00000000061', '04', 'SANTA ROSA');
INSERT INTO `distrito` VALUES ('372', '00000000061', '05', 'LÍBANO');
INSERT INTO `distrito` VALUES ('373', '00000000061', '06', 'TIERRAS MORENAS');
INSERT INTO `distrito` VALUES ('374', '00000000061', '07', 'ARENAL');
INSERT INTO `distrito` VALUES ('375', '00000000062', '01', 'CARMONA');
INSERT INTO `distrito` VALUES ('376', '00000000062', '02', 'SANTA RITA');
INSERT INTO `distrito` VALUES ('377', '00000000062', '03', 'ZAPOTAL');
INSERT INTO `distrito` VALUES ('378', '00000000062', '04', 'SAN PABLO');
INSERT INTO `distrito` VALUES ('379', '00000000062', '05', 'PORVENIR');
INSERT INTO `distrito` VALUES ('380', '00000000062', '06', 'BEJUCO');
INSERT INTO `distrito` VALUES ('381', '00000000063', '01', 'LA CRUZ');
INSERT INTO `distrito` VALUES ('382', '00000000063', '02', 'SANTA CECILIA');
INSERT INTO `distrito` VALUES ('383', '00000000063', '03', 'LA GARITA');
INSERT INTO `distrito` VALUES ('384', '00000000063', '04', 'SANTA ELENA');
INSERT INTO `distrito` VALUES ('385', '00000000064', '01', 'HOJANCHA');
INSERT INTO `distrito` VALUES ('386', '00000000064', '02', 'MONTE ROMO');
INSERT INTO `distrito` VALUES ('387', '00000000064', '03', 'PUERTO CARRILLO');
INSERT INTO `distrito` VALUES ('388', '00000000064', '04', 'HUACAS');
INSERT INTO `distrito` VALUES ('389', '00000000065', '01', 'PUNTARENAS');
INSERT INTO `distrito` VALUES ('390', '00000000065', '02', 'PITAHAYA');
INSERT INTO `distrito` VALUES ('391', '00000000065', '03', 'CHOMES');
INSERT INTO `distrito` VALUES ('392', '00000000065', '04', 'LEPANTO');
INSERT INTO `distrito` VALUES ('393', '00000000065', '05', 'PAQUERA');
INSERT INTO `distrito` VALUES ('394', '00000000065', '06', 'MANZANILLO');
INSERT INTO `distrito` VALUES ('395', '00000000065', '07', 'GUACIMAL');
INSERT INTO `distrito` VALUES ('396', '00000000065', '08', 'BARRANCA');
INSERT INTO `distrito` VALUES ('397', '00000000065', '09', 'MONTE VERDE');
INSERT INTO `distrito` VALUES ('398', '00000000065', '11', 'CÓBANO');
INSERT INTO `distrito` VALUES ('399', '00000000065', '12', 'CHACARITA');
INSERT INTO `distrito` VALUES ('400', '00000000065', '13', 'CHIRA');
INSERT INTO `distrito` VALUES ('401', '00000000065', '14', 'ACAPULCO');
INSERT INTO `distrito` VALUES ('402', '00000000065', '15', 'EL ROBLE');
INSERT INTO `distrito` VALUES ('403', '00000000065', '16', 'ARANCIBIA');
INSERT INTO `distrito` VALUES ('404', '00000000066', '01', 'ESPÍRITU SANTO');
INSERT INTO `distrito` VALUES ('405', '00000000066', '02', 'SAN JUAN GRANDE');
INSERT INTO `distrito` VALUES ('406', '00000000066', '03', 'MACACONA');
INSERT INTO `distrito` VALUES ('407', '00000000066', '04', 'SAN RAFAEL');
INSERT INTO `distrito` VALUES ('408', '00000000066', '05', 'SAN JERÓNIMO');
INSERT INTO `distrito` VALUES ('409', '00000000066', '06', 'CALDERA');
INSERT INTO `distrito` VALUES ('410', '00000000067', '01', 'BUENOS AIRES');
INSERT INTO `distrito` VALUES ('411', '00000000067', '02', 'VOLCÁN');
INSERT INTO `distrito` VALUES ('412', '00000000067', '03', 'POTRERO GRANDE');
INSERT INTO `distrito` VALUES ('413', '00000000067', '04', 'BORUCA');
INSERT INTO `distrito` VALUES ('414', '00000000067', '05', 'PILAS');
INSERT INTO `distrito` VALUES ('415', '00000000067', '06', 'COLINAS');
INSERT INTO `distrito` VALUES ('416', '00000000067', '07', 'CHÁNGUENA');
INSERT INTO `distrito` VALUES ('417', '00000000067', '08', 'BIOLLEY');
INSERT INTO `distrito` VALUES ('418', '00000000067', '09', 'BRUNKA');
INSERT INTO `distrito` VALUES ('419', '00000000068', '01', 'MIRAMAR');
INSERT INTO `distrito` VALUES ('420', '00000000068', '02', 'LA UNIÓN');
INSERT INTO `distrito` VALUES ('421', '00000000068', '03', 'SAN ISIDRO');
INSERT INTO `distrito` VALUES ('422', '00000000069', '01', 'PUERTO CORTÉS');
INSERT INTO `distrito` VALUES ('423', '00000000069', '02', 'PALMAR');
INSERT INTO `distrito` VALUES ('424', '00000000069', '03', 'SIERPE');
INSERT INTO `distrito` VALUES ('425', '00000000069', '04', 'BAHÍA BALLENA');
INSERT INTO `distrito` VALUES ('426', '00000000069', '05', 'PIEDRAS BLANCAS');
INSERT INTO `distrito` VALUES ('427', '00000000069', '06', 'BAHÍA DRAKE');
INSERT INTO `distrito` VALUES ('428', '00000000070', '01', 'QUEPOS');
INSERT INTO `distrito` VALUES ('429', '00000000070', '02', 'SAVEGRE');
INSERT INTO `distrito` VALUES ('430', '00000000070', '03', 'NARANJITO');
INSERT INTO `distrito` VALUES ('431', '00000000071', '01', 'GOLFITO');
INSERT INTO `distrito` VALUES ('432', '00000000071', '02', 'PUERTO JIMÉNEZ');
INSERT INTO `distrito` VALUES ('433', '00000000071', '03', 'GUAYCARÁ');
INSERT INTO `distrito` VALUES ('434', '00000000071', '04', 'PAVÓN');
INSERT INTO `distrito` VALUES ('435', '00000000072', '01', 'SAN VITO');
INSERT INTO `distrito` VALUES ('436', '00000000072', '02', 'SABALITO');
INSERT INTO `distrito` VALUES ('437', '00000000072', '03', 'AGUABUENA');
INSERT INTO `distrito` VALUES ('438', '00000000072', '04', 'LIMONCITO');
INSERT INTO `distrito` VALUES ('439', '00000000072', '05', 'PITTIER');
INSERT INTO `distrito` VALUES ('440', '00000000072', '06', 'GUTIERREZ BRAUN');
INSERT INTO `distrito` VALUES ('441', '00000000073', '01', 'PARRITA');
INSERT INTO `distrito` VALUES ('442', '00000000074', '01', 'CORREDOR');
INSERT INTO `distrito` VALUES ('443', '00000000074', '02', 'LA CUESTA');
INSERT INTO `distrito` VALUES ('444', '00000000074', '03', 'CANOAS');
INSERT INTO `distrito` VALUES ('445', '00000000074', '04', 'LAUREL');
INSERT INTO `distrito` VALUES ('446', '00000000075', '01', 'JACÓ');
INSERT INTO `distrito` VALUES ('447', '00000000075', '02', 'TÁRCOLES');
INSERT INTO `distrito` VALUES ('448', '00000000076', '01', 'LIMÓN');
INSERT INTO `distrito` VALUES ('449', '00000000076', '02', 'VALLE LA ESTRELLA');
INSERT INTO `distrito` VALUES ('450', '00000000076', '04', 'MATAMA');
INSERT INTO `distrito` VALUES ('451', '00000000077', '01', 'GUÁPILES');
INSERT INTO `distrito` VALUES ('452', '00000000077', '02', 'JIMÉNEZ');
INSERT INTO `distrito` VALUES ('453', '00000000077', '03', 'RITA');
INSERT INTO `distrito` VALUES ('454', '00000000077', '04', 'ROXANA');
INSERT INTO `distrito` VALUES ('455', '00000000077', '05', 'CARIARI');
INSERT INTO `distrito` VALUES ('456', '00000000077', '06', 'COLORADO');
INSERT INTO `distrito` VALUES ('457', '00000000077', '07', 'LA COLONIA');
INSERT INTO `distrito` VALUES ('458', '00000000078', '01', 'SIQUIRRES');
INSERT INTO `distrito` VALUES ('459', '00000000078', '02', 'PACUARITO');
INSERT INTO `distrito` VALUES ('460', '00000000078', '03', 'FLORIDA');
INSERT INTO `distrito` VALUES ('461', '00000000078', '04', 'GERMANIA');
INSERT INTO `distrito` VALUES ('462', '00000000078', '05', 'EL CAIRO');
INSERT INTO `distrito` VALUES ('463', '00000000078', '06', 'ALEGRÍA');
INSERT INTO `distrito` VALUES ('464', '00000000079', '01', 'BRATSI');
INSERT INTO `distrito` VALUES ('465', '00000000079', '02', 'SIXAOLA');
INSERT INTO `distrito` VALUES ('466', '00000000079', '03', 'CAHUITA');
INSERT INTO `distrito` VALUES ('467', '00000000079', '04', 'TELIRE');
INSERT INTO `distrito` VALUES ('468', '00000000080', '01', 'MATINA');
INSERT INTO `distrito` VALUES ('469', '00000000080', '02', 'BATÁN');
INSERT INTO `distrito` VALUES ('470', '00000000080', '03', 'CARRANDI');
INSERT INTO `distrito` VALUES ('471', '00000000081', '01', 'GUÁCIMO');
INSERT INTO `distrito` VALUES ('472', '00000000081', '02', 'MERCEDES');
INSERT INTO `distrito` VALUES ('473', '00000000081', '03', 'POCORA');
INSERT INTO `distrito` VALUES ('474', '00000000081', '04', 'RÍO JIMÉNEZ');
INSERT INTO `distrito` VALUES ('475', '00000000081', '05', 'DUACARÍ');
