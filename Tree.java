import java.util.*;

enum Label{
	YES, NO, PARENT
}

class Node{
	String name;
	int index;
	ArrayList<Integer> subset;
	Node left;
	Node right;
	Label label;
}
