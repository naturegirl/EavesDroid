import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Accuracy {

    public int num_found;
    public int num_files;
    
    Accuracy() {
        this.num_found = 0;
        this.num_files = 0;
    }
    
    public void processDirectory(File dir) throws FileNotFoundException {
        File[] files = dir.listFiles();
        this.num_files = files.length;
        for (File file : files) {
            this.processWordsFile(file);
        }
    }
    
    private void processWordsFile(File file) throws FileNotFoundException {
        String label = file.getName().split("\\.")[0];
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.indexOf(label) != -1) {
                this.num_found++;
                break;
            }
        }
        scanner.close();
    }

    /**
     * @param args
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        String dir_path = "../../data/possible-words";
        Accuracy accuracy = new Accuracy();
        accuracy.processDirectory(new File(dir_path));
        System.out.println(String.format("Accuracy: %d/%d (%.2f)",
                accuracy.num_found, accuracy.num_files,
                (double)accuracy.num_found / accuracy.num_files * 100
                ));
    }

}
