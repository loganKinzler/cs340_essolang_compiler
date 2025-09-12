package edu.lkinzler.compiler;

import java.io.File;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.StringTokenizer;

public class Tokenizer {

    private char seperator = ',';

//    private String[] tokens;
//    private String[] whitespace;
//    private String[] endOfLines;

    private File inputFile;
    private File outputFile;


    public Tokenizer(File input, File output) {

        // I'll throw an IOException later if I can't read input or write output
        this.inputFile = input;
        this.outputFile = output;
    }


//    public void tokenize() {
//
//        // Setup 1: Grab text from inputFile as a String
//        StringBuilder inputAsString = "";
//        Scanner inputScanner = new Scanner(this.inputFile.getAbsolutePath());
//
//        while (inputScanner.hasNextLine())
//            inputAsString += inputScanner.nextLine();
//
//        inputScanner.close();
//
//        // Step 1: Remove Whitespace
//
//    }

    @Override
    public String toString() {
        return "";
    }
}
