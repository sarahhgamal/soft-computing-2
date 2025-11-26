package fuzzylogic.membershipFunction;

public class TriangularMF implements MembershipFunction {
    private final double a, b, c;

    public TriangularMF(double a, double b, double c) {
        if (a > b || b > c) {
            throw new IllegalArgumentException("Invalid triangular parameters: a < b < c required");
        }
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double evaluate(double x) {
        if (x < a || x > c) return 0.0;
        if (x == b) return 1.0;
        if (x < b) return (x - a) / (b - a);
        return (c - x) / (c - b);
    }

    @Override
    public double getStart() { return a; }

    @Override
    public double getEnd() { return c; }
}