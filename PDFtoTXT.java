/*	@Author Jacob Elbirt
 * Test code to print pdf file contents line-by-line
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PDFtoTXT {
	public static void pdf2txt(String text, File outFile) throws IOException {
		FileWriter fw = new FileWriter(outFile);
		fw.write(text);
		fw.close();
	}
}
