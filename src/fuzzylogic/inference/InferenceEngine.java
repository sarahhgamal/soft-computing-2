package fuzzylogic.inference;

import fuzzylogic.core.LinguisticVariable;
import fuzzylogic.rules.FuzzyRule;

import java.util.List;
import java.util.Map;

public interface InferenceEngine {
    Map<String, Double> infer(Map<String, Double> crispInputs, Map<String, LinguisticVariable> inputVariables, Map<String, LinguisticVariable> outputVariables, List<FuzzyRule> rules);
}
