package io.github.yaojiqunaer.entity;

import lombok.Data;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;

/**
 * A spring controlled bean that will be injected
 * with properties about the repository state at build time.
 * This information is supplied by my plugin - <b>pl.project13.maven.git-commit-id-maven-plugin</b>
 */
@Data
@Component
public class GitRepositoryState {

    String tags;                    // =${git.tags} // comma separated tag names
    String branch;                  // =${git.branch}
    String dirty;                   // =${git.dirty}
    String remoteOriginUrl;         // =${git.remote.origin.url}

    String commitId;                // =${git.commit.id.full} OR ${git.commit.id}
    String commitIdAbbrev;          // =${git.commit.id.abbrev}
    String describe;                // =${git.commit.id.describe}
    String describeShort;           // =${git.commit.id.describe-short}
    String commitUserName;          // =${git.commit.user.name}
    String commitUserEmail;         // =${git.commit.user.email}
    String commitMessageFull;       // =${git.commit.message.full}
    String commitMessageShort;      // =${git.commit.message.short}
    String commitTime;              // =${git.commit.time}
    String closestTagName;          // =${git.closest.tag.name}
    String closestTagCommitCount;   // =${git.closest.tag.commit.count}

    String buildUserName;           // =${git.build.user.name}
    String buildUserEmail;          // =${git.build.user.email}
    String buildTime;               // =${git.build.time}
    String buildHost;               // =${git.build.host}
    String buildVersion;            // =${git.build.version}
    String buildNumber;             // =${git.build.number}
    String buildNumberUnique;       // =${git.build.number.unique}

    private final static String GIT_PREFIX = "";        //"git.";

    public GitRepositoryState(GitProperties properties) {
        this.tags = properties.get(GIT_PREFIX + "tags");
        this.branch = properties.get(GIT_PREFIX + "branch");
        this.dirty = properties.get(GIT_PREFIX + "dirty");
        this.remoteOriginUrl = properties.get(GIT_PREFIX + "remote.origin.url");

        this.commitId = properties.get(GIT_PREFIX + "commit.id"); // OR properties.get("commit.id
        // .full") 
        // depending on your configuration
        this.commitIdAbbrev = properties.get(GIT_PREFIX + "commit.id.abbrev");
        this.describe = properties.get(GIT_PREFIX + "commit.id.describe");
        this.describeShort = properties.get(GIT_PREFIX + "commit.id.describe-short");
        this.commitUserName = properties.get(GIT_PREFIX + "commit.user.name");
        this.commitUserEmail = properties.get(GIT_PREFIX + "commit.user.email");
        this.commitMessageFull = properties.get(GIT_PREFIX + "commit.message.full");
        this.commitMessageShort = properties.get(GIT_PREFIX + "commit.message.short");
        this.commitTime = properties.get(GIT_PREFIX + "commit.time");
        this.closestTagName = properties.get(GIT_PREFIX + "closest.tag.name");
        this.closestTagCommitCount = properties.get(GIT_PREFIX + "closest.tag.commit.count");

        this.buildUserName = properties.get(GIT_PREFIX + "build.user.name");
        this.buildUserEmail = properties.get(GIT_PREFIX + "build.user.email");
        this.buildTime = properties.get(GIT_PREFIX + "build.time");
        this.buildHost = properties.get(GIT_PREFIX + "build.host");
        this.buildVersion = properties.get(GIT_PREFIX + "build.version");
        this.buildNumber = properties.get(GIT_PREFIX + "build.number");
        this.buildNumberUnique = properties.get(GIT_PREFIX + "build.number.unique");
    }

}