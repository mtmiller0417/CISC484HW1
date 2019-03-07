import java.util.*;

//TODO:
//	* Impliment graph
//	* Use a placeholder function that just chooses a random attribute

public class Tree {
	private Node root;
	ArrayList<ArrayList<Integer>> trainingData;
	String[] trainingDataNames;

	public class Node {
		int index;	// location in super array
		ArrayList<Integer> subset; //array of acceptable index
		Node zero;	//left
		Node one;	//right

		public Node(int index, ArrayList<Integer> subset) {
			this.index = index;
			this.subset = subset;
		}


		@Override
		public String toString(){
			return trainingDataNames[index];
		}
	}

	public Tree(ArrayList<ArrayList<Integer>> trainingData, String[] trainingDataNames) {
		this.trainingData = trainingData;
		this.trainingDataNames = trainingDataNames;

	}

	public void buildTree() {
		int rootIndex = chooseRandomAttribute();
		ArrayList<Integer> set = new ArrayList<>();
		System.out.println(trainingData);
		for(int i = 0; i < trainingData.size(); i++) {
			set.add(i);
		}

		root = new Node(rootIndex, set);
		System.out.println(root);
		
		
	}


	// Returns the subset of usable data at a Node
	ArrayList<Integer> get_subset(Node parent, int val) {
		ArrayList<Integer> newSubset = new ArrayList<>();
		for (Integer ki: parent.subset)
			if(trainingData.get(parent.index).get(ki)==val)
				newSubset.add(ki);
		return newSubset;
	}

	int chooseRandomAttribute(){
		return 0;
	}
	boolean isPure(){
		return true;
	}

}
