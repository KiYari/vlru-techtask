package org.example.model;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Log {
    private String ipAddress;
    private LocalDateTime timestamp;
    private String requestMethod;
    private String url;
    private int statusCode;
    private double responseTime;
    private String processName;
    private int priority;
}
