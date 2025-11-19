package edu.lkinzler.utility;

import com.sun.tools.jdeprscan.CSV;
import edu.lkinzler.utility.functions.InstructionCategorizer;

import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class CSVReader implements Closeable {

    private BufferedReader reader;

    public CSVReader(File CSVfile) throws IOException {
        // TODO: change file directory of the 'storage' files
        this.reader = new BufferedReader(new FileReader(CSVfile));
    }

    public List<String> next() throws IOException {
        String line = reader.readLine();

        if (line == null)
            return null;

        List<String> tuple = new ArrayList<String>();
        StringTokenizer token = new StringTokenizer(line, " ");

        while (token.hasMoreTokens())
            tuple.add( token.nextToken().trim() );

        return tuple;
    }

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


    public HashMap<String, Integer> interpretAsEncodingTable() throws IOException {
        HashMap<String, Integer> encodingTable = new HashMap<String, Integer>();

        List<String> line = next();
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

            line = next();
            lineNumber++;
        }

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

    public HashMap<Pair<Integer, Integer>, String> interpretAsCONOtable(HashMap<String, Integer> encodingTable, List<InstructionCategorizer> instructionCategories) throws IOException {
        HashMap<Pair<Integer, Integer>, String> conoTable = new HashMap<Pair<Integer, Integer>, String>();

        List<String> line = next();
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

            line = next();
            lineNumber++;
        }

        return conoTable;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
