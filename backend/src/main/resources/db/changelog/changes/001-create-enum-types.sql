--liquibase formatted sql

--changeset axi:001-create-enum-types
CREATE TYPE gender_type AS ENUM ('MALE', 'FEMALE', 'OTHER');
CREATE TYPE marital_status_type AS ENUM ('SINGLE', 'MARRIED', 'DIVORCED', 'WIDOWED', 'OTHER');
CREATE TYPE loan_decision_status AS ENUM ('PENDING', 'APPROVED', 'DENIED');
CREATE TYPE signature_status_type AS ENUM ('UNSIGNED', 'SIGNED');

--rollback DROP TYPE signature_status_type;
--rollback DROP TYPE loan_decision_status;
--rollback DROP TYPE marital_status_type;
--rollback DROP TYPE gender_type;
