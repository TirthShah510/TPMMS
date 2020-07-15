import java.io.IOException;

public class main {

    public static void main(String args[]) throws Exception {
        System.gc();
        Runtime runtime = Runtime.getRuntime();
        System.out.println("System Available Memory: " + (float) (runtime.totalMemory() / 1000000) + " MB");
        long begin = System.currentTimeMillis();
        PhaseOne phaseOne = new PhaseOne();
        phaseOne.mergeInputFiles();
        PhaseTwo phaseTwo = new PhaseTwo();
        int numberOfBlocks = phaseTwo.start();
        System.out.println("Total Number Of Blocks: " + numberOfBlocks);
        DuplicateHandler duplicateHandler = new DuplicateHandler();
        duplicateHandler.removeDuplicateAndWriteOutputFile();
        long end = System.currentTimeMillis();
        System.out.println("Total Time: " + (end - begin) + " ms" + ", " + (end - begin) / 1000 + " sec");
        System.out.print("Disk I/O: " + (2 * numberOfBlocks + " for writing the blocks and "
                + "reading the same number of blocks to sort and write the output to the file."));
    }
}