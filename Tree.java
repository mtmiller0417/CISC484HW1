import java.util.*;

//TODO:
//	* Impliment graph
//	* Use a placeholder function that just chooses a random attribute


// nodes are basically attributes

public class Tree {
	private Node root;
	ArrayList<ArrayList<Integer>> trainingData;
	String[] trainingDataNames;

	public class Node {
		int index;	// location of attribute in super array
		ArrayList<Integer> subset; //array of acceptable index
		Node zero;	//left
		Node one;	//right
		public Node(int index, ArrayList<Integer> subset) {
			this.index = index;
			this.subset = subset;
		}
		// public Node leafNode(){
		// 	return 0;
		// }


		@Override
		public String toString(){
			return ((Integer)index).toString();
		}
	}

	public Tree(ArrayList<ArrayList<Integer>> data, String[] names) {
		this.trainingData = data;
		this.trainingDataNames = names;

	}

	public void buildTree() {
		ArrayList<Integer> set = new ArrayList<>();
		// System.out.println(trainingData);
		for(int i = 0; i < trainingData.get(0).size(); i++) {
			set.add(i);
		}
		int rootIndex = chooseRandomAttribute(set);
		root = new Node(rootIndex, set);
		expandNode(root, set);
	}


	public void expandNode(Node node, ArrayList<Integer> subset){
		System.out.println(node.index);
		ArrayList<Integer> subset0 = getSubset(node, 0);
		ArrayList<Integer> subset1 = getSubset(node, 1);
		if (isPure(subset0)) {
			node.zero = null;
		} else {
			int index = chooseRandomAttribute(subset0);
			node.zero = new Node(index, subset0);
			expandNode(node.zero, subset0);
		}

		if (isPure(subset1)) {
			node.one = null;
		} else {
			int index = chooseRandomAttribute(subset1);
			node.one = new Node(index, subset1);
			expandNode(node.one, subset1);
		}
		
	}

	// Returns the subset of usable data at an attribue node
	// given an attribute value
	ArrayList<Integer> getSubset(Node parent, int val) {
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

	boolean isPure(ArrayList<Integer> subset){
		if (subset.isEmpty())
			return true;
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
