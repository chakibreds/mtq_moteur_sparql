QUERIES=queries/
DATA=data/100K.nt
OUTPUT=output/


all: compil run

init:
	mvn -Dmaven.repo.local=./target/repository  verify

compil:
	javac @classpath.argfile -d ./target/classes/ ./src/main/java/qengine/program/*.java

run:
	java @classpath.argfile qengine.program.Main -queries $(QUERIES) -data $(DATA) -output $(OUTPUT) -export_query_results

jena: 
	java @classpath.argfile qengine.program.Main -jena -queries $(QUERIES) -data $(DATA) -output $(OUTPUT) -export_query_results

jar:
	mvn package
	cp target/qengine-0.0.1-SNAPSHOT-jar-with-dependencies.jar ./rdfengine.jar