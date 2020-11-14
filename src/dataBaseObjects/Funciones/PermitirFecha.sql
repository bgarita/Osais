DROP FUNCTION if EXISTS `PermitirFecha`;

delimiter $

CREATE DEFINER=`root`@`localhost` FUNCTION `PermitirFecha`(
	`pdFecha` datetime
)
RETURNS tinyint(1)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT 'Determinar que la fecha no esté en un periodo cerrado'
BEGIN
    -- Autor:    Bosco Garita A. 09/02/2011.
    -- Objet:    Validar que la fecha no esté en un período cerrado.
    --           Este SP devuelve un Result Set con dos campos que indican si hubo error o no.
    
    Declare vcMes      char(2);
    Declare vcAno      char(4);
    Declare vcFecha    char(10);
    Declare vdFecha    datetime;
    Declare vnPermitir TinyInt(1);

    Set vnPermitir = 0;

    -- Obtener el mes y año cerrado
    Set vcMes = (Select Cast(MesCerrado as char(2)) from config);
    Set vcAno = (Select Cast(AnoCerrado as char(4)) from config);

    -- Si alguno de estos valores está nulo se asume que nunca se ha hecho un cierre
    If vcMes is null or vcAno is null then
        Set vnPermitir = 1;
    Else
        -- Concateno los valores del período cerrado para establecer el último día como día de cierre
        -- y luego verificar si la fecha recibida es mayor y de ser así esa fecha sería aceptada.
        Set vcFecha = Concat(vcAno,'-', vcMes, '-', '01');
        
        Set vdFecha = last_day(vcFecha);

        If pdFecha > vdFecha then
            Set vnPermitir = 1;
        End if;
    End if;

    Return vnPermitir;
END$
delimiter ;