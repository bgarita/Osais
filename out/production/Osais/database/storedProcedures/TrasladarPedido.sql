DROP PROCEDURE if EXISTS TrasladarPedido;

delimiter $$


CREATE DEFINER=`root`@`localhost` PROCEDURE `TrasladarPedido`(
	IN `pFacnume` integer,
	IN `pArtcode` varchar(20),
	IN `pBodega` char(3)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
	Declare vHayError tinyInt;     
	Declare vError varchar(500);   
	Declare vBorrarLinea tinyInt;  
	Declare vReservado Float;      
	Declare vExisteEnc tinyInt;    
	Declare vExisteDet tinyInt;    
	
	Set vHayError  = 0;
	Set vError     = '';
	Set vExisteEnc = 0;
	
	Set vReservado = IfNull((
		Select Reservado from pedidod
		Where facnume = pFacnume
		and artcode = pArtcode
		and bodega  = pBodega),0);
	
	If vReservado = 0 then
		Set vHayError = 1;
		Set vError    = '[BD] No se puede enviar a facturación una línea con reservado cero';
	End if;
	
	If vHayError = 0 then
		Set vExisteEnc = If(Exists(Select facnume from pedidofe Where facnume = pFacnume), 1, 0);
		
		INSERT INTO `pedidofd`
			(`facnume`,
			`artcode`,
			`bodega`,
			`faccant`,
			`reservado`,
			`fechares`,
			`tempres`,
			`artprec`,
			`facimve`,
			`facpive`,
			`facdesc`,
			`facmont`,
			`artcosp`,
			`facestado`,
			`fechaped`,
			`codigoTarifa`)
			SELECT 
			    `facnume`,
			    `artcode`,
			    `bodega`,
			    `faccant`,
			    `reservado`,
			    `fechares`,
			    `artprec`, -- Debe ir dos veces
			    `artprec`,
			    `facimve`,
			    `facpive`,
			    `facdesc`,
			    `facmont`,
			    `artcosp`,
			    `facestado`,
			    `fechaped`,
			    `codigoTarifa`
			FROM `pedidod`
			Where facnume = pFacnume
			and artcode = pArtcode
			and bodega  = pBodega;
		
		-- Insert into pedidofd
-- 			Select * from pedidod
-- 			Where facnume = pFacnume
-- 			and artcode = pArtcode
-- 			and bodega  = pBodega;
		
		If (Select count(*)
			  from pedidofd
			  Where facnume = pFacnume
			  and artcode = pArtcode and bodega  = pBodega) = 0 then
			
			Set vHayError = 1;
			Set vError = '[BD] Esta línea no se pudo enviar a facturación.';
		
		End if;
	End if; 
	
	If vHayError = 0 then
		Set vBorrarLinea =
			If(Exists(Select faccant from pedidod
			  Where facnume = pFacnume
			  and artcode = pArtcode
			  and bodega  = pBodega
			  and faccant > 0), 0, 1);
		
		If vBorrarLinea = 1 then
			Delete from pedidod
			Where facnume = pFacnume
			and artcode = pArtcode
			and bodega  = pBodega;
		Else
			Update pedidod Set Reservado = 0
			Where facnume = pFacnume
			and artcode = pArtcode
			and bodega  = pBodega;
		End if;
	
		If row_count() = 0 then
			Set vHayError = 1;
			Set vError    = '[BD] No se pudo actualizar la tabla de pedidos.';
		End if;
	End if;
	
	If vHayError = 0 and vExisteEnc = 0 then
		INSERT INTO `pedidofe`
		       (`facnume`,
		        `clicode`,
		        `factipo`,
		        `chequeotar`,
		        `vend`,
		        `terr`,
		        `facfech`,
		        `facplazo`,
		        `facimve`,
		        `facdesc`,
		        `facmont`,
		        `user`,
		        `precio`,
		        `facivi`)
		  Select
		        `facnume`,
		        `clicode`,
		        `factipo`,
		        `chequeotar`,
		        `vend`,
		        `terr`,
		        `facfech`,
		        `facplazo`,
		        `facimve`,
		        `facdesc`,
		        `facmont`,
		        `user`,
		        `precio`,
		        `facivi`
		  From pedidoe
		  Where facnume = pFacnume;
		If row_count() = 0 then
			Set vHayError = 1;
			Set vError = '[BD] No se pudo agregar el encabezado del pedido';
		End if;
	End if;
	
	Select vHayError, vError;

END$$

delimiter ;