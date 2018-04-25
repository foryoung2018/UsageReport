package com.htc.lib1.HtcCalendarFramework.util.calendar.service;

import com.htc.lib1.HtcCalendarFramework.util.calendar.EventInstance;

import java.util.List;

interface IEventService {
    List<EventInstance> getEventsByDay(int startJulianDay, int endJulianDay, 
            long millisOfFirstDay, int widgetId);
    List<EventInstance> getEventsByTime(long begin, long end);
}
