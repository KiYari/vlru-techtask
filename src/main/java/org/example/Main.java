package org.example;

import org.example.service.LogService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {

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
//                System.out.println("IM PROCESSING: ");
//                System.out.println(line);
//                System.out.println("ACCESSIBS: " + logService.accessibility + " " + logService.getCurrentLogsUnaccessible() + " " + logService.getCurrentLogsAccessible());
                logService.processLog(line, timeout, accessibility, false);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occured while reading log lines");
        }

    }

    private Boolean isEndOfFile(BufferedReader reader) {
        return true;
    }
}