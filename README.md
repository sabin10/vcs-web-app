## Description
##### Version Control Web App
Developed by Sabin Hantu, 2nd year student

A Java Web App with Spring Framework which keeps track of each modification of a file and give user the chance to come back to a previous version of the project.

Keeping in database only the changes occured, instead of the whole file. Each commit keeps track only of changes occured in the new created commit to the last meaningful commit which improves efficiency in reconstruction of file's versions.

## Stack of technologies
+ Spring Boot
+ Hibernate, JPA, H2 datasource
+ Thymeleaf, HTML, CSS, Bootstrap
+ java-diff-utils 3rd party library

## Functionalities
+ User Registration with Spring Security
+ Create and update projects
+ Add more contributors to the same project, so more users can work at the same project
+ View user's projects list
+ Branching and merging. Create a new branch for making independent changes from master and then merge changes to master
+ Commiting. Save changes to the project and have choice to come back to a previous version from another commit

## How to build / run from Intellij
~~~~
git clone https://github.com/sabin10/version-control-web-app-spring.git
cd version-control-web-app-spring
mvn install
mvn spring-boot:run
~~~~

Or open Intellij IDEA and start Spring boot application from the main class: `com.sabinhantu.vcs.VcsApplication`

Open http://localhost:8080 in your browser
