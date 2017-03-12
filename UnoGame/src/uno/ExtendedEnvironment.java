/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uno;

import java.util.ArrayList;

/**
 *
 * @author joshchoi
 */
public class ExtendedEnvironment extends Environment {
    
    private ArrayList<Card> myHand;
    
    public ExtendedEnvironment(Card upCard, UnoPlayer.Color calledColor, GameState state) {
        super(upCard, calledColor, state);
        
        self.myHand = 
    }
    
}
