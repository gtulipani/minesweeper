CREATE TABLE `games` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `rows_quantity` bigint(11) unsigned NOT NULL,
  `columns_quantity` bigint(11) unsigned NOT NULL,
  `mines_quantity` bigint(11) unsigned NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'PLAYING',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
