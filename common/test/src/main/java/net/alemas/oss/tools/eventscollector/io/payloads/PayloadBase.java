package net.alemas.oss.tools.eventscollector.io.payloads;


import java.time.LocalDateTime;


public class PayloadBase
{

    /* --- properties --- */
    private final LocalDateTime     when;

    /* --- constructors --- */
    protected PayloadBase( LocalDateTime when )
    {
        this.when = when;
    }
    public static PayloadBase build( LocalDateTime when )
    {
        return
                new PayloadBase( when );
    }

    /* --- getters --- */
    public LocalDateTime getWhen()
    {
        return
                this.when;
    }

}
