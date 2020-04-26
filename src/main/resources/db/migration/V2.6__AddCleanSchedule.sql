INSERT INTO
    Schedule (`name`, `interval`, `delay`, `java_class`)
VALUE
    ('CleanRemovedImages', '1:hours',    '0:minutes',  'io.linuxserver.fleet.v2.thread.schedule.sync.CleanRemovedImagesSchedule');
