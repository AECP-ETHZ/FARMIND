package reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ParameterInput {
	
	private ArrayList<Integer> preferences = new ArrayList<Integer>();
	
	public ArrayList<Integer> readParameters() throws FileNotFoundException {
		Scanner scanner = new Scanner(new File("./inFile/test.csv"));
        scanner.useDelimiter(",");
        while(scanner.hasNext()){
        	String next = scanner.next();
            preferences.add(Integer.parseInt(next));
        }
        scanner.close();
		return preferences;
	}
}
