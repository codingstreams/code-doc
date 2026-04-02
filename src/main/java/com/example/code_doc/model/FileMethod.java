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
public class FileMethod {
  @Id
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_file_id")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private JobFile jobFile;

  private String methodName;
  private String methodHash;

  @Enumerated(EnumType.STRING)
  private JobStatus status;

  @OneToMany(mappedBy = "fileMethod", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Set<MethodComment> comments;
}
