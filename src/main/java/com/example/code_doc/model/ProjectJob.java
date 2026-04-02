package com.example.code_doc.model;

import com.example.code_doc.dto.JobStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Set;


@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectJob {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;
  private String folderPath; // projectPath

  @Enumerated(EnumType.STRING)
  private JobStatus status;

  private Instant createdAt;

  @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Set<JobFile> jobFiles;
}

