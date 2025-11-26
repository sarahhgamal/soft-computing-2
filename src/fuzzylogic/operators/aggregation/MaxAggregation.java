package fuzzylogic.operators.aggregation;

public class MaxAggregation implements AggregationOperator {
    @Override
    public double apply(double a, double b) {
        return Math.max(a, b);
    }
}