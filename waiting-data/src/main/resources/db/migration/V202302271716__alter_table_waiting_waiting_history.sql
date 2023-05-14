drop index cw_waiting_shop_id_index on cw_waiting;

drop index cw_waiting_history_shop_id_index on cw_waiting_history;

create index cw_waiting_shop_id_operation_date_index
    on cw_waiting (shop_id, operation_date);

create index cw_waiting_history_shop_id_operation_date_index
    on cw_waiting_history (shop_id, operation_date);

