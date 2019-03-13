import java.util.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


// TODO:
//  * We'll still need to print in
//  * We have to implement the post-pruning stuff
//  * Replace the choose random stuff
//  * Actual decision-making stuff (traverse tree):
//  	* Null == leaf node
//  	* Use checkResult of parent node's subset and leaf node's value
//  	  to see what answer a leaf node gives

public class Tree implements Serializable{
    	private static final long serialVersionUID = 1L;
	AttributeNode root;
	ArrayList<ArrayList<Integer>> trainingData;
	ArrayList<ArrayList<Integer>> validationData;
	String[] trainingDataNames;
	int heuristicNumber;

	public Tree deepCopy() throws Exception
	{
		//Serialization of object
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bos);
		out.writeObject(this);

		//De-serialization of object
		ByteArrayInputStream bis = new   ByteArrayInputStream(bos.toByteArray());
		ObjectInputStream in = new ObjectInputStream(bis);
		Tree copied = (Tree) in.readObject();

		//Verify that object is not corrupt

		//validateNameParts(fName);
		//validateNameParts(lName);

		return copied;
	}

	public class AttributeNode implements Serializable{
    		private static final long serialVersionUID = 1L;
		int index;	// location of attribute in super array; leaf nodes have index -1
		ArrayList<Integer> subset; //array of acceptable index (rows)
		ArrayList<Integer> attrIgnore; //attributes to ignore (columns);
		AttributeNode zero;	//left (Attribute=0) means value of 0
		AttributeNode one;	//right (Attribute=1) means value of 1
		int value; //-1 for internal nodes, 0 or 1 for leaf nodes
		public AttributeNode(int index, ArrayList<Integer> subset, ArrayList<Integer> attrIgnore) {
			this.index = index;
			this.subset = subset;
			this.attrIgnore = attrIgnore;
		}

		@Override
		public String toString(){
			if (index != -1)
				return trainingDataNames[index];
			else return "";
		}
	}
	
	//If heuristicNumber is 0, will run Entropy heuristic, if 1, will run varianceImpurity heruistic
	public Tree(ArrayList<ArrayList<Integer>> tdata, ArrayList<ArrayList<Integer>> vdata, String[] names, int heuristicNumber) {
		this.trainingData = tdata;
		this.validationData = vdata;
		this.trainingDataNames = names;
		this.heuristicNumber = heuristicNumber;
	}

	
	// Main Method
	public void buildDecisonTree() {
		ArrayList<Integer> set = new ArrayList<>();
		for(int i = 0; i < trainingData.get(0).size(); i++) {
			set.add(i);
		}
		ArrayList<Integer> attr = new ArrayList<>();
		attr.add(trainingData.size() - 1); //ignore class variable
		int rootIndex = -1; //since we don't know the initial attribute to split on
		
		root = new AttributeNode(rootIndex, set, attr);
		expandNode(root);
	}

	//Recursive function that expands given node by checking if it is pure 
	public void expandNode(AttributeNode node){
		if (isPure(node.subset)) {
			node.zero = null;
			node.one = null;
			node.value = trainingData.get(trainingData.size() - 1).get(node.subset.get(0));
		} else {
			node.value = -1;
			//double entropyS = getEntropy(node.subset);
			double gainS = getEntropy(node.subset);;
			if(heuristicNumber == 0)
				gainS = getEntropy(node.subset);
			/*else if(heuristicNumber == 1)
				gainS = varianceImpurity(node.subset);*/

			int ind = maxGain(gainS, node.subset, node.attrIgnore);
			//System.out.println("Maximizing Index: " + ind);
			if (ind != -1)
			{
			node.index = ind;
			ArrayList<Integer> subset0 = getSubset(node, 0);
			ArrayList<Integer> subset1 = getSubset(node, 1);

			ArrayList<Integer> chAttr = node.attrIgnore;
			chAttr.add(node.index); //adding attributes we've already split on 	
			if (subset0.isEmpty()){
				node.zero = null;
			} else {
				node.zero = new AttributeNode(-1, subset0, chAttr);
				if(contradictionCheck(node.zero.subset) == true){
					node.zero.value = (int)(Math.random() * 1 + 1);//Choose a random value bc of contradiction
					//node.zero = null;
				}
				else
					expandNode(node.zero);
			}
			if (subset1.isEmpty()){
				node.one = null;
			} else {
				node.one = new AttributeNode(-1, subset1, chAttr);
				if(contradictionCheck(node.one.subset) == true){
					node.one.value = (int)(Math.random() * 1 + 1);//Choose a random value bc of contradiction
					//node.one = null;
				}
				else
					expandNode(node.one);
			}
			} else {
				node.one = null;
				node.zero = null;
				node.value = -1;	
			}
		}
	}

	boolean contradictionCheck(ArrayList<Integer> set){
		//boolean flag = true;
		int[] array = new int[trainingData.size()];
		for(int i = 0; i < trainingData.size(); i++)
			array[i] = trainingData.get(i).get(set.get(0));

		for(int x = 1; x < set.size(); x++){ // Each element in set
			for(int y = 0; y < trainingData.size()-1; y++){
				if(array[y] != trainingData.get(y).get(set.get(x)))
					return false;
			}
		}
		// All elements in set are equal in terms of attributes, now must check classes
		for(int x = 1; x < set.size(); x++){
			if(array[trainingData.size()-1] != trainingData.get(trainingData.size()-1).get(set.get(x))){
				for(int i : set){
					//System.out.print(i + " ");
				}
				//System.out.print("\n");
				//System.exit(-1);
				return true;
			}
		}
		return false;
	}
	
	// Returns the subset of usable data at an attribue node
	// given an attribute value for that node
	ArrayList<Integer> getSubset(AttributeNode parent, int val) {
		ArrayList<Integer> newSubset = new ArrayList<>();
		for (Integer i: parent.subset)
			if(trainingData.get(parent.index).get(i)==val)
				newSubset.add(i);
		return newSubset;
	}

	// Will be replaced by heurisitcs
	int chooseRandomAttribute(ArrayList<Integer> subset){
		Random random = new Random();
		return random.nextInt(trainingData.size());
	}

	// true if subset is pure
	boolean isPure(ArrayList<Integer> subset){
		/*if (subset.isEmpty()){ 	// TODO: not entirely sure what we do when
			return true;		// a subset is empty, i think if empty we
								// should ignore that leaf
		}*/
		int firstVal =
			trainingData.get(trainingData.size()-1).get(subset.get(0));
		for (Integer i: subset)
			if (trainingData.get(trainingData.size()-1).get(i) != firstVal)
				return false;
		return true;
	}

	// Only use if Node's val leads to leaf node, this is kinda confusing
	// should probably make more intuitive at some point idk
	// -1 means subset is empty

	int getResult(AttributeNode node, int val){
		ArrayList<Integer> subset = getSubset(node, val);
		if (subset.isEmpty())
			return -1;
		return trainingData.get(trainingData.size()-1).get(subset.get(0));
	}
	
	public void printTree() {
		if (root==null)
			System.out.println("root is null");
		else
			System.out.println(nodeString(root, ""));
	}
	public String nodeString(AttributeNode node, String prefix) {
		if (node == null)
			return prefix;
		else { 
			String printString = "";
			printString += prefix + node + " = 0 : ";
			if (node.zero != null) {
				if (node.zero.value != -1)
					printString += "" + node.zero.value + "\n";
				else
					printString += "\n" + nodeString(node.zero, prefix + " | ");
			} 
			printString += prefix + node + " = 1 : ";
			if (node.one != null) {
				if (node.one.value != -1)
					printString += "" + node.one.value + "\n";
				else
					printString += "\n" + nodeString(node.one, prefix + " | ");
			}
			return printString;
		}
	}	
	double getEntropy(ArrayList<Integer> set){
		int p0 = 0, p1 = 0, p;
		p = set.size();
		//Get the number of p0's and p1's in the set
		for(int x : set){
			if(trainingData.get(trainingData.size()-1).get(x) == 0)	
				p0++;
			else if(trainingData.get(trainingData.size()-1).get(x) == 1)
				p1++;
		}
		// Check if p0/p or p1/p is equal to 0...
		// If so will cause an error when doing log
		// If it is return entropy as 0
		if(p0 == p || p0 == 0)//This is only true when one is 'pure'
			return 0.0;//This always equals 0
		else //This is true if neither is 'pure'
		{
			double e = -(((double) p0)/p)*(log2(p0)-log2(p)) - (((double) p1)/p)*(log2(p1)-log2(p));
			//Calculate and return Entropy
			return e;
		}
		
	}
	
	public double varianceImpurity(ArrayList<Integer> set){
		int arraySize = set.size();
		double k0 = 0, k1 = 0, varianceImpurity; 

		for(int x : set){
			if(trainingData.get(trainingData.size()-1).get(x) == 0)
				k0++;
			else if(trainingData.get(trainingData.size()-1).get(x) == 1)
				k1++;
		}

		//Checks and will stop NaN errors
		if(k0 ==  arraySize|| k0 == arraySize)//This is only true when one is 'pure'
			return 0.0;//This always equals 0

		return ((k0 * k1)/((double)arraySize*(double)arraySize));
	}

	//Used to calc log base 2
	double log2(double d){
		return (Math.log(d)/Math.log(2.0));
	}

	//Returns the index of the attribute that provides the most gain
	//double entropyS = entropy of the big set, used to calculate entropy gain
	//ArrayList<Integer> set = Set of indexes that make up current subset
	//attrIgnore = attributes that have already been split on

	int maxGain(double gainS, ArrayList<Integer> set, ArrayList<Integer> attrIgnore){
		double[] gain = new double[trainingData.size()]; //Used to hold the gain for each attribute 
		for(int attribute = 0; attribute < trainingData.size(); attribute++) {
			ArrayList<Integer> myList = trainingData.get(attribute);
			ArrayList <Integer> subset0 = new ArrayList<Integer>(); 
			ArrayList <Integer> subset1 = new ArrayList<Integer>();
			for(int x : set){
				if(myList.get(x) == 0) {
					subset0.add(x);
				}
				else if(myList.get(x) == 1)
					subset1.add(x);
			}
			double s0 = 0, s1 = 0, size = set.size();
			if(heuristicNumber == 0){
				s0 = getEntropy(subset0);
				s1 = getEntropy(subset1);
				gain[attribute] = gainS - (((subset0.size()/size)*s0) + ((subset1.size()/size)*s1));
			}
			else if(heuristicNumber == 1){
				s0 = varianceImpurity(subset0);
				s1 = varianceImpurity(subset1);
				/*if(size == 0)
					System.exit(-1);*/
				gain[attribute] = 1 - (((double)subset0.size()/size)*s0 + ((double)subset1.size()/size)*s1);
			}
			
			
		}
		double max = gain[0];
		int maxIndex = 0; 
		for(int x = 1; x < gain.length-1; x++){
			if(gain[x] > max){
				max = gain[x]; //Update max
				maxIndex = x; //Update the index of the max
			}
		}

		//System.out.println(max);
		return maxIndex;
	}

	// Consumes input list, do not include class values in list
	int traverseTree(ArrayList<Integer> input){
		if (input.size() != trainingData.size()-1){
			System.out.println("error, incorrect input size");
			return -1;
		}
		AttributeNode CurNode = root;
		for (Integer i: input){
			//System.out.println(CurNode.value);
			//System.exit(-1);
			if (CurNode.value != -1)
				return CurNode.value;
			if(i == 0)
				CurNode = CurNode.zero;
			if (i == 1)
				CurNode = CurNode.one;
		}
		Random r = new Random();
		return r.nextInt(2);
	}
	ArrayList<Integer> getInput(
			ArrayList<ArrayList<Integer>> validationData,
			int index
			) 
	{
		ArrayList<Integer> result = new ArrayList<>();
		for(int i = 0; i<trainingData.size()-1; i++) {
			result.add(validationData.get(i).get(index));
		}
		return result;
	}
	double getAccuracy(ArrayList<ArrayList<Integer>> validationData) {
		int numCorrect=0;
		for(int i = 0; i<validationData.get(0).size(); i++) {
			if (traverseTree(getInput(validationData, i)) == validationData.get(validationData.size()-1).get(i))
				numCorrect ++;
		}
		return (double) numCorrect / (double) validationData.get(0).size(); 
	}

	ArrayList<AttributeNode> listAllNodes(){
		return listSubNodes(root);
	}
	ArrayList<AttributeNode> listSubNodes(AttributeNode n){
		ArrayList<AttributeNode> result =  new ArrayList<>();
		if (n.value != -1)
			return result;
		result.add(n);
		result.addAll(listSubNodes(n.zero));
		result.addAll(listSubNodes(n.one));
		return result;
	}

	public int nonLeafNodes(AttributeNode n){
		if (n == null)
			return 0;
		else if (n.zero == null && n.one == null)
			return 0;
	
		return 1 + nonLeafNodes(n.zero) + nonLeafNodes(n.one);	
	}

	public Tree postPrune(Tree D, int L, int K) throws Exception{
		Tree Dbest = D.deepCopy();
		for(int i = 1; i < L; i++){
			Tree D1 = D.deepCopy();
			Random random = new Random();
			int M = random.nextInt(K-1)+1; //to generate random number between 1 and K
			for(int j = 1; j < M; j++){
				int N = D1.nonLeafNodes(D1.root);
				int P = random.nextInt(N); //to generate random number between 1 and N
				ArrayList<AttributeNode> l = D1.listAllNodes();
				AttributeNode replace = l.get(P);
				int c0 = 0;
				int c1 = 0;
				for(int x: replace.subset)
					if (D1.trainingData.get(trainingData.size() - 1).get(x) == 0)
						c0++;
					else
						c1++;
			
				replace.index = -1; //mark as leaf node	
				replace.zero = null;
				replace.one = null;
			
				if (c0 > c1){
					replace.value = 0;
				} else
					replace.value = 1;
			}
		//evaluate accuracy of D1 and Dbest

		double d1Accuracy = D1.getAccuracy(D1.validationData);
		double dbAccuracy = Dbest.getAccuracy(Dbest.validationData);	
		//System.out.printf("DBest accuracy: %f D1 accuracy %f", dbAccuracy, d1Accuracy);
		//System.out.println();
		if (d1Accuracy > dbAccuracy)
			Dbest = D1;
		}
		
		//return Dbest	
		return Dbest;	
	}
}
