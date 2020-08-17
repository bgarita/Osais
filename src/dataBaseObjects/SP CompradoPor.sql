DROP PROCEDURE if EXISTS CompradoPor;

delimiter $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `CompradoPor`(
	IN `pClicode` integer,
	IN `pArtcode` varchar(20)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
	Declare vComprado  TinyInt(1);
	Declare vMensajeEr varchar(1000);
	
	-- Traer los datos más recientes
	Create Temporary Table Datos as
		Select
			fadetall.facnume,
			faencabe.facfech,
			fadetall.Faccant,
			fadetall.Artprec,
			fadetall.Facpdesc,
			fadetall.Facpive,
			fadetall.codigoTarifa,
			faencabe.codigoTC,
			faencabe.tipoca
		From fadetall
		Inner join faencabe on faencabe.facnume = fadetall.facnume
		             and faencabe.facnd   = fadetall.facnd
		Where clicode = pClicode
		and artcode = pArtcode
		and fadetall.facnd = 0
		and facestado = ''
		Order by facfech Desc
		Limit 1;

	Set vComprado  = If(IfNull((Select count(facnume) from Datos),0) > 0, 1, 0);
	Set vMensajeEr = If(IfNull((Select count(facnume) from Datos),0) = 0, 'El artículo no fue comprado por este cliente','');
	
	If vComprado = 1 and (DATEDIFF(Now(),(Select facfech from Datos)) > (Select DiasDevol from config)) then
		Set vMensajeEr =
		    '[DB] Este artículo sobrepasa el tiempo establecido para aceptar devoluciones.';
	End if;

	If vComprado > 0 then
		Select
		   vComprado  as Comprado,
		   vMensajeEr as MensajeEr,
		   DATEDIFF(Now(),facfech) as Dias,
		   Datos.*
		From Datos;
	Else
		Select
		   vComprado  as Comprado,
		   vMensajeEr as MensajeEr;
	End if;
		
	Drop table Datos;

END$$

Delimiter ;