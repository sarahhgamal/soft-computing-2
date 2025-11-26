package fuzzylogic.operators;

public class AndMin implements BinaryOperator {
    @Override
    public double apply(double a, double b) {
        return Math.min(a, b);
    }
}
