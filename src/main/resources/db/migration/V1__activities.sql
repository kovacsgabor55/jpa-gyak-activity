CREATE TABLE `activities`
(
    `id`            BIGINT(20) NOT NULL AUTO_INCREMENT,
    `description`   VARCHAR(200) NULL DEFAULT NULL,
    `start_time`    DATETIME(6) NULL DEFAULT NULL,
    `activity_type` VARCHAR(20) NULL DEFAULT NULL,
    PRIMARY KEY (`id`)
);