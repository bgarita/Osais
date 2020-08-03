/*
Se aparece un mensaje de error que dice: 
El documento electrónico # 99999 aún no ha sido aceptado por el Ministerio de Hacienda
pero en la consulta de documentos electrónicos si aparece aceptado, hay que hacer esta
consulta y corregir los campos porque lo que pasó es que la herramienta tuvo algún error
a la hora de actualizar esta tabla.
*/
-- Revisar si un documento electrónico ha sido aceptado.
Select * 
from faestadoDocElect 
Where facnume IN (79673, 79672)
and facnd = 0 and tipoXML = 'v';