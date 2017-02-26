import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class SetRangeSum {

    BufferedReader br;
    PrintWriter out;
    StringTokenizer st;
    boolean eof;

    // Splay tree implementation

    // Vertex of a splay tree
    class Vertex {
        int key;
        // Sum of all the keys in the subtree - remember to update
        // it after each operation that changes the tree.
        long sum;
        Vertex left;
        Vertex right;
        Vertex parent;

        Vertex(int key, long sum, Vertex left, Vertex right, Vertex parent) {
            this.key = key;
            this.sum = sum;
            this.left = left;
            this.right = right;
            this.parent = parent;
        }
    }

    void update(Vertex v) {
        if (v == null) return;
        v.sum = v.key + (v.left != null ? v.left.sum : 0) + (v.right != null ? v.right.sum : 0);
        if (v.left != null) {
            v.left.parent = v;
        }
        if (v.right != null) {
            v.right.parent = v;
        }
    }

    void smallRotation(Vertex v) {
        Vertex parent = v.parent;
        if (parent == null) {
            return;
        }
        Vertex grandparent = v.parent.parent;
        if (parent.left == v) {
            Vertex m = v.right;
            v.right = parent;
            parent.left = m;
        } else {
            Vertex m = v.left;
            v.left = parent;
            parent.right = m;
        }
        update(parent);
        update(v);
        v.parent = grandparent;
        if (grandparent != null) {
            if (grandparent.left == parent) {
                grandparent.left = v;
            } else {
                grandparent.right = v;
            }
        }
    }

    void bigRotation(Vertex v) {
        if (v.parent.left == v && v.parent.parent.left == v.parent) {
            // Zig-zig
            smallRotation(v.parent);
            smallRotation(v);
        } else if (v.parent.right == v && v.parent.parent.right == v.parent) {
            // Zig-zig
            smallRotation(v.parent);
            smallRotation(v);
        } else {
            // Zig-zag
            smallRotation(v);
            smallRotation(v);
        }
    }

    // Makes splay of the given vertex and returns the new root.
    Vertex splay(Vertex v) {
        if (v == null) return null;
        while (v.parent != null) {
            if (v.parent.parent == null) {
                smallRotation(v);
                break;
            }
            bigRotation(v);
        }
        return v;
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

    // Searches for the given key in the tree with the given root
    // and calls splay for the deepest visited node after that.
    // Returns pair of the result and the new root.
    // If found, result is a pointer to the node with the given key.
    // Otherwise, result is a pointer to the node with the smallest
    // bigger key (next value in the order).
    // If the key is bigger than all keys in the tree,
    // then result is null.
    VertexPair find(Vertex root, int key) {
        Vertex v = root;
        Vertex last = root;
        Vertex next = null;
        while (v != null) {
            if (v.key >= key && (next == null || v.key < next.key)) {
                next = v;
            }
            last = v;
            if (v.key == key) {
                break;
            }
            if (v.key < key) {
                v = v.right;
            } else {
                v = v.left;
            }
        }
        root = splay(last);
        return new VertexPair(next, root);
    }

    VertexPair split(Vertex root, int key) {
        VertexPair result = new VertexPair();
        VertexPair findAndRoot = find(root, key);
        root = findAndRoot.right;
        result.right = findAndRoot.left; // node with key or next node with key larger than 'key'
        if (result.right == null) {
            result.left = root;
            return result;
        }
        result.right = splay(result.right); // bring node with key to the root
        result.left = result.right.left; 
        result.right.left = null; // right node contains key or next node with key larger than 'key'
        if (result.left != null) {
            result.left.parent = null;
        }
        update(result.left);
        update(result.right);
        return result;
    }

    Vertex merge(Vertex left, Vertex right) {
        if (left == null) return right;
        if (right == null) return left;
        while (right.left != null) {
            right = right.left;
        }
        right = splay(right);
        right.left = left;
        update(right);
        return right;
    }

    // Code that uses splay tree to solve the problem

    Vertex root = null;

    void insert(int x) {        
        Vertex left = null;
        Vertex right = null;
        Vertex new_vertex = null;
        VertexPair leftRight = split(root, x);
        left = leftRight.left;
        right = leftRight.right;
        if (right == null || right.key != x) {
            new_vertex = new Vertex(x, x, null, null, null);
        }
        root = merge(merge(left, new_vertex), right);
    }

    void erase(int x) {
        // Implement erase yourself        
        if(find(x)) {
            VertexPair leftRight = split(root, x);
            VertexPair noXPair = split(leftRight.right, x+1);
            root = merge(leftRight.left, noXPair.right);
        }
    }

    boolean find(int x) {
        // Implement find yourself
        VertexPair leftRight = find(root, x);
        root = leftRight.right;
        if(leftRight.left == null || leftRight.left.key != x) return false;
        return true;
    }

    long sum(int from, int to) {
        if(to < from) {
            int tmp = to;
            to = from;
            from = tmp;
        }

        VertexPair leftMiddle = split(root, from);
        Vertex left = leftMiddle.left;
        Vertex middle = leftMiddle.right;
        VertexPair middleRight = split(middle, to + 1);
        middle = middleRight.left;
        Vertex right = middleRight.right;
        long ans = 0;
        // Complete the implementation of sum
        ans = middle == null ? 0 : middle.sum;

        left = merge(left, middle);
        root = merge(left, right);
        return ans;
    }

    public static final int MODULO = 1000000001;

    void solve() throws IOException {
        int n = nextInt();
        int last_sum_result = 0;
        for (int i = 0; i < n; i++) {
            char type = nextChar();
            switch (type) {
                case '+' : {
                    int x = nextInt();
                    insert((x + last_sum_result) % MODULO);
                } break;
                case '-' : {
                    int x = nextInt();
                    erase((x + last_sum_result) % MODULO);
                } break;
                case '?' : {
                    int x = nextInt();
                    out.println(find((x + last_sum_result) % MODULO) ? "Found" : "Not found");
                } break;
                case 's' : {                    
                    int l = nextInt();
                    int r = nextInt();
                    long res = sum((l + last_sum_result) % MODULO, (r + last_sum_result) % MODULO);
                    out.println(res);
                    last_sum_result = (int)(res % MODULO);
                }
            }
        }
    }

    SetRangeSum() throws IOException {
        br = new BufferedReader(new InputStreamReader(System.in));
        out = new PrintWriter(System.out);
        solve();
        out.close();

        // stressTest();
    }

/*
    // Stress Test
    void stressTest() {        
        char[] operators = {'+', '-', '?', 's'};
        int numOperations = 10;
        int max_i = 1000000000;
        while(true) {
            System.out.println("----New Test Case----");
            ArrayList<Integer> ar = new ArrayList<Integer>(); 
            int last_sum_result = 0;
            root= null;
            for(int i=0; i<numOperations; i++) {
                char type = operators[ThreadLocalRandom.current().nextInt(0, 4)];    
                switch (type) {
                    case '+' : {
                        int x = ThreadLocalRandom.current().nextInt(0, max_i);
                        System.out.println(type + " " + x);
                        insert((x + last_sum_result) % MODULO);
                        if(ar.indexOf((x + last_sum_result) % MODULO) == -1) {
                            ar.add((x + last_sum_result) % MODULO);    
                        }                        
                        print_tree();
                    } break;
                    case '-' : {
                        int x = ThreadLocalRandom.current().nextInt(0, max_i);
                        System.out.println(type + " " + x);
                        erase((x + last_sum_result) % MODULO);
                        if(ar.indexOf((x + last_sum_result) % MODULO) != -1) {
                            ar.remove(ar.indexOf((x + last_sum_result) % MODULO));
                        }
                        print_tree();
                    } break;
                    case '?' : {
                        int x = ThreadLocalRandom.current().nextInt(0, max_i);
                        System.out.println(type + " " + x);
                        String fast= find((x + last_sum_result) % MODULO) ? "Found" : "Not found";
                        String slow= ar.indexOf((x + last_sum_result) % MODULO) != -1 ? "Found" : "Not found";
                        if(fast != slow) {
                            System.out.println("Different results found! " + "fast: " + fast + " slow: " + slow);
                            return;
                        }
                        print_tree();
                    } break;
                    case 's' : {                    
                        int l = ThreadLocalRandom.current().nextInt(0, max_i);
                        int r = ThreadLocalRandom.current().nextInt(0, max_i);
                        System.out.println(type + " " + l + " " + r);
                        int newL = (l + last_sum_result) % MODULO;
                        int newR = (r + last_sum_result) % MODULO;
                        long res = sum((l + last_sum_result) % MODULO, (r + last_sum_result) % MODULO);
                        long slowRes = 0;
                        if(newL > newR) {
                            int tmp = newR;
                            newR = newL;
                            newL = tmp;
                        }
                        for(Integer num : ar) {
                            if(num >= newL && num <= newR) slowRes += num;
                        }
                        if(res != slowRes) {
                            System.out.println("Different results found! " + "fast: " + res + " slow: " + slowRes);
                            return;
                        } else {
                            System.out.println("Sum: " + res);
                        }
                        last_sum_result = (int)(res % MODULO);
                        print_tree();
                    }
                }
            }    
        }
    }

//Print tree for testing
    void print_tree() {
        String s = "";
        traverse_tree(root, s);
        System.out.println("");
    }

    void traverse_tree(Vertex node, String s) {
        if(node == null) return;
        System.out.print(node.key + " ");
        if(node.parent != null) System.out.print("p: " + node.parent.key + " ");
        if(node.left != null) {
            System.out.print("l:");
            traverse_tree(node.left, s);
        }
        if(node.right != null) {
            System.out.print("r:");
            traverse_tree(node.right, s);
        }
    }
*/
    public static void main(String[] args) throws IOException {
        new SetRangeSum();
    }

    String nextToken() {
        while (st == null || !st.hasMoreTokens()) {
            try {
                st = new StringTokenizer(br.readLine());
            } catch (Exception e) {
                eof = true;
                return null;
            }
        }
        return st.nextToken();
    }

    int nextInt() throws IOException {
        return Integer.parseInt(nextToken());
    }
    char nextChar() throws IOException {
        return nextToken().charAt(0);
    }
}
