CREATE TABLE `app_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `file_id` bigint(20) NOT NULL,
  `content` text NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_comment_user` (`user_id`),
  CONSTRAINT `FK_comment_user` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`),
  KEY `FK_comment_file` (`file_id`),
  CONSTRAINT `FK_comment_file` FOREIGN KEY (`file_id`) REFERENCES `file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
