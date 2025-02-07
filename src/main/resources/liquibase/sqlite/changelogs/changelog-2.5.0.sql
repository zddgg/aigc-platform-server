-- liquibase formatted sql

-- changeset wenliang:1
create table am_model_config
(
    id             integer primary key autoincrement,
    mc_id          varchar(255)     null,
    mc_name        varchar(255)     null,
    mc_params_json text             null,
    am_type        varchar(255)     null,
    mf_id          varchar(255)     null,
    pa_id          varchar(255)     null,
    text           text             null,
    save_audio     bit              null,
    show_flag      bit default true null
);

create table am_model_file
(
    id       integer primary key autoincrement,
    mf_id    varchar(255) null,
    am_type  varchar(255) null,
    mf_group varchar(255) null,
    mf_role  varchar(255) null,
    mf_json  text         null
);

alter table ref_audio_entity
    rename ref_audio_id to pa_id;
alter table ref_audio_entity
    rename audio_group to pa_group;
alter table ref_audio_entity
    rename group_sort_order to pa_group_sort;
alter table ref_audio_entity
    rename group_show_flag to pa_group_show;
alter table ref_audio_entity
    rename audio_name to pa_role;
alter table ref_audio_entity
    rename gender to pa_role_gender;
alter table ref_audio_entity
    rename age_group to pa_role_age;
alter table ref_audio_entity
    rename language to pa_role_lang;
alter table ref_audio_entity
    rename tags to pa_role_tags;
alter table ref_audio_entity
    rename avatar to pa_role_avatar;
alter table ref_audio_entity
    rename mood_name to pa_mood;
alter table ref_audio_entity
    rename mood_avatar to pa_mood_avatar;
alter table ref_audio_entity
    rename mood_audio_name to pa_audio;
alter table ref_audio_entity
    rename mood_audio_text to pa_audio_text;
alter table ref_audio_entity
    rename mood_audio_lang to pa_audio_lang;
alter table ref_audio_entity
    rename mood_audio_tags to pa_audio_tags;

alter table ref_audio_entity
    rename to am_prompt_audio;

create table am_server
(
    id          integer primary key autoincrement,
    name        varchar(255) null,
    host        varchar(255) null,
    path        varchar(255) null,
    api_version varchar(255) null
);

insert into am_server (name, host, path, api_version)
values ('gpt-sovits', 'http://127.0.0.1:16860', '/', 'v1');
insert into am_server (name, host, path, api_version)
values ('fish-speech', 'http://127.0.0.1:16861', '/v1/tts', 'v1');
insert into am_server (name, host, path, api_version)
values ('edge-tts', 'http://127.0.0.1:16862', '/tts', 'v1');
insert into am_server (name, host, path, api_version)
values ('chat-tts', 'http://127.0.0.1:16863', '/tts', 'v1');
insert into am_server (name, host, path, api_version)
values ('cosy-voice', 'http://127.0.0.1:16864', '/tts', 'v1');

create table chapter_info
(
    id                    integer primary key autoincrement,
    project_id            varchar(255) null,
    chapter_id            varchar(255) null,
    para_index            int          null,
    sent_index            int          null,
    text_id               varchar(255) null,
    text                  longtext     null,
    text_lang             varchar(255) null,
    text_sort             int          null,
    dialogue_flag         bit          null,
    role                  varchar(255) null,
    gender                varchar(255) null,
    age                   varchar(255) null,
    am_type               varchar(255) null,
    am_pa_id              varchar(255) null,
    am_pa_group           varchar(255) null,
    am_pa_role            varchar(255) null,
    am_pa_mood            varchar(255) null,
    am_pa_audio           varchar(255) null,
    am_pa_audio_text      varchar(255) null,
    am_pa_audio_lang      varchar(255) null,
    am_mf_id              varchar(255) null,
    am_mf_group           varchar(255) null,
    am_mf_role            varchar(255) null,
    am_mf_json            text         null,
    am_mc_id              varchar(255) null,
    am_mc_name            varchar(255) null,
    am_mc_params_json     text         null,
    audio_volume          double       null,
    audio_speed           double       null,
    audio_interval        int          null,
    audio_length          bigint       null,
    audio_files           varchar(512) null,
    audio_task_state      int          null,
    text_markup_info_json text         null
);

create table text_chapter
(
    id               integer primary key autoincrement,
    chapter_id       varchar(255) not null,
    project_id       varchar(255) not null,
    chapter_name     varchar(255) not null,
    content          longtext     null,
    dialogue_pattern varchar(255) null,
    sort_order       int          null
);

create table text_common_role
(
    id                integer primary key autoincrement,
    project_id        varchar(255) null,
    role              varchar(255) null,
    gender            varchar(255) null,
    age               varchar(255) null,
    am_type           varchar(255) null,
    am_pa_id          varchar(255) null,
    am_pa_group       varchar(255) null,
    am_pa_role        varchar(255) null,
    am_pa_mood        varchar(255) null,
    am_pa_audio       varchar(255) null,
    am_pa_audio_text  varchar(255) null,
    am_pa_audio_lang  varchar(255) null,
    am_mf_id          varchar(255) null,
    am_mf_group       varchar(255) null,
    am_mf_role        varchar(255) null,
    am_mf_json        text         null,
    am_mc_id          varchar(255) null,
    am_mc_name        varchar(255) null,
    am_mc_params_json text         null
);

create table text_project
(
    id              integer primary key autoincrement,
    project_id      varchar(255) not null,
    project_name    varchar(255) not null,
    project_type    varchar(255) not null,
    content         longtext     null,
    chapter_pattern varchar(255) null,
    constraint UK4007hmg24ocw5td2jia1xvk6
        unique (project_name)
);

create table text_role
(
    id                integer primary key autoincrement,
    project_id        varchar(255) null,
    chapter_id        varchar(255) null,
    role              varchar(255) null,
    gender            varchar(255) null,
    age               varchar(255) null,
    am_type           varchar(255) null,
    am_pa_id          varchar(255) null,
    am_pa_group       varchar(255) null,
    am_pa_role        varchar(255) null,
    am_pa_mood        varchar(255) null,
    am_pa_audio       varchar(255) null,
    am_pa_audio_text  varchar(255) null,
    am_pa_audio_lang  varchar(255) null,
    am_mf_id          varchar(255) null,
    am_mf_group       varchar(255) null,
    am_mf_role        varchar(255) null,
    am_mf_json        text         null,
    am_mc_id          varchar(255) null,
    am_mc_name        varchar(255) null,
    am_mc_params_json text         null
);

create table text_role_inference
(
    id         integer primary key autoincrement,
    project_id varchar(255) null,
    chapter_id varchar(255) null,
    role       varchar(255) null,
    gender     varchar(255) null,
    age        varchar(255) null,
    mood       varchar(255) null,
    text_index varchar(255) null
);

create table tm_server
(
    id             integer primary key autoincrement,
    name           varchar(255) null,
    interface_type varchar(255) null,
    host           varchar(255) null,
    path           varchar(255) null,
    api_key        varchar(255) null,
    model          varchar(255) null,
    temperature    float        null,
    max_tokens     int          null,
    active         bit          null,
    app_id         varchar(255) null,
    api_secret     varchar(255) null
);

drop table if exists audio_server_config_entity;
drop table if exists chapter_info_entity;
drop table if exists chat_model_config_entity;
drop table if exists chat_model_template_entity;
drop table if exists chat_tts_config_entity;
drop table if exists edge_tts_config_entity;
drop table if exists edge_tts_setting_entity;
drop table if exists fish_speech_config_entity;
drop table if exists fish_speech_model_entity;
drop table if exists gpt_sovits_config_entity;
drop table if exists gpt_sovits_model_entity;
drop table if exists role_inference_entity;
drop table if exists text_chapter_entity;
drop table if exists text_common_role_entity;
drop table if exists text_project_entity;
drop table if exists text_role_entity;

