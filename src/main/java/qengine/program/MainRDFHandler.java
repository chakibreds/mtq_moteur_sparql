package qengine.program;

import java.util.HashMap;


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

	public static Integer counter = 1;
	public static HashMap<Integer, String> dict = new HashMap<Integer, String>();

	@Override
	public void handleStatement(Statement st) {
		if (!dict.containsValue(st.getSubject().toString())) {
			dict.put(counter, st.getSubject().toString());
			counter++;
		}

		if (!dict.containsValue(st.getPredicate().toString())) {
			dict.put(counter, st.getPredicate().toString());
			counter++;
		}

		if (!dict.containsValue(st.getObject().toString())) {
			dict.put(counter, st.getObject().toString());
			counter++;
		}
	};
}