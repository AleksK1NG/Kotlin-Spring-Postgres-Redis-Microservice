CREATE EXTENSION IF NOT EXISTS citext;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE SCHEMA microservices;


CREATE TABLE IF NOT EXISTS microservices.bank_accounts
(
    bank_account_id UUID                         DEFAULT uuid_generate_v4(),
    email           VARCHAR(250) UNIQUE NOT NULL CHECK ( email <> '' ),
    phone           VARCHAR(60) UNIQUE  NOT NULL CHECK ( phone <> '' ),
    balance         DECIMAL(16, 2)      NOT NULL DEFAULT 0.00,
    currency        VARCHAR(3)          NOT NULL DEFAULT 'USD',
    created_at      TIMESTAMP WITH TIME ZONE     DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE     DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS bank_account_email_idx ON microservices.bank_accounts (email);
