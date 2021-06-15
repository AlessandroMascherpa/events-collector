package net.alemas.oss.tools.eventscollector.io.linking;



/**
 * Binds the system where the event occurred and the event;
 *
 * Created by MASCHERPA on 07/06/2021.
 */
public class PairApplicationIdUsernameId
{
    /* --- properties --- */
    /**
     * the application identifier where the event occurred;
     */
    private final String application;
    /**
     * the event identifier;
     */
    private final String id;

    /* --- constructors --- */
    public static PairApplicationIdUsernameId build( String system, String id )
    {
        return
                new PairApplicationIdUsernameId( system, id )
        ;
    }
    private PairApplicationIdUsernameId( String system, String id )
    {
        this.application    = system;
        this.id             = id;
    }

    /* --- getters --- */
    /**
     * gets the application identifier;
     *
     * @return the application identifier;
     */
    public String getApplication()
    {
        return this.application;
    }

    /**
     * gets the event identifier;
     *
     * @return the event identifier;
     */
    public String getId()
    {
        return this.id;
    }

    /* --- object checking --- */
    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( ( obj == null ) || ( this.getClass() != obj.getClass() ) )
        {
            return false;
        }

        PairApplicationIdUsernameId that = (PairApplicationIdUsernameId) obj;
        return
                ( ( ( this.application != null ) && ( this.application.equals( that.application ) ) ) || ( ( this.application == null ) && ( that.application == null ) ) )
                &&
                ( ( ( this.id != null ) && ( this.id.equals( that.id ) ) ) || ( ( this.id == null ) && ( that.id == null ) ) )
                ;
    }

    @Override
    public int hashCode()
    {
        int result = ( this.application != null ) ? this.application.hashCode() : 0;
        result = 31 * result + ( ( this.id != null ) ? this.id.hashCode() : 0 );
        return
                result;
    }

}
