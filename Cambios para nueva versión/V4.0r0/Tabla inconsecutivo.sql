use LaFlor;

CREATE TABLE `inconsecutivo` (
  `docinv` char(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

Insert into inconsecutivo
	Select docinv from config;

Alter table config
	Drop column docinv;