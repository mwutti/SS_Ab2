package at.syssec.ss15.ss.ab2.test;


import at.syssec.ss15.ss.ab2.ElGamalSig;
import at.syssec.ss15.ss.ab2.impl.Kohlbacher_Wutti.ElGamalSigImpl;
import org.junit.Assert;
import org.junit.Test;
import java.math.BigInteger;

public class SigTest {

    public static final int BIT_LENGTH = 20;
    ElGamalSig tools = new ElGamalSigImpl();

    @Test
    public void testGoodSignature() {
        byte[] message = "qwertzuiopü+asdfghjklöä#yxcv bnm,.-1234567890?ßQWERTZUIOPÜ*ASDFGHJKLÖÄ'YXCVBNM;:_".getBytes();

        BigInteger p = tools.generatePrime(BIT_LENGTH);
        BigInteger g = tools.generateGenerator(p);
        BigInteger d = tools.generatePrivatePart(p);
        BigInteger e = tools.generatePublicPart(p, g, d);

        ElGamalSig.ElGamalSignature sig = tools.sign(message, p, g, d);

        Assert.assertEquals(true, tools.verify(message, sig, p, g, e));
    }

    @Test
    public void testBadSignature1() {

        byte[] message = "Das ist ein SysSec-Test".getBytes();

        BigInteger p = tools.generatePrime(BIT_LENGTH);
        BigInteger g = tools.generateGenerator(p);
        BigInteger d = tools.generatePrivatePart(p);
        BigInteger e = tools.generatePublicPart(p, g, d);

        ElGamalSig.ElGamalSignature sig = tools.sign(message, p, g, d);

        byte[] s = sig.getS();
        s[0] = (byte) (s[0] ^ (byte) 0xff);
        sig.setS(s);

        Assert.assertEquals(false, tools.verify(message, sig, p, g, e));
    }

    @Test
    public void testBadSignature2() {

        byte[] message = "Das ist ein SysSec-Test".getBytes();

        BigInteger p = tools.generatePrime(BIT_LENGTH);
        BigInteger g = tools.generateGenerator(p);
        BigInteger d = tools.generatePrivatePart(p);
        BigInteger e = tools.generatePublicPart(p, g, d);

        ElGamalSig.ElGamalSignature sig = tools.sign(message, p, g, d);

        byte[] s = sig.getS();
        sig.setS(sig.getR());
        sig.setR(s);

        Assert.assertEquals(false, tools.verify(message, sig, p, g, e));
    }

//    Primzahl sollte der Form p=2q+1 sein (Test kann theoretisch auch
//    fehlschlagen, obwohl p passend gewählt wurde)
	@Test
	public void testPrime() {
		BigInteger p = tools.generatePrime(BIT_LENGTH);

		Assert.assertEquals(true,
				p.subtract(BigInteger.valueOf(1)).divide(BigInteger.valueOf(2))
						.isProbablePrime(100));
	}

    @Test
    public void positiveSignatureValue() {
        byte[] message = "Das ist ein SysSec-Test".getBytes();

        BigInteger p = tools.generatePrime(BIT_LENGTH);
        BigInteger g = tools.generateGenerator(p);
        BigInteger d = tools.generatePrivatePart(p);
        BigInteger e = tools.generatePublicPart(p, g, d);

        ElGamalSig.ElGamalSignature sig = tools.sign(message, p, g, d);
        Assert.assertTrue(new BigInteger(sig.getS()).intValue() > 0);
    }


//	Annahme: Primzahl ist der Form p=2q+1. Generator soll Untergruppe der
//	Ordnung p-1 bilden
//	@Test
//	public void testGenerator1() {
//		BigInteger p = tools.generatePrime(BIT_LENGTH);
//		BigInteger g = tools.generateGenerator(p);
//
//		BigInteger q = p.subtract(BigInteger.valueOf(1)).divide(
//				BigInteger.valueOf(2));
//
//		// g^q und g^2 dürfen nicht 1 ergeben. Ist dies der Fall, so hat g die
//		// Ordnun p-1. Sonst entweder q oder 2
//		Assert.assertEquals(
//				1,
//				!g.modPow(q, p).equals(BigInteger.valueOf(1))
//						&& !g.modPow(BigInteger.valueOf(2), p).equals(
//						BigInteger.valueOf(1)));
//	}

    // Annahme: Primzahl ist der Form p=2q+1. Generator soll Untergruppe der
    // Ordnung q bilden
	@Test
	public void testGenerator2() {

			BigInteger p = tools.generatePrime(BIT_LENGTH);
			BigInteger g = tools.generateGenerator(p);

			BigInteger q = p.subtract(BigInteger.valueOf(1)).divide(BigInteger.valueOf(2));

			// g^q ergibt 1. Somit ist die Ordnung des Generator q (weil q eine Primzahl ist, kann es keine weiteren Untergruppen geben)
			Assert.assertEquals(true,g.modPow(q, p).equals(BigInteger.valueOf(1)));
		}
}
