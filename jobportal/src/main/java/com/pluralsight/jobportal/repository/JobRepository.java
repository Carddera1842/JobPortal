package com.pluralsight.jobportal.repository;

import com.pluralsight.jobportal.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}
