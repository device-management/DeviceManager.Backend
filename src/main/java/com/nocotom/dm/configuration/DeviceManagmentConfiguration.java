package com.nocotom.dm.configuration;

public class DeviceManagmentConfiguration {

    public final static String COMMAND_TOPIC_PATTERN = "devices/{0}/command";

    public final static String MEASUREMENT_TOPIC = "devices/+/measurement";

    public final static String REGISTER_TOPIC = "devices/+/register";

    public final static String STATE_TOPIC = "devices/+/state";
}
