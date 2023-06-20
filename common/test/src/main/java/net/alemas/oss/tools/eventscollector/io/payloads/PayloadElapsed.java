package net.alemas.oss.tools.eventscollector.io.payloads;


import java.time.LocalDateTime;


public class PayloadElapsed extends PayloadBase
{

    /* --- properties --- */
    private final long          elapsed;

    /* --- constructors --- */
    protected PayloadElapsed( LocalDateTime when, long elapsed )
    {
        super( when );
        this.elapsed = elapsed;
    }
    public static PayloadElapsed build( LocalDateTime when, long elapsed )
    {
        return
                new PayloadElapsed( when, elapsed );
    }

    /* --- getters --- */
    public long getElapsed()
    {
        return
                this.elapsed;
    }

}
