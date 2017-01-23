CREATE TABLE `letter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `recipient` varchar(255) NOT NULL,
  `subject` varchar(255) NOT NULL DEFAULT '',
  `body` varchar(255) NOT NULL DEFAULT '',
  `create_time` bigint(20) NOT NULL,
  `send_time` bigint(20) NOT NULL,
  `privacy_type` tinyint(4) NOT NULL DEFAULT '0',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态, 0:未付费, 未发送; 1:已付费, 未发送; 2:已付费, 未发送; 3:已付费, 已发送',
  PRIMARY KEY (`id`),
  KEY `idx_letter_info` (`recipient`,`send_time`,`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;