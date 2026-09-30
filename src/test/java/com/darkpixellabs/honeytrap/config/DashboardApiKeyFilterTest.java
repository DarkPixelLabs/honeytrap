package com.darkpixellabs.honeytrap.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class DashboardApiKeyFilterTest {
    DashboardApiKeyFilter filter;
    MockHttpServletRequest req;
    MockHttpServletResponse res;
    MockFilterChain chain;

    @BeforeEach
    void setUp() {
        filter = new DashboardApiKeyFilter();
        filter.configured = "s3cret";
        req = new MockHttpServletRequest();
        res = new MockHttpServletResponse();
        chain = new MockFilterChain();
    }

    void run(String uri, String key) throws Exception {
        req.setRequestURI(uri);
        if (key != null) req.addHeader("X-HoneyTrap-Api-Key", key);
        filter.doFilter(req, res, chain);
    }

    @Test
    void disabledLetsDashboardThroughWithoutKey() throws Exception {
        filter.enabled = false;
        run("/api/stats/summary", null);
        assertNotNull(chain.getRequest());
        assertEquals(200, res.getStatus());
    }

    @Test
    void correctKeyPassesWhenEnabled() throws Exception {
        filter.enabled = true;
        run("/api/stats/summary", "s3cret");
        assertNotNull(chain.getRequest());
        assertEquals(200, res.getStatus());
    }

    @Test
    void missingKeyRejectedWith401() throws Exception {
        filter.enabled = true;
        run("/api/stats/summary", null);
        assertNull(chain.getRequest());
        assertEquals(401, res.getStatus());
        assertTrue(res.getContentAsString().contains("dashboard API key required"));
    }

    @Test
    void wrongKeyRejectedWith401() throws Exception {
        filter.enabled = true;
        run("/api/hits", "nope");
        assertNull(chain.getRequest());
        assertEquals(401, res.getStatus());
    }

    @Test
    void hitDetailCoveredWhenEnabled() throws Exception {
        filter.enabled = true;
        run("/api/hits/9", null);
        assertNull(chain.getRequest());
        assertEquals(401, res.getStatus());
    }

    @Test
    void trapPathSkippedWhenEnabled() throws Exception {
        filter.enabled = true;
        run("/wp-login.php", null);
        assertNotNull(chain.getRequest());
        assertEquals(200, res.getStatus());
    }
}
