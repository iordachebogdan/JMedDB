package service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class LoggingService {
    private static final String logPath = "logs/log.csv";

    private static LoggingService instance = null;
    private CSVPrinter csvPrinter;

    //singleton pattern
    private LoggingService() {
        File f = new File(logPath);

        if (f.exists()) {
            try {
                this.csvPrinter = new CSVPrinter(new FileWriter(f, true), CSVFormat.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                this.csvPrinter = new CSVPrinter(new FileWriter(f),
                        CSVFormat.DEFAULT.withHeader(
                                "timestamp",
                                "operation",
                                "controller",
                                "details",
                                "username",
                                "status"
                        ));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static LoggingService getInstance() {
        if (instance == null)
            instance = new LoggingService();
        return instance;
    }

    public enum Operation {
        INFO, CREATE, DELETE, READ, UPDATE, AUTHENTICATE
    }

    public enum Status {
        OK, BAD_REQUEST, NOT_FOUND, UNAUTHORIZED
    }

    public static class LogEntry {
        private Date timestamp;
        private Operation operation;
        private String controller;
        private String details;

        public LogEntry(Operation operation, String controller, String details) {
            this.timestamp = new Date();
            this.operation = operation;
            this.controller = controller;
            this.details = details;
        }
    }

    public void log(LogEntry entry, String username, Status status) {
        try {
            csvPrinter.printRecord(
                    entry.timestamp,
                    entry.operation,
                    entry.controller,
                    entry.details,
                    username,
                    status.name()
            );
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
