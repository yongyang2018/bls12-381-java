package com.yzz.bls;

import com.herumi.mcl.G1;
import com.herumi.mcl.G2;
import com.herumi.mcl.Mcl;
/*
*  BLS API
*  Now only BLS12-381 supported
*  created by yuezz 201910
 */
public class Bls implements BlsConstants{
    static {
        String lib = "mcljava";
        JNIEnv env = new JNIEnv();
        env.prepare();
        String libName = System.mapLibraryName(lib);
        System.loadLibrary(lib);
    }
    private int curveType;
    /*
    * Generate private key
     */
    public PrivateKey generateSecKey() {
        PrivateKey privateKey = new PrivateKey(curveType);
        return privateKey;
    }
    public Bls(int curveType) {
        this.curveType = curveType;
        Mcl.SystemInit(curveType);
    }
    /*
     * Aggregate signatures
     */
    public byte[] aggregateSignature(byte[][] s) {
        G2[] ss = new G2[s.length];
        for(int i=0;i<s.length;i++) {
            ss[i] = new G2();
            ss[i].deserialize(s[i]);
        }
        for(int i=1;i<s.length;i++) {
            Mcl.add(ss[0], ss[0], ss[i]);
        }
        return ss[0].serialize();
    }
    /*
     * Aggregate public key
     */
    public byte[] aggregatePublicKey(byte[][] pub) {
        G1[] pubs = new G1[pub.length];
        for(int i=0;i<pub.length;i++) {
            pubs[i] = new G1();
            pubs[i].deserialize(pub[i]);
        }
        for(int i=1;i<pub.length;i++) {
            Mcl.add(pubs[0], pubs[0], pubs[i]);
        }
        return pubs[0].serialize();
    }
    /*
     * Aggregate message to be signed
     */
    public byte[] aggregateMsg(byte[][] msg) {
        G2[] msgs = new G2[msg.length];
        for(int i=0;i<msg.length;i++) {
            msgs[i] = new G2();
            Mcl.hashAndMapToG2(msgs[i], msg[i]);
        }
        for(int i=1;i<msg.length;i++) {
            Mcl.add(msgs[0], msgs[0], msgs[i]);
        }
        return msgs[0].serialize();
    }
}
