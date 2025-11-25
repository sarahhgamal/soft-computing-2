package fuzzylogic.membershipfunction;

public interface MembershipFunction {
    double evaluate(double x);
    double getStart();
    double getEnd();
}