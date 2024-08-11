-- liquibase formatted sql

-- changeset wenliang:1
alter table text_chapter_entity
    add sort_order int null;