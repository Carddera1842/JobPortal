package com.pluralsight.jobportal.service;

import com.pluralsight.jobportal.model.Application;
import com.pluralsight.jobportal.model.Job;
import com.pluralsight.jobportal.model.User;
import com.pluralsight.jobportal.repository.ApplicationRepository;
import com.pluralsight.jobportal.repository.JobRepository;
import com.pluralsight.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    public Application applyForJob(Long userId, Long jobId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not Found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        Application application = new Application(user, job);

        return applicationRepository.save(application);
    }
}
