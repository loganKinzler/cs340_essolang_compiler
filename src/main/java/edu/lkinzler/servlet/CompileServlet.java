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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;

import edu.lkinzler.graphics.GraphicalInterpreter;


public class CompileServlet extends HttpServlet {

    private ArrayList<String> tokenize(String code) {
        ArrayList<String> tokenList = new ArrayList<String>();

        //The delimiters for the token: whitespace, periods, tabs, next line
        StringTokenizer tokenizer =  new StringTokenizer(code, "\t\n ", false);
        while(tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();

            if (!token.matches(".*[~`!@#$%^&*\\-_=+()\\[\\]{}:;'\",.<>/?\\\\|].*")) {
                tokenList.add(token);
                continue;
            }

            StringTokenizer opperationTokenizer = new StringTokenizer(token,
                    "~`!@#$%^&*-_=+()[]{}:;'\",.<>/?\\|", true);

            while (opperationTokenizer.hasMoreTokens())
                tokenList.add( opperationTokenizer.nextToken() );
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

        //Creating a list of keywords and operators
        /*
        List<String> keywords = new ArrayList<>();
        keywords.add("if");
        keywords.add("else");
        keywords.add("for");
        keywords.add("while");
        keywords.add("do");
         */
        Map<String,Integer> keywords = new HashMap<>();
        keywords.put("if", 100);
        keywords.put("else", 101);
        keywords.put("for", 102);
        keywords.put("while", 103);
        keywords.put("do", 104);

        Map<String,Integer> operators = new HashMap<>();
        operators.put("+", 200);
        operators.put("-", 201);
        operators.put("*", 202);
        operators.put("/", 203);
        operators.put("=", 204);


        //This is the arraylist that will contain the operators and keywords for the tokens
        List<Integer> code_printout = new ArrayList<>();
//        tokenListIterator =
        Iterator<String> tokensIterator = tokens.iterator();

        while(tokensIterator.hasNext()) {
            String token = tokensIterator.next();
            if(keywords.containsKey(token)){
                code_printout.add(keywords.get(token));

            }

            if(operators.containsKey(token)){
                code_printout.add(operators.get(token));
            }

            //This deals with end of line and end of function.
            //They will just be entered as 0 into the arraylist
            if(token.equals("EOL") || token.equals("EOF")){
                code_printout.add(0);
            }
        }

        //Print out the code range for each token
        for(int i =0; i<code_printout.size();i++){
            if(code_printout.get(i) == 0){
                System.out.print("\n");
            }
            System.out.print(code_printout.get(i));
        }




        // Use the txt file in the project root, stops tomcat from creating a different one
        String projectPath = System.getProperty("user.dir");

        File userInputFile = new File(projectPath, "User_Input.txt");
        FileWriter user_input = new FileWriter(userInputFile);
        user_input.write( reqBodyStringBuilder.toString() );

        File tokenFile = new File(projectPath, "User_Tokens.txt");
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
