import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

class RopeProblem {
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

	/* Naive algorithm
	class Rope {
		String s;

		void process( int i, int j, int k ) {
                        // Replace this code with a faster implementation
                        String t = s.substring(0, i) + s.substring(j + 1);
                        s = t.substring(0, k) + s.substring(i, j + 1) + t.substring(k);
		}

		String result() {
			return s;
		}
		Rope( String s ) {
			this.s = s;
		}
	}
	*/

	class RopeTree {
		Vertex root = null;
		int str_size;

		RopeTree(String s) {
			for(int i=0; i< s.length(); i++) {
				char c = s.charAt(i);
				this.add(c);
			}

			this.str_size = 0;
		}

		VertexPair find(Vertex root, int index) {
			Vertex v = root;
			Vertex last = root;
			while(v != null) {
				last = v;
				int v_index = v.right == null ? v.size - 1 : v.size - v.right.size - 1;
				if(v_index == index) {
					break;
				}				
				if(index < v_index) {
					v = v.left;
				} else {
					index -= v_index+1;
					v = v.right;
				}
			}
			root = splay(last);

			return new VertexPair(last, root);
		}

		VertexPair split(int index) {
			VertexPair result = split(root, index);
			root = merge(result.left, result.right);			
			return result;
		}

		Vertex merge(Vertex left, Vertex right) {
			if(left == null) return right;
			if(right == null) return left;
			while(right.left != null) {
				right = right.left;
			}
			right = splay(right);
			right.left = left;
			update(right);
			return right;
		}

		VertexPair split(Vertex root, int index) {
			if(root == null) return new VertexPair();
			VertexPair result = new VertexPair();
			if(index >= root.size) {
				result.left = root;
				result.right = null;
				return result;
			}
			VertexPair found = find(root, index);
			root = found.right;
			result.right = found.left;
			if(result.right == null) {
				result.left = root;
				return result;
			}
			result.right = splay(result.right);
			result.left = result.right.left;
			result.right.left = null;
			if(result.left != null) {
				result.left.parent = null;
			}
			update(result.left);
			update(result.right);
			return result;
		}

		Vertex splay(Vertex v) {
			if(v == null) return null;

			while(v.parent != null) {
				if(v.parent.parent == null) {
					smallRotation(v);
					break;
				}
				if((v.parent.left == v && v.parent.parent.left == v.parent) || (v.parent.right == v && v.parent.parent.right == v.parent)) {
					// Zig-Zig or Zag-Zag
					smallRotation(v.parent);
					smallRotation(v);
				} else {
					// Zig-Zag or Zag-Zig
					smallRotation(v);
					smallRotation(v);
				}
			}
			return v;
		}

		void update(Vertex v) {
			if (v == null) return;
			v.size = 1;
			if(v.left != null) {
				v.size += v.left.size; 
				v.left.parent = v;
			}
			if(v.right != null) {
				v.size += v.right.size;
				v.right.parent = v;
			}
		}

		void add(char c) {
			Vertex right_most = root;
			Vertex new_node = new Vertex(c, 1, null, null, null);
			if(right_most == null) {
				root = new_node;
				return;
			}
			while(right_most.right != null) {
				right_most.size++;
				right_most = right_most.right;
			}
			right_most.right = new_node;
			update(right_most);
			root = splay(right_most.right);
		}

		void smallRotation(Vertex v) {
			Vertex initialParent = v.parent;			
			if (initialParent == null) {
				return;
			}
			Vertex initialGrandParent = v.parent.parent;
			if(initialParent.left == v) {
				Vertex m = v.right;
				v.right = initialParent;
				initialParent.left = m;
			} else {
				Vertex m = v.left;
				v.left = initialParent;
				initialParent.right = m;
			}
			update(initialParent);
			update(v);
			v.parent = initialGrandParent;			
			if(initialGrandParent != null) {
				if(initialGrandParent.left == initialParent) {
					initialGrandParent.left = v;
				} else {
					initialGrandParent.right = v;
				}
			}
		}

		class Vertex {
			char key;
			int size;
			Vertex left;
			Vertex right;
			Vertex parent;

			Vertex(char key, int size, Vertex left, Vertex right, Vertex parent) {
				this.size = size;
				this.key = key;
				this.left = left;
				this.right = right;
				this.parent = parent;
			}
		}

		class VertexPair {
			Vertex left;
			Vertex right;

			VertexPair() {				
			}
			VertexPair(Vertex left, Vertex right) {
				this.left = left;
				this.right = right;
			}
		}

		//Methods using split and merge
		void move_substring(int start, int end, int to) {
			VertexPair first_split = split(root, start);
			Vertex left = first_split.left;
			Vertex mid = first_split.right;
			VertexPair second_split = split(mid, end - start + 1);
			mid = second_split.left;
			Vertex right = second_split.right;
			Vertex remaining_string = merge(left, right);
			VertexPair third_split = split(remaining_string, to);
			Vertex new_left = third_split.left;
			Vertex new_mid = third_split.right;
			Vertex merge_left = merge(new_left, mid);
			root = merge(merge_left, new_mid);
		}

    String print_sequence() { // iterative in-order tree traversal
    	StringBuilder sb = new StringBuilder();
    	Stack<Vertex> st = new Stack<Vertex>();
    	Vertex v = root;
    	while(v != null) {
    		st.push(v);
    		v = v.left;
    	}
    	while(!st.empty()) {
    		v = st.pop();
    		sb.append(v.key);
    		if(v.right != null) {
    			v = v.right;
    			while(v != null) {
    				st.push(v);
    				v=v.left;
    			}
    		}
    	}

    	return sb.toString();
    }

/*
		//Print tree for testing
    void print_tree(Vertex root) {
        String s = "";
        traverse_tree(root, s);
        System.out.println("");
    }

    // for testing only
    void traverse_tree(Vertex node, String s) {
        if(node == null) return;        
        System.out.print(node.key + " s:" + node.size + " ");  
        if(node.parent != null) System.out.print("p:" + node.parent.key + " ");      
        if(node.left != null) {
            System.out.print("--->l:");            
            traverse_tree(node.left, s);
        }        
        if(node.right != null) {
            System.out.print("--->r:");     
            traverse_tree(node.right, s);       
        }
    }

    // Recursive in-order traversal of tree
    StringBuilder traverse_tree_index_order(Vertex node, StringBuilder sb) {
        if(node == null) return sb;        
        if(node.left != null) {
           sb = traverse_tree_index_order(node.left, sb);
        }
        sb.append(node.key);
        if(node.right != null) {
           sb= traverse_tree_index_order(node.right, sb);
        }    	
        return sb;
    }
    */
	}

	/* 
	public void stress_test() { // Stress testing only
		String s = "abcdefghijklmnopqrstuvwxyz";
		Rope rope = new Rope(s);
		RopeTree ropetree = new RopeTree(s);
		while(true) {
			int i = ThreadLocalRandom.current().nextInt(0, s.length()-1);
			int j = ThreadLocalRandom.current().nextInt(i, s.length()-1);
			int k = ThreadLocalRandom.current().nextInt(0, s.length() - (j - i + 1));
			rope.process(i, j , k);
			ropetree.move_substring(i, j, k);

			if(!(rope.result().equals(ropetree.print_sequence()))) {
				System.out.println(i + " " + j + " " + k);
				System.out.print("Naive algo: ");
				System.out.println(rope.result());
				System.out.print("Rope algo: ");
				System.out.println(ropetree.print_sequence());
				break;
			} else {
				System.out.println("ok");
			}
		}
	}
	*/
	public static void main( String[] args ) throws IOException {
		new RopeProblem().run();
	}
	public void run() throws IOException {
		FastScanner in = new FastScanner();
		PrintWriter out = new PrintWriter(System.out);
		// Rope rope = new Rope(in.next()); // naive algo
		RopeTree ropetree = new RopeTree(in.next());
		for (int q = in.nextInt(); q > 0; q--) {
			int i = in.nextInt();
			int j = in.nextInt();
			int k = in.nextInt();
			ropetree.move_substring(i, j, k);
		}
		out.println(ropetree.print_sequence());
		out.close();
		// stress_test();
	}
}
