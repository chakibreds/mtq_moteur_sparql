package qengine.program;

//import org.apache.http.client.cache.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;

/**
 * Le RDFHandler intervient lors du parsing de données et permet d'appliquer un
 * traitement pour chaque élément lu par le parseur.
 * 
 * <p>
 * Ce qui servira surtout dans le programme est la méthode
 * {@link #handleStatement(Statement)} qui va permettre de traiter chaque triple
 * lu.
 * </p>
 * <p>
 * À adapter/réécrire selon vos traitements.
 * </p>
 */
public final class MainRDFHandler extends AbstractRDFHandler {
	@Override
	public void handleStatement(Statement st) {
		double start = System.nanoTime();
		Main.nb_triplet++;
		if (!Main.dict.containsKey(st.getSubject().toString())) {
			Main.dict.put(st.getSubject().toString(), Main.counter);
			Main.counter++;
		}

		if (!Main.dict.containsKey(st.getPredicate().toString())) {
			Main.dict.put(st.getPredicate().toString(), Main.counter);
			Main.counter++;
		}

		if (!Main.dict.containsKey(st.getObject().toString())) {
			Main.dict.put(st.getObject().toString(), Main.counter);
			Main.counter++;
		}
		double end = System.nanoTime();
		if(Main.timeExec.get("timeDico") == null){
			Main.timeExec.put("timeDico", (end - start)/ 1000000L);
		}
		else{
			double timeDico = Main.timeExec.get("timeDico");
			timeDico += (end - start)/ 1000000L;
			Main.timeExec.put("timeDico",timeDico);
		}

		start = System.nanoTime();
		Main.dictIndex.get("spo").add(Main.dict.get(st.getSubject().toString()),Main.dict.get(st.getPredicate().toString()),Main.dict.get(st.getObject().toString()));
		Main.dictIndex.get("sop").add(Main.dict.get(st.getSubject().toString()),Main.dict.get(st.getPredicate().toString()),Main.dict.get(st.getObject().toString()));
		Main.dictIndex.get("pos").add(Main.dict.get(st.getSubject().toString()),Main.dict.get(st.getPredicate().toString()),Main.dict.get(st.getObject().toString()));
		Main.dictIndex.get("pso").add(Main.dict.get(st.getSubject().toString()),Main.dict.get(st.getPredicate().toString()),Main.dict.get(st.getObject().toString()));
		Main.dictIndex.get("ops").add(Main.dict.get(st.getSubject().toString()),Main.dict.get(st.getPredicate().toString()),Main.dict.get(st.getObject().toString()));
		Main.dictIndex.get("osp").add(Main.dict.get(st.getSubject().toString()),Main.dict.get(st.getPredicate().toString()),Main.dict.get(st.getObject().toString()));
		end = System.nanoTime();
		if(Main.timeExec.get("timeIndex") == null){
			Main.timeExec.put("timeIndex", (end - start) / 1000000L);
		}
		else{
			double timeIndex = Main.timeExec.get("timeIndex");
			timeIndex += (end - start)/ 1000000L;
			Main.timeExec.put("timeIndex",timeIndex);
		}
	};
}