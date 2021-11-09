package pl.edu.pwr;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MyRSAImpl {

    private final List<Integer> privateKey;
    private final List<Integer> publicKey;

    public MyRSAImpl(){
        privateKey = new ArrayList<>();
        publicKey = new ArrayList<>();
    }

    public void generateKey(int pRange, int qRange) {
        List<Integer> pValues = primeNumbersTill(pRange);
        List<Integer> qValues = primeNumbersTill(qRange);
        int p = pValues.get(ThreadLocalRandom.current().nextInt(0, pValues.size()));
        int q = qValues.get(ThreadLocalRandom.current().nextInt(qValues.size() / 2, qValues.size()));
        int n = p * q;
        int phi = (p - 1) * (q - 1);
        int e = 2;
        int start = ThreadLocalRandom.current().nextInt(140, n);
        while(start % 2 == 0)
            start = ThreadLocalRandom.current().nextInt(140, n);
        for (int i = start; i < n; i += 2) {
            if (findGCD(phi, i) == 1) {
                e = i;
                break;
            }
        }
        int d = extendedEuclides(e, phi);
        this.privateKey.add(d);
        this.privateKey.add(n);
        this.publicKey.add(e);
        this.publicKey.add(n);
    }

    public List<BigInteger> encrypt(String text){
        ArrayList<BigInteger> data = new ArrayList<>();
        for(char c : text.toCharArray()){
            data.add(myPowWithModulo(c, publicKey.get(0), publicKey.get(1)));
        }
        return data;
    }

    public byte[] hash(String text) throws NoSuchAlgorithmException {
        String salt = "1231212877QweuqweQHSADjhasUYWEQWE";
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update((text + salt).getBytes());
        return md.digest();
    }

    public String decrypt(List<BigInteger> encryptedText){
        StringBuilder stringBuilder = new StringBuilder();
        for(BigInteger bigInteger : encryptedText){
            stringBuilder.append((char)myPowWithModulo(bigInteger.intValue(), privateKey.get(0), privateKey.get(1)).intValue());
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return "MyRSAImpl{" +
                "privateKey =" + privateKey +
                ", publicKey =" + publicKey +
                '}';
    }

    private static List<Integer> primeNumbersTill(int n) {
        return IntStream.rangeClosed(2, n)
                .filter(MyRSAImpl::isPrime).boxed()
                .collect(Collectors.toList());
    }

    private static boolean isPrime(int number) {
        return IntStream.rangeClosed(2, (int) (Math.sqrt(number)))
                .allMatch(n -> number % n != 0);
    }

    public static int findGCD(int number1, int number2){
        if(number2 == 0)
            return number1;
        return findGCD(number2, number1 % number2);
    }

    private static int extendedEuclides(int e, int phi) {
        int u = 1;
        int w = e;
        int x = 0;
        int z = phi;
        int q;
        int temp;

        while(w != 0){
            if(w < z){
                temp = u;
                u = x;
                x = temp;
                temp = w;
                w = z;
                z = temp;
            }
            q = w / z;
            u -= q * x;
            w -= q * z;
        }

        if(z != 1)
            return -1;
        if(x < 0)
            x = x + phi;
        return x;
    }

    private static BigInteger myPowWithModulo(int x, int y, int z){
        BigInteger bigIntegerX = new BigInteger(String.valueOf(x));
        BigInteger bigIntegerY = new BigInteger(String.valueOf(y));
        BigInteger bigIntegerZ = new BigInteger(String.valueOf(z));
        return bigIntegerX.modPow(bigIntegerY, bigIntegerZ);
    }
}
