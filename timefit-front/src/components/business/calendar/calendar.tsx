'use client';

import koLocale from '@fullcalendar/core/locales/ko';
import dayGridPlugin from '@fullcalendar/daygrid'; // a plugin!
import interactionPlugin from '@fullcalendar/interaction'; // needed for dayClick
import FullCalendar from '@fullcalendar/react';
import { toast } from 'sonner';

export default function Calendar() {
  const handleDateClick = (arg: { dateStr: string }) => {
    toast('date click! ' + arg.dateStr);
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
