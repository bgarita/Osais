use sai;
Select * from casaldo
Where dFecha2 >= '2015-10-31';

start transaction;
Delete from casaldo
Where dFecha2 >= '2015-10-31';
-- commit