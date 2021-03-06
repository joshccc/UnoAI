package uno;

import java.util.ArrayList;
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
    //the cards in hand on that turn
    private final ArrayList<Card> hand;
    //the Card selected to play
    private final Card played;
    
    /**
     * Initializes a PlayRater to the given criteria.
     * 
     * @param turnEnv the environment
     * @param hand the cards in the hand that were not played
     * @param played the card I played given that environment
     */
    public PlayRater(Environment turnEnv, ArrayList<Card> hand, Card played)
    {
        this.turnEnv = turnEnv;
        this.hand = hand;
        this.played = played;
    }
    
    /**
     * Determines the number of cards in the hand of the given color.
     * 
     * @param hand the hand
     * @param color the color to search for
     * 
     * @return number of cards of that color in the hand
     */
    private int numColorInHand(List<Card> hand, UnoPlayer.Color color)
    {
        int out = 0;
        
        for(Card card : hand)
        {
            if(card.getColor() == color)
            {
                out++;
            }
        }
        
        return out;
    }
    
    /**
     * Determines the number of cards in the hand of the given special ranking.
     */
    private int numSpecialInHand(List<Card> hand, UnoPlayer.Rank rank)
    {
        int out = 0;
        
        for(Card card : hand)
        {
            if(card.getRank() == rank)
            {
                out++;
            }
        }
        
        return out;
    }
    
    private int numNumberInHand(List<Card> hand, int num)
    {
        int out = 0;
        
        for(Card card : hand)
        {
            if(card.getNumber() == num)
            {
                out++;
            }
        }
        
        return out;
    }
    
    /**
     * Scales the values in the specified array to one max, if needed.
     * 
     * @param origWeights the array to scale down if needed
     * 
     * @return scaled array
     */
    public double[] pareWeightsToOneMax(double[] origWeights)
    {
        double max = 1;
        double[] out;
        
        for(int i = 0; i < origWeights.length; i++)
        {
            if(origWeights[i] > max)
            {
                max = origWeights[i];
            }
        }
        
        if(max > 1)
        {
            out = new double[origWeights.length];
            
            for(int i = 0; i < origWeights.length; i++)
            {
                out[i] = origWeights[i] / max;
            }
        }
        else
        {
            out = origWeights;
        }
        
        return out;
    }
    
    /**
     * Gets the average of the passed in array.
     * 
     * @param weights array to take average of
     * 
     * @return average of this array
     */
    public double averageOf(double[] weights, double penalty)
    {
        double sum = 0;
        int numPenalizations = 0;
        int divideBy = weights.length;
        
        for(int i = 0; i < weights.length; i++)
        {
            if(weights[i] < 0)
            {
                numPenalizations++;
            }
            else if(weights[i] == 0)
            {
                divideBy--;
            }
            else
            {
                sum += weights[i];
            }
        }
        
        //I have no colored cards, so my color odds are the best they can be
        if(divideBy == 0)
        {
            sum = 1;
        }
        else
        {
            sum = sum / divideBy;
        }
        
        //for all colors I hold that are really bad, bring the entire color
        //score down
        if(numPenalizations > 0)
        {
            sum = sum / (numPenalizations * penalty);
        }
        
        return sum;
    }
    
    /**
     * Since countColor counts the number of cards that have been played of a
     * color, and includes cards in hand of a color, but I want cards in the
     * deck/in hands of a color, this method does so.
     * 
     * 
     * 
     * @return ratio of cards still in the deck or opponent's hands
     */
    private double getColorsUnplayed
        (Environment env, List<Card> hand, UnoPlayer.Color color)
    {
        return ((env.countColor(hand, color) * Environment.NUM_PER_COLOR) 
            - numColorInHand(hand, color)) / Environment.NUM_PER_COLOR;
    }
        
    /**
     * Similar to CTCSID (above), converts the measure of ranks left in the deck
     * or in opponent's hands.
     */
    private double getSpecialsUnplayed
        (Environment env, List<Card> hand, UnoPlayer.Rank rank)
    {
        return ((env.countSpecial(hand, rank) * Environment.NUM_SPECIAL) - 
            numSpecialInHand(hand, rank)) / Environment.NUM_SPECIAL;
    }
        
    private double getNumbersUnplayed
        (Environment env, List<Card> hand, int num)
    {
        int numOfThatNum = 0;
        if(num == 0)
        {
            numOfThatNum = Environment.NUM_ZEROES;
        }
        else
        {
            numOfThatNum = Environment.NUM_OF_A_NUMBER;
        }
        
        return((env.countNumber(hand, num) * numOfThatNum) -
            numNumberInHand(hand, num)) / numOfThatNum;
    }
    
    /**
     * Helper for ratePlay.
     * Determines the change in color counts across the change in turns.
     * Then, considering the hand the player now possesses, determines
     * the playability strictly based on color probabilities.
     * 
     * Or, short version, determines how good the colors in the hand the player
     * has now are.
     * 
     * @param currHand the player's current hand
     * @param turnAfter the current turn
     * @return a value between 0 and 1 of how good the colors in the hand are,
     *         0 being bad and 1 being good
     */
    private double measureColors
        (List<Card> currHand, Environment turnAfter)
    {
        int len = UnoPlayer.Color.values().length;
        
        double[] colorCounts = new double[len];
        UnoPlayer.Color[] colors = new UnoPlayer.Color[len];
        int idx = 0;
        
        //determine the color probabilities in the new environment
        for(UnoPlayer.Color color : UnoPlayer.Color.values())
        {
            //don't worry about wilds, we can play those whenever
            if(color != UnoPlayer.Color.NONE)
            {
                colorCounts[idx] = getColorsUnplayed
                    (turnAfter, currHand, color);
                colors[idx] = color;
                idx++;
            }
        }
        
        //less common colors in the given environment are not as good to possess
        for(idx = 0; idx < len; idx++)
        {
            int numInHand = numColorInHand(currHand, colors[idx]);
            
            //don't penalize self for not having cards
            //penalize self for having the last card(s) of a given color
            if(colorCounts[idx] == 0 & numInHand > 0)
            {
                colorCounts[idx] = -1;
            }
            else
            {
                colorCounts[idx] *= numInHand;
            }
        }
        
        colorCounts = pareWeightsToOneMax(colorCounts);
        return averageOf(colorCounts, 4);
    }
        
    /**
     * Determines if the AIPlayer was forced to draw cards as opposed to taking
     * a turn. Normally, we want a high drawing of cards to be reflected as a
     * good play, however if it makes us draw cards we don't want that.
     * 
     * @param cardsDrawn current cards drawn value
     * @param currHandSize the current hand's size
     * 
     * @return new cards drawn value, after taking into account being forced to
     *         draw
     */
    private double factorInDrawTwoed(double cardsDrawn, int currHandSize)
    {
        int handChange = hand.size() - currHandSize;
        
        //we drew cards...penalize ourselves for allowing this to happen
        if(handChange < 0)
        {
            cardsDrawn -= (3 * handChange);
        }
        
        //convert negative weights to a small positive weight
        if(cardsDrawn < 0)
        {
            cardsDrawn = -1.0 / cardsDrawn;
        }
        
        return cardsDrawn;
    }
    
    /**
     * Similar to measureColors, determines how good the ranks of the cards that
     * the player has in hand are.
     * 
     * @param currHand the player's current hand
     * @param turnAfter the current turn
     * 
     * @return a value between 0 and 1 of how good the ranks in a player's hand
     * are, 0 being bad and 1 being good
     */
    private double measureSpecials(List<Card> currHand, Environment turnAfter)
    {
        int len = UnoPlayer.Rank.values().length;
        
        double[] specCounts = new double[len];
        UnoPlayer.Rank[] specials = new UnoPlayer.Rank[len];
        int idx = 0;
        
        //determine the color probabilities in the new environment
        for(UnoPlayer.Rank rank : UnoPlayer.Rank.values())
        {
            //don't worry about wilds, we can play those whenever
            //also don't worry about numbers, we do that elsewhere
            if(rank != UnoPlayer.Rank.WILD && rank != UnoPlayer.Rank.WILD_D4
                && rank != UnoPlayer.Rank.NUMBER)
            {
                specCounts[idx] = getSpecialsUnplayed
                    (turnAfter, currHand, rank);
                specials[idx] = rank;
                idx++;
            }
        }
        
        //less common colors in the given environment are not as good to possess
        for(idx = 0; idx < len; idx++)
        {
            int numInHand = numSpecialInHand(currHand, specials[idx]);
            
            //don't penalize self for not having cards
            //penalize self for having the last card(s) of a given color
            if(specCounts[idx] == 0 & numInHand > 0)
            {
                specCounts[idx] = -1;
            }
            else
            {
                specCounts[idx] *= numInHand;
            }
        }
            
        specCounts = pareWeightsToOneMax(specCounts);
        //1.1 because it's easy to play the last reverse of a color, unless the
        //color is bad too, which is reflected elsewhere
        return averageOf(specCounts, 1.1);
    }
    
    /**
     * Similar to measureColors and measureSpecials, Determines on a scale from
     * 0 to 1 how good the numbers in the player's hand are.
     */
    private double measureNumbers(List<Card> currHand, Environment turnAfter)
    {
        int len = Card.MAX_NUMBER;
        
        int[] nums = new int[len + 1];
        double[] numCounts = new double[len + 1];
        
        for(int idx = 0; idx <= len; idx++)
        {
            nums[idx] = idx;
            numCounts[idx] = getNumbersUnplayed(turnAfter, currHand, nums[idx]);
        }
        
        //less common colors in the given environment are not as good to possess
        for(int idx = 0; idx < len; idx++)
        {
            int numInHand = numNumberInHand(currHand, nums[idx]);
            
            //don't penalize self for not having cards
            //penalize self for having the last card(s) of a given color
            if(numCounts[idx] == 0 & numInHand > 0)
            {
                numCounts[idx] = -1;
            }
            else
            {
                numCounts[idx] *= numInHand;
            }
        }
            
        numCounts = pareWeightsToOneMax(numCounts);
        //1.1 because it's easy to play the last 7 of a color, unless the
        //color is bad too, which is reflected elsewhere
        return averageOf(numCounts, 1.1);
    }
    
    /**
     * Determines if the card that was selected given the environment and the
     * player's hand, with respect to the environment & hand on the next turn,
     * was a good play. A play's "goodness" is measured using many metrics.
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
        //System.out.println("Cards currently in hand: " + currHand.size());
        //determine the number of cards played across the turns
        double cardsPlayed = this.turnEnv.numCardsPlayed(turnAfter);
        
        //ensure cardsPlayed isn't 0, for divisibility reasons
        //any case where no cards are played is great - cards drawn holds more
        //info to us, so just make cardsPlayed nonexistent
        cardsPlayed = cardsPlayed == 0 ? 1 : cardsPlayed;
        
        //System.out.println("Cards Played: " + cardsPlayed);
        
        //the cards drawn value is worsened in cases where I had to draw -
        //implies I didn't get a turn (I was draw-2ed or something)
        double cardsDrawn = 
            factorInDrawTwoed(turnEnv.numCardsDrawn(turnAfter), currHand.size());
        
        //System.out.println("Cards drawn metric: " + cardsDrawn);
        
        //compute a metric for the cards that were played and drawn
        double cardsGoodness = cardsDrawn / cardsPlayed;
        
       // System.out.println("Card goodness metric: " + cardsGoodness);
        
        double colors = measureColors(currHand, turnAfter);
        
        //System.out.println("Colors metric: " + colors);
        
        double specials = measureSpecials(currHand, turnAfter);
        
        //System.out.println("Specials metric: " + specials);
        
        double numbers = measureNumbers(currHand, turnAfter);
        
        //System.out.println("Numbers metric: " + numbers);
        
        //determine how good the player's hand is as a whole
        double handGoodness = (colors * numbers * specials) / 3;
        
        //System.out.println("Hand goodness metric: " + handGoodness);
        
        //times 5 acts as a small boost to extremely low scores
        //my metrics tended to generate mostly scores from 0.0001 to around 0.15
        //so the * 5 maps these to approximately 0 to 1
        double overallWeight = (cardsGoodness * handGoodness) * 5;
        
        if(cardsGoodness == 0 && handGoodness > 0)
        {
            overallWeight = handGoodness / 1.3;
        }
        else if(handGoodness == 0 && cardsGoodness > 0)
        {
            overallWeight = cardsGoodness / 2.0;
        }
        
        if(overallWeight > 1)
        {
            overallWeight = 1;
        }
        
        //System.out.println("Overall weight [PlayRater]: " + overallWeight);
    
        //System.out.println("\n\n");
        return overallWeight;
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
    public ArrayList<Card> getHand()
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
        int total = 0;
        
        for (Card card : hand)
        {
            total += card.forfeitCost();
        }
        return total;
    }
}