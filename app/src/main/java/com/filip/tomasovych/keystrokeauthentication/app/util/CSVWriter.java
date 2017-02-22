package com.filip.tomasovych.keystrokeauthentication.app.util;

import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Created by nolofinwe on 22/02/17.
 */

public class CSVWriter {

    private final static String TAG = CSVWriter.class.getSimpleName();

    private static final char DEFAULT_SEPARATOR = ',';

    public static void writeLine(FileOutputStream w, List<String> values) throws IOException {
        writeLine(w, values, DEFAULT_SEPARATOR, ' ');
    }

    public static void writeLine(FileOutputStream w, List<String> values, char separators) throws IOException {
        writeLine(w, values, separators, ' ');
    }


    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    public static void writeLine(FileOutputStream w, List<String> values, char separators, char customQuote) throws IOException {

        boolean first = true;

        //default customQuote is empty

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        w.write(sb.toString().getBytes());


    }

}