package com.jobtracker.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import com.jobtracker.dto.ApplicationRequest;
import com.jobtracker.model.Application;
import com.jobtracker.model.ApplicationStatus;
import com.jobtracker.security.JwtUtil;
import com.jobtracker.service.ApplicationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationService service;
    private final JwtUtil jwtUtil;

    public ApplicationController(ApplicationService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public Application create(@Valid @RequestBody ApplicationRequest req, HttpServletRequest request) {

        Application app = new Application();
        app.setCompanyName(req.getCompanyName());
        app.setWebsite(req.getWebsite());
        app.setDateApplied(LocalDate.now());
        app.setTimeApplied(LocalTime.now());

        ApplicationStatus status = (req.getStatus() == null || req.getStatus().isBlank())
        ? ApplicationStatus.APPLIED: ApplicationStatus.valueOf(req.getStatus().trim().toUpperCase());

        app.setStatus(status);
        app.setNotes(req.getNotes());

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = header.substring(7);
        Long userId = jwtUtil.extractUserId(token);

        app.setUserId(userId);

        return service.create(app);
    }

    @GetMapping
    public List<Application> getAll(HttpServletRequest request) {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = header.substring(7);
        Long userId = jwtUtil.extractUserId(token);

        return service.getAllByUserId(userId);
    }

    @PatchMapping("/{id}/status")
    public Application updateStatus(@PathVariable Long id,@RequestParam String status,HttpServletRequest request) {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = header.substring(7);
        Long userId = jwtUtil.extractUserId(token);

        ApplicationStatus newStatus = ApplicationStatus.valueOf(status.trim().toUpperCase());

        return service.updateStatusForUser(id, userId, newStatus);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, HttpServletRequest request) {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = header.substring(7);
        Long userId = jwtUtil.extractUserId(token);

        service.deleteForUser(id, userId);
    }
}
