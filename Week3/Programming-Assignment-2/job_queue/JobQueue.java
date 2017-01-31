import java.io.*;
import java.util.StringTokenizer;
import java.util.PriorityQueue;
import java.util.Comparator;

public class JobQueue {
    private int numWorkers;
    private int[] jobs;

    private int[] assignedWorker;
    private long[] startTime;

    private FastScanner in;
    private PrintWriter out;

    private PriorityQueue<JobWorker> pq; 

    private class JobWorker {
        int index;
        long taskEndTime;
    }

    private class WorkerComparator implements Comparator<JobWorker> { // Comparator function for use with priority queue
        public int compare(JobWorker a, JobWorker b) {
          if(a.taskEndTime > b.taskEndTime) return 1; // Return 1 if worker will end task later
          if(a.taskEndTime < b.taskEndTime) return -1;
          if(a.taskEndTime == b.taskEndTime) { // If workers end at the same time, sort by index
            if(a.index < b.index) return -1; 
            if(a.index > b.index) return 1;
          }
          return 0;
        }
    }

    public static void main(String[] args) throws IOException {
        new JobQueue().solve();
    }

    private void readData() throws IOException {
        numWorkers = in.nextInt();
        int m = in.nextInt();
        jobs = new int[m];
        for (int i = 0; i < m; ++i) { // Store job run times in array
            jobs[i] = in.nextInt();            
        }

        for (int i = 0; i < numWorkers; i++) { // Initialize workers by storing index and set initial task end time to 0 before adding to pq
            JobWorker jw = new JobWorker();
            jw.index = i;
            jw.taskEndTime = 0;
            pq.add(jw);
        }
    }

    private void writeResponse() {
        for (int i = 0; i < jobs.length; ++i) {
            out.println(assignedWorker[i] + " " + startTime[i]);
        }
    }

    private void assignJobs() {// Slow Alogrithm: replace this code with a faster algorithm.        
        long[] nextFreeTime = new long[numWorkers];
        for (int i = 0; i < jobs.length; i++) {
            int duration = jobs[i];
            int bestWorker = 0;
            for (int j = 0; j < numWorkers; ++j) {
                if (nextFreeTime[j] < nextFreeTime[bestWorker])
                    bestWorker = j;
            }
            assignedWorker[i] = bestWorker;
            startTime[i] = nextFreeTime[bestWorker];
            nextFreeTime[bestWorker] += duration;
        }
    }

    private void assignJobsFast() { // Fast algorithm using priority queue
        for(int i = 0; i < jobs.length; i++) { 
            JobWorker bestWorker = pq.poll(); // For each job, pop best worker from the stack based on comparator function
            assignedWorker[i] = bestWorker.index; // set the assigned worker for the task
            startTime[i] = bestWorker.taskEndTime; // store time job starts
            bestWorker.taskEndTime += jobs[i]; // update new end time for worker
            pq.add(bestWorker); // add back to Priority Queue
        }       
    }

    public void solve() throws IOException {
        in = new FastScanner();
        out = new PrintWriter(new BufferedOutputStream(System.out));
        pq = new PriorityQueue<JobWorker>(10, new WorkerComparator()); // Initialize prior
        readData();
        assignedWorker = new int[jobs.length]; // For storage of results
        startTime = new long[jobs.length]; // For storage of results
//        assignJobs(); // For naive algorithm
        assignJobsFast();
        writeResponse();
        out.close();
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
