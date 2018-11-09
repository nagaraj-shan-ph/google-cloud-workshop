-- EXTENSIONS
CREATE EXTENSION IF NOT EXISTS "plpgsql";
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- SCHEMA
CREATE SCHEMA IF NOT EXISTS clms;

CREATE TABLE clms.contacts (
  id            BIGSERIAL,
  address1      VARCHAR(255),
  address2      VARCHAR(255),
  city          VARCHAR(255),
  company       VARCHAR(255),
  country       VARCHAR(255),
  created_at    TIMESTAMP,
  created_by    VARCHAR(255),
  email         VARCHAR(255),
  first_name    VARCHAR(255),
  last_name     VARCHAR(255),
  middle_name   VARCHAR(255),
  mobile        VARCHAR(255),
  state         VARCHAR(255),
  updated_at    TIMESTAMP,
  updated_by    VARCHAR(255),
  zip           VARCHAR(255),
  list_id       BIGINT,
  PRIMARY KEY (id)
);

CREATE TABLE clms.contact_lists (
  id         BIGSERIAL,
  created_at TIMESTAMP,
  created_by VARCHAR(255),
  name       VARCHAR(255) NOT NULL,
  updated_at TIMESTAMP,
  updated_by VARCHAR(255),
  PRIMARY KEY (id)
);

-- INDEXES
CREATE INDEX "contacts_email_idx" ON clms.contacts USING btree (email);
CREATE INDEX "contacts_first_name_idx" ON clms.contacts USING btree (first_name);
CREATE INDEX "contacts_last_name_idx" ON clms.contacts USING btree (last_name);
CREATE INDEX "contacts_list_id_idx" ON clms.contacts USING btree (list_id);
CREATE INDEX "list_name_idx" ON clms.contact_lists USING btree (name);

-- CONSTRAINTS
ALTER TABLE clms.contacts
  ADD CONSTRAINT FK_contacts_list_id FOREIGN KEY (list_id) REFERENCES contact_lists (id);

ALTER TABLE clms.contacts
  ADD CONSTRAINT "contacts_email_list_id_unique_key" UNIQUE (email, list_id);

ALTER TABLE clms.contact_lists
  ADD CONSTRAINT "lists_name_unique_key" UNIQUE (name);


