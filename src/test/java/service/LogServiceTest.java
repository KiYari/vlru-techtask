package service;

import nl.altindag.console.ConsoleCaptor;
import org.example.service.LogService;
import org.example.util.IncorrentLogException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LogServiceTest {
    List<String> logs = new ArrayList<>();
    List<String> incorrectLogs = new ArrayList<>();
    LogService logService = new LogService();

    @BeforeEach
    public void setup() {
        logs.add("192.168.32.181 - - [14/06/2017:16:48:00 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=4a18e7b7 HTTP/1.1\" 200 2 14.488751 \"-\" \"@list-item-updater\" prio:0");
        logs.add("192.168.32.181 - - [14/06/2017:16:48:05 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=8204439 HTTP/1.1\" 200 2 17.874763 \"-\" \"@list-item-updater\" prio:0");
        logs.add("192.168.32.181 - - [14/06/2017:16:48:10 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=e1e3f391 HTTP/1.1\" 200 2 13.85702 \"-\" \"@list-item-updater\" prio:0");
        logs.add("192.168.32.181 - - [14/06/2017:16:48:15 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=4a18e7b7 HTTP/1.1\" 200 2 14.488751 \"-\" \"@list-item-updater\" prio:0");
        logs.add("192.168.32.181 - - [14/06/2017:16:48:20 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=8204439 HTTP/1.1\" 200 2 17.874763 \"-\" \"@list-item-updater\" prio:0");
        logs.add("192.168.32.181 - - [14/06/2017:16:48:25 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=e1e3f391 HTTP/1.1\" 200 2 13.85702 \"-\" \"@list-item-updater\" prio:0");

        logs.add("192.168.32.181 - - [14/06/2017:16:48:15 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=e1e3f391 HTTP/1.1\" 501 2 12.85702 \"-\" \"@list-item-updater\" prio:0");
        logs.add("192.168.32.181 - - [14/06/2017:16:48:18 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=e1e3f391 HTTP/1.1\" 200 2 45.85702 \"-\" \"@list-item-updater\" prio:0");
        logs.add("192.168.32.181 - - [14/06/2017:16:48:19 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=e1e3f391 HTTP/1.1\" 200 2 45.0 \"-\" \"@list-item-updater\" prio:0");

        incorrectLogs.add("apple");
        incorrectLogs.add("666.666.6666.666666 - - [14/06/2017:16:48:52 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=e1e3f391 HTTP/1.1\" 200 2 13.85702 \"-\" \"@list-item-updater\" prio:0");
        incorrectLogs.add("192.168.32.181 - - [14/06/2017:16:48:52 +1000] \"@list-item-updater\" prio:0");
    }

    @Test
    public void testParseLog_shouldThrowException() {
        assertThrows(IncorrentLogException.class,() -> logService.parseLog(incorrectLogs.get(0)));
    }

    @Test
    public void testParseLog_shouldReturnValidLog() {
        assertNotNull(logService.parseLog(logs.get(0)));
    }

    @Test
    public void testProcessLog_shouldReturn100Accessibility() {
        logService.processLog(logs.get(0), 45L, 75.0, false);
        logService.processLog(logs.get(1), 45L, 75.0, false);
        logService.processLog(logs.get(2), 45L, 75.0, true);

        assertEquals(logService.accessibility, 100L);
    }

    @Test
    public void testProcessLog_shouldNotReturn100Accessibility() {
        logService.processLog(logs.get(0), 45L, 75.0, false);
        logService.processLog(logs.get(1), 45L, 75.0, false);
        logService.processLog(logs.get(6), 45L, 75.0, true);

        assertNotEquals(logService.accessibility, 100L);
    }

    @Test
    public void testProcessLog_shouldReturn0Accessibility() {
        logService.processLog(logs.get(6), 45L, 75.0, false);
        logService.processLog(logs.get(7), 45L, 75.0, false);
        logService.processLog(logs.get(8), 45L, 75.0, true);

        assertEquals(logService.accessibility, 0.0);
    }

    @Test
    public void testAnalyzeLog_shouldOutputNothing() {
        logService.processLog(logs.get(0), 45L, 75.0, false);
        logService.processLog(logs.get(1), 45L, 75.0, false);
        logService.processLog(logs.get(2), 45L, 75.0, false);
        logService.processLog(logs.get(3), 45L, 75.0, true);

        try (ConsoleCaptor cc = new ConsoleCaptor()){
            cc.getStandardOutput()
                    .forEach(str -> assertEquals("", str));
        }
    }

    @Test
    public void testProcessLog_shouldOutputAccessibility() {
        try (ConsoleCaptor cc = new ConsoleCaptor()){
            logService.processLog(logs.get(0), 45L, 75.0, false);
            logService.processLog(logs.get(0), 45L, 75.0, false);
            logService.processLog(logs.get(1), 45L, 75.0, false);
            logService.processLog(logs.get(2), 45L, 75.0, false);
            logService.processLog(logs.get(6), 45L, 75.0, false);
            logService.processLog(logs.get(7), 45L, 75.0, false);
            logService.processLog(logs.get(4), 45L, 75.0, false);
            logService.processLog(logs.get(5), 45L, 75.0, false);
            logService.processLog(logs.get(5), 45L, 75.0, false);
            logService.processLog(logs.get(5), 45L, 75.0, true);
            assertTrue(cc.getStandardOutput().contains("09:48:18 09:48:25 70.83333333333333"));

        }
    }

    @Test
    public void testProcessLog_shouldOutputAccessibilityOnEnd() {
        try (ConsoleCaptor cc = new ConsoleCaptor()){
            logService.processLog(logs.get(0), 45L, 75.0, false);
            logService.processLog(logs.get(1), 45L, 75.0, false);
            logService.processLog(logs.get(2), 45L, 75.0, false);
            logService.processLog(logs.get(6), 45L, 75.0, false);
            logService.processLog(logs.get(7), 45L, 75.0, false);
            logService.processLog(logs.get(4), 45L, 75.0, false);
            logService.processLog(logs.get(5), 45L, 75.0, true);
            assertTrue(cc.getStandardOutput().contains("09:48:18 09:48:25 65.71428571428572"));

        }
    }
}
