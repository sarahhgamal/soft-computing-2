package fuzzylogic.core;

import fuzzylogic.membershipfunction.MembershipFunction;

public class FuzzySet {
    private final String name;
    private final MembershipFunction membershipFunction;

    public FuzzySet(String name, MembershipFunction membershipFunction) {
        this.name = name;
        this.membershipFunction = membershipFunction;
    }

    public double getMembership(double value) {
        return membershipFunction.evaluate(value);
    }

    public String getName() {
        return name;
    }

    public MembershipFunction getMembershipFunction() {
        return membershipFunction;
    }
}