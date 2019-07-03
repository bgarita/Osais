use LaFlor;
Drop PROCEDURE `InsertarDocInvDesdeNC`;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarDocInvDesdeNC`(
  IN `pFacnume` int(10)
)
BEGIN
  
  
  Declare vContinuar tinyint(1);
  Declare vMensajeEr varchar(500);

  Set vContinuar = 1;
  Set vMensajeEr = '';

  
  If pFacnume > 0 then
      Set pFacnume = pFacnume * -1;
  End if;


  If (vContinuar = 1 and ConsultarDocumento(pFacnume, 'E', 6) = 1) then
      Set vContinuar = 0;
      Set vMensajeEr = Concat('[BD] La NC # ',pFacnume, ' ya existe en inventarios.');
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
      Trim(Cast(Abs(facnume) as char(10))) as movdocu,
      'E'     as movtimo,
      'NC'    as movorco,
      Concat('Notas de crédito -- ',dtoc(facfech)) as movdesc,
      facfech as movfech,
      tipoca,
      user,
      4       as movtido,
      ' '     as movsolic,
      now(),
      codigoTC
    From faencabe
    Where facnume = pFacnume and facnd > 0;


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
           Trim(Cast(Abs(facnume) as char(10))) as movdocu,
           'E' as movtimo,
           artcode,
           bodega,
           Abs(faccant) as movcant,
           Abs(artcosp) as movcoun,
           Abs(artprec),
           Abs(facimve),
           Abs(facdesc),
           4 as movtido
      From fadetall
      Where facnume = pFacnume and facnd > 0;

      If Row_count() = 0 then
         Set vContinuar = 0;
         Set vMensajeEr = '[BD] No se pudo crear el detalle del documento';
      End if;
    End if;

  Select vContinuar,vMensajeEr;
END$$
DELIMITER ;
