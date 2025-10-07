package edu.lkinzler.servlet;


import edu.lkinzler.graphics.GraphicalInterpreter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import java.util.Scanner;


public class RunServlet extends HttpServlet {

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

        StringBuilder codeLine = new StringBuilder();
        StringBuilder graphicsHTML = new StringBuilder();
        StringBuilder graphicsAnimation = new StringBuilder();
        GraphicalInterpreter graphicsInterpreter = new GraphicalInterpreter();

        String projectPath = System.getProperty("user.dir");
        File tokenFile = new File(projectPath, "User_Tokens.txt");
        Scanner tokenScan = new Scanner(tokenFile);


        while (tokenScan.hasNextLine()) {
            String token = tokenScan.nextLine();

            if (token.equals("EOL") || token.equals("EOF")) {

                graphicsHTML.append(
                        graphicsInterpreter.interpretShape( codeLine.toString() )
                ).append("\\n");

//                if (graphicsInterpreter.isAnimation( codeLine.toString() ))
//                    graphicsHTML.append(
//                            graphicsInterpreter.interpretAnimation( codeLine.toString() )
//                    ).append("\\n");

                codeLine = new StringBuilder();
                continue;
            }

            codeLine.append(token).append(" ");
        }


        // format response
        resp.setContentType("application/json");

        // write
        PrintWriter respBodyWriter = resp.getWriter();
        respBodyWriter.print(
                "{\n\"HTML\":\n\"" +
                        graphicsHTML.toString() +
                        "\",\n\"Animation\":\n\"" +
//                        graphicsAnimation.toString() +
                        "\"\n}"
        );
        respBodyWriter.close();
	}
}