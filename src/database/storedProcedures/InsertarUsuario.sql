drop procedure if exists `InsertarUsuario`;

delimiter $
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarUsuario`(
	IN `pUser` char(16),
	IN `pNivel` smallint(5),
	IN `pN1` tinyint(1),
	IN `pN2` tinyint(1),
	IN `pN3` tinyint(1),
	IN `pFacturas` tinyint(1),
	IN `pN5` tinyint(1),
	IN `pN6` tinyint(1),
	IN `pN7` tinyint(1),
	IN `pN8` tinyint(1),
	IN `pN9` tinyint(1),
	IN `pPrecios` tinyint(1),
	IN `pDevoluciones` tinyint(1),
	IN `pDescuentos` tinyint(1),
	IN `pMaxDesc` decimal,
	IN `pNotifCompra` tinyint(1),
	IN `pIntervalo1` smallInt,
	IN `pNotifFactcxc` tinyint(1),
	IN `pIntervalo2` smallInt,
	IN `pNotifFactcxp` tinyint(1),
	IN `pIntervalo3` smallInt,
	IN `pNotifxmlfe` tinyint(1),
	IN `pIntervalo4` smallInt,
	IN `pActivo` char(1),
	IN `pUltimaClave` datetime,
	IN `pFirmas` tinyint(1)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    Declare vExisteEnBD tinyint(1);
    Declare vExisteEnOSAIS tinyint(1);
    Declare vMensaje varchar(40);

    Set vExisteEnBD    = Exists(Select 1 from vistausuarios where user = pUser);
    Set vExisteEnOSAIS = Exists(Select 1 from usuario where user = pUser);

    If not vExisteEnBD then
       Set vMensaje = '[BD] Usuario no existe en MySQL';
    Else
       if vExisteEnOSAIS then
          Set vMensaje = '[BD] Usuario ya existe';
       End if;
    End if;

    If vMensaje is null then
       Insert into usuario (
          User,
          Nivel,
          N1,
          N2,
          N3,
          Facturas,
          N5,
          N6,
          N7,
          N8,
          N9,
          Precios,
          Devoluciones,
          Descuentos,
          MaxDesc,
          NotifCompra,
		Intervalo1,   -- Bosco agregado 28/07/2013
		NotifFactcxc, -- Bosco agregado 28/07/2013
		Intervalo2,   -- Bosco agregado 28/07/2013
		NotifFactcxp, -- Bosco agregado 28/07/2013
		Intervalo3,   -- Bosco agregado 28/07/2013
		notifxmlfe, 	-- Bosco agregado 23/12/2018
		Intervalo4,   -- Bosco agregado 23/12/2018
          Firmas )
       values(
          pUser,
          pNivel,
          pN1,
          pN2,
          pN3,
          pFacturas,
          pN5,
          pN6,
          pN7,
          pN8,
          pN9,
          pPrecios,
          pDevoluciones,
          pDescuentos,
          pMaxDesc,
          pNotifCompra,
		pIntervalo1,   -- Bosco agregado 28/07/2013
		pNotifFactcxc, -- Bosco agregado 28/07/2013
		pIntervalo2,   -- Bosco agregado 28/07/2013
		pNotifFactcxp, -- Bosco agregado 28/07/2013
		pIntervalo3,   -- Bosco agregado 28/07/2013
		pNotifxmlfe, 	 -- Bosco agregado 23/12/2018
		pIntervalo4,   -- Bosco agregado 23/12/2018
          pFirmas );

		-- Si el usuario no existe en la base de datos del sistema
		-- lo agrego.
		if not Exists(Select user from saisystem.usuario
					  Where user = pUser) then
			Insert into saisystem.usuario (
			  User,
			  activo,       -- Bosco agregado 06/11/2011
			  ultimaClave)  -- Bosco agregado 06/11/2011
			Values(pUser, pActivo, pUltimaClave);
		End if;
    else
       Select vMensaje;
    end if;
END$
delimiter ;