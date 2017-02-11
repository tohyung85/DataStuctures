import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class HashSubstring {

    private static FastScanner in;
    private static PrintWriter out;

    private static long prime = 100000000003L;
    private static int multiplier = 31;

    private static long[] hashes;

    public static void main(String[] args) throws IOException {
        in = new FastScanner();
        out = new PrintWriter(new BufferedOutputStream(System.out));
        Data input = readInput();
        // printOccurrences(getOccurrences(input));
        // out.println();
        printOccurrences(getOccurrencesFast(input));
        out.close();
    }

    private static Data readInput() throws IOException {
        String pattern = in.next();
        String text = in.next();
        return new Data(pattern, text);
    }

    private static long hashFunc(String s) {
        long hash = 0;
        for (int i = s.length() - 1; i >= 0; --i)
            hash = (hash * multiplier + s.charAt(i)) % prime;
        return hash;
    }

    private static void printOccurrences(List<Integer> ans) throws IOException {
        for (Integer cur : ans) {
            out.print(cur);
            out.print(" ");
        }
    }

    private static List<Integer> getOccurrencesFast(Data input) {
        String s = input.pattern, t = input.text;        
        int m = s.length(), n = t.length();
        hashes = new long[n - m + 1];
        preComputeHashes(hashes, t, s);
        long patternHash = hashFunc(s);
        List<Integer> occurrences = new ArrayList<Integer>();      

        for(int i = 0; i < n-m +1 ; i++) { // Count from 0!!!!
            if(hashes[i] != patternHash) {
                continue;
            } 
            // If hashes match will need to compare strings incase of conflicts.
            boolean match = true;
            for(int j = 0; j < m; j++) { 
                if(t.charAt(i+j) != s.charAt(j)) {
                    match = false;
                    break;
                }
            } 
            if(match) {
                occurrences.add(i); // This is a O(1) operation. occurences.add(0, i) is a O(N) operation!! Huge difference!
            }              
        }
        return occurrences;
    }

    private static void preComputeHashes(long[] hashArr, String text, String pattern) {
        // Similar to dynamic programming. Find last value and use them to calculate the remaining values.
        int m = pattern.length(), n = text.length();
        hashes[hashArr.length - 1] = hashFunc(text.substring(n - m));
        long y = 1;
        for(int i = 0; i < m; i++) {
            y = (y*multiplier) % prime;
        }
        for(int i = n - m - 1; i>=0; i--) {
            // note that due to potential negative remainder, will need to add prime and mod again.
            hashes[i] = ((multiplier * hashes[i + 1] - text.charAt(i+m)*y + text.charAt(i))%prime + prime) % prime; 
        }
    }

// Naive Implementation
    // private static List<Integer> getOccurrences(Data input) {
    //     String s = input.pattern, t = input.text;
    //     int m = s.length(), n = t.length();
    //     List<Integer> occurrences = new ArrayList<Integer>();
    //     for (int i = 0; i + m <= n; ++i) {
	   //  boolean equal = true;
	   //  for (int j = 0; j < m; ++j) {
    // 		if (s.charAt(j) != t.charAt(i + j)) {
    // 		     equal = false;
    //  		    break;
    // 		}
	   //  }
    //         if (equal)
    //             occurrences.add(i);
	   // }
    //     return occurrences;
    // }

    static class Data {
        String pattern;
        String text;
        public Data(String pattern, String text) {
            this.pattern = pattern;
            this.text = text;
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

