/*
 * Copyright 2013 Stefan Zobel
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
package math.fast;

/**
 * SpeedyMath can be used as a drop-in replacement for some {@link Math}
 * methods. This means that for a couple of methods in {@code java.lang.Math}
 * (say {@code Math.sinh(x)} or {@code Math.hypot(y)}), you can directly change
 * the class and use the methods as is (using {@code SpeedyMath.sinh(x)} or
 * {@code SpeedyMath.hypot(y)} in the previous example).
 */
public final class SpeedyMath {

    /*
     * Don't let anyone instantiate this class.
     */
    private SpeedyMath() {
        throw new AssertionError();
    }

    /**
     * Returns the trigonometric cosine of an angle. Special cases:
     * <ul>
     * <li>If the argument is NaN or an infinity, then the result is NaN.
     * </ul>
     * 
     * <p>
     * The computed result must be within 1 ulp of the exact result. Results
     * must be semi-monotonic.
     * 
     * @param a
     *            an angle, in radians.
     * @return the cosine of the argument.
     */
    public static double cos(double a) {
        return JafamaFastMath.cos(a);
    }

    /**
     * Returns the arc sine of a value; the returned angle is in the range
     * -<i>pi</i>/2 through <i>pi</i>/2. Special cases:
     * <ul>
     * <li>If the argument is NaN or its absolute value is greater than 1, then
     * the result is NaN.
     * <li>If the argument is zero, then the result is a zero with the same sign
     * as the argument.
     * </ul>
     * 
     * <p>
     * The computed result must be within 1 ulp of the exact result. Results
     * must be semi-monotonic.
     * 
     * @param a
     *            the value whose arc sine is to be returned.
     * @return the arc sine of the argument.
     */
    public static double asin(double a) {
        return JafamaFastMath.asin(a);
    }

    /**
     * Returns the arc cosine of a value; the returned angle is in the range 0.0
     * through <i>pi</i>. Special case:
     * <ul>
     * <li>If the argument is NaN or its absolute value is greater than 1, then
     * the result is NaN.
     * </ul>
     * 
     * <p>
     * The computed result must be within 1 ulp of the exact result. Results
     * must be semi-monotonic.
     * 
     * @param a
     *            the value whose arc cosine is to be returned.
     * @return the arc cosine of the argument.
     */
    public static double acos(double a) {
        return JafamaFastMath.acos(a);
    }

    /**
     * Returns the arc tangent of a value; the returned angle is in the range
     * -<i>pi</i>/2 through <i>pi</i>/2. Special cases:
     * <ul>
     * <li>If the argument is NaN, then the result is NaN.
     * <li>If the argument is zero, then the result is a zero with the same sign
     * as the argument.
     * </ul>
     * 
     * <p>
     * The computed result must be within 1 ulp of the exact result. Results
     * must be semi-monotonic.
     * 
     * @param a
     *            the value whose arc tangent is to be returned.
     * @return the arc tangent of the argument.
     */
    public static double atan(double a) {
        return JafamaFastMath.atan(a);
    }

    /**
     * Returns the angle <i>theta</i> from the conversion of rectangular
     * coordinates ({@code x},&nbsp;{@code y}) to polar coordinates
     * (r,&nbsp;<i>theta</i>). This method computes the phase <i>theta</i> by
     * computing an arc tangent of {@code y/x} in the range of -<i>pi</i> to
     * <i>pi</i>. Special cases:
     * <ul>
     * <li>If either argument is NaN, then the result is NaN.
     * <li>If the first argument is positive zero and the second argument is
     * positive, or the first argument is positive and finite and the second
     * argument is positive infinity, then the result is positive zero.
     * <li>If the first argument is negative zero and the second argument is
     * positive, or the first argument is negative and finite and the second
     * argument is positive infinity, then the result is negative zero.
     * <li>If the first argument is positive zero and the second argument is
     * negative, or the first argument is positive and finite and the second
     * argument is negative infinity, then the result is the {@code double}
     * value closest to <i>pi</i>.
     * <li>If the first argument is negative zero and the second argument is
     * negative, or the first argument is negative and finite and the second
     * argument is negative infinity, then the result is the {@code double}
     * value closest to -<i>pi</i>.
     * <li>If the first argument is positive and the second argument is positive
     * zero or negative zero, or the first argument is positive infinity and the
     * second argument is finite, then the result is the {@code double} value
     * closest to <i>pi</i>/2.
     * <li>If the first argument is negative and the second argument is positive
     * zero or negative zero, or the first argument is negative infinity and the
     * second argument is finite, then the result is the {@code double} value
     * closest to -<i>pi</i>/2.
     * <li>If both arguments are positive infinity, then the result is the
     * {@code double} value closest to <i>pi</i>/4.
     * <li>If the first argument is positive infinity and the second argument is
     * negative infinity, then the result is the {@code double} value closest to
     * 3*<i>pi</i>/4.
     * <li>If the first argument is negative infinity and the second argument is
     * positive infinity, then the result is the {@code double} value closest to
     * -<i>pi</i>/4.
     * <li>If both arguments are negative infinity, then the result is the
     * {@code double} value closest to -3*<i>pi</i>/4.
     * </ul>
     * 
     * <p>
     * The computed result must be within 2 ulps of the exact result. Results
     * must be semi-monotonic.
     * 
     * @param y
     *            the ordinate coordinate
     * @param x
     *            the abscissa coordinate
     * @return the <i>theta</i> component of the point
     *         (<i>r</i>,&nbsp;<i>theta</i>) in polar coordinates that
     *         corresponds to the point (<i>x</i>,&nbsp;<i>y</i>) in Cartesian
     *         coordinates.
     */
    public static double atan2(double y, double x) {
        return JafamaFastMath.atan2(y, x);
    }

    /**
     * Returns the hyperbolic sine of a {@code double} value. The hyperbolic
     * sine of <i>x</i> is defined to be
     * (<i>e<sup>x</sup>&nbsp;-&nbsp;e<sup>-x</sup></i>)/2 where <i>e</i> is
     * {@linkplain Math#E Euler's number}.
     * 
     * <p>
     * Special cases:
     * <ul>
     * 
     * <li>If the argument is NaN, then the result is NaN.
     * 
     * <li>If the argument is infinite, then the result is an infinity with the
     * same sign as the argument.
     * 
     * <li>If the argument is zero, then the result is a zero with the same sign
     * as the argument.
     * 
     * </ul>
     * 
     * <p>
     * The computed result must be within 2.5 ulps of the exact result.
     * 
     * @param x
     *            The number whose hyperbolic sine is to be returned.
     * @return The hyperbolic sine of {@code x}.
     * @since 1.5
     */
    public static double sinh(double x) {
        return CommonsAccurateMath.sinh(x);
    }

    /**
     * Returns the hyperbolic cosine of a {@code double} value. The hyperbolic
     * cosine of <i>x</i> is defined to be
     * (<i>e<sup>x</sup>&nbsp;+&nbsp;e<sup>-x</sup></i>)/2 where <i>e</i> is
     * {@linkplain Math#E Euler's number}.
     * 
     * <p>
     * Special cases:
     * <ul>
     * 
     * <li>If the argument is NaN, then the result is NaN.
     * 
     * <li>If the argument is infinite, then the result is positive infinity.
     * 
     * <li>If the argument is zero, then the result is {@code 1.0}.
     * 
     * </ul>
     * 
     * <p>
     * The computed result must be within 2.5 ulps of the exact result.
     * 
     * @param x
     *            The number whose hyperbolic cosine is to be returned.
     * @return The hyperbolic cosine of {@code x}.
     * @since 1.5
     */
    public static double cosh(double x) {
        return CommonsAccurateMath.cosh(x);
    }

    /**
     * Returns the hyperbolic tangent of a {@code double} value. The hyperbolic
     * tangent of <i>x</i> is defined to be
     * (<i>e<sup>x</sup>&nbsp;-&nbsp;e<sup>-
     * x</sup></i>)/(<i>e<sup>x</sup>&nbsp;+&nbsp;e<sup>-x</sup></i>), in other
     * words, {@linkplain Math#sinh sinh(<i>x</i>)}/{@linkplain Math#cosh
     * cosh(<i>x</i>)}. Note that the absolute value of the exact tanh is always
     * less than 1.
     * 
     * <p>
     * Special cases:
     * <ul>
     * 
     * <li>If the argument is NaN, then the result is NaN.
     * 
     * <li>If the argument is zero, then the result is a zero with the same sign
     * as the argument.
     * 
     * <li>If the argument is positive infinity, then the result is
     * {@code +1.0}.
     * 
     * <li>If the argument is negative infinity, then the result is
     * {@code -1.0}.
     * 
     * </ul>
     * 
     * <p>
     * The computed result must be within 2.5 ulps of the exact result. The
     * result of {@code tanh} for any finite input must have an absolute value
     * less than or equal to 1. Note that once the exact result of tanh is
     * within 1/2 of an ulp of the limit value of &plusmn;1, correctly signed
     * &plusmn;{@code 1.0} should be returned.
     * 
     * @param x
     *            The number whose hyperbolic tangent is to be returned.
     * @return The hyperbolic tangent of {@code x}.
     * @since 1.5
     */
    public static double tanh(double x) {
        return CommonsAccurateMath.tanh(x);
    }

    /**
     * Returns sqrt(<i>x</i><sup>2</sup>&nbsp;+<i>y</i><sup>2</sup>) without
     * intermediate overflow or underflow.
     * 
     * <p>
     * Special cases:
     * <ul>
     * 
     * <li>If either argument is infinite, then the result is positive infinity.
     * 
     * <li>If either argument is NaN and neither argument is infinite, then the
     * result is NaN.
     * 
     * </ul>
     * 
     * <p>
     * The computed result must be within 1 ulp of the exact result. If one
     * parameter is held constant, the results must be semi-monotonic in the
     * other parameter.
     * 
     * @param x
     *            a value
     * @param y
     *            a value
     * @return sqrt(<i>x</i><sup>2</sup>&nbsp;+<i>y</i><sup>2</sup>) without
     *         intermediate overflow or underflow
     * @since 1.5
     */
    public static double hypot(double x, double y) {
        return JafamaFastMath.hypot(x, y);
    }

    /**
     * Returns <i>e</i><sup>x</sup>&nbsp;-1. Note that for values of <i>x</i>
     * near 0, the exact sum of {@code expm1(x)}&nbsp;+&nbsp;1 is much closer to
     * the true result of <i>e</i><sup>x</sup> than {@code exp(x)}.
     * 
     * <p>
     * Special cases:
     * <ul>
     * <li>If the argument is NaN, the result is NaN.
     * 
     * <li>If the argument is positive infinity, then the result is positive
     * infinity.
     * 
     * <li>If the argument is negative infinity, then the result is -1.0.
     * 
     * <li>If the argument is zero, then the result is a zero with the same sign
     * as the argument.
     * 
     * </ul>
     * 
     * <p>
     * The computed result must be within 1 ulp of the exact result. Results
     * must be semi-monotonic. The result of {@code expm1} for any finite input
     * must be greater than or equal to {@code -1.0}. Note that once the exact
     * result of <i>e</i><sup>{@code x}</sup>&nbsp;-&nbsp;1 is within 1/2 ulp of
     * the limit value -1, {@code -1.0} should be returned.
     * 
     * @param x
     *            the exponent to raise <i>e</i> to in the computation of
     *            <i>e</i><sup>{@code x}</sup>&nbsp;-1.
     * @return the value <i>e</i><sup>{@code x}</sup>&nbsp;-&nbsp;1.
     * @since 1.5
     */
    public static double expm1(double x) {
        return CommonsAccurateMath.expm1(x);
    }
}
