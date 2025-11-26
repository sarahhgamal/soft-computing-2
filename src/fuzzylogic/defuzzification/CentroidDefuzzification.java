package fuzzylogic.defuzzification;

public class CentroidDefuzzification implements DefuzzificationMethod {

    @Override
    public double defuzzify(double[] membershipValues, double minValue, double maxValue, int numSteps) {
        double numerator = 0.0;
        double denominator = 0.0;
        double step = (maxValue - minValue) / (numSteps - 1);

        for (int i = 0; i < numSteps; i++) {
            double x = minValue + i * step;
            numerator += x * membershipValues[i];
            denominator += membershipValues[i];
        }

        return denominator == 0 ? (minValue + maxValue) / 2 : numerator / denominator;
    }
}
