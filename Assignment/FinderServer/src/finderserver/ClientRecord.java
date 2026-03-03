package finderserver;

/**
 * Represents a registered client in the Finder Server database.
 */
public class ClientRecord {

    private String name;
    private String host;
    private int port;
    private String uniqueCode;

    public ClientRecord(String name, String host, int port, String uniqueCode) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.uniqueCode = uniqueCode;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    @Override
    public String toString() {
        return name + " | " + host + ":" + port + " | Code: " + uniqueCode;
    }
}
