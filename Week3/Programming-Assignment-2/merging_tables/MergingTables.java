import java.io.*;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadLocalRandom;

public class MergingTables {
    private final InputReader reader;
    private final OutputWriter writer;

    public MergingTables(InputReader reader, OutputWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        InputReader reader = new InputReader(System.in);
        OutputWriter writer = new OutputWriter(System.out);
        new MergingTables(reader, writer).run();
        writer.writer.flush();
    }

    class Table {
        Table parent;
        int rank;
        long numberOfRows;

        Table(long numberOfRows) {
            this.numberOfRows = numberOfRows;
            rank = 0;
            parent = this;
        }
        Table getParent() {
            while(parent != parent.parent) { 
                parent = parent.parent; // set parent to grandparent to enable path compression
            }
            return parent; // return parent which should now be the root parent
        }
    }

    long maximumNumberOfRows = -1;

    void merge(Table destination, Table source) { // Impt. Deal solely with the root tables!
        Table realDestination = destination.getParent(); // Get root parent of destination table
        Table realSource = source.getParent(); // Get root parent of source table
        if (realDestination == realSource) { // If same root return
            return;
        }

        if(realSource.rank < realDestination.rank) { // Compare ranks and set smaller tree to hang on larger tree
          realSource.parent = realDestination; 
          realDestination.numberOfRows += realSource.numberOfRows; // transfer rows from smaller to larger table
          realSource.numberOfRows = 0;  
          maximumNumberOfRows = Math.max(realDestination.numberOfRows, maximumNumberOfRows); // Compare new table row with current max
        } else {
          realDestination.parent = realSource;
          realSource.numberOfRows += realDestination.numberOfRows;
          realDestination.numberOfRows = 0;
          maximumNumberOfRows = Math.max(realSource.numberOfRows, maximumNumberOfRows);
          if(realSource.rank == realDestination.rank) realSource.rank += 1;
        }
    }

    public void run() {
        int n = reader.nextInt();
        int m = reader.nextInt();
        Table[] tables = new Table[n];
        for (int i = 0; i < n; i++) {
            long numberOfRows = reader.nextInt();
            tables[i] = new Table(numberOfRows);
            maximumNumberOfRows = Math.max(maximumNumberOfRows, numberOfRows);
        }
        for (int i = 0; i < m; i++) {
            int destination = reader.nextInt() - 1;
            int source = reader.nextInt() - 1;
            merge(tables[destination], tables[source]);
            writer.printf("%d\n", maximumNumberOfRows);
        }
    }

    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }

        public double nextDouble() {
            return Double.parseDouble(next());
        }

        public long nextLong() {
            return Long.parseLong(next());
        }
    }

    static class OutputWriter {
        public PrintWriter writer;

        OutputWriter(OutputStream stream) {
            writer = new PrintWriter(stream);
        }

        public void printf(String format, Object... args) {
            writer.print(String.format(Locale.ENGLISH, format, args));
        }
    }
}
