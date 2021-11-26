QUERIES=data/STAR_ALL_workload.queryset
DATA=data/100K.nt
OUTPUT=output/


all: compil run

init:
	mvn -Dmaven.repo.local=./target/repository  verify

compil:
	javac @classpath.argfile -d ./target/classes/ ./src/main/java/qengine/program/*.java

run: 
	java @classpath.argfile qengine.program.Main -queries $(QUERIES) -data $(DATA) -output $(OUTPUT) -export_query_results

jar:
	mvn clean package
	cp target/qengine-0.0.1-SNAPSHOT-jar-with-dependencies.jar ./rdfengine.jar