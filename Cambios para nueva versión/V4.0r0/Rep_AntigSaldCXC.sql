

USE `LaFlor`;
DROP procedure IF EXISTS `Rep_AntigSaldCXC`;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `Rep_AntigSaldCXC`(
  IN  `pClicode1`  int,
  IN  `pClicode2`  int,
  IN  `pSoloVenc`  tinyint(1),
  IN  `pSaldoMay`  double,
  IN  `pClasif1`   tinyint(2),
  IN  `pClasif2`   tinyint(2),
  IN  `pClasif3`   tinyint(2),
  IN  `pFechaDoc`  tinyint(1),
  IN  `pOrden`     tinyint(1)
)
BEGIN
	-- Autor: Bosco Garita Azofeifa

    Declare vEmpresa varchar(60);

    
    If pOrden is null or pOrden > 5 or pOrden < 1 then
        Set pOrden = 1;
    End if;

    If pClicode1 = 0 then
        Set pClicode1 = (Select min(clicode) from inclient);
    End if;
    If pClicode2 = 0 then
        Set pClicode2 = (Select max(clicode) from inclient);
    End if;

    Set pSaldoMay = IfNull(pSaldoMay,0);
    Set pFechaDoc = IfNull(pFechaDoc,1);


    Select Empresa from config into vEmpresa;


    Create Temporary Table AntigSald
    Select
        vEmpresa as Empresa,
        b.clicode,
        b.clidesc,
        b.clitel1,
        b.clicelu,
        a.facnume,
        If(pFechaDoc,a.facfepa,a.facfech) as FechaRep,
        If(pFechaDoc = 1,'Venc','Emis') as TipoFecha,
        a.facfech,
        a.facplazo,
        a.facfepa as Vence,
        If(DateDiff(Now(),a.facfepa) < 0,0,DateDiff(Now(),a.facfepa)) as DiasVenc,
        a.facmont * a.tipoca as facmont,
        a.facsald * a.tipoca as facsald,
        Concat('0 - ',Cast(pClasif1 as char(3))) as Strclasif1,
        Concat(Cast(pClasif1 + 1 as char(3)),' - ',Cast(pClasif2 as char(3))) as Strclasif2,
        Concat(Cast(pClasif2 + 1 as char(3)),' - n') as Strclasif3
    From faencabe a
    Inner join inclient b on a.clicode = b.clicode
    Where b.clicode between pClicode1 and pClicode2
    and (a.facsald * a.tipoca) > pSaldoMay
    and a.facnd <= 0
    and a.facestado = ''
    and If(pSoloVenc = 1,DateDiff(Now(),a.facfepa) > 0,1);


    Case pOrden
        When 1 then
            Select
                AntigSald.*,
                IF(DiasVenc <= pClasif1, facsald, 0) as Clasif1,
                IF(DiasVenc > pClasif1 and DiasVenc <= pClasif2, facsald, 0) as Clasif2,
                IF(DiasVenc > pClasif2, facsald, 0) as Clasif3
            from AntigSald
            Order by Vence;
        When 2 then
            Select
                AntigSald.*,
                IF(DiasVenc <= pClasif1, facsald, 0) as Clasif1,
                IF(DiasVenc > pClasif1 and DiasVenc <= pClasif2, facsald, 0) as Clasif2,
                IF(DiasVenc > pClasif2, facsald, 0) as Clasif3
            from AntigSald
            Order by clicode;
        When 3 then
            Select
                AntigSald.*,
                IF(DiasVenc <= pClasif1, facsald, 0) as Clasif1,
                IF(DiasVenc > pClasif1 and DiasVenc <= pClasif2, facsald, 0) as Clasif2,
                IF(DiasVenc > pClasif2, facsald, 0) as Clasif3
            from AntigSald
            Order by clidesc;
        When 4 then
            Select
                AntigSald.*,
                IF(DiasVenc <= pClasif1, facsald, 0) as Clasif1,
                IF(DiasVenc > pClasif1 and DiasVenc <= pClasif2, facsald, 0) as Clasif2,
                IF(DiasVenc > pClasif2, facsald, 0) as Clasif3
            from AntigSald
            Order by facnume;
        When 5 then
            Select
                AntigSald.*,
                IF(DiasVenc <= pClasif1, facsald, 0) as Clasif1,
                IF(DiasVenc > pClasif1 and DiasVenc <= pClasif2, facsald, 0) as Clasif2,
                IF(DiasVenc > pClasif2, facsald, 0) as Clasif3
            from AntigSald
            Order by facsald desc;
    End Case;

    Drop table AntigSald;
END$$
DELIMITER ;

