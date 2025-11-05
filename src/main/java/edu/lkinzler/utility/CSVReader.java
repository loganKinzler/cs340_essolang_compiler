package edu.lkinzler.utility;

import com.sun.tools.jdeprscan.CSV;

import java.io.*;

import java.util.ArrayList;
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

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
