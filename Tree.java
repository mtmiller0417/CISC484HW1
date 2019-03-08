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
		int index;	// location of attribute in super array
		ArrayList<Integer> subset; //array of acceptable index (values)
		AttributeNode zero;	//left (Attribute=0) means value of 0
		AttributeNode one;	//right (Attribute=1) means value of 1
		public AttributeNode(int index, ArrayList<Integer> subset) {
			this.index = index;
			this.subset = subset;
		}

		@Override
		public String toString(){
			return ((Integer)index).toString();
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
		int rootIndex = chooseRandomAttribute(set);
		root = new AttributeNode(rootIndex, set);
		expandNode(root);
	}

		
	public void expandNode(AttributeNode node){
		ArrayList<Integer> subset0 = getSubset(node, 0);
		ArrayList<Integer> subset1 = getSubset(node, 1);
		if (isPure(subset0)) {
			node.zero = null;
			System.out.println(getResult(node, 0));
		} else {
			int index = chooseRandomAttribute(subset0);
			node.zero = new AttributeNode(index, subset0);
			expandNode(node.zero);
		}

		if (isPure(subset1)) {
			node.one = null;
			System.out.println(getResult(node, 1));
		} else {
			int index = chooseRandomAttribute(subset1);
			node.one = new AttributeNode(index, subset1);
			expandNode(node.one);
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

		if(p0/p == 0 || p0/1 == 0)//This is only true when one is 'pure'
			return 0;//This always equals 0
		else //This is true if neither is 'pure'
			return -(p0/p)*log2(p0/p) - (p1/p)*log2(p1/p);//Calculate and return Entropy
		
	}

	//Used to calc log base 2
	double log2(double d){
		return (Math.log(d)/Math.log(2.0));
	}

	//Returns the index of the attribute that provides the most gain
	//double entropyS = entropy of the big set, used to calculate entropy gain
	//int index = the index of current attribute, excluded from seach when looking
	//ArrayList<Integer> set = Set of indexes that make up current subset
	int maxGain(double entropyS, int index, ArrayList<Integer> set){
		double[] gain = new double[trainingData.size()-1];//Used to hold the gain for each attribute excpet itself(index) and the class attribute
		gain[index] = 0.0;
		for(ArrayList<Integer> attribute : trainingData){
			//If not equal to the index or the class attribute(last attribute in trainingData)
			if(trainingData.indexOf(attribute) != index || trainingData.indexOf(attribute) != trainingData.size()-1){
				//Create subset0
				//double s0 = entropy(subset0)
				//Create subset1
				//double s1 = entropy(subset0)
				ArrayList <Integer> subset0 = new ArrayList<Integer>(); 
				ArrayList <Integer> subset1 = new ArrayList<Integer>();
				for(int x : set){
					if(x == 0)
						subset0.add(new Integer(0));
					else if(x == 1)
						subset1.add(new Integer(1));
				} 
				double s0 = getEntropy(subset0);
				double s1 = getEntropy(subset1);
				gain[trainingData.indexOf(attribute)] = entropyS - ((subset0.size()/set.size())*s0 + ((subset1.size()/set.size())*s1));
			}
		}
		double max = 0;
		int maxIndex = 0;//i = index of the max
		for(int x = 0; x < gain.length; x++){
			if(gain[x] > max){
				max = gain[x];//Update max
				maxIndex = x;//Update the index of the max
			}
		}
		return maxIndex;
	}
}
