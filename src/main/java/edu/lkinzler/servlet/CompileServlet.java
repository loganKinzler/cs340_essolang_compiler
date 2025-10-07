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

import java.util.StringJoiner;
import java.util.StringTokenizer;

import edu.lkinzler.graphics.GraphicalInterpreter;


public class CompileServlet extends HttpServlet {

    private String tokenize(String code) {
        StringJoiner tokenJoiner = new StringJoiner("\n", "", "");

        //The delimiters for the token: whitespace, periods, tabs, next line
        StringTokenizer tokenizer =  new StringTokenizer(code, "\t\n ", false);

        while(tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();

            if (!token.matches(".*[~`!@#$%^&*\\-_=+()\\[\\]{}:;'\",.<>/?\\\\|].*")) {
                tokenJoiner.add(token);
                continue;
            }

            StringTokenizer opperationTokenizer = new StringTokenizer(token,
                    "~`!@#$%^&*-_=+()[]{}:;'\",.<>/?\\|", true);

            while (opperationTokenizer.hasMoreTokens())
                tokenJoiner.add( opperationTokenizer.nextToken() );
        }

        return tokenJoiner.toString();
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
        String tokenString = tokenize(code);


        // Use the txt file in the project root, stops tomcat from creating a different one
        String projectPath = System.getProperty("user.dir");

        File userInputFile = new File(projectPath, "User_Input.txt");
        FileWriter user_input = new FileWriter(userInputFile);
        user_input.write( reqBodyStringBuilder.toString() );

        File tokenFile = new File(projectPath, "User_Tokens.txt");
        FileWriter tokenWriter = new FileWriter(tokenFile);
        tokenWriter.write( tokenString );

        // close
        reqBodyReader.close();
        user_input.close();
        tokenWriter.close();


        // format response
        resp.setContentType("text/html");

        // write
        PrintWriter respBodyWriter = resp.getWriter();
        respBodyWriter.print( traceOutputBuilder.toString());
        respBodyWriter.close();
	}
}