*SET STEP ON
SET EXACT ON 
SET EXCLUSIVE ON 
SET DATE BRITISH
SET SAFETY OFF 

** Posibles valores a modificar de acuerdo con el inicio de la contabilidad que se vaya a migrar
SET DEFAULT TO "C:\VCONTA\2"
ano = 2010
mes = 'feb'
nMes = 2
archivo = mes + ALLTRIM(STR(ano))
addField = .t. 
**


DO WHILE(FILE(archivo + ".dbf"))
	ultimoDia = lastDate()
	
	IF addField 
		USE &archivo
		COPY TO ..\Migration\CAT2C.DBF TYPE FOX2X
		ALTER table ..\Migration\CAT2C ADD COLUMN fecha_cier date
		addField = .f.
	ELSE 
		SELECT CAT2C
		APPEND FROM &archivo
	ENDIF
	
	UPDATE ..\Migration\CAT2C SET fecha_cier = ultimoDia WHERE EMPTY(fecha_cier)

	DO addMonth
ENDDO 
UPDATE ..\migration\cat2c SET fnombre = .f. WHERE fnombre <> .t.
UPDATE ..\migration\cat2c SET electa = .f. WHERE electa <> .t.
UPDATE ..\migration\cat2c SET hacienda = .f. WHERE hacienda <> .t.
UPDATE ..\migration\cat2c SET nom_cta = 'Libre' WHERE EMPTY(ALLTRIM(nom_cta ))

CLOSE DATABASES ALL 


** Preparar los movimientos
Select * from aslcg02a INTO CURSOR temp where tipo_comp not in(Select tipo_comp from tiposa)
IF RECCOUNT() > 0
	MESSAGEBOX("Hay movimientos cuyo tipo de asiento es incorrecto",0+16)
	BROWSE
	USE 
	CLOSE DATABASES ALL 
	RETURN 
ENDIF 

CLOSE DATABASES ALL 
USE aslcg02a
COPY TO ..\migration\aslcg02a.DBF TYPE FOX2X 

MESSAGEBOX("Se mostrará la estructura de aslcg02, si el campo fecha_comp está primero que no_refer debe intercambiarlos de posición",0+64)
USE aslcg02 EXCLUSIVE 
MODIFY STRUCTURE
COPY TO ..\migration\aslcg02.DBF TYPE FOX2X

** Corregir referencias en cero
UPDATE ..\migration\aslcg02a SET no_refer = VAL(ALLTRIM(STR(MONTH(fecha_comp))) + ALLTRIM(STR(tipo_comp)) + ALLTRIM(STR(YEAR(fecha_comp)))) WHERE no_refer == 0

** Hacer que los números de asiento sean únicos.
UPDATE ..\migration\aslcg02a SET descrip = ALLTRIM(descrip) + '-' + ALLTRIM(no_comprob) WHERE ALLTRIM(no_comprob) <> '99999' AND tipo_comp <> 99
UPDATE ..\migration\aslcg02a SET no_comprob = ALLTRIM(STR(no_refer))

UPDATE ..\migration\aslcg02 SET descrip = ALLTRIM(descrip) + '-' + ALLTRIM(no_comprob)
UPDATE ..\migration\aslcg02 SET no_comprob = ALLTRIM(STR(no_refer))

CLOSE DATABASES ALL 







PROCEDURE addMonth
	nMes = nMes + 1
	IF nMes = 13
		ano = ano + 1
		nMes = 1
	ENDIF

	DO CASE
		CASE nMes = 1
			mes = "ene"
		CASE nMes = 2
			mes = "feb"
		CASE nMes = 3
			mes = "mar"
		CASE nMes = 4
			mes = "abr"
		CASE nMes = 5
			mes = "may"
		CASE nMes = 6
			mes = "jun"
		CASE nMes = 7
			mes = "jul"
		CASE nMes = 8
			mes = "ago"
		CASE nMes = 9
			mes = "set"
		CASE nMes = 10
			mes = "oct"
		CASE nMes = 11
			mes = "nov"
		CASE nMes = 12
			mes = "dic"
	ENDCASE
	archivo = mes + ALLTRIM(STR(ano))
ENDPROC




FUNCTION lastDate()
	LOCAL fecha
	fecha = CTOD("01/" + ALLTRIM(STR(nMes))+ "/" + ALLTRIM(STR(ano)))
	fecha = GOMONTH(fecha,1)
	fecha = fecha - 1
	RETURN fecha
ENDFUNC 