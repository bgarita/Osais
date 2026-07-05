
DROP PROCEDURE if EXISTS RecalcularSaldoClientes;

Delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `RecalcularSaldoClientes`(
	IN `pnClicode` int
)
LANGUAGE SQL
NOT DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''
BEGIN
	# Autor:        Bosco Garita año 2010.
	# Descripción:  Recalcular el saldo de los clientes.
	# NOTA:         Debe correr dentro de una transacción.
	
	
	If pnClicode is null then --  Recalcular todos los clientes
	
		Update inclient Set clisald = 0, clifeuc = null;
		
		Update inclient
			Set clisald = IfNull((Select sum(facsald * tipoca)
								from faencabe
								Where clicode = inclient.clicode 
								and facsald <> 0
								and facestado <> 'A'),0);
		
		Update inclient
			Set clifeuc = (Select max(facfech)
						from faencabe
						where clicode = inclient.clicode 
						and facnd = 0 
						and facestado <> 'A');
	
	ELSE -- Recalcular un solo cliente
		
		Update inclient Set clisald = 0, clifeuc = null Where clicode = pnClicode;
		
		Update inclient
			Set clisald = IfNull((Select sum(facsald * tipoca)
								from faencabe
								Where clicode = inclient.clicode 
								and facsald <> 0
								and facestado <> 'A'),0)
		Where clicode = pnClicode;
		
		Update inclient
			Set clifeuc = (Select max(facfech)
						from faencabe
						where clicode = inclient.clicode 
						and facnd = 0 
						and facestado <> 'A')
		Where clicode = pnClicode;
	
	End if;

END$$
Delimiter ;