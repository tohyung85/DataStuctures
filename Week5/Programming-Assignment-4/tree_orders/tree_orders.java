import java.util.*;
import java.io.*;

public class tree_orders {
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

	public class TreeOrders {
		int n;
		int[] key, left, right;

    private class Node {
      int key;
      Node left;
      Node right;
    }
		
		void read() throws IOException {
			FastScanner in = new FastScanner();
			n = in.nextInt();
			key = new int[n];
			left = new int[n];
			right = new int[n];
			for (int i = 0; i < n; i++) { 
				key[i] = in.nextInt();
				left[i] = in.nextInt();
				right[i] = in.nextInt();
			}
		}

		List<Integer> inOrder() {
			ArrayList<Integer> result = new ArrayList<Integer>();
                        // Finish the implementation
                        // You may need to add a new recursive method to do that
      traverseInOrder(0, result);
                        
			return result;
		}

    void traverseInOrder(int vertice, ArrayList<Integer> result) {
      if(vertice == -1) return;
      traverseInOrder(left[vertice], result);
      result.add(key[vertice]);
      traverseInOrder(right[vertice], result);
    }

		List<Integer> preOrder() {
			ArrayList<Integer> result = new ArrayList<Integer>();
                        // Finish the implementation
                        // You may need to add a new recursive method to do that
      traversePreOrder(0, result);
			return result;
		}

    void traversePreOrder(int vertice, ArrayList<Integer> result) {
      if(vertice == -1) return;
      result.add(key[vertice]);
      traversePreOrder(left[vertice], result);      
      traversePreOrder(right[vertice], result);
    }

		List<Integer> postOrder() {
			ArrayList<Integer> result = new ArrayList<Integer>();
                        // Finish the implementation
                        // You may need to add a new recursive method to do that
      traversePostOrder(0, result);
			return result;
		}

    void traversePostOrder(int vertice, ArrayList<Integer> result) {
      if(vertice == -1) return;      
      traversePostOrder(left[vertice], result);      
      traversePostOrder(right[vertice], result);
      result.add(key[vertice]);
    }
	}

	static public void main(String[] args) throws IOException {
            new Thread(null, new Runnable() {
                    public void run() {
                        try {
                            new tree_orders().run();
                        } catch (IOException e) {
                        }
                    }
                }, "1", 1 << 26).start();
	}

	public void print(List<Integer> x) {
		for (Integer a : x) {
			System.out.print(a + " ");
		}
		System.out.println();
	}

	public void run() throws IOException {
		TreeOrders tree = new TreeOrders();
		tree.read();
		print(tree.inOrder());
		print(tree.preOrder());
		print(tree.postOrder());
	}
}
