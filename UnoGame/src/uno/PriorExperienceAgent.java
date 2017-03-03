package uno;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * PriorExperienceAgent maintains a map of previously known game states,
 * and uses these prior states to determine the best card to play given the
 * current state.
 * 
 * @author Alec
 */
public class PriorExperienceAgent 
{
    /**
     * Inner class to store what happened on a previous turn,
     * e.g. what card was played given the Cards that were in the hand.
     */
    private class PriorTurn
    {
        final Environment turnEnv;
        final List<Card> hand;
        final Card played;
        //this one isn't a part of the turn.
        //it's used to track how many times this turn comes up as similar
        //to other turns.
        int numTimesSimilar;
        
        /**
         * Sets all passed values to the instances.
         * 
         * @param turnEnv the environment
         * @param hand the hand
         * @param played the card that was played
         */
        private PriorTurn(Environment turnEnv, List<Card> hand, Card played)
        {
            this.turnEnv = turnEnv;
            this.hand = hand;
            this.played = played;
            this.numTimesSimilar = 0;
        }
    }
    
    //knowledge file that this PEA uses.
    //the file is used to store knowledge across executions.
    private final String knowledgeFile;
    
    //the knowledge map. All prior turn plays and their goodness of play
    //scores as determined by PlayRater.
    private HashMap<PriorTurn, Double> playKnowledge;
    
    //the maximum number of mappings to hold at a time. When this number of
    //turn mappings is reached all entries with the lowest number of
    //similarities are removed from the table, to make room for new entries.
    private int recycleNum;
    
    /**
     * Constructor. Takes in the name of the knowledge file. This file
     * contains all determined knowledge from previous Uno games.
     * if the file does not exist, a new one is created with that name. If
     * it does exist, it is expected to contain the prior knowledge map.
     * 
     * @param knowledgeFile the name of the knowledge file
     * @param recycleNum number of table entries to hold before recycling
     */
    public PriorExperienceAgent(String knowledgeFile, int recycleNum)
    {
        this.knowledgeFile = knowledgeFile;
        this.recycleNum = recycleNum;
        buildKnowledgeTable();
    }
    
    /**
     * Helper for constructor.
     * Builds the knowledge file if it exists. If it doesn't, creates the file.
     */
    private void buildKnowledgeTable()
    {
        //make a file object on knowledgeFile
        //if the file exists
            //deserialize the info in it
            //set this to the knowledge table
        //else make the file
    }
    
    /**
     * Writes the knowledge table to the knowledge file.
     */
    private void writeKnowledgeFile()
    {
        //destroy the knowledge file
        //make a new knowledge file
        //serialize the knowledge table
        //write it to the file
    }
    
    /**
     * Determines the playability of each card in the passed hand. Playability
     * is determined on a scale from 0 to 1.
     * 
     * @param currEnv the current game environment
     * @param hand the current hand
     * 
     * @return list containing the weights of all cards in the hand, indices
     *         between the hand and the weights list match
     */
    public List<Double> ratePlayableCards(Environment currEnv, List<Card> hand)
    {
        List<Double> weights = new ArrayList<Double>();
        //assume no card will be playable
        for (int i = 0; i < hand.size(); i++)
        {
            weights.set(i, 0.0);
        }
        
        //get all currently playable cards
        List<Card> playableCards = getPlayableCards(currEnv, hand);
        //get all PriorTurns that are similar to the current turn's setup,
        //in terms of environment and hand
        List<PriorTurn> similarTurns = getSimilarTurns(currEnv, hand);
        
        //look atr specific criteria for each card
        for(Card card : playableCards)
        {
            //get all similar environments where the card was played in that
            //environment
            List<PriorTurn> almostExactPlays = 
                getAlmostExactPlays(similarTurns, playableCards);
            //get all similar environments where the card was in the hand of
            //that environment, but it was not played
            List<PriorTurn> wasPlayableSituations = 
                getWasPlayableSituations(similarTurns, playableCards);
            //use this to determine the weight of that card
            double weight = calculateWeight(almostExactPlays,
                wasPlayableSituations, card);
            weights.set(hand.indexOf(card), weight);
        }
        
        //TODO
        return null;
    }
    
    /**
     * Gets all cards in the hand that are currently playable.
     * 
     * @param env the environment
     * @param hand the hand
     * 
     * @return all cards in the hand that are currently playable
     */
    private List<Card> getPlayableCards(Environment env, List<Card> hand)
    {
        //TODO
        return null;
    }
    
    private List<PriorTurn> getSimilarTurns(Environment env, List<Card> hand)
    {
        //TODO
        return null;
    }
    
    private List<PriorTurn> getAlmostExactPlays(List<PriorTurn> similarTurns,
        List<Card> playableCards)
    {
        //TODO
        return null;
    }
    
    private List<PriorTurn> getWasPlayableSituations
        (List<PriorTurn> similarTurns, List<Card> playableCards)
    {
        //TODO
        return null;
    }
        
    private double calculateWeight(List<PriorTurn> almostExactPlays,
        List<PriorTurn> wasPlayableSituations, Card card)
    {
        //TODO
        return 0.0;
    }
}
