=========================
RDF Commons Libray README
=========================

The RDF Commons Library provides a set of general utility classes for
dealing with RDF data, providing features as:

   - Java Bean to RDF mapping support;
   - RDF dataset abstraction over general RDF
     libraries such Jena and Sesame (coming soon);
   - parsing and serialization facilities;
   - data storing and querying facilities;
   - abstract query language over RDF.

-------------
Documentation
-------------

TODO: link to Getting Started
TODO: link to Developers
TODO: link to apidocs

----------------------
Build from Source Code
----------------------

Be sure to have the Apache Maven v.2.2.x+ installed and included in PATH.

For specific information about Maven see: http://maven.apache.org/

Go to the trunk folder:

    $ cd trunk/

and execute the following command:

    trunk$ mvn clean install

This will install the RDF Commons artifacts and its dependencies in your 
local Maven2 repository.

----------------------
Generate Documentation
----------------------

To generate the project site locally execute the following command from the trunk dir:

    trunk$ MAVEN_OPTS='-Xmx512m' mvn clean site:site

You can speed up the site generation process specifying the offline option ( -o ),
but it works only if all the involved plugin dependencies has been already downloaded
in the local M2 repository:

    trunk$ MAVEN_OPTS='-Xmx512m' mvn -o clean site:site

If you're interested in generating the Javadoc enriched with navigable UML graphs, you can activate
the umlgraphdoc profile. This profile relies on graphviz ( http://www.graphviz.org/) that must be 
installed in your system.

    trunk$ MAVEN_OPTS='-Xmx256m' mvn -P umlgraphdoc clean site:site

--------------------
Deploy Documentation
--------------------

(Developers interest only.)

In order to correctly deploy the site to a remote FTP you just need to properly set up
the following lines in your <distributionManagement> of the root pom.xml:

    <distributionManagement>
        ...
        <site>
            <id>rdf-commons-googlecode</id>
            <name>RDF Commons Developer Web Site</name>
            <url>https://rdf-commons.googlecode.com/svn/site/</url>
        </site>
    </distributionManagement>

Please remember that you need to set up your username and password to access to that FTP in your
~/.m2/settings.xml as:

    <server>
        <id>rdf-commons-googlecode</id>
        <username>USERNAME</username>
        <password>PASSWORD</password>
    </server>

------------------
Make a new release
------------------

(Developers interest only.)

To prepare a new release, just verify that the are no local changes and then invoke:

	mvn release:prepare -Dusername=<svn.username> -Dpassword=<svn.pass>
	
if everything goes right, perform the release simply typing:

	mvn release:perform

EOF
