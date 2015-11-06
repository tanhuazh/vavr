/*     / \____  _    _  ____   ______  / \ ____  __    _ _____
 *    /  /    \/ \  / \/    \ /  /\__\/  //    \/  \  / /  _  \   Javaslang
 *  _/  /  /\  \  \/  /  /\  \\__\\  \  //  /\  \ /\\/  \__/  /   Copyright 2014-now Daniel Dietrich
 * /___/\_/  \_/\____/\_/  \_/\__\/__/___\_/  \_//  \__/_____/    Licensed under the Apache License, Version 2.0
 */
package javaslang.collection;

import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Value;
import javaslang.control.None;
import javaslang.control.Option;
import javaslang.control.Some;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An interface for data structures that are traversable once.
 *
 * <p>
 * Basic operations:
 *
 * <ul>
 * <li>{@link #clear()}</li>
 * <li>{@link #contains(Object)}</li>
 * <li>{@link #containsAll(java.lang.Iterable)}</li>
 * <li>{@link #head()}</li>
 * <li>{@link #headOption()}</li>
 * <li>{@link #init()}</li>
 * <li>{@link #initOption()}</li>
 * <li>{@link #isEmpty()}</li>
 * <li>{@link #last()}</li>
 * <li>{@link #lastOption()}</li>
 * <li>{@link #length()}</li>
 * <li>{@link #tail()}</li>
 * <li>{@link #tailOption()}</li>
 * </ul>
 *
 * Filtering:
 *
 * <ul>
 * <li>{@link #filter(Predicate)}</li>
 * <li>{@link #retainAll(java.lang.Iterable)}</li>
 * </ul>
 *
 * Numeric operations:
 *
 * <ul>
 * <li>{@link #average()}</li>
 * <li>{@link #max()}</li>
 * <li>{@link #maxBy(Comparator)}</li>
 * <li>{@link #maxBy(Function)}</li>
 * <li>{@link #min()}</li>
 * <li>{@link #minBy(Comparator)}</li>
 * <li>{@link #minBy(Function)}</li>
 * <li>{@link #product()}</li>
 * <li>{@link #sum()}</li>
 * </ul>
 *
 * Reduction:
 *
 * <ul>
 * <li>{@link #fold(Object, BiFunction)}</li>
 * <li>{@link #foldLeft(Object, BiFunction)}</li>
 * <li>{@link #foldRight(Object, BiFunction)}</li>
 * <li>{@link #mkString()}</li>
 * <li>{@link #mkString(CharSequence)}</li>
 * <li>{@link #mkString(CharSequence, CharSequence, CharSequence)}</li>
 * <li>{@link #reduce(BiFunction)}</li>
 * <li>{@link #reduceLeft(BiFunction)}</li>
 * <li>{@link #reduceRight(BiFunction)}</li>
 * </ul>
 *
 * Selection:
 *
 * <ul>
 * <li>{@link #drop(int)}</li>
 * <li>{@link #dropRight(int)}</li>
 * <li>{@link #dropWhile(Predicate)}</li>
 * <li>{@link #findFirst(Predicate)}</li>
 * <li>{@link #findLast(Predicate)}</li>
 * <li>{@link #take(int)}</li>
 * <li>{@link #takeRight(int)}</li>
 * <li>{@link #takeWhile(Predicate)}</li>
 * </ul>
 *
 * Tests:
 *
 * <ul>
 * <li>{@link #existsUnique(Predicate)}</li>
 * <li>{@link #hasDefiniteSize()}</li>
 * <li>{@link #isTraversableAgain()}</li>
 * </ul>
 *
 * Transformation:
 *
 * <ul>
 * <li>{@link #distinct()}</li>
 * <li>{@link #distinctBy(Comparator)}</li>
 * <li>{@link #distinctBy(Function)}</li>
 * <li>{@link #flatMap(Function)}</li>
 * <li>{@link #flatten()}</li>
 * <li>{@link #groupBy(Function)}</li>
 * <li>{@link #map(Function)}</li>
 * <li>{@link #partition(Predicate)}</li>
 * <li>{@link #replace(Object, Object)}</li>
 * <li>{@link #replaceAll(Object, Object)}</li>
 * <li>{@link #span(Predicate)}</li>
 * </ul>
 *
 * @param <T> Component type
 * @author Daniel Dietrich and others
 * @since 2.0.0
 */
public interface TraversableOnce<T> extends Value<T> {

    /**
     * Calculates the average of this elements. Returns {@code None} if this is empty, otherwise {@code Some(average)}.
     * Supported component types are {@code Byte}, {@code Double}, {@code Float}, {@code Integer}, {@code Long},
     * {@code Short}, {@code BigInteger} and {@code BigDecimal}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * List.empty().average()              // = None
     * List.of(1, 2, 3).average()          // = Some(2.0)
     * List.of(0.1, 0.2, 0.3).average()    // = Some(0.2)
     * List.of("apple", "pear").average()  // throws
     * </code>
     * </pre>
     *
     * @return {@code Some(average)} or {@code None}, if there are no elements
     * @throws UnsupportedOperationException if this elements are not numeric
     */
    @SuppressWarnings("unchecked")
    default Option<Double> average() {
        if (isEmpty()) {
            return None.instance();
        } else {
            final TraversableOnce<?> objects = isTraversableAgain() ? this : toStream();
            final Object head = objects.head();
            final double d;
            if (head instanceof Integer || head instanceof Short || head instanceof Byte) {
                d = ((TraversableOnce<Number>) objects)
                        .toJavaStream()
                        .mapToInt(Number::intValue)
                        .average()
                        .getAsDouble();
            } else if (head instanceof Double || head instanceof Float || head instanceof BigDecimal) {
                d = ((TraversableOnce<Number>) objects)
                        .toJavaStream()
                        .mapToDouble(Number::doubleValue)
                        .average()
                        .getAsDouble();
            } else if (head instanceof Long || head instanceof BigInteger) {
                d = ((TraversableOnce<Number>) objects)
                        .toJavaStream()
                        .mapToLong(Number::longValue)
                        .average()
                        .getAsDouble();
            } else {
                throw new UnsupportedOperationException("not numeric");
            }
            return new Some<>(d);
        }
    }

    /**
     * Returns an empty version of this traversable, i.e. {@code this.clear().isEmpty() == true}.
     *
     * @return an empty TraversableOnce.
     */
    TraversableOnce<T> clear();

    /**
     * Tests if this TraversableOnce contains a given value.
     *
     * @param element An Object of type A, may be null.
     * @return true, if element is in this TraversableOnce, false otherwise.
     */
    default boolean contains(T element) {
        return findFirst(e -> java.util.Objects.equals(e, element)).isDefined();
    }

    /**
     * Tests if this TraversableOnce contains all given elements.
     * <p>
     * The result is equivalent to
     * {@code elements.isEmpty() ? true : contains(elements.head()) && containsAll(elements.tail())} but implemented
     * without recursion.
     *
     * @param elements A List of values of type T.
     * @return true, if this List contains all given elements, false otherwise.
     * @throws NullPointerException if {@code elements} is null
     */
    default boolean containsAll(java.lang.Iterable<? extends T> elements) {
        Objects.requireNonNull(elements, "elements is null");
        return List.ofAll(elements).distinct().findFirst(e -> !this.contains(e)).isEmpty();
    }

    /**
     * Returns a new version of this which contains no duplicates. Elements are compared using {@code equals}.
     *
     * @return a new {@code TraversableOnce} containing this elements without duplicates
     */
    TraversableOnce<T> distinct();

    /**
     * Returns a new version of this which contains no duplicates. Elements are compared using the given
     * {@code comparator}.
     *
     * @param comparator A comparator
     * @return a new {@code TraversableOnce} containing this elements without duplicates
     */
    TraversableOnce<T> distinctBy(Comparator<? super T> comparator);

    /**
     * Returns a new version of this which contains no duplicates. Elements mapped to keys which are compared using
     * {@code equals}.
     * <p>
     * The elements of the result are determined in the order of their occurrence - first match wins.
     *
     * @param keyExtractor A key extractor
     * @param <U>          key type
     * @return a new {@code TraversableOnce} containing this elements without duplicates
     * @throws NullPointerException if {@code keyExtractor} is null
     */
    <U> TraversableOnce<T> distinctBy(Function<? super T, ? extends U> keyExtractor);

    /**
     * Drops the first n elements of this or all elements, if this length &lt; n.
     *
     * @param n The number of elements to drop.
     * @return a new instance consisting of all elements of this except the first n ones, or else the empty instance,
     * if this has less than n elements.
     */
    TraversableOnce<T> drop(int n);

    /**
     * Drops the last n elements of this or all elements, if this length &lt; n.
     *
     * @param n The number of elements to drop.
     * @return a new instance consisting of all elements of this except the last n ones, or else the empty instance,
     * if this has less than n elements.
     */
    TraversableOnce<T> dropRight(int n);

    /**
     * Drops elements while the predicate holds for the current element.
     *
     * @param predicate A condition tested subsequently for this elements starting with the first.
     * @return a new instance consisting of all elements starting from the first one which does not satisfy the
     * given predicate.
     * @throws NullPointerException if {@code predicate} is null
     */
    TraversableOnce<T> dropWhile(Predicate<? super T> predicate);

    /**
     * Checks, if a unique elements exists such that the predicate holds.
     *
     * @param predicate A Predicate
     * @return true, if predicate holds for a unique element, false otherwise
     * @throws NullPointerException if {@code predicate} is null
     */
    default boolean existsUnique(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        boolean exists = false;
        for (T t : this) {
            if (predicate.test(t)) {
                if (exists) {
                    return false;
                } else {
                    exists = true;
                }
            }
        }
        return exists;
    }

    /**
     * Returns a new traversable consisting of all elements which satisfy the given predicate.
     *
     * @param predicate A predicate
     * @return a new traversable
     * @throws NullPointerException if {@code predicate} is null
     */
    @Override
    TraversableOnce<T> filter(Predicate<? super T> predicate);

    /**
     * Returns the first element of this which satisfies the given predicate.
     *
     * @param predicate A predicate.
     * @return Some(element) or None, where element may be null (i.e. {@code List.of(null).findFirst(e -> e == null)}).
     * @throws NullPointerException if {@code predicate} is null
     */
    default Option<T> findFirst(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        for (T a : this) {
            if (predicate.test(a)) {
                return new Some<>(a); // may be Some(null)
            }
        }
        return Option.none();
    }

    /**
     * Returns the last element of this which satisfies the given predicate.
     * <p>
     * Same as {@code reverse().findFirst(predicate)}.
     *
     * @param predicate A predicate.
     * @return Some(element) or None, where element may be null (i.e. {@code List.of(null).findFirst(e -> e == null)}).
     * @throws NullPointerException if {@code predicate} is null
     */
    Option<T> findLast(Predicate<? super T> predicate);

    @Override
    <U> TraversableOnce<U> flatMap(Function<? super T, ? extends java.lang.Iterable<? extends U>> mapper);

    @Override
    <U> TraversableOnce<U> flatten();

    /**
     * Accumulates the elements of this TraversableOnce by successively calling the given operator {@code op}.
     * <p>
     * Example: {@code List("a", "b", "c").fold("", (xs, x) -> xs + x) = "abc"}
     *
     * @param zero Value to start the accumulation with.
     * @param op   The accumulator operator.
     * @return an accumulated version of this.
     * @throws NullPointerException if {@code op} is null
     */
    default T fold(T zero, BiFunction<? super T, ? super T, ? extends T> op) {
        Objects.requireNonNull(op, "op is null");
        return foldLeft(zero, op);
    }

    /**
     * Accumulates the elements of this TraversableOnce by successively calling the given function {@code f} from the left,
     * starting with a value {@code zero} of type B.
     * <p>
     * Example: Reverse and map a TraversableOnce in one pass
     * <pre><code>
     * List.of("a", "b", "c").foldLeft(List.empty(), (xs, x) -&gt; xs.prepend(x.toUpperCase()))
     * // = List("C", "B", "A")
     * </code></pre>
     *
     * @param zero Value to start the accumulation with.
     * @param f    The accumulator function.
     * @param <U>  Result type of the accumulator.
     * @return an accumulated version of this.
     * @throws NullPointerException if {@code f} is null
     */
    default <U> U foldLeft(U zero, BiFunction<? super U, ? super T, ? extends U> f) {
        Objects.requireNonNull(f, "f is null");
        U xs = zero;
        for (T x : this) {
            xs = f.apply(xs, x);
        }
        return xs;
    }

    /**
     * Accumulates the elements of this TraversableOnce by successively calling the given function {@code f} from the right,
     * starting with a value {@code zero} of type B.
     * <p>
     * Example: {@code List.of("a", "b", "c").foldRight("", (x, xs) -> x + xs) = "abc"}
     * <p>
     * In order to prevent recursive calls, foldRight is implemented based on reverse and foldLeft. A recursive variant
     * is based on foldMap, using the monoid of function composition (endo monoid).
     * <pre>
     * <code>
     * foldRight = reverse().foldLeft(zero, (b, a) -&gt; f.apply(a, b));
     * foldRight = foldMap(Algebra.Monoid.endoMonoid(), a -&gt; b -&gt; f.apply(a, b)).apply(zero);
     * </code>
     * </pre>
     *
     * @param zero Value to start the accumulation with.
     * @param f    The accumulator function.
     * @param <U>  Result type of the accumulator.
     * @return an accumulated version of this.
     * @throws NullPointerException if {@code f} is null
     */
    <U> U foldRight(U zero, BiFunction<? super T, ? super U, ? extends U> f);

    @Override
    default T get() {
        return iterator().next();
    }

    /**
     * Groups this elements by classifying the elements.
     *
     * @param classifier A function which classifies elements into classes
     * @param <C>        classified class type
     * @return A Map containing the grouped elements
     */
    <C> Map<C, ? extends TraversableOnce<T>> groupBy(Function<? super T, ? extends C> classifier);

    /**
     * Checks if this Traversable is known to have a finite size.
     * <p>
     * This method should be implemented by classes only, i.e. not by interfaces.
     *
     * @return true, if this Traversable is known to hafe a finite size, false otherwise.
     */
    boolean hasDefiniteSize();

    /**
     * Returns the first element of a non-empty TraversableOnce.
     *
     * @return The first element of this TraversableOnce.
     * @throws NoSuchElementException if this is empty
     */
    T head();

    /**
     * Returns the first element of a non-empty TraversableOnce as {@code Option}.
     *
     * @return {@code Some(element)} or {@code None} if this is empty.
     */
    Option<T> headOption();

    /**
     * Dual of {@linkplain #tail()}, returning all elements except the last.
     *
     * @return a new instance containing all elements except the last.
     * @throws UnsupportedOperationException if this is empty
     */
    TraversableOnce<T> init();

    /**
     * Dual of {@linkplain #tailOption()}, returning all elements except the last as {@code Option}.
     *
     * @return {@code Some(traversable)} or {@code None} if this is empty.
     */
    Option<? extends TraversableOnce<T>> initOption();

    /**
     * Checks if this TraversableOnce is empty.
     *
     * @return true, if this TraversableOnce contains no elements, false otherwise.
     */
    @Override
    boolean isEmpty();

    /**
     * Each of Javaslang's collections may contain more than one element.
     *
     * @return {@code false}
     */
    @Override
    default boolean isSingletonType() {
        return false;
    }

    /**
     * Checks if this Traversable can be repeatedly traversed.
     * <p>
     * This method should be implemented by classes only, i.e. not by interfaces.
     *
     * @return true, if this Traversable is known to be traversable repeatedly, false otherwise.
     */
    boolean isTraversableAgain();

    /**
     * An iterator by means of head() and tail(). Subclasses may want to override this method.
     *
     * @return A new Iterator of this TraversableOnce elements.
     */
    @Override
    default Iterator<T> iterator() {
        final TraversableOnce<T> that = this;
        return new AbstractIterator<T>() {

            TraversableOnce<T> traversable = that;

            @Override
            public boolean hasNext() {
                return !traversable.isEmpty();
            }

            @Override
            public T next() {
                if (traversable.isEmpty()) {
                    throw new NoSuchElementException();
                } else {
                    final T result = traversable.head();
                    traversable = traversable.tail();
                    return result;
                }
            }
        };
    }

    /**
     * Dual of {@linkplain #head()}, returning the last element.
     *
     * @return the last element.
     * @throws NoSuchElementException is this is empty
     */
    default T last() {
        if (isEmpty()) {
            throw new NoSuchElementException("last of empty TraversableOnce");
        } else {
            final Iterator<T> it = iterator();
            T result = null;
            while (it.hasNext()) {
                result = it.next();
            }
            return result;
        }
    }

    /**
     * Dual of {@linkplain #headOption()}, returning the last element as {@code Opiton}.
     *
     * @return {@code Some(element)} or {@code None} if this is empty.
     */
    default Option<T> lastOption() {
        return isEmpty() ? None.instance() : new Some<>(last());
    }

    /**
     * Computes the number of elements of this.
     *
     * @return the number of elements
     */
    int length();

    /**
     * Maps the elements of this traversable to elements of a new type preserving their order, if any.
     *
     * @param mapper A mapper.
     * @param <U>    Component type of the target TraversableOnce
     * @return a mapped TraversableOnce
     * @throws NullPointerException if {@code mapper} is null
     */
    @Override
    <U> TraversableOnce<U> map(Function<? super T, ? extends U> mapper);

    /**
     * Calculates the maximum of this elements according to their natural order.
     *
     * @return {@code Some(maximum)} of this elements or {@code None} if this is empty or this elements are not comparable
     */
    @SuppressWarnings("unchecked")
    default Option<T> max() {
        final Stream<T> stream = Stream.ofAll(iterator());
        if (isEmpty() || !(stream.head() instanceof Comparable)) {
            return None.instance();
        } else {
            return stream.maxBy((o1, o2) -> ((Comparable<T>) o1).compareTo(o2));
        }
    }

    /**
     * Calculates the maximum of this elements using a specific comparator.
     *
     * @param comparator A non-null element comparator
     * @return {@code Some(maximum)} of this elements or {@code None} if this is empty
     * @throws NullPointerException if {@code comparator} is null
     */
    default Option<T> maxBy(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator, "comparator is null");
        if (isEmpty()) {
            return None.instance();
        } else {
            final T value = reduce((t1, t2) -> comparator.compare(t1, t2) >= 0 ? t1 : t2);
            return new Some<>(value);
        }
    }

    /**
     * Calculates the maximum of this elements within the co-domain of a specific function.
     *
     * @param f   A function that maps this elements to comparable elements
     * @param <U> The type where elements are compared
     * @return The element of type T which is the maximum within U
     */
    default <U extends Comparable<? super U>> Option<T> maxBy(Function<? super T, ? extends U> f) {
        Objects.requireNonNull(f, "f is null");
        if (isEmpty()) {
            return None.instance();
        } else {
            T tm = null;
            U um = null;
            for (T t : Iterator.ofAll(this)) {
                U u = f.apply(t);
                if (um == null || u.compareTo(um) >= 0) {
                    um = u;
                    tm = t;
                }
            }
            return new Some<>(tm);
        }
    }

    /**
     * Calculates the minimum of this elements according to their natural order.
     *
     * @return {@code Some(minimum)} of this elements or {@code None} if this is empty or this elements are not comparable
     */
    @SuppressWarnings("unchecked")
    default Option<T> min() {
        final Stream<T> stream = Stream.ofAll(iterator());
        if (isEmpty() || !(stream.head() instanceof Comparable)) {
            return None.instance();
        } else {
            return stream.minBy((o1, o2) -> ((Comparable<T>) o1).compareTo(o2));
        }
    }

    /**
     * Calculates the minimum of this elements using a specific comparator.
     *
     * @param comparator A non-null element comparator
     * @return {@code Some(minimum)} of this elements or {@code None} if this is empty
     * @throws NullPointerException if {@code comparator} is null
     */
    default Option<T> minBy(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator, "comparator is null");
        if (isEmpty()) {
            return None.instance();
        } else {
            final T value = reduce((t1, t2) -> comparator.compare(t1, t2) <= 0 ? t1 : t2);
            return new Some<>(value);
        }
    }

    /**
     * Calculates the minimum of this elements within the co-domain of a specific function.
     *
     * @param f   A function that maps this elements to comparable elements
     * @param <U> The type where elements are compared
     * @return The element of type T which is the minimum within U
     */
    default <U extends Comparable<? super U>> Option<T> minBy(Function<? super T, ? extends U> f) {
        Objects.requireNonNull(f, "f is null");
        if (isEmpty()) {
            return None.instance();
        } else {
            T tm = null;
            U um = null;
            for (T t : Iterator.ofAll(this)) {
                U u = f.apply(t);
                if (um == null || u.compareTo(um) <= 0) {
                    um = u;
                    tm = t;
                }
            }
            return new Some<>(tm);
        }
    }

    /**
     * Joins the elements of this by concatenating their string representations.
     * <p>
     * This has the same effect as calling {@code mkString("", "", "")}.
     *
     * @return a new String
     */
    default String mkString() {
        return mkString("", "", "");
    }

    /**
     * Joins the string representations of this elements using a specific delimiter.
     * <p>
     * This has the same effect as calling {@code mkString(delimiter, "", "")}.
     *
     * @param delimiter A delimiter string put between string representations of elements of this
     * @return A new String
     */
    default String mkString(CharSequence delimiter) {
        return mkString(delimiter, "", "");
    }

    /**
     * Joins the string representations of this elements using a specific delimiter, prefix and suffix.
     * <p>
     * Example: {@code List.of("a", "b", "c").mkString(", ", "Chars(", ")") = "Chars(a, b, c)"}
     *
     * @param delimiter A delimiter string put between string representations of elements of this
     * @param prefix    prefix of the resulting string
     * @param suffix    suffix of the resulting string
     * @return a new String
     */
    default String mkString(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        final StringBuilder builder = new StringBuilder(prefix);
        iterator().map(String::valueOf).intersperse(String.valueOf(delimiter)).forEach(builder::append);
        return builder.append(suffix).toString();
    }

    /**
     * Creates a partition of this {@code TraversableOnce} by splitting this elements in two in distinct tarversables
     * according to a predicate.
     *
     * @param predicate A predicate which classifies an element if it is in the first or the second traversable.
     * @return A disjoint union of two traversables. The first {@code TraversableOnce} contains all elements that satisfy the given {@code predicate}, the second {@code TraversableOnce} contains all elements that don't. The original order of elements is preserved.
     * @throws NullPointerException if predicate is null
     */
    Tuple2<? extends TraversableOnce<T>, ? extends TraversableOnce<T>> partition(Predicate<? super T> predicate);

    @Override
    TraversableOnce<T> peek(Consumer<? super T> action);

    /**
     * Calculates the product of this elements. Supported component types are {@code Byte}, {@code Double}, {@code Float},
     * {@code Integer}, {@code Long}, {@code Short}, {@code BigInteger} and {@code BigDecimal}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * List.empty().product()              // = 1
     * List.of(1, 2, 3).product()          // = 6
     * List.of(0.1, 0.2, 0.3).product()    // = 0.006
     * List.of("apple", "pear").product()  // throws
     * </code>
     * </pre>
     *
     * @return a {@code Number} representing the sum of this elements
     * @throws UnsupportedOperationException if this elements are not numeric
     */
    @SuppressWarnings("unchecked")
    default Number product() {
        if (isEmpty()) {
            return 1;
        } else {
            final TraversableOnce<?> objects = isTraversableAgain() ? this : toStream();
            final Object head = objects.head();
            if (head instanceof Integer || head instanceof Short || head instanceof Byte) {
                return ((TraversableOnce<Number>) objects).toJavaStream().mapToInt(Number::intValue).reduce(1,
                        (i1, i2) -> i1 * i2);
            } else if (head instanceof Double || head instanceof Float || head instanceof BigDecimal) {
                return ((TraversableOnce<Number>) objects).toJavaStream().mapToDouble(Number::doubleValue).reduce(1.0,
                        (d1, d2) -> d1 * d2);
            } else if (head instanceof Long || head instanceof BigInteger) {
                return ((TraversableOnce<Number>) objects).toJavaStream().mapToLong(Number::longValue).reduce(1L,
                        (l1, l2) -> l1 * l2);
            } else {
                throw new UnsupportedOperationException("not numeric");
            }
        }
    }

    /**
     * Accumulates the elements of this TraversableOnce by successively calling the given operation {@code op}.
     * The order of element iteration is undetermined.
     *
     * @param op A BiFunction of type T
     * @return the reduced value.
     * @throws UnsupportedOperationException if this is empty
     * @throws NullPointerException          if {@code op} is null
     */
    default T reduce(BiFunction<? super T, ? super T, ? extends T> op) {
        Objects.requireNonNull(op, "op is null");
        return reduceLeft(op);
    }

    /**
     * Accumulates the elements of this TraversableOnce by successively calling the given operation {@code op} from the left.
     *
     * @param op A BiFunction of type T
     * @return the reduced value.
     * @throws NoSuchElementException if this is empty
     * @throws NullPointerException   if {@code op} is null
     */
    default T reduceLeft(BiFunction<? super T, ? super T, ? extends T> op) {
        Objects.requireNonNull(op, "op is null");
        if (isEmpty()) {
            throw new NoSuchElementException("reduceLeft on Nil");
        } else {
            return tail().foldLeft(head(), op);
        }
    }

    /**
     * Accumulates the elements of this TraversableOnce by successively calling the given operation {@code op} from the right.
     *
     * @param op An operation of type T
     * @return the reduced value.
     * @throws NoSuchElementException if this is empty
     * @throws NullPointerException   if {@code op} is null
     */
    T reduceRight(BiFunction<? super T, ? super T, ? extends T> op);

    /**
     * Replaces the first occurrence (if exists) of the given currentElement with newElement.
     *
     * @param currentElement An element to be substituted.
     * @param newElement     A replacement for currentElement.
     * @return a TraversableOnce containing all elements of this where the first occurrence of currentElement is replaced with newELement.
     */
    TraversableOnce<T> replace(T currentElement, T newElement);

    /**
     * Replaces all occurrences of the given currentElement with newElement.
     *
     * @param currentElement An element to be substituted.
     * @param newElement     A replacement for currentElement.
     * @return a TraversableOnce containing all elements of this where all occurrences of currentElement are replaced with newELement.
     */
    TraversableOnce<T> replaceAll(T currentElement, T newElement);

    /**
     * Keeps all occurrences of the given elements from this.
     *
     * @param elements Elements to be kept.
     * @return a TraversableOnce containing all occurrences of the given elements.
     * @throws NullPointerException if {@code elements} is null
     */
    TraversableOnce<T> retainAll(java.lang.Iterable<? extends T> elements);

    /**
     * Returns a tuple where the first element is the longest prefix of elements that satisfy p and the second element is the remainder.
     *
     * @param predicate A predicate.
     * @return a Tuple containing the longest prefix of elements that satisfy p and the remainder.
     * @throws NullPointerException if {@code predicate} is null
     */
    Tuple2<? extends TraversableOnce<T>, ? extends TraversableOnce<T>> span(Predicate<? super T> predicate);

    /**
     * Calculates the sum of this elements. Supported component types are {@code Byte}, {@code Double}, {@code Float},
     * {@code Integer}, {@code Long}, {@code Short}, {@code BigInteger} and {@code BigDecimal}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * List.empty().sum()              // = 0
     * List.of(1, 2, 3).sum()          // = 6
     * List.of(0.1, 0.2, 0.3).sum()    // = 0.6
     * List.of("apple", "pear").sum()  // throws
     * </code>
     * </pre>
     *
     * @return a {@code Number} representing the sum of this elements
     * @throws UnsupportedOperationException if this elements are not numeric
     */
    @SuppressWarnings("unchecked")
    default Number sum() {
        if (isEmpty()) {
            return 0;
        } else {
            final TraversableOnce<?> objects = isTraversableAgain() ? this : toStream();
            final Object head = objects.head();
            if (head instanceof Integer || head instanceof Short || head instanceof Byte) {
                return ((TraversableOnce<Number>) objects).toJavaStream().mapToInt(Number::intValue).sum();
            } else if (head instanceof Double || head instanceof Float || head instanceof BigDecimal) {
                return ((TraversableOnce<Number>) objects).toJavaStream().mapToDouble(Number::doubleValue).sum();
            } else if (head instanceof Long || head instanceof BigInteger) {
                return ((TraversableOnce<Number>) objects).toJavaStream().mapToLong(Number::longValue).sum();
            } else {
                throw new UnsupportedOperationException("not numeric");
            }
        }
    }

    /**
     * Drops the first element of a non-empty TraversableOnce.
     *
     * @return A new instance of TraversableOnce containing all elements except the first.
     * @throws UnsupportedOperationException if this is empty
     */
    TraversableOnce<T> tail();

    /**
     * Drops the first element of a non-empty TraversableOnce and returns an {@code Option}.
     *
     * @return {@code Some(traversable)} or {@code None} if this is empty.
     */
    Option<? extends TraversableOnce<T>> tailOption();

    /**
     * Takes the first n elements of this or all elements, if this length &lt; n.
     * <p>
     * The result is equivalent to {@code sublist(0, max(0, min(length(), n)))} but does not throw if {@code n < 0} or
     * {@code n > length()}.
     * <p>
     * In the case of {@code n < 0} the empty instance is returned, in the case of {@code n > length()} this is returned.
     *
     * @param n The number of elements to take.
     * @return A new instance consisting the first n elements of this or all elements, if this has less than n elements.
     */
    TraversableOnce<T> take(int n);

    /**
     * Takes the last n elements of this or all elements, if this length &lt; n.
     * <p>
     * The result is equivalent to {@code sublist(max(0, min(length(), length() - n)), n)}, i.e. takeRight will not
     * throw if {@code n < 0} or {@code n > length()}.
     * <p>
     * In the case of {@code n < 0} the empty instance is returned, in the case of {@code n > length()} this is returned.
     *
     * @param n The number of elements to take.
     * @return A new instance consisting the first n elements of this or all elements, if this has less than n elements.
     */
    TraversableOnce<T> takeRight(int n);

    /**
     * Takes elements until the predicate holds for the current element.
     * <p>
     * Note: This is essentially the same as {@code takeWhile(predicate.negate())}. It is intended to be used with
     * method references, which cannot be negated directly.
     *
     * @param predicate A condition tested subsequently for this elements.
     * @return a new instance consisting of all elements until the first which does satisfy the given predicate.
     * @throws NullPointerException if {@code predicate} is null
     */
    TraversableOnce<T> takeUntil(Predicate<? super T> predicate);

    /**
     * Takes elements while the predicate holds for the current element.
     *
     * @param predicate A condition tested subsequently for the contained elements.
     * @return a new instance consisting of all elements until the first which does not satisfy the given predicate.
     * @throws NullPointerException if {@code predicate} is null
     */
    TraversableOnce<T> takeWhile(Predicate<? super T> predicate);

    /**
     * Unzips this elements by mapping this elements to pairs which are subsequentially split into two distinct
     * sets.
     *
     * @param unzipper a function which converts elements of this to pairs
     * @param <T1>     1st element type of a pair returned by unzipper
     * @param <T2>     2nd element type of a pair returned by unzipper
     * @return A pair of set containing elements split by unzipper
     * @throws NullPointerException if {@code unzipper} is null
     */
    <T1, T2> Tuple2<? extends TraversableOnce<T1>, ? extends TraversableOnce<T2>> unzip(
            Function<? super T, Tuple2<? extends T1, ? extends T2>> unzipper);

    /**
     * Returns a traversable formed from this traversable and another java.lang.Iterable collection by combining
     * corresponding elements in pairs. If one of the two iterables is longer than the other, its remaining elements
     * are ignored.
     * <p>
     * The length of the returned traversable is the minimum of the lengths of this traversable and {@code that}
     * iterable.
     *
     * @param <U>  The type of the second half of the returned pairs.
     * @param that The java.lang.Iterable providing the second half of each result pair.
     * @return a new traversable containing pairs consisting of corresponding elements of this traversable and {@code that} iterable.
     * @throws NullPointerException if {@code that} is null
     */
    <U> TraversableOnce<Tuple2<T, U>> zip(java.lang.Iterable<U> that);

    /**
     * Returns a traversable formed from this traversable and another java.lang.Iterable by combining corresponding elements in
     * pairs. If one of the two collections is shorter than the other, placeholder elements are used to extend the
     * shorter collection to the length of the longer.
     * <p>
     * The length of the returned traversable is the maximum of the lengths of this traversable and {@code that}
     * iterable.
     * <p>
     * Special case: if this traversable is shorter than that elements, and that elements contains duplicates, the
     * resulting traversable may be shorter than the maximum of the lengths of this and that because a traversable
     * contains an element at most once.
     * <p>
     * If this Traversable is shorter than that, thisElem values are used to fill the result.
     * If that is shorter than this Traversable, thatElem values are used to fill the result.
     *
     * @param <U>      The type of the second half of the returned pairs.
     * @param that     The java.lang.Iterable providing the second half of each result pair.
     * @param thisElem The element to be used to fill up the result if this traversable is shorter than that.
     * @param thatElem The element to be used to fill up the result if that is shorter than this traversable.
     * @return A new traversable containing pairs consisting of corresponding elements of this traversable and that.
     * @throws NullPointerException if {@code that} is null
     */
    <U> TraversableOnce<Tuple2<T, U>> zipAll(java.lang.Iterable<U> that, T thisElem, U thatElem);

    /**
     * Zips this traversable with its indices.
     *
     * @return A new traversable containing all elements of this traversable paired with their index, starting with 0.
     */
    TraversableOnce<Tuple2<T, Integer>> zipWithIndex();
}
