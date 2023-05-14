alter table cw_customer
drop column name;

alter table cw_customer
drop column memo;

alter table cw_customer
drop column visit_count;

alter table cw_customer
drop column cancel_count;

alter table cw_customer
drop column last_visit_date;

drop index cw_customer_shop_id_enc_customer_phone_index on cw_customer;

alter table cw_customer
drop column shop_id;

alter table cw_customer
    add unique (enc_customer_phone);

create index cw_customer_enc_customer_phone_index
    on cw_customer (enc_customer_phone);

