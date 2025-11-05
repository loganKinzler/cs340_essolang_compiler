package edu.lkinzler.compiler;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import edu.lkinzler.utility.Pair;

public class Validator {
    private Map<String, Integer> encodingTable;
    private Map<Pair<Integer, Integer>, String> conoTable;


    public Validator(Map<String, Integer> encodingTable, Map<Pair<Integer, Integer>, String> conoTable) {
        this.encodingTable = encodingTable;
        this.conoTable = conoTable;
    }

    public Boolean validate(String code) {
        return false;
    }

    //    // output cono table
//    Integer encodedPrevious = encodingTable.get(previous_token);
//    Integer encodedCurrent = encodingTable.get(token);

//                if (conoTable.containsKey(new Pair<Integer, Integer>(encodedPrevious, encodedCurrent)))
//            System.out.println(previous_token + " : " + token + " => " +
//            conoTable.get( new Pair<Integer, Integer>(
//    encodedPrevious, encodedCurrent))
//            );
//
//            else
//                    System.out.println(previous_token + " : " + token + " => TSE");
//
//    previous_token = token;

}
