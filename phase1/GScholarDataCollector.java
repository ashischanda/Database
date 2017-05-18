package project;
// It reads from author file and show everything with time

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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;

public class GScholarDataCollector {

    public static String authorPosition = "", aiffiliation = "";
    public static String authorEmail = "", authorHomepage = "";

    public static void main(String[] args) throws IOException, Exception {
        DBconnect obj = new DBconnect();        // To insert data into DB  

        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        WebClient webClient = new WebClient();
        webClient.getOptions().setUseInsecureSSL(true);

        webClient.getOptions().setThrowExceptionOnScriptError(false);
        //String firstName = "Krishna";
        //String lastName = "Kant";   // if you divide by first and last name, then it creates problem to select as middle file
        String keyword = "jie wu"; //firstName + " "+lastName;  //sc.nextLine();
        String authorNameInfile = "";

        // ******************************************************* set the timer 
        // *******************************************************
        long startTime = System.currentTimeMillis();

        // ********************************************************Reading File
        // *********************************************************************
        Scanner fileScanner = new Scanner(new File("input.txt"));
        while (fileScanner.hasNextLine()) {
            authorNameInfile = fileScanner.nextLine();
            keyword = authorNameInfile;

            // There will be two list. 1) Author list, 2) Publication list
            Boolean flag = true;
            HtmlPage page;
            String nextAuthorLink = "";
            int totalAuthor = 0, totalHindex = 0;

            while (true) {

                if (flag) {
                    page = webClient.getPage("https://scholar.google.com/citations?view_op=search_authors&mauthors=" + URLEncoder.encode(keyword, "UTF-8"));
                } else {
                    page = webClient.getPage("https://scholar.google.com/citations?view_op=search_authors&hl=en&mauthors=" + keyword + "&after_author=" + nextAuthorLink);
                }

                int userNumber = page.getByXPath("//div[@class='gsc_1usr_photo']").size();
                for (int i = 0; i < userNumber; i++) {
                    final HtmlDivision div = (HtmlDivision) page.getByXPath("//div[@class='gsc_1usr_photo']").get(i);

                    String tem = div.asXml();
                    //System.out.println(tem );      // div of users profile list

                    int j = 0;
                    String link = "";
                    for (String retval : tem.split("href=\"")) { // searching the link
                        ++j;
                        if (j == 2) {
                            link = retval;  // got the value at 2nd attempt
                            break;
                        }

                    }

                    for (String retval : link.split("\"")) {
                        link = retval;
                        break;

                    }
                    //System.out.println("***********************************************");
                    //System.out.println(link);     // personal link of each scholar profile
                    String authorID = findingValue(link, "user=", "&amp;hl=en");        // ex: https://scholar.google.com/citations?user=eG-C3JIAAAAJ&hl=en

                    //System.out.println(authorID);                                       // ex: authorID = eG-C3JIAAAAJ
                    // Now, hit on the personal page
                    HtmlPage page2 = webClient.getPage("https://scholar.google.com/" + link);        // visiting profile page

                    final HtmlElement el = page2.getHtmlElementById("gsc_prf_in");
                    String authorName = el.asText();

                    // getting H index
                    String removeIntitalTag = page2.asXml();
                    int removeIndex = removeIntitalTag.indexOf("<td class=\"gsc_rsb_std\">") + 22;
                    removeIntitalTag = removeIntitalTag.substring(removeIndex);
                    removeIndex = removeIntitalTag.indexOf("<td class=\"gsc_rsb_std\">") + 22;
                    removeIntitalTag = removeIntitalTag.substring(removeIndex);

                    System.out.println(authorName);
                    String hIndex = findingValue(removeIntitalTag, "<td class=\"gsc_rsb_std\">", "</td>");
                    hIndex = hIndex.replaceAll("\\s+", "");
                    //String hIndex = findingValue( page2.asText() , "h-index", "\t");

                    //System.out.print(authorName + ", "+ hIndex);
                    //System.out.print(","); 
                    totalHindex += Integer.parseInt(hIndex);
                    totalAuthor += 1;

                    int divCount = page2.getByXPath("//div[@class='gsc_prf_il']").size();
                    for (int p = 0; p < divCount; p++) {
                        final HtmlDivision divProfile = (HtmlDivision) page2.getByXPath("//div[@class='gsc_prf_il']").get(p);

                        String temDiv = divProfile.asText();
                        //System.out.println( temDiv.equalsIgnoreCase("professor") );  // it works when two string perfectly match
                        // But, we need to match substring

                        findPost(temDiv.toLowerCase());

                        break;
                        // just reading first div                 
                    }

                    // ****************************************************           Subject
                    String interestSubject = "";
                    int subjectCount = page2.getByXPath("//a[@class='gsc_prf_ila']").size();
                    for (int p = 0; p < subjectCount; p++) {
                        final HtmlAnchor divSubject = (HtmlAnchor) page2.getByXPath("//a[@class='gsc_prf_ila']").get(p);
                        String temDiv = divSubject.asText();
                        if (p != 0) {
                            interestSubject += " - ";
                        }
                        interestSubject += temDiv;
                    }

                    //******************************************************        email
                    String scholarProfileText = page2.asText();
                    if (scholarProfileText.toLowerCase().contains("No verified email".toLowerCase())) {
                        authorEmail = "";
                    } else if (scholarProfileText.toLowerCase().contains("verified email".toLowerCase())) {
                        authorEmail = "yes";
                    } else {
                        authorEmail = "";
                    }

                    ///******************************************************       homepage
                    if (scholarProfileText.toLowerCase().contains("homepage".toLowerCase())) {
                        final HtmlElement homepageLink = page2.getHtmlElementById("gsc_prf_ivh");
                        String homepageTxt = homepageLink.asXml();
                        homepageTxt = findingValue(homepageTxt, "<a href=\"", "\" rel=\"nofollow\"");
                        authorHomepage = homepageTxt;

                    } else {
                        authorHomepage = "";
                    }

                    // *****************************************************************
                    obj.insertIntoAuthor(authorID, authorName, authorEmail, authorHomepage, authorPosition, interestSubject, aiffiliation, hIndex);
                     // *****************************************************************

                    //******************************************************************
                    // ***************************************************************** loading publcation list
                    int publicationNumberStartFrom = 20;
                    while (true) {
                        int tagNumber = 0;

                        tagNumber = page2.getByXPath("//tr[@class='gsc_a_tr']").size();

                        if (tagNumber == 1) // No more row value / paper list
                        {
                            break;
                        }

                        for (int k = 0; k < tagNumber; k++) {
                            final HtmlTableRow paperRow = (HtmlTableRow) page2.getByXPath("//tr[@class='gsc_a_tr']").get(k);

                            String paperID = findingValue(paperRow.asXml(), "citation_for_view=" + authorID + ":", "\" class=\"gsc_a_at");
                            //System.out.println(paperID);
                            
                            
                            String paperTitle = "", authors = "", place = "", year = "";
                            boolean firstCell = true;
                            for (final HtmlTableCell cell : paperRow.getCells()) {      // reading each cell value of table

                                String cellInfo = cell.asText();
                                //System.out.println(cellInfo);

                                if (firstCell) {      // divide the cell into title, author, publication
                                    int temValue = 0;
                                    StringTokenizer st = new StringTokenizer(cellInfo, "\n"); //--> working
                                    while (st.hasMoreTokens()) {
                                        if (temValue == 0) {
                                            paperTitle = st.nextToken();
                                        } else if (temValue == 1) {
                                            authors = st.nextToken();
                                        } else {
                                            place = st.nextToken();
                                        }
                                        temValue++;

                                    }
                                    firstCell = false;
                                } else {
                                    year = cellInfo;
                                }

                            }

                            paperTitle = paperTitle.replaceAll(",", "");
                            paperTitle = paperTitle.replaceAll("'", "");
                            paperID = calcualtePaperId( paperTitle);
                            
                            place = place.replaceAll(",", "");
                            place = place.replaceAll("'", "");
                            //findAuthorsFromPublication(authors);
                            authors = authors.replaceAll(",", "");
                            authors = authors.replaceAll("'", "");

                            obj.insertIntoPaperList(paperID, paperTitle.trim(), authors.trim(), place.trim(), year.trim());
                            obj.insertIntoPaperOwner(authorID, paperID);
                            //System.out.println(paperTitle.trim() +">>"+authors.trim()+">>"+place.trim()+">>"+year.trim()+" ");
                        }

                        page2 = webClient.getPage("https://scholar.google.com/" + link + "&cstart=" + publicationNumberStartFrom + "&pagesize=20");// go next 20 publication
                        publicationNumberStartFrom += 20;
                        Thread.sleep(4000);    // pause code to slow down process
                    }
                    //******************************************************************

                }// end of 1st 10 authors

                String content = page.asXml();
                nextAuthorLink = findingValue(content, "after_author\\x3d", "\\x26astart");

                int firstIndex = content.indexOf("after_author\\x3d");

                if (nextAuthorLink == "NO") {
                    //System.out.println("No more pages");
                    break;
                } else {
                    flag = false;  //continue for next page
                }

            }// end of while loop

            // Result  **************************************************************
            System.out.println(authorNameInfile + " Total author number: " + totalAuthor + " Total average H index: " + (totalHindex / totalAuthor));
        }// end of file read
        // *******************************************
        long elapsedTime = System.currentTimeMillis() - startTime;
        long elapsedSeconds = elapsedTime / 1000;
        long secondsDisplay = elapsedSeconds % 60;
        long elapsedMinutes = elapsedSeconds / 60;
        System.out.println("Time :" + elapsedMinutes + " " + secondsDisplay);

    }// end of main

    static String findingValue(String content, String fistKey, String secondKey) {
        String result = "NO";
        int firstIndex = content.indexOf(fistKey);
        int wordLengthOfFirstKey = fistKey.length();
        if (firstIndex == - 1) {
            return result;
        } else {

            int lastIndex = content.indexOf(secondKey);
            int count = 0;
            while (lastIndex <= firstIndex && count < 100) { // lastIndex must > than firstIndex
                lastIndex = content.indexOf(secondKey, lastIndex + 1);    // taking next lastIndex
                count++;    // to avoid infinite loop
            }

            //System.out.println( firstIndex + "       "+ lastIndex);
            result = content.substring(firstIndex + wordLengthOfFirstKey, lastIndex);   // add first word length

            return result;
        }

    }

    static void findPost(String temDiv) {

        if (temDiv.toLowerCase().contains("assistant professor".toLowerCase())) {
            authorPosition = "Assistant Professor";
            temDiv = temDiv.replace("Assistant Professor", "");
            temDiv = temDiv.replaceAll(",", "");

            aiffiliation = temDiv;
        } else if (temDiv.toLowerCase().contains("Associate professor".toLowerCase())) {
            authorPosition = "Associate Professor";
            temDiv = temDiv.replaceAll(",", "");
            temDiv = temDiv.replace("Associate Professor", "");
            aiffiliation = temDiv;
        } else if (temDiv.toLowerCase().contains("Chair".toLowerCase())) {
            authorPosition = "Chair";
            temDiv = temDiv.replace("Chair", "");
            temDiv = temDiv.replaceAll(",", "");
            aiffiliation = temDiv;
        } else if (temDiv.toLowerCase().contains("Scientist".toLowerCase())) {
            authorPosition = "Scientist";
            temDiv = temDiv.replace("Scientist", "");
            temDiv = temDiv.replaceAll(",", "");
            aiffiliation = temDiv;
        } else if (temDiv.toLowerCase().contains("Researcher".toLowerCase())) {
            authorPosition = "Researcher";
            temDiv = temDiv.replace("Researcher", "");
            temDiv = temDiv.replaceAll(",", "");
            aiffiliation = temDiv;
        } else if (temDiv.toLowerCase().contains("Professor and Dean".toLowerCase())) {
            authorPosition = "Professor and Dean";
            temDiv = temDiv.replace("Professor and Dean", "");
            temDiv = temDiv.replaceAll(",", "");
            aiffiliation = temDiv;
        } else if (temDiv.toLowerCase().contains("Professor and post graduate teacher".toLowerCase())) {
            authorPosition = "Professor and post graduate teacher";
            temDiv = temDiv.replace("Professor and post graduate teacher", "");
            temDiv = temDiv.replaceAll(",", "");
            aiffiliation = temDiv;
        } else if (temDiv.toLowerCase().contains("Professor  & Head".toLowerCase())) {
            authorPosition = "Professor  & Head";
            temDiv = temDiv.replace("Professor  & Head", "");
            temDiv = temDiv.replaceAll(",", "");
            aiffiliation = temDiv;
        } else if (temDiv.toLowerCase().contains("Professor".toLowerCase())) {
            authorPosition = "Professor";
            temDiv = temDiv.replace("Professor", "");
            temDiv = temDiv.replaceAll(",", "");
            aiffiliation = temDiv;
        } else if (temDiv.toLowerCase().contains("Director".toLowerCase())) {
            authorPosition = "Director";
            temDiv = temDiv.replace("Director", "");
            temDiv = temDiv.replaceAll(",", "");
            aiffiliation = temDiv;
        } else if (temDiv.toLowerCase().contains("Graduate Students".toLowerCase())) {
            authorPosition = "Graduate Students";
            temDiv = temDiv.replace("Graduate Students", "");
            temDiv = temDiv.replaceAll(",", "");
            aiffiliation = temDiv;
        } else if (temDiv.toLowerCase().contains("Graduate Student".toLowerCase())) {
            authorPosition = "Graduate Student";
            temDiv = temDiv.replace("Graduate Student", "");
            temDiv = temDiv.replaceAll(",", "");
            aiffiliation = temDiv;
        } else if (temDiv.toLowerCase().contains("PhD Student".toLowerCase())) {
            authorPosition = "PhD Student";
            temDiv = temDiv.replace("PhD Student", "");
            temDiv = temDiv.replaceAll(",", "");
            aiffiliation = temDiv;
        } else if (temDiv.toLowerCase().contains("Student".toLowerCase())) {
            authorPosition = "Student";
            temDiv = temDiv.replace("Student", "");
            temDiv = temDiv.replaceAll(",", "");
            aiffiliation = temDiv;
        } else if (temDiv.toLowerCase().contains("Postdoc Fellow".toLowerCase())) {
            authorPosition = "Postdoc Fellow";
            temDiv = temDiv.replace("Postdoc Fellow", "");
            temDiv = temDiv.replaceAll(",", "");
            aiffiliation = temDiv;
        } else {
            authorPosition = "";
            temDiv = temDiv.replaceAll(",", "");
            aiffiliation = temDiv;
        }

    }
    
    private static final String FILENAME = "authorListFromPublication.txt";
    
    static void findAuthorsFromPublication(String authorlist){
        BufferedWriter bw = null;
	FileWriter fw = null;

	try {
        	File file = new File(FILENAME);
        	// if file doesnt exists, then create it
                if (!file.exists()) {
			file.createNewFile();
		}
		// true = append file
		fw = new FileWriter(file.getAbsoluteFile(), true);
		bw = new BufferedWriter(fw);
      
        
                String splitBy = ",";
                String[] individualAuthor = authorlist.split(splitBy);
                for (int i = 0; i< individualAuthor.length; i++){
                    bw.write(individualAuthor[i]+"\n");
                }
                //System.out.println( "done");
            }
            catch (IOException e) {
                e.printStackTrace();

            } finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}   
    }
    
    //************************************
    static String calcualtePaperId(String title){
        String ID = "";
        String splitBy = " ";
        int coutner_word = 0;
        int counter_char = 0;
        int sum_ascii = 0;
        
        String word="";
        String[] individualWord = title.split(splitBy);
        coutner_word = individualWord.length;
        for (int i = 0; i< individualWord.length; i++){
            word = individualWord[i];
            counter_char += word.length();
            for(int j =0; j<word.length(); j++)
                sum_ascii += word.charAt(j);
            
        }
        ID = ""+coutner_word+"-"+counter_char+sum_ascii+"-"+individualWord[0]+"-"+word; // adding last word
        return ID;
    }
}
