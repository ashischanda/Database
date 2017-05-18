
package project;

import java.sql.*;

/**
 * You should stop MYSQL. Then, you go at cmd. Type: sqlplus
 * Give your username, password. It will connected.
 * If still it is not connected, go at start>run> Type: Services.msc, select OracleService, press start 
 */
 
public class DBoracleConnect {
    
    
    private     static String host = "jdbc:oracle:thin:@localhost:1521:xe";
    private     String dbName = "googleScholarDB";                   //  Add db name
    private     static String username = "system";
    private     static String password = "tuCSE2017";
    private     String url = host + dbName + "?user=" +username + "&password="+ password;
    private Connection con;
    DBoracleConnect() throws ClassNotFoundException{
           try{
               Class.forName("oracle.jdbc.driver.OracleDriver");
               con = DriverManager.getConnection(host, username, password); 
               System.out.println("connected at class constructor");
           }
           catch(ClassNotFoundException e){
                    System.out.println("Class not found! "+ e);
            }
           catch(Exception e){

           }
           
    }
     public void insertIntoAuthor(String authorID,String authorName,String authorEmail,String authorHomepage,String authorPosition,String interestSubject,String aiffiliation,String hIndex){
       
             
        try{
       
            int hIndexValue = 0;
                
            try{
                hIndexValue = Integer.parseInt( hIndex);            // converting string to integer
            }catch(Exception e){

            }

                // the mysql insert statement
                String query = "INSERT INTO  authorlist  VALUES (?, ?, ?, ?, ?,?, ?,?)";

                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = con.prepareStatement(query);
                preparedStmt.setString (1, authorID);
                preparedStmt.setString (2, authorName);
                preparedStmt.setString (3, authorHomepage);
                preparedStmt.setString (4, authorEmail);
                preparedStmt.setString (5, interestSubject);
                preparedStmt.setString (6, authorPosition);
                preparedStmt.setString (7, aiffiliation);
                preparedStmt.setInt(8, hIndexValue);

                // execute the preparedstatement
                preparedStmt.execute();
                
                //System.out.println("Succeed!   1");
            }
            
            catch(SQLException e){
                    System.out.println("1  SQL exception! "+e);

            }

    }
     public void insertIntoPaperList( String paperID,String paperTitle,String authors,String place,String year)
     {
          try{
              
                int yearValue = 0;
              
                try{
                    yearValue = Integer.parseInt( year);            // converting string to integer
                }catch(Exception e){
                    
                }
             
                // the mysql insert statement
                String query = "INSERT INTO  publicationlist  VALUES (?, ?, ?, ?, ?)";

                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = con.prepareStatement(query);
                preparedStmt.setString (1, paperID);
                preparedStmt.setString (2, paperTitle);
                preparedStmt.setString (3, authors);
                preparedStmt.setString (4, place);
                preparedStmt.setInt(5, yearValue);

                // execute the preparedstatement
                preparedStmt.execute();
                
               
                
                //System.out.println("Succeed!  2");
            }
            
            catch(SQLException e){
                    System.out.println("2  SQL exception! " +e);

            }
    }
     
      public void insertIntoPaperOwner(String authorId, String paperID){
          try{
           
            // the mysql insert statement
            String query = "INSERT INTO  paperowner  VALUES (?, ?)";

            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString (1, authorId);
            preparedStmt.setString (2, paperID);

            // execute the preparedstatement
            preparedStmt.execute();

            
            //System.out.println("Succeed!  3");
            }
            
            catch(SQLException e){
                    System.out.println("3  SQL exception! "+e);

            }
    }
      
      public void closeConnection() throws SQLException
      {
          
          con.close();
      }
      public void openConnection() throws SQLException
      {
          
          try{
               Class.forName("oracle.jdbc.driver.OracleDriver");
               con = DriverManager.getConnection(host, username, password); 
               System.out.println("connected at inner code");
           }
           catch(ClassNotFoundException e){
                    System.out.println("Class not found! "+ e);
            }
           catch(Exception e){

           }
      }
      
      // For checking some rough calculation
      
     /* 
     public static void main(String args[]) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");

            Connection con = DriverManager.getConnection(host, username, password);

            Statement stmt = con.createStatement();
            System.out.println("connected");
            
            ResultSet rs = stmt.executeQuery("select * from course");
            while (rs.next()) {
                System.out.println(rs.getString(1) + "  " + rs.getInt(2) + "  " + rs.getInt(3));
            }
            
            System.out.println("insert");
            String query = "INSERT INTO  course  VALUES (?, ?, ?)";
            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString (1, "ME");
            preparedStmt.setString (2, "54");
            preparedStmt.setString (3, "21");
                // execute the preparedstatement
            preparedStmt.execute();
            con.close();
            System.out.println("Succeed!   1");           

        } catch (Exception e) {
            System.out.println(e);
        }

    }
      */
}
