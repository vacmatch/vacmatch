package com.vac.manager.controllers.conversions

import org.springframework.format.Formatter
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat
import java.text.ParseException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource

class DateFormatter extends Formatter[Date] {

    @Autowired
    private var messageSource: MessageSource = _

    @throws(classOf[ParseException])
    def parse(text: String, locale: Locale): Date = {
        val dateFormat: SimpleDateFormat = createDateFormat(locale);
        dateFormat.parse(text);
    }

    def print(obj: Date,locale: Locale): String = {
        val dateFormat: SimpleDateFormat = createDateFormat(locale);
        dateFormat.format(obj);
    }

    def createDateFormat(locale: Locale): SimpleDateFormat = {
        val format: String = this.messageSource.getMessage("date.format", null, locale);
        val dateFormat: SimpleDateFormat = new SimpleDateFormat(format);
        dateFormat.setLenient(false);
        dateFormat;
    }

}