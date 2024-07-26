-- liquibase formatted sql

-- changeset wenliang:1
alter table chapter_info_entity
    add sort_order int null;
alter table chapter_info_entity
    add phonetic_info text null;
alter table chapter_info_entity
    add audio_instruct text null;