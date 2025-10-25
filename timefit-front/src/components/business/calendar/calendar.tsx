'use client';

import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid'; // a plugin!
import interactionPlugin from '@fullcalendar/interaction'; // needed for dayClick
import koLocale from '@fullcalendar/core/locales/ko';

export default function Calendar() {
  const handleDateClick = (arg: any) => {
    alert('date click! ' + arg.dateStr);
  };

  return (
    <FullCalendar
      plugins={[dayGridPlugin, interactionPlugin]}
      initialView="dayGridMonth"
      dateClick={handleDateClick}
      locale={koLocale}
    />
  );
}
