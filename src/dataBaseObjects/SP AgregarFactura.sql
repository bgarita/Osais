
drop procedure if exists AgregarFactura;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `AgregarFactura`(
	IN `pId` int
)
BEGIN
  
  INSERT INTO faencabe
    (facnume,  clicode,
    factipo,   chequeotar,
    vend,      terr,
    facfech,   facplazo,
    facimve,   facpive,
    facdesc,   facmont,
    facfepa,   facpago,
    facsald,   facnpag,
    facmpag,   facdpago,
    facfppago, facestado,
    facnd,     user,
    referencia,precio,
    facfechac, ordenc,
    formulario,codigoTC,
    tipoca,    faccsfc,
    codExpress,facmonexp)
  Select
    facnume,  clicode,
    factipo,  IfNull(chequeotar,''),
    vend,     terr,
    facfech,  facplazo,
    facimve,  facpive,
    facdesc,  facmont,
    facfepa,  facpago,
    facsald,  facnpag,
    facmpag,  facdpago,
    facfppago,facestado,
    facnd,    user,
    referencia,precio,
    facfechac, Case When ordenc is null then ' ' else ordenc end,
    formulario,codigoTC,
    tipoca,   faccsfc,
    codExpress,facmonexp
  From wrk_faencabe
  Where id = pid;


  INSERT INTO fadetall
	(facnume, artcode, bodega,
	faccant, artprec, facimve,
	facpive, facdesc, facmont,
	artcosp, facnd, facpdesc, 
	artcost, codigoTarifa)
	Select
		facnume, artcode, bodega,
		faccant, artprec, facimve,
		facpive, facdesc, facmont,
		artcosp, facnd, facpdesc, 
		artcost, codigoTarifa
	From wrk_fadetall
	Where id = pid;
END$$
DELIMITER ;

