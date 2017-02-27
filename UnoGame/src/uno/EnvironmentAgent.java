package uno;

import java.util.ArrayList;

public class EnvironmentAgent
{

    private Card upCard;
    private UnoPlayer.Color calledColor;
    private GameState state;

    private static int cardsInDeck;                             //Number of cards in deck
    private static ArrayList<Integer> cardsInOpponentsHand;     //Number of cards in each opponents' hand
    private static ArrayList<Card> cardsPlayed;

    public EnvironmentAgent(Card upCard, UnoPlayer.Color calledColor, GameState state){
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

}
