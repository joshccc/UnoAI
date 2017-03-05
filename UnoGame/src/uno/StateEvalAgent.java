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
    
    /* List<Double> calcColor(Hand hand, Environment env)*/
    List<Double> calcColor(List<Card> hand, Environment env)
    {
        List<Double> out = new ArrayList<>();
        double red   = env.countColor(hand, UnoPlayer.Color.RED);
        double blue  = env.countColor(hand, UnoPlayer.Color.BLUE);
        double green = env.countColor(hand, UnoPlayer.Color.GREEN);
        double yellow= env.countColor(hand, UnoPlayer.Color.YELLOW);
        
        double playable = 0;
        
        for (int i = 0; i < hand.size(); i++)
        {
            Card c = hand.get(i);
            playable = env.checkPlayable(c);
            
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
    List<Double> calcNumber(List<Card> hand, Environment env)
    {
        List<Double> out = new ArrayList<>();
        
        for (int i = 0; i < hand.size(); i++)
        {
           Card c = hand.get(i);
           
           double num = env.countNumber(hand, c.getNumber());
           num += env.checkPlayable(c);
           out.add(num);
        }
        
        return out;
    }
    
    // List<Double> calcSpecial(Hand hand, Environment env)
    List<Double> calcSpecial(List<Card> hand, Environment env)
    {
        List<Double> out = new ArrayList<>();
        
        for (int i = 0; i < hand.size(); i++)
        {
           Card c = hand.get(i);
           
           double num = env.countSpecial(hand, c.getRank());
           num += env.checkPlayable(c);
           out.add(num);
        }
        
        return out;
    }
    
    List<Double> ratePlayableCards(List<Card> hand, Environment env)
    {
        List<Double> out = new ArrayList<>();
        
        List<Double> color = calcColor(hand, env);
        List<Double> number = calcNumber(hand, env);
        List<Double> special = calcSpecial(hand, env);
        
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

