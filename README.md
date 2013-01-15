# Joos Compiler

CS 444 Course Project  
Member: Greg Wang, Wenzhu Man, Matt Baker

## Overview

The Language Specification is [Joos 1W](https://www.student.cs.uwaterloo.ca/~cs444/joos.html). 

## To Start Development

### Setup Git

1. Download [Git](http://git-scm.com/downloads) from [http://git-scm.com/downloads]()
2. Setup git by following the **STEP1** on [https://confluence.atlassian.com/display/BITBUCKET/Set+up+Git+and+Mercurial]()  
	(Essentially is setup your user.name and user.email, so don't hesitate if you are not on Windows.)

### Clone and Create the Project

1. Download and install [Eclipse IDE for Java Developers](http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/junosr1) from [http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/junosr1]()
2. Open Eclipse and set Workspace folder
3. "Menu" -> "File" -> "Import" -> "Git/Projects from Git" -> "URI"
4. Fill in the wizard form with
	- URI: `https://bitbucket.org/gregwym/joos-compiler-java.git`
	- In the authentication, fill in your bitbucket username and password
	- Don't need to change any other; press "Next >" 3 times
5. Select "Use the New Project wizard", "Finish"
6. Select "Java/Java Project"
7. Fill in the "Project name" with `joos-compiler-java`, press "Next >"  
	(If you typed correctly, the wizard should notice you "The wizard will automatically configure the JRE and the project layout based on the existingg source.")
8. Press "Finish"

Now you should be able to run and see the "Hello World!" being printed in the Console. 