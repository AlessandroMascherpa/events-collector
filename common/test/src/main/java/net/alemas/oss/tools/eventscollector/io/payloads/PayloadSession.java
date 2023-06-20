package net.alemas.oss.tools.eventscollector.io.payloads;


import java.time.LocalDateTime;


public class PayloadSession extends PayloadBase
{

    /* --- properties --- */
    private final String    session;

    /* --- constructors --- */
    protected PayloadSession( LocalDateTime when, String session )
    {
        super( when );
        this.session = session;
    }
    public static PayloadSession build( LocalDateTime when, String session )
    {
        return
                new PayloadSession( when, session );
    }

    /* --- getters --- */
    public String getSession()
    {
        return
                this.session;
    }

}
