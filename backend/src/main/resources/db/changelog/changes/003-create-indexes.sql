--liquibase formatted sql

--changeset axi:003-create-indexes
CREATE INDEX idx_clients_name
    ON clients (last_name, first_name, middle_name);
CREATE INDEX idx_employments_client_id
    ON employments (client_id);
CREATE INDEX idx_loan_applications_client_id
    ON loan_applications (client_id);
CREATE INDEX idx_loan_decisions_status
    ON loan_decisions (status);
CREATE INDEX idx_loan_agreements_signature_status
    ON loan_agreements (signature_status);

--rollback DROP INDEX idx_loan_agreements_signature_status;
--rollback DROP INDEX idx_loan_decisions_status;
--rollback DROP INDEX idx_loan_applications_client_id;
--rollback DROP INDEX idx_employments_client_id;
--rollback DROP INDEX idx_clients_name;
