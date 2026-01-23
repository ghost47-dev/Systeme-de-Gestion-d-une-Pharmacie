create database if not exists pharmacy;

use pharmacy;

create table client
(
    id      int auto_increment primary key,
    name    varchar(20) null,
    surname varchar(20) null,
    phone   int         null,
    constraint client_pk
        unique (phone)
);

create table product
(
    id       int auto_increment primary key,
    name     varchar(100) unique null,
    price    double       null,
    quantity int          null
);

create table sale
(
    id        int auto_increment primary key,
    total_price double,
    client_id int null,
    constraint sale_client_id_fk
        foreign key (client_id) references client (id)
            on update cascade on delete cascade
);

create table sale_product
(
    id         int auto_increment primary key,
    sale_id    int null,
    product_id int null,
    quantity   int null,
    constraint sale_product_product_id_fk
        foreign key (product_id) references product (id)
            on update cascade on delete cascade,
    constraint sale_product_sale_id_fk
        foreign key (sale_id) references sale (id)
            on update cascade on delete cascade
);

create table supplier
(
    id                int auto_increment primary key,
    name              varchar(20)   null,
    phone             int           null,
    no_late_shipments int default 0 null,
    constraint supplier_pk
        unique (phone)
);

create table shipment
(
    id           int auto_increment primary key,
    supplier_id  int        null,
    request_date date       null,
    received     tinyint(1) null,
    arrival_date date       null,
    constraint shipment_supplier_id_fk
        foreign key (supplier_id) references supplier (id)
            on update cascade on delete cascade
);

create table shipment_good
(
    id          int auto_increment primary key,
    shipment_id int    null,
    product_id  int    null,
    price       double null,
    quantity    int    null,
    constraint shipment_good_product_id_fk
        foreign key (product_id) references product (id)
            on update cascade on delete cascade,
    constraint shipment_good_shipment_id_fk
        foreign key (shipment_id) references shipment (id)
            on update cascade on delete cascade
);

create table user
(
    login     varchar(20) not null primary key,
    password  varchar(50) null,
    privilege varchar(10) null
);

INSERT INTO user VALUES ("admin", "admin", "admin");
