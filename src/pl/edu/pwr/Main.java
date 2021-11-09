package pl.edu.pwr;

public class Main {

    public static void main(String[] args) {
        MyRSAImpl myRSA = new MyRSAImpl();
        myRSA.generateKey(10000, 20000);
        System.out.println(myRSA);
        System.out.println(myRSA.decrypt(myRSA.encrypt("important data")));
    }
}
