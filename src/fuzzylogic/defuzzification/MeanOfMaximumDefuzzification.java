package fuzzylogic.defuzzification;

public class MeanOfMaximumDefuzzification implements DefuzzificationMethod {
    @Override
    public double defuzzify(double[] membershipValues, double minValue, double maxValue, int numSteps) {
        double maxMembership = 0.0;
        for (double value : membershipValues) {
            maxMembership = Math.max(maxMembership, value);
        }

        if (maxMembership == 0.0) {
            return (minValue + maxValue) / 2;
        }

        double sum = 0.0;
        int count = 0;
        double step = (maxValue - minValue) / (numSteps - 1);
        for (int i = 0; i < numSteps; i++) {
            if (Math.abs(membershipValues[i] - maxMembership) < 0.0000001) {  // almost equal to 0
                sum += minValue + i * step;
                count++;
            }
        }

        return count > 0 ? sum / count : (minValue + maxValue) / 2;
    }
}