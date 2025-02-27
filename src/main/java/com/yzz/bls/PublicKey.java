package com.yzz.bls;

import com.herumi.mcl.G1;
import com.herumi.mcl.G2;
import com.herumi.mcl.GT;
import com.herumi.mcl.Mcl;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

public class PublicKey implements java.security.PublicKey {
    private int curveType;
    private byte[] pubKey;
    private String QF = "282899BF4430ADD41BDCE37577237ED1CCF1D1DD8F035ABEFC5CB4B2F5C8845F45257BACD6C4019535B0DC651084FF02";
    @Override
    public String getAlgorithm() {
        if(curveType == Bls.BLS12_381) {
            return "BLS12-381";
        } else {
            return "NOT SUPPORTED";
        }
    }

    @Override
    public String getFormat() {
        return null;
    }

    @Override
    public byte[] getEncoded() {
        return pubKey;
    }

    public PublicKey(int curveType, byte[] pubKey) {
        this.curveType = curveType;
        this.pubKey = pubKey;
    }
    /*
     * Veryfi signature
     * Case 1: single message and single signature
     * Case 2: single message and multiple signatures
     */
    public boolean verify(byte[] msg, byte[] sig) {
        G2 H = new G2();
        Mcl.hashAndMapToG2(H, msg); // H = Hash(m)
        G1 pub = new G1();
        pub.deserialize(pubKey);
        G1 Q = new G1();
        Q.deserialize(HexBin.decode(QF));
        G2 g2 = new G2();
        g2.deserialize(sig);
        GT e1 = new GT();
        GT e2 = new GT();
        Mcl.pairing(e1, pub, H); // e1 = e(H, s Q)
        Mcl.pairing(e2, Q, g2); // e2 = e(s H, Q);
        return e1.equals(e2);
    }
    /*
     * Verify mutiple signatures
     * Case: Single private key and single message with multiple signatures
     * h: Not the message itself, it's result of hashAndMapToG2
     */
    public boolean verifyAggregate(byte[] h, byte[] sig) {
        G2 H = new G2();
        H.deserialize(h); // H = Hash(m)
        G1 pub = new G1();
        pub.deserialize(pubKey);
        G1 Q = new G1();
        Q.deserialize(HexBin.decode(QF));
        G2 g2 = new G2();
        g2.deserialize(sig);
        GT e1 = new GT();
        GT e2 = new GT();
        Mcl.pairing(e1, pub, H); // e1 = e(H, s Q)
        Mcl.pairing(e2, Q, g2); // e2 = e(s H, Q);
        return e1.equals(e2);
    }
}
