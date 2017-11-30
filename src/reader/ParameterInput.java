package reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ParameterInput {
	
	public void readParameters() throws FileNotFoundException {
		Scanner scanner = new Scanner(new File("./inFile/test.csv"));
        scanner.useDelimiter(",");
        while(scanner.hasNext()){
            System.out.println(scanner.next());
        }
        scanner.close();
	}
}
