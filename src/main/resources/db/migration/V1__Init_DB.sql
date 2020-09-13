create table captcha_codes (
    id integer not null auto_increment,
    code varchar(255) not null,
    secret_code varchar(255) not null,
    time datetime(6) not null,
    primary key (id)
);

create table global_settings (
    id integer not null auto_increment,
    code varchar(255) not null,
    name varchar(255) not null,
    value varchar(255) not null,
    primary key (id)
);

create table post_comments (
    id integer not null auto_increment,
    parent_id integer,
    text text not null,
    time datetime(6) not null,
    post_id integer not null,
    user_id integer not null,
    primary key (id)
);

create table post_votes (
    id integer not null auto_increment,
    time datetime(6) not null,
    value tinyint not null,
    post_id integer not null,
    user_id integer not null,
    primary key (id)
 );

create table posts (
    id integer not null auto_increment,
    is_active tinyint not null,
    moderation_status varchar(255) not null,
    text text not null,
    time datetime(6) not null,
    title varchar(255) not null,
    view_count integer not null,
    moderator_id integer,
    user_id integer not null,
    primary key (id)
);

create table tag2post (
    id integer not null auto_increment,
    post_id integer not null,
    tag_id integer not null,
    primary key (id)
);

create table tags (
    id integer not null auto_increment,
    name varchar(255) not null,
    primary key (id)
);

create table users (
    id integer not null auto_increment,
    code varchar(255),
    email varchar(255) not null,
    is_moderator tinyint not null,
    name varchar(255) not null,
    password varchar(255) not null,
    photo varchar(255),
    reg_time datetime(6) not null,
    primary key (id)
);

alter table post_comments
    add constraint comment_post_fk
    foreign key (post_id) references posts (id);

alter table post_comments
    add constraint comment_user_fk
    foreign key (user_id) references users (id);

alter table post_votes
    add constraint vote_post_fk
    foreign key (post_id) references posts (id);

alter table post_votes
    add constraint vote_user_fk
    foreign key (user_id) references users (id);

alter table posts
    add constraint post_moderator_fk
    foreign key (moderator_id) references users (id);

alter table posts
    add constraint post_user_fk
    foreign key (user_id) references users (id);

alter table tag2post
    add constraint tag_post_id
    foreign key (post_id) references posts (id);

alter table tag2post
    add constraint tag_tag_fk
    foreign key (tag_id) references tags (id);