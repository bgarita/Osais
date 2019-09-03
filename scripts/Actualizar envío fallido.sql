
-- Este script se usa cuando EnviarFactura2.exe no tuvo éxito a la
-- hora de actualizar la tabla faestadoDocelect.
-- Eso puede suceder cuando la herramienta no se lográ conectar a
-- la base de datos.
-- Bosco 25/08/2019

UPDATE `laflor`.`faestadoDocelect` 
	SET `estado`='1', `descrip`='Enviado', `referencia`='261069' 
WHERE  `facnume`=77503 AND `facnd`=0 AND `tipoxml`='V';