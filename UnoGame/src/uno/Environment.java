package uno;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Environment implements Serializable
{

    private Card upCard;
    private UnoPlayer.Color calledColor;
    private GameState state;

    private int cardsInDeck;                             //Number of cards in deck
    private ArrayList<Integer> cardsInOpponentsHand;     //Number of cards in each opponents' hand
    private ArrayList<Card> cardsPlayed;

    public static final int NUM_PER_COLOR = 25;
    public static final int NUM_ZEROES = 4 * Deck.NUMBER_OF_DUP_ZERO_CARDS;
    public static final int NUM_OF_A_NUMBER = 4 * Deck.NUMBER_OF_DUP_REGULAR_CARDS;
    public static final int NUM_SPECIAL = 4 * Deck.NUMBER_OF_DUP_SPECIAL_CARDS;
    public static final int NUM_IN_DECK = 
        (4 * NUM_PER_COLOR) + 
        Deck.NUMBER_OF_WILD_CARDS + Deck.NUMBER_OF_WILD_D4_CARDS;
    
    
    public Environment(Card upCard, UnoPlayer.Color calledColor, GameState state){
        this.upCard = upCard;
        this.calledColor = calledColor;
        this.state = state;
            
        this.cardsInDeck = state.getDeckSize();
        this.cardsPlayed = state.getDiscardPile();
        this.cardsInOpponentsHand = new ArrayList();
        
        for (int num : state.getNumCardsInHandsOfUpcomingPlayers()) {
            this.cardsInOpponentsHand.add(num);
        }
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
    
    public Card getUpCard() {
        return this.upCard;
    }
    
    public UnoPlayer.Color getCalledColor() {
        return this.calledColor;
    }
    
    double checkPlayable(Card c)
    {
        double out = 0;
        
        if (c.getColor() == upCard.getColor() || c.getColor() == 
            UnoPlayer.Color.NONE)
        {
            out++;
        }
        else if (c.getNumber() == upCard.getNumber())
        {
            out++;
        }
        
        return out;
    }
    /**
     * Determines the ratio of the cards of the given color that have not been
     * played and are in the player's hand. For example, if the deck has 25
     * red cards, 4 have been played so far, and the player has 2 red cards in
     * hand, return 19/25.
     * 
     * @param color the color to count
     * @param hand the AI player's hand (these cards are not counted)
     */
    public double countColor(List<Card> hand, UnoPlayer.Color color)
    {
        double out = 0;
        List<Card> list = state.getPlayedCards();
        
        for (int i = 0; i < list.size(); i++)
        {
            Card c = list.get(i);
            
            if (c.getColor() == color)
            {
                out++;
            }
        }
        
        for (int i = 0; i < hand.size(); i++)
        {
            if (hand.get(i).getColor() == color)
            {
                out++;
            }
        }
        
        out = out / NUM_PER_COLOR;
        
        return out;
    }
    
    double countNumber(List<Card> hand, int number)
    {
        double out = 0;
        
        List<Card> list = state.getPlayedCards();
        
        for (int i = 0; i < list.size(); i++)
        {
            Card c = list.get(i);
            
            if (c.getNumber() == number)
            {
                out++;
            }
        }
        
        if (number == 0)
        {
            out = out / NUM_ZEROES;
        }
        else
        {
            out = out / NUM_OF_A_NUMBER;
        }
        
        return out;
    }
    
    double countSpecial(List<Card> hand, UnoPlayer.Rank rank)
    {
        double out = 0;
        
        List<Card> list = state.getPlayedCards();
        
        if (rank == UnoPlayer.Rank.NUMBER)
        {
            return out;
        }
        for (int i = 0; i < list.size(); i++)
        {
            Card c = list.get(i);
            
            if (c.getRank() == rank)
            {
                out++;
            }
        }
        
        for (int i = 0; i < hand.size(); i++)
        {
            if (hand.get(i).getRank() == rank)
            {
                out++;
            }
        }
        
        out = out / NUM_SPECIAL;
        
        return out;
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
        int out = afterEnv.cardsPlayed.size() - this.cardsPlayed.size();
        
        //deck was shuffled
        while(out < 0)
        {
            out += NUM_IN_DECK;
        }
        
        return out;
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
        int out = this.cardsInDeck - afterEnv.cardsInDeck;
        
        //deck was shuffled
        while(out < 0)
        {
            out += NUM_IN_DECK;
        }
        
        return out;
    }
    
    public ArrayList<Card> getDiscardedPile() {
        return state.getDiscardPile();
    }
}
