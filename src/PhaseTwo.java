import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PhaseTwo {

    PhaseTwo() {
    }

    Comparator<String> employeeIdAndDateComparator = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            int idCompare = o1.substring(0, Configuration.EMP_ID_LENGTH).compareTo(o2.substring(0, Configuration.EMP_ID_LENGTH));
            DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
            Date date1 = null;
            Date date2 = null;
            try {
                date1 = format.parse(o1.substring(Configuration.EMP_ID_LENGTH, 18));
                date2 = format.parse(o2.substring(Configuration.EMP_ID_LENGTH, 18));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int dateCompare = date2.compareTo(date1);

            if (idCompare == 0) {
                return dateCompare == 0 ? idCompare : dateCompare;
            } else {
                return idCompare;
            }
        }
    };

    int start() throws IOException {
        File mergedFile = new File(Configuration.FILE_PATH, Configuration.MERGED_FILE);
        long usableMemorySize = getUsableMemorySize();
        return readFileAndDivideIntoBlocks(mergedFile, usableMemorySize);
    }

    long getUsableMemorySize() {
        System.gc();
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        return (runtime.totalMemory() - usedMemory) / 3;
    }

    int readFileAndDivideIntoBlocks(File file, long usableMemorySize) throws IOException {
        long blockSize = 0;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        System.out.println("Creating 4KB Blocks");
        String tuple;
        ArrayList<String> blockData = new ArrayList<>();
        ArrayList<File> blockFiles = new ArrayList<>();
        int index = 0;
        int count = 0;
        while ((tuple = bufferedReader.readLine()) != null) {
            if (count >= Configuration.MAX_TUPLES) {
                File tempBlock = sortBlock(blockData, index);
                blockFiles.add(tempBlock);
                index++;
                count = 0;
                blockData.clear();
            }
            if (tuple != null) {
                blockData.add(tuple);
                count++;
            }
        }
        if (!blockData.isEmpty()) {
            index++;
            File tempBlock = sortBlock(blockData, index);
            blockFiles.add(tempBlock);
        }
        System.out.println("Total Number Of Blocks: " + index);
        createBiggerBlocks(blockFiles);
        return index;
    }

    File sortBlock(ArrayList<String> blockData, int index) throws IOException {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        Collections.sort(blockData, employeeIdAndDateComparator);
        File dir = new File(Configuration.BLOCK_FILE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File tempBlock = new File(Configuration.BLOCK_FILE_PATH + File.separator + Configuration.BLOCK_NAME + index);
        tempBlock.createNewFile();
        tempBlock.deleteOnExit();
        fileWriter = new FileWriter(tempBlock);
        bufferedWriter = new BufferedWriter(fileWriter);
        for (String line : blockData) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
        return tempBlock;
    }

    void createBiggerBlocks(ArrayList<File> blockFiles) throws IOException {
        System.out.println("Bigger blocks");
        ArrayList<Handler> FileHandler = new ArrayList<>();
        int index = 0;
        int i = 0;
        ArrayList<File> biggerBlocks = new ArrayList<>();
        for (File blockFile : blockFiles) {
            if (i >= 250) {
                i = 0;
                biggerBlocks.add(merge(FileHandler, index));
                FileHandler.clear();
                index++;
            }
            FileHandler.add(new Handler(new BufferedReader(new FileReader(blockFile))));
            i++;
        }
        if (i != 0) {
            biggerBlocks.add(merge(FileHandler, index));
            FileHandler.clear();
            i = 0;
        }
        System.out.println("Merging Bigger Blocks");
        createBufferReaderObjectsOfBlocks(biggerBlocks);
    }

    void createBufferReaderObjectsOfBlocks(ArrayList<File> blockFiles) throws IOException {
        ArrayList<Handler> FileHandler = new ArrayList<>();
        for (File blockFile : blockFiles) {
            FileHandler.add(new Handler(new BufferedReader(new FileReader(blockFile))));
        }
        merge(FileHandler, -1);
    }

    File merge(ArrayList<Handler> FileHandler, int index) throws IOException {
        File outputFile = new File(Configuration.FILE_PATH, Configuration.DUPLICATE_OUTPUT_FILE_NAME + index + ".txt");
        FileWriter fileWriter = new FileWriter(outputFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        PriorityQueue<Handler> priorityQueue = new PriorityQueue<>(new Comparator<Handler>() {
            @Override
            public int compare(Handler o1, Handler o2) {
                return employeeIdAndDateComparator.compare(o1.getCurrentLine(), o2.getCurrentLine());
            }
        });
        for (Handler bufferedBlock : FileHandler) {
            if (!bufferedBlock.isEmpty()) {
                priorityQueue.add(bufferedBlock);
            }
        }
        while (priorityQueue.size() > 0) {
            Handler block = priorityQueue.poll();
            bufferedWriter.write(block.readAndRemoveFromFile());
            bufferedWriter.newLine();
            if (!block.isEmpty()) {
                priorityQueue.add(block);
            }
        }
        bufferedWriter.close();
        outputFile.deleteOnExit();
        return outputFile;
    }
}
