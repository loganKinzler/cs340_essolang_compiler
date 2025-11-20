package edu.lkinzler.servlet;


import edu.lkinzler.compiler.Validator;
import edu.lkinzler.graphics.GraphicalInterpreter;

import edu.lkinzler.utility.*;
import edu.lkinzler.utility.functions.*;
import edu.lkinzler.utility.opperations.*;
import org.junit.Ignore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;
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

        String projectPath = System.getProperty("user.dir");

        File instructionsFile = new File(projectPath, "User_Instructions.txt");
        Scanner instructionScanner = new Scanner(instructionsFile);

        File generatorsFile = new File(projectPath, "codeGenerators.txt");
        Scanner generatorScanner = new Scanner(generatorsFile);

        // setup for loop (#generators = #instructions - 1)
        if (!instructionScanner.hasNextLine())
        {
            // no code, do something with that later
            return;
        }

        // generator + instruction pair
        String currentGenerator;
        Integer previousInstruction = instructionScanner.nextInt();
        Integer currentInstruction;

        // hashmaps
        HashMap<Integer, Object> variableToValueMap = new HashMap<>();
        HashMap<Integer, EssolangOpperation> opperationsMap = new HashMap<>();
        opperationsMap.put(201, new IntegerAddition());
        opperationsMap.put(202, new IntegerSubtraction());
        opperationsMap.put(203, new IntegerMultiplication());
        opperationsMap.put(204, new IntegerDivision());

        // runtime trackers
        // TODO: make compile time trackers as well (with added functionality later [EXTREMELY IMPORTANT THEN])
        Integer setVariableInstruction = null;
        Object opperationCache = null;

        Integer firstOpperand = null;
        Integer opperationInstruciton = null;



        // run through instructions
        while (instructionScanner.hasNextLine() && generatorScanner.hasNextLine()) {
            currentInstruction = instructionScanner.nextInt();
            currentGenerator = generatorScanner.nextLine();

            // immediately evaluate constants
            if (new ConstantCategorizer().withinCategory(currentInstruction)) {
                variableToValueMap.put(currentInstruction,
                        Integer.parseInt(instructionScanner.next().trim())
                );
            }

            System.out.println("\n Prev: " + previousInstruction + " Curr: " + currentInstruction + " Gen: " + currentGenerator + "\n");

            switch (currentGenerator) {
                case "nop": break;

                case "var_init":
                    variableToValueMap.put(currentInstruction, null);
                break;

                case "start_set":
                    setVariableInstruction = previousInstruction;
                break;

                case "set_val":
                    opperationCache = variableToValueMap.get(currentInstruction);
                break;

                case "end_set":
                    variableToValueMap.put(setVariableInstruction, opperationCache);
                    setVariableInstruction = null;
                    opperationCache = null;
                break;

                case "start_opp":
                    firstOpperand = previousInstruction;
                    opperationInstruciton = currentInstruction;
                break;

                case "end_opp":
                    opperationCache = opperationsMap.get(previousInstruction).operate(
                            opperationCache,
                            variableToValueMap.get(currentInstruction)
                    );

                    firstOpperand = null;
                break;

                default:
                    // generator not found, shouldn't happen?
                break;
            }

            System.out.println("Variables:");
            for (Integer varInstruction : variableToValueMap.keySet()) {
                System.out.printf("    %d: %s\n",
                        varInstruction,
                        (variableToValueMap.get(varInstruction) == null)? "Null" :
                        variableToValueMap.get(varInstruction).toString());
            }

            previousInstruction = currentInstruction;
        }


        // format response
        resp.setContentType("application/json");

        // write
        PrintWriter respBodyWriter = resp.getWriter();
//        respBodyWriter.print();
        respBodyWriter.close();
	}


}