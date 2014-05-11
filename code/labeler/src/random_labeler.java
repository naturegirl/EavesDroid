import java.util.*;
import java.io.*;
public class random_labeler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    if (args.length < 2) {
            System.out.println("Usage: java random_labeler num-letters filename"
                    );
            System.out.println("Example: java random_labeler 10 test");
            System.exit(0);
        }
    
		int count = 0;
		int max;
		int num;
		String name;
		Scanner scanner = new Scanner(System.in);
		Random rand = new Random();
		name = args[1];
		try{
			File file = new File("../data/"+name+".csv");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			max = Integer.parseInt(args[0]);
			System.out.println("*****************");
			while(count<max){
				scanner.nextLine();
				num = rand.nextInt(26)+65;
				System.out.print((char)num+"    --------   count: ");
				bw.write(new Integer(num-64).toString());
				bw.write("\n");
				count++;
				System.out.println(count);
			}
			bw.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}

}
