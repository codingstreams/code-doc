package com.example.code_doc.service.git;

import com.example.code_doc.event.GitEnvReadyEvent;
import com.example.code_doc.event.ProjectEvent;
import com.example.code_doc.event.ProjectJobFailedEvent;
import com.example.code_doc.service.projectjob.ProjectJobService;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class JGitService implements GitService {
  private static final String BRANCH_NAME = "ai-comments";
  private final ProjectJobService projectJobService;
  private final ApplicationEventPublisher eventPublisher;

  private static void createAiCommentBranch(Git git) throws GitAPIException {
    git.checkout()
        .setCreateBranch(true)
        .setName(JGitService.BRANCH_NAME)
        .call();
  }

  private static boolean isAiCommentBranchExists(Git git) throws GitAPIException {
    return git.branchList()
        .call()
        .stream()
        .anyMatch(e -> e.getName()
            .contains(JGitService.BRANCH_NAME));
  }


  private static void makeCommit(Git git,
                                 String commitMessage) throws GitAPIException {
    git.commit()
        .setMessage(commitMessage)
        .call();
  }

  @Override
  public void prepareGitEnv(String jobId) {
    final var projectJob = projectJobService.get(jobId);
    final var folderPath = projectJob.getFolderPath();

    final var file = new File(folderPath);
    ProjectEvent event;

    try {
      final var git = Git.open(file);

      if (!isAiCommentBranchExists(git)) {
        createAiCommentBranch(git);
      }

      event = new GitEnvReadyEvent(jobId, "Switched to new branch: %s".formatted(BRANCH_NAME));
    } catch (RepositoryNotFoundException _) {
      try {
        final var git = Git.init()
            .setDirectory(file)
            .call();

        git.add()
            .addFilepattern(".")
            .call();

        makeCommit(git, "Initial commit (Auto-generated)");
        createAiCommentBranch(git);

        event = new GitEnvReadyEvent(jobId, "Git initialized and branch created.");
      } catch (GitAPIException e) {
        event = new ProjectJobFailedEvent(jobId, "Git Setup Failed: " + e.getMessage());
      }
    } catch (Exception e) {
      event = new ProjectJobFailedEvent(jobId, "Git Setup Failed: " + e.getMessage());
    }

    eventPublisher.publishEvent(event);
  }
}
