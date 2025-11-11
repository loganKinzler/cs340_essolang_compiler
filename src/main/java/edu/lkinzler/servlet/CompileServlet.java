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
     * METHOD: interpretAsEncodingTable                        *
     * DESCRIPTION: This takes in a CSV file that describes    *
     *     the mappings from valid tokens to instruction codes *
     *     and outputs the list of mappings.                   *
     * PARAMETERS: File csvFile,                               *
     *     HashMap<String, Integer> encodingTable,             *
     *     List<InstructionCategorizer> instructionCategories  *
     * RETURN VALUE: ArrayList<Pair<Integer, Intger>, String>> *
     **********************************************************/

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

                        // add as new variable
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


    /***********************************************************
     * METHOD: interpretAsCONOtable                            *
     * DESCRIPTION: This takes in a CSV file that describes    *
     *     the valid sequence of instructions for the provided *
     *     essolang.                                           *
     * PARAMETERS: File csvFile,                               *
     *     HashMap<String, Integer> encodingTable,             *
     *     List<InstructionCategorizer> instructionCategories  *
     * RETURN VALUE: HashMap<Pair<Integer, Integer>, String>   *
     **********************************************************/

    private HashMap<Pair<Integer, Integer>, String> interpretAsCONOtable(File csvFile, HashMap<String, Integer> encodingTable, List<InstructionCategorizer> instructionCategories) throws IOException {
        HashMap<Pair<Integer, Integer>, String> conoTable = new HashMap<Pair<Integer, Integer>, String>();
        CSVReader csvReader = new CSVReader(csvFile);

        List<String> line = csvReader.next();
        Integer lineNumber = 1;

        while (line != null) {
            if (line.size() != 3)
                throw new IOException("Missing opperand or product in CONO table. (Line: " + lineNumber + ")");

            List<String> finalLine = line;
            if ( !encodingTable.containsKey(line.get(0)) &&
                    instructionCategories.stream().noneMatch(
                            (InstructionCategorizer category) -> category.getLabel().equals(finalLine.get(0))))
                throw new IOException("First opperand is not a valid token. (Line: " + lineNumber + ")");

            if ( !encodingTable.containsKey(line.get(1)) &&
                    instructionCategories.stream().noneMatch(
                            (InstructionCategorizer category) -> category.getLabel().equals(finalLine.get(1))))
                throw new IOException("Second opperand is not a valid token. (Line: " + lineNumber + ")");



            Integer encodeOne = null, encodeTwo = null;

            for (InstructionCategorizer category : instructionCategories) {
                if (category.getLabel().equals(line.get(0)))
                    encodeOne = category.getInstruction();

                if (category.getLabel().equals(line.get(1)))
                    encodeTwo = category.getInstruction();
            }

            if (encodeOne == null)
                encodeOne = encodingTable.get(line.get(0));

            if (encodeTwo == null)
                encodeTwo = encodingTable.get(line.get(1));


            conoTable.put( new Pair<Integer, Integer>(encodeOne, encodeTwo), line.get(2) );

            line = csvReader.next();
            lineNumber++;
        }

        csvReader.close();
        return conoTable;
    }


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

            // keyword, opperation, or old variable
            if(encodingTable.containsKey(token)) {
                codes.add(encodingTable.get(token));
                continue;
            }

            // new constant
            try {Integer.parseInt(token);}
            catch (NumberFormatException nfe) {
                encodingTable.put(token, newConstantEncodingNumber);
                codes.add(newConstantEncodingNumber);
                newConstantEncodingNumber++;
            }

            // new variable
            encodingTable.put(token, newVariableEncodingNumber);
            codes.add(newVariableEncodingNumber);
            newVariableEncodingNumber++;
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


        // import encoding table
        File encodingTableFile = new File(projectPath, "Encoding_Table.csv");
        HashMap<String, Integer> encodingTable = interpretAsEncodingTable(encodingTableFile);

        // encode tokens to instructions
        ArrayList<Integer> instructions = encode(encodingTable, tokens);

        // gather sequence labels
        ArrayList<InstructionCategorizer> instructionCategories = new ArrayList<InstructionCategorizer>();
        instructionCategories.add(new OpperationCategorizer());
        instructionCategories.add(new VariableCategorizer());
        instructionCategories.add(new ConstantCategorizer());

        // import CONO table
        File conoTableFile = new File(projectPath, "CONO_Table.csv");
        HashMap<Pair<Integer, Integer>, String> conoTable = interpretAsCONOtable(conoTableFile, encodingTable, instructionCategories);


        // validate instruction set
        Validator instructionValidator = new Validator(encodingTable, conoTable, instructionCategories);
        Boolean instructionsAreValid = instructionValidator.validate(instructions);
        // TODO: let user know if code compiled successfully


        //This is the arraylist that will contain the operators and keywords for the tokens
        List<Integer> code_printout = new ArrayList<>();
        // TODO: make comprehensive printout


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

        while (instructionsIterator.hasNext())
            instructionsString.add(instructionsIterator.next().toString());

        File instructionsFile = new File(projectPath, "User_Instructions.txt");
        FileWriter instructionsWriter = new FileWriter(instructionsFile);
        instructionsWriter.write( instructionsString.toString() );


        // close
        user_input.close();
        reqBodyReader.close();
        tokenWriter.close();
        instructionsWriter.close();


        // format response
        resp.setContentType("text/html");


        // write
        PrintWriter respBodyWriter = resp.getWriter();
        respBodyWriter.print( traceOutputBuilder.toString());
        respBodyWriter.close();
	}
}
