CREATE TABLE `game_configurations` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `rows_quantity` bigint(11) unsigned NOT NULL,
  `columns_quantity` bigint(11) unsigned NOT NULL,
  `mines_quantity` bigint(11) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

