package edu.lkinzler.servlet;

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

        // tokenize code
        String code = reqBodyStringBuilder.append(" eof ").toString().replaceAll("[\n]", "eol");
        StringTokenizer whiteSpaceTokenizer = new StringTokenizer(code, "\\w", false);


        // Use the txt file in the project root, stops tomcat from creating a different one
        String projectPath = System.getProperty("user.dir");
        File userInputFile = new File(projectPath, "User_Input.txt");
        FileWriter user_input = new FileWriter(userInputFile);

        user_input.write( reqBodyStringBuilder.toString() );

        // close
        user_input.close();
        reqBodyReader.close();

        // format response
        resp.setContentType("text/html");

        // write
        PrintWriter respBodyWriter = resp.getWriter();
        respBodyWriter.print( traceOutputBuilder.toString() );
        respBodyWriter.close();
	}
}
