package org.zalando.logbook;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.zalando.fauxpas.FauxPas.throwingFunction;
import static org.zalando.logbook.HttpHeaders.empty;
import static org.zalando.logbook.Origin.REMOTE;

final class MockHttpResponseTest implements MockHttpMessageTester {

    private final MockHttpResponse unit = MockHttpResponse.create();

    @Test
    void shouldDelegate() throws IOException {
        verifyResponse(unit);

        assertThat(unit.getBody(), is("".getBytes(UTF_8)));
        assertThat(unit.getBodyAsString(), is(emptyString()));
    }

    @Test
    void shouldSupportWith() {
        assertWith(unit, MockHttpResponse::withProtocolVersion, "HTTP/2", HttpResponse::getProtocolVersion);
        assertWith(unit, MockHttpResponse::withOrigin, REMOTE, HttpResponse::getOrigin);
        assertWith(unit, MockHttpResponse::withStatus, 404, HttpResponse::getStatus);
        assertWith(unit, MockHttpResponse::withHeaders, empty().update("Accept", "text/plain"), HttpResponse::getHeaders);
        assertWith(unit, MockHttpResponse::withContentType, "text/xml", HttpResponse::getContentType);
        assertWith(unit, MockHttpResponse::withCharset, ISO_8859_1, HttpResponse::getCharset);
        assertWith(unit, MockHttpResponse::withBodyAsString, "Hello", throwingFunction(HttpResponse::getBodyAsString));
    }

    @Test
    void shouldSupportWithBody() throws IOException {
        assertThat(unit.withBodyAsString("Hello").withBody().getBodyAsString(), is("Hello"));
    }

    @Test
    void shouldSupportWithoutBody() {
        unit.withoutBody();

        assertThat(unit.getBodyAsString(), is(emptyString()));
    }

}
