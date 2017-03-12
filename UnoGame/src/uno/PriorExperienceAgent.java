package uno;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
    private class PriorTurn implements Serializable
    {
        final Environment turnEnv;
        final List<Card> hand;
        final Card played;
        //this one isn't a part of the turn.
        //it's used to track how many times this turn comes up as similar
        //to other turns.
        long numTimesSimilar;
        
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
    //turn mappings is reached some entries are deleted to make room for entries
    //that will potentially serve more purpose. The number of entries that are
    //removed scales logarithmically with the number of entries before
    //recycling triggers.
    private final int recycleNum;
    
    //Playrating struture.
    private PlayRater rater;
    
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
        this.rater = null;
        buildKnowledgeTable();
    }
    
    /**
     * Helper for constructor.
     * Builds the knowledge file if it exists. If it doesn't, creates the file.
     */
    private void buildKnowledgeTable()
    {
        try
        {
            File file = new File(this.knowledgeFile);
            if(file.exists())
            {
                FileInputStream knowledge = 
                    new FileInputStream(this.knowledgeFile);
                ObjectInputStream inStream = new ObjectInputStream(knowledge);
                this.playKnowledge = 
                    (HashMap<PriorTurn, Double>) inStream.readObject();
                inStream.close();
                knowledge.close();
            }
            else
            {
                file.createNewFile();
            }
        }
        catch(IOException | ClassNotFoundException ex)
        {
            System.out.println("Problem with building knowledge table");
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Writes the knowledge table to the knowledge file.
     * 
     * Note to self: may make this run on a Thread to speed things up
     */
    private void writeKnowledgeFile()
    {
        try
        {
            File file = new File(this.knowledgeFile);
            file.delete();
            file.createNewFile();
        
            FileOutputStream knowledge = 
                new FileOutputStream(this.knowledgeFile);
            ObjectOutputStream outStream = new ObjectOutputStream(knowledge);
            outStream.writeObject(this.playKnowledge);
            outStream.close();
            knowledge.close();
        }
        catch(IOException ex)
        {
            System.out.println("Problem with writing knowledge table");
            ex.printStackTrace();
            System.exit(1);
        }
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
        
        //assume playable cards are terrible, and unplayable cards are
        //unplayable. Weights determined later.
        for (int i = 0; i < hand.size(); i++)
        {
            if(currEnv.checkPlayable(hand.get(i)) > 0)
            {
                weights.set(i, 0.0);
            }
            else
            {
                weights.set(i, -1.0);
            }
        }
        
        //get all currently playable cards
        List<Card> playableCards = getPlayableCards(currEnv, hand);
        
        //look at specific criteria for each card
        for(Card card : playableCards)
        {
            //get all similar environments where the card was played in that
            //environment
            List<PriorTurn> almostExact = getAlmostExactTurns(card);
            //get all similar environments where the card was in the hand of
            //that environment, would have been playable, but it was not played
            List<PriorTurn> wasPlayable = getWasPlayableTurns(card);
            //get all similar environments where the card is not played, is not
            //in the hand, but would have been playable if it was
            List<PriorTurn> couldHavePlayed = getCouldHavePlayedTurns(card);
            
            /*************************todo similar hands?****************/
            
            updateUses(almostExact, wasPlayable, couldHavePlayed);
            
            //use this to determine the weight of that card
            double weight = calculateWeight
                (almostExact, wasPlayable, couldHavePlayed);
            
            weights.set(hand.indexOf(card), weight);
        }
        
        return weights;
    }
    
    /**
     * Updates the usage on the passed PriorTurns.
     * 3 different lists are generated with varying levels of similarity. The
     * exact match list is a better choice as compared to the was playable list, 
     * which is a better choice than the hypothetically playable list.
     * As such, their usabilities are considered differently.
     */
    private void updateUses(List<PriorTurn> almostExact, 
        List<PriorTurn> wasPlayable, List<PriorTurn> couldHavePlayed)
    {
        for(PriorTurn turn : almostExact)
        {
            turn.numTimesSimilar += 3;
        }
        for(PriorTurn turn : wasPlayable)
        {
            turn.numTimesSimilar += 2;
        }
        for(PriorTurn turn : couldHavePlayed)
        {
            turn.numTimesSimilar += 1;
        }
        
        writeKnowledgeFile();
    }
    
    /**
     * Returns a sublist of the hand containing all the playable cards.
     * 
     * @param env the current environment
     * @param hand the hand
     * @return all playable cards in the hand
     */
    private List<Card> getPlayableCards(Environment env, List<Card> hand)
    {
        List<Card> out = new ArrayList<Card>();
        
        for(Card card : hand)
        {
            if(env.checkPlayable(card) > 0.0)
            {
                out.add(card);
            }
        }
        
        return out;
    }
    
    /**
     * Gets all similar turns where the card being considered was in fact
     * played in that environment.
     * 
     * @param card the card being considered
     * @return all similar turns described above
     */
    private List<PriorTurn> getAlmostExactTurns( Card card)
    {
        List<PriorTurn> almost = new ArrayList<PriorTurn>();
        
        for(PriorTurn turn : playKnowledge.keySet())
        {
            if(turn.played.equals(card))
            {
                almost.add(turn);
            }
        }
        
        return almost;
    }
        
    /**
     * Gets all similar turns where the card being considered was playable in
     * that environment and in the hand of that environment, but was not
     * chosen.
     * 
     * @param card the card being considered
     * @return all similar turns described above
     */
    private List<PriorTurn> getWasPlayableTurns(Card card)
    {
        List<PriorTurn> wasPlayable = new ArrayList<PriorTurn>();
        
        for(PriorTurn turn : playKnowledge.keySet())
        {
            if(!turn.played.equals(card) && turn.hand.contains(card) &&
                turn.turnEnv.checkPlayable(card) > 0.0)
            {
                wasPlayable.add(turn);
            }
        }
        
        return wasPlayable;
    }
    
    /**
     * Gets all similar turns where if the card being considered was in hand on
     * that turn, then it could have been played.
     * 
     * @param similarTurns list of all similar turns
     * @param card the card being considered
     * @return  list of similar turns described above
     */
    private List<PriorTurn> getCouldHavePlayedTurns(Card card)
    {
        List<PriorTurn> ifOnly = new ArrayList<PriorTurn>();
        
        for(PriorTurn turn : playKnowledge.keySet())
        {
            if(!turn.played.equals(card) && !turn.hand.contains(card) &&
                turn.turnEnv.checkPlayable(card) > 0.0)
            {
                ifOnly.add(turn);
            }
        }
        
        return ifOnly;
    }
        
    /**
     * Averages the weights of all PriorTurns in the given list.
     * 
     * @param list the list
     * @return average of all turns in the list
     */
    private double averageOf(List<PriorTurn> list)
    {
        double out = 0.0;
        
        if(!list.isEmpty())
        {
            for(PriorTurn turn : list)
            {
                out += playKnowledge.get(turn);
            }
            
            out /= list.size();
        }
        
        return out;
    }
        
    /**
     * Given the three generated similarilty lists, returns an overall weight
     * for the playability of the card in question.
     * 
     * This is done by averaging the weights of all prior experiences together,
     * and then taking a weighted average of the three averages.
     * almost exact is weighted 50%, was playable is weighted 33.33%, and
     * could have played is weighted 16.67%. This reflects a priority of 3, 2,
     * and 1 respectively.
     * 
     * In the case that any list is empty (highly possible for the first
     * several runs), the weighted average is determined differently
     * 
     * @param almostExact almost exact environments
     * @param wasPlayable was playable environments
     * @param couldHavePlayed hypothetically playable environments
     * @param card
     * @return 
     */
    private double calculateWeight(List<PriorTurn> almostExact,
        List<PriorTurn> wasPlayable, List<PriorTurn> couldHavePlayed)
    {
        double weight = 0;
        
        double almostExactAvg = averageOf(almostExact);
        double wasPlayableAvg = averageOf(wasPlayable);
        double couldHavePlayedAvg = averageOf(couldHavePlayed);
        
        if(almostExactAvg == 0 && wasPlayableAvg == 0 && couldHavePlayedAvg == 0)
        {
            weight = 0;
        }
        else if(almostExactAvg == 0 && wasPlayableAvg == 0)
        {
            weight = couldHavePlayedAvg;
        }
        else if(wasPlayableAvg == 0 && couldHavePlayedAvg == 0)
        {
            weight = almostExactAvg;
        }
        else if(almostExactAvg == 0 && couldHavePlayedAvg == 0)
        {
            weight = wasPlayableAvg;
        }
        else if(almostExactAvg == 0)
        {
            weight = (.6666 * wasPlayableAvg) + (.3334 * couldHavePlayedAvg);
        }
        else if(wasPlayableAvg == 0)
        {
            weight = (.75 * almostExactAvg) + (.25 * couldHavePlayedAvg);
        }
        else if(couldHavePlayedAvg == 0)
        {
            weight = (.6 * almostExactAvg) + (.4 * wasPlayableAvg);
        }
        else
        {
            weight = (.5 * almostExactAvg) + (.3333 * wasPlayableAvg) +
                (.1667 * couldHavePlayedAvg);
        }
        
        return weight;
    }
    
    /**
     * Learns from the previous game state, as compared to now, how good of
     * a play the card chosen on the prior turn was. This method needs to be
     * called when the AIPlayer has selected a card to play.
     * 
     * @param env the current Environment
     * @param hand the current hand
     * @param played the card that was selected to be played
     */
    public void learn(Environment env, List<Card> hand, Card played)
    {
        //don't learn from nonexistent turns
        if(this.rater != null)
        {
            PriorTurn prevTurn = new PriorTurn(this.rater.getPrevEnvironment(),
                this.rater.getHand(), this.rater.getPlayedCard());
            double playRating = this.rater.ratePlay(hand, env);
            this.playKnowledge.put(prevTurn, playRating);
            cleanKnowledgeMap();
            writeKnowledgeFile();
        }
        
        //set the PlayRater so that when it's the AIPlayer's turn again, he's
        //ready to learn
        this.rater = new PlayRater(env, hand, played);
    }
    
    /**
     * Cleans up the knowledge map if it contains too many entries.
     * Removes all entries in the knowledge map with a low usage rate. The
     * number of entries removed is scaled logarithmically with the number of
     * entries to hold before recycling.
     */
    private void cleanKnowledgeMap()
    {
        if(this.playKnowledge.size() > this.recycleNum)
        {
            int numEntriesToDelete = (int) Math.floor(Math.log10(recycleNum));
            clearEntriesFromKnowledgeMap(numEntriesToDelete);
            writeKnowledgeFile();
        }
    }
    
    /**
     * Returns a list containing all PriorTurn entries in the knowledge map
     * with the specified number of uses.
     * 
     * @param uses number of uses
     * @return list as specified above
     */
    private List<PriorTurn> getMinimalUseEntries(int uses)
    {
        ArrayList<PriorTurn> out = new ArrayList<PriorTurn>();
        
        for(PriorTurn turn : playKnowledge.keySet())
        {
            if(turn.numTimesSimilar == uses)
            {
                out.add(turn);
            }
        }
        
        return out;
    }
    
    /**
     * Removes all entries in the specified list from the knowledge map.
     * 
     * @param toRemove the list
     */
    void removeAllIn(List<PriorTurn> toRemove)
    {
        for(PriorTurn turn : toRemove)
        {
            this.playKnowledge.remove(turn);
        }
    }
    
    /**
     * Clears the specified number of entries from the knowledge map. The
     * entries that are selected are the entries with the lowest usage.
     */
    private void clearEntriesFromKnowledgeMap(int numEntriesToDelete)
    {
        int currUsageTimes = 0;
        
        while(numEntriesToDelete >= 0)
        {
            List<PriorTurn> useless = getMinimalUseEntries(currUsageTimes);
            
            if(useless.size() > numEntriesToDelete)
            {
                useless = useless.subList(0, numEntriesToDelete);
                numEntriesToDelete = 0;
            }
            else
            {
                numEntriesToDelete -= useless.size();
            }
            
            removeAllIn(useless);
            currUsageTimes++;
        }
    }
}
