
import java.util.Scanner;

public class SubnetCalculator {

    //changing Decimal format into binary
    public static String ipToBinary(String ip) {
        String[] octets = ip.split("\\.");
        StringBuilder binary = new StringBuilder();
        for (String octet : octets) {
            int octetInt = Integer.parseInt(octet);
            binary.append(String.format("%8s", Integer.toBinaryString(octetInt)).replace(' ', '0')).append(".");
        }
        return binary.substring(0, binary.length() - 1);
    }

    //changing binary into Decimal for Ip
    public static String binaryToIp(String binary) {
        String[] binaries = binary.split("\\.");
        StringBuilder ip = new StringBuilder();
        for (String b : binaries) {
            ip.append(Integer.parseInt(b, 2)).append(".");
        }
        return ip.substring(0, ip.length() - 1);
    }

    public static String getNetworkAddress(String ip, int maskBits) {
        String ipBin = ipToBinary(ip).replace(".", "");
        String maskBin = "1".repeat(maskBits) + "0".repeat(32 - maskBits);
        StringBuilder networkBin = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            if (ipBin.charAt(i) == '1' && maskBin.charAt(i) == '1') {
                networkBin.append('1');
            } else {
                networkBin.append('0');
            }
        }
        return binaryToIp(networkBin.toString().replaceAll("(.{8})", "$1."));
    }

    public static String getIpClass(String ip) {
        String[] octets = ip.split("\\.");
        if (octets.length != 4) {
            return "Invalid IP Address";
        }

        try {
            int firstOctet = Integer.parseInt(octets[0]);
            int secondOctet = Integer.parseInt(octets[1]);
            int thirdOctet = Integer.parseInt(octets[2]);
            int fourthOctet = Integer.parseInt(octets[3]);

            if (firstOctet < 0 || firstOctet > 255 ||
                    secondOctet < 0 || secondOctet > 255 ||
                    thirdOctet < 0 || thirdOctet > 255 ||
                    fourthOctet < 0 || fourthOctet > 255) {
                throw new SubnetException("Ip address in each octet must be between 0-255");
            }

            if (firstOctet >= 0 && firstOctet <= 127) {
                return "Class A";
            } else if (firstOctet >= 128 && firstOctet <= 191) {
                return "Class B";
            } else if (firstOctet >= 192 && firstOctet <= 223) {
                return "Class C";
            } else if (firstOctet >= 224 && firstOctet <= 239) {
                return "Class D";
            } else if (firstOctet >= 240 && firstOctet <= 255) {
                return "Class E";
            } else {
                return "Unknown Class";
            }
        } catch (NumberFormatException e) {
        }
        return null;
    }


    public static String getBroadcastAddress(String network, int maskBits) {
        String networkBin = ipToBinary(network).replace(".", "");
        String wildcardBin = "0".repeat(maskBits) + "1".repeat(32 - maskBits);
        StringBuilder broadcastBin = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            if (networkBin.charAt(i) == '1' || wildcardBin.charAt(i) == '1') {
                broadcastBin.append('1');
            } else {
                broadcastBin.append('0');
            }
        }
        return binaryToIp(broadcastBin.toString().replaceAll("(.{8})", "$1."));
    }

    public static String getWildcardMask(int maskBits) {
        String wildcardBin = "0".repeat(maskBits) + "1".repeat(32 - maskBits);
        return binaryToIp(wildcardBin.replaceAll("(.{8})", "$1."));
    }

    public static int calculateSubnetNumber(String ip, String network, int maskBits) {
        String ipBin = ipToBinary(ip).replace(".", "");
        String networkBin = ipToBinary(network).replace(".", "");
        int subnetNumber = 0;

        for (int i = 0; i < maskBits; i++) {
            if (ipBin.charAt(i) != networkBin.charAt(i)) {
                subnetNumber = (int) Math.pow(2, (maskBits - i - 1));
                break;
            }
        }
        return subnetNumber;
    }

    public static void calculateSubnetInfo(String ipWithMask) {
        String[] parts = ipWithMask.split("/");
        String ip = parts[0];
        int maskBits = Integer.parseInt(parts[1]);

        String netmaskBin = "1".repeat(maskBits) + "0".repeat(32 - maskBits);
        String netmask = binaryToIp(netmaskBin.replaceAll("(.{8})", "$1."));

        String network = getNetworkAddress(ip, maskBits);
        String broadcast = getBroadcastAddress(network, maskBits);
        String wildcard = getWildcardMask(maskBits);

        String ipClass = getIpClass(ip);

        int subnetNumber = calculateSubnetNumber(ip,network,maskBits);

        String networkBin = ipToBinary(network);
        String broadcastBin = ipToBinary(broadcast);

        String[] firstHostParts = network.split("\\.");
        firstHostParts[3] = String.valueOf(Integer.parseInt(firstHostParts[3]) + 1);
        String firstHost = String.join(".", firstHostParts);

        String[] lastHostParts = broadcast.split("\\.");
        lastHostParts[3] = String.valueOf(Integer.parseInt(lastHostParts[3]) - 1);
        String lastHost = String.join(".", lastHostParts);

        int numberOfHosts = (int) Math.pow(2, 32 - maskBits) - 2;

        System.out.println("Address: " + ip + " = " + ipToBinary(ip));
        System.out.println("Netmask: " + netmask + " = " + maskBits + " " + ipToBinary(netmask));
        System.out.println("Wildcard: " + wildcard + " " + ipToBinary(wildcard));
        System.out.println("Network: " + network + "/" + maskBits + " " + networkBin + "(" + ipClass + ")");
        System.out.println("Broadcast: " + broadcast + " " + broadcastBin);
        System.out.println("HostMin: " + firstHost);
        System.out.println("HostMax: " + lastHost);
        System.out.println("Hosts/Net: " + numberOfHosts);
        System.out.println("Subnet Number: " + subnetNumber);
    }

    public static void main(String[] args) {
        while(true) {
            try {
                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter IP address with mask (e.g., 192.168.1.1/24): ");
                String ipWithMask = scanner.nextLine();
                calculateSubnetInfo(ipWithMask);

            }catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e){
                System.err.println("Input must be the Ip address with CIDR notation.");
                System.out.println();
            }
        }
    }
}
