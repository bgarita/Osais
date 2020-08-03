
drop procedure if exists RecalcularFactura;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `RecalcularFactura`(
	IN `pID` int(10),
	IN `pAplicarIV` tinyint(1)
)
BEGIN

   Declare vResultado    tinyInt(1);     
   Declare vErrorMessage varchar(50);    
   Declare vFacimve      decimal(12,2);  
   Declare vFacdesc      decimal(12,2);  
   Declare vFacmont      decimal(12,2);  
   Declare vRedondear    tinyInt(1);     
   Declare vRedondearA5  tinyInt(1);     
   Declare vHayRegistros tinyInt(1);     

   Set vResultado    = 1;
   Set vErrorMessage = '';
   Set vHayRegistros =
       Case When (Select count(*)
                  from wrk_fadetall
                  Where id = pID) > 0 then 1
            Else 0
       End;

   If vHayRegistros = 1 then
            
      Set vRedondear =
          Case When (Select codigoTC
                     from wrk_faencabe
                     Where id = pID) = (Select codigoTC
                                        from config) then (Select redondear from config)
               else 0
          End;
      Set vRedondearA5 =
          Case When (Select codigoTC
                     from wrk_faencabe
                     Where id = pID) = (Select codigoTC
                                        from config) then (Select redond5 from config)
               Else 0
          End;
   End if;

   
   If vHayRegistros = 1 then
      
      Update wrk_fadetall Set
         facmont = faccant * Artprec,
         facpive = Case When pAplicarIV = 1 then
                             (Select porcentaje
                             from tarifa_iva
                             Where codigoTarifa = wrk_fadetall.codigoTarifa)
                        Else 0
                   End,
         facdesc = facmont * (facpdesc/100),
         facimve = (facmont - facdesc) * (facpive/100)
      Where id = pID;

      if row_count() = 0 then
         Set vResultado    = 0;
         Set vErrorMessage = '[BD] No se pudo actualizar el detalle de facturas';
      End if;

   End if; 


   if vHayRegistros = 1 and vResultado > 0 then
      
      Set vFacimve = (Select sum(facimve) from wrk_fadetall Where id = pID);
      Set vFacdesc = (Select sum(facdesc) from wrk_fadetall Where id = pID);
      Set vFacmont = (Select sum(facmont) from wrk_fadetall Where id = pID);

      
      
      If vRedondearA5 = 1 then
         Set vFacmont = RedondearA5(vFacmont - vFacdesc + vFacimve);
      End if;

      Update wrk_faencabe Set
          facimve = vFacimve,
          facdesc = vFacdesc,
          
          facmont = vFacmont,
          facmpag = facmont / facnpag,
          facsald = Case When facplazo > 0 then facmont else 0 end
      Where id = pID;

      if row_count() = 0 then
         Set vResultado    = 0;
         Set vErrorMessage = '[BD] No se pudo actualizar el encabezado de facturas';
      End if;
   End if; 

   Select vResultado, vErrorMessage;
END$$
DELIMITER ;
