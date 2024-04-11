/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package math.fast;

/** Class used to compute the classical functions tables.
 * @version $Id: FastMathCalc.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.0
 */
class CommonsCalc {

    /**
     * 0x40000000 - used to split a double into two parts, both with the low order bits cleared.
     * Equivalent to 2^30.
     */
    private static final long HEX_40000000 = 0x40000000L; // 1073741824L

    /** Factorial table, for Taylor series expansions. 0!, 1!, 2!, ... 19! */
    private static final double FACT[] = new double[]
        {
        +1.0d,                        // 0
        +1.0d,                        // 1
        +2.0d,                        // 2
        +6.0d,                        // 3
        +24.0d,                       // 4
        +120.0d,                      // 5
        +720.0d,                      // 6
        +5040.0d,                     // 7
        +40320.0d,                    // 8
        +362880.0d,                   // 9
        +3628800.0d,                  // 10
        +39916800.0d,                 // 11
        +479001600.0d,                // 12
        +6227020800.0d,               // 13
        +87178291200.0d,              // 14
        +1307674368000.0d,            // 15
        +20922789888000.0d,           // 16
        +355687428096000.0d,          // 17
        +6402373705728000.0d,         // 18
        +121645100408832000.0d,       // 19
        };

    /**
     * Private Constructor.
     */
    private CommonsCalc() {
    }

    /**
     *  For x between 0 and 1, returns exp(x), uses extended precision
     *  @param x argument of exponential
     *  @param result placeholder where to place exp(x) split in two terms
     *  for extra precision (i.e. exp(x) = result[0] + result[1]
     *  @return exp(x)
     */
    static double slowexp(final double x, final double result[]) {
        final double xs[] = new double[2];
        final double ys[] = new double[2];
        final double facts[] = new double[2];
        final double as[] = new double[2];
        split(x, xs);
        ys[0] = ys[1] = 0.0;

        for (int i = FACT.length-1; i >= 0; i--) {
            splitMult(xs, ys, as);
            ys[0] = as[0];
            ys[1] = as[1];

            split(FACT[i], as);
            splitReciprocal(as, facts);

            splitAdd(ys, facts, as);
            ys[0] = as[0];
            ys[1] = as[1];
        }

        if (result != null) {
            result[0] = ys[0];
            result[1] = ys[1];
        }

        return ys[0] + ys[1];
    }

    /** Compute split[0], split[1] such that their sum is equal to d,
     * and split[0] has its 30 least significant bits as zero.
     * @param d number to split
     * @param split placeholder where to place the result
     */
    private static void split(final double d, final double split[]) {
        if (d < 8e298 && d > -8e298) {
            final double a = d * HEX_40000000;
            split[0] = (d + a) - a;
            split[1] = d - split[0];
        } else {
            final double a = d * 9.31322574615478515625E-10;
            split[0] = (d + a - d) * HEX_40000000;
            split[1] = d - split[0];
        }
    }

    /** Recompute a split.
     * @param a input/out array containing the split, changed
     * on output
     */
    private static void resplit(final double a[]) {
        final double c = a[0] + a[1];
        final double d = -(c - a[0] - a[1]);

        if (c < 8e298 && c > -8e298) { // MAGIC NUMBER
            double z = c * HEX_40000000;
            a[0] = (c + z) - z;
            a[1] = c - a[0] + d;
        } else {
            double z = c * 9.31322574615478515625E-10;
            a[0] = (c + z - c) * HEX_40000000;
            a[1] = c - a[0] + d;
        }
    }

    /** Multiply two numbers in split form.
     * @param a first term of multiplication
     * @param b second term of multiplication
     * @param ans placeholder where to put the result
     */
    private static void splitMult(double a[], double b[], double ans[]) {
        ans[0] = a[0] * b[0];
        ans[1] = a[0] * b[1] + a[1] * b[0] + a[1] * b[1];

        /* Resplit */
        resplit(ans);
    }

    /** Add two numbers in split form.
     * @param a first term of addition
     * @param b second term of addition
     * @param ans placeholder where to put the result
     */
    private static void splitAdd(final double a[], final double b[], final double ans[]) {
        ans[0] = a[0] + b[0];
        ans[1] = a[1] + b[1];

        resplit(ans);
    }

    /** Compute the reciprocal of in.  Use the following algorithm.
     *  in = c + d.
     *  want to find x + y such that x+y = 1/(c+d) and x is much
     *  larger than y and x has several zero bits on the right.
     *
     *  Set b = 1/(2^22),  a = 1 - b.  Thus (a+b) = 1.
     *  Use following identity to compute (a+b)/(c+d)
     *
     *  (a+b)/(c+d)  =   a/c   +    (bc - ad) / (c^2 + cd)
     *  set x = a/c  and y = (bc - ad) / (c^2 + cd)
     *  This will be close to the right answer, but there will be
     *  some rounding in the calculation of X.  So by carefully
     *  computing 1 - (c+d)(x+y) we can compute an error and
     *  add that back in.   This is done carefully so that terms
     *  of similar size are subtracted first.
     *  @param in initial number, in split form
     *  @param result placeholder where to put the result
     */
    static void splitReciprocal(final double in[], final double result[]) {
        final double b = 1.0/4194304.0;
        final double a = 1.0 - b;

        if (in[0] == 0.0) {
            in[0] = in[1];
            in[1] = 0.0;
        }

        result[0] = a / in[0];
        result[1] = (b*in[0]-a*in[1]) / (in[0]*in[0] + in[0]*in[1]);

        if (result[1] != result[1]) { // can happen if result[1] is NAN
            result[1] = 0.0;
        }

        /* Resplit */
        resplit(result);

        for (int i = 0; i < 2; i++) {
            /* this may be overkill, probably once is enough */
            double err = 1.0 - result[0] * in[0] - result[0] * in[1] -
            result[1] * in[0] - result[1] * in[1];
            /*err = 1.0 - err; */
            err = err * (result[0] + result[1]);
            /*printf("err = %16e\n", err); */
            result[1] += err;
        }
    }

    /** Compute (a[0] + a[1]) * (b[0] + b[1]) in extended precision.
     * @param a first term of the multiplication
     * @param b second term of the multiplication
     * @param result placeholder where to put the result
     */
    private static void quadMult(final double a[], final double b[], final double result[]) {
        final double xs[] = new double[2];
        final double ys[] = new double[2];
        final double zs[] = new double[2];

        /* a[0] * b[0] */
        split(a[0], xs);
        split(b[0], ys);
        splitMult(xs, ys, zs);

        result[0] = zs[0];
        result[1] = zs[1];

        /* a[0] * b[1] */
        split(b[1], ys);
        splitMult(xs, ys, zs);

        double tmp = result[0] + zs[0];
        result[1] = result[1] - (tmp - result[0] - zs[0]);
        result[0] = tmp;
        tmp = result[0] + zs[1];
        result[1] = result[1] - (tmp - result[0] - zs[1]);
        result[0] = tmp;

        /* a[1] * b[0] */
        split(a[1], xs);
        split(b[0], ys);
        splitMult(xs, ys, zs);

        tmp = result[0] + zs[0];
        result[1] = result[1] - (tmp - result[0] - zs[0]);
        result[0] = tmp;
        tmp = result[0] + zs[1];
        result[1] = result[1] - (tmp - result[0] - zs[1]);
        result[0] = tmp;

        /* a[1] * b[0] */
        split(a[1], xs);
        split(b[1], ys);
        splitMult(xs, ys, zs);

        tmp = result[0] + zs[0];
        result[1] = result[1] - (tmp - result[0] - zs[0]);
        result[0] = tmp;
        tmp = result[0] + zs[1];
        result[1] = result[1] - (tmp - result[0] - zs[1]);
        result[0] = tmp;
    }

    /** Compute exp(p) for a integer p in extended precision.
     * @param p integer whose exponential is requested
     * @param result placeholder where to put the result in extended precision
     * @return exp(p) in standard precision (equal to result[0] + result[1])
     */
    static double expint(int p, final double result[]) {
        //double x = M_E;
        final double xs[] = new double[2];
        final double as[] = new double[2];
        final double ys[] = new double[2];
        //split(x, xs);
        //xs[1] = (double)(2.7182818284590452353602874713526625L - xs[0]);
        //xs[0] = 2.71827697753906250000;
        //xs[1] = 4.85091998273542816811e-06;
        //xs[0] = Double.longBitsToDouble(0x4005bf0800000000L);
        //xs[1] = Double.longBitsToDouble(0x3ed458a2bb4a9b00L);

        /* E */
        xs[0] = 2.718281828459045;
        xs[1] = 1.4456468917292502E-16;

        split(1.0, ys);

        while (p > 0) {
            if ((p & 1) != 0) {
                quadMult(ys, xs, as);
                ys[0] = as[0]; ys[1] = as[1];
            }

            quadMult(xs, xs, as);
            xs[0] = as[0]; xs[1] = as[1];

            p >>= 1;
        }

        if (result != null) {
            result[0] = ys[0];
            result[1] = ys[1];

            resplit(result);
        }

        return ys[0] + ys[1];
    }
}
