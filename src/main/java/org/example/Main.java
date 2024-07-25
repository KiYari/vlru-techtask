package org.example;

import org.example.service.LogService;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    private static String logToProcess = "";

    public static void main(String[] args) {
        if (args.length < 4) {
            throw new IllegalArgumentException("There is no -u or -t arguments");
        }
        Long timeout;
        Double accessibility;

        try {
            accessibility = Double.parseDouble(args[1]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Incorrect -u argument. \"" + args[1] + "\" is not a valid number");
        }

        try {
            timeout = Long.parseLong(args[3]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Incorrect -t argument. \"" + args[1] + "\" is not a valid number");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        LogService logService = new LogService();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (!logToProcess.isEmpty()) {
                    logService.processLog(logToProcess, timeout, accessibility, false);
                }
                logToProcess = line;
            }
            logService.processLog(logToProcess, timeout, accessibility, true);
        } catch (Exception e) {
            throw new RuntimeException("Error occured while reading log lines");
        }

    }
}