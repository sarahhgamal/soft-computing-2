package fuzzylogic.rules;

import java.util.*;

public class RuleBase {
    private final List<FuzzyRule> rules;

    public RuleBase() {
        this.rules = new ArrayList<>();
    }

    public void addRule(FuzzyRule rule) {
        rules.add(rule);
    }

    public void removeRule(String ruleName) {
        rules.removeIf(rule -> rule.getName().equals(ruleName));
    }

    public FuzzyRule getRule(String ruleName) {
        return rules.stream()
                .filter(rule -> rule.getName().equals(ruleName))
                .findFirst()
                .orElse(null);
    }

    public List<FuzzyRule> getAllRules() {
        return new ArrayList<>(rules);
    }

    public void enableRule(String ruleName, boolean enabled) {
        FuzzyRule rule = getRule(ruleName);
        if (rule != null) {
            rule.setEnabled(enabled);
        }
    }

    public void setRuleWeight(String ruleName, double weight) {
        FuzzyRule rule = getRule(ruleName);
        if (rule != null) {
            rule.setWeight(weight);
        }
    }
}