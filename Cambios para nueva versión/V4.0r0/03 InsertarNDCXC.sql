USE `sai`;
DROP procedure IF EXISTS `InsertarNDCXC`;

DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarNDCXC`(
  IN  `pfacnume`     int(10),
  IN  `pclicode`     int(10),
  IN  `pfacfech`     datetime,
  IN  `pfacmont`     double,
  IN  `pReferencia`  varchar(10),
  IN  `pcodigoTC`    char(3),
  IN  `ptipoca`      float,
  IN  `pOrdenc`  	 varchar(10) -- Bosco agregado 26/09/2018
)
BEGIN
  
  -- Autor: Bosco Garita Azofeifa
  Declare vHayError tinyInt(1);
  Declare vMensaje  varchar(200);
  Declare vVend     tinyint(3);
  Declare vTerr     tinyint(3);
  Declare vBodega   char(3);

  Set vHayError = 0;
  Set vMensaje  = '';

  
  If Exists(Select facnume from faencabe Where facnume = pFacnume and facnd = (pFacnume * -1)) then
     Set vHayError = 1;
     Set vMensaje  = '[BD] Nota de débito ya existe.';
  End if;

  
  If not Exists(Select artcode from inarticu Where artcode = '_NOINV') THEN
     Set vHayError = 1;
     Set vMensaje  = '[BD] El artículo _NOINV debe estar creado.';
  End if;

  If vHayError = 0 then
     Set vVend   = (Select vend from inclient Where clicode = pClicode);
     Set vTerr   = (Select terr from inclient Where clicode = pClicode);
     Set vBodega = (Select bodega from config);  


     INSERT INTO faencabe (
        facnume,
        clicode,
        vend,
        terr,
        facfech,
        facplazo,
        facmont,
        facfepa,
        facsald,
        facnd,
        user,
        referencia,
        precio,
        facfechac,
        codigoTC,
        tipoca,
		ordenc)
    VALUES (
        pfacnume,
        pclicode,
        vVend,
        vTerr,
        pfacfech,
        1,          
        pfacmont,
        pfacfech,   
        pfacmont,   
        (pfacnume * -1),  
        Trim(user()),
        preferencia,
        1,          
        Now(),
        pcodigoTC,
        ptipoca,
		pOrdenc);

    If Row_Count() <= 0 then
       Set vHayError = 1;
       Set vMensaje  = '[BD] No se pudo guardar el encabezado de la ND';
    End if;

    If vHayError = 0 then
       Insert into fadetall (
         facnume,
         artcode,
         bodega,
         faccant,
         artprec,
         facmont,
         facnd )
       Values (
         pfacnume,
         '_NOINV',  
         vBodega,
         1,
         pfacmont,
         pfacmont,
         pfacnume * -1);

       If Row_Count() <= 0 then
          Set vHayError = 1;
          Set vMensaje  = '[BD] No se pudo guardar el detalle de la ND';
       End if;
    End if;
  end if;

  Select vHayError as vHayError, vMensaje as vMensaje;
END$$

DELIMITER ;

