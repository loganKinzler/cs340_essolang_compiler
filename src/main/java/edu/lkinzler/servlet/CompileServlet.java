package edu.lkinzler.servlet;


/********************************************************************
 * CS340 Esolang Compiler                                           *
 *                                                                  *
 * PROGRAMMER: Evan Natale & Logan Kinzler                          *
 * COURSE: CS340 - Programming Language Design                      *
 * DATE: 09/10/25                                                   *
 * REQUIREMENT: 3                                                   *
 *                                                                  *
 * DESCRIPTION:                                                     *
 * This is a dynamic webserver that hosts an online IDE for an      *
 * esoteric programming language.                                   *
 *                                                                  *
 * COPYRIGHT:                                                       *
 * This code is copyright (c)2025 Evan Natale, Logan Kinzler,       *
 * and Dean Zeller.                                                 *
 *                                                                  *
 * CREDITS:                                                         *
 * None                                                             *
 *                                                                  *
 *******************************************************************/


import java.io.IOException;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.util.Iterator;

import java.util.StringTokenizer;
import java.util.StringJoiner;

import java.util.List;
import java.util.ArrayList;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.lkinzler.graphics.GraphicalInterpreter;
import edu.lkinzler.utility.CSVReader;
import edu.lkinzler.utility.Pair;

public class CompileServlet extends HttpServlet {

    private HashMap<String, Integer> interpretAsEncodingTable(File csvFile) throws IOException {
        HashMap<String, Integer> encodingTable = new HashMap<String, Integer>();
        CSVReader csvReader = new CSVReader(csvFile);

        List<String> line = csvReader.next();
        Integer currentSequence = -1, lineNumber = 1;

        while (line != null) {
            switch (line.size()) {

                // sequence is continued
                case 1:

                    // check if sequence has been started and the string is not missing
                    if (currentSequence < 0)
                        throw new IOException("Missing sequence initialization in encoding table. (Line: " + lineNumber + ")");

                    // check if key is missing
                    try {
                        Integer.parseInt(line.get(0));
                    }
                    catch (NumberFormatException parsementError) {
                        // add as new key-value pair in sequence
                        encodingTable.put(line.get(0), currentSequence);
                        currentSequence++;
                        break;
                    }

                    // the key doesn't exist when the only value is an Integer (the class type of the value)
                    throw new IOException("Missing key value in encoding table. (Line: " + lineNumber + ")");


                // new sequence
                case 2:

                    // check if value is an integer
                    try {
                        currentSequence = Integer.parseInt(line.get(1));

                        // check if sequence number isn't in the encoding table already
                        if (encodingTable.containsValue(currentSequence))
                            throw new IOException("Key already exists in table (" +
                                    "Line: " + lineNumber + ")");

                        // add as new sequence
                        encodingTable.put(line.get(0), currentSequence);
                        currentSequence++;
                    }
                    catch (NumberFormatException parsementError) {
                        throw new IOException("Value in Key-Value pair is not of type Integer in encoding table. (Line: " + lineNumber + ")");
                    }
                break;
            }

            line = csvReader.next();
            lineNumber++;
        }

        csvReader.close();
        return encodingTable;
    }

    private HashMap<Pair<Integer, Integer>, String> interpretAsCONOtable(File csvFile, HashMap<String, Integer> encodingTable) throws IOException {
        HashMap<Pair<Integer, Integer>, String> conoTable = new HashMap<Pair<Integer, Integer>, String>();
        CSVReader csvReader = new CSVReader(csvFile);

        List<String> line = csvReader.next();
        Integer lineNumber = 1;

        while (line != null) {
            if (line.size() != 3)
                throw new IOException("Missing opperand or product in CONO table. (Line: " + lineNumber + ")");

            if ( !encodingTable.containsKey(line.get(0)) )
                throw new IOException("First opperand is not a valid token. (Line: " + lineNumber + ")");

            if ( !encodingTable.containsKey(line.get(1)) )
                throw new IOException("Second opperand is not a valid token. (Line: " + lineNumber + ")");

            conoTable.put(
                    new Pair<Integer, Integer>(
                            encodingTable.get(line.get(0)),
                            encodingTable.get(line.get(1))),
                    line.get(2));

            line = csvReader.next();
            lineNumber++;
        }

        csvReader.close();
        return conoTable;
    }

    private ArrayList<Integer> encode(HashMap<String, Integer> encodingTable, ArrayList<String> tokens) {
        ArrayList<Integer> codes = new ArrayList<Integer>();
        Iterator<String> tokensIterator = tokens.iterator();
        Integer newVariableEncodingNumber = 300;

        while (tokensIterator.hasNext()) {
            String token = tokensIterator.next();

            // keyword, opperation, or old variable
            if(encodingTable.containsKey(token))
                codes.add(encodingTable.get(token));

            // new variable
            encodingTable.put(token, newVariableEncodingNumber);
            newVariableEncodingNumber++;
        }

        return codes;
    }

    private ArrayList<String> tokenize(String code) {
        ArrayList<String> tokenList = new ArrayList<String>();

        //The delimiters for the token: whitespace, periods, tabs, next line
        StringTokenizer tokenizer =  new StringTokenizer(code, "\t\n ", false);
        while(tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();

            /*
            StringTokenizer opperationTokenizer = new StringTokenizer(token,
                    "~`!@#$%^&*-_=+()[]{}:;'\",.<>/?\\|", true);

            while (opperationTokenizer.hasMoreTokens())
                tokenList.add( opperationTokenizer.nextToken() );
        }
        */
            Pattern pattern = Pattern.compile("\\+=|-=|\\*=|/=|\\*\\*|==|!=|>=|<=|[A-Za-z_][A-Za-z0-9_]*|\\d+|\\S");
            Matcher matcher = pattern.matcher(token);

            while(matcher.find()){
                tokenList.add(matcher.group());
            }

        }

        return tokenList;
    }


    /***********************************************************
     * METHOD: doPost                                          *
     * DESCRIPTION: This receives HTTP POST requests &         *
     *     tokenizes the given esolang code, responding with   *
     *     a trace of the compiled code.                       *
     * PARAMETERS: HttpServletRequest, HttpServletResponse     *
     * RETURN VALUE: void                                      *
     **********************************************************/

    @Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Use the txt file in the project root, stops tomcat from creating a different one
        String projectPath = System.getProperty("user.dir");


        //Writing textarea string into a txt file
        BufferedReader reqBodyReader = req.getReader();
        String line = reqBodyReader.readLine();

        StringBuilder reqBodyStringBuilder = new StringBuilder();
        StringBuilder traceOutputBuilder = new StringBuilder();
        int lineNum = 1;

        while (line != null) {
            traceOutputBuilder.append( String.format("Line #%d: ", lineNum) );
            traceOutputBuilder.append(line);
            traceOutputBuilder.append("\n");
            lineNum++;

            reqBodyStringBuilder.append(line);
            reqBodyStringBuilder.append("\n");
            line = reqBodyReader.readLine();
        }



        // remove the last line (which is empty)
        traceOutputBuilder.deleteCharAt(traceOutputBuilder.length() - 1);
        reqBodyStringBuilder.deleteCharAt(reqBodyStringBuilder.length() - 1);


        //Save user input as a string into code
        String code = reqBodyStringBuilder.toString();
        //EOL will replace and next line's inside of code
        code = code.replace("\n", "\nEOL\n");
        //Add EOF at the end of code
        code += "\nEOF";

        // tokenize code
        ArrayList<String> tokens = tokenize(code);

        StringJoiner tokenJoiner = new StringJoiner("\n", "", "");
        for (String token : tokens)
            tokenJoiner.add(token);

        String tokenString = tokenJoiner.toString();


        // encode code
        File encodingTableFile = new File(projectPath, "Encoding_Table.csv");
        HashMap<String, Integer> encodingTable = interpretAsEncodingTable(encodingTableFile);
        Integer newVariableEncodingNumber = 300;

        // validate order of tokens
        File conoTableFile = new File(projectPath, "CONO_Table.csv");
        HashMap<Pair<Integer, Integer>, String> conoTable = interpretAsCONOtable(conoTableFile, encodingTable);

        //This is the arraylist that will contain the operators and keywords for the tokens
        List<Integer> code_printout = new ArrayList<>();

        // encode variables
        ArrayList<Integer> encodedTokens = encode(encodingTable, tokens);

        File userInputFile = new File(projectPath, "User_Input.txt");
        FileWriter user_input = new FileWriter(userInputFile);
        user_input.write( reqBodyStringBuilder.toString() );

        File tokenFile = new File(projectPath, "User_Encoded_Tokens.txt");
        FileWriter tokenWriter = new FileWriter(tokenFile);
        tokenWriter.write( tokenString );

        // close
        user_input.close();
        reqBodyReader.close();
        tokenWriter.close();

        // format response
        resp.setContentType("text/html");

        // write
        PrintWriter respBodyWriter = resp.getWriter();
        respBodyWriter.print( traceOutputBuilder.toString());
        respBodyWriter.close();
	}
}
