CREATE DATABASE DocEngine;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
--Table users
CREATE TABLE users
(
    user_id       UUID    DEFAULT uuid_generate_v4() PRIMARY KEY,
    username      VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    profile_image VARCHAR(500),
    is_enabled    BOOLEAN DEFAULT TRUE
);

--Table Otp
CREATE TABLE opt_codes
(
    opt_id       UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    digit_code   INT NOT NULL,
    create_date  TIMESTAMP  NOT NULL,
    expired_date TIMESTAMP  NOT NULL,
    has_verify   BOOLEAN,
    user_id      INT        NOT NULL,
    CONSTRAINT users_fk FOREIGN KEY (user_id) REFERENCES users (user_id)
);

--Table workspaces
CREATE TABLE workspaces
(
    workspace_id   UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    workspace_name VARCHAR(255) NOT NULL,
    workspace_code VARCHAR(255) NOT NULL,
    created_date   TIMESTAMP    NOT NULL
);

--Table users_workspaces
CREATE TABLE users_workspaces
(
    user_id      UUID NOT NULL REFERENCES users (user_id),
    workspace_id UUID NOT NULL REFERENCES workspaces (workspace_id),
    is_owner     BOOLEAN DEFAULT FALSE,
    CONSTRAINT users_workspaces_pk PRIMARY KEY (user_id, workspace_id)
);

--Table documents
CREATE TABLE documents
(
    document_id  UUID    DEFAULT uuid_generate_v4() PRIMARY KEY,
    page_url     VARCHAR,
    status       BOOLEAN DEFAULT FALSE,
    page_id      UUID NOT NULL,
    workspace_id UUID NOT NULL,
    block_id     UUID NOT NULL,
    CONSTRAINT pages_fk FOREIGN KEY (page_id) REFERENCES documents (document_id),
    CONSTRAINT workspaces_fk FOREIGN KEY (workspace_id) REFERENCES workspaces (workspace_id),
    CONSTRAINT blocks_fk FOREIGN KEY (block_id) REFERENCES blocks (block_id)
);

--Table blocks
CREATE TABLE blocks
(
    block_id      UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    block_type    VARCHAR(255) NOT NULL,
    block_content VARCHAR(300),
    block_order   SERIAL       NOT NULL
);

--Table users_documents
CREATE TABLE users_documents
(
    user_id     UUID NOT NULL REFERENCES users (user_id),
    document_id UUID NOT NULL REFERENCES documents (document_id),
    CONSTRAINT users_documents_pk PRIMARY KEY (user_id, document_id)
);

--Table tags
CREATE TABLE tags
(
    tag_id   UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    tag_name VARCHAR(255) NOT NULL
);

--Table tags_documents
CREATE TABLE tags_documents
(
    tag_id      UUID NOT NULL REFERENCES tags (tag_id),
    document_id UUID NOT NULL REFERENCES documents (document_id),
    CONSTRAINT tags_documents_pk PRIMARY KEY (tag_id, document_id)
);

--Table histories
CREATE TABLE histories
(
    history_id       UUID    DEFAULT uuid_generate_v4() PRIMARY KEY,
    version          VARCHAR(255) NOT NULL,
    edited_date      TIMESTAMP    NOT NULL,
    edited_by        VARCHAR(255) NOT NULL,
    page_url         VARCHAR,
    status           BOOLEAN DEFAULT FALSE,
    user_id          UUID         NOT NULL,
    document_id      UUID         NOT NULL,
    page_id          UUID         NOT NULL,
    workspace_id     UUID         NOT NULL,
    history_block_id UUID         NOT NULL,
    CONSTRAINT users_fk FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT documents_fk FOREIGN KEY (document_id) REFERENCES documents (document_id),
    CONSTRAINT histories_fk FOREIGN KEY (page_id) REFERENCES histories (history_id),
    CONSTRAINT workspaces_fk FOREIGN KEY (workspace_id) REFERENCES workspaces (workspace_id),
    CONSTRAINT histories_blocks_fk FOREIGN KEY (history_block_id) REFERENCES histories_blocks (history_block_id)
);

CREATE TABLE histories_blocks
(
    history_block_id      UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    history_block_type    VARCHAR(255) NOT NULL,
    history_block_content VARCHAR(300),
    history_block_order   SERIAL       NOT NULL
);
