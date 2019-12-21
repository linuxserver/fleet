INSERT INTO
    Repository (`id`, `name`, `sync_enabled`, `version_mask`, `hidden`, `stable`, `deprecated`, `modified`)
SELECT
   `id`, `name`, `sync_enabled`, `version_mask`, 0, 1, 0, `modified`
FROM
     Repositories;

INSERT INTO
    Image (`id`, `repository`, `name`, `description`, `pulls`, `stars`, `sync_enabled`, `version_mask`, `hidden`, `stable`, `deprecated`, `modified`)
SELECT
    `id`, `repository`, `name`, null, `pulls`, 0, 1, `version_mask`, `hidden`, NOT(`unstable`), `deprecated`, `modified`
FROM
    Images;

INSERT INTO
    TagBranch (`image_id`, `name`, `latest_version`, `protected`, `build_date`)
SELECT
    `id`, 'latest', `latest_version`, 1, `latest_version_buildtime`
FROM Images;

INSERT INTO
    Schedule (`name`, `interval`, `java_class`)
VALUES
    ('SyncAllCachedImages', '1:hours',    'io.linuxserver.fleet.v2.thread.schedule.sync.AllImagesSyncSchedule'),
    ('GetMissingImages',    '30:minutes', 'io.linuxserver.fleet.v2.thread.schedule.sync.GetMissingImagesSchedule'),
    ('RefreshCache',        '1:days',     'io.linuxserver.fleet.v2.thread.schedule.cache.RefreshCacheSchedule');
