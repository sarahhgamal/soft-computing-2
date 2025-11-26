package fuzzylogic.inference;

import fuzzylogic.core.FuzzySet;
import fuzzylogic.core.LinguisticVariable;
import fuzzylogic.defuzzification.DefuzzificationMethod;
import fuzzylogic.operators.BinaryOperator;
import fuzzylogic.operators.UnaryOperator;
import fuzzylogic.operators.aggregation.AggregationOperator;
import fuzzylogic.operators.implication.ImplicationOperator;
import fuzzylogic.rules.FuzzyRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MamdaniInferenceEngine implements InferenceEngine {
    private BinaryOperator andOperator;
    private BinaryOperator orOperator;
    private UnaryOperator notOperator;
    private ImplicationOperator implicationOperator;
    private AggregationOperator aggregationOperator;
    private DefuzzificationMethod defuzzificationMethod;
    private int numSteps;

    public MamdaniInferenceEngine(BinaryOperator andOp, BinaryOperator orOp, UnaryOperator notOp,
                                  ImplicationOperator implOp, AggregationOperator aggOp,
                                  DefuzzificationMethod defuzzMethod, int numSteps) {
        this.andOperator = andOp;
        this.orOperator = orOp;
        this.notOperator = notOp;
        this.implicationOperator = implOp;
        this.aggregationOperator = aggOp;
        this.defuzzificationMethod = defuzzMethod;
        this.numSteps = numSteps;
    }

    @Override
    public Map<String, Double> infer(Map<String, Double> crispInputs, Map<String, LinguisticVariable> inputVariables, Map<String, LinguisticVariable> outputVariables, List<FuzzyRule> rules) {

        // fuzzification
        Map<String, Map<String, Double>> fuzzifiedInputs = new HashMap<>();
        for (Map.Entry<String, Double> entry : crispInputs.entrySet()) {
            LinguisticVariable var = inputVariables.get(entry.getKey());
            if (var != null) {
                fuzzifiedInputs.put(entry.getKey(), var.fuzzify(entry.getValue()));
            }
        }

        // rule evaluation
        Map<String, double[]> aggregatedOutputs = new HashMap<>();

        for (LinguisticVariable outputVar : outputVariables.values()) {
            aggregatedOutputs.put(outputVar.getName(), new double[numSteps]);
        }

        for (FuzzyRule rule : rules) {
            if (!rule.isEnabled()) continue;

            double ruleStrength = rule.evaluateStrength(
                    fuzzifiedInputs, andOperator, orOperator, notOperator
            );

            if (ruleStrength > 0) {
                FuzzyRule.Consequent consequent = rule.getConsequent();
                if (consequent == null) continue;

                String outputVarName = consequent.getVariable();
                String outputSetName = consequent.getFuzzySet();

                LinguisticVariable outputVar = outputVariables.get(outputVarName);
                if (outputVar == null) continue;

                FuzzySet outputSet = outputVar.getFuzzySets().get(outputSetName);
                if (outputSet == null) continue;

                double[] aggregated = aggregatedOutputs.get(outputVarName);
                double min = outputVar.getMinValue();
                double max = outputVar.getMaxValue();
                double step = (max - min) / (numSteps - 1);

                for (int i = 0; i < numSteps; i++) {
                    double x = min + i * step;
                    double membership = outputSet.getMembership(x);
                    double impliedMembership = implicationOperator.apply(ruleStrength, membership);
                    aggregated[i] = aggregationOperator.apply(aggregated[i], impliedMembership);
                }
            }
        }

        // defuzzification
        Map<String, Double> crispOutputs = new HashMap<>();
        for (Map.Entry<String, double[]> entry : aggregatedOutputs.entrySet()) {
            LinguisticVariable outputVar = outputVariables.get(entry.getKey());
            double crispValue = defuzzificationMethod.defuzzify(entry.getValue(), outputVar.getMinValue(), outputVar.getMaxValue(), numSteps);
            crispOutputs.put(entry.getKey(), crispValue);
        }

        return crispOutputs;
    }

    public void setAndOperator(BinaryOperator op) { this.andOperator = op; }
    public void setOrOperator(BinaryOperator op) { this.orOperator = op; }
    public void setNotOperator(UnaryOperator op) { this.notOperator = op; }
    public void setImplicationOperator(ImplicationOperator op) { this.implicationOperator = op; }
    public void setAggregationOperator(AggregationOperator op) { this.aggregationOperator = op; }
    public void setDefuzzificationMethod(DefuzzificationMethod method) { this.defuzzificationMethod = method; }
    public void setResolution(int numSteps) { this.numSteps = numSteps; }
}

