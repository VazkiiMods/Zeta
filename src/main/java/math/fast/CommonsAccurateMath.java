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

/**
 * Faster, more accurate, portable alternative to {@link Math} and
 * {@link StrictMath} for large scale computation.
 * <p>
 * CommonsAccurateMath is a drop-in replacement for some methods from Math and
 * StrictMath. This means that for any method in Math (say {@code Math.exp(x)}
 * or {@code Math.cbrt(y)}) that is defined in CommonsAccurateMath, user can
 * directly change the class and use the methods as is (using
 * {@code CommonsAccurateMath.exp(x)} or {@code CommonsAccurateMath.cbrt(y)} in
 * the previous example).
 * </p>
 * <p>
 * CommonsAccurateMath speed is achieved by relying heavily on optimizing
 * compilers to native code present in many JVMs today and use of large tables.
 * The larger tables are lazily initialised on first use, so that the setup time
 * does not penalise methods that don't need them.
 * </p>
 * <p>
 * CommonsAccurateMath accuracy should be mostly independent of the JVM as it
 * relies only on IEEE-754 basic operations and on embedded tables. Almost all
 * operations are accurate to about 0.5 ulp throughout the domain range. This
 * statement, of course is only a rough global observed behavior, it is
 * <em>not</em> a guarantee for <em>every</em> double numbers input (see William
 * Kahan's <a
 * href="http://en.wikipedia.org/wiki/Rounding#The_table-maker.27s_dilemma"
 * >Table Maker's Dilemma</a>).
 * </p>
 * 
 * @version $Id: FastMath.java 1462503 2013-03-29 15:48:27Z luc $
 * @since 2.2
 */
final class CommonsAccurateMath {
    /*
     * There are 52 bits in the mantissa of a double. For additional precision,
     * the code splits double numbers into two parts, by clearing the low order
     * 30 bits if possible, and then performs the arithmetic on each half
     * separately.
     */

    /**
     * 0x40000000 - used to split a double into two parts, both with the low
     * order bits cleared. Equivalent to 2^30.
     */
    private static final long HEX_40000000 = 0x40000000L; // 1073741824L

    private static final boolean RECOMPUTE_TABLES_AT_RUNTIME = false;

    /** Index of exp(0) in the array of integer exponentials. */
    static final int EXP_INT_TABLE_MAX_INDEX = 750;

    /** Length of the array of integer exponentials. */
    static final int EXP_INT_TABLE_LEN = EXP_INT_TABLE_MAX_INDEX * 2;
    /** Logarithm table length. */
    static final int LN_MANT_LEN = 1024;

    /** Exponential fractions table length. */
    static final int EXP_FRAC_TABLE_LEN = 1025; // 0, 1/1024, ... 1024/1024

    /** StrictMath.log(Double.MAX_VALUE): {@value} */
    private static final double LOG_MAX_VALUE = StrictMath
            .log(Double.MAX_VALUE);

    /**
     * Enclose large data table in nested static class so it's only loaded on
     * first access.
     */
    private static final class ExpIntTable {
        /**
         * Exponential evaluated at integer values, exp(x) = expIntTableA[x +
         * EXP_INT_TABLE_MAX_INDEX] + expIntTableB[x+EXP_INT_TABLE_MAX_INDEX].
         */
        static final double[] EXP_INT_TABLE_A;
        /**
         * Exponential evaluated at integer values, exp(x) = expIntTableA[x +
         * EXP_INT_TABLE_MAX_INDEX] + expIntTableB[x+EXP_INT_TABLE_MAX_INDEX]
         */
        static final double[] EXP_INT_TABLE_B;

        static {
            if (RECOMPUTE_TABLES_AT_RUNTIME) {
                EXP_INT_TABLE_A = new double[CommonsAccurateMath.EXP_INT_TABLE_LEN];
                EXP_INT_TABLE_B = new double[CommonsAccurateMath.EXP_INT_TABLE_LEN];

                final double tmp[] = new double[2];
                final double recip[] = new double[2];

                // Populate expIntTable
                for (int i = 0; i < CommonsAccurateMath.EXP_INT_TABLE_MAX_INDEX; i++) {
                    CommonsCalc.expint(i, tmp);
                    EXP_INT_TABLE_A[i
                            + CommonsAccurateMath.EXP_INT_TABLE_MAX_INDEX] = tmp[0];
                    EXP_INT_TABLE_B[i
                            + CommonsAccurateMath.EXP_INT_TABLE_MAX_INDEX] = tmp[1];

                    if (i != 0) {
                        // Negative integer powers
                        CommonsCalc.splitReciprocal(tmp, recip);
                        EXP_INT_TABLE_A[CommonsAccurateMath.EXP_INT_TABLE_MAX_INDEX
                                - i] = recip[0];
                        EXP_INT_TABLE_B[CommonsAccurateMath.EXP_INT_TABLE_MAX_INDEX
                                - i] = recip[1];
                    }
                }
            } else {
                EXP_INT_TABLE_A = CommonsMathLiterals.loadExpIntA();
                EXP_INT_TABLE_B = CommonsMathLiterals.loadExpIntB();
            }
        }
    }

    /**
     * Enclose large data table in nested static class so it's only loaded on
     * first access.
     */
    private static final class ExpFracTable {
        /**
         * Exponential over the range of 0 - 1 in increments of 2^-10
         * exp(x/1024) = expFracTableA[x] + expFracTableB[x]. 1024 = 2^10
         */
        static final double[] EXP_FRAC_TABLE_A;
        /**
         * Exponential over the range of 0 - 1 in increments of 2^-10
         * exp(x/1024) = expFracTableA[x] + expFracTableB[x].
         */
        static final double[] EXP_FRAC_TABLE_B;

        static {
            if (RECOMPUTE_TABLES_AT_RUNTIME) {
                EXP_FRAC_TABLE_A = new double[CommonsAccurateMath.EXP_FRAC_TABLE_LEN];
                EXP_FRAC_TABLE_B = new double[CommonsAccurateMath.EXP_FRAC_TABLE_LEN];

                final double tmp[] = new double[2];

                // Populate expFracTable
                final double factor = 1d / (EXP_FRAC_TABLE_LEN - 1);
                for (int i = 0; i < EXP_FRAC_TABLE_A.length; i++) {
                    CommonsCalc.slowexp(i * factor, tmp);
                    EXP_FRAC_TABLE_A[i] = tmp[0];
                    EXP_FRAC_TABLE_B[i] = tmp[1];
                }
            } else {
                EXP_FRAC_TABLE_A = CommonsMathLiterals.loadExpFracA();
                EXP_FRAC_TABLE_B = CommonsMathLiterals.loadExpFracB();
            }
        }
    }

    /**
     * Internal helper method for exponential function.
     * 
     * @param x
     *            original argument of the exponential function
     * @param extra
     *            extra bits of precision on input (To Be Confirmed)
     * @param hiPrec
     *            extra bits of precision on output (To Be Confirmed)
     * @return exp(x)
     */
    private static double exp_(double x, double extra, double[] hiPrec) {
        double intPartA;
        double intPartB;
        int intVal;

        /*
         * Lookup exp(floor(x)). intPartA will have the upper 22 bits, intPartB
         * will have the lower 52 bits.
         */
        if (x < 0.0) {
            intVal = (int) -x;

            if (intVal > 746) {
                if (hiPrec != null) {
                    hiPrec[0] = 0.0;
                    hiPrec[1] = 0.0;
                }
                return 0.0;
            }

            if (intVal > 709) {
                /* This will produce a subnormal output */
                final double result = exp_(x + 40.19140625, extra, hiPrec) / 285040095144011776.0;
                if (hiPrec != null) {
                    hiPrec[0] /= 285040095144011776.0;
                    hiPrec[1] /= 285040095144011776.0;
                }
                return result;
            }

            if (intVal == 709) {
                /* exp(1.494140625) is nearly a machine number... */
                final double result = exp_(x + 1.494140625, extra, hiPrec) / 4.455505956692756620;
                if (hiPrec != null) {
                    hiPrec[0] /= 4.455505956692756620;
                    hiPrec[1] /= 4.455505956692756620;
                }
                return result;
            }

            intVal++;

            intPartA = ExpIntTable.EXP_INT_TABLE_A[EXP_INT_TABLE_MAX_INDEX
                    - intVal];
            intPartB = ExpIntTable.EXP_INT_TABLE_B[EXP_INT_TABLE_MAX_INDEX
                    - intVal];

            intVal = -intVal;
        } else {
            intVal = (int) x;

            if (intVal > 709) {
                if (hiPrec != null) {
                    hiPrec[0] = Double.POSITIVE_INFINITY;
                    hiPrec[1] = 0.0;
                }
                return Double.POSITIVE_INFINITY;
            }

            intPartA = ExpIntTable.EXP_INT_TABLE_A[EXP_INT_TABLE_MAX_INDEX
                    + intVal];
            intPartB = ExpIntTable.EXP_INT_TABLE_B[EXP_INT_TABLE_MAX_INDEX
                    + intVal];
        }

        /*
         * Get the fractional part of x, find the greatest multiple of 2^-10
         * less than x and look up the exp function of it. fracPartA will have
         * the upper 22 bits, fracPartB the lower 52 bits.
         */
        final int intFrac = (int) ((x - intVal) * 1024.0);
        final double fracPartA = ExpFracTable.EXP_FRAC_TABLE_A[intFrac];
        final double fracPartB = ExpFracTable.EXP_FRAC_TABLE_B[intFrac];

        /*
         * epsilon is the difference in x from the nearest multiple of 2^-10. It
         * has a value in the range 0 <= epsilon < 2^-10. Do the subtraction
         * from x as the last step to avoid possible loss of precision.
         */
        final double epsilon = x - (intVal + intFrac / 1024.0);

        /*
         * Compute z = exp(epsilon) - 1.0 via a minimax polynomial. z has full
         * double precision (52 bits). Since z < 2^-10, we will have 62 bits of
         * precision when combined with the constant 1. This will be used in the
         * last addition below to get proper rounding.
         */

        /*
         * Remez generated polynomial. Converges on the interval [0, 2^-10],
         * error is less than 0.5 ULP
         */
        double z = 0.04168701738764507;
        z = z * epsilon + 0.1666666505023083;
        z = z * epsilon + 0.5000000000042687;
        z = z * epsilon + 1.0;
        z = z * epsilon + -3.940510424527919E-20;

        /*
         * Compute (intPartA+intPartB) * (fracPartA+fracPartB) by binomial
         * expansion. tempA is exact since intPartA and intPartB only have 22
         * bits each. tempB will have 52 bits of precision.
         */
        double tempA = intPartA * fracPartA;
        double tempB = intPartA * fracPartB + intPartB * fracPartA + intPartB
                * fracPartB;

        /*
         * Compute the result. (1+z)(tempA+tempB). Order of operations is
         * important. For accuracy add by increasing size. tempA is exact and
         * much larger than the others. If there are extra bits specified from
         * the pow() function, use them.
         */
        final double tempC = tempB + tempA;
        final double result;
        if (extra != 0.0) {
            result = tempC * extra * z + tempC * extra + tempC * z + tempB
                    + tempA;
        } else {
            result = tempC * z + tempB + tempA;
        }

        if (hiPrec != null) {
            // If requesting high precision
            hiPrec[0] = tempA;
            hiPrec[1] = tempC * extra * z + tempC * extra + tempC * z + tempB;
        }

        return result;
    }

    /**
     * Compute exp(x) - 1
     * 
     * @param x
     *            number to compute shifted exponential
     * @return exp(x) - 1
     */
    static double expm1(double x) {
        return expm1_(x, null);
    }

    /**
     * Internal helper method for expm1
     * 
     * @param x
     *            number to compute shifted exponential
     * @param hiPrecOut
     *            receive high precision result for -1.0 < x < 1.0
     * @return exp(x) - 1
     */
    private static double expm1_(double x, double hiPrecOut[]) {
        if (Double.isNaN(x) || x == 0.0) { // NaN or zero
            return x;
        }

        if (x <= -1.0 || x >= 1.0) {
            // If not between +/- 1.0
            // return exp(x) - 1.0;
            double hiPrec[] = new double[2];
            exp_(x, 0.0, hiPrec);
            if (x > 0.0) {
                return -1.0 + hiPrec[0] + hiPrec[1];
            } else {
                final double ra = -1.0 + hiPrec[0];
                double rb = -(ra + 1.0 - hiPrec[0]);
                rb += hiPrec[1];
                return ra + rb;
            }
        }

        double baseA;
        double baseB;
        double epsilon;
        boolean negative = false;

        if (x < 0.0) {
            x = -x;
            negative = true;
        }

        {
            int intFrac = (int) (x * 1024.0);
            double tempA = ExpFracTable.EXP_FRAC_TABLE_A[intFrac] - 1.0;
            double tempB = ExpFracTable.EXP_FRAC_TABLE_B[intFrac];

            double temp = tempA + tempB;
            tempB = -(temp - tempA - tempB);
            tempA = temp;

            temp = tempA * HEX_40000000;
            baseA = tempA + temp - temp;
            baseB = tempB + (tempA - baseA);

            epsilon = x - intFrac / 1024.0;
        }

        /* Compute expm1(epsilon) */
        double zb = 0.008336750013465571;
        zb = zb * epsilon + 0.041666663879186654;
        zb = zb * epsilon + 0.16666666666745392;
        zb = zb * epsilon + 0.49999999999999994;
        zb = zb * epsilon;
        zb = zb * epsilon;

        double za = epsilon;
        double temp = za + zb;
        zb = -(temp - za - zb);
        za = temp;

        temp = za * HEX_40000000;
        temp = za + temp - temp;
        zb += za - temp;
        za = temp;

        /*
         * Combine the parts. expm1(a+b) = expm1(a) + expm1(b) +
         * expm1(a)*expm1(b)
         */
        double ya = za * baseA;
        // double yb = za*baseB + zb*baseA + zb*baseB;
        temp = ya + za * baseB;
        double yb = -(temp - ya - za * baseB);
        ya = temp;

        temp = ya + zb * baseA;
        yb += -(temp - ya - zb * baseA);
        ya = temp;

        temp = ya + zb * baseB;
        yb += -(temp - ya - zb * baseB);
        ya = temp;

        // ya = ya + za + baseA;
        // yb = yb + zb + baseB;
        temp = ya + baseA;
        yb += -(temp - baseA - ya);
        ya = temp;

        temp = ya + za;
        // yb += (ya > za) ? -(temp - ya - za) : -(temp - za - ya);
        yb += -(temp - ya - za);
        ya = temp;

        temp = ya + baseB;
        // yb += (ya > baseB) ? -(temp - ya - baseB) : -(temp - baseB - ya);
        yb += -(temp - ya - baseB);
        ya = temp;

        temp = ya + zb;
        // yb += (ya > zb) ? -(temp - ya - zb) : -(temp - zb - ya);
        yb += -(temp - ya - zb);
        ya = temp;

        if (negative) {
            /* Compute expm1(-x) = -expm1(x) / (expm1(x) + 1) */
            double denom = 1.0 + ya;
            double denomr = 1.0 / denom;
            double denomb = -(denom - 1.0 - ya) + yb;
            double ratio = ya * denomr;
            temp = ratio * HEX_40000000;
            final double ra = ratio + temp - temp;
            double rb = ratio - ra;

            temp = denom * HEX_40000000;
            za = denom + temp - temp;
            zb = denom - za;

            rb += (ya - za * ra - za * rb - zb * ra - zb * rb) * denomr;

            // f(x) = x/1+x
            // Compute f'(x)
            // Product rule: d(uv) = du*v + u*dv
            // Chain rule: d(f(g(x)) = f'(g(x))*f(g'(x))
            // d(1/x) = -1/(x*x)
            // d(1/1+x) = -1/( (1+x)^2) * 1 = -1/((1+x)*(1+x))
            // d(x/1+x) = -x/((1+x)(1+x)) + 1/1+x = 1 / ((1+x)(1+x))

            // Adjust for yb
            rb += yb * denomr; // numerator
            rb += -ya * denomb * denomr * denomr; // denominator

            // negate
            ya = -ra;
            yb = -rb;
        }

        if (hiPrecOut != null) {
            hiPrecOut[0] = ya;
            hiPrecOut[1] = yb;
        }

        return ya + yb;
    }

    /**
     * Compute the hyperbolic cosine of a number.
     * 
     * @param x
     *            number on which evaluation is done
     * @return hyperbolic cosine of x
     */
    static double cosh(double x) {
        if (Double.isNaN(x)) {
            return x;
        }

        // cosh[z] = (exp(z) + exp(-z))/2

        // for numbers with magnitude 20 or so,
        // exp(-z) can be ignored in comparison with exp(z)

        if (x > 20) {
            if (x >= LOG_MAX_VALUE) {
                // Avoid overflow (MATH-905).
                final double t = Math.exp(0.5 * x);
                return (0.5 * t) * t;
            } else {
                return 0.5 * Math.exp(x);
            }
        } else if (x < -20) {
            if (x <= -LOG_MAX_VALUE) {
                // Avoid overflow (MATH-905).
                final double t = Math.exp(-0.5 * x);
                return (0.5 * t) * t;
            } else {
                return 0.5 * Math.exp(-x);
            }
        }

        final double hiPrec[] = new double[2];
        if (x < 0.0) {
            x = -x;
        }
        exp_(x, 0.0, hiPrec);

        double ya = hiPrec[0] + hiPrec[1];
        double yb = -(ya - hiPrec[0] - hiPrec[1]);

        double temp = ya * HEX_40000000;
        double yaa = ya + temp - temp;
        double yab = ya - yaa;

        // recip = 1/y
        double recip = 1.0 / ya;
        temp = recip * HEX_40000000;
        double recipa = recip + temp - temp;
        double recipb = recip - recipa;

        // Correct for rounding in division
        recipb += (1.0 - yaa * recipa - yaa * recipb - yab * recipa - yab
                * recipb)
                * recip;
        // Account for yb
        recipb += -yb * recip * recip;

        // y = y + 1/y
        temp = ya + recipa;
        yb += -(temp - ya - recipa);
        ya = temp;
        temp = ya + recipb;
        yb += -(temp - ya - recipb);
        ya = temp;

        double result = ya + yb;
        result *= 0.5;
        return result;
    }

    /**
     * Compute the hyperbolic sine of a number.
     * 
     * @param x
     *            number on which evaluation is done
     * @return hyperbolic sine of x
     */
    static double sinh(double x) {
        boolean negate = false;
        if (Double.isNaN(x)) {
            return x;
        }

        // sinh[z] = (exp(z) - exp(-z) / 2

        // for values of z larger than about 20,
        // exp(-z) can be ignored in comparison with exp(z)

        if (x > 20) {
            if (x >= LOG_MAX_VALUE) {
                // Avoid overflow (MATH-905).
                final double t = Math.exp(0.5 * x);
                return (0.5 * t) * t;
            } else {
                return 0.5 * Math.exp(x);
            }
        } else if (x < -20) {
            if (x <= -LOG_MAX_VALUE) {
                // Avoid overflow (MATH-905).
                final double t = Math.exp(-0.5 * x);
                return (-0.5 * t) * t;
            } else {
                return -0.5 * Math.exp(-x);
            }
        }

        if (x == 0) {
            return x;
        }

        if (x < 0.0) {
            x = -x;
            negate = true;
        }

        double result;

        if (x > 0.25) {
            double hiPrec[] = new double[2];
            exp_(x, 0.0, hiPrec);

            double ya = hiPrec[0] + hiPrec[1];
            double yb = -(ya - hiPrec[0] - hiPrec[1]);

            double temp = ya * HEX_40000000;
            double yaa = ya + temp - temp;
            double yab = ya - yaa;

            // recip = 1/y
            double recip = 1.0 / ya;
            temp = recip * HEX_40000000;
            double recipa = recip + temp - temp;
            double recipb = recip - recipa;

            // Correct for rounding in division
            recipb += (1.0 - yaa * recipa - yaa * recipb - yab * recipa - yab
                    * recipb)
                    * recip;
            // Account for yb
            recipb += -yb * recip * recip;

            recipa = -recipa;
            recipb = -recipb;

            // y = y + 1/y
            temp = ya + recipa;
            yb += -(temp - ya - recipa);
            ya = temp;
            temp = ya + recipb;
            yb += -(temp - ya - recipb);
            ya = temp;

            result = ya + yb;
            result *= 0.5;
        } else {
            double hiPrec[] = new double[2];
            expm1_(x, hiPrec);

            double ya = hiPrec[0] + hiPrec[1];
            double yb = -(ya - hiPrec[0] - hiPrec[1]);

            /* Compute expm1(-x) = -expm1(x) / (expm1(x) + 1) */
            double denom = 1.0 + ya;
            double denomr = 1.0 / denom;
            double denomb = -(denom - 1.0 - ya) + yb;
            double ratio = ya * denomr;
            double temp = ratio * HEX_40000000;
            double ra = ratio + temp - temp;
            double rb = ratio - ra;

            temp = denom * HEX_40000000;
            double za = denom + temp - temp;
            double zb = denom - za;

            rb += (ya - za * ra - za * rb - zb * ra - zb * rb) * denomr;

            // Adjust for yb
            rb += yb * denomr; // numerator
            rb += -ya * denomb * denomr * denomr; // denominator

            // y = y - 1/y
            temp = ya + ra;
            yb += -(temp - ya - ra);
            ya = temp;
            temp = ya + rb;
            yb += -(temp - ya - rb);
            ya = temp;

            result = ya + yb;
            result *= 0.5;
        }

        if (negate) {
            result = -result;
        }

        return result;
    }

    /**
     * Compute the hyperbolic tangent of a number.
     * 
     * @param x
     *            number on which evaluation is done
     * @return hyperbolic tangent of x
     */
    static double tanh(double x) {
        boolean negate = false;

        if (Double.isNaN(x)) {
            return x;
        }

        // tanh[z] = sinh[z] / cosh[z]
        // = (exp(z) - exp(-z)) / (exp(z) + exp(-z))
        // = (exp(2x) - 1) / (exp(2x) + 1)

        // for magnitude > 20, sinh[z] == cosh[z] in double precision

        if (x > 20.0) {
            return 1.0;
        }

        if (x < -20) {
            return -1.0;
        }

        if (x == 0) {
            return x;
        }

        if (x < 0.0) {
            x = -x;
            negate = true;
        }

        double result;
        if (x >= 0.5) {
            double hiPrec[] = new double[2];
            // tanh(x) = (exp(2x) - 1) / (exp(2x) + 1)
            exp_(x * 2.0, 0.0, hiPrec);

            double ya = hiPrec[0] + hiPrec[1];
            double yb = -(ya - hiPrec[0] - hiPrec[1]);

            /* Numerator */
            double na = -1.0 + ya;
            double nb = -(na + 1.0 - ya);
            double temp = na + yb;
            nb += -(temp - na - yb);
            na = temp;

            /* Denominator */
            double da = 1.0 + ya;
            double db = -(da - 1.0 - ya);
            temp = da + yb;
            db += -(temp - da - yb);
            da = temp;

            temp = da * HEX_40000000;
            double daa = da + temp - temp;
            double dab = da - daa;

            // ratio = na/da
            double ratio = na / da;
            temp = ratio * HEX_40000000;
            double ratioa = ratio + temp - temp;
            double ratiob = ratio - ratioa;

            // Correct for rounding in division
            ratiob += (na - daa * ratioa - daa * ratiob - dab * ratioa - dab
                    * ratiob)
                    / da;

            // Account for nb
            ratiob += nb / da;
            // Account for db
            ratiob += -db * na / da / da;

            result = ratioa + ratiob;
        } else {
            double hiPrec[] = new double[2];
            // tanh(x) = expm1(2x) / (expm1(2x) + 2)
            expm1_(x * 2.0, hiPrec);

            double ya = hiPrec[0] + hiPrec[1];
            double yb = -(ya - hiPrec[0] - hiPrec[1]);

            /* Numerator */
            double na = ya;
            double nb = yb;

            /* Denominator */
            double da = 2.0 + ya;
            double db = -(da - 2.0 - ya);
            double temp = da + yb;
            db += -(temp - da - yb);
            da = temp;

            temp = da * HEX_40000000;
            double daa = da + temp - temp;
            double dab = da - daa;

            // ratio = na/da
            double ratio = na / da;
            temp = ratio * HEX_40000000;
            double ratioa = ratio + temp - temp;
            double ratiob = ratio - ratioa;

            // Correct for rounding in division
            ratiob += (na - daa * ratioa - daa * ratiob - dab * ratioa - dab
                    * ratiob)
                    / da;

            // Account for nb
            ratiob += nb / da;
            // Account for db
            ratiob += -db * na / da / da;

            result = ratioa + ratiob;
        }

        if (negate) {
            result = -result;
        }

        return result;
    }
}
