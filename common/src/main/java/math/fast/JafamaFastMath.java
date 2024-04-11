/*
 * Copyright 2012-2013 Jeff Hain
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * =============================================================================
 * Notice of fdlibm package this program is partially derived from:
 *
 * Copyright (C) 1993 by Sun Microsystems, Inc. All rights reserved.
 *
 * Developed at SunSoft, a Sun Microsystems, Inc. business.
 * Permission to use, copy, modify, and distribute this
 * software is freely granted, provided that this notice
 * is preserved.
 * =============================================================================
 */
package math.fast;

/**
 * Class providing math treatments that:
 * - are meant to be faster than java.lang.Math class equivalents,
 * - are still somehow accurate and robust (handling of NaN and such),
 * - do not (or not directly) generate objects at run time (no "new").
 *
 * Use of look-up tables: around 1 MiB total, and initialized on class load.
 *
 * Depending on JVM, or JVM options, these treatments can actually be slower
 * than Math ones.
 * In particular, they can be slower if not optimized by the JIT, which you
 * can see with -Xint JVM option.
 * Another cause of slowness can be cache-misses on look-up tables.
 * Also, look-up tables initialization, done on class load, typically
 * takes multiple hundreds of milliseconds (and is about twice slower
 * in J6 than in J5, and in J7 than in J6, possibly due to intrinsifications
 * preventing optimizations such as use of hardware sqrt, and Math delegating
 * to StrictMath with JIT optimizations not yet up during class load).
 *
 * These treatments are not strictfp: if you want identical results
 * across various architectures, you must roll your own implementation
 * by adding strictfp and using StrictMath instead of Math.
 *
 * Methods with same signature than Math ones, are meant to return
 * "good" approximations on all range.
 *
 * --- words, words, words ---
 *
 * "0x42BE0000 percents of the folks out there
 * are completely clueless about floating-point."
 *
 * The difference between precision and accuracy:
 * "3.177777777777777 is a precise (16 digits)
 * but inaccurate (only correct up to the second digit)
 * approximation of PI=3.141592653589793(etc.)."
 */
final class JafamaFastMath {

    /*
     * For trigonometric functions, use of look-up tables and Taylor-Lagrange formula
     * with 4 derivatives (more take longer to compute and don't add much accuracy,
     * less require larger tables (which use more memory, take more time to initialize,
     * and are slower to access (at least on the machine they were developed on))).
     *
     * For angles reduction of cos/sin/tan functions:
     * - for small values, instead of reducing angles, and then computing the best index
     *   for look-up tables, we compute this index right away, and use it for reduction,
     * - for large values, treatments derived from fdlibm package are used, as done in
     *   java.lang.Math. They are faster but still "slow", so if you work with
     *   large numbers and need speed over accuracy for them, you might want to use
     *   normalizeXXXFast treatments before your function, or modify cos/sin/tan
     *   so that they call the fast normalization treatments instead of the accurate ones.
     *   NB: If an angle is huge (like PI*1e20), in double precision format its last digits
     *       are zeros, which most likely is not the case for the intended value, and doing
     *       an accurate reduction on a very inaccurate value is most likely pointless.
     *       But it gives some sort of coherence that could be needed in some cases.
     *
     * Multiplication on double appears to be about as fast (or not much slower) than call
     * to <double_array>[<index>], and regrouping some doubles in a private class, to use
     * index only once, does not seem to speed things up, so:
     * - for uniformly tabulated values, to retrieve the parameter corresponding to
     *   an index, we recompute it rather than using an array to store it,
     * - for cos/sin, we recompute derivatives divided by (multiplied by inverse of)
     *   factorial each time, rather than storing them in arrays.
     *
     * Lengths of look-up tables are usually of the form 2^n+1, for their values to be
     * of the form (<a_constant> * k/2^n, k in 0 .. 2^n), so that particular values
     * (PI/2, etc.) are "exactly" computed, as well as for other reasons.
     *
     * Most math treatments I could find on the web, including "fast" ones,
     * usually take care of special cases (NaN, etc.) at the beginning, and
     * then deal with the general case, which adds a useless overhead for the
     * general (and common) case. In this class, special cases are only dealt
     * with when needed, and if the general case does not already handle them.
     */

    //--------------------------------------------------------------------------
    // CONFIGURATION
    //--------------------------------------------------------------------------

    /**
     * Using two pow tab can just make things barely faster,
     * but could relatively hurt in case of cache-misses,
     * especially for methods that otherwise wouldn't rely
     * on any tab, so we don't use it.
     */
    private static final boolean USE_TWO_POW_TAB = false;

    //--------------------------------------------------------------------------
    // GENERAL CONSTANTS
    //--------------------------------------------------------------------------

    /**
     * High approximation of PI, which is further from PI
     * than the low approximation Math.PI:
     *              PI ~= 3.14159265358979323846...
     *         Math.PI ~= 3.141592653589793
     * JafamaFastMath.PI_SUP ~= 3.1415926535897936
     */
    public static final double PI_SUP = Double.longBitsToDouble(Double.doubleToRawLongBits(Math.PI)+1);

    private static final double ONE_DIV_F2 = 1/2.0;
    private static final double ONE_DIV_F3 = 1/6.0;
    private static final double ONE_DIV_F4 = 1/24.0;

    private static final double TWO_POW_24 = twoPow(24);
    private static final double TWO_POW_N24 = twoPow(-24);

    private static final double TWO_POW_66 = twoPow(66);

    private static final double TWO_POW_450 = twoPow(450);
    private static final double TWO_POW_N450 = twoPow(-450);

    private static final double TWO_POW_750 = twoPow(750);
    private static final double TWO_POW_N750 = twoPow(-750);

    // Not storing float/double mantissa size in constants,
    // for 23 and 52 are shorter to read and more
    // bitwise-explicit than some constant's name.

    private static final int MIN_DOUBLE_EXPONENT = -1074;
    private static final int MAX_DOUBLE_EXPONENT = 1023;

    //--------------------------------------------------------------------------
    // CONSTANTS FOR NORMALIZATIONS
    //--------------------------------------------------------------------------

    /*
     * Table of constants for 1/(2*PI), 282 Hex digits (enough for normalizing doubles).
     * 1/(2*PI) approximation = sum of ONE_OVER_TWOPI_TAB[i]*2^(-24*(i+1)).
     */
    private static final double ONE_OVER_TWOPI_TAB[] = {
        0x28BE60, 0xDB9391, 0x054A7F, 0x09D5F4, 0x7D4D37, 0x7036D8,
        0xA5664F, 0x10E410, 0x7F9458, 0xEAF7AE, 0xF1586D, 0xC91B8E,
        0x909374, 0xB80192, 0x4BBA82, 0x746487, 0x3F877A, 0xC72C4A,
        0x69CFBA, 0x208D7D, 0x4BAED1, 0x213A67, 0x1C09AD, 0x17DF90,
        0x4E6475, 0x8E60D4, 0xCE7D27, 0x2117E2, 0xEF7E4A, 0x0EC7FE,
        0x25FFF7, 0x816603, 0xFBCBC4, 0x62D682, 0x9B47DB, 0x4D9FB3,
        0xC9F2C2, 0x6DD3D1, 0x8FD9A7, 0x97FA8B, 0x5D49EE, 0xB1FAF9,
        0x7C5ECF, 0x41CE7D, 0xE294A4, 0xBA9AFE, 0xD7EC47};

    /*
     * Constants for 2*PI. Only the 23 most significant bits of each mantissa are used.
     * 2*PI approximation = sum of TWOPI_TAB<i>.
     */
    private static final double TWOPI_TAB0 = Double.longBitsToDouble(0x401921FB40000000L);
    private static final double TWOPI_TAB1 = Double.longBitsToDouble(0x3E94442D00000000L);
    private static final double TWOPI_TAB2 = Double.longBitsToDouble(0x3D18469880000000L);
    private static final double TWOPI_TAB3 = Double.longBitsToDouble(0x3B98CC5160000000L);
    private static final double TWOPI_TAB4 = Double.longBitsToDouble(0x3A101B8380000000L);

    private static final double INVPIO2 = Double.longBitsToDouble(0x3FE45F306DC9C883L); // 6.36619772367581382433e-01 53 bits of 2/pi
    private static final double PIO2_HI = Double.longBitsToDouble(0x3FF921FB54400000L); // 1.57079632673412561417e+00 first 33 bits of pi/2
    private static final double PIO2_LO = Double.longBitsToDouble(0x3DD0B4611A626331L); // 6.07710050650619224932e-11 pi/2 - PIO2_HI
    private static final double INVTWOPI = INVPIO2/4;
    private static final double TWOPI_HI = 4*PIO2_HI;
    private static final double TWOPI_LO = 4*PIO2_LO;

    // fdlibm uses 2^19*PI/2 here, but we normalize with % 2*PI instead of % PI/2,
    // and we can bear some more error.
    private static final double NORMALIZE_ANGLE_MAX_MEDIUM_DOUBLE = StrictMath.pow(2,20)*(2*Math.PI);

    //--------------------------------------------------------------------------
    // CONSTANTS AND TABLES FOR SIN AND COS
    //--------------------------------------------------------------------------

    private static final int SIN_COS_TABS_SIZE = (1<<getTabSizePower(11)) + 1;
    private static final double SIN_COS_DELTA_HI = TWOPI_HI/(SIN_COS_TABS_SIZE-1);
    private static final double SIN_COS_DELTA_LO = TWOPI_LO/(SIN_COS_TABS_SIZE-1);
    private static final double SIN_COS_INDEXER = 1/(SIN_COS_DELTA_HI+SIN_COS_DELTA_LO);
    private static final double[] sinTab = new double[SIN_COS_TABS_SIZE];
    private static final double[] cosTab = new double[SIN_COS_TABS_SIZE];

    // Max abs value for fast modulo, above which we use regular angle normalization.
    // This value must be < (Integer.MAX_VALUE / SIN_COS_INDEXER), to stay in range of int type.
    // The higher it is, the higher the error, but also the faster it is for lower values.
    // If you set it to ((Integer.MAX_VALUE / SIN_COS_INDEXER) * 0.99), worse accuracy on double range is about 1e-10.
    private static final double SIN_COS_MAX_VALUE_FOR_INT_MODULO = ((Integer.MAX_VALUE>>9) / SIN_COS_INDEXER) * 0.99;

    //--------------------------------------------------------------------------
    // CONSTANTS AND TABLES FOR ACOS, ASIN
    //--------------------------------------------------------------------------

    // We use the following formula:
    // 1) acos(x) = PI/2 - asin(x)
    // 2) asin(-x) = -asin(x)
    // ---> we only have to compute asin(x) on [0,1].
    // For values not close to +-1, we use look-up tables;
    // for values near +-1, we use code derived from fdlibm.

    // Supposed to be >= sin(77.2deg), as fdlibm code is supposed to work with values > 0.975,
    // but seems to work well enough as long as value >= sin(25deg).
    private static final double ASIN_MAX_VALUE_FOR_TABS = StrictMath.sin(Math.toRadians(73.0));

    private static final int ASIN_TABS_SIZE = (1<<getTabSizePower(13)) + 1;
    private static final double ASIN_DELTA = ASIN_MAX_VALUE_FOR_TABS/(ASIN_TABS_SIZE - 1);
    private static final double ASIN_INDEXER = 1/ASIN_DELTA;
    private static final double[] asinTab = new double[ASIN_TABS_SIZE];
    private static final double[] asinDer1DivF1Tab = new double[ASIN_TABS_SIZE];
    private static final double[] asinDer2DivF2Tab = new double[ASIN_TABS_SIZE];
    private static final double[] asinDer3DivF3Tab = new double[ASIN_TABS_SIZE];
    private static final double[] asinDer4DivF4Tab = new double[ASIN_TABS_SIZE];

    private static final double ASIN_PIO2_HI = Double.longBitsToDouble(0x3FF921FB54442D18L); // 1.57079632679489655800e+00
    private static final double ASIN_PIO2_LO = Double.longBitsToDouble(0x3C91A62633145C07L); // 6.12323399573676603587e-17
    private static final double ASIN_PS0 = Double.longBitsToDouble(0x3fc5555555555555L); //  1.66666666666666657415e-01
    private static final double ASIN_PS1 = Double.longBitsToDouble(0xbfd4d61203eb6f7dL); // -3.25565818622400915405e-01
    private static final double ASIN_PS2 = Double.longBitsToDouble(0x3fc9c1550e884455L); //  2.01212532134862925881e-01
    private static final double ASIN_PS3 = Double.longBitsToDouble(0xbfa48228b5688f3bL); // -4.00555345006794114027e-02
    private static final double ASIN_PS4 = Double.longBitsToDouble(0x3f49efe07501b288L); //  7.91534994289814532176e-04
    private static final double ASIN_PS5 = Double.longBitsToDouble(0x3f023de10dfdf709L); //  3.47933107596021167570e-05
    private static final double ASIN_QS1 = Double.longBitsToDouble(0xc0033a271c8a2d4bL); // -2.40339491173441421878e+00
    private static final double ASIN_QS2 = Double.longBitsToDouble(0x40002ae59c598ac8L); //  2.02094576023350569471e+00
    private static final double ASIN_QS3 = Double.longBitsToDouble(0xbfe6066c1b8d0159L); // -6.88283971605453293030e-01
    private static final double ASIN_QS4 = Double.longBitsToDouble(0x3fb3b8c5b12e9282L); //  7.70381505559019352791e-02

    //--------------------------------------------------------------------------
    // CONSTANTS AND TABLES FOR ATAN
    //--------------------------------------------------------------------------

    // We use the formula atan(-x) = -atan(x)
    // ---> we only have to compute atan(x) on [0,+infinity[.
    // For values corresponding to angles not close to +-PI/2, we use look-up tables;
    // for values corresponding to angles near +-PI/2, we use code derived from fdlibm.

    // Supposed to be >= tan(67.7deg), as fdlibm code is supposed to work with values > 2.4375.
    private static final double ATAN_MAX_VALUE_FOR_TABS = StrictMath.tan(Math.toRadians(74.0));

    private static final int ATAN_TABS_SIZE = (1<<getTabSizePower(12)) + 1;
    private static final double ATAN_DELTA = ATAN_MAX_VALUE_FOR_TABS/(ATAN_TABS_SIZE - 1);
    private static final double ATAN_INDEXER = 1/ATAN_DELTA;
    private static final double[] atanTab = new double[ATAN_TABS_SIZE];
    private static final double[] atanDer1DivF1Tab = new double[ATAN_TABS_SIZE];
    private static final double[] atanDer2DivF2Tab = new double[ATAN_TABS_SIZE];
    private static final double[] atanDer3DivF3Tab = new double[ATAN_TABS_SIZE];
    private static final double[] atanDer4DivF4Tab = new double[ATAN_TABS_SIZE];

    private static final double ATAN_HI3 = Double.longBitsToDouble(0x3ff921fb54442d18L); // 1.57079632679489655800e+00 atan(inf)hi
    private static final double ATAN_LO3 = Double.longBitsToDouble(0x3c91a62633145c07L); // 6.12323399573676603587e-17 atan(inf)lo
    private static final double ATAN_AT0 = Double.longBitsToDouble(0x3fd555555555550dL); //  3.33333333333329318027e-01
    private static final double ATAN_AT1 = Double.longBitsToDouble(0xbfc999999998ebc4L); // -1.99999999998764832476e-01
    private static final double ATAN_AT2 = Double.longBitsToDouble(0x3fc24924920083ffL); //  1.42857142725034663711e-01
    private static final double ATAN_AT3 = Double.longBitsToDouble(0xbfbc71c6fe231671L); // -1.11111104054623557880e-01
    private static final double ATAN_AT4 = Double.longBitsToDouble(0x3fb745cdc54c206eL); //  9.09088713343650656196e-02
    private static final double ATAN_AT5 = Double.longBitsToDouble(0xbfb3b0f2af749a6dL); // -7.69187620504482999495e-02
    private static final double ATAN_AT6 = Double.longBitsToDouble(0x3fb10d66a0d03d51L); //  6.66107313738753120669e-02
    private static final double ATAN_AT7 = Double.longBitsToDouble(0xbfadde2d52defd9aL); // -5.83357013379057348645e-02
    private static final double ATAN_AT8 = Double.longBitsToDouble(0x3fa97b4b24760debL); //  4.97687799461593236017e-02
    private static final double ATAN_AT9 = Double.longBitsToDouble(0xbfa2b4442c6a6c2fL); // -3.65315727442169155270e-02
    private static final double ATAN_AT10 = Double.longBitsToDouble(0x3f90ad3ae322da11L); // 1.62858201153657823623e-02

    //--------------------------------------------------------------------------
    // TABLE FOR POWERS OF TWO
    //--------------------------------------------------------------------------

    private static final double[] twoPowTab = (USE_TWO_POW_TAB ? new double[(MAX_DOUBLE_EXPONENT-MIN_DOUBLE_EXPONENT)+1] : null);

    //--------------------------------------------------------------------------
    // PUBLIC TREATMENTS
    //--------------------------------------------------------------------------

    /*
     * trigonometry
     */

    /**
     * @param angle Angle in radians.
     * @return Angle cosine.
     */
    static double cos(double angle) {
        angle = Math.abs(angle);
        if (angle > SIN_COS_MAX_VALUE_FOR_INT_MODULO) {
            // Faster than using normalizeZeroTwoPi.
            angle = remainderTwoPi(angle);
            if (angle < 0.0) {
                angle += 2*Math.PI;
            }
        }
        // index: possibly outside tables range.
        int index = (int)(angle * SIN_COS_INDEXER + 0.5);
        double delta = (angle - index * SIN_COS_DELTA_HI) - index * SIN_COS_DELTA_LO;
        // Making sure index is within tables range.
        // Last value of each table is the same than first, so we ignore it (tabs size minus one) for modulo.
        index &= (SIN_COS_TABS_SIZE-2); // index % (SIN_COS_TABS_SIZE-1)
        double indexCos = cosTab[index];
        double indexSin = sinTab[index];
        return indexCos + delta * (-indexSin + delta * (-indexCos * ONE_DIV_F2 + delta * (indexSin * ONE_DIV_F3 + delta * indexCos * ONE_DIV_F4)));
    }

    /**
     * @param value Value in [-1,1].
     * @return Value arcsine, in radians, in [-PI/2,PI/2].
     */
    static double asin(double value) {
        boolean negateResult;
        if (value < 0.0) {
            value = -value;
            negateResult = true;
        } else {
            negateResult = false;
        }
        if (value <= ASIN_MAX_VALUE_FOR_TABS) {
            int index = (int)(value * ASIN_INDEXER + 0.5);
            double delta = value - index * ASIN_DELTA;
            double result = asinTab[index] + delta * (asinDer1DivF1Tab[index] + delta * (asinDer2DivF2Tab[index] + delta * (asinDer3DivF3Tab[index] + delta * asinDer4DivF4Tab[index])));
            return negateResult ? -result : result;
        } else { // value > ASIN_MAX_VALUE_FOR_TABS, or value is NaN
            // This part is derived from fdlibm.
            if (value < 1.0) {
                double t = (1.0 - value)*0.5;
                double p = t*(ASIN_PS0+t*(ASIN_PS1+t*(ASIN_PS2+t*(ASIN_PS3+t*(ASIN_PS4+t*ASIN_PS5)))));
                double q = 1.0+t*(ASIN_QS1+t*(ASIN_QS2+t*(ASIN_QS3+t*ASIN_QS4)));
                double s = Math.sqrt(t);
                double z = s+s*(p/q);
                double result = ASIN_PIO2_HI-((z+z)-ASIN_PIO2_LO);
                return negateResult ? -result : result;
            } else { // value >= 1.0, or value is NaN
                if (value == 1.0) {
                    return negateResult ? -Math.PI/2 : Math.PI/2;
                } else {
                    return Double.NaN;
                }
            }
        }
    }

    /**
     * @param value Value in [-1,1].
     * @return Value arccosine, in radians, in [0,PI].
     */
    static double acos(double value) {
        return Math.PI/2 - JafamaFastMath.asin(value);
    }

    /**
     * @param value A double value.
     * @return Value arctangent, in radians, in [-PI/2,PI/2].
     */
    static double atan(double value) {
        boolean negateResult;
        if (value < 0.0) {
            value = -value;
            negateResult = true;
        } else {
            negateResult = false;
        }
        if (value == 1.0) {
            // We want "exact" result for 1.0.
            return negateResult ? -Math.PI/4 : Math.PI/4;
        } else if (value <= ATAN_MAX_VALUE_FOR_TABS) {
            int index = (int)(value * ATAN_INDEXER + 0.5);
            double delta = value - index * ATAN_DELTA;
            double result = atanTab[index] + delta * (atanDer1DivF1Tab[index] + delta * (atanDer2DivF2Tab[index] + delta * (atanDer3DivF3Tab[index] + delta * atanDer4DivF4Tab[index])));
            return negateResult ? -result : result;
        } else { // value > ATAN_MAX_VALUE_FOR_TABS, or value is NaN
            // This part is derived from fdlibm.
            if (value < TWO_POW_66) {
                double x = -1/value;
                double x2 = x*x;
                double x4 = x2*x2;
                double s1 = x2*(ATAN_AT0+x4*(ATAN_AT2+x4*(ATAN_AT4+x4*(ATAN_AT6+x4*(ATAN_AT8+x4*ATAN_AT10)))));
                double s2 = x4*(ATAN_AT1+x4*(ATAN_AT3+x4*(ATAN_AT5+x4*(ATAN_AT7+x4*ATAN_AT9))));
                double result = ATAN_HI3-((x*(s1+s2)-ATAN_LO3)-x);
                return negateResult ? -result : result;
            } else { // value >= 2^66, or value is NaN
                if (Double.isNaN(value)) {
                    return Double.NaN;
                } else {
                    return negateResult ? -Math.PI/2 : Math.PI/2;
                }
            }
        }
    }

    /**
     * For special values for which multiple conventions could be adopted,
     * behaves like Math.atan2(double,double).
     *
     * @param y Coordinate on y axis.
     * @param x Coordinate on x axis.
     * @return Angle from x axis positive side to (x,y) position, in radians, in [-PI,PI].
     *         Angle measure is positive when going from x axis to y axis (positive sides).
     */
    static double atan2(double y, double x) {
        /*
         * Using sub-methods, to make method lighter for general case,
         * and to avoid JIT-optimization crash on NaN.
         */
        if (x > 0.0) {
            if (y == 0.0) {
                // +-0.0
                return y;
            }
            if (x == Double.POSITIVE_INFINITY) {
                return atan2_pinf_yyy(y);
            } else {
                return JafamaFastMath.atan(y/x);
            }
        } else if (x < 0.0) {
            if (y == 0.0) {
                return JafamaFastMath.signFromBit(y) * Math.PI;
            }
            if (x == Double.NEGATIVE_INFINITY) {
                return atan2_ninf_yyy(y);
            } else if (y > 0.0) {
                return Math.PI/2 + JafamaFastMath.atan(-x/y);
            } else if (y < 0.0) {
                return -Math.PI/2 - JafamaFastMath.atan(x/y);
            } else {
                return Double.NaN;
            }
        } else {
            return atan2_zeroOrNaN_yyy(x, y);
        }
    }

    /*
     * logarithms
     */

    /**
     * Returns sqrt(x^2+y^2) without intermediate overflow or underflow.
     */
    static double hypot(double x, double y) {
        x = Math.abs(x);
        y = Math.abs(y);
        if (y < x) {
            double a = x;
            x = y;
            y = a;
        } else if (!(y >= x)) { // Testing if we have some NaN.
            if ((x == Double.POSITIVE_INFINITY) || (y == Double.POSITIVE_INFINITY)) {
                return Double.POSITIVE_INFINITY;
            } else {
                return Double.NaN;
            }
        }
        if (y-x == y) { // x too small to substract from y
            return y;
        } else {
            double factor;
            if (x > TWO_POW_450) { // 2^450 < x < y
                x *= TWO_POW_N750;
                y *= TWO_POW_N750;
                factor = TWO_POW_750;
            } else if (y < TWO_POW_N450) { // x < y < 2^-450
                x *= TWO_POW_750;
                y *= TWO_POW_750;
                factor = TWO_POW_N750;
            } else {
                factor = 1.0;
            }
            return factor * Math.sqrt(x*x+y*y);
        }
    }

    //--------------------------------------------------------------------------
    //  PRIVATE TREATMENTS
    //--------------------------------------------------------------------------

    /**
     * JafamaFastMath is non-instantiable.
     */
    private JafamaFastMath() {
    }

    /**
     * Use look-up tables size power through this method,
     * to make sure is it small in case java.lang.Math
     * is directly used.
     */
    private static int getTabSizePower(int tabSizePower) {
        return tabSizePower;
    }

    /**
     * @param value A double value.
     * @return -1 if sign bit if 1, 1 if sign bit if 0.
     */
    private static long signFromBit(double value) {
        // Returning a long, to avoid useless cast into int.
        return ((Double.doubleToRawLongBits(value)>>62)|1);
    }

    /**
     * Returns the exact result, provided it's in double range,
     * i.e. if power is in [-1074,1023].
     *
     * @param power A power.
     * @return 2^power.
     */
    private static double twoPow(int power) {
        if (power <= -MAX_DOUBLE_EXPONENT) { // Not normal.
            if (power >= MIN_DOUBLE_EXPONENT) { // Subnormal.
                return Double.longBitsToDouble(0x0008000000000000L>>(-(power+MAX_DOUBLE_EXPONENT)));
            } else { // Underflow.
                return 0.0;
            }
        } else if (power > MAX_DOUBLE_EXPONENT) { // Overflow.
            return Double.POSITIVE_INFINITY;
        } else { // Normal.
            return Double.longBitsToDouble(((long)(power+MAX_DOUBLE_EXPONENT))<<52);
        }
    }

    /**
     * @param power Must be in normal values range.
     */
    private static double twoPowNormal(int power) {
        if (USE_TWO_POW_TAB) {
            return twoPowTab[power-MIN_DOUBLE_EXPONENT];
        } else {
            return Double.longBitsToDouble(((long)(power+MAX_DOUBLE_EXPONENT))<<52);
        }
    }

    private static double atan2_pinf_yyy(double y) {
        if (y == Double.POSITIVE_INFINITY) {
            return Math.PI/4;
        } else if (y == Double.NEGATIVE_INFINITY) {
            return -Math.PI/4;
        } else if (y > 0.0) {
            return 0.0;
        } else if (y < 0.0) {
            return -0.0;
        } else {
            return Double.NaN;
        }
    }

    private static double atan2_ninf_yyy(double y) {
        if (y == Double.POSITIVE_INFINITY) {
            return 3*Math.PI/4;
        } else if (y == Double.NEGATIVE_INFINITY) {
            return -3*Math.PI/4;
        } else if (y > 0.0) {
            return Math.PI;
        } else if (y < 0.0) {
            return -Math.PI;
        } else {
            return Double.NaN;
        }
    }

    private static double atan2_zeroOrNaN_yyy(double x, double y) {
        if (x == 0.0) {
            if (y == 0.0) {
                if (JafamaFastMath.signFromBit(x) < 0) {
                    // x is -0.0
                    return JafamaFastMath.signFromBit(y) * Math.PI;
                } else {
                    // +-0.0
                    return y;
                }
            }
            if (y > 0.0) {
                return Math.PI/2;
            } else if (y < 0.0) {
                return -Math.PI/2;
            } else {
                return Double.NaN;
            }
        } else {
            return Double.NaN;
        }
    }

    /**
     * Remainder using an accurate definition of PI.
     * Derived from a fdlibm treatment called __ieee754_rem_pio2.
     *
     * This method can return values slightly (like one ULP or so) outside [-Math.PI,Math.PI] range.
     *
     * @param angle Angle in radians.
     * @return Remainder of (angle % (2*PI)), which is in [-PI,PI] range.
     */
    private static double remainderTwoPi(double angle) {
        boolean negateResult;
        if (angle < 0.0) {
            negateResult = true;
            angle = -angle;
        } else {
            negateResult = false;
        }
        if (angle <= NORMALIZE_ANGLE_MAX_MEDIUM_DOUBLE) {
            double fn = (int)(angle*INVTWOPI+0.5);
            double result = (angle - fn*TWOPI_HI) - fn*TWOPI_LO;
            return negateResult ? -result : result;
        } else if (angle < Double.POSITIVE_INFINITY) {
            // Reworking exponent to have a value < 2^24.
            long lx = Double.doubleToRawLongBits(angle);
            long exp = ((lx>>52)&0x7FF) - 1046;
            double z = Double.longBitsToDouble(lx - (exp<<52));

            double x0 = ((int)z);
            z = (z-x0)*TWO_POW_24;
            double x1 = ((int)z);
            double x2 = (z-x1)*TWO_POW_24;

            double result = subRemainderTwoPi(x0, x1, x2, (int)exp, (x2 == 0) ? 2 : 3);
            return negateResult ? -result : result;
        } else { // angle is +infinity or NaN
            return Double.NaN;
        }
    }

    /**
     * Remainder using an accurate definition of PI.
     * Derived from a fdlibm treatment called __kernel_rem_pio2.
     *
     * @param x0 Most significant part of the value, as an integer < 2^24, in double precision format. Must be >= 0.
     * @param x1 Following significant part of the value, as an integer < 2^24, in double precision format.
     * @param x2 Least significant part of the value, as an integer < 2^24, in double precision format.
     * @param e0 Exponent of x0 (value is (2^e0)*(x0+(2^-24)*(x1+(2^-24)*x2))). Must be >= -20.
     * @param nx Number of significant parts to take into account. Must be 2 or 3.
     * @return Remainder of (value % (2*PI)), which is in [-PI,PI] range.
     */
    private static double subRemainderTwoPi(double x0, double x1, double x2, int e0, int nx) {
        int ih;
        double z,fw;
        double f0,f1,f2,f3,f4,f5,f6 = 0.0,f7;
        double q0,q1,q2,q3,q4,q5;
        int iq0,iq1,iq2,iq3,iq4;

        final int jx = nx - 1; // jx in [1,2] (nx in [2,3])
        // Could use a table to avoid division, but the gain isn't worth it most likely...
        final int jv = (e0-3)/24; // We do not handle the case (e0-3 < -23).
        int q = e0-((jv<<4)+(jv<<3))-24; // e0-24*(jv+1)

        final int j = jv + 4;
        if (jx == 1) {
            f5 = (j >= 0) ? ONE_OVER_TWOPI_TAB[j]: 0.0;
            f4 = (j >= 1) ? ONE_OVER_TWOPI_TAB[j-1]: 0.0;
            f3 = (j >= 2) ? ONE_OVER_TWOPI_TAB[j-2]: 0.0;
            f2 = (j >= 3) ? ONE_OVER_TWOPI_TAB[j-3]: 0.0;
            f1 = (j >= 4) ? ONE_OVER_TWOPI_TAB[j-4]: 0.0;
            f0 = (j >= 5) ? ONE_OVER_TWOPI_TAB[j-5]: 0.0;

            q0 = x0*f1 + x1*f0;
            q1 = x0*f2 + x1*f1;
            q2 = x0*f3 + x1*f2;
            q3 = x0*f4 + x1*f3;
            q4 = x0*f5 + x1*f4;
        } else { // jx == 2
            f6 = (j >= 0) ? ONE_OVER_TWOPI_TAB[j]: 0.0;
            f5 = (j >= 1) ? ONE_OVER_TWOPI_TAB[j-1]: 0.0;
            f4 = (j >= 2) ? ONE_OVER_TWOPI_TAB[j-2]: 0.0;
            f3 = (j >= 3) ? ONE_OVER_TWOPI_TAB[j-3]: 0.0;
            f2 = (j >= 4) ? ONE_OVER_TWOPI_TAB[j-4]: 0.0;
            f1 = (j >= 5) ? ONE_OVER_TWOPI_TAB[j-5]: 0.0;
            f0 = (j >= 6) ? ONE_OVER_TWOPI_TAB[j-6]: 0.0;

            q0 = x0*f2 + x1*f1 + x2*f0;
            q1 = x0*f3 + x1*f2 + x2*f1;
            q2 = x0*f4 + x1*f3 + x2*f2;
            q3 = x0*f5 + x1*f4 + x2*f3;
            q4 = x0*f6 + x1*f5 + x2*f4;
        }

        z = q4;
        fw  = ((int)(TWO_POW_N24*z));
        iq0 = (int)(z-TWO_POW_24*fw);
        z   = q3+fw;
        fw  = ((int)(TWO_POW_N24*z));
        iq1 = (int)(z-TWO_POW_24*fw);
        z   = q2+fw;
        fw  = ((int)(TWO_POW_N24*z));
        iq2 = (int)(z-TWO_POW_24*fw);
        z   = q1+fw;
        fw  = ((int)(TWO_POW_N24*z));
        iq3 = (int)(z-TWO_POW_24*fw);
        z   = q0+fw;

        // Here, q is in [-25,2] range or so,
        // so in normal exponents range.
        double twoPowQ = twoPowNormal(q);

        z = (z*twoPowQ) % 8.0;
        z -= ((int)z);
        if (q > 0) {
            iq3 &= 0xFFFFFF>>q;
            ih = iq3>>(23-q);
        } else if (q == 0) {
            ih = iq3>>23;
        } else if (z >= 0.5) {
            ih = 2;
        } else {
            ih = 0;
        }
        if (ih > 0) {
            int carry;
            if (iq0 != 0) {
                carry = 1;
                iq0 = 0x1000000 - iq0;
                iq1 = 0x0FFFFFF - iq1;
                iq2 = 0x0FFFFFF - iq2;
                iq3 = 0x0FFFFFF - iq3;
            } else {
                if (iq1 != 0) {
                    carry = 1;
                    iq1 = 0x1000000 - iq1;
                    iq2 = 0x0FFFFFF - iq2;
                    iq3 = 0x0FFFFFF - iq3;
                } else {
                    if (iq2 != 0) {
                        carry = 1;
                        iq2 = 0x1000000 - iq2;
                        iq3 = 0x0FFFFFF - iq3;
                    } else {
                        if (iq3 != 0) {
                            carry = 1;
                            iq3 = 0x1000000 - iq3;
                        } else {
                            carry = 0;
                        }
                    }
                }
            }
            if (q > 0) {
                switch (q) {
                case 1:
                    iq3 &= 0x7FFFFF;
                    break;
                case 2:
                    iq3 &= 0x3FFFFF;
                    break;
                }
            }
            if (ih == 2) {
                z = 1.0 - z;
                if (carry != 0) {
                    z -= twoPowQ;
                }
            }
        }

        if (z == 0.0) {
            if (jx == 1) {
                f6 = ONE_OVER_TWOPI_TAB[jv+5];
                q5 = x0*f6 + x1*f5;
            } else { // jx == 2
                f7 = ONE_OVER_TWOPI_TAB[jv+5];
                q5 = x0*f7 + x1*f6 + x2*f5;
            }

            z = q5;
            fw  = ((int)(TWO_POW_N24*z));
            iq0 = (int)(z-TWO_POW_24*fw);
            z   = q4+fw;
            fw  = ((int)(TWO_POW_N24*z));
            iq1 = (int)(z-TWO_POW_24*fw);
            z   = q3+fw;
            fw  = ((int)(TWO_POW_N24*z));
            iq2 = (int)(z-TWO_POW_24*fw);
            z   = q2+fw;
            fw  = ((int)(TWO_POW_N24*z));
            iq3 = (int)(z-TWO_POW_24*fw);
            z   = q1+fw;
            fw  = ((int)(TWO_POW_N24*z));
            iq4 = (int)(z-TWO_POW_24*fw);
            z   = q0+fw;

            z = (z*twoPowQ) % 8.0;
            z -= ((int)z);
            if (q > 0) {
                // some parentheses for Eclipse formatter's weaknesses with bits shifts
                iq4 &= (0xFFFFFF>>q);
                ih = (iq4>>(23-q));
            } else if (q == 0) {
                ih = iq4>>23;
            } else if (z >= 0.5) {
                ih = 2;
            } else {
                ih = 0;
            }
            if (ih > 0) {
                if (iq0 != 0) {
                    iq0 = 0x1000000 - iq0;
                    iq1 = 0x0FFFFFF - iq1;
                    iq2 = 0x0FFFFFF - iq2;
                    iq3 = 0x0FFFFFF - iq3;
                    iq4 = 0x0FFFFFF - iq4;
                } else {
                    if (iq1 != 0) {
                        iq1 = 0x1000000 - iq1;
                        iq2 = 0x0FFFFFF - iq2;
                        iq3 = 0x0FFFFFF - iq3;
                        iq4 = 0x0FFFFFF - iq4;
                    } else {
                        if (iq2 != 0) {
                            iq2 = 0x1000000 - iq2;
                            iq3 = 0x0FFFFFF - iq3;
                            iq4 = 0x0FFFFFF - iq4;
                        } else {
                            if (iq3 != 0) {
                                iq3 = 0x1000000 - iq3;
                                iq4 = 0x0FFFFFF - iq4;
                            } else {
                                if (iq4 != 0) {
                                    iq4 = 0x1000000 - iq4;
                                }
                            }
                        }
                    }
                }
                if (q > 0) {
                    switch (q) {
                    case 1:
                        iq4 &= 0x7FFFFF;
                        break;
                    case 2:
                        iq4 &= 0x3FFFFF;
                        break;
                    }
                }
            }
            fw = twoPowQ * TWO_POW_N24; // q -= 24, so initializing fw with ((2^q)*(2^-24)=2^(q-24))
        } else {
           iq4 = (int)(z/twoPowQ);
            fw = twoPowQ;
        }

        q4 = fw*iq4;
        fw *= TWO_POW_N24;
        q3 = fw*iq3;
        fw *= TWO_POW_N24;
        q2 = fw*iq2;
        fw *= TWO_POW_N24;
        q1 = fw*iq1;
        fw *= TWO_POW_N24;
        q0 = fw*iq0;
        fw *= TWO_POW_N24;

        fw = TWOPI_TAB0*q4;
        fw += TWOPI_TAB0*q3 + TWOPI_TAB1*q4;
        fw += TWOPI_TAB0*q2 + TWOPI_TAB1*q3 + TWOPI_TAB2*q4;
        fw += TWOPI_TAB0*q1 + TWOPI_TAB1*q2 + TWOPI_TAB2*q3 + TWOPI_TAB3*q4;
        fw += TWOPI_TAB0*q0 + TWOPI_TAB1*q1 + TWOPI_TAB2*q2 + TWOPI_TAB3*q3 + TWOPI_TAB4*q4;

        return (ih == 0) ? fw : -fw;
    }

    //--------------------------------------------------------------------------
    // STATIC INITIALIZATIONS
    //--------------------------------------------------------------------------

    static {
        init();
    }

    /**
     * Initializes look-up tables.
     *
     * Using redefined pure Java treatments in this method, instead of Math
     * or StrictMath ones (even asin(double)), can make this class load much
     * slower, because class loading is likely not to be optimized.
     *
     * Could use strictfp and StrictMath here, to always end up with the same tables,
     * but this class doesn't guarantee identical results across various architectures,
     * so to make it simple (less keywords/dependencies), we don't use strictfp,
     * and use Math - which could also help if used StrictMath methods are slow.
     */
    private static void init() {

        /*
         * sin and cos
         */

        final int SIN_COS_PI_INDEX = (SIN_COS_TABS_SIZE-1)/2;
        final int SIN_COS_PI_MUL_2_INDEX = 2*SIN_COS_PI_INDEX;
        final int SIN_COS_PI_MUL_0_5_INDEX = SIN_COS_PI_INDEX/2;
        final int SIN_COS_PI_MUL_1_5_INDEX = 3*SIN_COS_PI_INDEX/2;
        for (int i=0;i<SIN_COS_TABS_SIZE;i++) {
            // angle: in [0,2*PI].
            double angle = i * SIN_COS_DELTA_HI + i * SIN_COS_DELTA_LO;
            double sinAngle = Math.sin(angle);
            double cosAngle = Math.cos(angle);
            // For indexes corresponding to zero cosine or sine, we make sure the value is zero
            // and not an epsilon. This allows for a much better accuracy for results close to zero.
            if (i == SIN_COS_PI_INDEX) {
                sinAngle = 0.0;
            } else if (i == SIN_COS_PI_MUL_2_INDEX) {
                sinAngle = 0.0;
            } else if (i == SIN_COS_PI_MUL_0_5_INDEX) {
                cosAngle = 0.0;
            } else if (i == SIN_COS_PI_MUL_1_5_INDEX) {
                cosAngle = 0.0;
            }
            sinTab[i] = sinAngle;
            cosTab[i] = cosAngle;
        }

        /*
         * asin
         */

        for (int i=0;i<ASIN_TABS_SIZE;i++) {
            // x: in [0,ASIN_MAX_VALUE_FOR_TABS].
            double x = i * ASIN_DELTA;
            double oneMinusXSqInv = 1/(1-x*x);
            double oneMinusXSqInv0_5 = Math.sqrt(oneMinusXSqInv);
            double oneMinusXSqInv1_5 = oneMinusXSqInv0_5*oneMinusXSqInv;
            double oneMinusXSqInv2_5 = oneMinusXSqInv1_5*oneMinusXSqInv;
            double oneMinusXSqInv3_5 = oneMinusXSqInv2_5*oneMinusXSqInv;
            asinTab[i] = Math.asin(x);
            asinDer1DivF1Tab[i] = oneMinusXSqInv0_5;
            asinDer2DivF2Tab[i] = (x*oneMinusXSqInv1_5) * ONE_DIV_F2;
            asinDer3DivF3Tab[i] = ((1+2*x*x)*oneMinusXSqInv2_5) * ONE_DIV_F3;
            asinDer4DivF4Tab[i] = ((5+2*x*(2+x*(5-2*x)))*oneMinusXSqInv3_5) * ONE_DIV_F4;
        }

        /*
         * atan
         */

        for (int i=0;i<ATAN_TABS_SIZE;i++) {
            // x: in [0,ATAN_MAX_VALUE_FOR_TABS].
            double x = i * ATAN_DELTA;
            double onePlusXSqInv = 1/(1+x*x);
            double onePlusXSqInv2 = onePlusXSqInv*onePlusXSqInv;
            double onePlusXSqInv3 = onePlusXSqInv2*onePlusXSqInv;
            double onePlusXSqInv4 = onePlusXSqInv2*onePlusXSqInv2;
            atanTab[i] = Math.atan(x);
            atanDer1DivF1Tab[i] = onePlusXSqInv;
            atanDer2DivF2Tab[i] = (-2*x*onePlusXSqInv2) * ONE_DIV_F2;
            atanDer3DivF3Tab[i] = ((-2+6*x*x)*onePlusXSqInv3) * ONE_DIV_F3;
            atanDer4DivF4Tab[i] = ((24*x*(1-x*x))*onePlusXSqInv4) * ONE_DIV_F4;
        }

        /*
         * twoPowTab
         */

        if (USE_TWO_POW_TAB) {
            for (int i=MIN_DOUBLE_EXPONENT;i<=MAX_DOUBLE_EXPONENT;i++) {
                twoPowTab[i-MIN_DOUBLE_EXPONENT] = twoPow(i);
            }
        }
    }
}
