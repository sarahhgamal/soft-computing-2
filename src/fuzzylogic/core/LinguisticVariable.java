package fuzzylogic.core;

import java.util.*;

public class LinguisticVariable {
    private final String name;
    private final double minValue;
    private final double maxValue;
    private final Map<String, FuzzySet> fuzzySets;

    public LinguisticVariable(String name, double minValue, double maxValue) {
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.fuzzySets = new LinkedHashMap<>();
    }

    public void addFuzzySet(FuzzySet fuzzySet) {
        fuzzySets.put(fuzzySet.getName(), fuzzySet);
    }

    public Map<String, Double> fuzzify(double crispValue) {
        double clampedValue = Math.max(minValue, Math.min(maxValue, crispValue));
        Map<String, Double> result = new HashMap<>();

        for (Map.Entry<String, FuzzySet> entry : fuzzySets.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getMembership(clampedValue));
        }

        return result;
    }

    public String getName() { return name; }
    public double getMinValue() { return minValue; }
    public double getMaxValue() { return maxValue; }
    public Map<String, FuzzySet> getFuzzySets() { return fuzzySets; }
}