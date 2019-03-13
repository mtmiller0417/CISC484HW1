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
	//String trainingData1 = "data_sets1/trial_set.csv";
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

	public Main(String [] args)throws Exception{

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

		trainingData = new ArrayList<ArrayList<Integer>>();
		validationData = new ArrayList<ArrayList<Integer>>();
		testingData = new ArrayList<ArrayList<Integer>>();

		trainingDataNames = new String[21];
		validationDataNames = new String[21];
		testingDataNames = new String[21];

		if(trData == null)
			trData = trainingData1;
		if(vaData == null)
			vaData = validationData1;
		if(tsData == null)
			tsData = testData1;

		trainingDataNames = parseInput(trainingData, trainingDataNames, trData);
		validationDataNames = parseInput(validationData, validationDataNames, vaData);
		testingDataNames = parseInput(testingData, testingDataNames, tsData);

		int entropy = 0, variance = 1;

		double acc, acc1, acc2, acc3, acc4;

		Tree tree0 = new Tree(trainingData, validationData, trainingDataNames, entropy);
		tree0.buildDecisonTree();
		acc = tree0.getAccuracy(testingData);

		System.out.println("\nAccuracy on testingData w/ Heuristic One: " + acc);

		Tree tree1 = new Tree(trainingData, validationData, trainingDataNames, variance);
		tree1.buildDecisonTree();
		acc = tree1.getAccuracy(testingData);

		System.out.println("Accuracy on testingData w/ Heuristic Two: " + acc);

		//Heuristic one 

		Tree t1 = new Tree(trainingData, validationData ,trainingDataNames, entropy);
		t1.buildDecisonTree();
		if(toPrint.equals("yes") || toPrint.equals("Yes"))
			t1.printTree();
		acc1 = t1.getAccuracy(validationData);

		//Heuristic two 

		Tree t2 = new Tree(trainingData, validationData ,trainingDataNames, variance);
		t2.buildDecisonTree();
		if(toPrint.equals("yes") || toPrint.equals("Yes"))
			t2.printTree();
		acc2 = t2.getAccuracy(validationData);

		//Post-pruned heuristic one 

		Tree t3 = t1.postPrune(t1, L, K);
		if(toPrint.equals("yes") || toPrint.equals("Yes"))
			t3.printTree();
		acc3 = t3.getAccuracy(validationData);

		//Post-pruned heuristic two 

		Tree t4 = t2.postPrune(t2, L, K);
		if(toPrint.equals("yes") || toPrint.equals("Yes"))
			t4.printTree();
		acc4 = t4.getAccuracy(validationData);

		//Print all accuracies

		System.out.println("\nACCURACY");
		System.out.printf("Heristic one accuracy: %.3f\n", acc1);
		System.out.printf("Heristic two accuracy: %.3f\n", acc2);
		System.out.printf("Heristic one post-pruned accuracy: %.3f\n", acc3);
		System.out.printf("Heristic two post-pruned accuracy: %.3f\n", acc4);

	}

		public String[] parseInput(ArrayList<ArrayList<Integer>> set, String[] attNames, String fileLocation) throws IOException{
			br = new BufferedReader(new FileReader(fileLocation));
			String line = "";
			String csvSplitby = ",";
			line = br.readLine();

			//Read the first line of the file (attribute names)
			attNames = line.split(csvSplitby);

			//System.out.println("Number of attributes in data set: " + attNames.length);

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

		 	//System.out.println("Size " + set.get(0).size());

			br.close();

			return attNames;
		}
}
