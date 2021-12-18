package com.denisindenbom.cyberauth.logfilter;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsoleFilter extends AbstractFilter
{
    private final List<String> hiddenCommands = getCommandsList("/login ", "/l ", "/log ", "/register ", "/r ", "/reg ", "/change_password ");

    public ConsoleFilter()
    {}

    private Result validateMessage(Message message)
    {
        if (message == null) return Result.NEUTRAL;
        return validateMessage(message.getFormattedMessage());
    }

    private Result validateMessage(String message)
    {
        if (message == null) return Result.NEUTRAL;

        message = message.toLowerCase();

        if (!message.contains("issued server command:")) return Result.NEUTRAL;
        for (String command : this.hiddenCommands)
        {
            if (message.contains(command)) return Result.DENY;
        }

        return Result.NEUTRAL;
    }

    @Override
    public Result filter(LogEvent event)
    {
        Message candidate = null;
        if (event != null)
        {
            candidate = event.getMessage();
        }
        return validateMessage(candidate);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t)
    {
        return validateMessage(msg);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params)
    {
        return validateMessage(msg);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t)
    {
        String candidate = null;
        if (msg != null)
        {
            candidate = msg.toString();
        }
        return validateMessage(candidate);
    }

    private List<String> getCommandsList(String... commands)
    {
        return new ArrayList<>(Arrays.stream(commands).toList());
    }
}
