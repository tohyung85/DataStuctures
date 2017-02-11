import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Random;

public class PhoneBook {

    private FastScanner in = new FastScanner();
    // Keep list of all existing (i.e. not deleted yet) contacts.
    private List<Contact> contacts = new ArrayList<>();

    private int numberContacts;

    private HashFunction h = new HashFunction(10);

    private List<Contact>[] hashmap = (List<Contact>)new List<Contact>[10];

    public static void main(String[] args) {
        new PhoneBook().processQueries();
    }

    private Query readQuery() {
        String type = in.next();
        int number = in.nextInt();
        if (type.equals("add")) {
            String name = in.next();
            return new Query(type, name, number);
        } else {
            return new Query(type, number);
        }
    }

    private void writeResponse(String response) {
        System.out.println(response);
    }

    private class HashFunction {
        int a;
        int b;
        int p = 10000019; // Smallest Prime number > 10^7
        int m;

        public HashFunction(int cardinal) {
            Random rand = new Random();
            this.a = rand.next(p - 1) + 1;
            this.b = rand.next(p);
            this.m = cardinal;
        }

    	private int cardinal() {
    	    return m;
    	}

        private int hash(int n) {
            return ((a * n + b) % p) % m;
        }
    }

    private void rehash() {
        List<Contact>[] newHash = (List<Contact>)new List<Contact>[hashmap.size * 2];
    	HashFunction hnew = new HashFunction(2*hashmap.size);
    	for(int i = 0; i<2*hashmap.size; i++) {
    	  newHash[i] = new List<Contact>();
    	}
    	for(int i = 0; i<hashmap.size; i++) {
    	  List<Contact> contactList = hashmap[i];
    	  for(Contact contact : contactList) {
    	    int index = hnew.hash(contact.number);
    	    newHash[index].add(contact);
    	  }
    	}
    	
    	h = hnew;
    	hashmap = newHash;
    }

    private Contact findContact(int number) {
    	List<Contact> contactList = hashMap[h.hash(number)];
    	for(Contact contact : contactList) {
    	  if(contact.number == number) {
    	    return contact;
    	  }
    	}
    	return new Contact("not found", 0);
    }

    private void addContact(String name, int num) {
    	if(numberContacts/h.cardinal() > 0.9) {
    	  rehash();
    	}
    	Contact c = findContact(query.number);
    	if(c.name.equals("not found")) {
    	  hashmap[h.hash(num)].add(new Contact(name, num));
    	} else {
    	  c.name = name;
    	}
    }

    private void delContact(int num) {
    	Contact c = findContact(num);
    	if(!c.name.equals("not found")){
    	  hashmap[h.hash(num)].remove(c);
    	}
    }

    private void processQueryFast(Query query) {
    	if (query.type.equals("add")) {
    	  addContact(query.name, query.number);
    	}

    	if (query.type.equals("del")) {
    	  delContact(query.number);
    	}

    	if (query.type.equals("find")) {
    	  Contact c = findContact(query.number);
    	  writeResponse(c.name);
    	}
    }

    private void processQuery(Query query) {
        if (query.type.equals("add")) {
            // if we already have contact with such number,
            // we should rewrite contact's name
            boolean wasFound = false;
            for (Contact contact : contacts)
                if (contact.number == query.number) {
                    contact.name = query.name;
                    wasFound = true;
                    break;
                }
            // otherwise, just add it
            if (!wasFound)
                contacts.add(new Contact(query.name, query.number));
        } else if (query.type.equals("del")) {
            for (Iterator<Contact> it = contacts.iterator(); it.hasNext(); )
                if (it.next().number == query.number) {
                    it.remove();
                    break;
                }
        } else {
            String response = "not found";
            for (Contact contact: contacts)
                if (contact.number == query.number) {
                    response = contact.name;
                    break;
                }
            writeResponse(response);
        }
    }

    public void processQueries() {
        int queryCount = in.nextInt();
        for (int i = 0; i < queryCount; ++i)
            processQuery(readQuery());
    }

    static class Contact {
        String name;
        int number;

        public Contact(String name, int number) {
            this.name = name;
            this.number = number;
        }
    }

    static class Query {
        String type;
        String name;
        int number;

        public Query(String type, String name, int number) {
            this.type = type;
            this.name = name;
            this.number = number;
        }

        public Query(String type, int number) {
            this.type = type;
            this.number = number;
        }
    }

    class FastScanner {
        BufferedReader br;
        StringTokenizer st;

        FastScanner() {
            br = new BufferedReader(new InputStreamReader(System.in));
        }

        String next() {
            while (st == null || !st.hasMoreTokens()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }
    }
}
