package com.nt.js;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TaxCalculate {

    private static final double SLAB_1_RATE = 0.05;
    private static final double SLAB_2_RATE = 0.1;
    private static final double SLAB_3_RATE = 0.15;
    private static final double SLAB_4_RATE = 0.20;
    private static final double SLAB_5_RATE = 0.25;
    private static final double SLAB_6_RATE = 0.30;

    private static final double SLAB_1_THRESHOLD = 250000;
    private static final double SLAB_2_THRESHOLD = 500000;
    private static final double SLAB_3_THRESHOLD = 750000;
    private static final double SLAB_4_THRESHOLD = 1000000;
    private static final double SLAB_5_THRESHOLD = 1250000;
    private static final double SLAB_6_THRESHOLD = 1500000;

    private static final Map<Double, Double> slabRates;

    static {
        slabRates = new HashMap<>();
        slabRates.put(SLAB_2_THRESHOLD, SLAB_1_RATE);
        slabRates.put(SLAB_3_THRESHOLD, SLAB_2_RATE);
        slabRates.put(SLAB_4_THRESHOLD, SLAB_3_RATE);
        slabRates.put(SLAB_5_THRESHOLD, SLAB_4_RATE);
        slabRates.put(SLAB_6_THRESHOLD, SLAB_5_RATE);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter employee name: ");
        String name = scanner.nextLine();

        System.out.print("Enter employee email id: ");
        String email = scanner.nextLine();
        // Validate email id using appropriate logic

        System.out.print("Enter employee gross total income: ");
        double income = scanner.nextDouble();

        double tds = calculateTax(income);
        System.out.println("Total Tax Amount: Rs. " + tds);

        storeRequestHistory(name, email, income, tds);

        System.out.println("Request history stored for " + name + " (" + email + ")");
    }

    private static double calculateTax(double income) {
        double totalTax = 0;

        if (income <= SLAB_1_THRESHOLD) {
            totalTax = 0;
        } else if (income <= SLAB_2_THRESHOLD) {
            totalTax = (income - SLAB_1_THRESHOLD) * slabRates.get(SLAB_2_THRESHOLD);
        } else if (income <= SLAB_3_THRESHOLD) {
            totalTax = (SLAB_2_THRESHOLD - SLAB_1_THRESHOLD) * slabRates.get(SLAB_2_THRESHOLD)
                    + (income - SLAB_2_THRESHOLD) * slabRates.get(SLAB_3_THRESHOLD);
        } else if (income <= SLAB_4_THRESHOLD) {
            totalTax = (SLAB_2_THRESHOLD - SLAB_1_THRESHOLD) * slabRates.get(SLAB_2_THRESHOLD)
                    + (SLAB_3_THRESHOLD - SLAB_2_THRESHOLD) * slabRates.get(SLAB_3_THRESHOLD)
                    + (income - SLAB_3_THRESHOLD) * slabRates.get(SLAB_4_THRESHOLD);
        } else if (income <= SLAB_5_THRESHOLD) {
            totalTax = (SLAB_2_THRESHOLD - SLAB_1_THRESHOLD) * slabRates.get(SLAB_2_THRESHOLD)
                    + (SLAB_3_THRESHOLD - SLAB_2_THRESHOLD) * slabRates.get(SLAB_3_THRESHOLD)
                    + (SLAB_4_THRESHOLD - SLAB_3_THRESHOLD) * slabRates.get(SLAB_4_THRESHOLD)
                    + (income - SLAB_4_THRESHOLD) * slabRates.get(SLAB_5_THRESHOLD);
        } else {
            totalTax = (SLAB_2_THRESHOLD - SLAB_1_THRESHOLD) * slabRates.get(SLAB_2_THRESHOLD)
                    + (SLAB_3_THRESHOLD - SLAB_2_THRESHOLD) * slabRates.get(SLAB_3_THRESHOLD)
                    + (SLAB_4_THRESHOLD - SLAB_3_THRESHOLD) * slabRates.get(SLAB_4_THRESHOLD)
                    + (SLAB_5_THRESHOLD - SLAB_4_THRESHOLD) * slabRates.get(SLAB_5_THRESHOLD)
                    + (income - SLAB_5_THRESHOLD) * SLAB_6_RATE;
        }

        return totalTax;
    }

    private static void storeRequestHistory(String name, String email, double income, double tds) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Akangkshit", "postgres", "stalin");

            String sql = "INSERT INTO public.\"EmployeeList\" (name, email, income, tds) VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setDouble(3, income);
            statement.setDouble(4, tds);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
