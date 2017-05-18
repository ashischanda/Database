package project;
// It is for specific author. You need to give author name or google scholar link


import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;

public class GScholarRough {
     public static String authorPosition ="", aiffiliation ="" ;
     public static String authorEmail = "", authorHomepage ="";
            
      public static void main(String[] args) throws IOException, Exception {
        DBconnect obj = new DBconnect();        // To insert data into DB  
          
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        WebClient webClient = new WebClient();
        webClient.getOptions().setUseInsecureSSL(true);

        webClient.getOptions().setThrowExceptionOnScriptError(false);
        String firstName = "Krishan";
        String lastName = "Kant";   // if you divide by first and last name, then it creates problem to select as middle file
        String keyword ="jie wu"; //firstName + " "+lastName;  //sc.nextLine();
        String authorNameInfile = "";
        
      
       // There will be two list. 1) Author list, 2) Publication list
        
       Boolean flag = true;
       HtmlPage page;
       String nextAuthorLink="";
       int totalAuthor =0, totalHindex = 0;
       String authorID="-e-QkGMAAAAJ";
       String link ="citations?user=-e-QkGMAAAAJ&hl=en";
       
            // Now, hit on the personal page
            HtmlPage page2 = webClient.getPage("https://scholar.google.com/"+link );        // visiting profile page
            
            final HtmlElement el = page2.getHtmlElementById("gsc_prf_in");
            String authorName = el.asText();
            
            // getting H index
            String removeIntitalTag = page2.asXml();
            int removeIndex = removeIntitalTag.indexOf("<td class=\"gsc_rsb_std\">" )+22;
            removeIntitalTag = removeIntitalTag.substring(removeIndex  );
            removeIndex = removeIntitalTag.indexOf("<td class=\"gsc_rsb_std\">" )+22;
            removeIntitalTag = removeIntitalTag.substring(removeIndex  );
            
            System.out.println(authorName);
            String hIndex = findingValue( removeIntitalTag, "<td class=\"gsc_rsb_std\">", "</td>");
            hIndex = hIndex.replaceAll("\\s+", "");
            //String hIndex = findingValue( page2.asText() , "h-index", "\t");
            
            //System.out.print(authorName + ", "+ hIndex);
            //System.out.print(","); 
            totalHindex+= Integer.parseInt(hIndex);
            totalAuthor+=1;
            
            int divCount = page2.getByXPath( "//div[@class='gsc_prf_il']").size() ;
            for( int p =0; p< divCount; p++){
                  final HtmlDivision divProfile = (HtmlDivision)  page2.getByXPath( "//div[@class='gsc_prf_il']").get(p); 
                 

                 String temDiv = divProfile.asText();
                 //System.out.println( temDiv.equalsIgnoreCase("professor") );  // it works when two string perfectly match
                 // But, we need to match substring
                 
                 findPost(temDiv.toLowerCase());
                 
                 break;
                 // just reading first div                 
            }
            
            // ****************************************************           Subject
            String interestSubject = "";
            int subjectCount = page2.getByXPath( "//a[@class='gsc_prf_ila']").size() ;
            for( int p =0; p< subjectCount; p++){
                final HtmlAnchor divSubject = (HtmlAnchor)  page2.getByXPath( "//a[@class='gsc_prf_ila']").get(p); 
                String temDiv = divSubject.asText();
                if( p!=0 )
                    interestSubject += " - "; 
                interestSubject += temDiv;
            }
            
            //******************************************************        email
            
            String scholarProfileText = page2.asText();
            if( scholarProfileText.toLowerCase().contains("No verified email".toLowerCase()) )
                authorEmail ="null";
            else if(scholarProfileText.toLowerCase().contains("verified email".toLowerCase()))
                authorEmail = "yes";
            else
                authorEmail ="null";
            
            ///******************************************************       homepage
          
             if( scholarProfileText.toLowerCase().contains("homepage".toLowerCase()) ){
                  final HtmlElement homepageLink = page2.getHtmlElementById("gsc_prf_ivh");
                  String homepageTxt = homepageLink.asXml();
                  homepageTxt = findingValue(homepageTxt, "<a href=\"" , "\" rel=\"nofollow\"");
                  authorHomepage = homepageTxt;
                
             }
            else
                authorHomepage = "null";    
            
            // *****************************************************************
            obj.insertIntoAuthor(authorID, authorName, authorEmail, authorHomepage, authorPosition, interestSubject, aiffiliation, hIndex);
            // *****************************************************************
        




            //******************************************************************
            // ***************************************************************** loading publcation list
            int publicationNumberStartFrom =20;
            while(true){
                int tagNumber =0;
                
                tagNumber = page2.getByXPath( "//tr[@class='gsc_a_tr']").size() ;
                
                if(tagNumber==1)    // No more row value / paper list
                    break;
                
                
                for( int k =0; k< tagNumber; k++){
                    final HtmlTableRow paperRow = (HtmlTableRow)  page2.getByXPath( "//tr[@class='gsc_a_tr']").get(k); 
                   
                    String paperID = findingValue( paperRow.asXml()  , "citation_for_view=" + authorID+":" ,"\" class=\"gsc_a_at" ) ;
                    //System.out.println(paperID);
                    
                    String paperTitle = "", authors ="", place ="", year ="";
                    boolean firstCell = true;
                    for (final HtmlTableCell cell : paperRow.getCells()) {      // reading each cell value of table
                        
                        String cellInfo = cell.asText();
                        //System.out.println(cellInfo);
                        
                        if(firstCell){      // divide the cell into title, author, publication
                            int temValue =0;
                            StringTokenizer st = new StringTokenizer(cellInfo, "\n"); //--> working
                            while (st.hasMoreTokens()) {
                                if(temValue==0)
                                    paperTitle= st.nextToken();
                                else if(temValue==1)
                                    authors = st.nextToken();
                                else
                                    place = st.nextToken();
                                temValue++;
 
                            }
                            firstCell=false;
                        }
                        else{
                            year = cellInfo;
                        }
                        
                        
                    }
                    
                    paperTitle = paperTitle.replaceAll(",", "");
                    paperTitle = paperTitle.replaceAll("'", "");
                    place = place.replaceAll(",", "");
                    place = place.replaceAll("'", "");
                    authors = authors.replaceAll(",", "");
                    authors = authors.replaceAll("'", "");
                    
                    
                    obj.insertIntoPaperList(paperID, paperTitle.trim() , authors.trim(), place.trim(), year.trim() );
                    obj.insertIntoPaperOwner(authorID, paperID);
                    //System.out.println(paperTitle.trim() +">>"+authors.trim()+">>"+place.trim()+">>"+year.trim()+" ");
                }
            
             page2 = webClient.getPage("https://scholar.google.com/"+link +"&cstart="+publicationNumberStartFrom +"&pagesize=20");// go next 20 publication
             publicationNumberStartFrom+= 20;
             Thread.sleep(4000);    // pause code to slow down process
            }
            //******************************************************************
          
        // end of 1st 10 authors
        
      
       
        // Result  **************************************************************
        System.out.println(authorNameInfile + " Total author number: "+ totalAuthor + " Total average H index: "+ (totalHindex/totalAuthor) );
      }
      
      
      
      
      
      
      
    static String findingValue(String content, String fistKey, String secondKey){
        String result ="NO";
        int firstIndex = content.indexOf(fistKey);
          int wordLengthOfFirstKey = fistKey.length(); 
         if(firstIndex == - 1) {
           return result;
         } 
         else {
            
            int lastIndex = content.indexOf(secondKey);
            int count = 0;
            while(lastIndex <= firstIndex && count<100) { // lastIndex must > than firstIndex
                lastIndex = content.indexOf(secondKey, lastIndex+1);    // taking next lastIndex
                count++;    // to avoid infinite loop
            }

            //System.out.println( firstIndex + "       "+ lastIndex);
            result = content.substring( firstIndex+wordLengthOfFirstKey, lastIndex );   // add first word length
            
            return result;
        }
        
    
    }
    
    
    static void findPost(String temDiv)
    {
        
        if (  temDiv.toLowerCase().contains("assistant professor".toLowerCase()) ){
                     authorPosition = "Assistant Professor";
                     temDiv = temDiv.replace("Assistant Professor", "");
                     temDiv = temDiv.replaceAll(",", "");
                     
                     aiffiliation = temDiv;
                 }
                 else if( temDiv.toLowerCase().contains("Associate professor".toLowerCase()) ){
                     authorPosition = "Associate Professor";
                     temDiv = temDiv.replaceAll(",", "");
                     temDiv = temDiv.replace("Associate Professor", "");
                     aiffiliation = temDiv;
                 }
                 else if ( temDiv.toLowerCase().contains("Chair".toLowerCase()) ){
                     authorPosition = "Chair";
                     temDiv = temDiv.replace("Chair", "");
                     temDiv = temDiv.replaceAll(",", "");
                      aiffiliation = temDiv;
                 }
                 else if ( temDiv.toLowerCase().contains("Scientist".toLowerCase()) ){ 
                     authorPosition = "Scientist";
                     temDiv = temDiv.replace("Scientist", "");
                     temDiv = temDiv.replaceAll(",", "");
                     aiffiliation = temDiv;
                 }
                 else if ( temDiv.toLowerCase().contains("Researcher".toLowerCase()) ){
                     authorPosition = "Researcher";
                     temDiv = temDiv.replace("Researcher", "");
                     temDiv = temDiv.replaceAll(",", "");
                      aiffiliation = temDiv;
                 }
                 
                 else if ( temDiv.toLowerCase().contains("Professor and Dean".toLowerCase()) ){
                     authorPosition = "Professor and Dean";
                     temDiv = temDiv.replace("Professor and Dean", "");
                     temDiv = temDiv.replaceAll(",", "");
                    aiffiliation = temDiv;
                 }
                  else if ( temDiv.toLowerCase().contains("Professor and post graduate teacher".toLowerCase()) ){
                     authorPosition = "Professor and post graduate teacher";
                     temDiv = temDiv.replace("Professor and post graduate teacher", "");
                     temDiv = temDiv.replaceAll(",", "");
                      aiffiliation = temDiv;
                 } 
                 else if ( temDiv.toLowerCase().contains("Professor  & Head".toLowerCase()) ){
                     authorPosition = "Professor  & Head";
                     temDiv = temDiv.replace("Professor  & Head", "");
                     temDiv = temDiv.replaceAll(",", "");
                      aiffiliation = temDiv;
                 } 
                 else if ( temDiv.toLowerCase().contains("Professor".toLowerCase()) ){
                     authorPosition = "Professor";
                     temDiv = temDiv.replace("Professor", "");
                     temDiv = temDiv.replaceAll(",", "");
                      aiffiliation = temDiv;
                 }
                 else if ( temDiv.toLowerCase().contains("Director".toLowerCase()) ){
                     authorPosition = "Director";
                     temDiv = temDiv.replace("Director", "");
                     temDiv = temDiv.replaceAll(",", "");
                     aiffiliation = temDiv;
                 } 
                 else if ( temDiv.toLowerCase().contains("Graduate Students".toLowerCase()) ){
                     authorPosition = "Graduate Students";
                     temDiv = temDiv.replace("Graduate Students", "");
                     temDiv = temDiv.replaceAll(",", "");
                      aiffiliation = temDiv;
                 } 
                 else if ( temDiv.toLowerCase().contains("Graduate Student".toLowerCase()) ){
                     authorPosition = "Graduate Student";
                     temDiv = temDiv.replace("Graduate Student", "");
                     temDiv = temDiv.replaceAll(",", "");
                      aiffiliation = temDiv;
                 } 
                 else if ( temDiv.toLowerCase().contains("PhD Student".toLowerCase()) ){
                     authorPosition = "PhD Student";
                     temDiv = temDiv.replace("PhD Student", "");
                     temDiv = temDiv.replaceAll(",", "");
                      aiffiliation = temDiv;
                 } 
                 else if ( temDiv.toLowerCase().contains("Student".toLowerCase()) ){
                     authorPosition ="Student";
                     temDiv = temDiv.replace("Student", "");
                     temDiv = temDiv.replaceAll(",", "");
                      aiffiliation = temDiv;
                 } 
                 else if ( temDiv.toLowerCase().contains("Postdoc Fellow".toLowerCase()) ){
                     authorPosition  = "Postdoc Fellow";
                     temDiv = temDiv.replace("Postdoc Fellow", "");
                     temDiv = temDiv.replaceAll(",", "");
                      aiffiliation = temDiv;
                 } 
                 else{
                      authorPosition = "null";
                      temDiv = temDiv.replaceAll(",", "");
                       aiffiliation = temDiv;
                 }
    
    }

}
