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
			return trainingDataNames[index];
		}
	}
	
	public Tree(ArrayList<ArrayList<Integer>> data, String[] names) {
		this.trainingData = data;
		this.trainingDataNames = names;
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
			double entropyS = getEntropy(node.subset);
			//System.out.println(""+entropyS);
			int ind = maxGain(entropyS, node.subset, node.attrIgnore);
			node.index = ind;
			System.out.printf("%d", ind);
			ArrayList<Integer> subset0 = getSubset(node, 0);
			ArrayList<Integer> subset1 = getSubset(node, 1);

			ArrayList<Integer> chAttr = node.attrIgnore;
			chAttr.add(node.index); //adding attributes we've already split on 	

			node.zero = new AttributeNode(-1, subset0, chAttr);
			node.one = new AttributeNode(-1, subset1, chAttr);
			//TO DO: ONLY WORKS IF ONE OF THESE EXPANDNODES IS COMMENTED OUT
			expandNode(node.zero); 
			//System.out.println();
			//expandNode(node.one);
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
		if (subset.isEmpty()){ 	// TODO: not entirely sure what we do when
			return true;		// a subset is empty, i think if empty we
								// should ignore that leaf
		}
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
		String printString = "";
		printString += prefix + node + " = 0 : ";
		if (node.zero == null)
			printString += "" + getResult(node,0) + "\n";
		else
			printString += "\n" + nodeString(node.zero, prefix + " | ");
		printString += prefix + node + " = 1 : ";
		if (node.one == null)
			printString += "" + getResult(node,1) + "\n";
		else
			printString += "\n" + nodeString(node.one, prefix + " | ");
		return printString;
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

	//Used to calc log base 2
	double log2(double d){
		return (Math.log(d)/Math.log(2.0));
	}

	//Returns the index of the attribute that provides the most gain
	//double entropyS = entropy of the big set, used to calculate entropy gain
	//ArrayList<Integer> set = Set of indexes that make up current subset
	//attrIgnore = attributes that have already been split on
	int maxGain(double entropyS, ArrayList<Integer> set, ArrayList<Integer> attrIgnore){
		double[] gain = new double[trainingData.size()]; //Used to hold the gain for each attribute excpet the class attribute
		for(int attribute = 0; attribute < trainingData.size(); attribute++)
			if (attrIgnore.contains(attribute))
				gain[attribute] = 0.0; //if attribute has already been split on, set gain to 0
			else {
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
				double s0 = getEntropy(subset0);
				double s1 = getEntropy(subset1);
				double size = set.size();
				gain[attribute] = entropyS - (((subset0.size()/size)*s0) + ((subset1.size()/size)*s1));
			}
		double max = gain[0];
		int maxIndex = 0; //i = index of the max
		for(int x = 1; x < gain.length-1; x++){
			//System.out.printf("%f ", gain[x]);
			if(gain[x] > max){
				max = gain[x];//Update max
				maxIndex = x;//Update the index of the max
			}
		}
		return maxIndex;
	}
}
