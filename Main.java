import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

	ArrayList<ArrayList<Integer>> trainingData;
	ArrayList<ArrayList<Integer>> validationData;
	ArrayList<ArrayList<Integer>> testingData;
	String[] trainingDataNames;
	String[] validationDataNames;
	String[] testingDataNames;
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

		trainingData = new ArrayList<ArrayList<Integer>>();
		validationData = new ArrayList<ArrayList<Integer>>();
		testingData = new ArrayList<ArrayList<Integer>>();

		parseInput(trainingData, trainingDataNames, trainingData1);
		parseInput(validationData, validationDataNames, validationData1);
		parseInput(testingData, testingDataNames, testData1);

		System.out.println(trainingData.get(0).size());
		}

		public void parseInput(ArrayList<ArrayList<Integer>> set, String [] attNames, String fileLocation) throws IOException{
			br = new BufferedReader(new FileReader(fileLocation));
			String line = "";
			String csvSplitby = ",";
			line = br.readLine();

			//Read the first line of the file (attribute names)
			attNames = line.split(csvSplitby);

			System.out.println("Number of attributes in data set: " + attNames.length);

			//Read the rest of the data files and store them in appropriate data structure

	    for(String s: attNames){
				set.add(new ArrayList<Integer>());
			}

			while((line = br.readLine()) != null){
				String[] row = line.split(csvSplitby);

				for(int i = 0; i < set.size(); i++)
				 	set.get(i).add(Integer.parseInt(row[i]));
			 }

		 System.out.println("Size " + set.get(0).size());

			br.close();
		}
}
