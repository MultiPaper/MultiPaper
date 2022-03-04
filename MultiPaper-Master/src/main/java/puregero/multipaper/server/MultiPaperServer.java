package puregero.multipaper.server;

import org.json.JSONObject;
import puregero.multipaper.server.proxy.ProxyServer;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MultiPaperServer extends Thread {
    public static final int DEFAULT_PORT = 35353;
    public static final String SECRET = UUID.randomUUID().toString();

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Usage: java -jar MultiPaperServer.jar <port> [proxy port]");
                System.exit(1);
            }
        }

        if (args.length > 1) {
            try {
                ProxyServer.openServer(Integer.parseInt(args[1]));
            } catch (NumberFormatException e) {
                System.err.println("Usage: java -jar MultiPaperServer.jar <port> [proxy port]");
                System.exit(1);
            }
        }

        new MultiPaperServer(port).start();

        new CommandLineInput().run();
    }

    private final ServerSocket serverSocket;

    public MultiPaperServer(int port) throws IOException {
        loadLicense();

        serverSocket = new ServerSocket(port);

        System.out.println("[MultiPaperMaster] Listening on " + serverSocket.getLocalSocketAddress());
    }

    private String loadLicense() {
        String license = UUID.randomUUID().toString();
        String licenseOnDisk = null;
        String licenseInfoOnDisk = null;

        if (new File("license.txt").isFile()) {
            try {
                List<String> lines = Files.readAllLines(new File("license.txt").toPath());
                license = lines.get(0);
                licenseOnDisk = license;

                if (lines.size() > 1) {
                    licenseInfoOnDisk = lines.get(1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (System.getenv("MULTIPAPER_LICENSE") != null) {
            license = System.getenv("MULTIPAPER_LICENSE");
        }

        if (System.getProperty("MULTIPAPER_LICENSE") != null) {
            license = System.getProperty("MULTIPAPER_LICENSE");
        }

        try {
            UUID.fromString(license);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            license = UUID.randomUUID().toString();
        }

        String licenseInfo = queryLicenseInfo(license);

        if (!Objects.equals(license, licenseOnDisk) || !Objects.equals(licenseInfo, licenseInfoOnDisk)) {
            try {
                Files.write(new File("license.txt").toPath(), Arrays.asList(
                        license,
                        licenseInfo,
                        "",
                        "This is your license identifier for this MultiPaper Master database.",
                        "You may only use this on one MultiPaper Master database.",
                        "If you wish to use another MultiPaper Master database, you must purchase another license for that Master."
                ));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return license;
    }

    private String queryLicenseInfo(String license) {
        long time = System.currentTimeMillis();
        try {
            HttpRequest request = HttpRequest.newBuilder(new URI("https://multipaper.io/api/license/getcount?license=" + license)).build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());
            System.out.println("[MultiPaperMaster] Loaded a " + json.getString("plan") + " license with a player limit of " + json.getInt("count") + " (" + (System.currentTimeMillis() - time) + "ms)");
            if (json.getString("plan").equalsIgnoreCase("free")) {
                System.out.println("[MultiPaperMaster] Note: Free plans do not have a player limit while MultiPaper is in public beta");
            }
            return json.getString("plan") + " plan with " + json.getInt("count") + " player slots.";
        } catch (IOException | URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
        return "Failed to query license";
    }

    public void run() {
        try {
            while (!serverSocket.isClosed()) {
                new ServerConnection(serverSocket.accept());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
