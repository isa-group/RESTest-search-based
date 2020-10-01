# RESTest-search-based

This is the search-based module for [RESTest](https://github.com/isa-group/RESTest), our black-box testing framework for RESTful web APIs.

## Quickstart guide
To get started with RESTest-search-based, download the code and move to the parent directory:
````
git clone https://github.com/isa-group/RESTest-search-based.git
cd RESTest-search-based
````

### Maven configuration
To build and run RESTest, you **MUST** include the dependencies in the `lib` folder on your local Maven repository (e.g., `~/.m2` folder in Mac). You can do it as follows:
```sh
mvn install:install-file -Dfile=lib/restest.jar -DgroupId=es.us.isa -DartifactId=restest -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar
mvn install:install-file -Dfile=lib/JSONmutator.jar -DgroupId=es.us.isa -DartifactId=json-mutator -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar
mvn install:install-file -Dfile=lib/IDL2MiniZincMapper.jar -DgroupId=es.us.isa -DartifactId=idl-2-minizinc-mapper -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar
mvn install:install-file -Dfile=lib/IDLreasoner.jar -DgroupId=es.us.isa -DartifactId=idl-reasoner -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar
```
