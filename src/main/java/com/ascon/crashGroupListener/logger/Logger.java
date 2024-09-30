package com.ascon.crashGroupListener.logger;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.ascon.crashGroupListener.constants.Config.*;

public class Logger{
    private final String loggingClass;
    private final String logFile;
    private final DateTimeFormatter logDateFormat;

    public Logger(Class loggingClass, String directory){
        this.loggingClass = loggingClass.toString();
        this.logFile = directory + File.separatorChar + LOG_FILE;
        this.logDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public void write(String message) {
        writeLog(String.format("[ %s ] [ %s ] : %s. %s",
                LocalDateTime.now().format(this.logDateFormat),
                loggingClass,
                message,
                System.lineSeparator()));
    }

    public void error(String message, Exception ex) {
        writeLog(String.format("[ %s ] [ %s ] : %s. %s %s",
                LocalDateTime.now().format(this.logDateFormat),
                loggingClass,
                message,
                ex.getMessage(),
                System.lineSeparator()));
    }

    private void writeLog(String logMessage){
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(logMessage);
        } catch (IOException e) {
            System.err.println("Error writing to log: " + e.getMessage());
        }
    }
}