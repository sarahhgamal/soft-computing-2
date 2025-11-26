import fuzzylogic.core.*;
import fuzzylogic.membershipFunction.TrapezoidalMF;
import fuzzylogic.membershipFunction.TriangularMF;
import fuzzylogic.operators.*;
import fuzzylogic.defuzzification.*;
import fuzzylogic.inference.*;
import fuzzylogic.operators.aggregation.MaxAggregation;
import fuzzylogic.operators.implication.MinImplication;
import fuzzylogic.rules.*;
import java.util.*;

public class MusicRecommendationSystem {

    private FuzzySystem mamdaniSystem;
    private FuzzySystem sugenoSystem;
    private FuzzySystem currentSystem;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("MOOD-BASED MUSIC RECOMMENDATION SYSTEM");

        System.out.println("Choose fuzzy system (1 or 2):");
        System.out.println("1. Mamdani");
        System.out.println("2. Sugeno");
        System.out.print("Your choice: ");
        int choice = scanner.nextInt();

        MusicRecommendationSystem system = new MusicRecommendationSystem();
        switch (choice) {
            case 1 -> {
                system.initializeMamdaniSystem();
                system.currentSystem = system.mamdaniSystem;
                System.out.println("\nRunning Mamdani system...\n");
            }
            case 2 -> {
                system.initializeSugenoSystem();
                system.currentSystem = system.sugenoSystem;
                System.out.println("\nRunning Sugeno system...\n");
            }
            default -> {
                System.out.println("\nInvalid choice. Defaulting to Mamdani system.");
                system.initializeMamdaniSystem();
                system.currentSystem = system.mamdaniSystem;
            }
        }

        system.runTestCases();

        system.runInteractiveDemo();

        scanner.close();
    }

    // Initialize Mamdani music recommendation system
    private void initializeMamdaniSystem() {
        System.out.println("Initializing Mamdani Inference Engine...");

        MamdaniInferenceEngine engine =
                new MamdaniInferenceEngine(new AndMin(), new OrMax(), new NotComplement(),
                        new MinImplication(), new MaxAggregation(), new CentroidDefuzzification(), 200);

        mamdaniSystem = new FuzzySystem(engine);

        // Heart Rate (50-120 bpm)
        LinguisticVariable heartRate = new LinguisticVariable("heartRate", 50, 120);
        heartRate.addFuzzySet(new FuzzySet("low", new TrapezoidalMF(50, 50, 60, 70)));
        heartRate.addFuzzySet(new FuzzySet("medium", new TriangularMF(65, 77.5, 90)));
        heartRate.addFuzzySet(new FuzzySet("high", new TrapezoidalMF(85, 95, 120, 120)));
        mamdaniSystem.addInputVariable(heartRate);

        // Facial Expression (0-10 scale)
        LinguisticVariable facialExpression = new LinguisticVariable("facialExpression", 0, 10);
        facialExpression.addFuzzySet(new FuzzySet("frown", new TrapezoidalMF(0, 0, 2, 4)));
        facialExpression.addFuzzySet(new FuzzySet("neutral", new TriangularMF(3, 5, 7)));
        facialExpression.addFuzzySet(new FuzzySet("smile", new TrapezoidalMF(6, 8, 10, 10)));
        mamdaniSystem.addInputVariable(facialExpression);

        // Time of Day (0-24 hours)
        LinguisticVariable timeOfDay = new LinguisticVariable("timeOfDay", 0, 24);
        timeOfDay.addFuzzySet(new FuzzySet("night", new TrapezoidalMF(0, 0, 6, 8)));
        timeOfDay.addFuzzySet(new FuzzySet("morning", new TrapezoidalMF(6, 8, 11, 13)));
        timeOfDay.addFuzzySet(new FuzzySet("afternoon", new TriangularMF(12, 15, 18)));
        timeOfDay.addFuzzySet(new FuzzySet("evening", new TrapezoidalMF(17, 20, 24, 24)));
        mamdaniSystem.addInputVariable(timeOfDay);

        // Music Mood Score (0-100)
        LinguisticVariable musicMood = new LinguisticVariable("musicMood", 0, 100);
        musicMood.addFuzzySet(new FuzzySet("calm", new TrapezoidalMF(0, 0, 10, 25)));
        musicMood.addFuzzySet(new FuzzySet("chill", new TriangularMF(15, 30, 45)));
        musicMood.addFuzzySet(new FuzzySet("neutral", new TriangularMF(35, 50, 65)));
        musicMood.addFuzzySet(new FuzzySet("upbeat", new TriangularMF(55, 70, 85)));
        musicMood.addFuzzySet(new FuzzySet("energetic", new TrapezoidalMF(75, 90, 100, 100)));
        mamdaniSystem.addOutputVariable(musicMood);

        // FUZZY RULES

        // IF Heart Rate is Low AND Facial Expression is Frown THEN Music = Calm/Melancholic
        FuzzyRule rule1 = new FuzzyRule("Rule1_Low_HR_Sad_Face");
        rule1.addAntecedent("heartRate", "low", true, false);
        rule1.addAntecedent("facialExpression", "frown", true, false);
        rule1.setConsequent("musicMood", "calm");
        rule1.setWeight(1.0);
        mamdaniSystem.addRule(rule1);

        // IF Heart Rate is High AND Facial Expression is Smile THEN Music = Energetic/Happy
        FuzzyRule rule2 = new FuzzyRule("Rule2_High_HR_Happy_Face");
        rule2.addAntecedent("heartRate", "high", true, false);
        rule2.addAntecedent("facialExpression", "smile", true, false);
        rule2.setConsequent("musicMood", "energetic");
        rule2.setWeight(1.0);
        mamdaniSystem.addRule(rule2);

        // IF Heart Rate is Medium AND Time of Day is Morning THEN Music = Neutral/Background
        FuzzyRule rule3 = new FuzzyRule("Rule3_Medium_HR_Morning");
        rule3.addAntecedent("heartRate", "medium", true, false);
        rule3.addAntecedent("timeOfDay", "morning", true, false);
        rule3.setConsequent("musicMood", "neutral");
        rule3.setWeight(0.9);
        mamdaniSystem.addRule(rule3);

        // IF Heart Rate is Low AND Facial Expression is Smile THEN Music = Chill/Happy
        FuzzyRule rule4 = new FuzzyRule("Rule4_Low_HR_Happy_Face");
        rule4.addAntecedent("heartRate", "low", true, false);
        rule4.addAntecedent("facialExpression", "smile", true, false);
        rule4.setConsequent("musicMood", "chill");
        rule4.setWeight(1.0);
        mamdaniSystem.addRule(rule4);

        // IF Heart Rate is High AND Facial Expression is Neutral THEN Music = Moderate/Upbeat
        FuzzyRule rule5 = new FuzzyRule("Rule5_High_HR_Neutral_Face");
        rule5.addAntecedent("heartRate", "high", true, false);
        rule5.addAntecedent("facialExpression", "neutral", true, false);
        rule5.setConsequent("musicMood", "upbeat");
        rule5.setWeight(1.0);
        mamdaniSystem.addRule(rule5);

        // IF Heart Rate is Medium AND Facial Expression is Neutral THEN Music = Neutral
        FuzzyRule rule6 = new FuzzyRule("Rule6_Medium_HR_Neutral_Face");
        rule6.addAntecedent("heartRate", "medium", true, false);
        rule6.addAntecedent("facialExpression", "neutral", true, false);
        rule6.setConsequent("musicMood", "neutral");
        rule6.setWeight(0.8);
        mamdaniSystem.addRule(rule6);

        // IF Time of Day is Night AND Heart Rate is Low THEN Music = Calm
        FuzzyRule rule7 = new FuzzyRule("Rule7_Night_Low_HR");
        rule7.addAntecedent("timeOfDay", "night", true, false);
        rule7.addAntecedent("heartRate", "low", true, false);
        rule7.setConsequent("musicMood", "calm");
        rule7.setWeight(0.85);
        mamdaniSystem.addRule(rule7);

        // IF Time of Day is Evening AND Heart Rate is Medium THEN Music = Chill
        FuzzyRule rule8 = new FuzzyRule("Rule8_Evening_Medium_HR");
        rule8.addAntecedent("timeOfDay", "evening", true, false);
        rule8.addAntecedent("heartRate", "medium", true, false);
        rule8.setConsequent("musicMood", "chill");
        rule8.setWeight(0.75);
        mamdaniSystem.addRule(rule8);

        System.out.println("✓ Mamdani system initialized with " + mamdaniSystem.getRuleBase().getAllRules().size() + " rules.");
    }

    // Initialize Sugeno music recommendation system (Zero-Order)
    private void initializeSugenoSystem() {
        System.out.println("Initializing Sugeno Inference Engine (Zero-Order)...");

        SugenoInferenceEngine engine = new SugenoInferenceEngine(new AndMin(), new OrMax(), new NotComplement());

        // define zero-order consequent functions (constants)
        engine.setConsequentFunction("musicMood", "calm",
                new SugenoInferenceEngine.ConstantFunction(15.0));
        engine.setConsequentFunction("musicMood", "chill",
                new SugenoInferenceEngine.ConstantFunction(30.0));
        engine.setConsequentFunction("musicMood", "neutral",
                new SugenoInferenceEngine.ConstantFunction(50.0));
        engine.setConsequentFunction("musicMood", "upbeat",
                new SugenoInferenceEngine.ConstantFunction(70.0));
        engine.setConsequentFunction("musicMood", "energetic",
                new SugenoInferenceEngine.ConstantFunction(90.0));

        sugenoSystem = new FuzzySystem(engine);

        // Heart Rate
        LinguisticVariable heartRate = new LinguisticVariable("heartRate", 50, 120);
        heartRate.addFuzzySet(new FuzzySet("low", new TrapezoidalMF(50, 50, 60, 70)));
        heartRate.addFuzzySet(new FuzzySet("medium", new TriangularMF(65, 77.5, 90)));
        heartRate.addFuzzySet(new FuzzySet("high", new TrapezoidalMF(85, 95, 120, 120)));
        sugenoSystem.addInputVariable(heartRate);

        // Facial Expression
        LinguisticVariable facialExpression = new LinguisticVariable("facialExpression", 0, 10);
        facialExpression.addFuzzySet(new FuzzySet("frown", new TrapezoidalMF(0, 0, 2, 4)));
        facialExpression.addFuzzySet(new FuzzySet("neutral", new TriangularMF(3, 5, 7)));
        facialExpression.addFuzzySet(new FuzzySet("smile", new TrapezoidalMF(6, 8, 10, 10)));
        sugenoSystem.addInputVariable(facialExpression);

        // Time of Day
        LinguisticVariable timeOfDay = new LinguisticVariable("timeOfDay", 0, 24);
        timeOfDay.addFuzzySet(new FuzzySet("night", new TrapezoidalMF(0, 0, 6, 8)));
        timeOfDay.addFuzzySet(new FuzzySet("morning", new TrapezoidalMF(6, 8, 11, 13)));
        timeOfDay.addFuzzySet(new FuzzySet("afternoon", new TriangularMF(12, 15, 18)));
        timeOfDay.addFuzzySet(new FuzzySet("evening", new TrapezoidalMF(17, 20, 24, 24)));
        sugenoSystem.addInputVariable(timeOfDay);

        // Music Mood Score
        LinguisticVariable musicMood = new LinguisticVariable("musicMood", 0, 100);
        musicMood.addFuzzySet(new FuzzySet("calm", new TrapezoidalMF(0, 0, 10, 25)));
        musicMood.addFuzzySet(new FuzzySet("chill", new TriangularMF(15, 30, 45)));
        musicMood.addFuzzySet(new FuzzySet("neutral", new TriangularMF(35, 50, 65)));
        musicMood.addFuzzySet(new FuzzySet("upbeat", new TriangularMF(55, 70, 85)));
        musicMood.addFuzzySet(new FuzzySet("energetic", new TrapezoidalMF(75, 90, 100, 100)));
        sugenoSystem.addOutputVariable(musicMood);

        // rules
        FuzzyRule rule1 = new FuzzyRule("Rule1_Low_HR_Sad_Face");
        rule1.addAntecedent("heartRate", "low", true, false);
        rule1.addAntecedent("facialExpression", "frown", true, false);
        rule1.setConsequent("musicMood", "calm");
        rule1.setWeight(1.0);
        sugenoSystem.addRule(rule1);

        FuzzyRule rule2 = new FuzzyRule("Rule2_High_HR_Happy_Face");
        rule2.addAntecedent("heartRate", "high", true, false);
        rule2.addAntecedent("facialExpression", "smile", true, false);
        rule2.setConsequent("musicMood", "energetic");
        rule2.setWeight(1.0);
        sugenoSystem.addRule(rule2);

        FuzzyRule rule3 = new FuzzyRule("Rule3_Medium_HR_Morning");
        rule3.addAntecedent("heartRate", "medium", true, false);
        rule3.addAntecedent("timeOfDay", "morning", true, false);
        rule3.setConsequent("musicMood", "neutral");
        rule3.setWeight(0.9);
        sugenoSystem.addRule(rule3);

        FuzzyRule rule4 = new FuzzyRule("Rule4_Low_HR_Happy_Face");
        rule4.addAntecedent("heartRate", "low", true, false);
        rule4.addAntecedent("facialExpression", "smile", true, false);
        rule4.setConsequent("musicMood", "chill");
        rule4.setWeight(1.0);
        sugenoSystem.addRule(rule4);

        FuzzyRule rule5 = new FuzzyRule("Rule5_High_HR_Neutral_Face");
        rule5.addAntecedent("heartRate", "high", true, false);
        rule5.addAntecedent("facialExpression", "neutral", true, false);
        rule5.setConsequent("musicMood", "upbeat");
        rule5.setWeight(1.0);
        sugenoSystem.addRule(rule5);

        FuzzyRule rule6 = new FuzzyRule("Rule6_Medium_HR_Neutral_Face");
        rule6.addAntecedent("heartRate", "medium", true, false);
        rule6.addAntecedent("facialExpression", "neutral", true, false);
        rule6.setConsequent("musicMood", "neutral");
        rule6.setWeight(0.8);
        sugenoSystem.addRule(rule6);

        FuzzyRule rule7 = new FuzzyRule("Rule7_Night_Low_HR");
        rule7.addAntecedent("timeOfDay", "night", true, false);
        rule7.addAntecedent("heartRate", "low", true, false);
        rule7.setConsequent("musicMood", "calm");
        rule7.setWeight(0.85);
        sugenoSystem.addRule(rule7);

        FuzzyRule rule8 = new FuzzyRule("Rule8_Evening_Medium_HR");
        rule8.addAntecedent("timeOfDay", "evening", true, false);
        rule8.addAntecedent("heartRate", "medium", true, false);
        rule8.setConsequent("musicMood", "chill");
        rule8.setWeight(0.75);
        sugenoSystem.addRule(rule8);

        System.out.println("Sugeno system initialized with " + sugenoSystem.getRuleBase().getAllRules().size() + " rules.");
    }

    private void runTestCases() {
        System.out.println("RUNNING TEST CASES");

        Object[][] testCases = {
                {55.0, 2.0, 3.0, "Relaxed & Sad at Night"},
                {60.0, 8.5, 10.0, "Low HR & Happy in Morning"},
                {105.0, 5.5, 20.0, "High HR & Neutral in Evening"}
        };

        for (int i = 0; i < testCases.length; i++) {
            Object[] testCase = testCases[i];
            double heartRate = (Double) testCase[0];
            double facialExpr = (Double) testCase[1];
            double timeOfDay = (Double) testCase[2];
            String scenario = (String) testCase[3];

            Map<String, Double> inputs = new HashMap<>();
            inputs.put("heartRate", heartRate);
            inputs.put("facialExpression", facialExpr);
            inputs.put("timeOfDay", timeOfDay);

            Map<String, Double> outputs = currentSystem.calculate(inputs);
            double musicMood = outputs.get("musicMood");

            System.out.println("┌─ Test Case " + (i + 1) + ": " + scenario);
            System.out.println("│  Inputs:");
            System.out.println("│    • Heart Rate: " + String.format("%.1f", heartRate) + " bpm");
            System.out.println("│    • Facial Expression: " + String.format("%.1f", facialExpr) + " (" + interpretFacialExpression(facialExpr) + ")");
            System.out.println("│    • Time of Day: " + formatTimeOfDay(timeOfDay) + " (" + interpretTimeOfDay(timeOfDay) + ")");

            System.out.println("│  Fuzzified Values:");
            Map<String, Map<String, Double>> fuzzified = currentSystem.getLastFuzzifiedInputs();
            for (Map.Entry<String, Map<String, Double>> varEntry : fuzzified.entrySet()) {
                for (Map.Entry<String, Double> setEntry : varEntry.getValue().entrySet()) {
                    if (setEntry.getValue() > 0.01) {
                        System.out.println("│    • " + varEntry.getKey() + "." + setEntry.getKey() + " = " + String.format("%.3f", setEntry.getValue()));
                    }
                }
            }

            System.out.println("│  Output:");
            System.out.println("│    ♪ Music Mood Score: " + String.format("%.2f", musicMood) + "/100 (" + interpretMusicMood(musicMood) + ")");
            System.out.println("└" + "─".repeat(60) + "\n");
        }
    }

    private void runInteractiveDemo() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("!!!!!!!  INTERACTIVE DEMO MODE  !!!!!!!");
        System.out.println("Enter your physiological and contextual data for personalized");
        System.out.println("music recommendations!\n");

        while (true) {
            try {
                System.out.print("Enter Heart Rate (50-120 bpm) [or 'q' to quit]: ");
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("q")) {
                    System.out.println("\n✓ Exiting interactive mode. Thank you!");
                    break;
                }
                double heartRate = Double.parseDouble(input);

                System.out.print("Enter Facial Expression (0=Frown, 5=Neutral, 10=Smile): ");
                double facialExpr = Double.parseDouble(scanner.nextLine().trim());

                System.out.print("Enter Time of Day (0-24 hours): ");
                double timeOfDay = Double.parseDouble(scanner.nextLine().trim());

                Map<String, Double> inputs = new HashMap<>();
                inputs.put("heartRate", heartRate);
                inputs.put("facialExpression", facialExpr);
                inputs.put("timeOfDay", timeOfDay);

                Map<String, Double> outputs = currentSystem.calculate(inputs);
                double musicMood = outputs.get("musicMood");

                System.out.println("\n" + "─".repeat(60));
                System.out.println("RECOMMENDATION RESULTS");
                System.out.println("─".repeat(60));
                System.out.println("Inputs:");
                System.out.println("  • Heart Rate: " + String.format("%.1f", heartRate) + " bpm");
                System.out.println("  • Facial Expression: " + String.format("%.1f", facialExpr) + " (" + interpretFacialExpression(facialExpr) + ")");
                System.out.println("  • Time of Day: " + formatTimeOfDay(timeOfDay) + " (" + interpretTimeOfDay(timeOfDay) + ")");

                System.out.println("\nFuzzified Membership Values:");
                Map<String, Map<String, Double>> fuzzified = currentSystem.getLastFuzzifiedInputs();
                for (Map.Entry<String, Map<String, Double>> varEntry : fuzzified.entrySet()) {
                    System.out.println("  " + varEntry.getKey() + ":");
                    for (Map.Entry<String, Double> setEntry : varEntry.getValue().entrySet()) {
                        if (setEntry.getValue() > 0.01) {
                            System.out.println("    - " + setEntry.getKey() + ": " +
                                    String.format("%.3f", setEntry.getValue()) +
                                    " " + createBar(setEntry.getValue()));
                        }
                    }
                }

                System.out.println("\n♪ MUSIC RECOMMENDATION:");
                System.out.println("  Mood Score: " + String.format("%.2f", musicMood) + "/100");
                System.out.println("  Category: " + interpretMusicMood(musicMood));
                System.out.println("  " + getMusicRecommendation(musicMood));
                System.out.println("─".repeat(60) + "\n");

            } catch (NumberFormatException e) {
                System.out.println("⚠ Invalid input. Please enter numeric values.\n");
            } catch (Exception e) {
                System.out.println("⚠ An error occurred: " + e.getMessage() + "\n");
            }
        }
    }

    // helpers
    private String createBar(double value) {
        int barLength = (int) (value * 20);
        return "[" + "█".repeat(barLength) + "░".repeat(20 - barLength) + "]";
    }

    private String formatTimeOfDay(double hour) {
        int h = (int) hour;
        int m = (int) ((hour - h) * 60);
        return String.format("%02d:%02d", h, m);
    }

    private String interpretFacialExpression(double value) {
        if (value < 3.5) return "Frown/Sad";
        else if (value < 6.5) return "Neutral";
        else return "Smile/Happy";
    }

    private String interpretTimeOfDay(double hour) {
        if (hour >= 0 && hour < 8) return "Night";
        else if (hour >= 8 && hour < 13) return "Morning";
        else if (hour >= 13 && hour < 18) return "Afternoon";
        else return "Evening";
    }

    private String interpretMusicMood(double mood) {
        if (mood < 25) return "Calm/Melancholic";
        else if (mood < 45) return "Chill/Happy";
        else if (mood < 65) return "Neutral/Background";
        else if (mood < 85) return "Moderate/Upbeat";
        else return "Energetic/Happy";
    }

    private String getMusicRecommendation(double mood) {
        if (mood < 25) {
            return "🎵 Recommendation: Ambient, Classical, Meditation music";
        } else if (mood < 45) {
            return "🎵 Recommendation: Indie, Acoustic, Lo-fi Hip Hop";
        } else if (mood < 65) {
            return "🎵 Recommendation: Jazz, Soft Rock, Easy Listening";
        } else if (mood < 85) {
            return "🎵 Recommendation: Pop, Dance, Upbeat Rock";
        } else {
            return "🎵 Recommendation: EDM, Fast Pop, High-Energy Dance";
        }
    }
}