drop procedure if exists `InsertarArticulo`;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertarArticulo`(
	IN `pArtcode` varchar(20),
	IN `pArtdesc` varchar(50),
	IN `pBarcode` varchar(20),
	IN `pArtfam` char(4),
	IN `pArtcosd` decimal(14,4),
	IN `pArtcost` decimal(14,4),
	IN `pArtcosp` decimal(14,4),
	IN `pArtcosa` decimal(14,4),
	IN `pArtcosfob` decimal(14,4),
	IN `pArtpre1` decimal(12,2),
	IN `pArtgan1` decimal(7,4),
	IN `pArtpre2` decimal(12,2),
	IN `pArtgan2` decimal(7,4),
	IN `pArtpre3` decimal(12,2),
	IN `pArtgan3` decimal(7,4),
	IN `pArtpre4` decimal(12,2),
	IN `pArtgan4` decimal(7,4),
	IN `pArtpre5` decimal(12,2),
	IN `pArtgan5` decimal(7,4),
	IN `pProcode` varchar(15),
	IN `pArtmaxi` decimal(12,4),
	IN `pArtiseg` decimal(10,4),
	IN `pArtdurp` decimal(8,2),
	IN `pArtfech` datetime,
	IN `pCodigoTarifa` VARCHAR(3),
	IN `pOtroc` varchar(10),
	IN `pAltarot` tinyint(1),
	IN `pAplicaOferta` tinyInt(1),
	IN `pVinternet` tinyInt(1),
	IN `pArtObse` varchar(1500),
	IN `pArtFoto` varchar(250)
)
BEGIN
  # Autor: Bosco Garita Azofeifa

  If (ConsultarArticulo(pArtcode, 1) is null) then
    Insert into inarticu (
      Artcode ,
      Artdesc ,
      Barcode ,
      Artfam  ,
      Artcosd ,
      Artcost ,
      Artcosp ,
      Artcosa ,
      Artcosfob,
      Artpre1 ,
      Artgan1 ,
      Artpre2 ,
      Artgan2 ,
      Artpre3 ,
      Artgan3 ,
      Artpre4 ,
      Artgan4 ,
      Artpre5 ,
      Artgan5 ,
      Procode ,
      Artmaxi ,
      Artiseg ,
      Artdurp ,
      Artfech ,
      CodigoTarifa ,
      Otroc   ,
      Altarot ,
	 aplicaOferta, -- Bosco agregado 09/03/2014
      Vinternet,
      ArtObse,
      ArtFoto )
    Values (
      pArtcode ,
      pArtdesc ,
      pBarcode ,
      pArtfam  ,
      pArtcosd ,
      pArtcost ,
      pArtcosp ,
      pArtcosa ,
      pArtcosfob ,
      pArtpre1 ,
      pArtgan1 ,
      pArtpre2 ,
      pArtgan2 ,
      pArtpre3 ,
      pArtgan3 ,
      pArtpre4 ,
      pArtgan4 ,
      pArtpre5 ,
      pArtgan5 ,
      pProcode ,
      pArtmaxi ,
      pArtiseg ,
      pArtdurp ,
      pArtfech ,
      pCodigoTarifa ,
      pOtroc   ,
      pAltarot ,
      pAplicaOferta, -- Bosco agregado 09/03/2014
      pVinternet,
      pArtObse,
      pArtFoto );
  Else
    Select '[BD] El artículo ya existe';
  End if;
END$$
DELIMITER ;
