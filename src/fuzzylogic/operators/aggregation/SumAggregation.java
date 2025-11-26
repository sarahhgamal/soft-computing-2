package fuzzylogic.operators.aggregation;

public class SumAggregation implements AggregationOperator {
    @Override
    public double apply(double a, double b) {
        return Math.min(1.0, a + b);
    }
}