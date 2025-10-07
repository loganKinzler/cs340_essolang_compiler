package edu.lkinzler.compiler;

import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.StringTokenizer;

public class Tokenizer {

    String code;

    public Tokenizer(String code) {
        this.code = code;
    }

    public String[] tokenize() {
        ArrayList<String> codeTokens = new ArrayList<String>();

        //The delimiters for the token: whitespace, periods, tabs, next line
        StringTokenizer tokenizer =  new StringTokenizer(code, "\t\n ", false);

        while(tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();

            if (!token.matches(".*[~`!@#$%^&*\\-_=+()\\[\\]{}:;'\",.<>/?\\\\|].*")) {
                codeTokens.add(token);
                continue;
            }

            StringTokenizer opperationTokenizer = new StringTokenizer(token,
                    "~`!@#$%^&*-_=+()[]{}:;'\",.<>/?\\|", true);

            while (opperationTokenizer.hasMoreTokens())
                codeTokens.add( opperationTokenizer.nextToken() );
        }

        return (String[])codeTokens.toArray();
    }
}
