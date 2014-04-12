import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * @author aks0
 *
 */
public class GForceComputation {
    
    public static final double G = 9.81;
    public static final BigInteger FACTOR = new BigInteger("1000");

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Usage: java GForceComputation " +
                    "<directory to traverse>");
            System.exit(0);
        }
        String path = "./" + args[0];
        File f = new File(path);
        File[] files = f.listFiles();
        GForceComputation ob = new GForceComputation();
        for (File file : files) {
            if (file.getAbsolutePath().endsWith(".csv")) {
                String datafile = file.getName().replaceFirst("[.][^.]+$", "");
                datafile = path + "/" + datafile + ".data.csv";
                ob.processCSV(file, datafile);
            }
        }
    }

    /**
     * Reads the csv file and outputs the time vs. gforce computation for
     * each row of data
     * 
     * @param file csv file to read the data from 
     * @param datafile name of the output .data.csv file
     * @throws IOException
     */
    private void processCSV(File file, String datafile) throws IOException {
        Scanner ob = new Scanner(file);
        PrintWriter pw = new PrintWriter(
                new BufferedWriter(new FileWriter(datafile)));
        BigInteger start_time = null;
        while(ob.hasNextLine()) {
            StringTokenizer data = new StringTokenizer(ob.nextLine(), ",");
            BigInteger time = new BigInteger(data.nextToken());
            if (start_time == null) {
                start_time = time;
                time = BigInteger.ZERO;
            } else {
                // convert time passed to micro-second
                time = time.subtract(start_time).divide(FACTOR);
            }
            double x = Double.parseDouble(data.nextToken());
            double y = Double.parseDouble(data.nextToken());
            double z = Double.parseDouble(data.nextToken());
            double gforce = Math.sqrt(x*x + y*y + z*z) - G;
            pw.println(time + ", " + gforce);
        }
        pw.close();
        ob.close();
    }

}
