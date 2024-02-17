import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.DataFormatException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.sl.usermodel.ObjectMetaData.Application;
import org.apache.poi.ss.usermodel.Workbook;

/*	@Author Jacob Elbirt
 * Example project to format text and images in 
 * a PDF document using Apache PDFBox
 * 
 */

public class WritePDFExample {

	private static String imgFileName = "IMGFILE.jpg";
    private static File myFile = new File("FILECREATED.pdf");
	private static String txtFile = "STRINGFILE.txt";
	private static ArrayList<List<String>> docAL = new ArrayList<List<String>>();
	private static String fullTxtString;

	
	
	public static List<String> chompString(int max_length, String the_string) throws DataFormatException {
        String[] initial_split = the_string.split("[\r\n]");
	    List<String> lines = new ArrayList<String>();
	    // Loop through the lines broken up by EOL
	    for(int i=0;i<initial_split.length;i++) {
	           // Clear the EOL
	           initial_split[i].replace("\r","");
	           initial_split[i].replace("\n","");
	          
	           // If the line is bigger than the max length allowed
	           if(initial_split[i].length() > max_length) {
	                 // Split the line into an array of words
	                 String[] line_words = initial_split[i].split("[ ]");
	                 String new_string = ""; // this will be the broken up line for storage
	                
	                 // Loop through the Words of the current line
	                 for(int j=0;j<line_words.length;j++) {
	                        if(line_words[j].length() > max_length) { 
	                        	throw new DataFormatException("Cannot split words greater than maximum length : " + line_words[j]);
	                        }
	                        // If adding the word to the new_string (plus 1 for the space) exceeds the max_length
	                        if((new_string.length() + line_words[j].length() + 1) > max_length) {
	                               lines.add(new_string);  // add the current line as is to the final line list
	                               new_string = line_words[j]; // start the next line as the current word that didnt fit.
	                        } else {
	                               // if we have content add the space... otherwise start fresh
	                               //if(!new_string.isEmpty()) {
	                               new_string = new_string + " ";
	                               new_string = new_string + line_words[j]; // add the new word
	                               //}
	                        }
	                 }
	                 if(!new_string.isEmpty()) {
	                        lines.add(new_string);
	                        new_string = "";
	                 }
                     System.out.println(lines.size());
	           } else {
	                 lines.add(initial_split[i]); // the line is less than or equal to the maximum length
	           }
	    } // for EOL lines
	    return lines;
	}
	
	public static List<String> readInTextString() throws IOException, DataFormatException {
		File file = new File(txtFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		fullTxtString = "";
		String currLine;
		while ((currLine = br.readLine()) != null) {
			fullTxtString += ("        " + currLine + "\n");
		}
		br.close();
		List<String> currLineChomped = chompString(90,fullTxtString);
		return currLineChomped;
	}
	
	public static void writeToPDFFile(PDDocument doc, PDPageContentStream cont, PDPage myPage,
			List<String> chompedString) throws IOException, DataFormatException {
		
        cont.setFont(PDType1Font.TIMES_ROMAN, 12);
        cont.setLeading(20f);	// this sets line spacing on pdf
		cont.beginText();
		
		// First line written begins indented
        cont.newLineAtOffset(65, 730);
		for (int i=0; i<chompedString.size(); i++) {
			System.out.println(i);
			String line = "";
			// Starts a new line visually on the doc (further down the page)
			cont.newLine();
			line += chompedString.get(i);
			cont.showText(line);

			// If statement checking for end/beginning of page
			if (i > 0 && (i%30 == 0)) {
				cont.endText();
				PDPage blank = new PDPage();
				doc.addPage(blank);
				cont = new PDPageContentStream(doc, blank);
		        cont.setFont(PDType1Font.TIMES_ROMAN, 12);
		        cont.setLeading(20f);	// this sets line spacing on pdf
				cont.beginText();
		        cont.newLineAtOffset(65, 730);
				System.out.println("POW!   " + chompedString.get(i+1));
			}
		}
		
		cont.endText();
		cont.close();
	}
	
	
    public static void readInText() throws IOException {
    	FileInputStream text = new FileInputStream(txtFile);
    	BufferedReader br;
    	try {	
    		InputStreamReader inputStreamReader = new InputStreamReader(text, "UTF8"); 
        	br = new BufferedReader(inputStreamReader);
        	String currLine;
        	while ((currLine = br.readLine()) != null) {
            	ArrayList<String> lineAL = new ArrayList<String>();
	    		int noLines = currLine.length() / 70;
	    		System.out.println(noLines);
	    		int lastSPC = 0;
	    		for (int i = 0; i < noLines; i++) {
	    			for (int j = 69; j > 0; --j) {
	    				if (currLine.charAt(j) == ' ') {
	    					lastSPC = j;
	    					break;
	    				}
	    			}
	    			String line = currLine.substring(0, lastSPC).trim();
	    			//System.out.println(line);
	    			
	    			lineAL.add(line);
	    			currLine = currLine.substring(lastSPC).trim();
	    		}
				if (currLine.length() > 70) {
					for (int j = 69; j > 0; --j) {
	    				if (currLine.charAt(j) == ' ') {
	    					lastSPC = j;
	    					break;
	    				}
	    			}
					String line = currLine.substring(0, lastSPC).trim();
	    			//System.out.println(line);
	    			lineAL.add(line);
	    			currLine = currLine.substring(lastSPC).trim();
				}
				lineAL.add(currLine);
				
	        	docAL.add(lineAL); 
    		}
        	
        	
    	} catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //METHOD TO SET DOCUMENT INFORMATION
    public static void JavaPdfBoxDocumentInformation(PDDocument doc) throws IOException {
    	
        PDDocumentInformation pdi = doc.getDocumentInformation();	
        
        pdi.setAuthor("Jan Bodnar");			// Can use .get functions for these
        pdi.setTitle("World war II");
        pdi.setCreator("Java code");
        
        Calendar date = Calendar.getInstance();
        pdi.setCreationDate(date);
        pdi.setModificationDate(date);
        pdi.setKeywords("World war II, conflict, Allies, Axis powers");

        doc.save(myFile);
    }
    
	// METHOD TO GENERATE IMAGE IN PDF FILE
	public static void JavaPdfBoxCreateImage(PDDocument doc, PDPage myPage, int imgPageNo) throws IOException {
		PDPage imgPage = doc.getPage(imgPageNo);
        PDImageXObject pdImage = PDImageXObject.createFromFile(imgFileName, doc);	//PDImageXObject for using images in PDFs
        
        //obtains width+height of image
        int iw = pdImage.getWidth();
        int ih = pdImage.getHeight();		
        
        float offset = 40f; 		// offset from page edge, originates at BOTTOM LEFT corner

        try (PDPageContentStream img = new PDPageContentStream(doc, imgPage, PDPageContentStream.AppendMode.APPEND, true)) {		// Uses PDPageContentStream (similar to writing text) to draw image
            img.drawImage(pdImage, offset, offset, iw, ih);
        }
        doc.save(myFile);
	}
	
	// METHOD TO READ IN TEXT FROM PDF FILE
	public static void JavaPdfBoxReadText(File fileToRead) throws IOException {
        try (PDDocument doc = PDDocument.load(fileToRead)) {		//load in doc myFile

            PDFTextStripper stripper = new PDFTextStripper();	// extract text with TextStripper
            String text = stripper.getText(doc);

            System.out.println("Text size: " + text.length() + " characters:");
            //System.out.println(text);
        }
    }

	 // CREATE AND WRITE SMALL DOC
	 public static void main(String[] args) throws IOException, DataFormatException {
			//creates new doc
	        try (PDDocument doc = new PDDocument()) {
	        	//creates page object and adds to doc
	            PDPage myPage = new PDPage();
	            doc.addPage(myPage);
	            try (PDPageContentStream cont = new PDPageContentStream(doc, myPage)) {	// need a content stream to add text to page (or other presumably)
	            	// Reads text string in and chomps it to desired length
	            	List<String> inputString = readInTextString();
	            	// Writes formatted text to PDF file
	            	writeToPDFFile(doc, cont, myPage, inputString);
	            }
	            JavaPdfBoxCreateImage(doc,myPage,1);
	            doc.save(myFile);
	         // analyzes the text read into myFile (after it's been read in)
	            JavaPdfBoxReadText(myFile);	
	        }
	    } // end main
}

/*	Prints all lines read in
 * for (int i = 0; i < docAL.size(); ++i) {
	for (int j = 0; j < docAL.get(i).size(); ++j) {
		cont.newLine();
		String ln = docAL.get(i).get(j);
		System.out.println(ln);
		cont.showText(ln);
	}
}*/