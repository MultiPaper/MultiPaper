package puregero.multipaper.server;

import java.util.Scanner;

public class CommandLineInput extends Thread {

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String line = scanner.nextLine();

            if (line.equalsIgnoreCase("shutdown")) {
                ServerConnection.shutdownAndWait();
                System.exit(0);
            } else {
                System.out.println("Unknown command, use 'shutdown' to shutdown all servers or ctrl+c to stop just this master server");
            }
        }
    }

}
