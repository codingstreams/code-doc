package com.example.code_doc.model;

import com.example.code_doc.dto.JobStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobFile {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String path;

  @Enumerated(EnumType.STRING)
  private JobStatus status;

  @ManyToOne
  @JoinColumn(name = "job_id")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private ProjectJob job;

  @OneToMany(mappedBy = "jobFile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Set<FileMethod> methods;
}
