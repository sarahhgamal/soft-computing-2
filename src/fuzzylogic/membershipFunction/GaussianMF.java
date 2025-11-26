package fuzzylogic.membershipFunction;

public class GaussianMF implements MembershipFunction {
    private final double mean, sigma;
    private final double start, end;

    public GaussianMF(double mean, double sigma) {
        if (sigma <= 0) {
            throw new IllegalArgumentException("Sigma must be positive");
        }
        this.mean = mean;
        this.sigma = sigma;
        this.start = mean - 3 * sigma;
        this.end = mean + 3 * sigma;
    }

    @Override
    public double evaluate(double x) {
        double exp = -Math.pow(x - mean, 2) / (2 * sigma * sigma);
        return Math.exp(exp);
    }

    @Override
    public double getStart() { return start; }

    @Override
    public double getEnd() { return end; }
}