package com.projectkorra.rpg.modules.worldevents.schedule;

/**
 * TEMP CLASS
 */
public enum ScheduleType {
    IN_GAME_DAYS,
    REAL_DAYS;

    public static ScheduleType fromString(String rawCalendar) {
        return IN_GAME_DAYS;
    }
}
