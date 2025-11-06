package edu.lkinzler.utility.functions;

public interface InstructionCategorizer {

    /***********************************************************
     * METHOD: getLabel                                        *
     * DESCRIPTION: This returns the label of the instruction  *
     *     category.                                           *
     * PARAMETERS: void                                        *
     * RETURN VALUE: String                                    *
     **********************************************************/

    String getLabel();


    /***********************************************************
     * METHOD: getInstruction                                  *
     * DESCRIPTION: This returns the instruction code of the   *
     *     instruction category.                               *
     * PARAMETERS: void                                        *
     * RETURN VALUE: Integer                                   *
     **********************************************************/

    Integer getInstruction();


    /***********************************************************
     * METHOD: withinCategory                                  *
     * DESCRIPTION: This takes in an instruction and           *
     *      determines whether the instruction belongs to this *
     *      category.                                          *
     * PARAMETERS: Integer instruction                         *
     * RETURN VALUE: Boolean                                   *
     **********************************************************/

    Boolean withinCategory(Integer instruction);


    /***********************************************************
     * METHOD: categorize                                      *
     * DESCRIPTION: This takes in an instruction and           *
     *      returns the category instruction value if the      *
     *      given instruction is within the category,          *
     *      otherwise it returns the given instruction.        *
     *      category.                                          *
     * PARAMETERS: Integer instruction                         *
     * RETURN VALUE: Integer                                   *
     **********************************************************/

    Integer categorize(Integer instruction);
}
