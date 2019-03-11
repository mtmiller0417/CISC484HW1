import java.util.*;



// TODO:
//  * We'll still need to print in
//  * We have to implement the post-pruning stuff
//  * Replace the choose random stuff
//  * Actual decision-making stuff (traverse tree):
//  	* Null == leaf node
//  	* Use checkResult of parent node's subset and leaf node's value
//  	  to see what answer a leaf node gives

public class Tree {
	private AttributeNode root;
	ArrayList<ArrayList<Integer>> trainingData;
	String[] trainingDataNames;
	int heuristicNumber;

	public class AttributeNode {
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
	public Tree(ArrayList<ArrayList<Integer>> data, String[] names, int heuristicNumber) {
		this.trainingData = data;
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
			System.out.println("Maximizing Index: " + ind);
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
				expandNode(node.zero);
			}
			if (subset1.isEmpty()){
				node.one = null;
			} else {
				node.one = new AttributeNode(-1, subset1, chAttr);
				expandNode(node.one);
			}
			} else {
				node.one = null;
				node.zero = null;
				node.value = -1;	
			}
		}
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
				if(size == 0)
					System.exit(-1);
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

		System.out.println(max);
		return maxIndex;
	}
}
