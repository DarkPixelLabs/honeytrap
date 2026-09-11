package com.darkpixellabs.honeytrap.controller;
import org.springframework.core.io.ClassPathResource; import org.springframework.core.io.Resource; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController public class DashboardPageController {
 @GetMapping({"/","/index.html"}) public ResponseEntity<Resource> index(){return resource("static/index.html",MediaType.TEXT_HTML);}
 @GetMapping("/style.css") public ResponseEntity<Resource> css(){return resource("static/style.css",MediaType.valueOf("text/css"));}
 @GetMapping("/app.js") public ResponseEntity<Resource> app(){return resource("static/app.js",MediaType.valueOf("application/javascript"));}
 @GetMapping("/chart.js") public ResponseEntity<Resource> chart(){return resource("static/chart.js",MediaType.valueOf("application/javascript"));}
 private ResponseEntity<Resource> resource(String path,MediaType type){Resource r=new ClassPathResource(path);return ResponseEntity.ok().contentType(type).body(r);}
}
