package com.client.core.base.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class Utility {

	public static Map<String, String> commaDelimitedStringToMap(String commaDelimited) {
		List<String> parsed = Arrays.asList(commaDelimited.split(","));
		Map<String, String> theMap = new LinkedHashMap<String, String>();

		for(String value : parsed) {
			theMap.put(value, value);
		}

		return theMap;
	}

	public static BigDecimal parseBigDecimal(String strVal) {
		if(strVal == null || strVal.isEmpty()) {
			return null;
		} else {
			try {
				Double check = Double.parseDouble(strVal);
				return new BigDecimal(check);
			} catch(NumberFormatException e) {
				return null;
			}
		}
	}

	public static BigDecimal forceParseBigDecimal(String strVal) {
		if(strVal == null || strVal.isEmpty()) {
			return new BigDecimal(0);
		} else {
			try {
				Double check = Double.parseDouble(strVal);
				return new BigDecimal(check);
			} catch(NumberFormatException e) {
				return new BigDecimal(0);
			}
		}
	}

    public static Integer parseInteger(Object strVal) {
        if(strVal == null || strVal.toString().isEmpty()) {
            return null;
        } else {
            try {
                return Integer.parseInt(strVal.toString());
            } catch(NumberFormatException e) {
                return null;
            }
        }
    }

	public static Integer forceParseInteger(String strVal) {
		if(strVal == null || strVal.isEmpty()) {
			return new Integer(0);
		} else {
			try {
				return Integer.parseInt(strVal);
			} catch(NumberFormatException e) {
				return new Integer(0);
			}
		}
	}

	public static Double parseDouble(String strVal) {
		if(strVal == null || strVal.isEmpty()) {
			return null;
		}else {
			try {
				return Double.parseDouble(strVal);
			} catch(NumberFormatException e) {
				return null;
			}
		}
	}

	public static Double forceParseDouble(String strVal) {
		if(strVal == null || strVal.isEmpty()) {
			return new Double(0);
		}else {
			try {
				return Double.parseDouble(strVal);
			} catch(NumberFormatException e) {
				return new Double(0);
			}
		}
	}

	public static Map<String, String> getRequestParameterMap(HttpServletRequest req) {
		Map<String, String> parameters = new LinkedHashMap<String, String>();

		Enumeration<?> e = req.getParameterNames();
		while(e.hasMoreElements()) {
			String name = e.nextElement().toString();
			String value = req.getParameter(name);
			parameters.put(name, value);
		}

		return parameters;
	}

	public static void printParameters(HttpServletRequest req) {
		@SuppressWarnings("unchecked")
		Enumeration<String> enumeration = req.getParameterNames();

		while (enumeration.hasMoreElements()) {
			String paramName = enumeration.nextElement().toString();
			String paramValue = req.getParameter(paramName);
			System.out.println("Element : " + paramName + " ------ value : " + paramValue);
		}
		System.out.println(" ------\n ");
	}


	public static  Date parseStringToDate(String dateStr, String dateFormat) {
		if(dateStr == null || dateStr.isEmpty() || dateFormat == null || dateFormat.isEmpty()) {
			return null;
		} else {
			DateFormat format = new SimpleDateFormat(dateFormat);
			try {
				return format.parse(dateStr);
			} catch(ParseException e) {
				return null;
			}
		}
	}

	public static  Date forceParseStringToDate(String dateStr, String dateFormat) {
		if(dateStr == null || dateStr.isEmpty() || dateFormat == null || dateFormat.isEmpty()) {
            return DateTime.now().toDate();
		} else {
			TimeZone timeZone = TimeZone.getTimeZone("America/Detroit");
			DateFormat format = new SimpleDateFormat(dateFormat);
			format.setTimeZone(timeZone);
			format.setLenient(false);
			try {
				return format.parse(dateStr);
			} catch(ParseException e) {
				return new Date(0);
			}
		}
	}

	public static DateTime parseStringToDateTime(String dateStr, String dateFormat) {
	
	    
	    Date date = parseStringToDate(dateStr, dateFormat);
	    
	    if(date == null){
	        return null;
	    }
		
		DateTime now = Util.nowUsingTimeZone(null);
		
		DateTime theCorrectDate = new DateTime(date,DateTimeZone.UTC);
		theCorrectDate = theCorrectDate.plusHours(now.getHourOfDay());
		theCorrectDate = theCorrectDate.plusMinutes(now.getMinuteOfHour());
		return theCorrectDate;
	}
	

	
	public static DateTime forceParseStringToDateTime(String dateStr, String dateFormat) {
		return new DateTime(forceParseStringToDate(dateStr, dateFormat));
	}

	public static  XMLGregorianCalendar forceParseStringToXMLGregorianCalendar(String dateStr, String dateFormat) {
		return dateToXMLGregorianCal(forceParseStringToDate(dateStr, dateFormat));
	}

	public static String formatDate(Date date, String format) {
		if(date == null) {
			return "";
		}

		TimeZone timeZone = TimeZone.getTimeZone("America/Detroit");
		
		DateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setLenient(false);
		dateFormat.setTimeZone(timeZone);
		return dateFormat.format(date);
	}
	
	public static String formatDateTime(DateTime date, String format) {
		if(date == null) {
			return "";
		}
		
		return formatDate(date.toDate(), format);
	}

	public static XMLGregorianCalendar dateToXMLGregorianCal(Date date) {
		if(date == null) {
			return  null;
		}
		
		TimeZone timeZone = TimeZone.getTimeZone("America/Detroit");
		
		GregorianCalendar gregorianCal = new GregorianCalendar(timeZone);
		gregorianCal.setTime(date);

		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCal);
		} catch (DatatypeConfigurationException e) {
			return null;
		}
	}

	public static XMLGregorianCalendar dateToXMLGregorianCalWithTimezone(Date date, TimeZone timezone) {
		if(date == null) {
			return  null;
		}
		
		DateTimeZone dateTimeZone = DateTimeZone.forTimeZone(timezone);
		
		DateTime datetime = new DateTime(date.getTime(), dateTimeZone);
		
		return dateTimeToXmlGregorianCalendar(datetime);
	}

	public static Date xmlGregorianCalToDate(XMLGregorianCalendar xmlGregCal) {
		if(xmlGregCal == null) {
			return null;
		}
		return xmlGregCal.toGregorianCalendar().getTime();
	}

	public static Boolean stringContains(String toCheckIn, String toCheckFor) {
		if(toCheckIn == null || toCheckFor == null || toCheckFor.isEmpty()) {
			return false;
		}
		
		if(toCheckIn.isEmpty()) {
			return true;
		}
	
		List<String> valuesForCheck = Arrays.asList(toCheckIn.split(","));
		for(String value : valuesForCheck) {
			if(value.trim().equalsIgnoreCase(toCheckFor.trim())) {
				return true;
			}
		}
		
		return false;
	}
	
	public static Boolean listContains(List<String> toCheckIn, String toCheckFor) {
		for(String object : toCheckIn) {
			if(object.equalsIgnoreCase(toCheckFor)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static String arrayToString(String[] a, String separator) {
		StringBuilder result = new StringBuilder();
		if (a == null) {
			return null;
		} else if (a.length > 0) {
			result.append(a[0]);
			for (int i = 1; i < a.length; i++) {
				result.append(separator);
				result.append(a[i]);
			}
		}
		
		return result.toString();
	}

	public static Boolean parseBoolean(String bool) {
		if(bool == null || bool.isEmpty()) {
			return false;
		}
		
		if(bool.equalsIgnoreCase(Boolean.TRUE.toString())) {
			return true;
		}
		
		return false;
	}

    public static String parseString(Object value) {
        return value == null ? "" : value.toString();
    }
	
	public static DateTime xmlGregorianCalendarToDateTime(XMLGregorianCalendar calendar) {
		if(calendar == null) {
			return  null;
		}
		
		DateTimeZone timeZone = DateTimeZone.forTimeZone(calendar.getTimeZone(0));
		
		DateTime dateTime = new DateTime(calendar.toGregorianCalendar().getTime(), timeZone);
		
		return dateTime;
	}
	
	public static XMLGregorianCalendar dateTimeToXmlGregorianCalendar(DateTime dateTime) {
		if(dateTime == null) {
			return  null;
		}
		
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTimeInMillis(dateTime.toLocalDateTime().toDate().getTime());

		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
		} catch (DatatypeConfigurationException e) {
			return null;
		}
	}
	
	public static String createEntireInStatement(List<String> values) {
		StringBuilder builder = new StringBuilder(" IN (");
		
		for(String value : values) {
			builder.append("'"+value + "',");
		}
		
		return builder.subSequence(0, builder.length()-1).toString()+")";
	}
	
	public static Integer nullCheck(Integer value) {
		return value == null ? 0 : value;
	}

}
