package fuzzylogic.inference;

import fuzzylogic.core.LinguisticVariable;
import fuzzylogic.operators.BinaryOperator;
import fuzzylogic.operators.UnaryOperator;
import fuzzylogic.rules.FuzzyRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SugenoInferenceEngine implements InferenceEngine {

    private final BinaryOperator andOperator;
    private final BinaryOperator orOperator;
    private final UnaryOperator notOperator;
    private final Map<String, Map<String, ConstantFunction>> consequentFunctions;

    public SugenoInferenceEngine(BinaryOperator andOp, BinaryOperator orOp, UnaryOperator notOp) {
        this.andOperator = andOp;
        this.orOperator = orOp;
        this.notOperator = notOp;
        this.consequentFunctions = new HashMap<>();
    }

    public void setConsequentFunction(String outputVar, String fuzzySet, ConstantFunction function) {
        consequentFunctions.computeIfAbsent(outputVar, k -> new HashMap<>()).put(fuzzySet, function);
    }

    @Override
    public Map<String, Double> infer(Map<String, Double> crispInputs, Map<String, LinguisticVariable> inputVariables,
            Map<String, LinguisticVariable> outputVariables, List<FuzzyRule> rules) {

        // fuzzification
        Map<String, Map<String, Double>> fuzzifiedInputs = new HashMap<>();
        for (Map.Entry<String, Double> entry : crispInputs.entrySet()) {
            LinguisticVariable var = inputVariables.get(entry.getKey());
            if (var != null) {
                fuzzifiedInputs.put(entry.getKey(), var.fuzzify(entry.getValue()));
            }
        }

        // weighted average calculation
        Map<String, Double> numerators = new HashMap<>();
        Map<String, Double> denominators = new HashMap<>();

        for (LinguisticVariable outputVar : outputVariables.values()) {
            numerators.put(outputVar.getName(), 0.0);
            denominators.put(outputVar.getName(), 0.0);
        }

        for (FuzzyRule rule : rules) {
            if (!rule.isEnabled()) continue;

            // evaluate rule strength
            double ruleStrength = rule.evaluateStrength(fuzzifiedInputs, andOperator, orOperator, notOperator);

            if (ruleStrength > 0) {
                FuzzyRule.Consequent consequent = rule.getConsequent();
                if (consequent == null) continue;

                String outputVarName = consequent.getVariable();
                String outputSetName = consequent.getFuzzySet();

                ConstantFunction function = null;
                if (consequentFunctions.containsKey(outputVarName)) {
                    function = consequentFunctions.get(outputVarName).get(outputSetName);
                }

                double consequentValue;
                if (function != null) {
                    consequentValue = function.evaluate();
                } else {
                    consequentValue = 0.5;
                }

                numerators.put(outputVarName, numerators.get(outputVarName) + ruleStrength * consequentValue);
                denominators.put(outputVarName, denominators.get(outputVarName) + ruleStrength);
            }
        }

        // calculate weighted average
        Map<String, Double> crispOutputs = new HashMap<>();
        for (String outputVarName : numerators.keySet()) {
            double denominator = denominators.get(outputVarName);
            if (denominator > 0) {
                crispOutputs.put(outputVarName, numerators.get(outputVarName) / denominator);
            } else {
                LinguisticVariable outputVar = outputVariables.get(outputVarName);
                crispOutputs.put(outputVarName,
                        (outputVar.getMinValue() + outputVar.getMaxValue()) / 2);
            }
        }

        return crispOutputs;
    }

    // zero-order function
    public static class ConstantFunction {
        private final double constant;

        public ConstantFunction(double constant) {
            this.constant = constant;
        }

        public double evaluate() {
            return constant;
        }
    }
}