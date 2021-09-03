package farmind.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import com.opencsv.CSVReader;

import farmind.main.Consumat;

public class ConsumatTest {

    @SuppressWarnings("static-method")
    @Test
    public void testMain() throws FileNotFoundException, IOException {
        int yers = 2;
        Consumat.main(new String[] {""+yers, "test-out"});
        
        ArrayList<HashMap<String, HashMap<String, String>>> activityPrice 
            = new ArrayList<HashMap<String, HashMap<String, String>>>();
        ArrayList<HashMap<String, HashMap<String, String>>> parameters 
            = new ArrayList<HashMap<String, HashMap<String, String>>>();
        for (int i=0; i<yers; i++) {
            activityPrice.add(new HashMap<String, HashMap<String, String>>());
            parameters.add(new HashMap<String, HashMap<String, String>>());
        }
        
        try (CSVReader reader = new CSVReader(new FileReader(
                String.format("output/test-out_0_activity_actualPrice.csv")
            ), ',')) {
            String[] columns = reader.readNext();
            
            String[] row;
            while ((row = reader.readNext()) != null) {
                
                int year = Integer.parseInt(row[0])-1;
                String farm = row[1].trim();
                
                activityPrice.get(year).put(farm, new HashMap<String,String>());
                for (int i=2; i<columns.length; i++) {
                    activityPrice.get(year).get(farm).put(columns[i], row[i]);
                }
                
            }
        }
        
        try (CSVReader reader = new CSVReader(new FileReader(
                String.format("output/test-out_0_parameters.csv")
            ), ',')) {
            String[] columns = reader.readNext();
            
            String[] row;
            while ((row = reader.readNext()) != null) {
                
                int year = Integer.parseInt(row[0])-1;
                String farm = row[1].trim();
                
                parameters.get(year).put(farm, new HashMap<String,String>());
                for (int i=2; i<columns.length; i++) {
                    parameters.get(year).get(farm).put(columns[i], row[i]);
                }
                
            }
        }
        
        assertEquals(activityPrice.get(yers-1).get("agent05").get("income"), "247861.0");
        assertEquals(parameters.get(yers-1).get("agent05").get("possible_activity_e"), "activity05");
    }
}
