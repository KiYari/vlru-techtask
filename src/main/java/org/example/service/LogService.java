package org.example.service;

import lombok.Getter;
import org.example.model.Log;
import org.example.util.IncorrentLogException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogService {
    public Double accessibility = 0.0;
    private Long currentLogsAccessible = 0L;
    private Long currentLogsUnaccessible = 0L;
    private LocalDateTime start = LocalDateTime.MIN;
    private LocalDateTime stop = LocalDateTime.MIN;
    private Double accessibilityMinimumForDropPeriod = 100.0;
    private Boolean shouldPrint = false;
    private final Long MINIMAL_OUTPUT_SAMPLING = 1L;

    private final Pattern pattern = Pattern.compile(
            "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})\\s-\\s-\\s\\[(.*?)\\]\\s\"(PUT|GET|POST|DELETE|HEAD)\\s(\\/[^?\\s]*)(\\?[^\"\\s]*)?\\sHTTP\\/1\\.1\"\\s(\\d{3})\\s(\\d+)\\s([\\d.]+)\\s\"-\"?\\s\"([^\"]*)\"?(\\sprio:\\d)?$",
            Pattern.DOTALL);
    public Log parseLog(String logString) {
        Matcher matcher = pattern.matcher(logString);
        Log log = new Log();

        if (matcher.matches()) {
            log.setIpAddress(matcher.group(1));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ss Z");
            log.setTimestamp(ZonedDateTime.parse(matcher.group(2), formatter.withZone(ZoneId.systemDefault())).toLocalDateTime());

            log.setRequestMethod(matcher.group(3));
            log.setUrl(matcher.group(4) + (matcher.group(5) != null ? "?" + matcher.group(5) : ""));
            log.setStatusCode(Integer.parseInt(matcher.group(6)));
            log.setResponseTime(Double.parseDouble(matcher.group(8)));
            log.setProcessName(matcher.group(9));
            log.setPriority(Integer.parseInt(matcher.group(10).substring(6)));
        } else {
            throw new IncorrentLogException("Incorrect log");
        }

        return log;
    }

    public void processLog(String log, Long timeout, Double targetAccessibility, Boolean isLast) {
        Log parsedLog = parseLog(log);
        if (parsedLog.getResponseTime() >= timeout || (parsedLog.getStatusCode() >= 500 && parsedLog.getStatusCode() < 600)) {
            tickAccessibility(false);
        } else {
            tickAccessibility(true);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        if (targetAccessibility > this.accessibility && start.equals(LocalDateTime.MIN)) {
            start = parsedLog.getTimestamp();
        }

        if (targetAccessibility > this.accessibility && this.accessibility < accessibilityMinimumForDropPeriod) {
            accessibilityMinimumForDropPeriod = this.accessibility;
        }

        if ((isLast &&  !start.equals(LocalDateTime.MIN) && !accessibilityMinimumForDropPeriod.equals(100.0) )
                || (targetAccessibility <= this.accessibility && !start.equals(LocalDateTime.MIN) && !accessibilityMinimumForDropPeriod.equals(100.0))) {
            stop = parsedLog.getTimestamp();
            shouldPrint = true;
        }

        if (isMinimalTimeBeforeLastOutputPassed() && shouldPrint) {
            shouldPrint = false;
            System.out.println(formatter.format(start) + " " + formatter.format(stop) + " " + (accessibilityMinimumForDropPeriod + this.accessibility)/2);
            start = LocalDateTime.MIN;
            accessibilityMinimumForDropPeriod = 100.0;
            stop = LocalDateTime.MIN;
        }
    }

    private Boolean isMinimalTimeBeforeLastOutputPassed() {
        return ChronoUnit.SECONDS.between(start, stop) > MINIMAL_OUTPUT_SAMPLING;
    }

    private void tickAccessibility(Boolean isAccessible) {
        if (isAccessible) {
            this.currentLogsAccessible++;
        } else {
            this.currentLogsUnaccessible++;
        }

        if (this.currentLogsUnaccessible.equals(0L)) {
            this.accessibility = 100.0;
        } else {
            this.accessibility = 100.0 * (currentLogsAccessible/(double)(currentLogsAccessible + currentLogsUnaccessible));
        }
    }

    public void refresh() {
        this.accessibility = 0.0;
        this.currentLogsAccessible = 0L;
        this.currentLogsUnaccessible = 0L;
    }
}
