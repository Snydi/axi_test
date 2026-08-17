--liquibase formatted sql

--changeset axi:004-create-clients-pagination-index
CREATE INDEX idx_clients_list_order
    ON clients (last_name, first_name, id);

--rollback DROP INDEX idx_clients_list_order;
