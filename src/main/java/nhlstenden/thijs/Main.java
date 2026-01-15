package nhlstenden.thijs;


import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static Random random = new Random();
    public static void main(String[] args) {
        System.out.println("-------------------------");
        System.out.println("Digital Twin - Database Benchmark");
        System.out.println("-------------------------");

        // dummy data
        ArrayList<Bouwblok> bouwblokken = new ArrayList<>();
        System.out.println("Dummy data genereren...");
        for (int i = 0; i < 50000; i++) {
            Bouwblok bouwblok = new Bouwblok("huis", random(), random(), random(), random(), random(), random());
            bouwblokken.add(bouwblok);
        }
        System.out.println("Dummy data gereed!");
        System.out.println("-------------------------");
        System.out.println("CONNECTIES:");
        MongoBenchmark mongo = new MongoBenchmark();
        mongo.init();

        PostgresBenchmark postgres = new PostgresBenchmark();
        postgres.init();

        SqlBenchmark sql = new SqlBenchmark();
        sql.init();
        System.out.println("-------------------------");
        System.out.println("Benchmark #1 - Opslaan van 50.000 bouwblokken");

        System.out.println("MongoDB (1/3)");
        double mongo1 = mongo.insert(bouwblokken);

        System.out.println("PostgresSQL (2/3)");
        double postgres1 = postgres.insert(bouwblokken);

        System.out.println("MySQL (3/3)");
        double mysql1 = sql.insert(bouwblokken);

        System.out.println("-------------------------");

        System.out.println("Benchmark #2 - Terughalen van 50.000 bouwblokken");

        System.out.println("MongoDB (1/3)");
        double mongo2 = mongo.retrieve();

        System.out.println("PostgresSQL (2/3)");
        double postgres2 = postgres.retrieve();

        System.out.println("MySQL (3/3)");
        double mysql2 = sql.retrieve();

        System.out.println("-------------------------");
        System.out.println("Benchmark #3 - Aanpassen van 50.000 bouwblokken");
        System.out.println("MongoDB (1/3)");
        double mongo3 = mongo.modify();

        System.out.println("PostgresSQL (2/3)");
        double postgres3 = postgres.modify();

        System.out.println("MySQL (3/3)");
        double mysql3 = sql.modify();

        System.out.println("-------------------------");
        System.out.println("RESULTATEN PER BENCHMARK:");

// --- Benchmark #1: Insert ---
        System.out.println("Benchmark #1 - Opslaan van 50.000 bouwblokken");
        double[] insertTimes = {mongo1, postgres1, mysql1};
        String[] dbNames = {"MongoDB", "PostgresSQL", "MySQL"};

// Find fastest
        int fastestIndex = 0;
        for (int i = 1; i < insertTimes.length; i++) {
            if (insertTimes[i] < insertTimes[fastestIndex]) fastestIndex = i;
        }
        System.out.printf("Snelste: %s (%.3f seconden)%n", dbNames[fastestIndex], insertTimes[fastestIndex]);

// --- Benchmark #2: Retrieve ---
        System.out.println("Benchmark #2 - Ophalen van 50.000 bouwblokken");
        double[] retrieveTimes = {mongo2, postgres2, mysql2};
        fastestIndex = 0;
        for (int i = 1; i < retrieveTimes.length; i++) {
            if (retrieveTimes[i] < retrieveTimes[fastestIndex]) fastestIndex = i;
        }
        System.out.printf("Snelste: %s (%.3f seconden)%n", dbNames[fastestIndex], retrieveTimes[fastestIndex]);

// --- Benchmark #3: Modify ---
        System.out.println("Benchmark #3 - Aanpassen van 50.000 bouwblokken");
        double[] modifyTimes = {mongo3, postgres3, mysql3};
        fastestIndex = 0;
        for (int i = 1; i < modifyTimes.length; i++) {
            if (modifyTimes[i] < modifyTimes[fastestIndex]) fastestIndex = i;
        }
        System.out.printf("Snelste: %s (%.3f seconden)%n", dbNames[fastestIndex], modifyTimes[fastestIndex]);

// --- Overall ranking ---
        System.out.println("-------------------------");
        System.out.println("OVERALL RANKING (totaal tijd per database)");

// Sum times per DB
        double[] totalTimes = new double[3];
        for (int i = 0; i < 3; i++) {
            totalTimes[i] = insertTimes[i] + retrieveTimes[i] + modifyTimes[i];
        }

// Simple sorting by total time (ascending)
        Integer[] indices = {0, 1, 2};
        java.util.Arrays.sort(indices, (a, b) -> Double.compare(totalTimes[a], totalTimes[b]));

        for (int rank = 0; rank < indices.length; rank++) {
            int i = indices[rank];
            System.out.printf("%d. %s - %.3f seconden%n", rank + 1, dbNames[i], totalTimes[i]);
        }

    }

    public static int random() {
        return random.nextInt(0, 50000);
    }
}