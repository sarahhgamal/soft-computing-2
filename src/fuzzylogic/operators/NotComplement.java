package fuzzylogic.operators;

public class NotComplement implements UnaryOperator {
    @Override
    public double apply(double x) {
        return 1.0 - x;
    }
}
