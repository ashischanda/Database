
package project;

import java.sql.*;

public class DBconnect {
    private     String host = "jdbc:mysql://localhost:3306/";
    private     String dbName = "googleScholarDB";                   //  Add db name
    private     String username = "root";
    private     String password = "root";
    private     String url = host + dbName + "?user=" +username + "&password="+ password;
    PreparedStatement preparedStmt;
            
    public void insertIntoAuthor(String authorID,String authorName,String authorEmail,String authorHomepage,String authorPosition,String interestSubject,String aiffiliation,String hIndex){
       
             
        try{
                Class.forName("com.mysql.jdbc.Driver");			///  ****************************   this is Main CLASS
                Connection conn = DriverManager.getConnection(url);
                int hIndexValue = 0;
                
                try{
                    hIndexValue = Integer.parseInt( hIndex);            // converting string to integer
                }catch(Exception e){
                    
                }
                
                // the mysql insert statement
                String query = "INSERT INTO  authorlist  VALUES (?, ?, ?, ?, ?,?, ?,?)";

                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = conn.prepareStatement(query);
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
                conn.close();
                //System.out.println("Succeed!   1");
            }
            catch(ClassNotFoundException e){
                    System.out.println("1  Class not found! "+ e);
            }
            catch(SQLException e){
                    System.out.println("1  SQL exception! "+e);

            }

    }
     public void insertIntoPaperList( String paperID,String paperTitle,String authors,String place,String year)
     {
          try{
                Class.forName("com.mysql.jdbc.Driver");			///  ****************************   this is Main CLASS
                Connection conn = DriverManager.getConnection(url);
                 int yearValue = 0;
              
                try{
                    yearValue = Integer.parseInt( year);            // converting string to integer
                }catch(Exception e){
                    
                }
             
                // the mysql insert statement
                String query = "INSERT INTO  publicationlist  VALUES (?, ?, ?, ?, ?)";

                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setString (1, paperID);
                preparedStmt.setString (2, paperTitle);
                preparedStmt.setString (3, authors);
                preparedStmt.setString (4, place);
                preparedStmt.setInt(5, yearValue);

                // execute the preparedstatement
                preparedStmt.execute();
                
               
                conn.close();
                //System.out.println("Succeed!  2");
            }
            catch(ClassNotFoundException e){
                    System.out.println("2  Class not found! "+ e);
            }
            catch(SQLException e){
                    System.out.println("2  SQL exception! " +e);

            }
    }
     
      public void insertIntoPaperOwner(String authorId, String paperID){
          try{
                Class.forName("com.mysql.jdbc.Driver");			///  ****************************   this is Main CLASS
                Connection conn = DriverManager.getConnection(url);
                // the mysql insert statement
                String query = "INSERT INTO  paperowner  VALUES (?, ?)";

                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setString (1, authorId);
                preparedStmt.setString (2, paperID);
                
                // execute the preparedstatement
                preparedStmt.execute();
               
                conn.close();
                //System.out.println("Succeed!  3");
            }
            catch(ClassNotFoundException e){
                    System.out.println("3  Class not found! "+ e);
            }
            catch(SQLException e){
                    System.out.println("3  SQL exception! "+e);

            }
    }

}
