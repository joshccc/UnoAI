package uno;

import java.util.ArrayList;
import java.util.List;

public class Environment
{

    private Card upCard;
    private UnoPlayer.Color calledColor;
    private GameState state;

    private static int cardsInDeck;                             //Number of cards in deck
    private static ArrayList<Integer> cardsInOpponentsHand;     //Number of cards in each opponents' hand
    private static ArrayList<Card> cardsPlayed;

    public Environment(Card upCard, UnoPlayer.Color calledColor, GameState state){
            this.upCard = upCard;
            this.calledColor = calledColor;
            this.state = state;

            cardsInDeck--;
            cardsPlayed.add(upCard);
    }


    public int getCardsInDeck(){
            return this.cardsInDeck;
    }

    public ArrayList<Integer> getcardsInOppHand(){
            return this.cardsInOpponentsHand;
    }

    public ArrayList<Card> getCardsPlayed(){            //Returns list of cards already played.
            return this.cardsPlayed;
    }

    /**
     * Determines the ratio of the cards of the given color that have not been
     * played and are not in the player's hand. For example, if the deck has 25
     * red cards, 4 have been played so far, and the player has 2 red cards in
     * hand, return 19/25.
     * 
     * @param color the color to count
     * @param hand the AI player's hand (these cards are not counted)
     */
    public double countColor(List<Card> hand, UnoPlayer.Color color)
    {
        //todo
        return 0.0;
    }
    
    /**
     * Determines the number of cards played to get to the input environment.
     * That is, if this environment has 2 cards in the discard pile and the
     * passed environment has 5 cards in the discard pile, then return 3.
     * 
     * @param afterEnv the subsequent Environment to compare to this one
     * 
     * @return number of cards played to get to that environment
     */
    public int numCardsPlayed(Environment afterEnv)
    {
        //TODO
        return 0;
    }
    
    /**
     * Determines the number of cards drawn to get to the input environment.
     * For example if this environment has 25 cards in the deck and the passed
     * environment has 22 cards, then return 3.
     * 
     * @param afterEnv the subsequent Environment to compare with this one
     * 
     * @return number of cards drawn to get to that environment
     */
    public int numCardsDrawn(Environment afterEnv)
    {
        //TODO
        return 0;
    }
}
