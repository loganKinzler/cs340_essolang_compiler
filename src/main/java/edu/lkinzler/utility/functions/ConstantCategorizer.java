package edu.lkinzler.utility.functions;

public class ConstantCategorizer implements InstructionCategorizer{

    public String getLabel() {return "CONST";}
    public Integer getInstruction() {return 600;}

    public Boolean withinCategory(Integer instruction) {
        return instruction > getInstruction();
    }

    public Integer categorize(Integer instruction) {
        if (!withinCategory(instruction))
            return instruction;
        return getInstruction();
    }
}
