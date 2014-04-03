package se.chalmers.agile.core;

import java.util.Calendar;
import java.util.Date;

public class Commit {

    private final String repo;
    private final String branch;
    private final Date time;
    private final String committer;
    private final String commitMsg;

    public Commit(String repo, String branch, Date time, String committer, String commitMsg) {
        this.repo = repo;
        this.branch = branch;
        this.time = time;
        this.committer = committer;
        this.commitMsg = commitMsg;
    }

    public String getRepo() {
        return repo;
    }

    public String getBranch() {
        return branch;
    }

    public Date getTime() {
        return time;
    }

    public String getCommitter() {
        return committer;
    }

    public String getCommitMsg() {
        return commitMsg;
    }
}
