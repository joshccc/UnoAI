/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uno;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author joshchoi
 */
public class MonteCarloAgent {
    
    private class Node {
        protected LinkedList<Node> branches;
        protected Card element;
        protected int value;
        protected UnoPlayer.Color color;
        protected boolean isWild;
        protected int key;
        
        protected Node(Card element, int key) {
            this.element = element;
            this.branches = new LinkedList<Node>();
            this.value = 0;
            this.color = element.getColor();
            this.isWild = this.color == UnoPlayer.Color.NONE ? true : false;
            this.key = key;
        }
    }
    
    private Node rootNode;
    
    private int toplay;
    
    private UnoPlayer.Color tocolor;
    
    public MonteCarloAgent() {
        toplay = -1;
        tocolor = UnoPlayer.Color.NONE;
    }
    
    public List<Double> ratePlayableCards(List<Card> hand, Environment env)
    {
        rootNode = new Node(env.getUpCard(), -1);
        ArrayList<Double> returnvals = new ArrayList<Double>();
        
        for (Card card : hand) {
            returnvals.add(env.checkPlayable(card));
        }
        
        for (int counter = 0; counter < hand.size(); counter++) {
            Card card = hand.get(counter);
            Node tempNode = new Node(card, counter);
            rootNode.branches.add(tempNode);
            
            if (tempNode.isWild) {
                for (int i = 0; i < 4; i++) {
                    Node colorNode = new Node(card, counter);
                    colorNode.color = i == 0 ? UnoPlayer.Color.RED : 
                                    i == 1 ? UnoPlayer.Color.BLUE : 
                                    i == 2 ? UnoPlayer.Color.GREEN : UnoPlayer.Color.YELLOW;
                    colorNode.isWild = true;
                    tempNode.branches.add(colorNode);
                }
            }
        }
        
        ArrayList<Node> possiblemoves = new ArrayList<Node>();
        
        for (Node node : rootNode.branches) {
            if (env.checkPlayable(node.element) > 0) {
                if (node.color == UnoPlayer.Color.NONE) {
                    possiblemoves.add(node.branches.get(0));
                    possiblemoves.add(node.branches.get(1));
                    possiblemoves.add(node.branches.get(2));
                    possiblemoves.add(node.branches.get(3));
                }
                else {
                    possiblemoves.add(node);
                }
            }
        }
        
        if (possiblemoves.size() == 1) {
            return returnvals;
        }
        else if (possiblemoves.size() == 0) {
            return returnvals;
        }
        
        for (Node node : possiblemoves) {
            int sum = 0;
            
            Card upCard = node.element;
            UnoPlayer.Color color = node.color;
                
            for (int simnum = 0; simnum < 50; simnum++) {
                List<Integer> opponents = env.getcardsInOppHand();
                Deck simDeck = new Deck();
                ArrayList<ArrayList<Card>> hands = new ArrayList<ArrayList<Card>>();
                
                for (int opponent = 0; opponent < opponents.size(); opponent++) {
                    ArrayList<Card> temphand = new ArrayList<Card>();
                    
                    for (int cards = 0; cards < opponents.get(opponent); cards++) {
                        
                        Card newCard = null;
                        
                        try {
                            newCard = simDeck.draw();
                        }
                        catch (EmptyDeckException e) {
                            System.out.println("Deck should never be empty");
                        }
                        
                        while(hand.contains(newCard)) {
                            
                            try {
                                simDeck.insertCardBack(newCard);
                                newCard = simDeck.draw();
                            }
                            catch (EmptyDeckException e) {
                                System.out.println("Deck should never be empty");
                            }
                        }
                        temphand.add(newCard);
                    }
                    hands.add(temphand);
                }
                
                ArrayList<Card> playerHand = new ArrayList<Card>(hand);
                playerHand.remove(node.element);
                
                hands.add(playerHand);
                
                int winner = -1;
                int ndxturn = 0;
                int rounds = 0;
                
                while (winner == -1 && rounds < 40) {
                    ArrayList<Card> turnpossibles = new ArrayList<Card>();
                    
                    for (Card card : hands.get(ndxturn)) {
                        if (card.canPlayOn(upCard, color)) {
                            turnpossibles.add(card);
                        }
                    }
                    
                    Random generator = new Random();
                    
                    if (turnpossibles.size() == 1) {
                        upCard = turnpossibles.get(0);
                        hands.get(ndxturn).remove(turnpossibles.get(0));
                        color = upCard.getColor();
                        
                        if (color == UnoPlayer.Color.NONE) {
                            int randcol= generator.nextInt(4);
                            
                            color = randcol == 0 ? UnoPlayer.Color.RED : 
                                    randcol == 1 ? UnoPlayer.Color.BLUE : 
                                    randcol == 2 ? UnoPlayer.Color.GREEN : UnoPlayer.Color.YELLOW;
                        }
                        simDeck.discard(upCard);
                    }
                    else if (turnpossibles.size() > 1) {
                        int random = generator.nextInt(turnpossibles.size());
                        upCard = hands.get(ndxturn).get(random);
                        hands.get(ndxturn).remove(random);
                        color = upCard.getColor();
                        
                        if (color == UnoPlayer.Color.NONE) {
                            int randcol= generator.nextInt(4);
                            
                            color = randcol == 0 ? UnoPlayer.Color.RED : 
                                    randcol == 1 ? UnoPlayer.Color.BLUE : 
                                    randcol == 2 ? UnoPlayer.Color.GREEN : UnoPlayer.Color.YELLOW;
                        }
                        
                        simDeck.discard(upCard);
                    }
                    else {
                        try {
                            hands.get(ndxturn).add(simDeck.draw());
                        }
                        catch (EmptyDeckException e1) 
                        {
                            simDeck.remix();
                            
                            try {
                            hands.get(ndxturn).add(simDeck.draw());
                            }
                            catch (EmptyDeckException e2) 
                            {
                                System.out.println("Deck should never be empty");
                            }
                        }
                    }
                    
                    if (hands.get(ndxturn).isEmpty()) {
                        winner = ndxturn;
                    }
                    
                    ndxturn++;
                    ndxturn = ndxturn == 4 ? 0 : ndxturn;
                    rounds++;
                }
                
                if (winner == 3) {
                    sum++;
                }
                else if (winner == -1) {
                }
                else {
                    sum--;
                }
            }
            
            node.value = sum;
        }
        
        int highestsum = possiblemoves.get(0).value;
        tocolor = possiblemoves.get(0).color;
        toplay = possiblemoves.get(0).key;
        
        for (Node node : possiblemoves) {
            if (node.isWild) {
                for (Node colornodes : node.branches) {
                    if (highestsum > colornodes.value) {
                        highestsum = colornodes.value;
                        tocolor = colornodes.color;
                        toplay = colornodes.key;
                    }
                }
            }
            else {
                if (highestsum > node.value) {
                    highestsum = node.value;
                    tocolor = node.color;
                    toplay = node.key;
                }
            }
        }
        
        returnvals.set(toplay, (double) 1);
        
        return returnvals;
    }
    
    public UnoPlayer.Color getColor() {
        return tocolor;
    }
}
