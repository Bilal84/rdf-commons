#!/bin/bash
mvn install:install-file -DgroupId=com.openlink.virtuoso -DartifactId=virtuoso-jdbc-3  -Dversion=1.0.0 -Dpackaging=jar -Dfile=virtjdbc3.jar
mvn install:install-file -DgroupId=com.openlink.virtuoso -DartifactId=virtuoso-jdbc-4  -Dversion=1.0.0 -Dpackaging=jar -Dfile=virtjdbc4.jar
mvn install:install-file -DgroupId=com.openlink.virtuoso -DartifactId=virtuoso-sesame2 -Dversion=1.0.0 -Dpackaging=jar -Dfile=virt_sesame2.jar
