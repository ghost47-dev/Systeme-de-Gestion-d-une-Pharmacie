package com.gestionpharmacie.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Locale;

public class InputUtils {
    private static Scanner sc = new Scanner(System.in).useLocale(Locale.US);
    public static Date inputDate() {
        String input = sc.nextLine();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return sdf.parse(input);
        } catch (ParseException e) {
            System.err.println("Invalid date format!");
        }
        return null;
    }
    public static int readInt() {
        int result = sc.nextInt();
        sc.nextLine();
        return result;
    }
    public static double readDouble() {

        double result = sc.nextDouble();
        sc.nextLine();
        return result;
    }

}

