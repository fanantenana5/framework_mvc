package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class DateUtil {
	private DateUtil() {
		
	}
	
	private static List<SimpleDateFormat> dateFormats = new ArrayList<SimpleDateFormat>() {{
		add(new SimpleDateFormat("M/dd/yyyy"));
		add(new SimpleDateFormat("dd.M.yyyy"));
		add(new SimpleDateFormat("M/dd/yyy hh:mm:ss a"));
		add(new SimpleDateFormat("dd.MM.yyyy hh:mm:ss a"));
		add(new SimpleDateFormat("dd.MMM.yyyy"));
		add(new SimpleDateFormat("dd-MMM-yyyy"));
		add(new SimpleDateFormat("yyyy-MM-dd"));
		add(new SimpleDateFormat("yyyy/MM/dd"));
		add(new SimpleDateFormat("dd-MM-yyyy"));
		add(new SimpleDateFormat("dd/MM/yyyy"));
	}};
	
	public static Date convertToDate(String input) {
		Date date = null;
		if(input == null) {
			return null;
		}
		for(SimpleDateFormat format: DateUtil.dateFormats) {
			try {
				format.setLenient(false);
				date = format.parse(input);
			}catch(ParseException e) {
				continue;
			}
			if(date != null) {
				break;
			}
		}
		return date;
	}
}
