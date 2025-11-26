package fuzzylogic.rules;

import fuzzylogic.operators.BinaryOperator;
import fuzzylogic.operators.UnaryOperator;

import java.util.*;

public class FuzzyRule {
    private final String name;
    private final List<Antecedent> antecedents;
    private Consequent consequent;
    private boolean enabled;
    private double weight;

    public FuzzyRule(String name) {
        this.name = name;
        this.antecedents = new ArrayList<>();
        this.enabled = true;
        this.weight = 1.0;
    }

    public void addAntecedent(String variable, String fuzzySet, boolean isAnd, boolean isNot) {
        antecedents.add(new Antecedent(variable, fuzzySet, isAnd, isNot));
    }

    public void setConsequent(String variable, String fuzzySet) {
        this.consequent = new Consequent(variable, fuzzySet);
    }

    public double evaluateStrength(Map<String, Map<String, Double>> fuzzifiedInputs, BinaryOperator andOp, BinaryOperator orOp, UnaryOperator notOp) {
        if (!enabled || antecedents.isEmpty()) return 0.0;

        Antecedent first = antecedents.get(0);
        double strength = fuzzifiedInputs.get(first.variable).get(first.fuzzySet);
        if (first.isNot) {
            strength = notOp.apply(strength);
        }

        for (int i = 1; i < antecedents.size(); i++) {
            Antecedent ant = antecedents.get(i);
            double value = fuzzifiedInputs.get(ant.variable).get(ant.fuzzySet);
            if (ant.isNot) {
                value = notOp.apply(value);
            }

            strength = ant.isAnd ? andOp.apply(strength, value) : orOp.apply(strength, value);
        }

        return strength * weight;
    }

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
