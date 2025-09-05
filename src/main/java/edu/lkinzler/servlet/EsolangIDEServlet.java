package edu.lkinzler.servlet;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 import javax.servlet.http.HttpSession;


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

        String line = reqBodyReader.readLine();
        int lineNum = 1;

        while (line != null) {
            traceOutput.append( String.format("Line %d: %s<br>", lineNum, line) );
            line = reqBodyReader.readLine();
            lineNum++;
        }

        reqBodyReader.close();

        // format response
        resp.setContentType("text/html");
        PrintWriter respBodyWriter = resp.getWriter();

        respBodyWriter.print( traceOutput.toString() );
        respBodyWriter.close();
	}
}
