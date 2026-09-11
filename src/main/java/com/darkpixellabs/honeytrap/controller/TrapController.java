package com.darkpixellabs.honeytrap.controller;

import com.darkpixellabs.honeytrap.service.HitLoggingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class TrapController {
    private final HitLoggingService logger;
    private static final Set<String> SCANNER_MARKERS=Set.of(".php",".asp",".aspx","wp-","xmlrpc.php","phpmyadmin","cgi-bin","actuator","server-status",".env",".git/");
    public TrapController(HitLoggingService logger){this.logger=logger;}
    @GetMapping(value="/wp-login.php",produces=MediaType.TEXT_HTML_VALUE) public ResponseEntity<String> wpGet(HttpServletRequest r){logger.log(r);return html("WordPress Login","Invalid credentials");}
    @PostMapping(value="/wp-login.php",produces=MediaType.TEXT_HTML_VALUE) public ResponseEntity<String> wpPost(HttpServletRequest r){logger.log(r);return html("WordPress Login","Invalid credentials");}
    @RequestMapping(value="/wp-admin/**",method={RequestMethod.GET,RequestMethod.POST},produces=MediaType.TEXT_HTML_VALUE) public ResponseEntity<String> wpAdmin(HttpServletRequest r){logger.log(r);return html("WordPress Admin","Invalid credentials");}
    @GetMapping(value="/.env",produces=MediaType.TEXT_PLAIN_VALUE) public String env(HttpServletRequest r){logger.log(r);return "# HONEYTRAP FAKE CONFIG\nAPP_NAME=honeytrap-demo\nDB_HOST=placeholder.invalid\nDB_PASSWORD=NOT_A_REAL_PASSWORD\nSECRET_KEY=DEMO_ONLY_NOT_SECRET\n";}
    @GetMapping(value="/.git/config",produces=MediaType.TEXT_PLAIN_VALUE) public String gitConfig(HttpServletRequest r){logger.log(r);return "[core]\n\trepositoryformatversion = 0\n\tbare = false\n[remote \"origin\"]\n\turl = https://example.invalid/honeytrap-demo.git\n";}
    @GetMapping(value="/phpmyadmin/**",produces=MediaType.TEXT_HTML_VALUE) public ResponseEntity<String> phpMyAdmin(HttpServletRequest r){logger.log(r);return html("phpMyAdmin","Sign in to continue");}
    @GetMapping(value="/admin/login",produces=MediaType.TEXT_HTML_VALUE) public ResponseEntity<String> adminGet(HttpServletRequest r){logger.log(r);return html("Admin Portal","Invalid credentials");}
    @PostMapping(value="/admin/login",produces=MediaType.TEXT_HTML_VALUE) public ResponseEntity<String> adminPost(HttpServletRequest r){logger.log(r);return html("Admin Portal","Invalid credentials");}
    @GetMapping(value="/api/v1/users",produces=MediaType.APPLICATION_JSON_VALUE) public List<Map<String,Object>> users(HttpServletRequest r){logger.log(r);return List.of(Map.of("id",1001,"username","demo-user","email","demo@example.invalid"),Map.of("id",1002,"username","test-account","email","test@example.invalid"));}
    @RequestMapping(value={"/shell.php","/cmd.php"},method={RequestMethod.GET,RequestMethod.POST}) public ResponseEntity<Void> shell(HttpServletRequest r){logger.log(r);return ResponseEntity.status(HttpStatus.NOT_FOUND).build();}
    @GetMapping(value="/{*path}") public ResponseEntity<Void> scannerCatchAll(HttpServletRequest r){String p=r.getRequestURI().toLowerCase(Locale.ROOT);if(SCANNER_MARKERS.stream().anyMatch(p::contains))logger.log(r);return ResponseEntity.status(HttpStatus.NOT_FOUND).build();}
    private ResponseEntity<String> html(String title,String message){return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body("<!doctype html><html><head><meta charset=\"utf-8\"><title>"+title+"</title></head><body><h1>"+title+"</h1><p>"+message+".</p><form method=\"post\"><input name=\"username\" autocomplete=\"username\"><input type=\"password\" name=\"password\"><button>Log In</button></form></body></html>");}
}
