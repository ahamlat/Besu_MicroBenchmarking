//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.bouncycastle.crypto.digests;

import jdk.incubator.vector.LongVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorShuffle;
import jdk.incubator.vector.VectorSpecies;
import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class KeccakDigest implements ExtendedDigest {
    private static long[] KeccakRoundConstants = new long[]{1L, 32898L, -9223372036854742902L, -9223372034707259392L, 32907L, 2147483649L, -9223372034707259263L, -9223372036854743031L, 138L, 136L, 2147516425L, 2147483658L, 2147516555L, -9223372036854775669L, -9223372036854742903L, -9223372036854743037L, -9223372036854743038L, -9223372036854775680L, 32778L, -9223372034707292150L, -9223372034707259263L, -9223372036854742912L, 2147483649L, -9223372034707259384L};
    private VectorSpecies<Long> SPECIES = LongVector.SPECIES_512;

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
        VectorMask<Long> mask = VectorMask.fromLong(SPECIES, 0b11111);
        LongVector a0to4Vector ;
        LongVector a5to9Vector ;
        LongVector a10t014Vector ;
        LongVector a15to19Vector ;
        LongVector a20to24Vector ;
        var rhoA0to4Left = LongVector.fromArray(SPECIES, new long[]{0L, 1L, 62L, 28L, 27L}, 0, mask);
        var rhoA0to4Right = LongVector.fromArray(SPECIES, new long[]{0L, 63L, 2L, 36L, 37L}, 0, mask);

        var rhoA5to9Left = LongVector.fromArray(SPECIES, new long[]{36L, 44L, 6L, 55L, 20L}, 0, mask);
        var rhoA5to9Right = LongVector.fromArray(SPECIES, new long[]{28L, 20L, 58L, 9L, 44L}, 0, mask);

        var rhoA10to14Left = LongVector.fromArray(SPECIES, new long[]{3L, 10L, 43L, 25L, 39L}, 0, mask);
        var rhoA10to14Right = LongVector.fromArray(SPECIES, new long[]{61L, 54L, 21L, 39L, 25L}, 0, mask);

        var rhoA15to19Left = LongVector.fromArray(SPECIES, new long[]{41L, 45L, 15L, 21L, 8L}, 0, mask);
        var rhoA15to19Right = LongVector.fromArray(SPECIES, new long[]{23L, 19L, 49L, 43L, 56L}, 0, mask);

        var rhoA20to24Left = LongVector.fromArray(SPECIES, new long[]{18L, 2L, 61L, 56L, 14L}, 0, mask);
        var rhoA20to24Right = LongVector.fromArray(SPECIES, new long[]{46L, 62L, 3L, 8L, 50L}, 0, mask);

        long c0;
        long c1;

        for (int i = 0; i < 24; i++)
        {
            // theta
            //  long c0 = a00 ^ a05 ^ a10 ^ a15 ^ a20;
            //  long c1 = a01 ^ a06 ^ a11 ^ a16 ^ a21;
            //  long c2 = a02 ^ a07 ^ a12 ^ a17 ^ a22;
            //  long c3 = a03 ^ a08 ^ a13 ^ a18 ^ a23;
            //  long c4 = a04 ^ a09 ^ a14 ^ a19 ^ a24;
            a0to4Vector = LongVector.fromArray(SPECIES,A, 0, mask);
            a5to9Vector = LongVector.fromArray(SPECIES,A, 5, mask);
            a10t014Vector = LongVector.fromArray(SPECIES,A, 10, mask);
            a15to19Vector = LongVector.fromArray(SPECIES,A, 15, mask);
            a20to24Vector = LongVector.fromArray(SPECIES,A, 20, mask);

            var c0to4Vector = a0to4Vector.lanewise(VectorOperators.XOR, a5to9Vector).lanewise(VectorOperators.XOR, a10t014Vector).lanewise(VectorOperators.XOR, a15to19Vector).lanewise(VectorOperators.XOR, a20to24Vector);
            var shuffle = VectorShuffle.fromValues(SPECIES, 3, 4, 0, 1, 2, 5, 6, 7); // Rearrange the elements according to the shuffle
            var c0to4VectorShuffled = c0to4Vector.rearrange(shuffle);
            var d0to4Vector = c0to4Vector.lanewise(VectorOperators.LSHL, 1).lanewise(VectorOperators.OR, c0to4Vector.lanewise(VectorOperators.LSHR, -1)).lanewise(VectorOperators.XOR, c0to4VectorShuffled);
            // long d0 = (c0 << 1 | c0 >>> -1) ^ c3;
            //long d1 = (c1 << 1 | c1 >>> -1) ^ c4;
            //long d2 = (c2 << 1 | c2 >>> -1) ^ c0;
            //long d3 = (c3 << 1 | c3 >>> -1) ^ c1;
            //long d4 = (c4 << 1 | c4 >>> -1) ^ c2;

            var dshuffle = VectorShuffle.fromValues(SPECIES, 1, 2, 3, 4, 0, 5, 6, 7); // Rearrange the elements according to the shuffle
            var d0to4VectorSuffled = d0to4Vector.rearrange(dshuffle);
            a0to4Vector = a0to4Vector.lanewise(VectorOperators.XOR, d0to4VectorSuffled);
            a5to9Vector = a5to9Vector.lanewise(VectorOperators.XOR, d0to4VectorSuffled);
            a10t014Vector = a10t014Vector.lanewise(VectorOperators.XOR, d0to4VectorSuffled);
            a15to19Vector = a15to19Vector.lanewise(VectorOperators.XOR, d0to4VectorSuffled);
            a20to24Vector = a20to24Vector.lanewise(VectorOperators.XOR, d0to4VectorSuffled);

            //  a00 ^= d1; a05 ^= d1; a10 ^= d1; a15 ^= d1; a20 ^= d1;
            //  a01 ^= d2; a06 ^= d2; a11 ^= d2; a16 ^= d2; a21 ^= d2;
            //   a02 ^= d3; a07 ^= d3; a12 ^= d3; a17 ^= d3; a22 ^= d3;
            //   a03 ^= d4; a08 ^= d4; a13 ^= d4; a18 ^= d4; a23 ^= d4;
            // A[4] ^= d0; A[9] ^= d0; A[14] ^= d0; A[19] ^= d0; A[24] ^= d0;


            // rho
            a0to4Vector = a0to4Vector.lanewise(VectorOperators.LSHL, rhoA0to4Left).lanewise(VectorOperators.OR, a0to4Vector.lanewise(VectorOperators.LSHR, rhoA0to4Right));
            a5to9Vector = a5to9Vector.lanewise(VectorOperators.LSHL, rhoA5to9Left).lanewise(VectorOperators.OR, a5to9Vector.lanewise(VectorOperators.LSHR, rhoA5to9Right));
            a10t014Vector = a10t014Vector.lanewise(VectorOperators.LSHL, rhoA10to14Left).lanewise(VectorOperators.OR, a10t014Vector.lanewise(VectorOperators.LSHR, rhoA10to14Right));
            a15to19Vector = a15to19Vector.lanewise(VectorOperators.LSHL, rhoA15to19Left).lanewise(VectorOperators.OR, a15to19Vector.lanewise(VectorOperators.LSHR, rhoA15to19Right));
            a20to24Vector = a20to24Vector.lanewise(VectorOperators.LSHL, rhoA20to24Left).lanewise(VectorOperators.OR, a20to24Vector.lanewise(VectorOperators.LSHR, rhoA20to24Right));

            a0to4Vector.intoArray(A, 0, mask);
            a5to9Vector.intoArray(A, 5, mask);
            a10t014Vector.intoArray(A, 10, mask);
            a15to19Vector.intoArray(A, 15, mask);
            a20to24Vector.intoArray(A, 20, mask);

            // rho/pi
            /*c1  = A[1] <<  1 | A[1] >>> 63;
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
            A[10] = c1;*/

            // Pi
            c0 =  A[16];
            A[16] = A[5];
            A[5] = A[3];
            A[3] = A[18];
            A[18] = A[17];
            A[17] = A[11];
            A[11] = A[7];
            A[7] = A[10];
            A[10] = A[1];
            A[1] = A[6];
            A[6] = A[9];
            A[9] = A[22];
            A[22] = A[14];
            A[14] = A[20];
            A[20] = A[2];
            A[2] = A[12];
            A[12] = A[13];
            A[13] = A[19];
            A[19] = A[23];
            A[23] = A[15];
            A[15] = A[4];
            A[4] = A[24];
            A[24] = A[21];
            A[21] = A[8];
            A[8] = c0;

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
