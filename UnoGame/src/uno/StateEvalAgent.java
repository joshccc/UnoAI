/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uno;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author TTStarck
 */
public class StateEvalAgent {
    double checkPlayable(Card c, Card upCard)
    {
        double out = 0;
        
        if (c.getColor() == upCard.getColor())
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
     * Return ratio of cards not discarded over discarded
     * @param state (environment)
     * @param hand (hand)
     * @param color (color you want to get)
     * @return 
     */
    double countColor(GameState state, List<Card> hand, UnoPlayer.Color color)
    {
        int out = 0;
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
        // 25 is the number of cards of each color
        out = out / 25;
        
        return 0;
    }
    
    double countNumber(GameState state, List<Card> hand, int number)
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
        
        // the 4 is the number of colors
        if (number == 0)
        {
            out = out / Deck.NUMBER_OF_DUP_ZERO_CARDS * 4;
        }
        else
        {
            out = out / Deck.NUMBER_OF_DUP_REGULAR_CARDS * 4;
        }
        
        return out;
    }
    
    double countSpecial(GameState state, List<Card> hand, UnoPlayer.Rank rank)
    {
        double out = 0;
        
        List<Card> list = state.getPlayedCards();
        
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
        // 25 is the number of cards of each color
        out = out / Deck.NUMBER_OF_DUP_SPECIAL_CARDS;
        
        return out;
    }
    
    /* List<Double> calcColor(Hand hand, Environment env)*/
    List<Double> calcColor(List<Card> hand, GameState state)
    {
        List<Double> out = new ArrayList<Double>();
        double red   = countColor(state, hand, UnoPlayer.Color.RED);
        double blue  = countColor(state, hand, UnoPlayer.Color.BLUE);
        double green = countColor(state, hand, UnoPlayer.Color.GREEN);
        double yellow= countColor(state, hand, UnoPlayer.Color.YELLOW);
        
        double playable = 0;
        Card upCard = state.getPlayedCards().get(0);
        
        for (int i = 0; i < hand.size(); i++)
        {
            Card c = hand.get(i);
            playable = checkPlayable(c, upCard);
            
            if (c.getColor() == UnoPlayer.Color.RED)
            {
                out.add(red + playable);
            }
            else if (c.getColor() == UnoPlayer.Color.BLUE)
            {
                out.add(blue + playable);
            }
            else if (c.getColor() == UnoPlayer.Color.GREEN)
            {
                out.add(green + playable);
            }
            else if (c.getColor() == UnoPlayer.Color.YELLOW)
            {
                out.add(yellow + playable);
            }
        }
        
        
        return out;
    }
    
    // List<Double> calcNumber(Hand hand, Environment env)
    List<Double> calcNumber(List<Card> hand, GameState state)
    {
        List<Double> out = new ArrayList<Double>();
        Card upCard = state.getPlayedCards().get(0);
        for (int i = 0; i < hand.size(); i++)
        {
           Card c = hand.get(i);
           
           double num = countNumber(state, hand, c.getNumber());
           num += checkPlayable(c, upCard);
           out.add(num);
        }
        
        return out;
    }
    
    // List<Double> calcSpecial(Hand hand, Environment env)
    List<Double> calcSpecial(List<Card> hand, GameState state)
    {
        List<Double> out = new ArrayList<Double>();
        
        Card upCard = state.getPlayedCards().get(0);
        for (int i = 0; i < hand.size(); i++)
        {
           Card c = hand.get(i);
           
           double num = countSpecial(state, hand, c.getRank());
           num += checkPlayable(c, upCard);
           out.add(num);
        }
        
        return out;
    }
    
    List<Double> ratePlayableCard(List<Card> hand, GameState state)
    {
        List<Double> out = new ArrayList<>();
        
        List<Double> color = calcColor(hand, state);
        List<Double> number = calcNumber(hand, state);
        List<Double> special = calcSpecial(hand, state);
        
        for (int i = 0; i < color.size(); i++)
        {
            double total = 0;
            total += color.get(i) * 0.7;
            total += number.get(i) * 0.2;
            total += special.get(i) * 0.1;
            out.add(total);
        }
        return out;
    }
}
