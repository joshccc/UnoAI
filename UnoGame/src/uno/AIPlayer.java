package uno;

import java.util.ArrayList;
import java.util.List;

public class AIPlayer implements UnoPlayer //interface
{
    PriorExperienceAgent priorExp;
    StateEvalAgent gameSt;
    MonteCarloAgent monte;
    double peWeight;
    double gsWeight;
    double mcWeight;
    String knowledgefile;	//for PriorExperience, pass to him
    static final int PE_RECYCLE_VAL = 1000000; //for PriorExperience


    //Constructor
    public AIPlayer(double peWeight, double gsWeight, double mcWeight, String knowledgeFileName){
            priorExp = 
                new PriorExperienceAgent(knowledgeFileName, PE_RECYCLE_VAL);
            gameSt = new StateEvalAgent();
            monte = new MonteCarloAgent();
            
            this.mcWeight = mcWeight;
            this.gsWeight = gsWeight;
            this.peWeight = peWeight;
    }
    
    private int getMaxWeightIdx(ArrayList<Double> totalWeightedValues){
        int outIdx = 0;
        double maxWeight = Double.MIN_VALUE;
        for (int i = 0; i < totalWeightedValues.size(); i++)
        {
            if(totalWeightedValues.get(i) > maxWeight)
            {
                outIdx = i;
                maxWeight = totalWeightedValues.get(i);
            }
        }

        return outIdx;
    }

    //multiplies each card's value with a weight, then adds them together and returns index of where max weighted card lies.
    public int makeDecision(List<Double> peWeights, List<Double> gsWeights, List<Double> mcWeights){
        ArrayList<Double> peWeightedValues = new ArrayList<Double>();
        ArrayList<Double> gsWeightedValues = new ArrayList<Double>();
        ArrayList<Double> mcWeightedValues = new ArrayList<Double>();
        
        ArrayList<Double> totalWeightedValues = new ArrayList<Double>();
        
        for (Double pe: peWeights){
                pe = pe * peWeight;
                peWeightedValues.add(pe);
        }

        for (Double gs: gsWeights){
                gs = gs * gsWeight;
                gsWeightedValues.add(gs);
        }
        
        for (Double mc: mcWeights){
                mc = mc * mcWeight;
                mcWeightedValues.add(mc);
        }
        
        for(int i = 0; i < gsWeights.size(); i++){
                double total = peWeightedValues.get(i) +
                    gsWeightedValues.get(i) + mcWeightedValues.get(i);
                totalWeightedValues.add(total);
        }

        return this.getMaxWeightIdx(totalWeightedValues);
    }


    public int play(List<Card> hand, Card upCard, Color calledColor, GameState state){
        //  pass Environment Agent CPEto GameState and PriorExperience agents
        Environment env = new Environment(upCard, calledColor, state);
        
        List<Double> priorExpWeights = this.priorExp.ratePlayableCards(env, hand);
        List<Double> gameStateWeights = this.gameSt.ratePlayableCards(hand, env);
        List<Double> monteCarloWeights = null;
                
        if (mcWeight > 0) {
            monteCarloWeights = this.monte.ratePlayableCards(hand, env);
        }
        else {
            monteCarloWeights = new ArrayList<Double>();
            for (Card card : hand) {
                monteCarloWeights.add((double) 0);
            }
        }
        
        int idx = this.makeDecision(priorExpWeights, gameStateWeights, monteCarloWeights);
        this.priorExp.learn(env, new ArrayList<Card>(hand), hand.get(idx));
        
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
        
        if (mcWeight == 1) {
            return monte.getColor();
        }

        return out;
    }
}