package fuzzylogic.operators;

public class AndProduct implements BinaryOperator {
    @Override
    public double apply(double a, double b) {
        return a * b;
    }
}
