package com.example.code_doc.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MethodComment {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_method_id")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private FileMethod fileMethod;

  private String expressionHash;

  @Column(columnDefinition = "TEXT")
  private String commentContent;
}