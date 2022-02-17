package net.alemas.oss.tools.eventscollector.configuration;


/**
 * The class defines the paths for the all endpoints.
 */
public class EndpointsPaths
{

    /* --- endpoint paths for: events counter --- */
    public final static String  REQUEST_PATH_COUNTER_BASE       = "/events/counter";
    public final static String  REQUEST_PATH_COUNTER_EVENT      = "/";
    public final static String  REQUEST_PATH_COUNTER_EVENT_XLSX = "/counters.xlsx";

    public final static String  FULL_PATH_COUNTER_EVENT         = EndpointsPaths.REQUEST_PATH_COUNTER_BASE + EndpointsPaths.REQUEST_PATH_COUNTER_EVENT;


    /* --- endpoint paths for: events timer --- */
    public final static String  REQUEST_PATH_TIMER_BASE         = "/events/timing";
    public final static String  REQUEST_PATH_TIMER_EVENT        = "/";
    public final static String  REQUEST_PATH_TIMER_EVENT_XLSX   = "/timings.xlsx";

    public final static String  FULL_PATH_TIMER_EVENT           = EndpointsPaths.REQUEST_PATH_TIMER_BASE + EndpointsPaths.REQUEST_PATH_TIMER_EVENT;


    /* --- endpoint paths for: log in/out events --- */
    public final static String  REQUEST_PATH_LOGIN_BASE         = "/events/log-in-out";
    public final static String  REQUEST_PATH_LOGIN_EVENT        = "/";
    public final static String  REQUEST_PATH_LOGIN_EVENT_XLSX   = "/logs-in-out.xlsx";

    public final static String  FULL_PATH_LOGIN_EVENT           = EndpointsPaths.REQUEST_PATH_LOGIN_BASE + EndpointsPaths.REQUEST_PATH_LOGIN_EVENT;

}
