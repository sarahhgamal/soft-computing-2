package fuzzylogic.operators;

public class AndMin implements AndOperator {
    @Override
    public double apply(double a, double b) {
        return Math.min(a, b);
    }
}
