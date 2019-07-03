USE `sai`;
DROP procedure IF EXISTS `Rep_DetalleCXC`;

DELIMITER $$

CREATE DEFINER=`bgarita`@`%` PROCEDURE `Rep_DetalleCXC`(
  IN  `pFacfech1`  datetime,
  IN  `pFacfech2`  datetime,
  IN  `pOrden`     tinyint
)
BEGIN
    -- Autor: Bosco Garita Azofeifa
    Declare vEmpresa varchar(60);
    Declare vFacturas int;        
    Declare vOtrosDoc int;        
    Declare vDocumento int;
    Declare vmonto  double;
    Declare vNconsecutivo int;
    Declare vNeto   double;       

    
    If pOrden is null or pOrden > 3 then
		Set pOrden = 0;
    End if;

    
    Set pFacfech1 = IfNull(pFacfech1,'1900-01-01');
    Set pFacfech2 = IfNull(pFacfech2,Date(Now()));


    
    Set pFacfech1 = date(pFacfech1);
    Set pFacfech2 = date(pFacfech2);


    Select Empresa from config into vEmpresa;


    
    CREATE Temporary TABLE cxc (
        Factura  int(10) default 0,
        MontoFa  double  default 0,
        Ndebito  int(10) default 0,
        MontoND  double  default 0,
        Ncredito int(10) default 0,
        MontoNC  double  default 0,
        Abono    int(10) default 0,  
        MontoAb  double  default 0,
        nConsecutivo int AUTO_INCREMENT primary key);

    
    INSERT INTO cxc (Factura,MontoFa)
        SELECT facnume,facmont * tipoca AS facmont
        FROM faencabe
        WHERE facfech BETWEEN pFacfech1 AND pFacfech2
        AND facnd = 0
        AND facestado = ''
        AND facplazo > 0;

    
    Select count(factura) from cxc into vFacturas;
    Set vFacturas = IfNull(vFacturas,0);

    
    CREATE TEMPORARY TABLE tmpOtros (
        Documento int,
        monto     double,
        Tipo      char(1),      
        procesado char(1) not null default 'N');

    
    INSERT tmpOtros (Documento,Monto,Tipo)
        SELECT  
          recnume,monto*tipoca,'R'
        FROM pagos
        WHERE fecha BETWEEN pFacfech1 AND pFacfech2
        
        UNION ALL
        
        SELECT  
          Abs(facnume), Abs(facmont * tipoca),'C'
        FROM faencabe
        WHERE facfech BETWEEN pFacfech1 AND pFacfech2
        AND facnd > 0
        AND facestado = ''
        AND facplazo > 0
        
        UNION ALL
        
        SELECT  
          facnume, facmont * tipoca,'D'
        FROM faencabe
        WHERE facfech BETWEEN pFacfech1 and pFacfech2
        AND facnd < 0
        AND facestado = ''
        AND facplazo > 0;

    
    Select count(Documento) from tmpOtros Where Tipo = 'R' into vOtrosDoc;
    Set vOtrosDoc = IfNull(vOtrosDoc,0);

    
    WHILE vFacturas < vOtrosDoc DO
        INSERT INTO cxc (Factura) VALUES(0);
        SET vFacturas = vFacturas + 1;
    END WHILE;

    
    Select count(Documento) from tmpOtros Where Tipo = 'C' into vOtrosDoc;
    Set vOtrosDoc = IfNull(vOtrosDoc,0);


    
    
    WHILE vFacturas < vOtrosDoc DO
        INSERT INTO cxc (Factura) VALUES(0);
        SET vFacturas = vFacturas + 1;
    END WHILE;

    
    Select count(Documento) from tmpOtros Where Tipo = 'D' into vOtrosDoc;
    Set vOtrosDoc = IfNull(vOtrosDoc,0);


    
    
    WHILE vFacturas < vOtrosDoc DO
        INSERT INTO cxc (Factura) VALUES(0);
        SET vFacturas = vFacturas + 1;
    END WHILE;


    
    Select count(Documento) from tmpOtros Where Tipo = 'R' into vOtrosDoc;
    Set vOtrosDoc = IfNull(vOtrosDoc,0);

    
    WHILE vOtrosDoc > 0 DO
    
        
        SELECT Documento,monto FROM tmpOtros 
        WHERE procesado = 'N' AND Tipo = 'R' LIMIT 1 INTO vDocumento,vMonto;

        
        SELECT nConsecutivo FROM cxc
        WHERE Abono = 0 limit 1 INTO vNconsecutivo;

        
        UPDATE cxc
        SET abono = vDocumento, MontoAb = vMonto
        WHERE nConsecutivo = vNconsecutivo;

        
        UPDATE tmpOtros
        SET procesado = 'S'
        WHERE documento = vDocumento AND Tipo = 'R' AND procesado = 'N';

        
        SET vOtrosDoc = vOtrosDoc - 1;

    END WHILE;

    
    Select count(Documento) from tmpOtros Where Tipo = 'C' into vOtrosDoc;
    Set vOtrosDoc = IfNull(vOtrosDoc,0);

    
    WHILE vOtrosDoc > 0 DO

        SELECT Documento,monto FROM tmpOtros
        WHERE procesado = 'N' AND Tipo = 'C' LIMIT 1 INTO vDocumento,vMonto;

        SELECT nConsecutivo FROM cxc
        WHERE NCredito = 0 LIMIT 1 INTO vNconsecutivo;

        UPDATE cxc
			SET NCredito = vDocumento, MontoNC = vMonto
        WHERE nConsecutivo = vNconsecutivo;

        UPDATE tmpOtros
			SET procesado = 'S'
        WHERE documento = vDocumento AND Tipo = 'C' and procesado = 'N';

        SET vOtrosDoc = vOtrosDoc - 1;

    End While;

    
    Select count(Documento) from tmpOtros Where Tipo = 'D' into vOtrosDoc;
    Set vOtrosDoc = IfNull(vOtrosDoc,0);

    
    WHILE vOtrosDoc > 0 DO

        SELECT Documento,monto FROM tmpOtros
        Where procesado = 'N' AND Tipo = 'D' LIMIT 1 INTO vDocumento,vMonto;

        SELECT nConsecutivo FROM cxc
        WHERE NDebito = 0 LIMIT 1 INTO vNconsecutivo;

        UPDATE cxc
			SET NDebito = vDocumento, MontoND = vMonto
        WHERE nConsecutivo = vNconsecutivo;

        UPDATE tmpOtros
			SET procesado = 'S'
        WHERE documento = vDocumento AND Tipo = 'D' AND procesado = 'N';

        SET vOtrosDoc = vOtrosDoc - 1;

    END WHILE;

    
    Select Sum(MontoFa + MontoND - MontoNC - MontoAb) from cxc into vNeto;

    
    Case When pOrden = 0 Then
       Select
         vEmpresa as Empresa,
         cxc.*,
         vNeto as Neto
       from cxc order by Factura;

       When pOrden = 1 Then
       Select
         vEmpresa as Empresa,
         cxc.*,
         vNeto as Neto
       from cxc order by NCredito;

       When pOrden = 2 Then
       Select
         vEmpresa as Empresa,
         cxc.*,
         vNeto as Neto
       from cxc order by NDebito;

       When pOrden = 3 Then
       Select
         vEmpresa as Empresa,
         cxc.*,
         vNeto as Neto
       from cxc order by Abono;
    End Case;

    Drop table cxc;
    Drop table tmpOtros;
END$$

DELIMITER ;