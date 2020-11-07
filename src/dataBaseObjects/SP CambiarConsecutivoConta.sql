DROP PROCEDURE if EXISTS `CambiarConsecutivoConta`;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `CambiarConsecutivoConta`(
	IN numero int,
	in tipo tinyInt,
	in tipocon tinyInt
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
	# Autor:    Bosco Garita A. 29/03/2013.
	# Objet:    Cambiar los consecutivos de asientos.
	# Modif:    Bosco Garita A. 12/10/2013
	#		  Agrego el consecutivo de los asientos de recibos de CXC

	
	Declare HayError tinyInt;
	Declare ErrorMessage varchar(200);
	Declare vNo_comprob varchar(10);
	Declare vDescripTipo varchar(25);

	Set HayError = 0;
	Set ErrorMessage = '';
	Set numero = ABS(numero) + 1;
	Set vNo_comprob = lpad(cast(numero AS VARCHAR(10)),10,'0');

		/*
	Si es la primera vez que se agrega un consecutivo la tabla estará vacía.
	*/


	If not exists(select no_comprobv from coconsecutivo) then
		Insert into coconsecutivo(no_comprobv) values('0000000000');
	End if;


	/*
	Hago una revisión para determinar si el número existe. El número que se debe
	establecer es el último número utilizado, por esa razón se suma uno para
	verificar si ya existe.  Después de la verificación se vuelve a restar uno
	para dejar el número original.
	*/

	Case 
		When tipocon = 1 then -- Asiento de ventas
			Set vDescripTipo = 'asiento de ventas';

		When tipocon = 2 then -- Asiento de compras
			Set vDescripTipo = 'asiento de compras';

		When tipocon = 3 then -- Asiento de recibos (CXC)
			Set vDescripTipo = 'asiento de recibos (CXC)';
		Else 
			Set HayError = 1;
			Set ErrorMessage = '[BD CambiarConsecutivoConta] tipo no adecuado';
	End Case;


	# Verificar si el número ya existe.
	If Exists(	Select no_comprob from coasientoe
				Where no_comprob = vNo_comprob
				and tipo_comp = tipo) then

		Set HayError = 1;
		Set ErrorMessage = 
			Concat('[BD CambiarConsecutivoConta] El consecutivo de ', vDescripTipo, ' ya existe.');
	End If;


	/*
		Si no hubo error entonces vuelvo a dejar el número como venía
		originalmente  e inicio el proceso decambiar el consecutivo
		que corresponda.
	*/

	If HayError = 0 then
		Set numero = numero - 1;
		Set vNo_comprob = lpad(cast(numero AS VARCHAR(10)),10,'0');

		Case 
			When tipocon = 1 then -- Asiento de ventas
				Update coconsecutivo 
				set no_comprobv = vNo_comprob,
					tipo_compv = tipo;

			When tipocon = 2 then -- Asiento de compras
				Update coconsecutivo 
				set no_comprobc = vNo_comprob,
					tipo_compc = tipo;

			When tipocon = 3 then -- Asiento de recibos (CXC)
				Update coconsecutivo 
				set no_comprobrv = vNo_comprob,
					tipo_comprv = tipo;
		End Case;
	End if;

	Select HayError as HayError, ErrorMessage as ErrorMessage;

END$$

DELIMITER ;