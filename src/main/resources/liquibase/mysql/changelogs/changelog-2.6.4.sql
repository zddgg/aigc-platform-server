-- liquibase formatted sql

-- changeset wenliang:1
create table tm_prompt_template
(
    id             int auto_increment
        primary key,
    template_group varchar(255) null,
    is_default     bit null,
    template_name  varchar(255) null,
    system_prompt  text null,
    user_prompt    text null,
    is_preset     bit null
);
INSERT INTO tm_prompt_template (id, template_group, is_default, template_name, system_prompt, user_prompt, is_preset) VALUES (1, 'novel_role_inference', true, '小说角色推理', '你是一个小说内容台词分析员，你会精确的找到台词在原文中的位置并分析属于哪个角色，以及角色在说这句台词时的上下文环境及情绪等。', '严格按照以下要求工作：
1. 分析下面原文中有哪些角色，角色中有观众、群众之类的角色时统一使用观众这个角色，他们的性别和年龄段只能在下面范围中选择一个：
性别：男、女、未知。
年龄段：少年、青年、中年、老年、未知。

2. 请分析下面台词部分的内容是属于原文部分中哪个角色的，然后结合上下文分析当时的情绪，情绪只能在下面范围中选择一个：
情绪：中立、开心、吃惊、难过、厌恶、生气、恐惧。

3. 严格按照台词文本中的顺序在原文文本中查找。每行台词都做一次处理，不能合并台词。
4. 分析的台词内容如果不是台词，不要加入到返回结果中。
5. 返回结果只能包含角色分析、台词分析两个部分。

输出格式如下：
角色分析:
角色名,男,青年
角色名,女,青年

台词分析:
台词序号,角色名,高兴
台词序号,角色名,难过

原文部分：
@{小说内容}

台词部分：
@{对话列表}', true);
