import java.util.*;
import java.io.*;

public class tree_height {
    class FastScanner {
		StringTokenizer tok = new StringTokenizer("");
		BufferedReader in;

		FastScanner() {
			in = new BufferedReader(new InputStreamReader(System.in));
		}

		String next() throws IOException {
			while (!tok.hasMoreElements())
				tok = new StringTokenizer(in.readLine());
			return tok.nextToken();
		}
		int nextInt() throws IOException {
			return Integer.parseInt(next());
		}
	}

	public class TreeHeight {
		int n;
		int parent[];
		Node root;
		Node[] nodes;
		
		void read() throws IOException {

			FastScanner in = new FastScanner();
			n = in.nextInt();
			parent = new int[n];
			nodes = new Node[n];			
			for (int i = 0; i < n; i++) {
				parent[i] = in.nextInt(); // create array with parent node as value

				Node node = new Node(); // create tree node
				node.value = i; // Value of node is simply the sequence which the integers are given
				nodes[i] = node; // set in such a way that the index of nodes array corresponds to the value
			}

			for(int i = 0; i<n; i++) { // loop through the parent array to set the children of each node.
				if(parent[i] == -1) {
					root = nodes[i];
				} else {
					nodes[parent[i]].addChild(nodes[i]); // because of node array is index according to it's value, just set it's child accordingly.
				}
			}

		}

		class Node {
			int value;
			Stack<Node> children = new Stack<Node>(); // Variable number of children, have to use stack or queue

			void addChild(Node n) {
				children.push(n);
			}

			Node getChild() {
				return children.pop();
			}

			boolean hasChildren() {
				return !children.empty();
			}
		}		

		Integer computeHeightFast() {
			Stack<Node> nodeStack = new Stack<Node>(); // node stack to keep track of nodes
			Stack<Integer> levelStack = new Stack<Integer>(); // Level stack, to keep track of current tree height
			Integer maxHeight = 1;
			nodeStack.push(root);
			levelStack.push(1);

			while(!nodeStack.empty()) { // Interesting point: Queues will enable breadth first, stack enables depth first
				Node current = nodeStack.pop(); // remove node at top of stack
				Integer currentHeight = levelStack.pop(); // set current height where the node is at
				if(currentHeight > maxHeight) maxHeight = currentHeight;

				while(current.hasChildren()) { // while node has children, push it to the node stack. Also push the height where the children are located to top of level stack. (i.e 1 level higher)
					nodeStack.push(current.getChild());
					levelStack.push(currentHeight + 1);
				}
			}
			return maxHeight;
		}

// Slower Naive Algorithm, for each element backtrace until parent while counting height. O(n2)
		// int computeHeight() {
  //                       // Replace this code with a faster implementation
		// 	int maxHeight = 0;
		// 	for (int vertex = 0; vertex < n; vertex++) {
		// 		int height = 0;
		// 		for (int i = vertex; i != -1; i = parent[i])
		// 			height++;
		// 		maxHeight = Math.max(maxHeight, height);
		// 	}
		// 	return maxHeight;
		// }		
	}

	static public void main(String[] args) throws IOException {
            new Thread(null, new Runnable() {
                    public void run() {
                        try {
                            new tree_height().run();
                        } catch (IOException e) {
                        }
                    }
                }, "1", 1 << 26).start();
	}
	public void run() throws IOException {
		TreeHeight tree = new TreeHeight();
		tree.read();
		// System.out.println(tree.computeHeight()); for slower algo
		System.out.println(tree.computeHeightFast());
	}
}
