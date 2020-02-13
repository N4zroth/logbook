package org.zalando.logbook;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.UnaryOperator;

import static java.util.Collections.singleton;
import static org.zalando.logbook.Fold.fold;

interface BaseHttpHeaders extends HttpHeaders {

    @Override
    default HttpHeaders update(
            final String name,
            final String... values) {

        return update(name, Arrays.asList(values));
    }

    @Override
    default HttpHeaders update(
            final Map<String, List<String>> headers) {

        final HttpHeaders self = this;
        return fold(headers.entrySet(), self, (result, entry) -> {
            final String name = entry.getKey();
            final List<String> values = entry.getValue();
            return result.update(name, values);
        });
    }

    @Override
    default HttpHeaders apply(
            final String name,
            final UnaryOperator<List<String>> operator) {
        return apply(singleton(name), (ignored, previous) ->
                operator.apply(previous));
    }

    @Override
    default HttpHeaders apply(
            final Collection<String> names,
            final BiFunction<String, List<String>, Collection<String>> operator) {

        final HttpHeaders self = this;
        return fold(names, self, (result, name) -> {
            final List<String> previous = get(name);
            @Nullable final Collection<String> next = operator.apply(name, previous);

            return next == null ?
                    result.delete(name) :
                    result.update(name, next);
        });
    }

    @Override
    default HttpHeaders apply(
            final BiPredicate<String, List<String>> predicate,
            final BiFunction<String, List<String>, Collection<String>> operator) {

        return apply((name, previous) -> {
            if (predicate.test(name, previous)) {
                return operator.apply(name, previous);
            }
            return previous;
        });
    }

    @Override
    default HttpHeaders apply(
            final BiFunction<String, List<String>, Collection<String>> operator) {

        final HttpHeaders self = this;
        return fold(entrySet(), self, (result, entry) -> {
            final String name = entry.getKey();
            final List<String> previous = entry.getValue();
            @Nullable final Collection<String> next = operator.apply(name, previous);
            return next == null ?
                    result.delete(name) :
                    result.update(name, next);
        });
    }

    @Override
    default HttpHeaders delete(final String... names) {
        return delete(Arrays.asList(names));
    }

    @Override
    default HttpHeaders delete(
            final BiPredicate<String, List<String>> predicate) {
        return apply(predicate, (name, previous) -> null);
    }

}
