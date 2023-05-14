alter table cw_waiting
    drop column reg_waiting_order;

alter table cw_waiting_history
    drop column reg_waiting_order;
