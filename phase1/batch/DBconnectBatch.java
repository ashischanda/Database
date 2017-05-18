
package project.batch;

import java.sql.*;

public class DBconnectBatch {
    private     String host = "jdbc:mysql://localhost:3306/";
    private     String dbName = "googleScholarDB";                   //  Add db name
    private     String username = "root";
    private     String password = "";
    private     String url = host + dbName + "?user=" +username + "&password="+ password;
    PreparedStatement preparedStmt;
    Connection conn;

    public DBconnectBatch() {
        try{
                Class.forName("com.mysql.jdbc.Driver");			///  ****************************   this is Main CLASS
                conn = DriverManager.getConnection(url);
        }catch(Exception e){

        }

    }
    public void closeConnection() throws SQLException{
        conn.close();
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
                preparedStmt = conn.prepareStatement(query);
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
     public void insertIntoPaperList(int counter, String paperID,String paperTitle,String authors,String place,String year)
     {
          try{
                int yearValue = 0;
                int batchSize = 100;

                try{
                    yearValue = Integer.parseInt( year);            // converting string to integer
                }catch(Exception e){

                }

                // the mysql insert statement
                String query = "INSERT INTO  publicationlist  VALUES (?, ?, ?, ?, ?)";

                // create the mysql insert preparedstatement
                if ( counter ==1)
                    preparedStmt = conn.prepareStatement(query);

                preparedStmt.setString (1, paperID);
                preparedStmt.setString (2, paperTitle);
                preparedStmt.setString (3, authors);
                preparedStmt.setString (4, place);
                preparedStmt.setInt(5, yearValue);
                preparedStmt.addBatch();
                // execute the preparedstatement

                //preparedStmt.execute();
                if(counter % batchSize == 0) {
		preparedStmt.executeBatch();
                }

                //System.out.println("Succeed!  2");
            }

            catch(SQLException e){
                    System.out.println("2  SQL exception! " +e);

            }
    }

      public void insertIntoPaperOwner(int counter, String authorId, String paperID){
          try{
                // the mysql insert statement
                String query = "INSERT INTO  paperowner  VALUES (?, ?)";
                int batchSize = 100;
                if ( counter ==1)
                    preparedStmt = conn.prepareStatement(query);
                // create the mysql insert preparedstatement

                preparedStmt.setString (1, authorId);
                preparedStmt.setString (2, paperID);
                preparedStmt.addBatch();

                // execute the preparedstatement
                //preparedStmt.execute();
                if(counter % batchSize == 0) {
		preparedStmt.executeBatch();
                }

                //System.out.println("Succeed!  3");
            }

            catch(SQLException e){
                    System.out.println("3  SQL exception! "+e);

            }
    }

}
