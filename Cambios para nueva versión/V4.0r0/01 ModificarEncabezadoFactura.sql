USE `sai`;
DROP procedure IF EXISTS `ModificarEncabezadoFactura`;

DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `ModificarEncabezadoFactura`(
	IN  `pID`          	int(10),
	IN  `pFacnume`     	int(10),
	IN  `pVend`        	tinyint(3),
	IN  `pTerr`        	tinyint(3),
	IN  `pFacfech`     	datetime,
	IN  `pFacplazo`    	tinyint(3),
	IN  `pCodExpress`  	smallint,
	IN  `pFacmonexp`   	double,
	IN  `pOrdenc`  		varchar(10) -- Bosco agregado 23/09/2018
)
BEGIN

	/*
	Este SP se usa para actualizar los últimos detalles de la factura
	en la tabla temporal justo antes de trasladarla a la tabla definitiva.
	OJO: Si viene algún código de Express inexistente aparecerá un error
	que dice 'valor_nopermitido doesn't exist'
	*/

	Declare vFacfepa  datetime;    -- Se usa para calcular la fecha de vencimiento


	Set pFacfech = IfNull(pFacfech,now());
	Set vFacfepa = AddDate(pFacfech, interval pFacplazo day);

	# Verifico el código Express
	If pFacmonexp <= 0 then
		Set pCodExpress = 0;
	End if;

	If pCodExpress > 0 then
		If not Exists(Select codExpress
					  from faexpress
					  Where codExpress = pCodExpress) then
			-- Provoco el error
			Insert into valor_nopermitido Select 1;
		End if;
	End if;

	If pOrdenc is NULL THEN
		Set pOrdenc = '';
	End if;


	-- No hago ninguna validación, le dejo la tarea a la integridad referencial
	Update wrk_faencabe Set
		facnume    = pFacnume,
		facmont    = facmont + pFacmonexp, -- Agregado 18/10/2010
		vend       = pVend,
		terr       = pTerr,
		facfech    = pFacfech,
		facplazo   = pFacplazo,
		facfepa    = vFacfepa,
		facsald    = If(pFacplazo > 0, facmont, 0),
		facdpago   = Round(pFacplazo / facnpag,0),
		facfppago  = AddDate(pFacfech, interval facdpago day),
		codExpress = pCodExpress,
		facmonexp  = pFacmonexp,
		ordenc	   = pOrdenc
	Where id = pID;

END$$

DELIMITER ;