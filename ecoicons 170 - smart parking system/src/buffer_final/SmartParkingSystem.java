package buffer_final;


import java.util.*;

//Represents a connection between two nodes (gate ↔ parking spot)
class Edge {
 int to;         // destination node
 int weight;     // distance

 Edge(int t, int w) {
     to = t;
     weight = w;
 }
}

//Represents a parking spot
class ParkingSpot {
 int price;              // cost of the spot
 boolean available = true;  // initially all spots are free

 ParkingSpot(int price) {
     this.price = price;
 }
}

//Represents a user entering the system
class User {
 int gate;       // entry gate
 int maxPrice;   // budget constraint

 User(int gate, int maxPrice) {
     this.gate = gate;
     this.maxPrice = maxPrice;
 }
}

public class SmartParkingSystem {

 static int g;       // number of gates
 static int n;       // number of parking spots
 static int total;   // total nodes (gates + spots)

 static List<List<Edge>> graph;   // adjacency list
 static ParkingSpot[] spots;      // parking spot details

 // queue for users waiting for parking
 static Queue<User> queue = new LinkedList<>();

 // Dijkstra's algorithm to find shortest distance from a gate
 static int[] dijkstra(int src) {

     int[] dist = new int[total];
     Arrays.fill(dist, Integer.MAX_VALUE);

     dist[src] = 0;

     // min-heap based on distance
     PriorityQueue<int[]> pq =
             new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));

     pq.add(new int[]{src, 0});

     while (!pq.isEmpty()) {

         int[] current = pq.poll();
         int node = current[0];
         int currentDist = current[1];

         // skip outdated entries
         if (currentDist > dist[node]) continue;

         // explore neighbors
         for (Edge e : graph.get(node)) {
             if (dist[node] + e.weight < dist[e.to]) {
                 dist[e.to] = dist[node] + e.weight;
                 pq.add(new int[]{e.to, dist[e.to]});
             }
         }
     }

     return dist;
 }

 public static void main(String[] args) {

     Scanner sc = new Scanner(System.in);

     // ====== BASIC INPUT ======
     System.out.print("Enter number of gates: ");
     g = sc.nextInt();

     System.out.print("Enter number of parking spots: ");
     n = sc.nextInt();

     total = g + n;

     // initialize graph
     graph = new ArrayList<>();
     for (int i = 0; i < total; i++) {
         graph.add(new ArrayList<>());
     }

     spots = new ParkingSpot[total];

     // ====== INPUT PARKING DETAILS ======
     for (int i = 0; i < n; i++) {

         int node = g + i;

         System.out.print("Price of spot " + (i + 1) + ": ");
         int price = sc.nextInt();

         // validation: price must be positive
         if (price <= 0) {
             System.out.println("Invalid price! Must be positive.");
             i--;
             continue;
         }

         spots[node] = new ParkingSpot(price);

         // connect each spot with all gates
         for (int gate = 0; gate < g; gate++) {

             System.out.print("Distance from Gate " + gate + ": ");
             int dist = sc.nextInt();

             // validation: distance cannot be negative
             if (dist < 0) {
                 System.out.println("Distance cannot be negative!");
                 gate--;
                 continue;
             }

             graph.get(gate).add(new Edge(node, dist));
             graph.get(node).add(new Edge(gate, dist));
         }
     }

     // ====== MAIN MENU LOOP ======
     while (true) {

         System.out.println("\n1. Add User");
         System.out.println("2. Process Queue");
         System.out.println("3. Leave Parking");
         System.out.println("4. Exit");

         int choice = sc.nextInt();

         // ====== ADD USER ======
         if (choice == 1) {

             System.out.print("Enter gate: ");
             int gate = sc.nextInt();

             if (gate < 0 || gate >= g) {
                 System.out.println("Invalid gate!");
                 continue;
             }

             System.out.print("Enter max price: ");
             int price = sc.nextInt();

             queue.add(new User(gate, price));
             System.out.println("User added to queue.");

         }

         // ====== PROCESS NEXT USER ======
         else if (choice == 2) {

             if (queue.isEmpty()) {
                 System.out.println("No users in queue.");
                 continue;
             }

             User user = queue.poll();

             // shortest distances from user's gate
             int[] dist = dijkstra(user.gate);

             int bestSpot = -1;
             int minDistance = Integer.MAX_VALUE;

             // find best valid spot
             for (int i = g; i < total; i++) {

                 if (spots[i].available &&
                     spots[i].price <= user.maxPrice) {

                     if (dist[i] < minDistance) {
                         minDistance = dist[i];
                         bestSpot = i;
                     }
                 }
             }

             // assign spot if found
             if (bestSpot != -1) {

                 spots[bestSpot].available = false;

                 System.out.println("Assigned Spot " + (bestSpot - g + 1)
                         + " | Distance: " + minDistance
                         + " | Price: " + spots[bestSpot].price);
             }

             // otherwise, ask user to wait
             else {
                 System.out.println("No parking available right now. Please wait...");
                 queue.add(user);  // put back in queue
             }
         }

         // ====== FREE A SPOT ======
         else if (choice == 3) {

             System.out.print("Enter spot ID (1 to " + n + "): ");
             int id = sc.nextInt();

             if (id < 1 || id > n) {
                 System.out.println("Invalid ID.");
                 continue;
             }

             int node = g + id - 1;

             if (!spots[node].available) {
                 spots[node].available = true;
                 System.out.println("Spot freed.");
             } else {
                 System.out.println("Already empty.");
             }
         }

         // ====== EXIT ======
         else {
             break;
         }
     }

     sc.close();
 }
}