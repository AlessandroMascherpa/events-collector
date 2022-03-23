package net.alemas.oss.tools.eventscollector.configuration;


/**
 * The class defines the paths for the all endpoints.
 */
public class EndpointsPaths
{

    /* --- endpoint root --- */
    private final static String  EVENTS_ROOT                     = "events";
    
    /* --- endpoint paths for: events counter --- */
    private final static String  EVENTS_COUNTER                  = "counter";
    public  final static String  EXPORTED_FILE_NAME_COUNTER      = EVENTS_COUNTER + "s.xlsx";
    public  final static String  REQUEST_PATH_COUNTER_BASE       = '/' + EVENTS_ROOT + '/' + EVENTS_COUNTER;
    public  final static String  REQUEST_PATH_COUNTER_EVENT      = "/";
    public  final static String  REQUEST_PATH_COUNTER_EVENT_XLSX = '/' + EXPORTED_FILE_NAME_COUNTER;

    public  final static String  FULL_PATH_COUNTER_EVENT         = EndpointsPaths.REQUEST_PATH_COUNTER_BASE + EndpointsPaths.REQUEST_PATH_COUNTER_EVENT;


    /* --- endpoint paths for: events timer --- */
    private final static String  EVENTS_TIMER                   = "timing";
    public  final static String  EXPORTED_FILE_NAME_TIMER       = EVENTS_TIMER + "s.xlsx";
    public  final static String  REQUEST_PATH_TIMER_BASE        = '/' + EVENTS_ROOT + '/' + EVENTS_TIMER;
    public  final static String  REQUEST_PATH_TIMER_EVENT       = "/";
    public  final static String  REQUEST_PATH_TIMER_EVENT_XLSX  = '/' + EXPORTED_FILE_NAME_TIMER;

    public  final static String  FULL_PATH_TIMER_EVENT          = EndpointsPaths.REQUEST_PATH_TIMER_BASE + EndpointsPaths.REQUEST_PATH_TIMER_EVENT;


    /* --- endpoint paths for: log in/out events --- */
    private final static String  EVENTS_LOGIN                   = "log-in-out";
    public  final static String  EXPORTED_FILE_NAME_LOGIN       = "logs-in-out.xlsx";
    public  final static String  REQUEST_PATH_LOGIN_BASE        = '/' + EVENTS_ROOT + '/' + EVENTS_LOGIN;
    public  final static String  REQUEST_PATH_LOGIN_EVENT       = "/";
    public  final static String  REQUEST_PATH_LOGIN_EVENT_XLSX  = '/' + EXPORTED_FILE_NAME_LOGIN;

    public  final static String  FULL_PATH_LOGIN_EVENT          = EndpointsPaths.REQUEST_PATH_LOGIN_BASE + EndpointsPaths.REQUEST_PATH_LOGIN_EVENT;

    /* --- endpoint media types --- */
    public  final static String  APPLICATION_SPREADSHEET        = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

}
