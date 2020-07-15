import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class DuplicateHandler {
	void removeDuplicateAndWriteOutputFile() throws Exception {
		System.out.println("Removing Duplicates");
		File duplicateFile = new File(Configuration.FILE_PATH,Configuration.DUPLICATE_OUTPUT_FILE_NAME+"-1"+".txt");
		BufferedReader bufferedReader = new BufferedReader(new FileReader(duplicateFile));
		File outputFile = new File(Configuration.FILE_PATH+File.separator+Configuration.OUTPUT_FILE_NAME);
        outputFile.createNewFile();
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
        String line = bufferedReader.readLine();
        bufferedWriter.write(line);
    	bufferedWriter.newLine();
        String previousLine = line;
        int numberOfTuples = 0;
        while ((line = bufferedReader.readLine()) != null) {
        	if(line.length()>0) {
	            if(!line.substring(0, Configuration.EMP_ID_LENGTH).equals(previousLine.substring(0,Configuration.EMP_ID_LENGTH))) {
	            	bufferedWriter.write(line);
	            	numberOfTuples++;
	            	bufferedWriter.newLine();
	            	previousLine = line;
	            }
            }
        }
        System.out.println("Total Number of Tuples: "+ (numberOfTuples+1));
        bufferedReader.close();
        bufferedWriter.close();
	}
}