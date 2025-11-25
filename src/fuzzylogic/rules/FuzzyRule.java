package fuzzylogic.rules;

import java.util.*;

public class FuzzyRule {
    private final String name;
    private final List<Antecedent> antecedents;
    private Consequent consequent;  // not final
    private boolean enabled;
    private double weight;

    public FuzzyRule(String name) {
        this.name = name;
        this.antecedents = new ArrayList<>();
        this.enabled = true;
        this.weight = 1.0;
    }

    // Add an antecedent: AND/OR + optional NOT
    public void addAntecedent(String variable, String fuzzySet, boolean isAnd, boolean isNot) {
        antecedents.add(new Antecedent(variable, fuzzySet, isAnd, isNot));
    }

    // Set the rule's consequent
    public void setConsequent(String variable, String fuzzySet) {
        this.consequent = new Consequent(variable, fuzzySet);
    }

    // Evaluate rule strength given fuzzified inputs
    public double evaluateStrength(Map<String, Map<String, Double>> fuzzifiedInputs) {
        if (!enabled || antecedents.isEmpty()) return 0.0;

        // Start with first antecedent
        Antecedent first = antecedents.get(0);
        double strength = fuzzifiedInputs.get(first.variable).get(first.fuzzySet);
        if (first.isNot) strength = 1.0 - strength;

        // Combine remaining antecedents using AND/OR
        for (int i = 1; i < antecedents.size(); i++) {
            Antecedent ant = antecedents.get(i);
            double value = fuzzifiedInputs.get(ant.variable).get(ant.fuzzySet);
            if (ant.isNot) value = 1.0 - value;

            strength = ant.isAnd ? Math.min(strength, value) : Math.max(strength, value);
        }

        // Apply rule weight
        return strength * weight;
    }

    // Getters and setters
    public String getName() { return name; }
    public Consequent getConsequent() { return consequent; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = Math.max(0, Math.min(1, weight)); }

    // Nested classes
    private static class Antecedent {
        String variable;
        String fuzzySet;
        boolean isAnd;
        boolean isNot;

        Antecedent(String variable, String fuzzySet, boolean isAnd, boolean isNot) {
            this.variable = variable;
            this.fuzzySet = fuzzySet;
            this.isAnd = isAnd;
            this.isNot = isNot;
        }
    }

    public static class Consequent {
        private final String variable;
        private final String fuzzySet;

        public Consequent(String variable, String fuzzySet) {
            this.variable = variable;
            this.fuzzySet = fuzzySet;
        }

        public String getVariable() { return variable; }
        public String getFuzzySet() { return fuzzySet; }
    }
}
