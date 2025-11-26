package fuzzylogic.membershipFunction;

public interface MembershipFunction {
    double evaluate(double x);
    double getStart();
    double getEnd();
}