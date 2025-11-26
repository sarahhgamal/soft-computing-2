package fuzzylogic.operators.implication;

public class ProductImplication implements ImplicationOperator {
    @Override
    public double apply(double ruleStrength, double membershipValue) {
        return ruleStrength * membershipValue;
    }
}