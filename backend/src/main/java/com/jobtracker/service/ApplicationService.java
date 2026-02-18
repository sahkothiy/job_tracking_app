package com.jobtracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jobtracker.model.Application;
import com.jobtracker.model.ApplicationStatus;
import com.jobtracker.repository.ApplicationRepository;

@Service
public class ApplicationService {

    private final ApplicationRepository repo;

    public ApplicationService(ApplicationRepository repo) {
        this.repo = repo;
    }

    public Application create(Application app) {
        return repo.save(app);
    }

    public List<Application> getAllByUserId(Long userId) {
        return repo.findByUserId(userId);
    }

    public Application updateStatusForUser(Long id, Long userId, ApplicationStatus status) {
        Application app = repo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        app.setStatus(status);
        return repo.save(app);
    }

    public void deleteForUser(Long id, Long userId) {
        repo.deleteByIdAndUserId(id, userId);
    }
}
