package fuzzylogic.operators.implication;

public class MinImplication implements ImplicationOperator {
    @Override
    public double apply(double ruleStrength, double membershipValue) {
        return Math.min(ruleStrength, membershipValue);
    }
}