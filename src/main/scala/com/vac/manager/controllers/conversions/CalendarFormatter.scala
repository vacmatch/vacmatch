package com.vac.manager.controllers.conversions

import java.text.{ ParseException, SimpleDateFormat }
import java.util.{ Calendar, Date, GregorianCalendar, Locale }
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.format.Formatter
import org.springframework.stereotype.Component

@Component
class CalendarFormatter extends Formatter[Calendar] {

  @Autowired
  var messageSource: MessageSource = _

  @throws(classOf[ParseException])
  def parse(text: String, locale: Locale): Calendar = {
    val dateFormat: SimpleDateFormat = createDateFormat(locale);
    val cal = new GregorianCalendar()
    cal.setTime(dateFormat.parse(text))

    return cal
  }

  def print(obj: Calendar, locale: Locale): String = {
    val dateFormat: SimpleDateFormat = createDateFormat(locale);
    println("CALENDAR PRINTING HAPPENING NOW " + obj)
    return dateFormat.format(obj.getTime());
  }

  def createDateFormat(locale: Locale): SimpleDateFormat = {

    println("MESSAGESOURCE IS = " + messageSource)
    // TODO: messageSource is null
    //val format: String = messageSource.getMessage("date.format", null, locale);
    //throw new RuntimeException(format)

    val format = "dd/MM/yyyy HH:mm"
    val dateFormat: SimpleDateFormat = new SimpleDateFormat(format);
    dateFormat.setLenient(false);
    dateFormat;
  }

}
