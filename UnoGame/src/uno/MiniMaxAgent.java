/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uno;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author joshchoi
 */
public class MiniMaxAgent {
    
    private class Node<T> {
        protected LinkedList<Node> branches;
        protected T element;
        
        protected Node(T element) {
            this.element = element;
            this.branches = new LinkedList<Node>();
        }
    }
    
    private Node<T> rootNode;
    
    /** if node is terminal
                    return static evaluation function
             if node is min-node
                    currentBest = ∞
                    FOREACH child of node
                           currentBest = min(currentBest, minimax(child))
             if node is max-node
                    currentBest = -∞
                    FOREACH child of node
                           currentBest = max(currentBest, minimax(child))
             return currentBest
    **/
    
    public List<Double> ratePlayableCards(List<Card> hand, Environment env)
    {
        
        // create minimax tree if not created before
        if (rootNode == null) {
            
        }
        
        // prune the tree
        
        // find the best card to play
        
        return null;
    }
}
