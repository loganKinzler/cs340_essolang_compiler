package edu.lkinzler.utility.functions;

public class OpperationCategorizer implements InstructionCategorizer{

    public String getLabel() {return "OPP";}
    public Integer getInstruction() {return 201;}

    public Boolean withinCategory(Integer instruction) {
        return instruction > getInstruction() && instruction < 300;
    }

    public Integer categorize(Integer instruction) {
        if (!withinCategory(instruction))
            return instruction;

        return getInstruction();
    }
}
