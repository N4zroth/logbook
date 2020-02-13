package org.zalando.logbook;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.zalando.logbook.HttpHeaders.empty;

final class HeaderFilterTest {

    @Test
    void noneShouldDefaultToNoOp() {
        final HeaderFilter unit = HeaderFilter.none();

        final HttpHeaders headers = empty().update("Authorization", "Bearer s3cr3t");
        assertThat(unit.filter(headers), is(sameInstance(headers)));
    }

}
