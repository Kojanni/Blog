INSERT INTO `post_comments`
(`id`,
`text`,
`time`,
`post_id`,
`user_id`)
VALUES
(1,
'good, I want to read a book about this',
'2020-01-01 07:10:00.000000',
1,
2);

INSERT INTO `post_comments`
(`id`,
`parent_id`,
`text`,
`time`,
`post_id`,
`user_id`)
VALUES
(2,
1,
'me too',
'2020-01-01 07:15:00.000000',
1,
1);

INSERT INTO `post_comments`
(`id`,
`text`,
`time`,
`post_id`,
`user_id`)
VALUES
(3,
'good top, thx',
'2020-01-02 10:10:00.000000',
2,
2);


INSERT INTO `post_comments`
(`id`,
`text`,
`time`,
`post_id`,
`user_id`)
VALUES
(4,
'oooo, thanks you for post (4)',
'2020-01-02 10:10:00.000000',
3,
2);

INSERT INTO `post_comments`
(`id`,
`text`,
`time`,
`post_id`,
`user_id`)
VALUES
(5,
'second comment',
'2020-01-10 10:10:00.000000',
3,
2);

INSERT INTO `post_comments`
(`id`,
`text`,
`time`,
`post_id`,
`user_id`)
VALUES
(6,
'3 comment',
'2020-01-11 10:10:00.000000',
3,
2);