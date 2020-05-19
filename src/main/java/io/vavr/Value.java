/* ____  ______________  ________________________  __________
 * \   \/   /      \   \/   /   __/   /      \   \/   /      \
 *  \______/___/\___\______/___/_____/___/\___\______/___/\___\
 *
 * Copyright 2020 Vavr, http://vavr.io
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
package io.vavr;

import io.vavr.collection.Iterator;

/**
 * {@code Value} is the base type of all values in Vavr. It offers a single method, {@link #iterator()},
 * that returns a rich {@link io.vavr.collection.Iterator}.
 * <p>
 * In a functional setting, values play an integral role. Programs can be seen as transformations of values.
 * A pure application just has an input and produces an output. However, a meaningful application will
 * mutate the outer world, like writing to logs or to database tables.
 * <p>
 * Vavr's implementations of {@code Value} help us to focus on computations resp. business logic. Values
 * encode aspects of everyday programming by encapsulating the program state for further transformation.
 * This kind of high-level programming increases the overall readability of our programs, given that we
 * know the basic vocabulary of available types and transformations.
 * <p>
 * {@code Value}s are wrappers that might be stateful, like {@link io.vavr.concurrent.Future}.
 *
 * @param <T> Element type
 */
public interface Value<T> extends Iterable<T> {

    /**
     * Narrows a widened {@code Value<? extends T>} to {@code Value<T>}
     * by performing a type-safe cast. This is eligible because immutable/read-only
     * types are covariant.
     *
     * @param value A {@code Value}.
     * @param <T>   Component type of the {@code Value}.
     * @return the given {@code value} instance as narrowed type {@code Value<T>}.
     */
    @SuppressWarnings("unchecked")
    static <T> Value<T> narrow(Value<? extends T> value) {
        return (Value<T>) value;
    }

    /**
     * Returns a rich {@code io.vavr.collection.Iterator}.
     *
     * @return A new Iterator
     */
    @Override
    Iterator<T> iterator();

}
