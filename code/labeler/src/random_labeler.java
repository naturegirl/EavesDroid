import java.util.*;
import java.io.*;
public class random_labeler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int count = 0;
		int max;
		int num;
		String name;
		Random rand = new Random();
		Scanner scanner = new Scanner(System.in);
		System.out.println("please enter the file name:");
		name = scanner.next();
		System.out.println("please enter how many letters you would like to record:");
		try{
			File file = new File("../data/"+name+".csv");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			max = scanner.nextInt();
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
