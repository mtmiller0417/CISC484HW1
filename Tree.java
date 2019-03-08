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

}
