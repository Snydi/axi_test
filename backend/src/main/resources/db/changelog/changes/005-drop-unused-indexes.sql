--liquibase formatted sql

--changeset axi:005-drop-unused-indexes
DROP INDEX idx_clients_name;
DROP INDEX idx_clients_list_order;
DROP INDEX idx_loan_decisions_status;
DROP INDEX idx_loan_agreements_signature_status;

--rollback CREATE INDEX idx_clients_name ON clients (last_name, first_name, middle_name);
--rollback CREATE INDEX idx_clients_list_order ON clients (last_name, first_name, id);
--rollback CREATE INDEX idx_loan_decisions_status ON loan_decisions (status);
--rollback CREATE INDEX idx_loan_agreements_signature_status ON loan_agreements (signature_status);
