package com.example.code_doc.repo;

import com.example.code_doc.model.JobFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobFileRepo extends JpaRepository<JobFile, String> {
  @Query("SELECT jf FROM JobFile jf " +
      "WHERE jf.job.id = :job_id")
  List<JobFile> findAllByJobId(@Param("job_id") String jobId);

  @Query("SELECT jf FROM JobFile jf " +
      "LEFT JOIN FETCH jf.methods fm " +
      "WHERE jf.id = :job_id")
  Optional<JobFile> getJobFileWithMethods(@Param("job_id") String id);

  @Query("SELECT jf FROM JobFile jf " +
      "LEFT JOIN FETCH jf.methods fm " +
      "LEFT JOIN FETCH fm.comments " +
      "WHERE jf.id = :job_id")
  Optional<JobFile> getComplete(@Param("job_id") String jobFileId);

  @Query("SELECT COUNT(fm.status) = 0 " +
      "FROM JobFile jf " +
      "JOIN FileMethod fm " +
      "ON jf.id = fm.jobFile.id " +
      "WHERE jf.id = :jobFileId " +
      "AND fm.status != 'COMPLETED'")
  boolean allMethodsProcessed(String jobFileId);
}
