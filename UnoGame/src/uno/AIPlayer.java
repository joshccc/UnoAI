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
    static final int PE_RECYCLE_VAL = 1000000; //for PriorExperience


    //Constructor
    public AIPlayer(int peWeight, int gsWeight, String knowledgeFileName){
            //priorExp = new PriorExperienceAgent(knowledgeFileName, PE_RECYCLE_VAL);
            gameSt = new StateEvalAgent();
            this.gsWeight = gsWeight;
            this.peWeight = peWeight;
    }
    
    private int getMaxWeightIdx(ArrayList<Double> totalWeightedValues){
            int index = 0;
            for (int i = 1; i < totalWeightedValues.size(); i++){
                    if (totalWeightedValues.get(i) > totalWeightedValues.get(index))
                            index = i;
            }

            //return totalWeightedValues.indexOf(maximum);
            return index;
    }

    //multiplies each card's value with a weight, then adds them together and returns index of where max weighted card lies.
    public int makeDecision(List<Double> peWeights, List<Double> gsWeights, int peWeight, int gsWeight){
        ArrayList<Double> peWeightedValues = new ArrayList<Double>();
        ArrayList<Double> gsWeightedValues = new ArrayList<Double>();
        ArrayList<Double> totalWeightedValues = new ArrayList<Double>();

        //currently omitted b/c PEA doesn't work yet*/
        
        /*for (Double pe: peWeights){
                pe = pe * peWeight;
                peWeightedValues.add(pe);
        }*/

        for (Double gs: gsWeights){
                gs = gs * gsWeight;
                gsWeightedValues.add(gs);
        }

        for(int i = 0; i < gsWeights.size(); i++){
                double total = /*peWeightedValues.get(i) +*/
                    gsWeightedValues.get(i);
                totalWeightedValues.add(total);
        }

        return this.getMaxWeightIdx(totalWeightedValues);
    }


    public int play(List<Card> hand, Card upCard, Color calledColor, GameState state){
        //  pass Environment Agent CPEto GameState and PriorExperience agents
        Environment env = new Environment(upCard, calledColor, state);
        
        List<Double> priorExpWeights = new ArrayList<>();/* = this.priorExp.ratePlayableCards(env, hand);*/
        List<Double> gameStateWeights = this.gameSt.ratePlayableCards(hand, env);
        int idx = this.makeDecision(priorExpWeights, gameStateWeights, peWeight, gsWeight);
        //this.priorExp.learn(env, hand, hand.get(idx));
        
        if (env.checkPlayable(hand.get(idx)) == 0)
        {
            idx = -1;
        }
        return idx;
    }
    
    @Override
    public Color callColor(List<Card> hand)
    {
        //TODO
        UnoPlayer.Color out = UnoPlayer.Color.RED;

        return out;
    }
}