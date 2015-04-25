package at.syssec.ss15.ss.ab2.impl.Kohlbacher_Wutti;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import at.syssec.ss15.ss.ab2.ElGamalSig;

public class ElGamalSigImpl implements ElGamalSig {


    @Override
    public BigInteger generatePrime(int n) {
        BigInteger p = null;
        do {
            BigInteger q = BigInteger.probablePrime(n, new Random(System.currentTimeMillis()));
            p = q.multiply(BigInteger.valueOf(2L)).add(BigInteger.ONE);
        } while (!p.isProbablePrime(1000));
        return p;
    }

    @Override
    public BigInteger generateGenerator(BigInteger p) {
        List<BigInteger> primTeiler = findPrimteiler(p.subtract(BigInteger.ONE));

        for (long i = 2; BigInteger.valueOf(i).compareTo(p.subtract(BigInteger.ONE)) <= 0; i++) {
            boolean isGenerator = true;
            BigInteger g = BigInteger.valueOf(i);

            for (BigInteger t : primTeiler) {
                if (g.mod(p.subtract(BigInteger.ONE).divide(t)).compareTo(BigInteger.ONE) == 0) {

                    isGenerator = false;
                }
            }

            BigInteger q = p.subtract(BigInteger.valueOf(1)).divide(BigInteger.valueOf(2));
            if (!g.modPow(q, p).equals(BigInteger.valueOf(1))) {
                isGenerator = false;
            }

            if (isGenerator) {
                return g;
            }
        }


        return null;
    }

    private List<BigInteger> findPrimteiler(BigInteger p) {
        List<BigInteger> primTeiler = new ArrayList<BigInteger>();
        BigInteger prim = BigInteger.valueOf(2L);
        BigInteger pTemp = p.add(BigInteger.ZERO);
        //Suche nur notmendig bis n/2 (optimale wäre sqrt(n), ~.~)
        while (prim.compareTo(p.divide(BigInteger.valueOf(2L))) <= 0) {
            if (pTemp.mod(prim).equals(BigInteger.ZERO)) {
                primTeiler.add(prim);
                pTemp = pTemp.divide(prim);
            }
            prim = prim.nextProbablePrime();
        }

        return primTeiler;
    }

    @Override
    public BigInteger generatePrivatePart(BigInteger p) {
        BigInteger max = p.subtract(BigInteger.valueOf(2L));
        BigInteger d;
        do {
            d = new BigInteger(max.bitLength(), new Random(System.currentTimeMillis()));
        } while (d.compareTo(BigInteger.valueOf(2L)) < 0);

        return d;
    }

    @Override
    public BigInteger generatePublicPart(BigInteger p, BigInteger g, BigInteger d) {
        return g.modPow(d, p);
    }

    @Override
    public ElGamalSignature sign(byte[] message, BigInteger p, BigInteger g, BigInteger d) {
        BigInteger s;
        BigInteger k;
        BigInteger kInverse;
        BigInteger r;
        do {
            do {
                k = new BigInteger(p.subtract(BigInteger.ONE).bitLength(), new Random(System.currentTimeMillis()));
            } while (!p.subtract(BigInteger.ONE).gcd(k).equals(BigInteger.ONE));
            kInverse = k.modInverse(p.subtract(BigInteger.ONE));
            r = g.modPow(k, p);

            s = ((new BigInteger(message).subtract(d.multiply(r))).multiply(kInverse)).mod(p.subtract(BigInteger.ONE));
            s = s.mod(p.subtract(BigInteger.ONE));
        } while (s.compareTo(BigInteger.ZERO) == 0);

        return new ElGamalSig.ElGamalSignature(r.toByteArray(), s.toByteArray());

    }

    @Override
    public boolean verify(byte[] message, ElGamalSignature sig, BigInteger p, BigInteger g, BigInteger e) {

        BigInteger r = new BigInteger(sig.getR());
        Integer rInt = r.intValue();

        BigInteger s = new BigInteger(sig.getS());
        Integer sInt = s.intValue();
        // 0 < r
        if (r.compareTo(BigInteger.ZERO) <= 0) {
            return false;
        }
        // r < p
        if (p.compareTo(r) <= 0) {
            return false;
        }
        // 0 < s
        if (s.compareTo(BigInteger.ZERO) <= 0) {
            return false;
        }
        // s < p-1
        if (p.subtract(BigInteger.ONE).compareTo(s) <= 0) {
            return false;
        }

        BigInteger leftSide = g.modPow(new BigInteger(message), p);

		BigInteger rightSide;
        BigInteger temp1 = e.modPow(r, p);
        BigInteger temp2 = r.modPow(s, p);

        BigInteger produkt = temp1.multiply(temp2);
        rightSide = produkt.mod(p);


		if(leftSide.compareTo(rightSide) == 0) {
			return true;
		}

//		BigInteger x = new BigInteger(message);
//		BigInteger r = new BigInteger(sig.getR());
//		BigInteger s = new BigInteger(sig.getS());
//		System.out.println("Message: " + x);
//		System.out.println("s: " + s);
//		System.out.println("r: " + r);
//		BigInteger mes = g.modPow(x,p);
//		BigInteger signatur = (e.modPow(r,p).multiply(r.modPow(s,p))).mod(p);
//		System.out.println("Mess: " + mes);
//		System.out.println("Sign: " + signatur);
//
//		return mes.compareTo(signatur) == 0;
        return false;
    }

    private BigInteger pow(BigInteger base, BigInteger exponent) {
        BigInteger result = BigInteger.ONE;
        while (exponent.signum() > 0) {
            if (exponent.testBit(0)) result = result.multiply(base);
            base = base.multiply(base);
            exponent = exponent.shiftRight(1);
        }
        return result;
    }

}