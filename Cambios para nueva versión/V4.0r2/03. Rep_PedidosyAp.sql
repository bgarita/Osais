USE `sai`;
DROP procedure IF EXISTS `Rep_PedidosyAp`;

DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `Rep_PedidosyAp`(
  IN  `pArtcode1`  varchar(20),
  IN  `pArtcode2`  varchar(20),
  IN  `pBodega`    varchar(3)
)
BEGIN
	-- Autor: Bosco Garita Azofeifa

	Declare vEmpresa varchar(60);

	Set vEmpresa = (Select empresa from config);


	If pArtcode1 is null or pArtcode1 = '' then
		Select min(artcode) from inarticu into pArtcode1;
	End if;
	If pArtcode2 is null or pArtcode2 = '' then
		Select max(artcode) from inarticu into pArtcode2;
	End if;

	Set pBodega = IfNull(pBodega,'');

	Select
		a.artcode,
		a.bodega,
		b.artdesc,
		a.faccant as Pedido,
		a.reservado,
		d.clidesc,
		a.fechaped,
		a.fechares,
		vEmpresa as Empresa
	From pedidod a
	Inner join inarticu b on b.artcode = a.artcode
	Inner join pedidoe  c on c.facnume = a.facnume
	Inner join inclient d on c.clicode = d.clicode
	Where a.artcode between pArtcode1 and pArtcode2
	and a.bodega = If(pBodega = '', a.bodega, pBodega)
	Order by b.artdesc, a.reservado desc;
END$$

DELIMITER ;

