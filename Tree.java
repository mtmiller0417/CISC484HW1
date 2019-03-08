import java.util.*;



// Stuff About the Tree:
// Each node of the tree is an attribute

public class Tree {
	private AttributeNode root;
	ArrayList<ArrayList<Integer>> trainingData;
	String[] trainingDataNames;

	public class AttributeNode {
		int index;	// location of attribute in super array
		ArrayList<Integer> subset; //array of acceptable index (values)
		AttributeNode zero;	//left (Attribute=0)
		AttributeNode one;	//right (Attribute=1)
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
		} else {
			int index = chooseRandomAttribute(subset0);
			node.zero = new AttributeNode(index, subset0);
			expandNode(node.zero);
		}

		if (isPure(subset1)) {
			node.one = null;
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

	int chooseRandomAttribute(ArrayList<Integer> subset){
		Random random = new Random();
		return random.nextInt(trainingData.size());
	}

	// true if subset is pure
	boolean isPure(ArrayList<Integer> subset){
		if (subset.isEmpty()){ 	// TODO: not entirely sure what we do when
			return true;		// a subset is empty
		}
		int firstVal =
			trainingData.get(trainingData.size()-1).get(subset.get(0));
		for (Integer i: subset)
			if (trainingData.get(trainingData.size()-1).get(i) != firstVal)
				return false;
		return true;
	}

	int getAnswer(){
		return 0;
	}

}
