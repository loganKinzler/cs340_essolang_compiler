package edu.lkinzler.compiler;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import edu.lkinzler.utility.Pair;
import edu.lkinzler.utility.functions.InstructionCategorizer;

public class Validator {
    private final Map<String, Integer> encodingTable;
    private final Map<Pair<Integer, Integer>, String> CONOtable;
    private final List<InstructionCategorizer> categories;
    private TraceOutputBuilder outputBuilder;
    private List<String> codeGenerators;


    public Validator(Map<String, Integer> encodingTable,
                     Map<Pair<Integer, Integer>, String> CONOtable,
                     List<InstructionCategorizer> categories,
                     TraceOutputBuilder outputBuilder, List<String> codeGenerators) {

        this.encodingTable = encodingTable;
        this.CONOtable = CONOtable;
        this.categories = categories;
        this.outputBuilder = outputBuilder;
        this.codeGenerators = codeGenerators;
    }


    /***********************************************************
     * METHOD: validate                                        *
     * DESCRIPTION: This takes in the list of instructions and *
     *     validates if the order is valid.                    *
     * PARAMETERS: List<Integer> instructions                  *
     * RETURN VALUE: Boolean                                   *
     **********************************************************/

    public Boolean validate(List<Integer> instructions) {
        return validateOrder(instructions);
    }

//    private Boolean validateParenthesis(List<Integer> instructions) {
//        // TODO: add parenthesis & recursive code segmentation
//        return true;
//    }

    private Integer categorizeInstruction(Integer instruction) {
        for (InstructionCategorizer ic : categories)
            if (ic.withinCategory(instruction))
                return ic.categorize(instruction);
        return instruction;
    }

    /***********************************************************
     * METHOD: validateOrder                                   *
     * DESCRIPTION: This is a sub utility method used by the   *
     *     public validate method.                             *
     * PARAMETERS: List<Integer> instructions                  *
     * RETURN VALUE: Boolean                                   *
     **********************************************************/

    private Boolean validateOrder(List<Integer> instructions) {
        Iterator<Integer> instructionInterator = instructions.iterator();
        Integer previousInstruction;
        Integer currentInstruction = 0;

        // initialize previous instruction
        if (instructionInterator.hasNext())
            previousInstruction = instructionInterator.next();

        // no instructions, and thus valid
        else
            return true;


        Integer line = 0;
        while (instructionInterator.hasNext()) {
            currentInstruction = instructionInterator.next();

            codeGenerators.add(CONOtable.get(new Pair<Integer, Integer>(
                    categorizeInstruction( previousInstruction ),
                    categorizeInstruction( currentInstruction ))));

            // sequence is valid
            if (CONOtable.containsKey(new Pair<Integer, Integer>(
                    categorizeInstruction( previousInstruction ),
                    categorizeInstruction( currentInstruction )))) {

                // place into list of code generators
                if (currentInstruction == 0 || currentInstruction == 1){
                    line++;
                    previousInstruction = currentInstruction;
                    continue;
                }

                if (previousInstruction == 0) {
                    previousInstruction = currentInstruction;
                    continue;
                }



                outputBuilder.addCodeGeneratorToLine(CONOtable.get(new Pair<Integer, Integer>(
                        categorizeInstruction( previousInstruction ),
                        categorizeInstruction( currentInstruction ))), line);

                previousInstruction = currentInstruction;
                continue;
            }

            // sequence is not valid
            return false;
        }

        // total sequence is valid
        return true;
    }

    //Getters
    public List<String> getCodeGenerators() {
        return codeGenerators;
    }
}
