package io.scriptor.debug;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class RedJDebugFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        return String.format(
                "[%1$tF %1$tT.%1$tL] [%2$-7s] %3$s %n",
                new Date(record.getMillis()),
                record.getLevel(),
                record.getMessage()
        );
    }
}
