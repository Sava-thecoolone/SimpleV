package simplev.common;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
    private static final String RESET = "\u001B[0m";
    private static final String WHITE = "\u001B[37m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String GREY = "\u001B[38;5;238m";

    @Override
    public String format(LogRecord record) {
        Level lev = record.getLevel();
        String out = "";
        if (lev == Level.SEVERE) out += RED;
        else if (lev == Level.WARNING) out += YELLOW;
        else if (lev == Level.INFO) out += WHITE;
        else out += GREY;
        out += "["+record.getLoggerName()+" at "+record.getInstant()+"] "+lev.getName()+": "+formatMessage(record);
        String prevStr = "";
        int prevCount = 0;
        if (record.getThrown() != null) {
            out += "\n"+record.getThrown().toString();
            for (StackTraceElement trace : record.getThrown().getStackTrace()) {
                if (prevStr.equals(trace.toString())) {
                    prevCount++;
                    if (prevCount == 3) {
                        out += "\n    ...";
                    } else if (prevCount < 3) {
                        out += "\n    at "+trace.toString();
                    }
                } else {
                    if (prevCount >= 3) {
                        out += " ["+prevCount+" times]";
                    }
                    out += "\n    at "+trace.toString();
                    prevCount = 0;
                    prevStr = trace.toString();
                }
            }
            if (prevCount >= 3) {
                out += " ["+prevCount+" times]";
            }
        }
        return out+RESET+"\n";
    }
}