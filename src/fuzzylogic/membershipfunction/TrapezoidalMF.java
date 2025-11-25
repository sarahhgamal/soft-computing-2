package fuzzylogic.membershipfunction;

public class TrapezoidalMF implements MembershipFunction {
    private final double a, b, c, d;

    public TrapezoidalMF(double a, double b, double c, double d) {
        if (a >= b || b >= c || c >= d) {
            throw new IllegalArgumentException("Invalid trapezoidal parameters: a < b < c < d required");
        }
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override
    public double evaluate(double x) {
        if (x <= a || x >= d) return 0.0;
        if (x >= b && x <= c) return 1.0;
        if (x < b) return (x - a) / (b - a);
        return (d - x) / (d - c);
    }

    @Override
    public double getStart() { return a; }

    @Override
    public double getEnd() { return d; }
}