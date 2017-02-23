ALTER DATABASE simple CHARACTER SET utf8 COLLATE utf8_unicode_ci;

-- use us-ascii or iso-8859 on mime header tables ?

use simple;

-- select addArticle(archive_ref, archive_date, archive_title, archive_mid, archive_pid, archive_server, archive_group, archive_username, archive_usermail, archive_head, archive_content) as mess_id, archive_title from archives

drop function if exists addUser;
drop function if exists addGroup;
drop function if exists addServer;

drop function if exists getThreadId;
drop function if exists getMessageId;
drop function if exists getLastReference;
drop function if exists getTotalReferences;

drop procedure if exists insertArchives;
drop procedure if exists addHeaders;
drop procedure if exists addHeader;

drop procedure if exists haveGroup;
drop procedure if exists addArticle;

drop procedure if exists show_groups_stats;
drop procedure if exists show_hnames_stats;

drop table if exists news_references;
drop table if exists news_headers;
drop table if exists news_headers_names;
drop table if exists posts;
drop table if exists users;
drop table if exists news_messages;
drop table if exists news_groups;
drop table if exists news_servers;

create table news_servers (
    server_id int(5) unsigned not null auto_increment,
    server_host varchar(250) not null,

    primary key(server_id),

    unique(server_host)
);

create table news_groups (
    group_id int(5) unsigned not null auto_increment,
    group_name varchar(250) not null,
    group_since datetime not null,

    primary key(group_id)
);

create table news_messages (
    message_id int(8) unsigned not null auto_increment,
    message_unique varchar(250) not null,

    primary key(message_id),

    unique(message_unique)
);

create table users (
    user_id int(8) unsigned not null auto_increment,
    user_date datetime not null,
    user_title varchar(250),
    user_mail varchar(250) not null,

    primary key(user_id),

    unique(user_title, user_mail),

    constraint name_and_mail_not_null
        check(user_title is not null or user_mail is not null)
);

create table posts (
    post_id int(8) unsigned not null auto_increment,
    user_id int(8) unsigned not null,
    parent_id int(8) unsigned,
    message_id int(8) unsigned not null,
    post_date datetime not null,
    post_title text,
    post_header longtext,
    post_content longtext,

    primary key(post_id),

    unique(message_id), 

    constraint fk_posts_pid
        foreign key (parent_id)
        references news_messages(message_id)
        on delete cascade,

    constraint fk_posts_uid
        foreign key (user_id)
        references users(user_id)
        on delete cascade,

    constraint fk_posts_mid
        foreign key (message_id)
        references news_messages(message_id)
        on delete cascade,

    constraint no_empty_entries
        check(parent_id is not null or
              post_content is not null or
              post_title is not null)
);

create table news_headers_names (
    key_id int(5) unsigned not null auto_increment,
    key_title varchar(250) not null,

    primary key(key_id),

    unique(key_title)
);

create table news_headers (
    post_id int(8) unsigned not null,
    header_title int(5) unsigned not null,
    header_value text not null,

    unique(post_id, header_title),

    constraint fk_headers_post_id
        foreign key (post_id)
        references posts(post_id)
        on delete cascade,

    constraint fk_headers_key_id
        foreign key (header_title)
        references news_headers_names(key_id)  
        on delete cascade 
);

create table news_references (
    reference_id int(8) unsigned not null,
    server_id int(5) unsigned not null,
    group_id int(5) unsigned not null,
    post_id int(8) unsigned not null,

    unique(server_id, group_id, reference_id),

    constraint fk_news_references_post_id
        foreign key (post_id)
        references posts(post_id)
        on delete cascade,

    constraint fk_news_references_server_id
        foreign key (server_id)
        references news_servers(server_id)
        on delete cascade,

    constraint fk_news_references_group_id
        foreign key (group_id)
        references news_groups(group_id)
        on delete cascade
);

delimiter $$

create function addUser(
    v_user_title varchar(250),
    v_user_mail varchar(250))
returns integer
begin
    declare v_user_id int default null;

    select user_id into v_user_id from users where user_mail = v_user_mail;
    
    if v_user_id is null then
        insert into users (
            user_date,
            user_title,
            user_mail
        ) values (
            now(),
            v_user_title,
            v_user_mail
        );

        set v_user_id = last_insert_id();
    end if;

    return v_user_id;
end$$

create function addGroup(
    v_group_name varchar(250))
returns integer
begin
    declare v_group_id int default null;

    select group_id into v_group_id from news_groups where group_name like v_group_name;
    
    if v_group_id is null then
        insert into news_groups (
            group_name,
            group_since
        ) values (
            v_group_name, now()
        );

        set v_group_id = last_insert_id();
    end if;

    return v_group_id;
end$$

create function addServer(
    v_server_host varchar(250))
returns integer
begin
    declare v_server_id int default null;

    select server_id into v_server_id from news_servers where server_host like v_server_host;
    
    if v_server_id is null then
        insert into news_servers (
            server_host
        ) values (
            v_server_host
        );

        set v_server_id = last_insert_id();
    end if;

    return v_server_id;
end$$

create function getThreadId(
    v_message_id int)
returns integer
begin
    declare v_count int default 0;
    declare v_post_id int default not null;

    while v_message_id > 0 do		
        select post_id, parent_id, count(*)
            into v_post_id, v_message_id, v_count
            from posts where message_id = v_message_id;
        
        if v_count <= 0 then	
            return null;
        end if;
    end while;

    return v_post_id;
end$$

create function getMessageId(
    v_message_uuid text)
returns integer
begin
    declare v_message_id int default null;

    if v_message_uuid is null then
        return null;
    end if;

    select message_id into v_message_id from news_messages
        where message_unique = v_message_uuid;

    if v_message_id is null then
        insert into news_messages(
            message_unique
        ) values (
            v_message_uuid
        );

        set v_message_id = last_insert_id();
    end if;

    return v_message_id;
end$$

create procedure addHeader(
    v_post_id int,
    v_header_title varchar(250),
    v_header_value text)
begin
    declare v_header_key int default null;

    --if v_header_title not like 'Message-ID'
    --or v_header_title not like 'Newsgroups'
    --or v_header_title not like 'References'
    --or v_header_title not like 'Subject'
    --or v_header_title not like 'Lines'
    --or v_header_title not like 'Date'
    --or v_header_title not like 'From'
    --or v_header_title not like 'Xref' then

        select key_id into v_header_key from news_headers_names where key_title = v_header_title;

        if v_header_key is null then
            insert into news_headers_names(
                key_title
            ) values (
                v_header_title
            );

            set v_header_key = last_insert_id();
        end if;

        insert into news_headers (
            post_id,
            header_title,
            header_value
        ) values (
            v_post_id,
            v_header_key,
            v_header_value
        ) on duplicate key update
            header_value = v_header_value;
    --end if;
end$$

create procedure addHeaders(
    v_post_id int,
    v_post_head text)
begin
    declare v_index int default null;
    declare v_header text default null;
    declare v_header_title text default null;
    declare v_header_value text default null;

    while length(v_post_head) > 0 do
        set v_index     = locate('\n', v_post_head, 1);

        if v_index > 0 then	
            set v_header    = substr(v_post_head, 1, v_index-1);
            set v_post_head = trim(substr(v_post_head, v_index+1));
        else
            set v_header    = v_post_head;
            set v_post_head = null;
        end if;

        set v_index         = locate(': ', v_header, 1);
        set v_header_title  = trim(substr(v_header, 1, v_index-1));
        set v_header_value  = trim(substr(v_header, v_index+1));

        call addHeader(v_post_id, v_header_title, v_header_value);		
    end while;
end$$

create procedure addArticle(
    v_reference_id int,
    v_post_date timestamp,
    v_post_title text,
    v_message_uuid text,
    v_parent_uuid text,
    v_server_host varchar(250),
    v_group_name varchar(250),
    v_user_title varchar(250),
    v_user_mail varchar(250),
    v_post_head longtext,
    v_post_content longtext) 
begin
    declare v_group_id int default null;
    declare v_server_id int default null;
    declare v_post_id int default null;
    declare v_parent_id int default null;
    declare v_user_id int default null;
    declare v_message_id int default null;
    declare v_title_id int default null;
    declare v_post_parent_mid int default null;
    
    set v_user_id = addUser(v_user_title, v_user_mail);
    set v_message_id = getMessageId(v_message_uuid);
    set v_post_parent_mid = getMessageId(v_parent_uuid);
    
    select post_id into v_post_id from posts where message_id = v_message_id;  
    select post_id into v_parent_id from posts where message_id = v_post_parent_mid;  


    if v_post_id is null then
        insert into posts (
            user_id, 
            message_id, 
            parent_id, 
            post_date, 
            post_title,
            post_header,
            post_content
        ) values (
            v_user_id, 
            v_message_id, 
            v_parent_id, 
            v_post_date, 
            v_post_title,
            v_post_head,
            v_post_content
        );

        set v_post_id = last_insert_id();
    end if;

    set v_server_id = addServer(v_server_host);
    set v_group_id = addGroup(v_group_name);

    insert into news_references (
        reference_id,
        post_id,
        server_id, 
        group_id
    ) values (
        v_reference_id,
        v_post_id,
        v_server_id, 
        v_group_id
    ) on duplicate key update
        reference_id = v_reference_id;
end$$

CREATE PROCEDURE `show_groups_stats`()
begin
    set SQL_BIG_SELECTS = 1;
    
    select news_groups.group_name, 
    		count(posts.post_id),
    		min(posts.post_date), 
            max(posts.post_date), 
            min(posts.post_id), 
            max(posts.post_id)
        from news_references
        join news_groups on news_groups.group_id=news_references.group_id
        join posts on posts.post_id=news_references.post_id
        group by news_references.group_id
        order by count(posts.post_id) desc;
end$$

CREATE PROCEDURE `show_hnames_stats`()
begin
	set SQL_BIG_SELECTS = 1;
    
    select key_id, key_title, count(*) from news_headers_names
        join news_headers on key_id=header_title
        group by header_title
        order by count(*) desc;
end$$

CREATE PROCEDURE `insertArchives`
    (v_limit int)
begin
    declare v_id int default 0;
    declare v_ref int default 0;
    declare v_mid varchar(250);
    declare v_pid varchar(250) default null;
    declare v_title text default null;
    declare v_date datetime default null;
    declare v_group varchar(250) default null;
    declare v_server varchar(250) default null;
    declare v_username varchar(250) default null;
    declare v_usermail varchar(250) default null;
    declare v_head text default null;
    declare v_content longtext default null;
    
    --drop table if exists tmp_archives;
    --rename table news_archives to tmp_archives;
    --create table news_archives like tmp_archives;
    --alter table tmp_archives engine=MyISAM;
    
    begin
        declare flag int default 0;

        declare fetcher cursor for select 
            archive_id,
            archive_ref, 
            archive_mid, 
            archive_pid, 
            archive_date, 
            archive_title, 
            archive_group, 
            archive_server, 
            archive_username, 
            archive_usermail, 
            archive_head, 
            archive_content
        from tmp_archives
        where year(archive_date) between 1970 and 2020 limit v_limit; 

        declare continue handler for not found set flag = 1;
        DECLARE CONTINUE HANDLER FOR SQLSTATE '23000' BEGIN END;

        open fetcher;

        repeat fetch fetcher 
           into v_id,
                v_ref,
                v_mid, 
                v_pid,
                v_date, 
                v_title,
                v_group,
                v_server,
                v_username,
                v_usermail,
                v_head, 
                v_content;
            call addArticle(
                v_ref,
                v_date, 
                v_title,
                v_mid, 
                v_pid,
                v_server,
                v_group,
                v_username,
                v_usermail,
                v_head, 
                v_content);

            delete from tmp_archives where archive_id = v_id;
        until flag
        end repeat;

        close fetcher;
    end;    

    --drop table tmp_archives;
end$$