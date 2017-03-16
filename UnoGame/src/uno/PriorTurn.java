package uno;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Inner class to store what happened on a previous turn, e.g. what card was
 * played given the Cards that were in the hand.
 */
public class PriorTurn implements Serializable
{
    public final Environment turnEnv;
    public final ArrayList<Card> hand;
    public final Card played;
        //this one isn't a part of the turn.
    //it's used to track how many times this turn comes up as similar
    //to other turns.
    private long numTimesSimilar;

    /**
     * Sets all passed values to the instances.
     *
     * @param turnEnv the environment
     * @param hand the hand
     * @param played the card that was played
     */
    public PriorTurn(Environment turnEnv, ArrayList<Card> hand, Card played) 
    {
        this.turnEnv = turnEnv;
        this.hand = hand;
        this.played = played;
        this.numTimesSimilar = 0;
    }
    
    public long getNumTimesSimilar()
    {
        return numTimesSimilar;
    }
    
    public void addToSimilarTimes(int amt)
    {
        numTimesSimilar += amt;
    }
}
