INSERT INTO `blogdb`.`posts`
(`id`,
`is_active`,
`moderation_status`,
`title`,
`time`,
`text`,
`view_count`,
`moderator_id`,
`user_id`)
VALUES
(1,
1,
'ACCEPTED',
'first post',
'2020-01-01 07:00:00.000000',
'Rain Man is a 1988 American road comedy-drama film directed by Barry Levinson and written by Barry Morrow and Ronald Bass. It tells the story of abrasive, selfish young wheeler-dealer Charlie Babbitt (Tom Cruise), who discovers that his estranged father has died and bequeathed virtually all of his multi-million dollar estate to his other son, Raymond (Dustin Hoffman), an autistic savant, of whose existence Charlie was unaware. Charlie is left with only his father beloved vintage car and rosebushes. Valeria Golino also stars as Charlie girlfriend Susanna.',
0,
1,
1);
INSERT INTO `blogdb`.`posts`
(`id`,
`is_active`,
`moderation_status`,
`title`,
`time`,
`text`,
`view_count`,
`moderator_id`,
`user_id`)
VALUES
(2,
1,
'ACCEPTED',
'User post',
'2020-01-01 10:00:00.000000',
'С самых первых версий в Java не было единого и удобного подхода для работы с датой и временем, поэтому новый Date/Time API является одним из самых нужных и важных нововведений в Java 8. В этой статье мы на примерах рассмотрим все самые главные нововведения для работы с датой и временем.',
0,
1,
2);