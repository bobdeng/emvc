package com.handwin.web;
public class MD5 {
		static final int S[][] = {
						{7, 12,17, 22},	{5, 9, 14, 20},
						{4, 11,16, 23},	{6, 10,15, 21}};

        static final byte[] PADDING = { -128, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        
        private long[] state = new long[4];  // state (ABCD)
        private long[] count = new long[2];  // number of bits, modulo 2^64 (lsb first)
        private byte[] buffer = new byte[64]; // input buffer
        
        public String digestHexStr;
        
        private byte[] digest = new byte[16];
        
        public String getMD5ofStr(String inbuf) {
                md5Init();
                md5Update(inbuf.getBytes(), inbuf.length());
                md5Final();
                digestHexStr = "";
                for (int i = 0; i < 16; i++) {
                        digestHexStr += byteHEX(digest[i]);
                }
                return digestHexStr;

        }
        public MD5() {
                md5Init();

                return;
        }
        


        private void md5Init() {
                count[0] = 0L;
                count[1] = 0L;
                ///* Load magic initialization constants.

                state[0] = 0x67452301L;
                state[1] = 0xefcdab89L;
                state[2] = 0x98badcfeL;
                state[3] = 0x10325476L;

                return;
        }

        private long F(long x, long y, long z) {
               return (x & y) | ((~x) & z);

        }
        private long G(long x, long y, long z) {
                return (x & z) | (y & (~z));

        }
        private long H(long x, long y, long z) {
                return x ^ y ^ z;
        }

        private long I(long x, long y, long z) {
                return y ^ (x | (~z));
        }
        

        private long FF(long a, long b, long c, long d, long x, long s,
                long ac) {
                a += F (b, c, d) + x + ac;
                a = ((int) a << s) | ((int) a >>> (32 - s));
                a += b;
                return a;
        }

        private long GG(long a, long b, long c, long d, long x, long s,
                long ac) {
                a += G (b, c, d) + x + ac;
                a = ((int) a << s) | ((int) a >>> (32 - s));
                a += b;
                return a;
        }
        private long HH(long a, long b, long c, long d, long x, long s,
                long ac) {
                a += H (b, c, d) + x + ac;
                a = ((int) a << s) | ((int) a >>> (32 - s));
                a += b;
                return a;
        }
        private long II(long a, long b, long c, long d, long x, long s,
                long ac) {
                a += I (b, c, d) + x + ac;
                a = ((int) a << s) | ((int) a >>> (32 - s));
                a += b;
                return a;
        }
        private void md5Update(byte[] inbuf, int inputLen) {

                int i, index, partLen;
                byte[] block = new byte[64];
                index = (int)(count[0] >>> 3) & 0x3F;
                // /* Update number of bits */
                if ((count[0] += (inputLen << 3)) < (inputLen << 3))
                        count[1]++;
                count[1] += (inputLen >>> 29);

                partLen = 64 - index;

                // Transform as many times as possible.
                if (inputLen >= partLen) {
                        md5Memcpy(buffer, inbuf, index, 0, partLen);
                        md5Transform(buffer);

                        for (i = partLen; i + 63 < inputLen; i += 64) {

                                md5Memcpy(block, inbuf, 0, i, 64);
                                md5Transform (block);
                        }
                        index = 0;

                } else

                        i = 0;

                ///* Buffer remaining input */
                md5Memcpy(buffer, inbuf, index, i, inputLen - i);

        }
        
        private void md5Final () {
                byte[] bits = new byte[8];
                int index, padLen;

                ///* Save number of bits */
                Encode (bits, count, 8);

                ///* Pad out to 56 mod 64.
                index = (int)(count[0] >>> 3) & 0x3f;
                padLen = (index < 56) ? (56 - index) : (120 - index);
                md5Update (PADDING, padLen);

                ///* Append length (before padding) */
                md5Update(bits, 8);

                ///* Store state in digest */
                Encode (digest, state, 16);

        }

        private void md5Memcpy (byte[] output, byte[] input,
                int outpos, int inpos, int len)
        {
                int i;

                for (i = 0; i < len; i++)
                        output[outpos + i] = input[inpos + i];
        }
        
        private void md5Transform (byte block[]) {
                long a = state[0], b = state[1], c = state[2], d = state[3];
                long[] x = new long[16];

                Decode (x, block, 64);

                /* Round 1 */
                a = FF (a, b, c, d, x[0], S[0][0], 0xd76aa478L); /* 1 */
                d = FF (d, a, b, c, x[1], S[0][1], 0xe8c7b756L); /* 2 */
                c = FF (c, d, a, b, x[2], S[0][2], 0x242070dbL); /* 3 */
                b = FF (b, c, d, a, x[3], S[0][3], 0xc1bdceeeL); /* 4 */
                a = FF (a, b, c, d, x[4], S[0][0], 0xf57c0fafL); /* 5 */
                d = FF (d, a, b, c, x[5], S[0][1], 0x4787c62aL); /* 6 */
                c = FF (c, d, a, b, x[6], S[0][2], 0xa8304613L); /* 7 */
                b = FF (b, c, d, a, x[7], S[0][3], 0xfd469501L); /* 8 */
                a = FF (a, b, c, d, x[8], S[0][0], 0x698098d8L); /* 9 */
                d = FF (d, a, b, c, x[9], S[0][1], 0x8b44f7afL); /* 10 */
                c = FF (c, d, a, b, x[10], S[0][2], 0xffff5bb1L); /* 11 */
                b = FF (b, c, d, a, x[11], S[0][3], 0x895cd7beL); /* 12 */
                a = FF (a, b, c, d, x[12], S[0][0], 0x6b901122L); /* 13 */
                d = FF (d, a, b, c, x[13], S[0][1], 0xfd987193L); /* 14 */
                c = FF (c, d, a, b, x[14], S[0][2], 0xa679438eL); /* 15 */
                b = FF (b, c, d, a, x[15], S[0][3], 0x49b40821L); /* 16 */

                /* Round 2 */
                a = GG (a, b, c, d, x[1], S[1][0], 0xf61e2562L); /* 17 */
                d = GG (d, a, b, c, x[6], S[1][1], 0xc040b340L); /* 18 */
                c = GG (c, d, a, b, x[11], S[1][2], 0x265e5a51L); /* 19 */
                b = GG (b, c, d, a, x[0], S[1][3], 0xe9b6c7aaL); /* 20 */
                a = GG (a, b, c, d, x[5], S[1][0], 0xd62f105dL); /* 21 */
                d = GG (d, a, b, c, x[10], S[1][1], 0x2441453L); /* 22 */
                c = GG (c, d, a, b, x[15], S[1][2], 0xd8a1e681L); /* 23 */
                b = GG (b, c, d, a, x[4], S[1][3], 0xe7d3fbc8L); /* 24 */
                a = GG (a, b, c, d, x[9], S[1][0], 0x21e1cde6L); /* 25 */
                d = GG (d, a, b, c, x[14], S[1][1], 0xc33707d6L); /* 26 */
                c = GG (c, d, a, b, x[3], S[1][2], 0xf4d50d87L); /* 27 */
                b = GG (b, c, d, a, x[8], S[1][3], 0x455a14edL); /* 28 */
                a = GG (a, b, c, d, x[13], S[1][0], 0xa9e3e905L); /* 29 */
                d = GG (d, a, b, c, x[2], S[1][1], 0xfcefa3f8L); /* 30 */
                c = GG (c, d, a, b, x[7], S[1][2], 0x676f02d9L); /* 31 */
                b = GG (b, c, d, a, x[12], S[1][3], 0x8d2a4c8aL); /* 32 */

                /* Round 3 */
                a = HH (a, b, c, d, x[5], S[2][0], 0xfffa3942L); /* 33 */
                d = HH (d, a, b, c, x[8], S[2][1], 0x8771f681L); /* 34 */
                c = HH (c, d, a, b, x[11], S[2][2], 0x6d9d6122L); /* 35 */
                b = HH (b, c, d, a, x[14], S[2][3], 0xfde5380cL); /* 36 */
                a = HH (a, b, c, d, x[1], S[2][0], 0xa4beea44L); /* 37 */
                d = HH (d, a, b, c, x[4], S[2][1], 0x4bdecfa9L); /* 38 */
                c = HH (c, d, a, b, x[7], S[2][2], 0xf6bb4b60L); /* 39 */
                b = HH (b, c, d, a, x[10], S[2][3], 0xbebfbc70L); /* 40 */
                a = HH (a, b, c, d, x[13], S[2][0], 0x289b7ec6L); /* 41 */
                d = HH (d, a, b, c, x[0], S[2][1], 0xeaa127faL); /* 42 */
                c = HH (c, d, a, b, x[3], S[2][2], 0xd4ef3085L); /* 43 */
                b = HH (b, c, d, a, x[6], S[2][3], 0x4881d05L); /* 44 */
                a = HH (a, b, c, d, x[9], S[2][0], 0xd9d4d039L); /* 45 */
                d = HH (d, a, b, c, x[12], S[2][1], 0xe6db99e5L); /* 46 */
                c = HH (c, d, a, b, x[15], S[2][2], 0x1fa27cf8L); /* 47 */
                b = HH (b, c, d, a, x[2], S[2][3], 0xc4ac5665L); /* 48 */

                /* Round 4 */
                a = II (a, b, c, d, x[0], S[3][0], 0xf4292244L); /* 49 */
                d = II (d, a, b, c, x[7], S[3][1], 0x432aff97L); /* 50 */
                c = II (c, d, a, b, x[14], S[3][2], 0xab9423a7L); /* 51 */
                b = II (b, c, d, a, x[5], S[3][3], 0xfc93a039L); /* 52 */
                a = II (a, b, c, d, x[12], S[3][0], 0x655b59c3L); /* 53 */
                d = II (d, a, b, c, x[3], S[3][1], 0x8f0ccc92L); /* 54 */
                c = II (c, d, a, b, x[10], S[3][2], 0xffeff47dL); /* 55 */
                b = II (b, c, d, a, x[1], S[3][3], 0x85845dd1L); /* 56 */
                a = II (a, b, c, d, x[8], S[3][0], 0x6fa87e4fL); /* 57 */
                d = II (d, a, b, c, x[15], S[3][1], 0xfe2ce6e0L); /* 58 */
                c = II (c, d, a, b, x[6], S[3][2], 0xa3014314L); /* 59 */
                b = II (b, c, d, a, x[13], S[3][3], 0x4e0811a1L); /* 60 */
                a = II (a, b, c, d, x[4], S[3][0], 0xf7537e82L); /* 61 */
                d = II (d, a, b, c, x[11], S[3][1], 0xbd3af235L); /* 62 */
                c = II (c, d, a, b, x[2], S[3][2], 0x2ad7d2bbL); /* 63 */
                b = II (b, c, d, a, x[9], S[3][3], 0xeb86d391L); /* 64 */

                state[0] += a;
                state[1] += b;
                state[2] += c;
                state[3] += d;

        }
        
        private void Encode (byte[] output, long[] input, int len) {
                int i, j;

                for (i = 0, j = 0; j < len; i++, j += 4) {
                        output[j] = (byte)(input[i] & 0xffL);
                        output[j + 1] = (byte)((input[i] >>> 8) & 0xffL);
                        output[j + 2] = (byte)((input[i] >>> 16) & 0xffL);
                        output[j + 3] = (byte)((input[i] >>> 24) & 0xffL);
                }
        }

        private void Decode (long[] output, byte[] input, int len) {
                int i, j;


                for (i = 0, j = 0; j < len; i++, j += 4)
                        output[i] = b2iu(input[j]) |
                                (b2iu(input[j + 1]) << 8) |
                                (b2iu(input[j + 2]) << 16) |
                                (b2iu(input[j + 3]) << 24);

                return;
        }
       
        public static long b2iu(byte b) {
                return b < 0 ? b & 0x7F + 128 : b;
        }
        public static String byteHEX(byte ib) {
                char[] Digit = { '0','1','2','3','4','5','6','7','8','9',
                'A','B','C','D','E','F' };
                char [] ob = new char[2];
                ob[0] = Digit[(ib >>> 4) & 0X0F];
                ob[1] = Digit[ib & 0X0F];
                String s = new String(ob);
                return s;
        }
/*
        public static void main(String args[]) {


                MD5 m = new MD5();
                if (Array.getLength(args) == 0) {   //���û�в���ִ�б�׼��Test Suite
                
                       	System.out.println("MD5 Test suite:");
                	System.out.println("MD5(\"\"):"+m.getMD5ofStr(""));
                	System.out.println("MD5(\"a\"):"+m.getMD5ofStr("a"));
                	System.out.println("MD5(\"abc\"):"+m.getMD5ofStr("abc"));
                	System.out.println("MD5(\"message digest\"):"+m.getMD5ofStr("message digest"));
                	System.out.println("MD5(\"abcdefghijklmnopqrstuvwxyz\"):"+
                        m.getMD5ofStr("abcdefghijklmnopqrstuvwxyz"));
                	System.out.println("MD5(\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789\"):"+
                     	m.getMD5ofStr("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"));
                }
                else 
                      	System.out.println("MD5(" + args[0] + ")=" + m.getMD5ofStr(args[0]));
                
         
        }*/

}

