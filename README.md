agile-group-1 [![Build Status](https://magnum.travis-ci.com/MMMarcy/agile-group-1.svg?token=JdSu2xvmKYUWzpGDUmY7&branch=master)](https://magnum.travis-ci.com/MMMarcy/agile-group-1)
=============



## Egit usage guide

### Authentication

```java
OAuthService os = new OAuthService();
os.getClient().setCredentials("username", "password");
try {
	os.getAuthorizations();
	System.out.println("Authorization successfull");
} catch (RequestException e){
	System.out.println("Authorization failed");
}
```

### Get branches

```java
RepositoryService rs = new RepositoryService();
rs.getClient().setCredentials("user", "password"); //optional
IRepositoryIdProvider repositoryId = RepositoryId.create("marcyb5st", "agile-group-1");
List<RepositoryBranch> branches = rs.getBranches(repositoryId);
for(RepositoryBranch branch : branches){
	System.out.println(branch.getName());
}
```

### Get last 10 commits starting for a branch

```java
CommitService cs = new CommitService();
cs.getClient().setCredentials("user", "password"); //optional
PageIterator<RepositoryCommit> commitPages = cs.pageCommits(repositoryId, "testingEnvironment", null, 10);
Collection<RepositoryCommit> lastTenCommits = commitPages.next();
SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
for(RepositoryCommit repoCommit : lastTenCommits){
	Commit commit = repoCommit.getCommit();
	System.out.printf("%s : %s commited: '%s'\n", dateFormat.format(commit.getCommitter().getDate()), commit.getCommitter().getName(), commit.getMessage());
}
```

If you don't understand smth about the egit API or how git branching works just ask me or we can discuss that on the next meeting
