package net.alemas.oss.tools.eventscollector.io;


/**
 * To check whether an event is well-formed.
 */
public interface WellFormed
{

    /**
     * Check the event correctness;
     *
     * @throws NotWellFormed if the event is not well-formed;
     */
    void isWellFormed() throws NotWellFormed;

}
