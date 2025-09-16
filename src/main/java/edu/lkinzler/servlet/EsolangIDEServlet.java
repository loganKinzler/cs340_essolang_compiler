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


import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.nio.file.Files;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.StringTokenizer;


public class EsolangIDEServlet extends HttpServlet {

    @Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/esolang_IDE.jsp").forward(req, resp);
	}
	
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

        /*
        // tokenize code
        String code = reqBodyStringBuilder.append(" eof ").toString().replaceAll("[\n]", "eol");
        StringTokenizer whiteSpaceTokenizer = new StringTokenizer(code, "\\w", false);
         */

        //Save user input as a string into code
        String code = reqBodyStringBuilder.toString();
        //EOL will replace and next line's inside of code
        code = code.replace("\n", "\nEOL\n");
        //Add EOF at the end of code
        code += "\nEOF";


        //Create a token
        //The delimiters for the token: whitespace, periods, tabs, next line
        StringTokenizer tokenizer =  new StringTokenizer(code, "\t\n ", false);
        StringJoiner tokenListJoiner = new StringJoiner("\n", "{", "}");

        while(tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();

            if (!token.matches(".*[~`!@#$%^&*\\-_=+()\\[\\]{}:;'\",.<>/?\\\\|].*")) {
                System.out.println(token);
                tokenListJoiner.add(token);
                continue;
            }

            StringTokenizer opperationTokenizer = new StringTokenizer(token,
                    "~`!@#$%^&*-_=+()[]{}:;'\",.<>/?\\|", true);

            while (opperationTokenizer.hasMoreTokens()) {
                String opperationToken = opperationTokenizer.nextToken();
                System.out.println(opperationToken);
                tokenListJoiner.add(opperationToken);
            }
        }

        // Use the txt file in the project root, stops tomcat from creating a different one
        String projectPath = System.getProperty("user.dir");

        File userInputFile = new File(projectPath, "User_Input.txt");
        FileWriter user_input = new FileWriter(userInputFile);
        user_input.write( reqBodyStringBuilder.toString() );

        File tokenFile = new File(projectPath, "User_Tokens.txt");
        FileWriter tokenWriter = new FileWriter(tokenFile);
        tokenWriter.write( tokenListJoiner.toString() );

        // close
        user_input.close();
        reqBodyReader.close();

        // format response
        resp.setContentType("text/html");

        // write
        PrintWriter respBodyWriter = resp.getWriter();
        //Also added text to indicate that the code is being "compiled"
        respBodyWriter.print( traceOutputBuilder.toString() + "\nCompiling..." );
        respBodyWriter.close();
	}
}
