package com.example.code_doc.repo;

import com.example.code_doc.model.ProjectJob;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ProjectJobRepo extends CrudRepository<ProjectJob, String> {
  @Query("SELECT COUNT(jf.status) = 0 " +
      "FROM ProjectJob pj " +
      "JOIN JobFile jf " +
      "ON pj.id = jf.job.id " +
      "WHERE pj.id = :job_id " +
      "AND jf.status != 'COMPLETED'")
  boolean allFilesProcessed(@Param("job_id") String jobId);
}
