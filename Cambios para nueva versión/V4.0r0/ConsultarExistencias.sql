use LaFlor;
Drop procedure ConsultarExistencias;


DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `ConsultarExistencias`(
  IN  `pArtcode`  varchar(20),
  IN  `pBodega`   varchar(3)
)
BEGIN
	# Bosco modificado 20/12/2015.  Dejo de usar las consultas individuales
	# y cargo todo en un solo select agregando la localización.

	Declare vArtexis    Decimal(14,4);
	Declare vDisponible Decimal(14,4);
	Declare vLocaliz    Varchar(7);
	

	#Set vArtexis    = ConsultarExistencia(pArtcode,pBodega);
	#Set vDisponible = ConsultarExistenciaDisponible(pArtcode,pBodega);
	Select 
		artexis,
		artexis - artreserv,
		localiz
	From bodexis 
	Where artcode = pArtcode and bodega = pBodega
	Into vArtexis, vDisponible, vLocaliz;
  
	Select vArtexis as artexis, vDisponible as disponible, vLocaliz as localizacion;

END$$
DELIMITER ;
