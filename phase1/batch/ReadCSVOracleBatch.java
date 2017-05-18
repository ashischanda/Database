package project.batch;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;


public class ReadCSVOracleBatch {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        // ******************************************************* set the timer 
        // *******************************************************
        long elapsedTime, elapsedSeconds, secondsDisplay, elapsedMinutes;
        long startTime = System.currentTimeMillis();
        
        String line = "";
        String cvsSplitBy = ",";
        DBoracleConnectBatch obj = new DBoracleConnectBatch();
        
        
        //*********************************************************************
        String csvFile = "one.txt"; // reading author list
        
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
                
                
                obj.insertIntoAuthor(values[0],values[1],values[3],values[2], values[5], values[4], values[6], ""+values[7]);
            
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //obj.closeConnection();
        
        //*********************************************************************
        int counter = 0;
        int c=0;
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
                
                obj.insertIntoPaperList(++c, values[0],values[1],values[2],values[3], values[4]);
             
                counter++;
                if(counter%500==0){
                    obj.closeConnection();
                    obj.openConnection();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //**************************************
        //**************************************
        obj.closeConnection();
        obj.openConnection();
        
        
        elapsedTime = System.currentTimeMillis() - startTime;
        elapsedSeconds = elapsedTime / 1000;
        secondsDisplay = elapsedSeconds % 60;
        elapsedMinutes = elapsedSeconds / 60;
        System.out.println("2nd Time :"+ elapsedMinutes +" "+ secondsDisplay);
       
        
        
        //*********************************************************************
        int counter2 = 0;
        int cc = 0;
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
                    obj.insertIntoPaperOwner(++cc, values[0],values[1]);
                    
                }catch(Exception e){
                    
                }
               
                counter2++;
                if(counter2%500==0){
                    obj.closeConnection();
                    obj.openConnection();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //**************************************
        //**************************************
        obj.closeConnection(); 
        
        elapsedTime = System.currentTimeMillis() - startTime;
        elapsedSeconds = elapsedTime / 1000;
        secondsDisplay = elapsedSeconds % 60;
        elapsedMinutes = elapsedSeconds / 60;
        System.out.println("Time :"+ elapsedMinutes +" "+ secondsDisplay);
     
    }
}

