package com.prokmodular.comms;

/**
 * Created by martin on 2018-12-21 at 18:11
 */
public class CommandContents {
    public String name;
    public String data;

    public CommandContents(String commandName, String value) {
        name = commandName;
        data = value;
    }

    public boolean is(String commandName) {
        return name.equalsIgnoreCase(commandName);
    }
}
