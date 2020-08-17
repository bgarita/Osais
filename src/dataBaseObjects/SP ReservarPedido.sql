DROP PROCEDURE if EXISTS ReservarPedido;

Delimiter $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `ReservarPedido`(
	IN `pFacnume` int(10),
	IN `pClicode` int(10),
	IN `pBodega` varchar(4),
	IN `pArtcode` varchar(20),
	IN `pFaccant` double,
	IN `pReservado` double,
	IN `pArtprec` double,
	IN `pFacpive` FLOAT,
	IN `pCodigoTarifa` VARCHAR(3)
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
    -- Autor: Bosco Garita Azofeifa, Octubre 2011
    Declare vReservadoAnterior double;
    Declare vReservadoActual   double;
    Declare vFaccantAnterior   double;
    Declare vFaccantActual     double;
    Declare vDisponible        double;
    Declare vArtcosp           double;  
    Declare vResultado         tinyInt(1);     
    Declare vErrorMessage      varchar(80);    
    Declare vFacimve           double;  
    Declare vFacdesc           double;  
    Declare vFacmont           double;  
    Declare vFechapedAnterior  datetime;       
    Declare vFechapedActual    datetime;       

    Set vResultado    = 1;
    Set vErrorMessage = '';

    Set vArtcosp = (Select artcosp from inarticu where artcode = pArtcode);

    Set vReservadoAnterior =
        (Select sum(reservado)
        from pedidod
        Where Facnume = pFacnume and Bodega = pBodega and artcode = pArtcode);

    Set vFaccantAnterior =
        (Select sum(faccant)
        from pedidod
        Where Facnume = pFacnume and Bodega = pBodega and artcode = pArtcode);

    Set vReservadoAnterior = Ifnull(vReservadoAnterior,0);
    Set vReservadoActual   = vReservadoAnterior + pReservado;
    Set vFaccantAnterior   = Ifnull(vFaccantAnterior,0);
    Set vFaccantActual     = vFaccantAnterior + pFaccant;
    Set vDisponible        = ConsultarExistenciaDisponible(pArtcode,pBodega);

    Set vFechapedAnterior = null;

    If vFaccantAnterior > 0 then
        Select max(fechaped) from pedidod
        Where Facnume = pFacnume and Bodega = pBodega and artcode = pArtcode
        Into vFechapedAnterior;
    End if;

    If vReservadoActual < 0 then
        Set vResultado    = 0;
        Set vErrorMessage = '[BD] La cantidad reservada no puede quedar negativa';
    End if;

    If vFaccantActual < 0 then
        Set vResultado    = 0;
        Set vErrorMessage = '[BD] El pedido no puede quedar negativo';
    End if;

    If vResultado = 1 then
        Set vFaccantActual = vFaccantActual - pReservado;

        # Bosco modificado 21/01/2012.
        # Si no se está reservando nada el sistema debe permitir que se agregue el pedido.
        -- If vDisponible - pReservado < 0 then
        If pReservado > 0 and vDisponible - pReservado < 0 then
            Set vResultado    = 0;
            Set vErrorMessage = '[BD] El disponible para este artículo es insuficiente';
        end if;
    End if;

    Set vFechapedActual = If(vFaccantActual > 0, vFechapedAnterior, null);

    If vFechapedActual is null and vFaccantActual > 0 then
        Set vFechapedActual = now();
    End if;

    If pFaccant <= 0 and pReservado >= 0 then
        If (Select count(*)
            from pedidod
            Where Facnume = pFacnume and Bodega = pBodega and artcode = pArtcode) = 0 then
            Set vResultado    = 0;
            Set vErrorMessage = '[BD] El pedido no tiene registrado este artículo';
        End if;
    End if;

    If vResultado = 1 then
        Update Bodexis
        Set Artreserv = Artreserv + pReservado
        Where Artcode = pArtcode and Bodega = pBodega;

        if row_count() = 0 then
            Set vResultado = 0;
            Set vErrorMessage = '[BD] Ocurrió un error al intentar reservar la cantidad en bodega';
        End if;
    End if; -- If vResultado = 1

    If vResultado = 1 then
        If (Select count(*)
            from pedidod
            Where Facnume = pFacnume and Bodega = pBodega and artcode = pArtcode) > 0 then

            Update pedidod Set
               reservado = vReservadoActual,
               faccant   = vFaccantActual,
               artprec   = pArtprec,
               facmont   = vReservadoActual * pArtprec,
               fechares  = Case When vReservadoActual > 0 and pReservado > 0 then now()
                                When vReservadoActual > 0 and fechares is not null then fechares
                                When vReservadoActual > 0 and fechares is null then now()
                                When vReservadoActual = 0 then null
                                Else null
                           End,
               facpive   = pFacpive,
               artcosp   = vArtcosp,
               codigoTarifa = pCodigoTarifa,
               fechaped  = vFechapedActual
            Where Facnume = pFacnume and Bodega = pBodega and artcode = pArtcode;
        Else
            Insert into pedidod (
              facnume,
              artcode,
              bodega,
              faccant,
              artprec,
              facmont,
              reservado,
              fechares,
              facpive,
              artcosp,
              codigoTarifa,
              fechaped)
            Values (
              pFacnume,
              pArtcode,
              pBodega,
              pFaccant - pReservado,
              pArtprec,
              pReservado * pArtprec,
              pReservado,
              Case When pReservado > 0 then now() else null end,
              pFacpive,
              vArtcosp,
              pCodigoTarifa,
              vFechapedActual);

        End if; -- If (Select count(*)..else..

        if row_count() = 0 then
            Set vResultado    = 0;
            Set vErrorMessage = '[BD] No se pudo actualizar la tabla de pedidos';
        End if;

        if vResultado > 0 then
            Update pedidod Set
                facimve = Round((facmont - facdesc) * (facpive/100),2) 
            Where facnume = pFacnume;

            Set vFacimve = (Select sum(facimve) from pedidod Where facnume = pFacnume);
            Set vFacdesc = (Select sum(facdesc) from pedidod Where facnume = pFacnume);
            Set vFacmont = (Select sum(facmont) from pedidod Where facnume = pFacnume);

            Update pedidoe Set
                facimve = vFacimve,
                facdesc = vFacdesc,
                facmont = vFacmont - vFacdesc + vFacimve
            Where facnume = pFacnume;
        End if; -- if vResultado > 0
    End if; -- If vResultado = 1

    Select vResultado, vErrorMessage;
END$$

Delimiter ;