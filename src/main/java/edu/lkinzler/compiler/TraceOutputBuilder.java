package edu.lkinzler.compiler;

import java.util.ArrayList;
import java.util.List;
import java.lang.StringBuilder;

public class TraceOutputBuilder {

    List<String> codeLines;
    List<String> tokenLines;
    List<String> instructionLines;
    List<String> generatorLines;

    public TraceOutputBuilder() {
        codeLines = new ArrayList<String>();
        tokenLines = new ArrayList<String>();
        instructionLines = new ArrayList<String>();
        generatorLines = new ArrayList<String>();
    }

    public void addNewLine(String codeLine) {
        codeLines.add("Line #" + (codeLines.size() + 1) + ": " + codeLine);
        tokenLines.add("");
        instructionLines.add("");
        generatorLines.add("");
    }

    public void addTokenToLine(String token, Integer index) {
        tokenLines.set(index, tokenLines.get(index) +
                (tokenLines.get(index).isEmpty()? "" : ", ") + token);
    }

    public void addInstructionToLine(Integer instruction, Integer index) {
        instructionLines.set(index, instructionLines.get(index) +
                (instructionLines.get(index).isEmpty()? "" : ", ") + instruction.toString());
    }

    public void addCodeGeneratorToLine(String generator, Integer index) {
        generatorLines.set(index, generatorLines.get(index) +
                (generatorLines.get(index).isEmpty()? "" : ", ") + generator);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        for (int i=0; i<codeLines.size(); i++) {
            output.append(codeLines.get(i));
            output.append("\n");

            output.append("Tokens: ");
            output.append(tokenLines.get(i));
            output.append("\n");

            output.append("Instructions: ");
            output.append(instructionLines.get(i));
            output.append("\n");

            output.append("Code Generators: ");
            output.append(generatorLines.get(i));
            output.append("\n\n");
        }

        return output.toString();
    }
}
