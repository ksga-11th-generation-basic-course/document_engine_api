CREATE DATABASE document_engine;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
--Table users
CREATE TABLE IF NOT EXISTS users
(
    user_id       UUID    DEFAULT uuid_generate_v4() PRIMARY KEY,
    username      VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    profile_image VARCHAR(500),
    is_enabled    BOOLEAN DEFAULT TRUE
    );

--Table Otp
CREATE TABLE IF NOT EXISTS opt_codes
(
    opt_id       UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    digit_code   INT       NOT NULL,
    created_date  TIMESTAMP NOT NULL,
    expired_date TIMESTAMP NOT NULL,
    has_verified BOOLEAN,
    user_id      UUID      NOT NULL,
    CONSTRAINT users_fk FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE
    );

--Table workspaces
CREATE TABLE IF NOT EXISTS workspaces
(
    workspace_id   UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    workspace_name VARCHAR(255) NOT NULL,
    workspace_code VARCHAR(255) NOT NULL,
    created_date   TIMESTAMP    NOT NULL
    );

--Table users_workspaces
CREATE TABLE IF NOT EXISTS user_workspace
(
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id              UUID NOT NULL,
    workspace_id         UUID NOT NULL,
    is_owner             BOOLEAN DEFAULT FALSE,
    accessibility_status BOOLEAN DEFAULT FALSE,
    CONSTRAINT users_fk FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT workspaces_fk FOREIGN KEY (workspace_id) REFERENCES workspaces (workspace_id) ON DELETE CASCADE ON UPDATE CASCADE
    );

--Table documents
CREATE TABLE IF NOT EXISTS documents
(
    document_id  UUID    DEFAULT uuid_generate_v4() PRIMARY KEY,
    title        VARCHAR(255),
    status       BOOLEAN DEFAULT FALSE,
    created_date  TIMESTAMP NOT NULL,
    page_id      UUID,
    workspace_id UUID NOT NULL,
    CONSTRAINT pages_fk FOREIGN KEY (page_id) REFERENCES documents (document_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT workspaces_fk FOREIGN KEY (workspace_id) REFERENCES workspaces (workspace_id) ON DELETE CASCADE ON UPDATE CASCADE
    );

--Table blocks
CREATE TABLE IF NOT EXISTS blocks
(
    block_id      UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    block_type    VARCHAR(255) NOT NULL,
    block_content JSON,
    block_order   INT       NOT NULL,
    document_id UUID NOT NULL,
    CONSTRAINT documents_fk FOREIGN KEY (document_id) REFERENCES documents(document_id) ON DELETE CASCADE ON UPDATE CASCADE
    );

--Table users_documents
CREATE TABLE IF NOT EXISTS user_document
(
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id     UUID NOT NULL,
    document_id UUID NOT NULL,
    is_owner             BOOLEAN DEFAULT FALSE,
    accessibility_status VARCHAR(255) DEFAULT 'NO ACCESS',
    CONSTRAINT users_fk FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT documents_fk FOREIGN KEY (document_id) REFERENCES documents(document_id) ON DELETE CASCADE ON UPDATE CASCADE

    );

--Table tags
CREATE TABLE IF NOT EXISTS tags
(
    tag_id   UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    tag_name VARCHAR(255) NOT NULL,
    workspace_id UUID NOT NULL,
    CONSTRAINT workspaces_fk FOREIGN KEY (workspace_id) REFERENCES workspaces(workspace_id) ON DELETE CASCADE ON UPDATE CASCADE
    );

--Table tags_documents
CREATE TABLE IF NOT EXISTS tag_document
(
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    tag_id      UUID NOT NULL,
    document_id UUID NOT NULL,
    CONSTRAINT tags_fk FOREIGN KEY (tag_id) REFERENCES tags(tag_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT documents_fk FOREIGN KEY (document_id) REFERENCES documents(document_id) ON DELETE CASCADE ON UPDATE CASCADE
    );

--Table histories
CREATE TABLE IF NOT EXISTS histories
(
    history_id       UUID    DEFAULT uuid_generate_v4() PRIMARY KEY,
    edited_date      TIMESTAMP    NOT NULL,
    edited_by        UUID NOT NULL,
    status           BOOLEAN DEFAULT FALSE,
    document_id      UUID         NOT NULL,
    page_id          UUID         NOT NULL,
    workspace_id     UUID         NOT NULL,
    CONSTRAINT users_fk FOREIGN KEY (edited_by) REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT documents_fk FOREIGN KEY (document_id) REFERENCES documents (document_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT histories_fk FOREIGN KEY (page_id) REFERENCES histories (history_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT workspaces_fk FOREIGN KEY (workspace_id) REFERENCES workspaces (workspace_id) ON DELETE CASCADE ON UPDATE CASCADE
    );

CREATE TABLE IF NOT EXISTS history_block
(
    history_block_id      UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    history_block_type    VARCHAR(255) NOT NULL,
    history_block_content JSON,
    history_block_order   INT       NOT NULL,
    history_id UUID NOT NULL,
    CONSTRAINT histories_fk FOREIGN KEY (history_id) REFERENCES histories(history_id) ON DELETE CASCADE ON UPDATE CASCADE
    );