# Code Visualizer

Code Visualizer is a development support tool to generate sequence diagram and class diagram from java source code.
Sequence diagrams are generated for each public method which belong to specified class.
Class diagrams are generated for each sequence diagram and include classes which appeare in it as parameter and return types.


## Demo

If you want to know how Code Visualizer works, try to run it for DDD Sample app (https://github.com/citerus/dddsample-core).
JRE v1.8+ and Git are required.
You can run Code Visualizer on Windows and macOS using following commands of each OS.

* Windows

```
git clone https://github.com/citerus/dddsample-core.git
cd dddsample-core
start http://repo1.maven.org/maven2/io/sitoolkit/cv/sit-cv-app/1.0.0-beta.1/sit-cv-app-1.0.0-beta.1.jar
move %USERPROFILE%\Downloads\sit-cv-app-1.0.0-beta.1.jar .
java -jar sit-cv-app-1.0.0-beta.1.jar
```

* macOS

```
brew install graphviz
git clone https://github.com/citerus/dddsample-core.git
cd dddsample-core
curl -o sit-cv-app-1.0.0-beta.1.jar -G http://repo1.maven.org/maven2/io/sitoolkit/cv/sit-cv-app/1.0.0-beta.1/sit-cv-app-1.0.0-beta.1.jar 
java -jar sit-cv-app-1.0.0-beta.1.jar
```

After running last java command, you can see following log on your console.

```
Started SitCvApplication in 00.000 seconds (JVM running for 00.000)
```

Then you can access http://localhost:8080 with browser and see UML diagrams of DDD Sample app.

* Report Mode

Running java command with --report option, static report files(html, css, js) are generated to docs/designdoc directory.

```
java -jar sit-cv-app-1.0.0-beta.1.jar --report
```

You can see diagrams by opening docs/designdoc/index.html with browser.


## How to Use in Your Project

### Maven Project

If your project uses Maven, add plugin to pom.xml of your project.

```xml
<project>
  ...
  <build>
    <plugins>
        <plugin>
            <groupId>io.sitoolkit.cv</groupId>
            <artifactId>sit-cv-maven-plugin</artifactId>
            <version>1.0.0-beta.1</version>
        </plugin>
    </plugins>
  </build>
  ...
</project>
```

Then you can use following commands in your project directory.

* Server mode

```
mvn sit-cv:run
```

* Report mode

```
mvn sit-cv:report
```


### Gradle Project

If your project uses Gradle, add plugin to build.gradle of your project.


```groovy
buildscript {
  repositories {
      mavenLocal()
  }
  dependencies {
      classpath group: 'io.sitoolkit.cv', name: 'sit-cv-gradle-plugin', version:'1.0.0-beta.1'
  }
}

apply plugin: 'sit-cv-gradle-plugin'
```

Then you can use following commands in your project directory.

* Server mode

```
gradle cvRun
```

* Report mode

```
gradle cvReport
```


## Configuration for Your Project

If you want to customize filter condition to draw classes on diagrams, put sit-cv-config.json in your project root directory.
It's JSON structure is as folows.

* sit-cv-config.json

```json
{
  "entryPointFilter": {
    "include":[
      {
        "name": ".*Controller"
      },
      {
        "annotation": "*.Controller"
      },
      {
        "name": ".*Controller",
        "annotation": ""
      },
    ],
    "exclude":[]
  },
  "sequenceDiagramFilter": {
    "include": [],
    "exclude": []
  }
}
```

|          Key          |                                   Description                                    |
| --------------------- | -------------------------------------------------------------------------------- |
| entoryPointFilter     | Filter rule to recognize as entry point i.e. left end class of sequence diagram. |
| include               | Classes which matches one of these rules are included to sequence diagram.       |
| exclude               | Classes which matches one of these rules are excluded to sequence diagram.       |
| name                  | Pattern to match class qualified name.                                           |
| annotation            | Pattern to match qualified annotation name of class.                             |
| sequenceDiagramFilter | Filter rule to draw sequence diagram.                                            |