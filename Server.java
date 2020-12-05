import java.io.IOException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Server {

    public static ArrayList<String> clientdata = new ArrayList<String>();
    public static ArrayList<String> locations = new ArrayList<String>();

    public static void main(String[] args) {

        try { ServerSocket s = new ServerSocket(5556);
            while (true) {
                Socket socket = s.accept();
                Handler sc = new Handler(socket);
                new Thread(sc).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Handler implements Runnable {

        private final Socket sc;

        public Handler(Socket socket){
            this.sc = socket;
        }

        @Override
        public void run() {

            try {

                // Creating output and input data streams
                ObjectOutputStream oos = new ObjectOutputStream(sc.getOutputStream());
                ObjectInputStream inp = new ObjectInputStream(sc.getInputStream());

                Object list = inp.readObject();

                // Information from taken from Client is saved in testData arraylist
                clientdata = (ArrayList<String>) list;

                // adds location data to the main list (Doesnt delete if the client disconnects)
                addClientData(clientdata);

                //Sending back an array list back to the client
                oos.writeObject(locations);

            }catch (IOException e){

            } catch (ClassNotFoundException e){

            }

        }

    }

    public static void addClientData(ArrayList<String> client){

        String name = null;
        name = clientdata.get(2);

        if (locations.contains(name)){
            System.out.println(name + " already exists in the list");

            int index = locations.indexOf(name);
            System.out.println("Location of the index is: " + index);
            System.out.println("Old Co-ordinates " + locations);
            locations.set((index - 2), client.get(0));
            locations.set((index - 1), client.get(1));

            System.out.println("Near Co-ordinates " + locations + "\n");
        } else {
            locations.add(client.get(0));
            locations.add(client.get(1));
            locations.add(client.get(2));
            System.out.println("Added New Location for " + name + " Co-ordinates = " + locations + "\n");
        }
    }

    public static void serverLog(){

    }
}