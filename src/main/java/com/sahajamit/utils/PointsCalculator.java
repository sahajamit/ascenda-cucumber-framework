package com.sahajamit.utils;

public class PointsCalculator {
    private static double pointsCoefficient = 0.75;

    public static double getAccruedPoints(double amount){
        return amount* pointsCoefficient;
    }

    public static int getRoundedAmount(double amount){
        int nearestLowestInteger = (int) Math.floor(amount);
        return nearestLowestInteger;
    }

    public static int getRoundedPoints(double amount){
        int nearestLowestInteger = (int) Math.floor(getAccruedPoints(amount));
        return nearestLowestInteger;
    }
}
