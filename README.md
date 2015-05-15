# cadence #

cadence is an algorithmic music composer written in Clojure. cadence takes an input MIDI file and transforms it using a Markov Chain process to produce unique MIDI sequences of varying degrees of listenability.

### Requirements ###

In order to build cadence, you must install (at a minimum) Java SDK 7. To verify your version of Java:

```
$ java -version
```

Once Java is installed, the easiest way to build the project and get going is with [Leiningen](https://github.com/technomancy/leiningen), a build automation and dependency management tool for Clojure projects.

### Usage ###

To run cadence from the command line, do the following:

```
$ cd ~/path/to/cadence && lein run -m composer.core
```

Alternatively, you can package cadence as a JAR file and execute it that way:

```
$ cd ~/path/to/cadence && lein uberjar
$ java -jar target/composer-0.1.0-SNAPSHOT-standalone.jar
```

### License ###

Copyright © 2013 Dylan Gleason

Distributed under the Eclipse Public License, the same as Clojure.
