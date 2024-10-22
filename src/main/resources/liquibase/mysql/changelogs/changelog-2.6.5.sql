-- liquibase formatted sql

-- changeset wenliang:1
alter table chapter_info
    add text_mood varchar(255) null after text;

update chapter_info
set text_mood = am_pa_mood
where dialogue_flag = 1;

