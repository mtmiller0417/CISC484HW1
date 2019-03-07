import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

	ArrayList<ArrayList<Integer>> data;
	String[] attrNames;
	String trainingData1 = "data_sets1/training_set.csv";
	String validationData1 = "data_sets1/validation_set.csv";
	String testData1 = "data_sets1/test_set.csv";
	String trData, vaData, tsData, toPrint;// Space for actual data
	int L,K;
	BufferedReader br;

	public static void main(String [] args){
		try{
			Main m = new Main(args);
		}
		catch (Exception e) {
			System.out.println("ERROR");
			e.printStackTrace();
		}

	}

	public Main(String [] args)throws IOException{
		/*L = Integer.parseInt(args[0]);
		K = Integer.parseInt(args[1]);
		trData = args[2];
		vaData = args[3];
		tsData = args[4];
		toPrint = args[5];*/
		for(String s:args){
			System.out.println(s);
		}

		data = new ArrayList<ArrayList<Integer>>();
		br = new BufferedReader(new FileReader(trainingData1));
		String line = "";
		String csvSplitby = ",";
		line = br.readLine();

		//Read the first line of the file (attribute names)
		attrNames = line.split(csvSplitby);

		System.out.println("Number of attributes in data set: " + attrNames.length);
		

		//Read the rest of the data files and store them in appropriate data structure

    for(String s: attrNames){
			data.add(new ArrayList<Integer>());
		}

		while((line = br.readLine()) != null){
			String[] row = line.split(csvSplitby);

			for(int i = 0; i < data.size(); i++)
			 	data.get(i).add(Integer.parseInt(row[i]));
		 }

	 	System.out.println("Size " + data.get(0).size());
	


		br.close();
		}

		//Inputs?
		// Set(s)?, holding variables
		public void parseInput(ArrayList<ArrayList<Integer>> set, String [] attNames){

		}

		public double varianceImpurtity(ArrayList<ArrayList<Integer>> bigSet){

			double varianceImpurtity;
			int k0, k1 = 0;
			int arraySize = bigSet.get(0).size();

			for(int i = 0; i < arraySize; i++){
				if()
			}

			return .0;
		}
}
