package qengine.program;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.HashBiMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.jena.base.Sys;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.riot.RDFDataMgr;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;

/**
 * Programme simple lisant un fichier de requête et un fichier de données.
 * 
 * <p>
 * Les entrées sont données ici de manière statique, à vous de programmer les
 * entrées par passage d'arguments en ligne de commande comme demandé dans
 * l'énoncé.
 * </p>
 * 
 * <p>
 * Le présent programme se contente de vous montrer la voie pour lire les
 * triples et requêtes depuis les fichiers ; ce sera à vous d'adapter/réécrire
 * le code pour finalement utiliser les requêtes et interroger les données. On
 * ne s'attend pas forcémment à ce que vous gardiez la même structure de code,
 * vous pouvez tout réécrire.
 * </p>
 * 
 * @author Olivier Rodriguez <olivier.rodriguez1@umontpellier.fr>
 */
final class Main {
	static final String baseURI = null;

	/**
	 * Votre répertoire de travail où vont se trouver les fichiers à lire
	 */
	static String workingDir = "data/";

	/**
	 * Fichier contenant les requêtes sparql
	 */
	static String queryFile = workingDir + "sample_query.queryset";

	/**
	 * Fichier contenant des données rdf
	 */
	static String dataFile = workingDir + "100K.nt";

	/**
	 * Output directory
	 */
	static String outputDir = "output/";

	/**
	 *  
	 * */
	static boolean export_query_results = false;

	// use jena?
	static boolean jena = false;

	/**
	 * Dictionnaire contenant les données et leur valeurs.
	 */
	public static Integer counter = 1;
	public static Integer nb_triplet = 0;
	public static HashBiMap<String, Integer> dict = HashBiMap.create();

	/**
	 * Dictionnaire contenant les six indexes
	 */
	public static HashMap<String, Index> dictIndex = new HashMap<String, Index>();

	/**
	 * Liste contenant les réponses de chaque requête
	 */
	// public static Integer reponsesCounter = 0;
	public static List<List<String>> reponses = new ArrayList<List<String>>();

	/**
	 * List contenant les temps d'exécutions
	 */
	public static HashMap<String, Double> timeExec = new HashMap<String, Double>();

	// jena variables
	public static Dataset dataset;

	// ========================================================================

	/**
	 * Méthode qui prend en entrée un seul pattern de requête et une liste de
	 * valeurs et qui retourne une sous-liste de valeurs correspondant aux valeurs
	 * matchant le pattern
	 */
	public static SortedSet<Integer> matchPattern(StatementPattern pattern, SortedSet<Integer> list) {
		Index index = dictIndex.get("pos");
		int p, o;
		try {
			p = dict.get(pattern.getPredicateVar().getValue().toString());
			o = dict.get(pattern.getObjectVar().getValue().toString());
		} catch (NullPointerException e) {
			return new TreeSet<Integer>();
		}

		SortedSet<Integer> reponseIndex = index.get(p, o);
		if (list == null) {
			//System.out.println("\nlist=null\nreponse.... "+ reponseIndex.toString());
			return reponseIndex;
		}
		SortedSet<Integer> merge = new TreeSet<Integer>();
		//System.out.println("list: "+ list.toString());
		//System.out.println("reponse.... "+ reponseIndex.toString());

		while (!list.isEmpty() && !reponseIndex.isEmpty()) {
			if (list.first().equals(reponseIndex.first())) {
				merge.add(list.first());
				list.remove(list.first());
				reponseIndex.remove(reponseIndex.first());
			} else if (list.first().intValue() < reponseIndex.first().intValue()) {
				list.remove(list.first());
			} else {
				reponseIndex.remove(reponseIndex.first());
			}
			//System.out.println("Merging.... \n");
			//System.out.println("list: "+ list.toString());
			//System.out.println("reponse.... "+ reponseIndex.toString());
 
		}
		//System.out.println("----------ENDMERGE--------");
		//System.out.println("\nmerge: "+merge.toString());
		return merge;
	}

	public static void loadDataSet(String fileName) {
		dataset = RDFDataMgr.loadDataset(fileName) ;
	}

	public static void processAQuery(String queryString) {
		Query query = QueryFactory.create(queryString);
		ArrayList<String> reponse = new ArrayList<String>();
		try (QueryExecution qexec = QueryExecutionFactory.create(query, dataset)) {
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				if(soln.varNames().hasNext()) {
					String var = soln.varNames().next();
					reponse.add(soln.get(var).toString());
				}
			}
		}
		reponses.add(reponse);
	}

	/**
	 * Méthode utilisée ici lors du parsing de requête sparql pour agir sur l'objet
	 * obtenu.
	 */
	public static void processAQuery(ParsedQuery query) {
		List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());
		Iterator<StatementPattern> pattern = patterns.iterator();

		SortedSet<Integer> reponse = null;
		if (!pattern.hasNext())
			reponse = new TreeSet<Integer>();

		while (pattern.hasNext()) {
			reponse = matchPattern(pattern.next(), reponse);
			if (reponse.isEmpty())
				break;
		}

		// transalte it to reponse<String>
		reponses.add(reponse.stream().map(e -> dict.inverse().get(e)).collect(Collectors.toList()));
		//System.out.println("\nTotal: "+reponses.toString());
		//System.out.println("\nResultat: "+reponse.toString());
	}

	/**
	 * Entrée du programme
	 */
	public static void main(String[] args) throws Exception {
		double startAll = System.nanoTime();

		dictIndex.put("spo", new Index("spo"));
		dictIndex.put("sop", new Index("sop"));
		dictIndex.put("pos", new Index("pos"));
		dictIndex.put("pso", new Index("pso"));
		dictIndex.put("ops", new Index("ops"));
		dictIndex.put("osp", new Index("osp"));

		if (!parseArg(args)) {
			System.err.println("Erreur lors du parsing des arguments");
			System.err.println("Pour afficher les options: java -jar rdfengine -help");
			System.exit(1);
		}

		if (jena) {
			System.out.println("Engine set to Jena");
		}

		double startTime = System.nanoTime();
		System.out.println("Chargement des données depuis '" + dataFile + "'.");
		if (jena) {
			loadDataSet(dataFile);
		} else {
			parseData();
		}
		double endTime = System.nanoTime();
		timeExec.put("load_data", (endTime - startTime) / 1000000L);

		System.out.println("Execution des requêtes contenu dans '" + queryFile + "'.");
		int nb_queries = parseQueries();

		if (export_query_results)
			exportQueryResults();

		double endAll = System.nanoTime();

		timeExec.put("allTime", (endAll - startAll) / 1000000L);
		System.out.println("\nTemps Total du chargement des données : " + timeExec.get("load_data") + " ms");
		System.out.println("Temps d'évaluation des requêtes : " + timeExec.get("processQueries") + " ms");
		System.out.println("Temps Total du programme: " + timeExec.get("allTime") + " ms\n");

		exportStats(dataFile, queryFile, nb_triplet, nb_queries);
	}

	public static void exportQueryResults() {
		String fileName = outputDir + "queryResult.csv";
		// write all the results in a csv file
		try {
			FileWriter writer = new FileWriter(fileName);
			CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
			printer.printRecord("Query", "Result");
			for (int i = 0; i < reponses.size(); i++) {
				printer.printRecord(i + 1, reponses.get(i));
			}
			printer.flush();
			printer.close();
			System.out.println("Résultat écrit dans le fichier '" + fileName + "'.");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void exportStats(String dataFile, String queryFile, int nb_triplet, int nb_queries) {
		String fileName = outputDir + "stats.csv";
		try {
			FileWriter writer = new FileWriter(fileName);
			CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);

			printer.printRecord("nom du fichier des données", "nom du fichier des reqûetes", "nombre de triplets RDF",
					"nombre de requêtes", "temps de lecture des données (ms) ", "temps de lecture des requêtes (ms)",
					"temps création dico (ms)", "nombre d’index", "temps de création des index (ms)",
					"temps total d’évaluation du workload (ms)", "temps total (du début à la fin du programme) (ms)");

			printer.printRecord(dataFile, queryFile, nb_triplet, nb_queries, timeExec.get("load_data"),
					timeExec.get("parseQueries"), timeExec.get("timeDico"), dictIndex.size(), timeExec.get("timeIndex"),
					timeExec.get("processQueries"), timeExec.get("allTime"));

			printer.flush();
			printer.close();
			System.out.println("Stats écrit dans le fichier '" + fileName + "'.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ========================================================================
	public static boolean parseArg(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-queries") && (++i < args.length)) {
				queryFile = args[i];
			} else if (args[i].equals("-data") && (++i < args.length)) {
				dataFile = args[i];
			} else if (args[i].equals("-output") && (++i < args.length)) {
				outputDir = args[i];
				if (outputDir.charAt(outputDir.length() - 1) != '/')
					outputDir += '/';
			} else if (args[i].equals("-export_query_results")) {
				export_query_results = true;
			} else if (args[i].equals("-help")) {
				System.out.println("java -jar rdfengine");
				System.out.println("\t-queries '/chemin/vers/fichier/requetes' : Fichier contenant les requêtes");
				System.out.println("\t-data '/chemin/vers/fichier/donnees' :	 Fichier contenant les données");
				System.out
						.println("\t-output '/chemin/vers/dossier/sortie':	 Dossier qui va contenir les resultats");
				System.out.println(
						"\t-export_query_result: 			Si specifié, export le resultat des requête dans un fichier csv");
				System.exit(1);
			} else if (args[i].equals("-jena")) {
				jena = true;
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Traite chaque requête lue dans {@link #queryFile} avec
	 * {@link #processAQuery(ParsedQuery)}.
	 */
	private static int parseQueries() throws FileNotFoundException, IOException {
		/**
		 * Try-with-resources
		 * 
		 * @see <a href=
		 *      "https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html">Try-with-resources</a>
		 */
		/*
		 * On utilise un stream pour lire les lignes une par une, sans avoir à toutes
		 * les stocker entièrement dans une collection.
		 */
		int counter = 0;
		try (Stream<String> lineStream = Files.lines(Paths.get(queryFile))) {
			SPARQLParser sparqlParser = new SPARQLParser();
			Iterator<String> lineIterator = lineStream.iterator();
			StringBuilder queryString = new StringBuilder();
			double timeParseQueries = 0.0;
			double timeProcessQueries = 0.0;
			double startProcessQuery = 0.0;
			while (lineIterator.hasNext())
			/*
			 * On stocke plusieurs lignes jusqu'à ce que l'une d'entre elles se termine par
			 * un '}' On considère alors que c'est la fin d'une requête
			 */
			{
				double startParseQuery = System.nanoTime();
				String line = lineIterator.next();
				queryString.append(line);

				if (line.trim().endsWith("}")) {
					counter++;
					if (jena) {
						startProcessQuery = System.nanoTime();
						processAQuery(queryString.toString());
					}
					else {
						ParsedQuery query = sparqlParser.parseQuery(queryString.toString(), baseURI);
						double endParseQuery = System.nanoTime();
						timeParseQueries += (endParseQuery - startParseQuery) / 1000000L;
						startProcessQuery = System.nanoTime();
						processAQuery(query);
					}
					double endProcessQuery = System.nanoTime();
					timeProcessQueries += (endProcessQuery - startProcessQuery) / 1000000L;

					queryString.setLength(0); // Reset le buffer de la requête en chaine vide
				}

			}
			timeExec.put("parseQueries", timeParseQueries);
			timeExec.put("processQueries", timeProcessQueries);
		}
		return counter;
	}

	/**
	 * Traite chaque triple lu dans {@link #dataFile} avec {@link MainRDFHandler}.
	 */
	private static void parseData() throws FileNotFoundException, IOException {
		try (Reader dataReader = new FileReader(dataFile)) {
			// On va parser des données au format ntriples
			RDFParser rdfParser = Rio.createParser(RDFFormat.NTRIPLES);

			// On utilise notre implémentation de handler
			rdfParser.setRDFHandler(new MainRDFHandler());

			// Parsing et traitement de chaque triple par le handler
			rdfParser.parse(dataReader, baseURI);
		}
	}
}
