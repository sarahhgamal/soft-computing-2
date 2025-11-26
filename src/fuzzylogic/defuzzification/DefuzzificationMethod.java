package fuzzylogic.defuzzification;

public interface DefuzzificationMethod {
    double defuzzify(double[] membershipValues, double minValue, double maxValue, int numSteps);
}
