import java.io.*;

public class PhaseOne {

    PhaseOne() {}

    void mergeInputFiles() {
        try {
        	System.out.println("Merging Input Files");
        	File input1 = new File(Configuration.FILE_PATH,Configuration.INPUT_FILE_NAME_1);
            File input2 = new File(Configuration.FILE_PATH,Configuration.INPUT_FILE_NAME_2);
            File mergedFile = new File(Configuration.FILE_PATH+File.separator+Configuration.MERGED_FILE);
            mergedFile.createNewFile();
            BufferedReader bufferedReader = null;
            FileWriter fileWriter = null;
            bufferedReader = new BufferedReader(new FileReader(input1));
            fileWriter = new FileWriter(mergedFile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            String line;
            int count1=0,count2=0;
            while ((line = bufferedReader.readLine()) != null) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            bufferedReader.close();
            bufferedReader = new BufferedReader(new FileReader(input2));
            while ((line = bufferedReader.readLine()) != null) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            bufferedReader.close();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
}