create table if not exists audio_server_config_entity
(
    id          int auto_increment
        primary key,
    name        varchar(255) null,
    host        varchar(255) null,
    path        varchar(255) null,
    api_version varchar(255) null
);

INSERT INTO audio_server_config_entity (name, host, path, api_version)
VALUES ('gpt-sovits', 'http://127.0.0.1:16860', '/tts', 'v2');
INSERT INTO audio_server_config_entity (name, host, path, api_version)
VALUES ('fish-speech', 'http://127.0.0.1:16861', '/v1/invoke', 'v1');
INSERT INTO audio_server_config_entity (name, host, path, api_version)
VALUES ('edge-tts', 'http://127.0.0.1:16862', '/tts', 'v1');
INSERT INTO audio_server_config_entity (name, host, path, api_version)
VALUES ('chat-tts', 'http://127.0.0.1:16863', '/tts', 'v1');

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
    audio_export_flag   bit          null
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

INSERT INTO chat_model_template_entity (template_name, interface_type, host, path, api_key, model, temperature,
                                        max_tokens, app_id, api_secret)
VALUES ('OpenAI', 'OpenAi', 'https://api.openai.com', '/v1/chat/completions', null, 'gpt-3.5-turbo', '0.3', '4096',
        null, null);
INSERT INTO chat_model_template_entity (template_name, interface_type, host, path, api_key, model, temperature,
                                        max_tokens, app_id, api_secret)
VALUES ('Kimi', 'OpenAi', 'https://api.moonshot.cn', '/v1/chat/completions', null, 'moonshot-v1-32k', '0.3', '4096',
        null, null);
INSERT INTO chat_model_template_entity (template_name, interface_type, host, path, api_key, model, temperature,
                                        max_tokens, app_id, api_secret)
VALUES ('DeepSeek', 'OpenAi', 'https://api.deepseek.com', '/v1/chat/completions', null, 'deepseek-chat', '0.3', '4096',
        null, null);
INSERT INTO chat_model_template_entity (template_name, interface_type, host, path, api_key, model, temperature,
                                        max_tokens, app_id, api_secret)
VALUES ('Ollama', 'OpenAi', 'http://127.0.0.1:11434', '/v1/chat/completions', null, 'qwen2:7b', '0.3', '4096', null,
        null);

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

INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('af-ZA-AdriNeural', 'Microsoft Server Speech Text to Speech Voice (af-ZA, AdriNeural)', 'af-ZA-AdriNeural',
        'Female', 'af-ZA', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Adri Online (Natural) - Afrikaans (South Africa)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('af-ZA-WillemNeural', 'Microsoft Server Speech Text to Speech Voice (af-ZA, WillemNeural)',
        'af-ZA-WillemNeural', 'Male', 'af-ZA', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Willem Online (Natural) - Afrikaans (South Africa)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('sq-AL-AnilaNeural', 'Microsoft Server Speech Text to Speech Voice (sq-AL, AnilaNeural)', 'sq-AL-AnilaNeural',
        'Female', 'sq-AL', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Anila Online (Natural) - Albanian (Albania)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('sq-AL-IlirNeural', 'Microsoft Server Speech Text to Speech Voice (sq-AL, IlirNeural)', 'sq-AL-IlirNeural',
        'Male', 'sq-AL', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Ilir Online (Natural) - Albanian (Albania)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('am-ET-AmehaNeural', 'Microsoft Server Speech Text to Speech Voice (am-ET, AmehaNeural)', 'am-ET-AmehaNeural',
        'Male', 'am-ET', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Ameha Online (Natural) - Amharic (Ethiopia)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('am-ET-MekdesNeural', 'Microsoft Server Speech Text to Speech Voice (am-ET, MekdesNeural)',
        'am-ET-MekdesNeural', 'Female', 'am-ET', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Mekdes Online (Natural) - Amharic (Ethiopia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-DZ-AminaNeural', 'Microsoft Server Speech Text to Speech Voice (ar-DZ, AminaNeural)', 'ar-DZ-AminaNeural',
        'Female', 'ar-DZ', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Amina Online (Natural) - Arabic (Algeria)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-DZ-IsmaelNeural', 'Microsoft Server Speech Text to Speech Voice (ar-DZ, IsmaelNeural)',
        'ar-DZ-IsmaelNeural', 'Male', 'ar-DZ', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Ismael Online (Natural) - Arabic (Algeria)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-BH-AliNeural', 'Microsoft Server Speech Text to Speech Voice (ar-BH, AliNeural)', 'ar-BH-AliNeural', 'Male',
        'ar-BH', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Ali Online (Natural) - Arabic (Bahrain)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-BH-LailaNeural', 'Microsoft Server Speech Text to Speech Voice (ar-BH, LailaNeural)', 'ar-BH-LailaNeural',
        'Female', 'ar-BH', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Laila Online (Natural) - Arabic (Bahrain)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-EG-SalmaNeural', 'Microsoft Server Speech Text to Speech Voice (ar-EG, SalmaNeural)', 'ar-EG-SalmaNeural',
        'Female', 'ar-EG', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Salma Online (Natural) - Arabic (Egypt)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-EG-ShakirNeural', 'Microsoft Server Speech Text to Speech Voice (ar-EG, ShakirNeural)',
        'ar-EG-ShakirNeural', 'Male', 'ar-EG', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Shakir Online (Natural) - Arabic (Egypt)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-IQ-BasselNeural', 'Microsoft Server Speech Text to Speech Voice (ar-IQ, BasselNeural)',
        'ar-IQ-BasselNeural', 'Male', 'ar-IQ', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Bassel Online (Natural) - Arabic (Iraq)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-IQ-RanaNeural', 'Microsoft Server Speech Text to Speech Voice (ar-IQ, RanaNeural)', 'ar-IQ-RanaNeural',
        'Female', 'ar-IQ', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Rana Online (Natural) - Arabic (Iraq)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-JO-SanaNeural', 'Microsoft Server Speech Text to Speech Voice (ar-JO, SanaNeural)', 'ar-JO-SanaNeural',
        'Female', 'ar-JO', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Sana Online (Natural) - Arabic (Jordan)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-JO-TaimNeural', 'Microsoft Server Speech Text to Speech Voice (ar-JO, TaimNeural)', 'ar-JO-TaimNeural',
        'Male', 'ar-JO', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Taim Online (Natural) - Arabic (Jordan)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-KW-FahedNeural', 'Microsoft Server Speech Text to Speech Voice (ar-KW, FahedNeural)', 'ar-KW-FahedNeural',
        'Male', 'ar-KW', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Fahed Online (Natural) - Arabic (Kuwait)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-KW-NouraNeural', 'Microsoft Server Speech Text to Speech Voice (ar-KW, NouraNeural)', 'ar-KW-NouraNeural',
        'Female', 'ar-KW', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Noura Online (Natural) - Arabic (Kuwait)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-LB-LaylaNeural', 'Microsoft Server Speech Text to Speech Voice (ar-LB, LaylaNeural)', 'ar-LB-LaylaNeural',
        'Female', 'ar-LB', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Layla Online (Natural) - Arabic (Lebanon)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-LB-RamiNeural', 'Microsoft Server Speech Text to Speech Voice (ar-LB, RamiNeural)', 'ar-LB-RamiNeural',
        'Male', 'ar-LB', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Rami Online (Natural) - Arabic (Lebanon)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-LY-ImanNeural', 'Microsoft Server Speech Text to Speech Voice (ar-LY, ImanNeural)', 'ar-LY-ImanNeural',
        'Female', 'ar-LY', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Iman Online (Natural) - Arabic (Libya)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-LY-OmarNeural', 'Microsoft Server Speech Text to Speech Voice (ar-LY, OmarNeural)', 'ar-LY-OmarNeural',
        'Male', 'ar-LY', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Omar Online (Natural) - Arabic (Libya)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-MA-JamalNeural', 'Microsoft Server Speech Text to Speech Voice (ar-MA, JamalNeural)', 'ar-MA-JamalNeural',
        'Male', 'ar-MA', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Jamal Online (Natural) - Arabic (Morocco)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-MA-MounaNeural', 'Microsoft Server Speech Text to Speech Voice (ar-MA, MounaNeural)', 'ar-MA-MounaNeural',
        'Female', 'ar-MA', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Mouna Online (Natural) - Arabic (Morocco)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-OM-AbdullahNeural', 'Microsoft Server Speech Text to Speech Voice (ar-OM, AbdullahNeural)',
        'ar-OM-AbdullahNeural', 'Male', 'ar-OM', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Abdullah Online (Natural) - Arabic (Oman)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-OM-AyshaNeural', 'Microsoft Server Speech Text to Speech Voice (ar-OM, AyshaNeural)', 'ar-OM-AyshaNeural',
        'Female', 'ar-OM', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Aysha Online (Natural) - Arabic (Oman)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-QA-AmalNeural', 'Microsoft Server Speech Text to Speech Voice (ar-QA, AmalNeural)', 'ar-QA-AmalNeural',
        'Female', 'ar-QA', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Amal Online (Natural) - Arabic (Qatar)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-QA-MoazNeural', 'Microsoft Server Speech Text to Speech Voice (ar-QA, MoazNeural)', 'ar-QA-MoazNeural',
        'Male', 'ar-QA', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Moaz Online (Natural) - Arabic (Qatar)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-SA-HamedNeural', 'Microsoft Server Speech Text to Speech Voice (ar-SA, HamedNeural)', 'ar-SA-HamedNeural',
        'Male', 'ar-SA', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Hamed Online (Natural) - Arabic (Saudi Arabia)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-SA-ZariyahNeural', 'Microsoft Server Speech Text to Speech Voice (ar-SA, ZariyahNeural)',
        'ar-SA-ZariyahNeural', 'Female', 'ar-SA', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Zariyah Online (Natural) - Arabic (Saudi Arabia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-SY-AmanyNeural', 'Microsoft Server Speech Text to Speech Voice (ar-SY, AmanyNeural)', 'ar-SY-AmanyNeural',
        'Female', 'ar-SY', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Amany Online (Natural) - Arabic (Syria)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-SY-LaithNeural', 'Microsoft Server Speech Text to Speech Voice (ar-SY, LaithNeural)', 'ar-SY-LaithNeural',
        'Male', 'ar-SY', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Laith Online (Natural) - Arabic (Syria)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-TN-HediNeural', 'Microsoft Server Speech Text to Speech Voice (ar-TN, HediNeural)', 'ar-TN-HediNeural',
        'Male', 'ar-TN', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Hedi Online (Natural) - Arabic (Tunisia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-TN-ReemNeural', 'Microsoft Server Speech Text to Speech Voice (ar-TN, ReemNeural)', 'ar-TN-ReemNeural',
        'Female', 'ar-TN', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Reem Online (Natural) - Arabic (Tunisia)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-AE-FatimaNeural', 'Microsoft Server Speech Text to Speech Voice (ar-AE, FatimaNeural)',
        'ar-AE-FatimaNeural', 'Female', 'ar-AE', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Fatima Online (Natural) - Arabic (United Arab Emirates)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-AE-HamdanNeural', 'Microsoft Server Speech Text to Speech Voice (ar-AE, HamdanNeural)',
        'ar-AE-HamdanNeural', 'Male', 'ar-AE', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Hamdan Online (Natural) - Arabic (United Arab Emirates)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-YE-MaryamNeural', 'Microsoft Server Speech Text to Speech Voice (ar-YE, MaryamNeural)',
        'ar-YE-MaryamNeural', 'Female', 'ar-YE', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Maryam Online (Natural) - Arabic (Yemen)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ar-YE-SalehNeural', 'Microsoft Server Speech Text to Speech Voice (ar-YE, SalehNeural)', 'ar-YE-SalehNeural',
        'Male', 'ar-YE', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Saleh Online (Natural) - Arabic (Yemen)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('az-AZ-BabekNeural', 'Microsoft Server Speech Text to Speech Voice (az-AZ, BabekNeural)', 'az-AZ-BabekNeural',
        'Male', 'az-AZ', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Babek Online (Natural) - Azerbaijani (Azerbaijan)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('az-AZ-BanuNeural', 'Microsoft Server Speech Text to Speech Voice (az-AZ, BanuNeural)', 'az-AZ-BanuNeural',
        'Female', 'az-AZ', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Banu Online (Natural) - Azerbaijani (Azerbaijan)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('bn-BD-NabanitaNeural', 'Microsoft Server Speech Text to Speech Voice (bn-BD, NabanitaNeural)',
        'bn-BD-NabanitaNeural', 'Female', 'bn-BD', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Nabanita Online (Natural) - Bangla (Bangladesh)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('bn-BD-PradeepNeural', 'Microsoft Server Speech Text to Speech Voice (bn-BD, PradeepNeural)',
        'bn-BD-PradeepNeural', 'Male', 'bn-BD', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Pradeep Online (Natural) - Bangla (Bangladesh)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('bn-IN-BashkarNeural', 'Microsoft Server Speech Text to Speech Voice (bn-IN, BashkarNeural)',
        'bn-IN-BashkarNeural', 'Male', 'bn-IN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Bashkar Online (Natural) - Bangla (India)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('bn-IN-TanishaaNeural', 'Microsoft Server Speech Text to Speech Voice (bn-IN, TanishaaNeural)',
        'bn-IN-TanishaaNeural', 'Female', 'bn-IN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Tanishaa Online (Natural) - Bengali (India)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('bs-BA-GoranNeural', 'Microsoft Server Speech Text to Speech Voice (bs-BA, GoranNeural)', 'bs-BA-GoranNeural',
        'Male', 'bs-BA', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Goran Online (Natural) - Bosnian (Bosnia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('bs-BA-VesnaNeural', 'Microsoft Server Speech Text to Speech Voice (bs-BA, VesnaNeural)', 'bs-BA-VesnaNeural',
        'Female', 'bs-BA', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Vesna Online (Natural) - Bosnian (Bosnia)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('bg-BG-BorislavNeural', 'Microsoft Server Speech Text to Speech Voice (bg-BG, BorislavNeural)',
        'bg-BG-BorislavNeural', 'Male', 'bg-BG', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Borislav Online (Natural) - Bulgarian (Bulgaria)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('bg-BG-KalinaNeural', 'Microsoft Server Speech Text to Speech Voice (bg-BG, KalinaNeural)',
        'bg-BG-KalinaNeural', 'Female', 'bg-BG', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Kalina Online (Natural) - Bulgarian (Bulgaria)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('my-MM-NilarNeural', 'Microsoft Server Speech Text to Speech Voice (my-MM, NilarNeural)', 'my-MM-NilarNeural',
        'Female', 'my-MM', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Nilar Online (Natural) - Burmese (Myanmar)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('my-MM-ThihaNeural', 'Microsoft Server Speech Text to Speech Voice (my-MM, ThihaNeural)', 'my-MM-ThihaNeural',
        'Male', 'my-MM', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Thiha Online (Natural) - Burmese (Myanmar)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ca-ES-EnricNeural', 'Microsoft Server Speech Text to Speech Voice (ca-ES, EnricNeural)', 'ca-ES-EnricNeural',
        'Male', 'ca-ES', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Enric Online (Natural) - Catalan (Spain)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ca-ES-JoanaNeural', 'Microsoft Server Speech Text to Speech Voice (ca-ES, JoanaNeural)', 'ca-ES-JoanaNeural',
        'Female', 'ca-ES', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Joana Online (Natural) - Catalan (Spain)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('zh-HK-HiuGaaiNeural', 'Microsoft Server Speech Text to Speech Voice (zh-HK, HiuGaaiNeural)',
        'zh-HK-HiuGaaiNeural', 'Female', 'zh-HK', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft HiuGaai Online (Natural) - Chinese (Cantonese Traditional)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('zh-HK-HiuMaanNeural', 'Microsoft Server Speech Text to Speech Voice (zh-HK, HiuMaanNeural)',
        'zh-HK-HiuMaanNeural', 'Female', 'zh-HK', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft HiuMaan Online (Natural) - Chinese (Hong Kong)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('zh-HK-WanLungNeural', 'Microsoft Server Speech Text to Speech Voice (zh-HK, WanLungNeural)',
        'zh-HK-WanLungNeural', 'Male', 'zh-HK', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft WanLung Online (Natural) - Chinese (Hong Kong)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('zh-CN-XiaoxiaoNeural', 'Microsoft Server Speech Text to Speech Voice (zh-CN, XiaoxiaoNeural)',
        'zh-CN-XiaoxiaoNeural', 'Female', 'zh-CN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Xiaoxiao Online (Natural) - Chinese (Mainland)', 'GA',
        '[{"contentCategories":["News","Novel"],"voicePersonalities":["Warm"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('zh-CN-XiaoyiNeural', 'Microsoft Server Speech Text to Speech Voice (zh-CN, XiaoyiNeural)',
        'zh-CN-XiaoyiNeural', 'Female', 'zh-CN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Xiaoyi Online (Natural) - Chinese (Mainland)', 'GA',
        '[{"contentCategories":["Cartoon","Novel"],"voicePersonalities":["Lively"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('zh-CN-YunjianNeural', 'Microsoft Server Speech Text to Speech Voice (zh-CN, YunjianNeural)',
        'zh-CN-YunjianNeural', 'Male', 'zh-CN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Yunjian Online (Natural) - Chinese (Mainland)', 'GA',
        '[{"contentCategories":["Sports"," Novel"],"voicePersonalities":["Passion"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('zh-CN-YunxiNeural', 'Microsoft Server Speech Text to Speech Voice (zh-CN, YunxiNeural)', 'zh-CN-YunxiNeural',
        'Male', 'zh-CN', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Yunxi Online (Natural) - Chinese (Mainland)',
        'GA', '[{"contentCategories":["Novel"],"voicePersonalities":["Lively","Sunshine"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('zh-CN-YunxiaNeural', 'Microsoft Server Speech Text to Speech Voice (zh-CN, YunxiaNeural)',
        'zh-CN-YunxiaNeural', 'Male', 'zh-CN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Yunxia Online (Natural) - Chinese (Mainland)', 'GA',
        '[{"contentCategories":["Cartoon","Novel"],"voicePersonalities":["Cute"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('zh-CN-YunyangNeural', 'Microsoft Server Speech Text to Speech Voice (zh-CN, YunyangNeural)',
        'zh-CN-YunyangNeural', 'Male', 'zh-CN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Yunyang Online (Natural) - Chinese (Mainland)', 'GA',
        '[{"contentCategories":["News"],"voicePersonalities":["Professional","Reliable"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('zh-CN-liaoning-XiaobeiNeural', 'Microsoft Server Speech Text to Speech Voice (zh-CN-liaoning, XiaobeiNeural)',
        'zh-CN-liaoning-XiaobeiNeural', 'Female', 'zh-CN-liaoning', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Xiaobei Online (Natural) - Chinese (Northeastern Mandarin)', 'GA',
        '[{"contentCategories":["Dialect"],"voicePersonalities":["Humorous"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('zh-TW-HsiaoChenNeural', 'Microsoft Server Speech Text to Speech Voice (zh-TW, HsiaoChenNeural)',
        'zh-TW-HsiaoChenNeural', 'Female', 'zh-TW', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft HsiaoChen Online (Natural) - Chinese (Taiwan)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('zh-TW-YunJheNeural', 'Microsoft Server Speech Text to Speech Voice (zh-TW, YunJheNeural)',
        'zh-TW-YunJheNeural', 'Male', 'zh-TW', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft YunJhe Online (Natural) - Chinese (Taiwan)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('zh-TW-HsiaoYuNeural', 'Microsoft Server Speech Text to Speech Voice (zh-TW, HsiaoYuNeural)',
        'zh-TW-HsiaoYuNeural', 'Female', 'zh-TW', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft HsiaoYu Online (Natural) - Chinese (Taiwanese Mandarin)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('zh-CN-shaanxi-XiaoniNeural', 'Microsoft Server Speech Text to Speech Voice (zh-CN-shaanxi, XiaoniNeural)',
        'zh-CN-shaanxi-XiaoniNeural', 'Female', 'zh-CN-shaanxi', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Xiaoni Online (Natural) - Chinese (Zhongyuan Mandarin Shaanxi)', 'GA',
        '[{"contentCategories":["Dialect"],"voicePersonalities":["Bright"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('hr-HR-GabrijelaNeural', 'Microsoft Server Speech Text to Speech Voice (hr-HR, GabrijelaNeural)',
        'hr-HR-GabrijelaNeural', 'Female', 'hr-HR', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Gabrijela Online (Natural) - Croatian (Croatia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('hr-HR-SreckoNeural', 'Microsoft Server Speech Text to Speech Voice (hr-HR, SreckoNeural)',
        'hr-HR-SreckoNeural', 'Male', 'hr-HR', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Srecko Online (Natural) - Croatian (Croatia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('cs-CZ-AntoninNeural', 'Microsoft Server Speech Text to Speech Voice (cs-CZ, AntoninNeural)',
        'cs-CZ-AntoninNeural', 'Male', 'cs-CZ', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Antonin Online (Natural) - Czech (Czech)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('cs-CZ-VlastaNeural', 'Microsoft Server Speech Text to Speech Voice (cs-CZ, VlastaNeural)',
        'cs-CZ-VlastaNeural', 'Female', 'cs-CZ', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Vlasta Online (Natural) - Czech (Czech)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('da-DK-ChristelNeural', 'Microsoft Server Speech Text to Speech Voice (da-DK, ChristelNeural)',
        'da-DK-ChristelNeural', 'Female', 'da-DK', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Christel Online (Natural) - Danish (Denmark)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('da-DK-JeppeNeural', 'Microsoft Server Speech Text to Speech Voice (da-DK, JeppeNeural)', 'da-DK-JeppeNeural',
        'Male', 'da-DK', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Jeppe Online (Natural) - Danish (Denmark)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('nl-BE-ArnaudNeural', 'Microsoft Server Speech Text to Speech Voice (nl-BE, ArnaudNeural)',
        'nl-BE-ArnaudNeural', 'Male', 'nl-BE', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Arnaud Online (Natural) - Dutch (Belgium)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('nl-BE-DenaNeural', 'Microsoft Server Speech Text to Speech Voice (nl-BE, DenaNeural)', 'nl-BE-DenaNeural',
        'Female', 'nl-BE', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Dena Online (Natural) - Dutch (Belgium)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('nl-NL-ColetteNeural', 'Microsoft Server Speech Text to Speech Voice (nl-NL, ColetteNeural)',
        'nl-NL-ColetteNeural', 'Female', 'nl-NL', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Colette Online (Natural) - Dutch (Netherlands)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('nl-NL-FennaNeural', 'Microsoft Server Speech Text to Speech Voice (nl-NL, FennaNeural)', 'nl-NL-FennaNeural',
        'Female', 'nl-NL', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Fenna Online (Natural) - Dutch (Netherlands)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('nl-NL-MaartenNeural', 'Microsoft Server Speech Text to Speech Voice (nl-NL, MaartenNeural)',
        'nl-NL-MaartenNeural', 'Male', 'nl-NL', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Maarten Online (Natural) - Dutch (Netherlands)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-AU-NatashaNeural', 'Microsoft Server Speech Text to Speech Voice (en-AU, NatashaNeural)',
        'en-AU-NatashaNeural', 'Female', 'en-AU', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Natasha Online (Natural) - English (Australia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-AU-WilliamNeural', 'Microsoft Server Speech Text to Speech Voice (en-AU, WilliamNeural)',
        'en-AU-WilliamNeural', 'Male', 'en-AU', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft William Online (Natural) - English (Australia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-CA-ClaraNeural', 'Microsoft Server Speech Text to Speech Voice (en-CA, ClaraNeural)', 'en-CA-ClaraNeural',
        'Female', 'en-CA', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Clara Online (Natural) - English (Canada)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-CA-LiamNeural', 'Microsoft Server Speech Text to Speech Voice (en-CA, LiamNeural)', 'en-CA-LiamNeural',
        'Male', 'en-CA', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Liam Online (Natural) - English (Canada)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-HK-SamNeural', 'Microsoft Server Speech Text to Speech Voice (en-HK, SamNeural)', 'en-HK-SamNeural', 'Male',
        'en-HK', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Sam Online (Natural) - English (Hongkong)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-HK-YanNeural', 'Microsoft Server Speech Text to Speech Voice (en-HK, YanNeural)', 'en-HK-YanNeural',
        'Female', 'en-HK', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Yan Online (Natural) - English (Hongkong)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-IN-NeerjaExpressiveNeural', 'Microsoft Server Speech Text to Speech Voice (en-IN, NeerjaExpressiveNeural)',
        'en-IN-NeerjaExpressiveNeural', 'Female', 'en-IN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Neerja Online (Natural) - English (India) (Preview)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-IN-NeerjaNeural', 'Microsoft Server Speech Text to Speech Voice (en-IN, NeerjaNeural)',
        'en-IN-NeerjaNeural', 'Female', 'en-IN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Neerja Online (Natural) - English (India)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-IN-PrabhatNeural', 'Microsoft Server Speech Text to Speech Voice (en-IN, PrabhatNeural)',
        'en-IN-PrabhatNeural', 'Male', 'en-IN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Prabhat Online (Natural) - English (India)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-IE-ConnorNeural', 'Microsoft Server Speech Text to Speech Voice (en-IE, ConnorNeural)',
        'en-IE-ConnorNeural', 'Male', 'en-IE', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Connor Online (Natural) - English (Ireland)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-IE-EmilyNeural', 'Microsoft Server Speech Text to Speech Voice (en-IE, EmilyNeural)', 'en-IE-EmilyNeural',
        'Female', 'en-IE', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Emily Online (Natural) - English (Ireland)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-KE-AsiliaNeural', 'Microsoft Server Speech Text to Speech Voice (en-KE, AsiliaNeural)',
        'en-KE-AsiliaNeural', 'Female', 'en-KE', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Asilia Online (Natural) - English (Kenya)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-KE-ChilembaNeural', 'Microsoft Server Speech Text to Speech Voice (en-KE, ChilembaNeural)',
        'en-KE-ChilembaNeural', 'Male', 'en-KE', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Chilemba Online (Natural) - English (Kenya)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-NZ-MitchellNeural', 'Microsoft Server Speech Text to Speech Voice (en-NZ, MitchellNeural)',
        'en-NZ-MitchellNeural', 'Male', 'en-NZ', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Mitchell Online (Natural) - English (New Zealand)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-NZ-MollyNeural', 'Microsoft Server Speech Text to Speech Voice (en-NZ, MollyNeural)', 'en-NZ-MollyNeural',
        'Female', 'en-NZ', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Molly Online (Natural) - English (New Zealand)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-NG-AbeoNeural', 'Microsoft Server Speech Text to Speech Voice (en-NG, AbeoNeural)', 'en-NG-AbeoNeural',
        'Male', 'en-NG', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Abeo Online (Natural) - English (Nigeria)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-NG-EzinneNeural', 'Microsoft Server Speech Text to Speech Voice (en-NG, EzinneNeural)',
        'en-NG-EzinneNeural', 'Female', 'en-NG', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Ezinne Online (Natural) - English (Nigeria)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-PH-JamesNeural', 'Microsoft Server Speech Text to Speech Voice (en-PH, JamesNeural)', 'en-PH-JamesNeural',
        'Male', 'en-PH', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft James Online (Natural) - English (Philippines)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-PH-RosaNeural', 'Microsoft Server Speech Text to Speech Voice (en-PH, RosaNeural)', 'en-PH-RosaNeural',
        'Female', 'en-PH', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Rosa Online (Natural) - English (Philippines)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-SG-LunaNeural', 'Microsoft Server Speech Text to Speech Voice (en-SG, LunaNeural)', 'en-SG-LunaNeural',
        'Female', 'en-SG', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Luna Online (Natural) - English (Singapore)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-SG-WayneNeural', 'Microsoft Server Speech Text to Speech Voice (en-SG, WayneNeural)', 'en-SG-WayneNeural',
        'Male', 'en-SG', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Wayne Online (Natural) - English (Singapore)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-ZA-LeahNeural', 'Microsoft Server Speech Text to Speech Voice (en-ZA, LeahNeural)', 'en-ZA-LeahNeural',
        'Female', 'en-ZA', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Leah Online (Natural) - English (South Africa)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-ZA-LukeNeural', 'Microsoft Server Speech Text to Speech Voice (en-ZA, LukeNeural)', 'en-ZA-LukeNeural',
        'Male', 'en-ZA', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Luke Online (Natural) - English (South Africa)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-TZ-ElimuNeural', 'Microsoft Server Speech Text to Speech Voice (en-TZ, ElimuNeural)', 'en-TZ-ElimuNeural',
        'Male', 'en-TZ', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Elimu Online (Natural) - English (Tanzania)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-TZ-ImaniNeural', 'Microsoft Server Speech Text to Speech Voice (en-TZ, ImaniNeural)', 'en-TZ-ImaniNeural',
        'Female', 'en-TZ', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Imani Online (Natural) - English (Tanzania)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-GB-LibbyNeural', 'Microsoft Server Speech Text to Speech Voice (en-GB, LibbyNeural)', 'en-GB-LibbyNeural',
        'Female', 'en-GB', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Libby Online (Natural) - English (United Kingdom)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-GB-MaisieNeural', 'Microsoft Server Speech Text to Speech Voice (en-GB, MaisieNeural)',
        'en-GB-MaisieNeural', 'Female', 'en-GB', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Maisie Online (Natural) - English (United Kingdom)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-GB-RyanNeural', 'Microsoft Server Speech Text to Speech Voice (en-GB, RyanNeural)', 'en-GB-RyanNeural',
        'Male', 'en-GB', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Ryan Online (Natural) - English (United Kingdom)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-GB-SoniaNeural', 'Microsoft Server Speech Text to Speech Voice (en-GB, SoniaNeural)', 'en-GB-SoniaNeural',
        'Female', 'en-GB', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Sonia Online (Natural) - English (United Kingdom)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-GB-ThomasNeural', 'Microsoft Server Speech Text to Speech Voice (en-GB, ThomasNeural)',
        'en-GB-ThomasNeural', 'Male', 'en-GB', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Thomas Online (Natural) - English (United Kingdom)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-US-AvaMultilingualNeural', 'Microsoft Server Speech Text to Speech Voice (en-US, AvaMultilingualNeural)',
        'en-US-AvaMultilingualNeural', 'Female', 'en-US', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft AvaMultilingual Online (Natural) - English (United States)', 'GA',
        '[{"contentCategories":["Conversation","Copilot"],"voicePersonalities":["Expressive","Caring","Pleasant","Friendly"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-US-AndrewMultilingualNeural',
        'Microsoft Server Speech Text to Speech Voice (en-US, AndrewMultilingualNeural)',
        'en-US-AndrewMultilingualNeural', 'Male', 'en-US', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft AndrewMultilingual Online (Natural) - English (United States)', 'GA',
        '[{"contentCategories":["Conversation","Copilot"],"voicePersonalities":["Warm","Confident","Authentic","Honest"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-US-EmmaMultilingualNeural', 'Microsoft Server Speech Text to Speech Voice (en-US, EmmaMultilingualNeural)',
        'en-US-EmmaMultilingualNeural', 'Female', 'en-US', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft EmmaMultilingual Online (Natural) - English (United States)', 'GA',
        '[{"contentCategories":["Conversation","Copilot"],"voicePersonalities":["Cheerful","Clear","Conversational"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-US-BrianMultilingualNeural',
        'Microsoft Server Speech Text to Speech Voice (en-US, BrianMultilingualNeural)',
        'en-US-BrianMultilingualNeural', 'Male', 'en-US', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft BrianMultilingual Online (Natural) - English (United States)', 'GA',
        '[{"contentCategories":["Conversation","Copilot"],"voicePersonalities":["Approachable","Casual","Sincere"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-US-AvaNeural', 'Microsoft Server Speech Text to Speech Voice (en-US, AvaNeural)', 'en-US-AvaNeural',
        'Female', 'en-US', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Ava Online (Natural) - English (United States)', 'GA',
        '[{"contentCategories":["Conversation","Copilot"],"voicePersonalities":["Expressive","Caring","Pleasant","Friendly"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-US-AndrewNeural', 'Microsoft Server Speech Text to Speech Voice (en-US, AndrewNeural)',
        'en-US-AndrewNeural', 'Male', 'en-US', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Andrew Online (Natural) - English (United States)', 'GA',
        '[{"contentCategories":["Conversation","Copilot"],"voicePersonalities":["Warm","Confident","Authentic","Honest"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-US-EmmaNeural', 'Microsoft Server Speech Text to Speech Voice (en-US, EmmaNeural)', 'en-US-EmmaNeural',
        'Female', 'en-US', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Emma Online (Natural) - English (United States)', 'GA',
        '[{"contentCategories":["Conversation","Copilot"],"voicePersonalities":["Cheerful","Clear","Conversational"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-US-BrianNeural', 'Microsoft Server Speech Text to Speech Voice (en-US, BrianNeural)', 'en-US-BrianNeural',
        'Male', 'en-US', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Brian Online (Natural) - English (United States)', 'GA',
        '[{"contentCategories":["Conversation","Copilot"],"voicePersonalities":["Approachable","Casual","Sincere"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-US-AnaNeural', 'Microsoft Server Speech Text to Speech Voice (en-US, AnaNeural)', 'en-US-AnaNeural',
        'Female', 'en-US', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Ana Online (Natural) - English (United States)', 'GA',
        '[{"contentCategories":["Cartoon","Conversation"],"voicePersonalities":["Cute"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-US-AriaNeural', 'Microsoft Server Speech Text to Speech Voice (en-US, AriaNeural)', 'en-US-AriaNeural',
        'Female', 'en-US', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Aria Online (Natural) - English (United States)', 'GA',
        '[{"contentCategories":["News","Novel"],"voicePersonalities":["Positive","Confident"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-US-ChristopherNeural', 'Microsoft Server Speech Text to Speech Voice (en-US, ChristopherNeural)',
        'en-US-ChristopherNeural', 'Male', 'en-US', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Christopher Online (Natural) - English (United States)', 'GA',
        '[{"contentCategories":["News","Novel"],"voicePersonalities":["Reliable","Authority"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-US-EricNeural', 'Microsoft Server Speech Text to Speech Voice (en-US, EricNeural)', 'en-US-EricNeural',
        'Male', 'en-US', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Eric Online (Natural) - English (United States)',
        'GA', '[{"contentCategories":["News","Novel"],"voicePersonalities":["Rational"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-US-GuyNeural', 'Microsoft Server Speech Text to Speech Voice (en-US, GuyNeural)', 'en-US-GuyNeural', 'Male',
        'en-US', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Guy Online (Natural) - English (United States)', 'GA',
        '[{"contentCategories":["News","Novel"],"voicePersonalities":["Passion"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-US-JennyNeural', 'Microsoft Server Speech Text to Speech Voice (en-US, JennyNeural)', 'en-US-JennyNeural',
        'Female', 'en-US', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Jenny Online (Natural) - English (United States)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Considerate","Comfort"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-US-MichelleNeural', 'Microsoft Server Speech Text to Speech Voice (en-US, MichelleNeural)',
        'en-US-MichelleNeural', 'Female', 'en-US', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Michelle Online (Natural) - English (United States)', 'GA',
        '[{"contentCategories":["News","Novel"],"voicePersonalities":["Friendly","Pleasant"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-US-RogerNeural', 'Microsoft Server Speech Text to Speech Voice (en-US, RogerNeural)', 'en-US-RogerNeural',
        'Male', 'en-US', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Roger Online (Natural) - English (United States)', 'GA',
        '[{"contentCategories":["News","Novel"],"voicePersonalities":["Lively"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('en-US-SteffanNeural', 'Microsoft Server Speech Text to Speech Voice (en-US, SteffanNeural)',
        'en-US-SteffanNeural', 'Male', 'en-US', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Steffan Online (Natural) - English (United States)', 'GA',
        '[{"contentCategories":["News","Novel"],"voicePersonalities":["Rational"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('et-EE-AnuNeural', 'Microsoft Server Speech Text to Speech Voice (et-EE, AnuNeural)', 'et-EE-AnuNeural',
        'Female', 'et-EE', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Anu Online (Natural) - Estonian (Estonia)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('et-EE-KertNeural', 'Microsoft Server Speech Text to Speech Voice (et-EE, KertNeural)', 'et-EE-KertNeural',
        'Male', 'et-EE', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Kert Online (Natural) - Estonian (Estonia)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('fil-PH-AngeloNeural', 'Microsoft Server Speech Text to Speech Voice (fil-PH, AngeloNeural)',
        'fil-PH-AngeloNeural', 'Male', 'fil-PH', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Angelo Online (Natural) - Filipino (Philippines)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('fil-PH-BlessicaNeural', 'Microsoft Server Speech Text to Speech Voice (fil-PH, BlessicaNeural)',
        'fil-PH-BlessicaNeural', 'Female', 'fil-PH', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Blessica Online (Natural) - Filipino (Philippines)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('fi-FI-HarriNeural', 'Microsoft Server Speech Text to Speech Voice (fi-FI, HarriNeural)', 'fi-FI-HarriNeural',
        'Male', 'fi-FI', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Harri Online (Natural) - Finnish (Finland)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('fi-FI-NooraNeural', 'Microsoft Server Speech Text to Speech Voice (fi-FI, NooraNeural)', 'fi-FI-NooraNeural',
        'Female', 'fi-FI', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Noora Online (Natural) - Finnish (Finland)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('fr-BE-CharlineNeural', 'Microsoft Server Speech Text to Speech Voice (fr-BE, CharlineNeural)',
        'fr-BE-CharlineNeural', 'Female', 'fr-BE', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Charline Online (Natural) - French (Belgium)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('fr-BE-GerardNeural', 'Microsoft Server Speech Text to Speech Voice (fr-BE, GerardNeural)',
        'fr-BE-GerardNeural', 'Male', 'fr-BE', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Gerard Online (Natural) - French (Belgium)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('fr-CA-ThierryNeural', 'Microsoft Server Speech Text to Speech Voice (fr-CA, ThierryNeural)',
        'fr-CA-ThierryNeural', 'Male', 'fr-CA', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Thierry Online (Natural) - French (Canada)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('fr-CA-AntoineNeural', 'Microsoft Server Speech Text to Speech Voice (fr-CA, AntoineNeural)',
        'fr-CA-AntoineNeural', 'Male', 'fr-CA', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Antoine Online (Natural) - French (Canada)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('fr-CA-JeanNeural', 'Microsoft Server Speech Text to Speech Voice (fr-CA, JeanNeural)', 'fr-CA-JeanNeural',
        'Male', 'fr-CA', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Jean Online (Natural) - French (Canada)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('fr-CA-SylvieNeural', 'Microsoft Server Speech Text to Speech Voice (fr-CA, SylvieNeural)',
        'fr-CA-SylvieNeural', 'Female', 'fr-CA', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Sylvie Online (Natural) - French (Canada)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('fr-FR-VivienneMultilingualNeural',
        'Microsoft Server Speech Text to Speech Voice (fr-FR, VivienneMultilingualNeural)',
        'fr-FR-VivienneMultilingualNeural', 'Female', 'fr-FR', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft VivienneMultilingual Online (Natural) - French (France)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('fr-FR-RemyMultilingualNeural', 'Microsoft Server Speech Text to Speech Voice (fr-FR, RemyMultilingualNeural)',
        'fr-FR-RemyMultilingualNeural', 'Male', 'fr-FR', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft RemyMultilingual Online (Natural) - French (France)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('fr-FR-DeniseNeural', 'Microsoft Server Speech Text to Speech Voice (fr-FR, DeniseNeural)',
        'fr-FR-DeniseNeural', 'Female', 'fr-FR', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Denise Online (Natural) - French (France)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('fr-FR-EloiseNeural', 'Microsoft Server Speech Text to Speech Voice (fr-FR, EloiseNeural)',
        'fr-FR-EloiseNeural', 'Female', 'fr-FR', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Eloise Online (Natural) - French (France)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('fr-FR-HenriNeural', 'Microsoft Server Speech Text to Speech Voice (fr-FR, HenriNeural)', 'fr-FR-HenriNeural',
        'Male', 'fr-FR', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Henri Online (Natural) - French (France)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('fr-CH-ArianeNeural', 'Microsoft Server Speech Text to Speech Voice (fr-CH, ArianeNeural)',
        'fr-CH-ArianeNeural', 'Female', 'fr-CH', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Ariane Online (Natural) - French (Switzerland)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('fr-CH-FabriceNeural', 'Microsoft Server Speech Text to Speech Voice (fr-CH, FabriceNeural)',
        'fr-CH-FabriceNeural', 'Male', 'fr-CH', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Fabrice Online (Natural) - French (Switzerland)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('gl-ES-RoiNeural', 'Microsoft Server Speech Text to Speech Voice (gl-ES, RoiNeural)', 'gl-ES-RoiNeural', 'Male',
        'gl-ES', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Roi Online (Natural) - Galician (Spain)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('gl-ES-SabelaNeural', 'Microsoft Server Speech Text to Speech Voice (gl-ES, SabelaNeural)',
        'gl-ES-SabelaNeural', 'Female', 'gl-ES', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Sabela Online (Natural) - Galician (Spain)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ka-GE-EkaNeural', 'Microsoft Server Speech Text to Speech Voice (ka-GE, EkaNeural)', 'ka-GE-EkaNeural',
        'Female', 'ka-GE', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Eka Online (Natural) - Georgian (Georgia)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ka-GE-GiorgiNeural', 'Microsoft Server Speech Text to Speech Voice (ka-GE, GiorgiNeural)',
        'ka-GE-GiorgiNeural', 'Male', 'ka-GE', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Giorgi Online (Natural) - Georgian (Georgia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('de-AT-IngridNeural', 'Microsoft Server Speech Text to Speech Voice (de-AT, IngridNeural)',
        'de-AT-IngridNeural', 'Female', 'de-AT', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Ingrid Online (Natural) - German (Austria)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('de-AT-JonasNeural', 'Microsoft Server Speech Text to Speech Voice (de-AT, JonasNeural)', 'de-AT-JonasNeural',
        'Male', 'de-AT', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Jonas Online (Natural) - German (Austria)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('de-DE-SeraphinaMultilingualNeural',
        'Microsoft Server Speech Text to Speech Voice (de-DE, SeraphinaMultilingualNeural)',
        'de-DE-SeraphinaMultilingualNeural', 'Female', 'de-DE', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft SeraphinaMultilingual Online (Natural) - German (Germany)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('de-DE-FlorianMultilingualNeural',
        'Microsoft Server Speech Text to Speech Voice (de-DE, FlorianMultilingualNeural)',
        'de-DE-FlorianMultilingualNeural', 'Male', 'de-DE', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft FlorianMultilingual Online (Natural) - German (Germany)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('de-DE-AmalaNeural', 'Microsoft Server Speech Text to Speech Voice (de-DE, AmalaNeural)', 'de-DE-AmalaNeural',
        'Female', 'de-DE', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Amala Online (Natural) - German (Germany)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('de-DE-ConradNeural', 'Microsoft Server Speech Text to Speech Voice (de-DE, ConradNeural)',
        'de-DE-ConradNeural', 'Male', 'de-DE', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Conrad Online (Natural) - German (Germany)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('de-DE-KatjaNeural', 'Microsoft Server Speech Text to Speech Voice (de-DE, KatjaNeural)', 'de-DE-KatjaNeural',
        'Female', 'de-DE', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Katja Online (Natural) - German (Germany)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('de-DE-KillianNeural', 'Microsoft Server Speech Text to Speech Voice (de-DE, KillianNeural)',
        'de-DE-KillianNeural', 'Male', 'de-DE', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Killian Online (Natural) - German (Germany)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('de-CH-JanNeural', 'Microsoft Server Speech Text to Speech Voice (de-CH, JanNeural)', 'de-CH-JanNeural', 'Male',
        'de-CH', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Jan Online (Natural) - German (Switzerland)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('de-CH-LeniNeural', 'Microsoft Server Speech Text to Speech Voice (de-CH, LeniNeural)', 'de-CH-LeniNeural',
        'Female', 'de-CH', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Leni Online (Natural) - German (Switzerland)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('el-GR-AthinaNeural', 'Microsoft Server Speech Text to Speech Voice (el-GR, AthinaNeural)',
        'el-GR-AthinaNeural', 'Female', 'el-GR', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Athina Online (Natural) - Greek (Greece)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('el-GR-NestorasNeural', 'Microsoft Server Speech Text to Speech Voice (el-GR, NestorasNeural)',
        'el-GR-NestorasNeural', 'Male', 'el-GR', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Nestoras Online (Natural) - Greek (Greece)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('gu-IN-DhwaniNeural', 'Microsoft Server Speech Text to Speech Voice (gu-IN, DhwaniNeural)',
        'gu-IN-DhwaniNeural', 'Female', 'gu-IN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Dhwani Online (Natural) - Gujarati (India)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('gu-IN-NiranjanNeural', 'Microsoft Server Speech Text to Speech Voice (gu-IN, NiranjanNeural)',
        'gu-IN-NiranjanNeural', 'Male', 'gu-IN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Niranjan Online (Natural) - Gujarati (India)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('he-IL-AvriNeural', 'Microsoft Server Speech Text to Speech Voice (he-IL, AvriNeural)', 'he-IL-AvriNeural',
        'Male', 'he-IL', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Avri Online (Natural) - Hebrew (Israel)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('he-IL-HilaNeural', 'Microsoft Server Speech Text to Speech Voice (he-IL, HilaNeural)', 'he-IL-HilaNeural',
        'Female', 'he-IL', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Hila Online (Natural) - Hebrew (Israel)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('hi-IN-MadhurNeural', 'Microsoft Server Speech Text to Speech Voice (hi-IN, MadhurNeural)',
        'hi-IN-MadhurNeural', 'Male', 'hi-IN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Madhur Online (Natural) - Hindi (India)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('hi-IN-SwaraNeural', 'Microsoft Server Speech Text to Speech Voice (hi-IN, SwaraNeural)', 'hi-IN-SwaraNeural',
        'Female', 'hi-IN', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Swara Online (Natural) - Hindi (India)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('hu-HU-NoemiNeural', 'Microsoft Server Speech Text to Speech Voice (hu-HU, NoemiNeural)', 'hu-HU-NoemiNeural',
        'Female', 'hu-HU', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Noemi Online (Natural) - Hungarian (Hungary)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('hu-HU-TamasNeural', 'Microsoft Server Speech Text to Speech Voice (hu-HU, TamasNeural)', 'hu-HU-TamasNeural',
        'Male', 'hu-HU', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Tamas Online (Natural) - Hungarian (Hungary)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('is-IS-GudrunNeural', 'Microsoft Server Speech Text to Speech Voice (is-IS, GudrunNeural)',
        'is-IS-GudrunNeural', 'Female', 'is-IS', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Gudrun Online (Natural) - Icelandic (Iceland)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('is-IS-GunnarNeural', 'Microsoft Server Speech Text to Speech Voice (is-IS, GunnarNeural)',
        'is-IS-GunnarNeural', 'Male', 'is-IS', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Gunnar Online (Natural) - Icelandic (Iceland)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('id-ID-ArdiNeural', 'Microsoft Server Speech Text to Speech Voice (id-ID, ArdiNeural)', 'id-ID-ArdiNeural',
        'Male', 'id-ID', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Ardi Online (Natural) - Indonesian (Indonesia)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('id-ID-GadisNeural', 'Microsoft Server Speech Text to Speech Voice (id-ID, GadisNeural)', 'id-ID-GadisNeural',
        'Female', 'id-ID', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Gadis Online (Natural) - Indonesian (Indonesia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ga-IE-ColmNeural', 'Microsoft Server Speech Text to Speech Voice (ga-IE, ColmNeural)', 'ga-IE-ColmNeural',
        'Male', 'ga-IE', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Colm Online (Natural) - Irish (Ireland)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ga-IE-OrlaNeural', 'Microsoft Server Speech Text to Speech Voice (ga-IE, OrlaNeural)', 'ga-IE-OrlaNeural',
        'Female', 'ga-IE', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Orla Online (Natural) - Irish (Ireland)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('it-IT-GiuseppeNeural', 'Microsoft Server Speech Text to Speech Voice (it-IT, GiuseppeNeural)',
        'it-IT-GiuseppeNeural', 'Male', 'it-IT', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Giuseppe Online (Natural) - Italian (Italy)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('it-IT-DiegoNeural', 'Microsoft Server Speech Text to Speech Voice (it-IT, DiegoNeural)', 'it-IT-DiegoNeural',
        'Male', 'it-IT', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Diego Online (Natural) - Italian (Italy)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('it-IT-ElsaNeural', 'Microsoft Server Speech Text to Speech Voice (it-IT, ElsaNeural)', 'it-IT-ElsaNeural',
        'Female', 'it-IT', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Elsa Online (Natural) - Italian (Italy)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('it-IT-IsabellaNeural', 'Microsoft Server Speech Text to Speech Voice (it-IT, IsabellaNeural)',
        'it-IT-IsabellaNeural', 'Female', 'it-IT', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Isabella Online (Natural) - Italian (Italy)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ja-JP-KeitaNeural', 'Microsoft Server Speech Text to Speech Voice (ja-JP, KeitaNeural)', 'ja-JP-KeitaNeural',
        'Male', 'ja-JP', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Keita Online (Natural) - Japanese (Japan)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ja-JP-NanamiNeural', 'Microsoft Server Speech Text to Speech Voice (ja-JP, NanamiNeural)',
        'ja-JP-NanamiNeural', 'Female', 'ja-JP', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Nanami Online (Natural) - Japanese (Japan)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('jv-ID-DimasNeural', 'Microsoft Server Speech Text to Speech Voice (jv-ID, DimasNeural)', 'jv-ID-DimasNeural',
        'Male', 'jv-ID', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Dimas Online (Natural) - Javanese (Indonesia)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('jv-ID-SitiNeural', 'Microsoft Server Speech Text to Speech Voice (jv-ID, SitiNeural)', 'jv-ID-SitiNeural',
        'Female', 'jv-ID', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Siti Online (Natural) - Javanese (Indonesia)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('kn-IN-GaganNeural', 'Microsoft Server Speech Text to Speech Voice (kn-IN, GaganNeural)', 'kn-IN-GaganNeural',
        'Male', 'kn-IN', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Gagan Online (Natural) - Kannada (India)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('kn-IN-SapnaNeural', 'Microsoft Server Speech Text to Speech Voice (kn-IN, SapnaNeural)', 'kn-IN-SapnaNeural',
        'Female', 'kn-IN', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Sapna Online (Natural) - Kannada (India)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('kk-KZ-AigulNeural', 'Microsoft Server Speech Text to Speech Voice (kk-KZ, AigulNeural)', 'kk-KZ-AigulNeural',
        'Female', 'kk-KZ', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Aigul Online (Natural) - Kazakh (Kazakhstan)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('kk-KZ-DauletNeural', 'Microsoft Server Speech Text to Speech Voice (kk-KZ, DauletNeural)',
        'kk-KZ-DauletNeural', 'Male', 'kk-KZ', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Daulet Online (Natural) - Kazakh (Kazakhstan)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('km-KH-PisethNeural', 'Microsoft Server Speech Text to Speech Voice (km-KH, PisethNeural)',
        'km-KH-PisethNeural', 'Male', 'km-KH', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Piseth Online (Natural) - Khmer (Cambodia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('km-KH-SreymomNeural', 'Microsoft Server Speech Text to Speech Voice (km-KH, SreymomNeural)',
        'km-KH-SreymomNeural', 'Female', 'km-KH', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Sreymom Online (Natural) - Khmer (Cambodia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ko-KR-HyunsuNeural', 'Microsoft Server Speech Text to Speech Voice (ko-KR, HyunsuNeural)',
        'ko-KR-HyunsuNeural', 'Male', 'ko-KR', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Hyunsu Online (Natural) - Korean (Korea)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ko-KR-InJoonNeural', 'Microsoft Server Speech Text to Speech Voice (ko-KR, InJoonNeural)',
        'ko-KR-InJoonNeural', 'Male', 'ko-KR', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft InJoon Online (Natural) - Korean (Korea)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ko-KR-SunHiNeural', 'Microsoft Server Speech Text to Speech Voice (ko-KR, SunHiNeural)', 'ko-KR-SunHiNeural',
        'Female', 'ko-KR', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft SunHi Online (Natural) - Korean (Korea)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('lo-LA-ChanthavongNeural', 'Microsoft Server Speech Text to Speech Voice (lo-LA, ChanthavongNeural)',
        'lo-LA-ChanthavongNeural', 'Male', 'lo-LA', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Chanthavong Online (Natural) - Lao (Laos)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('lo-LA-KeomanyNeural', 'Microsoft Server Speech Text to Speech Voice (lo-LA, KeomanyNeural)',
        'lo-LA-KeomanyNeural', 'Female', 'lo-LA', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Keomany Online (Natural) - Lao (Laos)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('lv-LV-EveritaNeural', 'Microsoft Server Speech Text to Speech Voice (lv-LV, EveritaNeural)',
        'lv-LV-EveritaNeural', 'Female', 'lv-LV', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Everita Online (Natural) - Latvian (Latvia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('lv-LV-NilsNeural', 'Microsoft Server Speech Text to Speech Voice (lv-LV, NilsNeural)', 'lv-LV-NilsNeural',
        'Male', 'lv-LV', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Nils Online (Natural) - Latvian (Latvia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('lt-LT-LeonasNeural', 'Microsoft Server Speech Text to Speech Voice (lt-LT, LeonasNeural)',
        'lt-LT-LeonasNeural', 'Male', 'lt-LT', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Leonas Online (Natural) - Lithuanian (Lithuania)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('lt-LT-OnaNeural', 'Microsoft Server Speech Text to Speech Voice (lt-LT, OnaNeural)', 'lt-LT-OnaNeural',
        'Female', 'lt-LT', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Ona Online (Natural) - Lithuanian (Lithuania)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('mk-MK-AleksandarNeural', 'Microsoft Server Speech Text to Speech Voice (mk-MK, AleksandarNeural)',
        'mk-MK-AleksandarNeural', 'Male', 'mk-MK', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Aleksandar Online (Natural) - Macedonian (Republic of North Macedonia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('mk-MK-MarijaNeural', 'Microsoft Server Speech Text to Speech Voice (mk-MK, MarijaNeural)',
        'mk-MK-MarijaNeural', 'Female', 'mk-MK', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Marija Online (Natural) - Macedonian (Republic of North Macedonia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ms-MY-OsmanNeural', 'Microsoft Server Speech Text to Speech Voice (ms-MY, OsmanNeural)', 'ms-MY-OsmanNeural',
        'Male', 'ms-MY', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Osman Online (Natural) - Malay (Malaysia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ms-MY-YasminNeural', 'Microsoft Server Speech Text to Speech Voice (ms-MY, YasminNeural)',
        'ms-MY-YasminNeural', 'Female', 'ms-MY', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Yasmin Online (Natural) - Malay (Malaysia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ml-IN-MidhunNeural', 'Microsoft Server Speech Text to Speech Voice (ml-IN, MidhunNeural)',
        'ml-IN-MidhunNeural', 'Male', 'ml-IN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Midhun Online (Natural) - Malayalam (India)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ml-IN-SobhanaNeural', 'Microsoft Server Speech Text to Speech Voice (ml-IN, SobhanaNeural)',
        'ml-IN-SobhanaNeural', 'Female', 'ml-IN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Sobhana Online (Natural) - Malayalam (India)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('mt-MT-GraceNeural', 'Microsoft Server Speech Text to Speech Voice (mt-MT, GraceNeural)', 'mt-MT-GraceNeural',
        'Female', 'mt-MT', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Grace Online (Natural) - Maltese (Malta)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('mt-MT-JosephNeural', 'Microsoft Server Speech Text to Speech Voice (mt-MT, JosephNeural)',
        'mt-MT-JosephNeural', 'Male', 'mt-MT', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Joseph Online (Natural) - Maltese (Malta)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('mr-IN-AarohiNeural', 'Microsoft Server Speech Text to Speech Voice (mr-IN, AarohiNeural)',
        'mr-IN-AarohiNeural', 'Female', 'mr-IN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Aarohi Online (Natural) - Marathi (India)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('mr-IN-ManoharNeural', 'Microsoft Server Speech Text to Speech Voice (mr-IN, ManoharNeural)',
        'mr-IN-ManoharNeural', 'Male', 'mr-IN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Manohar Online (Natural) - Marathi (India)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('mn-MN-BataaNeural', 'Microsoft Server Speech Text to Speech Voice (mn-MN, BataaNeural)', 'mn-MN-BataaNeural',
        'Male', 'mn-MN', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Bataa Online (Natural) - Mongolian (Mongolia)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('mn-MN-YesuiNeural', 'Microsoft Server Speech Text to Speech Voice (mn-MN, YesuiNeural)', 'mn-MN-YesuiNeural',
        'Female', 'mn-MN', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Yesui Online (Natural) - Mongolian (Mongolia)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ne-NP-HemkalaNeural', 'Microsoft Server Speech Text to Speech Voice (ne-NP, HemkalaNeural)',
        'ne-NP-HemkalaNeural', 'Female', 'ne-NP', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Hemkala Online (Natural) - Nepali (Nepal)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ne-NP-SagarNeural', 'Microsoft Server Speech Text to Speech Voice (ne-NP, SagarNeural)', 'ne-NP-SagarNeural',
        'Male', 'ne-NP', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Sagar Online (Natural) - Nepali (Nepal)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('nb-NO-FinnNeural', 'Microsoft Server Speech Text to Speech Voice (nb-NO, FinnNeural)', 'nb-NO-FinnNeural',
        'Male', 'nb-NO', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Finn Online (Natural) - Norwegian (Bokmål Norway)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('nb-NO-PernilleNeural', 'Microsoft Server Speech Text to Speech Voice (nb-NO, PernilleNeural)',
        'nb-NO-PernilleNeural', 'Female', 'nb-NO', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Pernille Online (Natural) - Norwegian (Bokmål, Norway)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ps-AF-GulNawazNeural', 'Microsoft Server Speech Text to Speech Voice (ps-AF, GulNawazNeural)',
        'ps-AF-GulNawazNeural', 'Male', 'ps-AF', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft GulNawaz Online (Natural) - Pashto (Afghanistan)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ps-AF-LatifaNeural', 'Microsoft Server Speech Text to Speech Voice (ps-AF, LatifaNeural)',
        'ps-AF-LatifaNeural', 'Female', 'ps-AF', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Latifa Online (Natural) - Pashto (Afghanistan)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('fa-IR-DilaraNeural', 'Microsoft Server Speech Text to Speech Voice (fa-IR, DilaraNeural)',
        'fa-IR-DilaraNeural', 'Female', 'fa-IR', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Dilara Online (Natural) - Persian (Iran)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('fa-IR-FaridNeural', 'Microsoft Server Speech Text to Speech Voice (fa-IR, FaridNeural)', 'fa-IR-FaridNeural',
        'Male', 'fa-IR', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Farid Online (Natural) - Persian (Iran)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('pl-PL-MarekNeural', 'Microsoft Server Speech Text to Speech Voice (pl-PL, MarekNeural)', 'pl-PL-MarekNeural',
        'Male', 'pl-PL', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Marek Online (Natural) - Polish (Poland)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('pl-PL-ZofiaNeural', 'Microsoft Server Speech Text to Speech Voice (pl-PL, ZofiaNeural)', 'pl-PL-ZofiaNeural',
        'Female', 'pl-PL', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Zofia Online (Natural) - Polish (Poland)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('pt-BR-ThalitaNeural', 'Microsoft Server Speech Text to Speech Voice (pt-BR, ThalitaNeural)',
        'pt-BR-ThalitaNeural', 'Female', 'pt-BR', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Thalita Online (Natural) - Portuguese (Brazil)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('pt-BR-AntonioNeural', 'Microsoft Server Speech Text to Speech Voice (pt-BR, AntonioNeural)',
        'pt-BR-AntonioNeural', 'Male', 'pt-BR', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Antonio Online (Natural) - Portuguese (Brazil)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('pt-BR-FranciscaNeural', 'Microsoft Server Speech Text to Speech Voice (pt-BR, FranciscaNeural)',
        'pt-BR-FranciscaNeural', 'Female', 'pt-BR', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Francisca Online (Natural) - Portuguese (Brazil)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('pt-PT-DuarteNeural', 'Microsoft Server Speech Text to Speech Voice (pt-PT, DuarteNeural)',
        'pt-PT-DuarteNeural', 'Male', 'pt-PT', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Duarte Online (Natural) - Portuguese (Portugal)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('pt-PT-RaquelNeural', 'Microsoft Server Speech Text to Speech Voice (pt-PT, RaquelNeural)',
        'pt-PT-RaquelNeural', 'Female', 'pt-PT', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Raquel Online (Natural) - Portuguese (Portugal)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ro-RO-AlinaNeural', 'Microsoft Server Speech Text to Speech Voice (ro-RO, AlinaNeural)', 'ro-RO-AlinaNeural',
        'Female', 'ro-RO', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Alina Online (Natural) - Romanian (Romania)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ro-RO-EmilNeural', 'Microsoft Server Speech Text to Speech Voice (ro-RO, EmilNeural)', 'ro-RO-EmilNeural',
        'Male', 'ro-RO', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Emil Online (Natural) - Romanian (Romania)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ru-RU-DmitryNeural', 'Microsoft Server Speech Text to Speech Voice (ru-RU, DmitryNeural)',
        'ru-RU-DmitryNeural', 'Male', 'ru-RU', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Dmitry Online (Natural) - Russian (Russia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ru-RU-SvetlanaNeural', 'Microsoft Server Speech Text to Speech Voice (ru-RU, SvetlanaNeural)',
        'ru-RU-SvetlanaNeural', 'Female', 'ru-RU', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Svetlana Online (Natural) - Russian (Russia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('sr-RS-NicholasNeural', 'Microsoft Server Speech Text to Speech Voice (sr-RS, NicholasNeural)',
        'sr-RS-NicholasNeural', 'Male', 'sr-RS', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Nicholas Online (Natural) - Serbian (Serbia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('sr-RS-SophieNeural', 'Microsoft Server Speech Text to Speech Voice (sr-RS, SophieNeural)',
        'sr-RS-SophieNeural', 'Female', 'sr-RS', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Sophie Online (Natural) - Serbian (Serbia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('si-LK-SameeraNeural', 'Microsoft Server Speech Text to Speech Voice (si-LK, SameeraNeural)',
        'si-LK-SameeraNeural', 'Male', 'si-LK', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Sameera Online (Natural) - Sinhala (Sri Lanka)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('si-LK-ThiliniNeural', 'Microsoft Server Speech Text to Speech Voice (si-LK, ThiliniNeural)',
        'si-LK-ThiliniNeural', 'Female', 'si-LK', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Thilini Online (Natural) - Sinhala (Sri Lanka)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('sk-SK-LukasNeural', 'Microsoft Server Speech Text to Speech Voice (sk-SK, LukasNeural)', 'sk-SK-LukasNeural',
        'Male', 'sk-SK', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Lukas Online (Natural) - Slovak (Slovakia)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('sk-SK-ViktoriaNeural', 'Microsoft Server Speech Text to Speech Voice (sk-SK, ViktoriaNeural)',
        'sk-SK-ViktoriaNeural', 'Female', 'sk-SK', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Viktoria Online (Natural) - Slovak (Slovakia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('sl-SI-PetraNeural', 'Microsoft Server Speech Text to Speech Voice (sl-SI, PetraNeural)', 'sl-SI-PetraNeural',
        'Female', 'sl-SI', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Petra Online (Natural) - Slovenian (Slovenia)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('sl-SI-RokNeural', 'Microsoft Server Speech Text to Speech Voice (sl-SI, RokNeural)', 'sl-SI-RokNeural', 'Male',
        'sl-SI', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Rok Online (Natural) - Slovenian (Slovenia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('so-SO-MuuseNeural', 'Microsoft Server Speech Text to Speech Voice (so-SO, MuuseNeural)', 'so-SO-MuuseNeural',
        'Male', 'so-SO', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Muuse Online (Natural) - Somali (Somalia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('so-SO-UbaxNeural', 'Microsoft Server Speech Text to Speech Voice (so-SO, UbaxNeural)', 'so-SO-UbaxNeural',
        'Female', 'so-SO', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Ubax Online (Natural) - Somali (Somalia)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-AR-ElenaNeural', 'Microsoft Server Speech Text to Speech Voice (es-AR, ElenaNeural)', 'es-AR-ElenaNeural',
        'Female', 'es-AR', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Elena Online (Natural) - Spanish (Argentina)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-AR-TomasNeural', 'Microsoft Server Speech Text to Speech Voice (es-AR, TomasNeural)', 'es-AR-TomasNeural',
        'Male', 'es-AR', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Tomas Online (Natural) - Spanish (Argentina)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-BO-MarceloNeural', 'Microsoft Server Speech Text to Speech Voice (es-BO, MarceloNeural)',
        'es-BO-MarceloNeural', 'Male', 'es-BO', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Marcelo Online (Natural) - Spanish (Bolivia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-BO-SofiaNeural', 'Microsoft Server Speech Text to Speech Voice (es-BO, SofiaNeural)', 'es-BO-SofiaNeural',
        'Female', 'es-BO', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Sofia Online (Natural) - Spanish (Bolivia)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-CL-CatalinaNeural', 'Microsoft Server Speech Text to Speech Voice (es-CL, CatalinaNeural)',
        'es-CL-CatalinaNeural', 'Female', 'es-CL', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Catalina Online (Natural) - Spanish (Chile)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-CL-LorenzoNeural', 'Microsoft Server Speech Text to Speech Voice (es-CL, LorenzoNeural)',
        'es-CL-LorenzoNeural', 'Male', 'es-CL', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Lorenzo Online (Natural) - Spanish (Chile)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-ES-XimenaNeural', 'Microsoft Server Speech Text to Speech Voice (es-ES, XimenaNeural)',
        'es-ES-XimenaNeural', 'Female', 'es-ES', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Ximena Online (Natural) - Spanish (Colombia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-CO-GonzaloNeural', 'Microsoft Server Speech Text to Speech Voice (es-CO, GonzaloNeural)',
        'es-CO-GonzaloNeural', 'Male', 'es-CO', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Gonzalo Online (Natural) - Spanish (Colombia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-CO-SalomeNeural', 'Microsoft Server Speech Text to Speech Voice (es-CO, SalomeNeural)',
        'es-CO-SalomeNeural', 'Female', 'es-CO', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Salome Online (Natural) - Spanish (Colombia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-CR-JuanNeural', 'Microsoft Server Speech Text to Speech Voice (es-CR, JuanNeural)', 'es-CR-JuanNeural',
        'Male', 'es-CR', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Juan Online (Natural) - Spanish (Costa Rica)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-CR-MariaNeural', 'Microsoft Server Speech Text to Speech Voice (es-CR, MariaNeural)', 'es-CR-MariaNeural',
        'Female', 'es-CR', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Maria Online (Natural) - Spanish (Costa Rica)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-CU-BelkysNeural', 'Microsoft Server Speech Text to Speech Voice (es-CU, BelkysNeural)',
        'es-CU-BelkysNeural', 'Female', 'es-CU', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Belkys Online (Natural) - Spanish (Cuba)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-CU-ManuelNeural', 'Microsoft Server Speech Text to Speech Voice (es-CU, ManuelNeural)',
        'es-CU-ManuelNeural', 'Male', 'es-CU', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Manuel Online (Natural) - Spanish (Cuba)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-DO-EmilioNeural', 'Microsoft Server Speech Text to Speech Voice (es-DO, EmilioNeural)',
        'es-DO-EmilioNeural', 'Male', 'es-DO', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Emilio Online (Natural) - Spanish (Dominican Republic)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-DO-RamonaNeural', 'Microsoft Server Speech Text to Speech Voice (es-DO, RamonaNeural)',
        'es-DO-RamonaNeural', 'Female', 'es-DO', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Ramona Online (Natural) - Spanish (Dominican Republic)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-EC-AndreaNeural', 'Microsoft Server Speech Text to Speech Voice (es-EC, AndreaNeural)',
        'es-EC-AndreaNeural', 'Female', 'es-EC', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Andrea Online (Natural) - Spanish (Ecuador)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-EC-LuisNeural', 'Microsoft Server Speech Text to Speech Voice (es-EC, LuisNeural)', 'es-EC-LuisNeural',
        'Male', 'es-EC', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Luis Online (Natural) - Spanish (Ecuador)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-SV-LorenaNeural', 'Microsoft Server Speech Text to Speech Voice (es-SV, LorenaNeural)',
        'es-SV-LorenaNeural', 'Female', 'es-SV', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Lorena Online (Natural) - Spanish (El Salvador)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-SV-RodrigoNeural', 'Microsoft Server Speech Text to Speech Voice (es-SV, RodrigoNeural)',
        'es-SV-RodrigoNeural', 'Male', 'es-SV', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Rodrigo Online (Natural) - Spanish (El Salvador)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-GQ-JavierNeural', 'Microsoft Server Speech Text to Speech Voice (es-GQ, JavierNeural)',
        'es-GQ-JavierNeural', 'Male', 'es-GQ', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Javier Online (Natural) - Spanish (Equatorial Guinea)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-GQ-TeresaNeural', 'Microsoft Server Speech Text to Speech Voice (es-GQ, TeresaNeural)',
        'es-GQ-TeresaNeural', 'Female', 'es-GQ', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Teresa Online (Natural) - Spanish (Equatorial Guinea)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-GT-AndresNeural', 'Microsoft Server Speech Text to Speech Voice (es-GT, AndresNeural)',
        'es-GT-AndresNeural', 'Male', 'es-GT', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Andres Online (Natural) - Spanish (Guatemala)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-GT-MartaNeural', 'Microsoft Server Speech Text to Speech Voice (es-GT, MartaNeural)', 'es-GT-MartaNeural',
        'Female', 'es-GT', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Marta Online (Natural) - Spanish (Guatemala)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-HN-CarlosNeural', 'Microsoft Server Speech Text to Speech Voice (es-HN, CarlosNeural)',
        'es-HN-CarlosNeural', 'Male', 'es-HN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Carlos Online (Natural) - Spanish (Honduras)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-HN-KarlaNeural', 'Microsoft Server Speech Text to Speech Voice (es-HN, KarlaNeural)', 'es-HN-KarlaNeural',
        'Female', 'es-HN', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Karla Online (Natural) - Spanish (Honduras)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-MX-DaliaNeural', 'Microsoft Server Speech Text to Speech Voice (es-MX, DaliaNeural)', 'es-MX-DaliaNeural',
        'Female', 'es-MX', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Dalia Online (Natural) - Spanish (Mexico)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-MX-JorgeNeural', 'Microsoft Server Speech Text to Speech Voice (es-MX, JorgeNeural)', 'es-MX-JorgeNeural',
        'Male', 'es-MX', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Jorge Online (Natural) - Spanish (Mexico)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-NI-FedericoNeural', 'Microsoft Server Speech Text to Speech Voice (es-NI, FedericoNeural)',
        'es-NI-FedericoNeural', 'Male', 'es-NI', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Federico Online (Natural) - Spanish (Nicaragua)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-NI-YolandaNeural', 'Microsoft Server Speech Text to Speech Voice (es-NI, YolandaNeural)',
        'es-NI-YolandaNeural', 'Female', 'es-NI', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Yolanda Online (Natural) - Spanish (Nicaragua)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-PA-MargaritaNeural', 'Microsoft Server Speech Text to Speech Voice (es-PA, MargaritaNeural)',
        'es-PA-MargaritaNeural', 'Female', 'es-PA', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Margarita Online (Natural) - Spanish (Panama)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-PA-RobertoNeural', 'Microsoft Server Speech Text to Speech Voice (es-PA, RobertoNeural)',
        'es-PA-RobertoNeural', 'Male', 'es-PA', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Roberto Online (Natural) - Spanish (Panama)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-PY-MarioNeural', 'Microsoft Server Speech Text to Speech Voice (es-PY, MarioNeural)', 'es-PY-MarioNeural',
        'Male', 'es-PY', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Mario Online (Natural) - Spanish (Paraguay)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-PY-TaniaNeural', 'Microsoft Server Speech Text to Speech Voice (es-PY, TaniaNeural)', 'es-PY-TaniaNeural',
        'Female', 'es-PY', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Tania Online (Natural) - Spanish (Paraguay)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-PE-AlexNeural', 'Microsoft Server Speech Text to Speech Voice (es-PE, AlexNeural)', 'es-PE-AlexNeural',
        'Male', 'es-PE', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Alex Online (Natural) - Spanish (Peru)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-PE-CamilaNeural', 'Microsoft Server Speech Text to Speech Voice (es-PE, CamilaNeural)',
        'es-PE-CamilaNeural', 'Female', 'es-PE', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Camila Online (Natural) - Spanish (Peru)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-PR-KarinaNeural', 'Microsoft Server Speech Text to Speech Voice (es-PR, KarinaNeural)',
        'es-PR-KarinaNeural', 'Female', 'es-PR', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Karina Online (Natural) - Spanish (Puerto Rico)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-PR-VictorNeural', 'Microsoft Server Speech Text to Speech Voice (es-PR, VictorNeural)',
        'es-PR-VictorNeural', 'Male', 'es-PR', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Victor Online (Natural) - Spanish (Puerto Rico)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-ES-AlvaroNeural', 'Microsoft Server Speech Text to Speech Voice (es-ES, AlvaroNeural)',
        'es-ES-AlvaroNeural', 'Male', 'es-ES', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Alvaro Online (Natural) - Spanish (Spain)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-ES-ElviraNeural', 'Microsoft Server Speech Text to Speech Voice (es-ES, ElviraNeural)',
        'es-ES-ElviraNeural', 'Female', 'es-ES', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Elvira Online (Natural) - Spanish (Spain)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-US-AlonsoNeural', 'Microsoft Server Speech Text to Speech Voice (es-US, AlonsoNeural)',
        'es-US-AlonsoNeural', 'Male', 'es-US', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Alonso Online (Natural) - Spanish (United States)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-US-PalomaNeural', 'Microsoft Server Speech Text to Speech Voice (es-US, PalomaNeural)',
        'es-US-PalomaNeural', 'Female', 'es-US', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Paloma Online (Natural) - Spanish (United States)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-UY-MateoNeural', 'Microsoft Server Speech Text to Speech Voice (es-UY, MateoNeural)', 'es-UY-MateoNeural',
        'Male', 'es-UY', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Mateo Online (Natural) - Spanish (Uruguay)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-UY-ValentinaNeural', 'Microsoft Server Speech Text to Speech Voice (es-UY, ValentinaNeural)',
        'es-UY-ValentinaNeural', 'Female', 'es-UY', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Valentina Online (Natural) - Spanish (Uruguay)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-VE-PaolaNeural', 'Microsoft Server Speech Text to Speech Voice (es-VE, PaolaNeural)', 'es-VE-PaolaNeural',
        'Female', 'es-VE', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Paola Online (Natural) - Spanish (Venezuela)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('es-VE-SebastianNeural', 'Microsoft Server Speech Text to Speech Voice (es-VE, SebastianNeural)',
        'es-VE-SebastianNeural', 'Male', 'es-VE', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Sebastian Online (Natural) - Spanish (Venezuela)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('su-ID-JajangNeural', 'Microsoft Server Speech Text to Speech Voice (su-ID, JajangNeural)',
        'su-ID-JajangNeural', 'Male', 'su-ID', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Jajang Online (Natural) - Sundanese (Indonesia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('su-ID-TutiNeural', 'Microsoft Server Speech Text to Speech Voice (su-ID, TutiNeural)', 'su-ID-TutiNeural',
        'Female', 'su-ID', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Tuti Online (Natural) - Sundanese (Indonesia)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('sw-KE-RafikiNeural', 'Microsoft Server Speech Text to Speech Voice (sw-KE, RafikiNeural)',
        'sw-KE-RafikiNeural', 'Male', 'sw-KE', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Rafiki Online (Natural) - Swahili (Kenya)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('sw-KE-ZuriNeural', 'Microsoft Server Speech Text to Speech Voice (sw-KE, ZuriNeural)', 'sw-KE-ZuriNeural',
        'Female', 'sw-KE', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Zuri Online (Natural) - Swahili (Kenya)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('sw-TZ-DaudiNeural', 'Microsoft Server Speech Text to Speech Voice (sw-TZ, DaudiNeural)', 'sw-TZ-DaudiNeural',
        'Male', 'sw-TZ', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Daudi Online (Natural) - Swahili (Tanzania)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('sw-TZ-RehemaNeural', 'Microsoft Server Speech Text to Speech Voice (sw-TZ, RehemaNeural)',
        'sw-TZ-RehemaNeural', 'Female', 'sw-TZ', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Rehema Online (Natural) - Swahili (Tanzania)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('sv-SE-MattiasNeural', 'Microsoft Server Speech Text to Speech Voice (sv-SE, MattiasNeural)',
        'sv-SE-MattiasNeural', 'Male', 'sv-SE', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Mattias Online (Natural) - Swedish (Sweden)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('sv-SE-SofieNeural', 'Microsoft Server Speech Text to Speech Voice (sv-SE, SofieNeural)', 'sv-SE-SofieNeural',
        'Female', 'sv-SE', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Sofie Online (Natural) - Swedish (Sweden)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ta-IN-PallaviNeural', 'Microsoft Server Speech Text to Speech Voice (ta-IN, PallaviNeural)',
        'ta-IN-PallaviNeural', 'Female', 'ta-IN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Pallavi Online (Natural) - Tamil (India)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ta-IN-ValluvarNeural', 'Microsoft Server Speech Text to Speech Voice (ta-IN, ValluvarNeural)',
        'ta-IN-ValluvarNeural', 'Male', 'ta-IN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Valluvar Online (Natural) - Tamil (India)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ta-MY-KaniNeural', 'Microsoft Server Speech Text to Speech Voice (ta-MY, KaniNeural)', 'ta-MY-KaniNeural',
        'Female', 'ta-MY', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Kani Online (Natural) - Tamil (Malaysia)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ta-MY-SuryaNeural', 'Microsoft Server Speech Text to Speech Voice (ta-MY, SuryaNeural)', 'ta-MY-SuryaNeural',
        'Male', 'ta-MY', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Surya Online (Natural) - Tamil (Malaysia)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ta-SG-AnbuNeural', 'Microsoft Server Speech Text to Speech Voice (ta-SG, AnbuNeural)', 'ta-SG-AnbuNeural',
        'Male', 'ta-SG', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Anbu Online (Natural) - Tamil (Singapore)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ta-SG-VenbaNeural', 'Microsoft Server Speech Text to Speech Voice (ta-SG, VenbaNeural)', 'ta-SG-VenbaNeural',
        'Female', 'ta-SG', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Venba Online (Natural) - Tamil (Singapore)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ta-LK-KumarNeural', 'Microsoft Server Speech Text to Speech Voice (ta-LK, KumarNeural)', 'ta-LK-KumarNeural',
        'Male', 'ta-LK', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Kumar Online (Natural) - Tamil (Sri Lanka)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ta-LK-SaranyaNeural', 'Microsoft Server Speech Text to Speech Voice (ta-LK, SaranyaNeural)',
        'ta-LK-SaranyaNeural', 'Female', 'ta-LK', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Saranya Online (Natural) - Tamil (Sri Lanka)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('te-IN-MohanNeural', 'Microsoft Server Speech Text to Speech Voice (te-IN, MohanNeural)', 'te-IN-MohanNeural',
        'Male', 'te-IN', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Mohan Online (Natural) - Telugu (India)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('te-IN-ShrutiNeural', 'Microsoft Server Speech Text to Speech Voice (te-IN, ShrutiNeural)',
        'te-IN-ShrutiNeural', 'Female', 'te-IN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Shruti Online (Natural) - Telugu (India)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('th-TH-NiwatNeural', 'Microsoft Server Speech Text to Speech Voice (th-TH, NiwatNeural)', 'th-TH-NiwatNeural',
        'Male', 'th-TH', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Niwat Online (Natural) - Thai (Thailand)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('th-TH-PremwadeeNeural', 'Microsoft Server Speech Text to Speech Voice (th-TH, PremwadeeNeural)',
        'th-TH-PremwadeeNeural', 'Female', 'th-TH', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Premwadee Online (Natural) - Thai (Thailand)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('tr-TR-AhmetNeural', 'Microsoft Server Speech Text to Speech Voice (tr-TR, AhmetNeural)', 'tr-TR-AhmetNeural',
        'Male', 'tr-TR', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Ahmet Online (Natural) - Turkish (Turkey)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('tr-TR-EmelNeural', 'Microsoft Server Speech Text to Speech Voice (tr-TR, EmelNeural)', 'tr-TR-EmelNeural',
        'Female', 'tr-TR', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Emel Online (Natural) - Turkish (Turkey)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('uk-UA-OstapNeural', 'Microsoft Server Speech Text to Speech Voice (uk-UA, OstapNeural)', 'uk-UA-OstapNeural',
        'Male', 'uk-UA', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Ostap Online (Natural) - Ukrainian (Ukraine)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('uk-UA-PolinaNeural', 'Microsoft Server Speech Text to Speech Voice (uk-UA, PolinaNeural)',
        'uk-UA-PolinaNeural', 'Female', 'uk-UA', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Polina Online (Natural) - Ukrainian (Ukraine)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ur-IN-GulNeural', 'Microsoft Server Speech Text to Speech Voice (ur-IN, GulNeural)', 'ur-IN-GulNeural',
        'Female', 'ur-IN', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Gul Online (Natural) - Urdu (India)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ur-IN-SalmanNeural', 'Microsoft Server Speech Text to Speech Voice (ur-IN, SalmanNeural)',
        'ur-IN-SalmanNeural', 'Male', 'ur-IN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Salman Online (Natural) - Urdu (India)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ur-PK-AsadNeural', 'Microsoft Server Speech Text to Speech Voice (ur-PK, AsadNeural)', 'ur-PK-AsadNeural',
        'Male', 'ur-PK', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Asad Online (Natural) - Urdu (Pakistan)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('ur-PK-UzmaNeural', 'Microsoft Server Speech Text to Speech Voice (ur-PK, UzmaNeural)', 'ur-PK-UzmaNeural',
        'Female', 'ur-PK', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Uzma Online (Natural) - Urdu (Pakistan)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('uz-UZ-MadinaNeural', 'Microsoft Server Speech Text to Speech Voice (uz-UZ, MadinaNeural)',
        'uz-UZ-MadinaNeural', 'Female', 'uz-UZ', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Madina Online (Natural) - Uzbek (Uzbekistan)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('uz-UZ-SardorNeural', 'Microsoft Server Speech Text to Speech Voice (uz-UZ, SardorNeural)',
        'uz-UZ-SardorNeural', 'Male', 'uz-UZ', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Sardor Online (Natural) - Uzbek (Uzbekistan)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('vi-VN-HoaiMyNeural', 'Microsoft Server Speech Text to Speech Voice (vi-VN, HoaiMyNeural)',
        'vi-VN-HoaiMyNeural', 'Female', 'vi-VN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft HoaiMy Online (Natural) - Vietnamese (Vietnam)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('vi-VN-NamMinhNeural', 'Microsoft Server Speech Text to Speech Voice (vi-VN, NamMinhNeural)',
        'vi-VN-NamMinhNeural', 'Male', 'vi-VN', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft NamMinh Online (Natural) - Vietnamese (Vietnam)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('cy-GB-AledNeural', 'Microsoft Server Speech Text to Speech Voice (cy-GB, AledNeural)', 'cy-GB-AledNeural',
        'Male', 'cy-GB', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Aled Online (Natural) - Welsh (United Kingdom)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('cy-GB-NiaNeural', 'Microsoft Server Speech Text to Speech Voice (cy-GB, NiaNeural)', 'cy-GB-NiaNeural',
        'Female', 'cy-GB', 'audio-24khz-48kbitrate-mono-mp3', 'Microsoft Nia Online (Natural) - Welsh (United Kingdom)',
        'GA', '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('zu-ZA-ThandoNeural', 'Microsoft Server Speech Text to Speech Voice (zu-ZA, ThandoNeural)',
        'zu-ZA-ThandoNeural', 'Female', 'zu-ZA', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Thando Online (Natural) - Zulu (South Africa)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');
INSERT INTO edge_tts_config_entity (config_id, name, short_name, gender, locale, suggested_codec, friendly_name, status,
                                    voice_tag)
VALUES ('zu-ZA-ThembaNeural', 'Microsoft Server Speech Text to Speech Voice (zu-ZA, ThembaNeural)',
        'zu-ZA-ThembaNeural', 'Male', 'zu-ZA', 'audio-24khz-48kbitrate-mono-mp3',
        'Microsoft Themba Online (Natural) - Zulu (South Africa)', 'GA',
        '[{"contentCategories":["General"],"voicePersonalities":["Friendly","Positive"]}]');

create table if not exists edge_tts_setting_entity
(
    id        int auto_increment
        primary key,
    en_name   varchar(255) null,
    zh_name   varchar(255) null,
    text      varchar(255) null,
    show_flag bit          null
);

INSERT INTO edge_tts_setting_entity (en_name, zh_name, text, show_flag)
VALUES ('en', '英文', 'Hello! Today is another day full of energy!', true);
INSERT INTO edge_tts_setting_entity (en_name, zh_name, text, show_flag)
VALUES ('zh', '中文', '你好呀！今天又是元气满满的一天！', true);
INSERT INTO edge_tts_setting_entity (en_name, zh_name, text, show_flag)
VALUES ('ja', '日文', 'こんにちは！今日はまた元気いっぱいの一日です！', true);
INSERT INTO edge_tts_setting_entity (en_name, zh_name, text, show_flag)
VALUES ('ko', '韩文', '안녕하세요! 오늘도 활기찬 하루입니다!', true);

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
    project_type    varchar(255) not null,
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

