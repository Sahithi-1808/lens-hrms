package com.lens.hrms.controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
public class HealthController {
    @GetMapping("/")
    public Map<String,String> root() { return Map.of("message","Lens Spring HRMS API"); }
    @GetMapping("/health")
    public Map<String,String> health() { return Map.of("status","healthy"); }
}
