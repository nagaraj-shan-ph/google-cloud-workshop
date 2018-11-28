-- EXTENSIONS
CREATE EXTENSION IF NOT EXISTS "plpgsql";
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- SCHEMA
CREATE SCHEMA IF NOT EXISTS workshop;

CREATE TABLE workshop.contacts (
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

CREATE TABLE workshop.contact_lists (
  id         BIGSERIAL,
  created_at TIMESTAMP,
  created_by VARCHAR(255),
  name       VARCHAR(255) NOT NULL,
  updated_at TIMESTAMP,
  updated_by VARCHAR(255),
  PRIMARY KEY (id)
);

-- INDEXES
CREATE INDEX "contacts_email_idx" ON workshop.contacts USING btree (email);
CREATE INDEX "contacts_first_name_idx" ON workshop.contacts USING btree (first_name);
CREATE INDEX "contacts_last_name_idx" ON workshop.contacts USING btree (last_name);
CREATE INDEX "contacts_list_id_idx" ON workshop.contacts USING btree (list_id);
CREATE INDEX "list_name_idx" ON workshop.contact_lists USING btree (name);

-- CONSTRAINTS
ALTER TABLE workshop.contacts
  ADD CONSTRAINT FK_contacts_list_id FOREIGN KEY (list_id) REFERENCES contact_lists (id);

ALTER TABLE workshop.contacts
  ADD CONSTRAINT "contacts_email_list_id_unique_key" UNIQUE (email, list_id);

ALTER TABLE workshop.contact_lists
  ADD CONSTRAINT "lists_name_unique_key" UNIQUE (name);


