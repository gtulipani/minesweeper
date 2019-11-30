CREATE TABLE `game_cells` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `game_id` bigint(11) unsigned,
  `row_number` bigint(11) unsigned NOT NULL,
  `col_number` bigint(11) unsigned NOT NULL,
  `cell_content` varchar(10) NOT NULL DEFAULT 'NUMBER',
  `mines_around` bigint(11) DEFAULT NULL,
  `cell_operation` varchar(20) NOT NULL DEFAULT 'NONE',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
