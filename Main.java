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
	//String trainingData1 = "data_sets1/training_set.csv";
	String trainingData1 = "data_sets1/trial_set.csv";
	String validationData1 = "data_sets1/validation_set.csv";
	String testData1 = "data_sets1/test_set.csv";
	String trData = null, vaData = null, tsData = null, toPrint = null;// Space for actual data
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

		if(args.length > 0)
			L = Integer.parseInt(args[0]);
		if(args.length > 1)
			K = Integer.parseInt(args[1]);
		if(args.length > 2)
			trData = args[2];
		if(args.length > 3)
			vaData = args[3];
		if(args.length > 4)
			tsData = args[4];
		if(args.length > 5)
			toPrint = args[5];

		for(String s:args){
			System.out.println(s);
		}

		trainingData = new ArrayList<ArrayList<Integer>>();
		validationData = new ArrayList<ArrayList<Integer>>();
		testingData = new ArrayList<ArrayList<Integer>>();

		trainingDataNames = new String[21];
		validationDataNames = new String[21];
		testingDataNames = new String[21];

		if(trData == null)
			trData = trainingData1;
		if(vaData == null)
			vrData = validationData1;
		if(tsData == null)
			tsData = testData1;

		trainingDataNames = parseInput(trainingData, trainingDataNames, trData);
		validationDataNames = parseInput(validationData, validationDataNames, vaData);
		testingDataNames = parseInput(testingData, testingDataNames, tsData);


		int entropy = 0, variance = 1;
		Tree tree = new Tree(trainingData, trainingDataNames, variance);
		tree.buildDecisonTree();

		toPrint = "yes";//Here just to assume that we want to print ours for testing purposes

		if(toPrint.equals("yes") || toPrint.equals("Yes"))
			tree.printTree();
		/*
		Tree t = new Tree(m.trainingData, m.trainingDataNames, variance);
		t.buildDecisonTree();
		t.printTree();
		*/

		System.out.println(trainingData.get(0).size());
	}

		public String[] parseInput(ArrayList<ArrayList<Integer>> set, String[] attNames, String fileLocation) throws IOException{
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
				s.length();// Used to get rid of error of unused variable
			}

			while((line = br.readLine()) != null){
				String[] row = line.split(csvSplitby);

				for(int i = 0; i < set.size(); i++)
				 	set.get(i).add(Integer.parseInt(row[i]));
			 }

		 	System.out.println("Size " + set.get(0).size());

			br.close();

			return attNames;
		}
}
