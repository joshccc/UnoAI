package uno;

import java.util.ArrayList;
import java.util.List;

public class AIPlayer implements UnoPlayer //interface
{
    PriorExperienceAgent priorExp;
    StateEvalAgent gameSt;
    int peWeight;
    int gsWeight;
    String knowledgefile;	//for PriorExperience, pass to him


    //Constructor
    public AIPlayer(int peWeight, int gsWeight){
            priorExp = new PriorExperienceAgent();
            gameSt = new StateEvalAgent();
            this.gsWeight = gsWeight;
            this.peWeight = peWeight;
    }

    //alec returns a list of doubles for weight of card from previousExp
    //tarrant returns a list of doubles for weight of card from state
    //ints are to be passed in
    //PE 0 ,2 ,3 1 * weight decided by us (int)
    //GE .2 .1 1 .8 * weight decided by us (int)

    //returns index of where the max weight is in ArrayList. Position correlates to position of card in hand.
    private int getMaxWeightIdx(ArrayList<Double> totalWeightedValues){
            double maximum = totalWeightedValues.get(0);
            for (int i = 1; i < totalWeightedValues.size(); i++){
                    if (totalWeightedValues.get(i) > maximum)
                            maximum = totalWeightedValues.get(i);
            }

            return totalWeightedValues.indexOf(maximum);
    }

    //multiplies each card's value with a weight, then adds them together and returns index of where max weighted card lies.
    public int makeDecision(List<Double> peWeights, List<Double> gsWeights, int peWeight, int gsWeight){
        ArrayList<Double> peWeightedValues = new ArrayList<Double>();
        ArrayList<Double> gsWeightedValues = new ArrayList<Double>();
        ArrayList<Double> totalWeightedValues = new ArrayList<Double>();

        for (Double pe: peWeights){
                pe = pe * peWeight;
                peWeightedValues.add(pe);
        }

        for (Double gs: gsWeights){
                gs = gs * gsWeight;
                gsWeightedValues.add(gs);
        }

        for(int i = 0; i < gsWeights.size(); i++){
                double total = peWeightedValues.get(i) + gsWeightedValues.get(i);
                totalWeightedValues.add(total);
        }

        return this.getMaxWeightIdx(totalWeightedValues);
    }


    public int play(List<Card> hand, Card upCard, Color calledColor, GameState state){
        //  pass Environment Agent CPEto GameState and PriorExperience agents
        EnvironmentAgent ea = new EnvironmentAgent(upCard, calledColor, state);
        ArrayList<Double> priorExpWeights = this.priorExp.ratePlayableCard();

        List<Double> gameStateWeights = this.gameSt.ratePlayableCard(hand, state);
        int idx = this.makeDecision(priorExpWeights, gameStateWeights, peWeight, gsWeight);
        return idx;
    }

    /**
       * <p>This method will be called when you have just played a
       * wild card, and is your way of specifying which color you want to 
       * change it to.</p>
       *
       * <p>You must return a valid Color value from this method. You must
       * not return the value Color.NONE under any circumstances.</p>
       */

    @Override
    public Color callColor(List<Card> hand)
    {
        UnoPlayer.Color out = UnoPlayer.Color.RED;

        return out;
    }
}