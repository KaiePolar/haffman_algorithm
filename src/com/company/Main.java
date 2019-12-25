package com.company;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    private static final String PRETEXT_PATH = "pretext.txt";
    private static final String PRETEXT_BYTES_PATH = "pretextinbytes.txt";
    private static final String POSTTEXT_BYTES_PATH = "posttextbytes.txt";
    private static final String POSTTEXT_PATH = "posttext.txt";
    private static Encoder encoder;

    public static void main(String[] args) {
        encoder = new Encoder();
        try {
            String initString = Files.readAllLines(Paths.get(PRETEXT_PATH), StandardCharsets.UTF_8).toString();
            encoder.startCompressing(initString);

            PrintWriter initialStringInBytes = new PrintWriter(PRETEXT_BYTES_PATH);
            initialStringInBytes.print(encoder.initialStringToByteString(initString));
            initialStringInBytes.close();

            PrintWriter decoded = new PrintWriter(POSTTEXT_BYTES_PATH);
            decoded.print(encoder.getCodes()+encoder.getString());
            decoded.close();

            PrintWriter decoded1 = new PrintWriter(POSTTEXT_PATH);
            decoded1.print(encoder.decode(POSTTEXT_BYTES_PATH));
            decoded1.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO Error");
        }
    }

}
