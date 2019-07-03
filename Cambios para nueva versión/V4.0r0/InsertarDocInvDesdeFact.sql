use LaFlor;
Drop procedure InsertarDocInvDesdeFact;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarDocInvDesdeFact`(
  IN `pFacnume` int(10)
)
BEGIN

  
  Declare vContinuar tinyint(1);
  Declare vMensajeEr varchar(500);

  Set vContinuar = 1;
  Set vMensajeEr = '';


  If (vContinuar = 1 and ConsultarDocumento(pFacnume, 'S', 5) = 1) then
      Set vContinuar = 0;
      Set vMensajeEr = Concat('[BD] La factura # ',pFacnume, ' ya existe en inventarios.');
  End if;


  If vContinuar = 1 then
    Insert into inmovime (
      movdocu  ,
      movtimo  ,
      movorco  ,
      Movdesc  ,
      movfech  ,
      tipoca   ,
      user     ,
      movtido  ,
      movsolic ,
      movfechac,
      codigoTC  )
    Select
      Trim(Cast(faencabe.facnume as char(10))) as movdocu,
      'S'       as movtimo,
      'FACTURA' as movorco,
      Concat('Facturación -- ',dtoc(facfech)) as movdesc,
      facfech   as movfech,
      tipoca,
      user,
      8         as movtido,
      ' '       as movsolic,
      now(),
      codigoTC
    From faencabe
    Where facnume = pFacnume and facnd = 0;

    If Row_count() = 0 then
       Set vContinuar = 0;
       Set vMensajeEr = '[BD] No se pudo crear el encabezado del documento';
    End if;
  End if;

  If vContinuar = 1 then
     Insert into inmovimd (
            movdocu,
            movtimo,
            artcode,
            bodega,
            movcant,
            movcoun,
            artprec,
            facimve,
            facdesc,
            movtido)
       Select
            Trim(Cast(facnume as char(10))) as movdocu,
            'S' as movtimo,
            artcode,
            bodega,
            faccant as movcant,
            artcosp as movcoun,
            artprec,
            facimve,
            facdesc,
            8 as movtido
       From fadetall
       Where facnume = pFacnume and facnd = 0;

     If Row_count() = 0 then
        Set vContinuar = 0;
        Set vMensajeEr = '[BD] No se pudo crear el detalle del documento';
     End if;
  End if;

  Select vContinuar,vMensajeEr;
END$$
DELIMITER ;
