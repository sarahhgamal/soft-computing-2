package fuzzylogic.core;

import fuzzylogic.inference.InferenceEngine;
import fuzzylogic.rules.FuzzyRule;
import fuzzylogic.rules.RuleBase;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FuzzySystem {
    private final Map<String, LinguisticVariable> inputVariables;
    private final Map<String, LinguisticVariable> outputVariables;
    private final RuleBase ruleBase;
    private InferenceEngine inferenceEngine;

    private Map<String, Map<String, Double>> lastFuzzifiedInputs;
    private Map<String, Double> lastCrispOutputs;

    public FuzzySystem(InferenceEngine inferenceEngine) {
        this.inputVariables = new LinkedHashMap<>();
        this.outputVariables = new LinkedHashMap<>();
        this.ruleBase = new RuleBase();
        this.inferenceEngine = inferenceEngine;
    }

    public void addInputVariable(LinguisticVariable variable) {
        inputVariables.put(variable.getName(), variable);
    }

    public void addOutputVariable(LinguisticVariable variable) {
        outputVariables.put(variable.getName(), variable);
    }

    public void addRule(FuzzyRule rule) {
        ruleBase.addRule(rule);
    }

    public void setInferenceEngine(InferenceEngine engine) {
        this.inferenceEngine = engine;
    }

    // pipeline: fuzzify → infer → aggregate → defuzzify
    public Map<String, Double> calculate(Map<String, Double> crispInputs) {

        Map<String, Double> validatedInputs = validateAndClampInputs(crispInputs);

        // Fuzzify inputs
        lastFuzzifiedInputs = new HashMap<>();
        for (Map.Entry<String, Double> entry : validatedInputs.entrySet()) {
            LinguisticVariable var = inputVariables.get(entry.getKey());
            if (var != null) {
                lastFuzzifiedInputs.put(entry.getKey(), var.fuzzify(entry.getValue()));
            }
        }

        // Infer (rule evaluation, aggregation, and defuzzification)
        lastCrispOutputs = inferenceEngine.infer(
                validatedInputs,
                inputVariables,
                outputVariables,
                ruleBase.getAllRules()
        );

        return new HashMap<>(lastCrispOutputs);
    }


    private Map<String, Double> validateAndClampInputs(Map<String, Double> inputs) {
        Map<String, Double> validated = new HashMap<>();

        for (Map.Entry<String, LinguisticVariable> entry : inputVariables.entrySet()) {
            String varName = entry.getKey();
            LinguisticVariable var = entry.getValue();

            Double value = inputs.get(varName);
            if (value == null) {
                // midpoint as default
                value = (var.getMinValue() + var.getMaxValue()) / 2.0;
            }

            value = Math.max(var.getMinValue(), Math.min(var.getMaxValue(), value));
            validated.put(varName, value);
        }

        return validated;
    }

    public Map<String, Map<String, Double>> getLastFuzzifiedInputs() { return lastFuzzifiedInputs != null ? new HashMap<>(lastFuzzifiedInputs) : null; }
    public Map<String, Double> getLastCrispOutputs() { return lastCrispOutputs != null ? new HashMap<>(lastCrispOutputs) : null; }
    public Map<String, LinguisticVariable> getInputVariables() { return new HashMap<>(inputVariables); }
    public Map<String, LinguisticVariable> getOutputVariables() { return new HashMap<>(outputVariables); }
    public RuleBase getRuleBase() { return ruleBase; }
    public InferenceEngine getInferenceEngine() { return inferenceEngine; }
}