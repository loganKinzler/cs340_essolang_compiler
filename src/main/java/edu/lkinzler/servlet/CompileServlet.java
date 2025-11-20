package edu.lkinzler.servlet;


/********************************************************************
 * CS340 Esolang Compiler                                           *
 *                                                                  *
 * PROGRAMMER: Evan Natale & Logan Kinzler                          *
 * COURSE: CS340 - Programming Language Design                      *
 * DATE: 11/06/25                                                   *
 * REQUIREMENT: 7                                                   *
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


import java.util.*;

import java.util.List;
import java.util.ArrayList;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.lkinzler.compiler.TraceOutputBuilder;
import edu.lkinzler.compiler.Validator;

import edu.lkinzler.graphics.GraphicalInterpreter;

import edu.lkinzler.utility.CSVReader;
import edu.lkinzler.utility.Pair;
import edu.lkinzler.utility.functions.ConstantCategorizer;
import edu.lkinzler.utility.functions.InstructionCategorizer;
import edu.lkinzler.utility.functions.OpperationCategorizer;
import edu.lkinzler.utility.functions.VariableCategorizer;



public class CompileServlet extends HttpServlet {

    /***********************************************************
     * METHOD: encode                                          *
     * DESCRIPTION: This takes in the tokens and a mapping     *
     *     from the token to a unique number.                  *
     * PARAMETERS: HashMap<String, Integer> encodingTable,     *
     *     ArrayList<String> tokens                            *
     * RETURN VALUE: ArrayList<Integer>                        *
     **********************************************************/

    private ArrayList<Integer> encode(HashMap<String, Integer> encodingTable, ArrayList<String> tokens) {
        ArrayList<Integer> codes = new ArrayList<Integer>();
        Iterator<String> tokensIterator = tokens.iterator();
        Integer newVariableEncodingNumber = 300;
        Integer newConstantEncodingNumber = 600;

        while (tokensIterator.hasNext()) {
            String token = tokensIterator.next();

            // keyword, opperation, or old variable/constant
            if(encodingTable.containsKey(token)) {
                codes.add(encodingTable.get(token));
                continue;
            }

            // new variable
            try {Integer.parseInt(token);}
            catch (NumberFormatException nfe) {

                // variables won't get parsed to an integer properly
                encodingTable.put(token, newVariableEncodingNumber);
                codes.add(newVariableEncodingNumber);
                newVariableEncodingNumber++;
                continue;
            }

            // new constant
            encodingTable.put(token, newConstantEncodingNumber);
            codes.add(newConstantEncodingNumber);
            newConstantEncodingNumber++;
        }

        return codes;
    }


    /***********************************************************
     * METHOD: tokenize                                        *
     * DESCRIPTION: This takes in the essolang code in its     *
     *     entirety and returns it as a list of tokens.        *
     * PARAMETERS: String code                                 *
     * RETURN VALUE: ArrayList<String>                         *
     **********************************************************/

    private ArrayList<String> tokenize(String code) {
        ArrayList<String> tokenList = new ArrayList<String>();

        //The delimiters for the token: whitespace, periods, tabs, next line
        StringTokenizer tokenizer =  new StringTokenizer(code, "\t\n ", false);
        while(tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();

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
     *     tokenizes, encodes, and verifies the structure of   *
     *     the given essolang code, responding with a trace of *
     *     the compiled code.                                  *
     * PARAMETERS: HttpServletRequest q, HttpServletResponse p *
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
        TraceOutputBuilder traceOutput = new TraceOutputBuilder();
        int lineNum = 1;

        while (line != null) {
            traceOutput.addNewLine(line);
            lineNum++;

            reqBodyStringBuilder.append(line);
            reqBodyStringBuilder.append("\n");
            line = reqBodyReader.readLine();
        }


        // remove the last line (which is empty)
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

        Integer outputLine = 0;
        for (String token : tokens)
        {
            if (token.equals("EOL") || token.equals("EOF"))
                outputLine++;
            else
                traceOutput.addTokenToLine(token, outputLine);

            tokenJoiner.add(token);
        }

        // import encoding table
        File encodingTableFile = new File(projectPath, "Encoding_Table.csv");
        CSVReader encodingTableReader = new CSVReader(encodingTableFile);
        HashMap<String, Integer> encodingTable = encodingTableReader.interpretAsEncodingTable();
        encodingTableReader.close();

        // encode tokens to instructions
        ArrayList<Integer> instructions = encode(encodingTable, tokens);
        outputLine = 0;

        for (Integer instruct : instructions)
        {
            if (instruct == 0 || instruct == 1)
                outputLine++;
            else
                traceOutput.addInstructionToLine(instruct, outputLine);

        }


        // gather sequence labels
        ArrayList<InstructionCategorizer> instructionCategories = new ArrayList<InstructionCategorizer>();
        instructionCategories.add(new OpperationCategorizer());
        instructionCategories.add(new VariableCategorizer());
        instructionCategories.add(new ConstantCategorizer());

       // import CONO table
        File conoTableFile = new File(projectPath, "CONO_Table.csv");
        CSVReader conoTableReader = new CSVReader(conoTableFile);
        HashMap<Pair<Integer, Integer>, String> conoTable = conoTableReader.interpretAsCONOtable(encodingTable, instructionCategories);
        conoTableReader.close();

        List<String> codeGenerators = new ArrayList<>();


        // validate instruction set
        Validator instructionValidator = new Validator(encodingTable, conoTable, instructionCategories, traceOutput, codeGenerators);
        instructionValidator.validate(instructions);



        // save user input
        File userInputFile = new File(projectPath, "User_Input.txt");
        FileWriter user_input = new FileWriter(userInputFile);
        user_input.write( reqBodyStringBuilder.toString() );


        // save tokens
        File tokenFile = new File(projectPath, "User_Tokens.txt");
        FileWriter tokenWriter = new FileWriter(tokenFile);
        tokenWriter.write( tokenJoiner.toString() );


        // save instructions
        Iterator<Integer> instructionsIterator = instructions.iterator();
        StringJoiner instructionsString = new StringJoiner("\n");

        while (instructionsIterator.hasNext()) {
            Integer saveInstruction = instructionsIterator.next();

            if (!new ConstantCategorizer().withinCategory(saveInstruction))
            {
                instructionsString.add(saveInstruction.toString());
                continue;
            }

            // find token
            for (String token : encodingTable.keySet()){
                if (encodingTable.get(token) == saveInstruction)
                {
                    instructionsString.add(saveInstruction.toString() + " " + token);
                    break;
                }
            }

            instructionsString.add(instructionsIterator.next().toString());
        }

        File instructionsFile = new File(projectPath, "User_Instructions.txt");
        FileWriter instructionsWriter = new FileWriter(instructionsFile);
        instructionsWriter.write( instructionsString.toString() );


        //Save codeGenerators
        File codeGeneratorsFile = new File(projectPath, "codeGenerators.txt");
        FileWriter codeGenWriter = new FileWriter(codeGeneratorsFile);
        codeGenWriter.write(
                String.join("\n", instructionValidator.getCodeGenerators())
        );
        codeGenWriter.close();

        // close
        user_input.close();
        reqBodyReader.close();
        tokenWriter.close();
        instructionsWriter.close();


        // format response
        resp.setContentType("text/html");


        // write
        PrintWriter respBodyWriter = resp.getWriter();
        respBodyWriter.print( traceOutput );
        respBodyWriter.close();
	}
}
