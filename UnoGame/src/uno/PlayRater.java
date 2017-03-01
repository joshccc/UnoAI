package uno;

import java.awt.Color;
import java.util.List;

/**
 * PlayRater determines how good of a play a certain card was.
 * This is determined by measuring the differences in the game Environment
 * as a Card is played (ie, during my turn), and the game
 * Environment as it becomes my turn again (ie, on my next turn).
 * 
 * @author Alec
 */
public class PlayRater
{
    //the environment on the turn
    private final Environment turnEnv;
    //the cards in hand on that turn, except for the played one
    private final List<Card> hand;
    //the Card selected to play
    private final Card played;
    
    /**
     * Initializes a PlayRater to the given criteria.
     * 
     * @param turnEnv the environment
     * @param hand the cards in the hand that were not played
     * @param played the card I played given that environment
     */
    public PlayRater(Environment turnEnv, List<Card> hand, Card played)
    {
        this.turnEnv = turnEnv;
        this.hand = hand;
        this.played = played;
    }
    
    /**
     * Helper for ratePlay.
     * Determines the change in color counts across the change in turns.
     * Then, considering the hand the player now possesses, determines
     * the playability strictly based on color probabilities.
     * Each color's playability is then [SOMETHING], which is what is returned.
     * 
     * Or, short version, determines how good the colors in the hand the player
     * has now are.
     * 
     * @param currHand the player's current hand
     * @param turn the current turn
     * @return a value between 0 and 1 of how good the colors in the hand are,
     *         0 being bad and 1 being good
     */
    private double measureColors(
        List<Card> currHand, Environment turnAfter)
    {
        int len = UnoPlayer.Color.values().length;
        
        double[] colorCounts = new double[len];
        UnoPlayer.Color[] colors = new UnoPlayer.Color[len];
        
        //determine the color probabilities in the new environment
        for(int i = 0; i < len; i++)
        {
            colorCounts[i] = turnAfter.countColor(currHand, colors[i]);
        }
        
        //convert each color percentage to be with regards to my hand
        
        //this is the part where I mathematically combine stuff together
        //this math is not figured out yet
        //this return is just a placeholder
        return 0.0;
    }
    
    /**
     * Determines if the card that was selected given the environment and the
     * player's hand, with respect to the environment on the next turn, was
     * a good play. A play's "goodness" is measured using many metrics.
     * 
     * This should be called as follows: initialize a PR on a turn with the
     * turn's data, then call this on the next turn with that Environment and
     * the hand the player has at that time.
     * 
     * @param currHand the hand on the turn after the turn the PR was
     *                 initialized to
     * @param turnAfter the environment on the turn after the turn the PR
     *                  was initialized to
     * @return weight between 0 and 1 of how good the play was, 0 being bad and
     *         1 being good
     */
    public double ratePlay(List<Card> currHand, Environment turnAfter)
    {
        //determine the number of cards played across the turns
        int cardsPlayed = this.turnEnv.numCardsPlayed(turnAfter);
        
        //determine the number of cards drawn across the turns
        
        //note: likely need to take into account hand size
        //note: can likely use played & drawn to determine if I was skipped or
        //reversed or something, ie didn't get my turn for awhile
        //this should be reflected as bad
        int cardsDrawn = this.turnEnv.numCardsDrawn(turnAfter);
        
        //determine how good the colors in the player's hand are
        double colorOdds = measureColors(currHand, turnAfter);
        
        //determine the current point value of the player's hand
        int pointValue = getPointValue(currHand);
        
        //may be other metrics to determine as well, but this is a start
        
        //other possible metrics:
        
        // individual number probabilities(eg if I have the last 1 in my hand, 
        //it'll be more difficult to play [Can likely just piggyback off of
        //functionality Tarantino needs in his Agent
        
        //now mathematically combine these metrics together
        //want to reflect the following as good:
        //  low cardsPlayed
        //  high cardsDrawn
        //  high colorOdds
        //  low pointValue
        return 0;
    }
    
    /**
     * Accessor for the previous environment.
     * 
     * @return previous environment
     */
    public Environment getPrevEnvironment()
    {
        return this.turnEnv;
    }
    
    /**
     * Accessor for the hand.
     * 
     * @return hand
     */
    public List<Card> getHand()
    {
        return this.hand;
    }
    
    /**
     * Accessor for the card that was selected to be played.
     * 
     * @return card that was played
     */
    public Card getPlayedCard()
    {
        return this.played;
    }
    
    /**
     * Determines the point value of the passed hand.
     * 
     * @param hand the hand
     * 
     * @return the point value of the hand
     */
    private int getPointValue(List<Card> hand)
    {
        //new Hand
        //add all cards in hand to the hand class
        //return hand class.countCards
        return -1;
    }
}
