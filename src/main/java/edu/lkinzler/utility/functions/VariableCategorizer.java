package edu.lkinzler.utility.functions;

public class VariableCategorizer implements InstructionCategorizer{

    public String getLabel() {return "VAR";}
    public Integer getInstruction() {return 300;}

    public Boolean withinCategory(Integer instruction) {
        return instruction > getInstruction() && instruction < 600;
    }

    public Integer categorize(Integer instruction) {
        if (!withinCategory(instruction))
            return instruction;

        return getInstruction();
    }
}
