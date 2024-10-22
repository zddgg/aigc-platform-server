-- liquibase formatted sql

-- changeset wenliang:1
alter table chapter_info
    add column text_mood text;

update chapter_info
set text_mood = am_pa_mood
where dialogue_flag = 1;