public class SubnetException extends ArrayIndexOutOfBoundsException {
    public SubnetException(String errorMessage){
        System.err.println("Subnet Exception: " + errorMessage);
    }
}
