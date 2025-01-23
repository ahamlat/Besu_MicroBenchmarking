package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.Digest;

class Utils {
    Utils() {
    }

    static CryptoServiceProperties getDefaultProperties(Digest var0, CryptoServicePurpose var1) {
        return new DefaultProperties(var0.getDigestSize() * 4, var0.getAlgorithmName(), var1);
    }

    static CryptoServiceProperties getDefaultProperties(Digest var0, int var1, CryptoServicePurpose var2) {
        return new DefaultPropertiesWithPRF(var0.getDigestSize() * 4, var1, var0.getAlgorithmName(), var2);
    }

    private static class DefaultProperties implements CryptoServiceProperties {
        private final int bitsOfSecurity;
        private final String algorithmName;
        private final CryptoServicePurpose purpose;

        public DefaultProperties(int var1, String var2, CryptoServicePurpose var3) {
            this.bitsOfSecurity = var1;
            this.algorithmName = var2;
            this.purpose = var3;
        }

        public int bitsOfSecurity() {
            return this.bitsOfSecurity;
        }

        public String getServiceName() {
            return this.algorithmName;
        }

        public CryptoServicePurpose getPurpose() {
            return this.purpose;
        }

        public Object getParams() {
            return null;
        }
    }

    private static class DefaultPropertiesWithPRF implements CryptoServiceProperties {
        private final int bitsOfSecurity;
        private final int prfBitsOfSecurity;
        private final String algorithmName;
        private final CryptoServicePurpose purpose;

        public DefaultPropertiesWithPRF(int var1, int var2, String var3, CryptoServicePurpose var4) {
            this.bitsOfSecurity = var1;
            this.prfBitsOfSecurity = var2;
            this.algorithmName = var3;
            this.purpose = var4;
        }

        public int bitsOfSecurity() {
            return this.purpose == CryptoServicePurpose.PRF ? this.prfBitsOfSecurity : this.bitsOfSecurity;
        }

        public String getServiceName() {
            return this.algorithmName;
        }

        public CryptoServicePurpose getPurpose() {
            return this.purpose;
        }

        public Object getParams() {
            return null;
        }
    }


    public static void pi() {
        for (int x = 0; x < 5; x++)
        {
            for (int y = 0; y < 5; y++)
            {
                //System.out.println("x= "+x+", y = "+y);
                System.out.println("A["+(y + 5 * ((2 * x + 3 * y) % 5))+"] = tempA["+(x + 5 * y)+"]");
            }
        }
        // output
        /*
        A[0] = tempA[0]
        A[16] = tempA[5]
        A[7] = tempA[10]
        A[23] = tempA[15]
        A[14] = tempA[20]
        A[10] = tempA[1]
        A[1] = tempA[6]
        A[17] = tempA[11]
        A[8] = tempA[16]
        A[24] = tempA[21]
        A[20] = tempA[2]
        A[11] = tempA[7]
        A[2] = tempA[12]
        A[18] = tempA[17]
        A[9] = tempA[22]
        A[5] = tempA[3]
        A[21] = tempA[8]
        A[12] = tempA[13]
        A[3] = tempA[18]
        A[19] = tempA[23]
        A[15] = tempA[4]
        A[6] = tempA[9]
        A[22] = tempA[14]
        A[13] = tempA[19]
        A[4] = tempA[24]
         */
    }

    public static void chi() {
        for (int y = 0; y < 5; y++)
        {
            for (int x = 0; x < 5; x++)
            {
                System.out.println("chiC["+x+"] = A["+(x + 5 * y)+"] ^ ((~A["+(((x + 1) % 5) + 5 * y)+"]) & A["+(((x + 2) % 5) + 5 * y)+"])");
            }
            for (int x = 0; x < 5; x++)
            {
                System.out.println("A["+(x + 5 * y)+"] = chiC["+x+"]");
            }
        }
        // output
        /*
        chiC[0] = A[0] ^ ((~A[1]) & A[2])
        chiC[1] = A[1] ^ ((~A[2]) & A[3])
        chiC[2] = A[2] ^ ((~A[3]) & A[4])
        chiC[3] = A[3] ^ ((~A[4]) & A[0])
        chiC[4] = A[4] ^ ((~A[0]) & A[1])
        A[0] = chiC[0]
        A[1] = chiC[1]
        A[2] = chiC[2]
        A[3] = chiC[3]
        A[4] = chiC[4]
        chiC[0] = A[5] ^ ((~A[6]) & A[7])
        chiC[1] = A[6] ^ ((~A[7]) & A[8])
        chiC[2] = A[7] ^ ((~A[8]) & A[9])
        chiC[3] = A[8] ^ ((~A[9]) & A[5])
        chiC[4] = A[9] ^ ((~A[5]) & A[6])
        A[5] = chiC[0]
        A[6] = chiC[1]
        A[7] = chiC[2]
        A[8] = chiC[3]
        A[9] = chiC[4]
        chiC[0] = A[10] ^ ((~A[11]) & A[12])
        chiC[1] = A[11] ^ ((~A[12]) & A[13])
        chiC[2] = A[12] ^ ((~A[13]) & A[14])
        chiC[3] = A[13] ^ ((~A[14]) & A[10])
        chiC[4] = A[14] ^ ((~A[10]) & A[11])
        A[10] = chiC[0]
        A[11] = chiC[1]
        A[12] = chiC[2]
        A[13] = chiC[3]
        A[14] = chiC[4]
        chiC[0] = A[15] ^ ((~A[16]) & A[17])
        chiC[1] = A[16] ^ ((~A[17]) & A[18])
        chiC[2] = A[17] ^ ((~A[18]) & A[19])
        chiC[3] = A[18] ^ ((~A[19]) & A[15])
        chiC[4] = A[19] ^ ((~A[15]) & A[16])
        A[15] = chiC[0]
        A[16] = chiC[1]
        A[17] = chiC[2]
        A[18] = chiC[3]
        A[19] = chiC[4]
        chiC[0] = A[20] ^ ((~A[21]) & A[22])
        chiC[1] = A[21] ^ ((~A[22]) & A[23])
        chiC[2] = A[22] ^ ((~A[23]) & A[24])
        chiC[3] = A[23] ^ ((~A[24]) & A[20])
        chiC[4] = A[24] ^ ((~A[20]) & A[21])
        A[20] = chiC[0]
        A[21] = chiC[1]
        A[22] = chiC[2]
        A[23] = chiC[3]
        A[24] = chiC[4]
         */
    }

    private static int[] keccakInitializeRhoOffsets()
    {
        int[] keccakRhoOffsets = new int[25];
        int x, y, t, newX, newY;
        keccakRhoOffsets[(((0) % 5) + 5 * ((0) % 5))] = 0;
        x = 1;
        y = 0;
        for (t = 0; t < 24; t++)
        {
            keccakRhoOffsets[(((x) % 5) + 5 * ((y) % 5))] = ((t + 1) * (t + 2) / 2) % 64;
            newX = (0 * x + 1 * y) % 5;
            newY = (2 * x + 3 * y) % 5;
            x = newX;
            y = newY;
        }
        return keccakRhoOffsets;
    }

    public static void rho()
    {
        int[] keccakRhoOffsets = keccakInitializeRhoOffsets();
        for (int x = 0; x < 5; x++)
        {
            for (int y = 0; y < 5; y++)
            {
                int index = x + 5 * y;
                if (keccakRhoOffsets[index] != 0) {
                    System.out.println("A["+index+"] =  (((A["+index+"]) << "+keccakRhoOffsets[index]+") ^ ((A["+index+"]) >>> ("+(64 - keccakRhoOffsets[index])+"]))");
                } else {
                    System.out.println("A["+index+"] = A["+index+"]");
                }
            }
        }
        // output
        /*
        A[0] = A[0]
        A[5] =  (((A[5]) << 36) ^ ((A[5]) >>> (28]))
        A[10] =  (((A[10]) << 3) ^ ((A[10]) >>> (61]))
        A[15] =  (((A[15]) << 41) ^ ((A[15]) >>> (23]))
        A[20] =  (((A[20]) << 18) ^ ((A[20]) >>> (46]))
        A[1] =  (((A[1]) << 1) ^ ((A[1]) >>> (63]))
        A[6] =  (((A[6]) << 44) ^ ((A[6]) >>> (20]))
        A[11] =  (((A[11]) << 10) ^ ((A[11]) >>> (54]))
        A[16] =  (((A[16]) << 45) ^ ((A[16]) >>> (19]))
        A[21] =  (((A[21]) << 2) ^ ((A[21]) >>> (62]))
        A[2] =  (((A[2]) << 62) ^ ((A[2]) >>> (2]))
        A[7] =  (((A[7]) << 6) ^ ((A[7]) >>> (58]))
        A[12] =  (((A[12]) << 43) ^ ((A[12]) >>> (21]))
        A[17] =  (((A[17]) << 15) ^ ((A[17]) >>> (49]))
        A[22] =  (((A[22]) << 61) ^ ((A[22]) >>> (3]))
        A[3] =  (((A[3]) << 28) ^ ((A[3]) >>> (36]))
        A[8] =  (((A[8]) << 55) ^ ((A[8]) >>> (9]))
        A[13] =  (((A[13]) << 25) ^ ((A[13]) >>> (39]))
        A[18] =  (((A[18]) << 21) ^ ((A[18]) >>> (43]))
        A[23] =  (((A[23]) << 56) ^ ((A[23]) >>> (8]))
        A[4] =  (((A[4]) << 27) ^ ((A[4]) >>> (37]))
        A[9] =  (((A[9]) << 20) ^ ((A[9]) >>> (44]))
        A[14] =  (((A[14]) << 39) ^ ((A[14]) >>> (25]))
        A[19] =  (((A[19]) << 8) ^ ((A[19]) >>> (56]))
        A[24] =  (((A[24]) << 14) ^ ((A[24]) >>> (50]))
         */
    }

    public static void theta()
    {
        for (int x = 0; x < 5; x++)
        {
            System.out.println("C["+x+"] = 0");
            for (int y = 0; y < 5; y++)
            {
                System.out.println("C["+x+"] ^= A["+(x + 5 * y)+"]");
            }
        }
        for (int x = 0; x < 5; x++)
        {
            System.out.println("d"+x+" = ((((C["+((x + 1) % 5)+"]) << 1) ^ ((C[("+((x + 1) % 5)+"]) >>> "+(64 - 1)+"))) ^ C["+((x + 4) % 5)+"]");
            for (int y = 0; y < 5; y++)
            {
                System.out.println("A["+(x + 5 * y)+"] ^= d"+x);
            }
        }
    }

    public static void main(String[] args) {
        theta();
        rho();
        pi();
        chi();
        //iota();

    }
}
