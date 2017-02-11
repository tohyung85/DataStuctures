import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class HashChains {

    private FastScanner in;
    private PrintWriter out;
    // store all strings in one list
    private List<String> elems;
    // Store in hashmap
    private ArrayList<String>[] hashMap;
    // for hash function
    private int bucketCount;
    private int prime = 1000000007;
    private int multiplier = 263;

    public static void main(String[] args) throws IOException {
        new HashChains().processQueries();
    }

    private int hashFunc(String s) {
        long hash = 0;
        for (int i = s.length() - 1; i >= 0; --i)
            hash = (hash * multiplier + s.charAt(i)) % prime;
        return (int)hash % bucketCount;
    }

    private Query readQuery() throws IOException {
        String type = in.next();
        if (!type.equals("check")) {
            String s = in.next();
            return new Query(type, s);
        } else {
            int ind = in.nextInt();
            return new Query(type, ind);
        }
    }

    private void writeSearchResult(boolean wasFound) {
        out.println(wasFound ? "yes" : "no");
        // Uncomment the following if you want to play with the program interactively.
        // out.flush();
    }

    private boolean findString (String str) {
        int index = hashFunc(str);
        ArrayList<String> list = hashMap[index];
        for(String s : list) {
            // System.out.println("string: " + s);
            if(s.equals(str)) {
                return true;
            }
        }        
        return false;
    }

    private void processQueryFast(Query query) {
        switch (query.type) {
            case "add":
            //add to hashtable
                if(!findString(query.s)) {
                    int index = hashFunc(query.s);
                    ArrayList<String> sList = hashMap[hashFunc(query.s)];
                    sList.add(0,query.s);
                }            
                break;
            case "del":
            // delete from hashtable
                hashMap[hashFunc(query.s)].remove(query.s);
                break;
            case "find":
            // look from hashtable
                writeSearchResult(findString(query.s));
                break;
            case "check":
            // look at list with index given in query
                ArrayList<String> list = hashMap[query.ind];
                for(String cur : list) {
                    out.print(cur + " ");
                }
                out.println();
                break;
            default:
                throw new RuntimeException("Unknown query: " + query.type);
        }
    }

// Naive method
    // private void processQuery(Query query) {
    //     switch (query.type) {
    //         case "add":
    //             if (!elems.contains(query.s))
    //                 elems.add(0, query.s);
    //             break;
    //         case "del":
    //             if (elems.contains(query.s))
    //                 elems.remove(query.s);
    //             break;
    //         case "find":
    //             writeSearchResult(elems.contains(query.s));
    //             break;
    //         case "check":
    //             for (String cur : elems)
    //                 if (hashFunc(cur) == query.ind)
    //                     out.print(cur + " ");
    //             out.println();
    //             // Uncomment the following if you want to play with the program interactively.
    //             // out.flush();
    //             break;
    //         default:
    //             throw new RuntimeException("Unknown query: " + query.type);
    //     }
    // }

    public void processQueries() throws IOException {
        elems = new ArrayList<>();

        in = new FastScanner();
        out = new PrintWriter(new BufferedOutputStream(System.out));
        bucketCount = in.nextInt();
        hashMap = (ArrayList<String>[])new ArrayList[bucketCount];
        for(int i=0; i<bucketCount; i++) {
            hashMap[i] = new ArrayList<String>();
        }        
        int queryCount = in.nextInt();
        for (int i = 0; i < queryCount; ++i) {
            Query q = readQuery();
            // processQuery(q); naive method
            processQueryFast(q);
        }
        out.close();
    }

    static class Query {
        String type;
        String s;
        int ind;

        public Query(String type, String s) {
            this.type = type;
            this.s = s;
        }

        public Query(String type, int ind) {
            this.type = type;
            this.ind = ind;
        }
    }

    static class FastScanner {
        private BufferedReader reader;
        private StringTokenizer tokenizer;

        public FastScanner() {
            reader = new BufferedReader(new InputStreamReader(System.in));
            tokenizer = null;
        }

        public String next() throws IOException {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                tokenizer = new StringTokenizer(reader.readLine());
            }
            return tokenizer.nextToken();
        }

        public int nextInt() throws IOException {
            return Integer.parseInt(next());
        }
    }
}
