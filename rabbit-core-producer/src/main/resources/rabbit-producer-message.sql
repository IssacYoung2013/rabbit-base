-- 表broker_message

CREATE TABLE IF NOT EXISTS `broker_message` (
    `message_id` varchar(128) NOT NULL ,
    `message` varchar(4000),
    `try_count` int(4) DEFAULT  0,
    `status` varchar(10) DEFAULT  '',
    `next_retry` timestamp  NULL DEFAULT  NULL ,
    `create_time` timestamp NOT NULL,
    `update_time` timestamp NOT NULL ,
    PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;