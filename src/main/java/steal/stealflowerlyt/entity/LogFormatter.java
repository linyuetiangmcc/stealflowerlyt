package steal.stealflowerlyt.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;

public class LogFormatter extends java.util.logging.Formatter {
	@Override
	public String format(LogRecord record) {
		Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd:HHmmss SSS");
        String excuteDateStr = format.format(now);
        return excuteDateStr + "---" + record.getMessage() + "\r\n";
	}

}
