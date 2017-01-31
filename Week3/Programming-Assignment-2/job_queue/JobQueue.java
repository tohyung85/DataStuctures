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

    private class WorkerComparator implements Comparator<JobWorker> {
    public int compare(JobWorker a, JobWorker b) {
      if(a.taskEndTime > b.taskEndTime) return -1;
      if(a.taskEndTime < b.taskEndTime) return 1;
      if(a.taskEndTime == b.taskEndTime) {
        if(a.index < b.index) return 1;
        if(a.index > b.index) return -1;
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
        for (int i = 0; i < m; ++i) {
            jobs[i] = in.nextInt();

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

    private void assignJobs() {
        // TODO: replace this code with a faster algorithm.
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

    private void assignJobsFast() {
    for(int i = 0; i < jobs.length; i++) {
          assignedWorker = new int[jobs.length];
          startTime = new long[jobs.length];
      JobWorker bestWorker = pq.poll();
      assignedWorker[i] = bestWorker.index;
      startTime[i] = bestWorker.taskEndTime;
          bestWorker.taskEndTime += jobs[i];
      pq.add(bestWorker);
    }       
    }

    public void solve() throws IOException {
        in = new FastScanner();
        out = new PrintWriter(new BufferedOutputStream(System.out));
    pq = new PriorityQueue<JobWorker>(10, new WorkerComparator());
        readData();
        assignedWorker = new int[jobs.length];
        startTime = new long[jobs.length];
//        assignJobs();
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
