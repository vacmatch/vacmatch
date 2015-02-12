Welcome
=======

First of all, welcome to the party. It's awesome to have you in the
team.

This project describes the main VACMatch repository. VACMatch
prospects to be a management app for federations to keep track of
their players and the competition, whilst serving the federation's
webpage among the competition results, by granting referees and the
federation staff administrative staff to edit such information in
near-real-time.

We are still far from done, but with your help, we will rock.

Anyway, if you are here, you are probably wondering how the hell
should you start hacking here. Next section is about that.


Hacking
=======

First of all, the list of ingredients.


Prerequisites
-------------

* JDK 7 or JDK 8
* Git
* Sbt 0.13.5+

Additional dependencies should be automatically obtained via sbt,
including Scala and Spring Framework.


How to hack with your editor
----------------------------

Step one, clone repo to your machine. That's easy.

Step two will depend on your IDE.

### If you are using Eclipse...

1. Create a file with path
2. Introduce in it the following line

		addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "3.0.0")

3. Open the sbt console in the project folder (`cd /path/to/project;
   sbt`)
4. Type in `eclipse` at the prompt. This should generate the eclipse
   project files.
5. In Eclipse use the *Import Wizard* to import *Existing projects
   into Workspace*
6. Whenever build.sbt changes (added or removed dependencies), use
   `sbt eclipse` again to recreate the project files and then
   *Refresh* the project in Eclipse.

<sub>More info: https://github.com/typesafehub/sbteclipse/</sub>

### If you are using IntelliJ IDEA...

Please fill in is this applies to you.

At the moment I think the Community Edition has already Scala + sbt
support as a provided plugin from IntelliJ.

There's a project for providing similar functionality as Eclipse's, so
you can also follow those instructions, and provide the following plugin instead:

	addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.7.0-SNAPSHOT")

And then use the `gen_idea` task.


### If you are using Emacs...

[ENSIME][ensime-server] is the way to go.

Old Emacsen versions may not be supported, but you should at least be
using GNU Emacs 24.1+ anyway.

1. First create a file with path `~/.sbt/0.13/plugins/plugins.sbt`
2. Introduce in it the following two lines:

		resolvers += Resolver.sonatypeRepo("snapshots")
		addSbtPlugin("org.ensime" % "ensime-sbt" % "0.1.5-SNAPSHOT")

3. Open the sbt console in the project folder (`cd /path/to/project;
   sbt`)
4. Type in `gen-ensime` at the prompt. This should generate a
   `.ensime` file in the root project folder.
5. In Emacs, install scala-mode with `M-x package-install RET scala-mode2 RET`
6. After that, install ensime with `M-x package-install RET ensime RET`
7. After ensime is installed, adapt the following snippet so that it fits nicely in your  `~/.emacs`

```elisp
;; Adapt this to your actual JAVA HOME where your JDK resides
(setenv "JAVA_HOME" "/usr/lib/jvm/java-7-openjdk-amd64")

(require 'scala-mode-auto)
(require 'ensime)

;; Whenever scala-mode gets loaded, patch-up ensime too
(add-hook 'scala-mode-hook 'ensime-scala-mode-hook)

;; When scala gets first loaded, append to the exec-path ($PATH in
;;   emacs) your path to sbt (if non-standard)
(eval-after-load "scala-mode"
  '(progn
	 (setq exec-path (append exec-path (list "~/usr/sbt")))))

```

### If you are using Vim/Sublime Text

You can also use [ENSIME][ensime-server], the ENhanced Scala
Interaction Mode for text Editors. Search for `vimside` and
`sublime-ensime` projects in GitHub respectively.


How to launch the stuff you made
--------------------------------

There's a wiki page explaining how [sbt][sbt-wiki] should be used, but tl;dr:

	$ sbt

	Sbt loading blah blah...

	> reStart

	sbt compiles your code and then a JVM forks off and runs the application with some hot-patching support

	> ~compile

	Now sbt watches your project folder and recompiles every changed file as soon as it happens.
	Meanwhile, the forked JVM picks each classfile whose methods' signature does not change

	Whenever you change a file which cannot be hot-patched, you have to issue

	> reStop
	> reStart


When you are done, do not forget to `reStop` before exiting the sbt REPL.


Whilst the server is running, go to http://127.0.0.1:8080/ to watch your code run.


Contributing
============

In order to contribute a patch (feature, bugfix, whatever), we are using a git-flow-like approach.

As we are not yet in production, some things change their meaning:

* The `master` branch is what git-flow approaches as `develop`
* There is no need for `hotfix` branches yet
* There is no need for `release` branches yet

In order to contribute a patch, if you have write permissions on the
repo, create a branch named `feature/descriptive-feature-name`, and
start working on it.

Whilst you are working on it you can do anything
with the branch (create, rebase, push -f, delete it...).

If you feel someone should comment on your work, either submit a PR
*with no reviewers assigned* or @-mention someone else in the
Slack/HipChat room.

Whenever you think the branch is ready to push, submit a PR and assign
a reviewer to it, so that the changeset can be properly peer-reviewed.

As a reviewer, make sure the branch is fast-forwarded to current
master (make sure there are no "behind" commits in the branch view on
BitBucket) before merging the pull-request.

If there are commits behind the branch already in master, you must first clone it locally and issue:

    git fetch -a
    git checkout feature/descriptive-feature-name
    git rebase origin/master
    git push -f origin feature/descriptive-feature-name

You can even merge the branches here

    git checkout master
    git merge --ff-only origin/master
	git merge --no-ff feature/descriptive-feature-name
	Write in the editor a descriptive message. At least say "Merged in feature/something (pull request #1)"
	git push origin master

Asuming no conflicts were generated. If any conflict should arise,
those should be handled by the original committer.

By creating merge commits, the history can be well-understood and
bissected by features, although bisect'ing features can be a bit harder.


License
=======

Copyright 2015 The VACMatch Contributors. All rights reserved.

The content within VACMatch organizational repos is copyrighted and
intellectual property which may not be publicly released until proper
authorization.


  [ensime-server]: http://github.com/ensime/ensime-server
  [sbt-wiki]: https://bitbucket.org/vacmatch/vacmatch/wiki/sbt
