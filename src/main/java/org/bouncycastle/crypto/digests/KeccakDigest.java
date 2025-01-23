//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.bouncycastle.crypto.digests;

import jdk.incubator.vector.LongVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class KeccakDigest implements ExtendedDigest {
    private static long[] KeccakRoundConstants = new long[]{1L, 32898L, -9223372036854742902L, -9223372034707259392L, 32907L, 2147483649L, -9223372034707259263L, -9223372036854743031L, 138L, 136L, 2147516425L, 2147483658L, 2147516555L, -9223372036854775669L, -9223372036854742903L, -9223372036854743037L, -9223372036854743038L, -9223372036854775680L, 32778L, -9223372034707292150L, -9223372034707259263L, -9223372036854742912L, 2147483649L, -9223372034707259384L};
    private VectorSpecies<Long> SPECIES = LongVector.SPECIES_128;

    protected final CryptoServicePurpose purpose;
    protected long[] state;
    protected byte[] dataQueue;
    protected int rate;
    protected int bitsInQueue;
    protected int fixedOutputLength;
    protected boolean squeezing;

    public KeccakDigest() {
        this(288, CryptoServicePurpose.ANY);
    }

    public KeccakDigest(CryptoServicePurpose var1) {
        this(288, var1);
    }

    public KeccakDigest(int var1) {
        this(var1, CryptoServicePurpose.ANY);
    }

    public KeccakDigest(int var1, CryptoServicePurpose var2) {
        this.state = new long[25];
        this.dataQueue = new byte[192];
        this.purpose = var2;
        this.init(var1);
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
    }

    public KeccakDigest(KeccakDigest var1) {
        this.state = new long[25];
        this.dataQueue = new byte[192];
        this.purpose = var1.purpose;
        System.arraycopy(var1.state, 0, this.state, 0, var1.state.length);
        System.arraycopy(var1.dataQueue, 0, this.dataQueue, 0, var1.dataQueue.length);
        this.rate = var1.rate;
        this.bitsInQueue = var1.bitsInQueue;
        this.fixedOutputLength = var1.fixedOutputLength;
        this.squeezing = var1.squeezing;
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
    }

    public String getAlgorithmName() {
        return "Keccak-" + this.fixedOutputLength;
    }

    public int getDigestSize() {
        return this.fixedOutputLength / 8;
    }

    public void update(byte var1) {
        this.absorb(var1);
    }

    public void update(byte[] var1, int var2, int var3) {
        this.absorb(var1, var2, var3);
    }

    public int doFinal(byte[] var1, int var2) {
        this.squeeze(var1, var2, (long)this.fixedOutputLength);
        this.reset();
        return this.getDigestSize();
    }

    protected int doFinal(byte[] var1, int var2, byte var3, int var4) {
        if (var4 > 0) {
            this.absorbBits(var3, var4);
        }

        this.squeeze(var1, var2, (long)this.fixedOutputLength);
        this.reset();
        return this.getDigestSize();
    }

    public void reset() {
        this.init(this.fixedOutputLength);
    }

    public int getByteLength() {
        return this.rate / 8;
    }

    private void init(int var1) {
        switch (var1) {
            case 128:
            case 224:
            case 256:
            case 288:
            case 384:
            case 512:
                this.initSponge(1600 - (var1 << 1));
                return;
            default:
                throw new IllegalArgumentException("bitLength must be one of 128, 224, 256, 288, 384, or 512.");
        }
    }

    private void initSponge(int var1) {
        if (var1 > 0 && var1 < 1600 && var1 % 64 == 0) {
            this.rate = var1;

            for(int var2 = 0; var2 < this.state.length; ++var2) {
                this.state[var2] = 0L;
            }

            Arrays.fill(this.dataQueue, (byte)0);
            this.bitsInQueue = 0;
            this.squeezing = false;
            this.fixedOutputLength = (1600 - var1) / 2;
        } else {
            throw new IllegalStateException("invalid rate value");
        }
    }

    protected void absorb(byte var1) {
        if (this.bitsInQueue % 8 != 0) {
            throw new IllegalStateException("attempt to absorb with odd length queue");
        } else if (this.squeezing) {
            throw new IllegalStateException("attempt to absorb while squeezing");
        } else {
            this.dataQueue[this.bitsInQueue >>> 3] = var1;
            if ((this.bitsInQueue += 8) == this.rate) {
                this.KeccakAbsorb(this.dataQueue, 0);
                this.bitsInQueue = 0;
            }

        }
    }

    protected void absorb(byte[] var1, int var2, int var3) {
        if (this.bitsInQueue % 8 != 0) {
            throw new IllegalStateException("attempt to absorb with odd length queue");
        } else if (this.squeezing) {
            throw new IllegalStateException("attempt to absorb while squeezing");
        } else {
            int var4 = this.bitsInQueue >>> 3;
            int var5 = this.rate >>> 3;
            int var6 = var5 - var4;
            if (var3 < var6) {
                System.arraycopy(var1, var2, this.dataQueue, var4, var3);
                this.bitsInQueue += var3 << 3;
            } else {
                int var7 = 0;
                if (var4 > 0) {
                    System.arraycopy(var1, var2, this.dataQueue, var4, var6);
                    var7 += var6;
                    this.KeccakAbsorb(this.dataQueue, 0);
                }

                int var8;
                while((var8 = var3 - var7) >= var5) {
                    this.KeccakAbsorb(var1, var2 + var7);
                    var7 += var5;
                }

                System.arraycopy(var1, var2 + var7, this.dataQueue, 0, var8);
                this.bitsInQueue = var8 << 3;
            }
        }
    }

    protected void absorbBits(int var1, int var2) {
        if (var2 >= 1 && var2 <= 7) {
            if (this.bitsInQueue % 8 != 0) {
                throw new IllegalStateException("attempt to absorb with odd length queue");
            } else if (this.squeezing) {
                throw new IllegalStateException("attempt to absorb while squeezing");
            } else {
                int var3 = (1 << var2) - 1;
                this.dataQueue[this.bitsInQueue >>> 3] = (byte)(var1 & var3);
                this.bitsInQueue += var2;
            }
        } else {
            throw new IllegalArgumentException("'bits' must be in the range 1 to 7");
        }
    }

    private void padAndSwitchToSqueezingPhase() {
        byte[] var10000 = this.dataQueue;
        int var10001 = this.bitsInQueue >>> 3;
        var10000[var10001] |= (byte)(1 << (this.bitsInQueue & 7));
        long[] var7;
        if (++this.bitsInQueue == this.rate) {
            this.KeccakAbsorb(this.dataQueue, 0);
        } else {
            int var1 = this.bitsInQueue >>> 6;
            int var2 = this.bitsInQueue & 63;
            int var3 = 0;

            for(int var4 = 0; var4 < var1; ++var4) {
                var7 = this.state;
                var7[var4] ^= Pack.littleEndianToLong(this.dataQueue, var3);
                var3 += 8;
            }

            if (var2 > 0) {
                long var6 = (1L << var2) - 1L;
                var7 = this.state;
                var7[var1] ^= Pack.littleEndianToLong(this.dataQueue, var3) & var6;
            }
        }

        var7 = this.state;
        var10001 = this.rate - 1 >>> 6;
        var7[var10001] ^= Long.MIN_VALUE;
        this.bitsInQueue = 0;
        this.squeezing = true;
    }

    protected void squeeze(byte[] var1, int var2, long var3) {
        if (!this.squeezing) {
            this.padAndSwitchToSqueezingPhase();
        }

        if (var3 % 8L != 0L) {
            throw new IllegalStateException("outputLength not a multiple of 8");
        } else {
            int var7;
            for(long var5 = 0L; var5 < var3; var5 += (long)var7) {
                if (this.bitsInQueue == 0) {
                    this.KeccakExtract();
                }

                var7 = (int)Math.min((long)this.bitsInQueue, var3 - var5);
                System.arraycopy(this.dataQueue, (this.rate - this.bitsInQueue) / 8, var1, var2 + (int)(var5 / 8L), var7 / 8);
                this.bitsInQueue -= var7;
            }

        }
    }

    private void KeccakAbsorb(byte[] var1, int var2) {
        int var3 = this.rate >>> 6;

        for(int var4 = 0; var4 < var3; ++var4) {
            long[] var10000 = this.state;
            var10000[var4] ^= Pack.littleEndianToLong(var1, var2);
            var2 += 8;
        }

        this.KeccakPermutation();
    }

    private void KeccakExtract() {
        this.KeccakPermutation();
        Pack.longToLittleEndian(this.state, 0, this.rate >>> 6, this.dataQueue, 0);
        this.bitsInQueue = this.rate;
    }

    private void KeccakPermutation()
    {
        long[] A = state;

        LongVector aline0001 ;
        LongVector aline0203 ;
        LongVector aline0506 ;
        LongVector aline0708 ;
        LongVector aline1011 ;
        LongVector aline1213 ;
        LongVector aline1516 ;
        LongVector aline1718 ;
        LongVector aline2021 ;
        LongVector aline2223 ;

        LongVector cline12 ;
        LongVector cline40 ;
        LongVector cline34;

        for (int i = 0; i < 24; i++)
        {
            // theta
            //  long c0 = a00 ^ a05 ^ a10 ^ a15 ^ a20;
            //  long c1 = a01 ^ a06 ^ a11 ^ a16 ^ a21;
            //  long c2 = a02 ^ a07 ^ a12 ^ a17 ^ a22;
            //  long c3 = a03 ^ a08 ^ a13 ^ a18 ^ a23;
            //  long c4 = a04 ^ a09 ^ a14 ^ a19 ^ a24;
            aline0001 = LongVector.fromArray(SPECIES,A, 0);
            aline0203 = LongVector.fromArray(SPECIES,A, 2);
            aline0506 = LongVector.fromArray(SPECIES,A, 5);
            aline0708 = LongVector.fromArray(SPECIES,A, 7);
            aline1011 = LongVector.fromArray(SPECIES,A, 10);
            aline1213 = LongVector.fromArray(SPECIES,A, 12);
            aline1516 = LongVector.fromArray(SPECIES,A, 15);
            aline1718 = LongVector.fromArray(SPECIES,A, 17);
            aline2021 = LongVector.fromArray(SPECIES,A, 20);
            aline2223 = LongVector.fromArray(SPECIES,A, 22);

            var c0to1 = aline0001.lanewise(VectorOperators.XOR, aline0506).lanewise(VectorOperators.XOR, aline1011).lanewise(VectorOperators.XOR, aline1516).lanewise(VectorOperators.XOR, aline2021);
            var c2to3 = aline0203.lanewise(VectorOperators.XOR, aline0708).lanewise(VectorOperators.XOR, aline1213).lanewise(VectorOperators.XOR, aline1718).lanewise(VectorOperators.XOR, aline2223);

            long[] cArray = c0to1.toArray();
            long c0 = cArray[0];
            long c1 = cArray[1];
            long[] cArray2 = c2to3.toArray();
            long c2 = cArray2[0];
            long c3 = cArray2[1];
            long c4 = A[4] ^ A[9] ^  A[14] ^ A[19] ^ A[24];

            cline12 = LongVector.fromArray(SPECIES,new long[]{c1, c2}, 0);
            cline40 = LongVector.fromArray(SPECIES,new long[]{c4, c0}, 0);
            cline34 = LongVector.fromArray(SPECIES,new long[]{c3, c4}, 0);

            var d12 = cline12.lanewise(VectorOperators.LSHL, 1).lanewise(VectorOperators.OR, cline12.lanewise(VectorOperators.LSHR, -1)).lanewise(VectorOperators.XOR, cline40);
            var d34 = cline34.lanewise(VectorOperators.LSHL, 1).lanewise(VectorOperators.OR, cline34.lanewise(VectorOperators.LSHR, -1)).lanewise(VectorOperators.XOR, cline12);

            //long d1 = (c1 << 1 | c1 >>> -1) ^ c4;
            //long d2 = (c2 << 1 | c2 >>> -1) ^ c0;
            //long d3 = (c3 << 1 | c3 >>> -1) ^ c1;
            //long d4 = (c4 << 1 | c4 >>> -1) ^ c2;

            long d0 = (c0 << 1 | c0 >>> -1) ^ c3;
            //  a00 ^= d1; a05 ^= d1; a10 ^= d1; a15 ^= d1; a20 ^= d1;
            //  a01 ^= d2; a06 ^= d2; a11 ^= d2; a16 ^= d2; a21 ^= d2;
            aline0001 = aline0001.lanewise(VectorOperators.XOR, d12);
            aline0506 = aline0506.lanewise(VectorOperators.XOR, d12);
            aline1011 = aline1011.lanewise(VectorOperators.XOR, d12);
            aline1516 = aline1516.lanewise(VectorOperators.XOR, d12);
            aline2021 = aline2021.lanewise(VectorOperators.XOR, d12);
            //   a02 ^= d3; a07 ^= d3; a12 ^= d3; a17 ^= d3; a22 ^= d3;
            //   a03 ^= d4; a08 ^= d4; a13 ^= d4; a18 ^= d4; a23 ^= d4;
            aline0203 = aline0203.lanewise(VectorOperators.XOR, d34);
            aline0708 = aline0708.lanewise(VectorOperators.XOR, d34);
            aline1213 = aline1213.lanewise(VectorOperators.XOR, d34);
            aline1718 = aline1718.lanewise(VectorOperators.XOR, d34);
            aline2223 = aline2223.lanewise(VectorOperators.XOR, d34);

            aline0001.intoArray(A, 0);
            aline0203.intoArray(A, 2);
            aline0506.intoArray(A, 5);
            aline0708.intoArray(A, 7);
            aline1011.intoArray(A, 10);
            aline1213.intoArray(A, 12);
            aline1516.intoArray(A, 15);
            aline1718.intoArray(A, 17);
            aline2021.intoArray(A, 20);
            aline2223.intoArray(A, 22);

            A[4] ^= d0; A[9] ^= d0; A[14] ^= d0; A[19] ^= d0; A[24] ^= d0;

            // rho/pi
            c1  = A[1] <<  1 | A[1] >>> 63;
            A[1] = A[6] << 44 | A[6] >>> 20;
            A[6] = A[9] << 20 | A[9] >>> 44;
            A[9] = A[22] << 61 | A[22] >>>  3;
            A[22] = A[14] << 39 | A[14] >>> 25;
            A[14] = A[20] << 18 | A[20] >>> 46;
            A[20] = A[2] << 62 | A[2] >>>  2;
            A[2] = A[12] << 43 | A[12] >>> 21;
            A[12] = A[13] << 25 | A[13] >>> 39;
            A[13] = A[19] <<  8 | A[19] >>> 56;
            A[19] = A[23] << 56 | A[23] >>>  8;
            A[23] = A[15] << 41 | A[15] >>> 23;
            A[15] = A[4] << 27 | A[4] >>> 37;
            A[4] = A[24] << 14 | A[24] >>> 50;
            A[24] = A[21] <<  2 | A[21] >>> 62;
            A[21] = A[8] << 55 | A[8] >>>  9;
            A[8] = A[16] << 45 | A[16] >>> 19;
            A[16] = A[5] << 36 | A[5] >>> 28;
            A[5] = A[3] << 28 | A[3] >>> 36;
            A[3] = A[18] << 21 | A[18] >>> 43;
            A[18] = A[17] << 15 | A[17] >>> 49;
            A[17] = A[11] << 10 | A[11] >>> 54;
            A[11] = A[7] <<  6 | A[7] >>> 58;
            A[7] = A[10] <<  3 | A[10] >>> 61;
            A[10] = c1;

            // chi
            c0 = A[0] ^ (~A[1] & A[2]);
            c1 = A[1] ^ (~A[2] & A[3]);
            A[2] ^= ~A[3] & A[4];
            A[3] ^= ~A[4] & A[0];
            A[4] ^= ~A[0] & A[1];
            A[0] = c0;
            A[1] = c1;

            c0 = A[5] ^ (~A[6] & A[7]);
            c1 = A[6] ^ (~A[7] & A[8]);
            A[7] ^= ~A[8] & A[9];
            A[8] ^= ~A[9] & A[5];
            A[9] ^= ~A[5] & A[6];
            A[5] = c0;
            A[6] = c1;

            c0 = A[10] ^ (~A[11] & A[12]);
            c1 = A[11] ^ (~A[12] & A[13]);
            A[12] ^= ~A[13] & A[14];
            A[13] ^= ~A[14] & A[10];
            A[14] ^= ~A[10] & A[11];
            A[10] = c0;
            A[11] = c1;

            c0 = A[15] ^ (~A[16] & A[17]);
            c1 = A[16] ^ (~A[17] & A[18]);
            A[17] ^= ~A[18] & A[19];
            A[18] ^= ~A[19] & A[15];
            A[19] ^= ~A[15] & A[16];
            A[15] = c0;
            A[16] = c1;

            c0 = A[20] ^ (~A[21] & A[22]);
            c1 = A[21] ^ (~A[22] & A[23]);
            A[22] ^= ~A[23] & A[24];
            A[23] ^= ~A[24] & A[20];
            A[24] ^= ~A[20] & A[21];
            A[20] = c0;
            A[21] = c1;

            // iota
            A[0] ^= KeccakRoundConstants[i];
        }
    }

    protected CryptoServiceProperties cryptoServiceProperties() {
        return Utils.getDefaultProperties(this, this.getDigestSize() * 8, this.purpose);
    }
}
