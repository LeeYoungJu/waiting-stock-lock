create index cw_message_send_history_waiting_id_send_type
    on cw_message_send_history (waiting_id, send_type);