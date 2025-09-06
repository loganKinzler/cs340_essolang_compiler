package edu.lkinzler.servlet;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Scanner;


public class EsolangIDEServlet extends HttpServlet {
    @Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/esolang_IDE.jsp").forward(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // interpret request
        BufferedReader reqBodyReader = req.getReader();
        StringBuilder traceOutput = new StringBuilder();

        //Use the txt file in the project root, stops tomcat from creating a different one
        String projectPath = System.getProperty("user.dir");
        File userInputFile = new File(projectPath, "User_Input.txt");
        //Debug test
        System.out.println(userInputFile.getAbsolutePath());

        //Writing textarea string into a txt file
        try{
            FileWriter user_input = new FileWriter(userInputFile);

            String line = reqBodyReader.readLine();
            int lineNum = 1;

            while (line != null) {
                user_input.write(line);
                line = reqBodyReader.readLine();
                lineNum++;
            }
            user_input.close();
        }
        catch(IOException e){
            System.out.println("file was not found.");
        }

        //Reading contents of the txt file
        int lineNum = 1;
        try{
            //File User_Input = new File(projectPath, "User_Input.txt");
            Scanner read = new Scanner(userInputFile);
            while(read.hasNextLine()){
                traceOutput.append(String.format("Line %d: %s\n", lineNum, read.nextLine()));
                lineNum++;
            }
            read.close();
        }
        catch(FileNotFoundException e){
            System.out.println("file was not found.");
        }
        /*
        String line = reqBodyReader.readLine();
        int lineNum = 1;

        while (line != null) {
            traceOutput.append( String.format("Line %d: %s\n", lineNum, line) );
            line = reqBodyReader.readLine();
            lineNum++;
        }

        reqBodyReader.close();

         */

        // format response
        resp.setContentType("text/html");
        PrintWriter respBodyWriter = resp.getWriter();

        respBodyWriter.print( traceOutput.toString() );
        respBodyWriter.close();
	}
}
