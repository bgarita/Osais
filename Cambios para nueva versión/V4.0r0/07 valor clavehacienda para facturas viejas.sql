use LaFlor;
-- Preparar la base de datos para que se puedan generar las facturas electrónicas que aún no se han generado
-- para evitar que se repita alguna.

Update faencabe
	Set claveHacienda = '-1'
Where claveHacienda = ' '
and date(facfechac) < '2018-10-01';