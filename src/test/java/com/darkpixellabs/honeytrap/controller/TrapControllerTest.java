package com.darkpixellabs.honeytrap.controller;

import com.darkpixellabs.honeytrap.service.HitLoggingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrapController.class)
class TrapControllerTest {
 @Autowired MockMvc mvc; @MockBean HitLoggingService logger;
 @Test void envLogsAndReturnsFakeConfig() throws Exception {mvc.perform(get("/.env")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("NOT_A_REAL_PASSWORD")));verify(logger).log(org.mockito.ArgumentMatchers.any());}
 @Test void adminPostLogsAndFails() throws Exception {mvc.perform(post("/admin/login").content("username=admin&password=x")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("Invalid credentials")));verify(logger).log(org.mockito.ArgumentMatchers.any());}
 @Test void wpLoginLogs() throws Exception {mvc.perform(get("/wp-login.php")).andExpect(status().isOk());verify(logger).log(org.mockito.ArgumentMatchers.any());}
 @Test void gitConfigIsFake() throws Exception {mvc.perform(get("/.git/config")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("example.invalid")));verify(logger).log(org.mockito.ArgumentMatchers.any());}
 @Test void shellNeverExecutes() throws Exception {mvc.perform(post("/shell.php").content("rm -rf /" )).andExpect(status().isNotFound());verify(logger).log(org.mockito.ArgumentMatchers.any());}
 @Test void usersAreObviouslyFake() throws Exception {mvc.perform(get("/api/v1/users")).andExpect(status().isOk()).andExpect(jsonPath("$[0].email").value("demo@example.invalid"));verify(logger).log(org.mockito.ArgumentMatchers.any());}
}
