This is an example selendion project.

To run it you need to install Java JDK 1.5 or greater and Apache Ant (http://ant.apache.org/bindownload.cgi)
You should also have firefox installed in the standard place for your operating system.

Once they are installed, open a cmd window.  Type
javac -version

The first line output should be something like
javac 1.5.0_16

Type
ant -version

You should see something like
Apache Ant version 1.7.1 compiled on June 27 2008

Once both those work, change directory in the command window to this directory, and type
ant

This will build the small example project and run the tests.  At the end of the output you will
see something like

[junit] c:\selendion-example\build\test-output\selendion\Suite.html
[junit] Successes: 2, Failures: 0
[junit]
[junit] Tests run: 1, Failures: 0, Errors: 0, Time elapsed: 7.81 sec

Open the Suite.html in your browser to see the results.
