package io.vavr.control;

import io.vavr.AbstractValueTest;

import java.util.NoSuchElementException;
import java.util.function.Supplier;
import org.junit.Test;

import static io.vavr.API.*;

// TODO: dublicate all tests to Option, Either, Try, Validation
public abstract class AbstractControlTest extends AbstractValueTest {

    // -- get()

    @Test(expected = NoSuchElementException.class)
    public void shouldGetEmpty() {
        empty().get();
    }

    @Test
    public void shouldGetNonEmpty() {
        assertThat(of(1).get()).isEqualTo(1);
    }

    // -- getOrElse(T)

    @Test
    public void shouldCalculateGetOrElseWithNull() {
        assertThat(this.<Integer> empty().getOrElse((Integer) null)).isEqualTo(null);
        assertThat(of(1).getOrElse((Integer) null)).isEqualTo(1);
    }

    @Test
    public void shouldCalculateGetOrElseWithNonNull() {
        assertThat(empty().getOrElse(1)).isEqualTo(1);
        assertThat(of(1).getOrElse(2)).isEqualTo(1);
    }

    // -- getOrElse(Supplier)

    @Test(expected = NullPointerException.class)
    public void shouldThrowOnGetOrElseWithNullSupplier() {
        final Supplier<?> supplier = null;
        empty().getOrElse(supplier);
    }

    @Test
    public void shouldCalculateGetOrElseWithSupplier() {
        assertThat(empty().getOrElse(() -> 1)).isEqualTo(1);
        assertThat(of(1).getOrElse(() -> 2)).isEqualTo(1);
    }

    // -- getOrElseThrow

    @Test(expected = ArithmeticException.class)
    public void shouldThrowOnGetOrElseThrowIfEmpty() {
        empty().getOrElseThrow(ArithmeticException::new);
    }

    @Test
    public void shouldNotThrowOnGetOrElseThrowIfNonEmpty() {
        assertThat(of(1).getOrElseThrow(ArithmeticException::new)).isEqualTo(1);
    }

    // -- getOrElseTry

    @Test
    public void shouldReturnUnderlyingValueWhenCallingGetOrElseTryOnNonEmptyValue() {
        assertThat(of(1).getOrElseTry(() -> 2)).isEqualTo(1);
    }

    @Test
    public void shouldReturnAlternateValueWhenCallingGetOrElseTryOnEmptyValue() {
        assertThat(empty().getOrElseTry(() -> 2)).isEqualTo(2);
    }

    @Test(expected = Error.class)
    public void shouldThrowWhenCallingGetOrElseTryOnEmptyValueAndTryIsAFailure() {
        empty().getOrElseTry(() -> {
            throw new Error();
        });
    }

    // -- getOrNull

    @Test
    public void shouldReturnNullWhenGetOrNullOfEmpty() {
        assertThat(empty().getOrNull()).isEqualTo(null);
    }

    @Test
    public void shouldReturnValueWhenGetOrNullOfNonEmpty() {
        assertThat(of(1).getOrNull()).isEqualTo(1);
    }

    // -- isEmpty

    @Test
    public void shouldCalculateIsEmpty() {
        assertThat(empty().isEmpty()).isTrue();
        assertThat(of(1).isEmpty()).isFalse();
    }

    // -- Conversions toXxx()

    @Test
    public void shouldConvertToOption() {
        assertThat(empty().toOption()).isSameAs(Option.none());
        assertThat(of(1).toOption()).isEqualTo(Option.of(1));
    }

    @Test
    public void shouldConvertToEither() {
        assertThat(empty().toEither("test")).isEqualTo(Left("test"));
        assertThat(empty().toEither(() -> "test")).isEqualTo(Left("test"));
        assertThat(of(1).toEither("test")).isEqualTo(Right(1));
    }

    @Test
    public void shouldConvertToValidation() {
        assertThat(empty().toValidation("test")).isEqualTo(Invalid("test"));
        assertThat(empty().toValidation(() -> "test")).isEqualTo(Invalid("test"));
        assertThat(of(1).toValidation("test")).isEqualTo(Valid(1));
    }

    @Test
    public void shouldConvertNonEmptyToTry() {
        assertThat(of(1, 2, 3).toTry()).isEqualTo(Try.of(() -> 1));
    }

    @Test
    public void shouldConvertEmptyToTry() {
        final Try<?> actual = empty().toTry();
        assertThat(actual.isFailure()).isTrue();
        assertThat(actual.getCause()).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void shouldConvertNonEmptyToTryUsingExceptionSupplier() {
        final Exception x = new Exception("test");
        assertThat(of(1, 2, 3).toTry(() -> x)).isEqualTo(Try.of(() -> 1));
    }

    @Test
    public void shouldConvertEmptyToTryUsingExceptionSupplier() {
        final Exception x = new Exception("test");
        assertThat(empty().toTry(() -> x)).isEqualTo(Try.failure(x));
    }

    // toLeft / toRight

    @Test
    public void shouldConvertToEitherLeftFromValueSupplier() {
        final Either<Integer, String> either = of(0).toLeft(() -> "fallback");
        assertThat(either.isLeft()).isTrue();
        assertThat(either.getLeft()).isEqualTo(0);

        final Either<Object, String> either2 = empty().toLeft(() -> "fallback");
        assertThat(either2.isRight()).isTrue();
        assertThat(either2.get()).isEqualTo("fallback");
    }

    @Test
    public void shouldConvertToEitherLeftFromValue() {
        final Either<Integer, String> either = of(0).toLeft("fallback");
        assertThat(either.isLeft()).isTrue();
        assertThat(either.getLeft()).isEqualTo(0);

        final Either<Object, String> either2 = empty().toLeft("fallback");
        assertThat(either2.isRight()).isTrue();
        assertThat(either2.get()).isEqualTo("fallback");
    }

    @Test
    public void shouldConvertToEitherRightFromValueSupplier() {
        final Either<String, Integer> either = of(0).toRight(() -> "fallback");
        assertThat(either.isRight()).isTrue();
        assertThat(either.get()).isEqualTo(0);

        final Either<String, Object> either2 = empty().toRight(() -> "fallback");
        assertThat(either2.isLeft()).isTrue();
        assertThat(either2.getLeft()).isEqualTo("fallback");
    }

    @Test
    public void shouldConvertToEitherRightFromValue() {
        final Either<String, Integer> either = of(0).toRight("fallback");
        assertThat(either.isRight()).isTrue();
        assertThat(either.get()).isEqualTo(0);

        final Either<String, Object> either2 = empty().toRight("fallback");
        assertThat(either2.isLeft()).isTrue();
        assertThat(either2.getLeft()).isEqualTo("fallback");
    }

    // toValid / toInvalid

    @Test
    public void shouldConvertToValidationInvalidFromValueSupplier() {
        final Validation<Integer, String> validation = of(0).toInvalid(() -> "fallback");
        assertThat(validation.isInvalid()).isTrue();
        assertThat(validation.getError()).isEqualTo(0);

        final Validation<Object, String> validation2 = empty().toInvalid(() -> "fallback");
        assertThat(validation2.isValid()).isTrue();
        assertThat(validation2.get()).isEqualTo("fallback");
    }

    @Test
    public void shouldConvertToValidationInvalidFromValue() {
        final Validation<Integer, String> validation = of(0).toInvalid("fallback");
        assertThat(validation.isInvalid()).isTrue();
        assertThat(validation.getError()).isEqualTo(0);

        final Validation<Object, String> validation2 = empty().toInvalid("fallback");
        assertThat(validation2.isValid()).isTrue();
        assertThat(validation2.get()).isEqualTo("fallback");
    }

    @Test
    public void shouldConvertToValidationRightFromValueSupplier() {
        final Validation<String, Integer> validation = of(0).toValid(() -> "fallback");
        assertThat(validation.isValid()).isTrue();
        assertThat(validation.get()).isEqualTo(0);

        final Validation<String, Object> validation2 = empty().toValid(() -> "fallback");
        assertThat(validation2.isInvalid()).isTrue();
        assertThat(validation2.getError()).isEqualTo("fallback");
    }

    @Test
    public void shouldConvertToValidationValidFromValue() {
        final Validation<String, Integer> validation = of(0).toValid("fallback");
        assertThat(validation.isValid()).isTrue();
        assertThat(validation.get()).isEqualTo(0);

        final Validation<String, Object> validation2 = empty().toValid("fallback");
        assertThat(validation2.isInvalid()).isTrue();
        assertThat(validation2.getError()).isEqualTo("fallback");
    }

}
