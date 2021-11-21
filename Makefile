QUERIES=data/extra_sample_query.queryset
DATA=data/sample_data.nt
OUTPUT=output/


all: compil run

init:
	mvn -Dmaven.repo.local=./target/repository  verify

compil:
	javac @classpath.argfile -d ./target/classes/ ./src/main/java/qengine/program/*.java

run: 
	java @classpath.argfile qengine.program.Main -queries $(QUERIES) -data $(DATA) -output $(OUTPUT)
