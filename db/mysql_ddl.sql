create table if not exists audio_server_config_entity
(
    id          int auto_increment
        primary key,
    name        varchar(255) null,
    host        varchar(255) null,
    path        varchar(255) null,
    api_version varchar(255) null
);

create table if not exists chapter_info_entity
(
    id                  int auto_increment
        primary key,
    project_id          varchar(255) null,
    chapter_id          varchar(255) null,
    paragraph_index     int          null,
    sentence_index      int          null,
    text                longtext     null,
    text_lang           varchar(255) null,
    dialogue_flag       bit          null,
    role                varchar(255) null,
    audio_model_type    varchar(255) null,
    audio_model_id      varchar(255) null,
    audio_config_id     varchar(255) null,
    ref_audio_id        varchar(255) null,
    audio_volume        double       null,
    audio_speed         double       null,
    next_audio_interval int          null,
    audio_state         int          null,
    audio_length        bigint       null,
    export_flag         bit          null
);

create table if not exists chat_model_config_entity
(
    id             int auto_increment
        primary key,
    name           varchar(255) null,
    interface_type varchar(255) null,
    host           varchar(255) null,
    path           varchar(255) null,
    api_key        varchar(255) null,
    model          varchar(255) null,
    temperature    varchar(255) null,
    max_tokens     varchar(255) null,
    active         bit          null,
    app_id         varchar(255) null,
    api_secret     varchar(255) null
);

create table if not exists chat_model_template_entity
(
    id             int auto_increment
        primary key,
    template_name  varchar(255) null,
    interface_type varchar(255) null,
    host           varchar(255) null,
    path           varchar(255) null,
    api_key        varchar(255) null,
    model          varchar(255) null,
    temperature    varchar(255) null,
    max_tokens     varchar(255) null,
    app_id         varchar(255) null,
    api_secret     varchar(255) null
);

create table if not exists chat_tts_config_entity
(
    id                 int auto_increment
        primary key,
    config_id          varchar(255) null,
    config_name        varchar(255) null,
    temperature        float        null,
    top_p              float        null,
    top_k              int          null,
    audio_seed_input   int          null,
    text_seed_input    int          null,
    refine_text_flag   bit          null,
    refine_text_params varchar(512) null
);

create table if not exists edge_tts_config_entity
(
    id              int auto_increment
        primary key,
    config_id       varchar(255) null,
    name            varchar(255) null,
    short_name      varchar(255) null,
    gender          varchar(255) null,
    locale          varchar(255) null,
    suggested_codec varchar(255) null,
    friendly_name   varchar(255) null,
    status          varchar(255) null,
    voice_tag       text         null
);

create table if not exists edge_tts_setting_entity
(
    id        int auto_increment
        primary key,
    en_name   varchar(255) null,
    zh_name   varchar(255) null,
    text      varchar(255) null,
    show_flag bit          null
);

create table if not exists fish_speech_config_entity
(
    id                 int auto_increment
        primary key,
    config_id          varchar(255) null,
    config_name        varchar(255) null,
    temperature        float        null,
    top_p              float        null,
    repetition_penalty float        null,
    model_id           varchar(255) null,
    mood_audio_id      varchar(255) null
);

create table if not exists fish_speech_model_entity
(
    id          int auto_increment
        primary key,
    model_id    varchar(255) null,
    model_group varchar(512) null,
    model_name  varchar(512) null,
    ckpt        varchar(512) null,
    pth         varchar(512) null
);

create table if not exists gpt_sovits_config_entity
(
    id                 int auto_increment
        primary key,
    config_id          varchar(255) null,
    config_name        varchar(255) null,
    temperature        float        null,
    top_p              float        null,
    top_k              int          null,
    repetition_penalty float        null,
    batch_size         int          null,
    parallel_infer     bit          null,
    split_bucket       bit          null,
    seed               int          null,
    text_split_method  varchar(255) null,
    fragment_interval  float        null,
    speed_factor       float        null,
    model_id           varchar(255) null,
    mood_audio_id      varchar(255) null
);

create table if not exists gpt_sovits_model_entity
(
    id          int auto_increment
        primary key,
    model_id    varchar(255) null,
    model_group varchar(512) null,
    model_name  varchar(512) null,
    ckpt        varchar(512) null,
    pth         varchar(512) null
);

create table if not exists ref_audio_entity
(
    id               int auto_increment
        primary key,
    ref_audio_id     varchar(255) null,
    audio_group      varchar(255) null,
    group_sort_order int          null,
    group_show_flag  bit          null,
    audio_name       varchar(255) null,
    gender           varchar(255) null,
    age_group        varchar(255) null,
    language         varchar(255) null,
    tags             varchar(255) null,
    avatar           varchar(255) null,
    mood_name        varchar(255) null,
    mood_avatar      varchar(255) null,
    mood_audio_name  varchar(255) null,
    mood_audio_text  varchar(255) null,
    mood_audio_lang  varchar(255) null,
    mood_audio_tags  varchar(255) null
);

create table if not exists role_inference_entity
(
    id         int auto_increment
        primary key,
    project_id varchar(255) null,
    chapter_id varchar(255) null,
    role       varchar(255) null,
    gender     varchar(255) null,
    age_group  varchar(255) null,
    mood       varchar(255) null,
    text_index varchar(255) null
);

create table if not exists text_chapter_entity
(
    id               int auto_increment
        primary key,
    chapter_id       varchar(255) not null,
    project_id       varchar(255) not null,
    chapter_name     varchar(255) not null,
    content          longtext     null,
    dialogue_pattern varchar(255) null
);

create table if not exists text_common_role_entity
(
    id               int auto_increment
        primary key,
    project_id       varchar(255) null,
    role             varchar(255) null,
    gender           varchar(255) null,
    age_group        varchar(255) null,
    audio_model_type varchar(255) null,
    audio_model_id   varchar(255) null,
    audio_config_id  varchar(255) null,
    ref_audio_id     varchar(255) null
);

create table if not exists text_project_entity
(
    id              int auto_increment
        primary key,
    project_id      varchar(255) not null,
    project_name    varchar(255) not null,
    content         longtext     null,
    chapter_pattern varchar(255) null,
    constraint UK4007hmg24ocw5td2jia1xvk6
        unique (project_name)
);

create table if not exists text_role_entity
(
    id               int auto_increment
        primary key,
    project_id       varchar(255) null,
    chapter_id       varchar(255) null,
    role             varchar(255) null,
    gender           varchar(255) null,
    age_group        varchar(255) null,
    audio_model_type varchar(255) null,
    audio_model_id   varchar(255) null,
    audio_config_id  varchar(255) null,
    ref_audio_id     varchar(255) null
);

