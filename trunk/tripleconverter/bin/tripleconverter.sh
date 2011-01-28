#!/bin/bash
CONVERTER_ROOT=$(cd "$(dirname "$0")"; pwd -P)
CONVERTER_ROOT="$CONVERTER_ROOT/.."

if [ ! -e $CONVERTER_ROOT/target/*-jar-with-dependencies.jar ]; then 
    echo "Generating executable JAR..."
    mvn -f $CONVERTER_ROOT/pom.xml -Dmaven.test.skip=true clean assembly:assembly	
fi

SEP=':'
if [ "$(uname)" = "Cygwin" ]; then SEP=';'; fi
for jar in "$CONVERTER_ROOT"/target/*-jar-with-dependencies.jar
do
  if [ ! -e "$jar" ]; then continue; fi
  CP="$CP$SEP$jar"
done
exec java -cp "$CP" -Xmx256M org.sindice.rdfcommons.tripleconverter.TripleConverter "$@"
