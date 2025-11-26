package fuzzylogic.operators;

public class OrSum implements BinaryOperator {
    @Override
    public double apply(double a, double b) {
        return a + b - (a * b);
    }
}
