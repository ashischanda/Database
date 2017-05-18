
package project;

/// It will read three file to insert data in three tables of MySQL 
/// It will finally print the running time


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import static project.GScholarDataCollector.aiffiliation;
import static project.GScholarDataCollector.authorEmail;
import static project.GScholarDataCollector.authorHomepage;
import static project.GScholarDataCollector.authorPosition;

public class ReadCSVfile {
    public static void main(String[] args) {

        // ******************************************************* set the timer 
        // *******************************************************
        long startTime = System.currentTimeMillis();
        
        String line = "";
        String cvsSplitBy = ",";
        DBconnect obj = new DBconnect();
        
        
        //*********************************************************************
        String csvFile = "one.txt"; // reading author list
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {
                
                String[] values = line.split(cvsSplitBy);       // use comma as separator
                
                for (int i = 0; i< values.length; i++){
                    values[i] = values[i].replaceAll("'", "");  // avoiding single quotation
                    values[i] = values[i].trim();               // avoiding extra space
                }
                String t_val = values[0].substring(1);          // avoiding "("
                values[0] = t_val;
                
                t_val = values[ values.length - 1 ];            // avoiding ")"
                values[ values.length - 1 ] = t_val.substring(0, t_val.length()-1 );
                                
                obj.insertIntoAuthor(values[0],values[1],values[3],values[2], values[5], values[4], values[6], ""+values[7]);
            
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        long elapsedTime = System.currentTimeMillis() - startTime;
        long elapsedSeconds = elapsedTime / 1000;
        long secondsDisplay = elapsedSeconds % 60;
        long elapsedMinutes = elapsedSeconds / 60;
        System.out.println("1st Time :"+ elapsedMinutes +" "+ secondsDisplay);
        startTime = System.currentTimeMillis();
        
        //*********************************************************************
        csvFile = "three.txt"; // reading publication list
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {
                
                String[] values = line.split(cvsSplitBy);
                for (int i = 0; i< values.length; i++){
                    values[i] = values[i].replaceAll("'", "");  // avoiding single quotation
                    values[i] = values[i].trim();               // avoiding extra space
                }
                String t_val = values[0].substring(1);
                values[0] = t_val;
                
                t_val = values[ values.length - 1 ];
                values[ values.length - 1 ] = t_val.substring(0, t_val.length()-1 );
                
                obj.insertIntoPaperList(values[0],values[1],values[2],values[3], values[4]);
               
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        elapsedTime = System.currentTimeMillis() - startTime;
        elapsedSeconds = elapsedTime / 1000;
        secondsDisplay = elapsedSeconds % 60;
        elapsedMinutes = elapsedSeconds / 60;
        System.out.println("2nd Time :"+ elapsedMinutes +" "+ secondsDisplay);
        startTime = System.currentTimeMillis();
        
        //*********************************************************************
        csvFile = "two.txt"; // reading author+publication ID list
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {
                
                String[] values = line.split(cvsSplitBy);
                for (int i = 0; i< values.length; i++){
                    values[i] = values[i].replaceAll("'", "");  // avoiding single quotation
                    values[i] = values[i].trim();               // avoiding extra space
                }
                try{
                    String t_val = values[0].substring(1);
                    values[0] = t_val;

                    t_val = values[ values.length - 1 ];
                    values[ values.length - 1 ] = t_val.substring(0, t_val.length()-1 );
                    obj.insertIntoPaperOwner(values[0],values[1]);
                }catch(Exception e){
                    
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
             
        elapsedTime = System.currentTimeMillis() - startTime;
        elapsedSeconds = elapsedTime / 1000;
        secondsDisplay = elapsedSeconds % 60;
        elapsedMinutes = elapsedSeconds / 60;
        System.out.println("Time :"+ elapsedMinutes +" "+ secondsDisplay);
     
    }
}
