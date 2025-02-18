create table IF NOT EXISTS ticketstatus (
id integer not null,
code varchar(30) not null,
libelle varchar(200) not null,
constraint  pk_ticket_status primary key (id));

create table IF NOT EXISTS caisse (
id integer not null,
libelle varchar(200) not null,
constraint  pk_caisse_id primary key (id));

create table IF NOT EXISTS ticket (
id integer not null,
date_emission date not null,
id_caisse integer not null,
id_status integer not null,
montant  smallint not null,
constraint  pk_ticket_id primary key (id),
constraint  fk_caisse_id foreign key (id_caisse) references caisse(id),
constraint  fk_ticket_status_id foreign key (id_status) references ticketstatus(id)
);

create table IF NOT EXISTS article (
id integer not null,
ticket_id integer not null,
quantite integer null,
libelle varchar(200),
montant smallint not null,
prix_unite smallint not null,
constraint  pk_artile_id primary key (id),
constraint  fk_ticket_id foreign key (ticket_id) references ticket(id)
);



create table IF NOT EXISTS chiffre_affaire (
id integer not null,
id_caisse integer not null,
montant integer null,
date_calcul date not null,
constraint  pk_chiffre_affaire_id primary key (id),
constraint  fk_chiffre_affaire_caisse_id foreign key (id_caisse) references caisse(id)
);

-- gestion des status de tickets
insert into status values(1, 'F','ticket de vente');
insert into status values(2, 'A', 'avoir');
insert into status values(3, 'G', 'ticket de garantie');
insert into status values(4, 'TEST', 'ticket de test');
insert into status values(5, 'R', 'ticket de remboursement');

insert into caisse values(1, 'Caisse A');
insert into caisse values(2, 'Caisse B');
insert into caisse values(3, 'Caisse C');

-- id / date / id_caisse / id_status
insert into ticket values(1, '2025-02-01',1,1, 20.50);
insert into ticket values(2, '2025-02-01',1,1, 1310.00);
insert into ticket values(3, '2025-02-01',1,3, 1300.00); -- garantie
insert into ticket values(4, '2025-02-02',1,1, 285.00);
insert into ticket values(5, '2025-02-02',2,5, -7.00);


-- id article / id ticket / quantite / libelle / prix total / prix unitaire
insert into article values(1, 1,2,'pizza jambon champignon', 10.50, 5.25);
insert into article values(2, 1,1,'pack de biere', 10.00, 7.99);

insert into article values(3, 2,1,'TV', 1300, 1300);
insert into article values(4, 2,1,'pack de biere', 10.00, 10.00);

insert into article values(5, 4,10,'pack de biere', 100.00, 10.00);
insert into article values(6, 4,15,'pizza jambon champignon', 150.00, 10.50);
insert into article values(7, 4,10,'pack d'' eau', 35.00, 3.50);

insert into article values(8, 5,2,'pack d'' eau', -7.00, 3.50);
