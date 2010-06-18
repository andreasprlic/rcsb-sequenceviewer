package org.rcsb.sequence.biojavadao;

import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.GroupIterator;
import org.biojava.bio.structure.Structure;

import org.biojava.bio.structure.align.ce.AbstractUserArgumentProcessor;
import org.biojava.bio.structure.align.util.AtomCache;
import org.biojava.bio.structure.io.FileParsingParameters;
import org.rcsb.sequence.core.SequenceCollectionFactory;
import org.rcsb.sequence.model.SequenceCollection;


public class BioJavaSequenceCollectionFactory implements
SequenceCollectionFactory {
	private static final String lineSplit = System.getProperty("file.separator");

	public SequenceCollection get(String structureId) {

		String tmp = System.getProperty(AbstractUserArgumentProcessor.PDB_DIR);
		if ( tmp == null){
			String property = "java.io.tmpdir";

			tmp = System.getProperty(property);
		}
		if ( !(tmp.endsWith(lineSplit) ) )
			tmp = tmp + lineSplit;

		AtomCache cache = new AtomCache(tmp, true);

		FileParsingParameters params = new FileParsingParameters();
		params.setAlignSeqRes(true);
		params.setLoadChemCompInfo(true);
		params.setHeaderOnly(false);
		params.setParseSecStruc(true);
		
		cache.setFileParsingParams(params);		
		cache.setAutoFetch(true);
		
		
		

		try {
			Structure structure = cache.getStructure(structureId);
			GroupIterator iter =new  GroupIterator(structure);
			while (iter.hasNext()){
				Group g = iter.next();
				System.out.println(g.getProperties());
			}
			BioJavaSequenceCollection collection = new BioJavaSequenceCollection();
			collection.setStructure(structure);

			return collection;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}

	}

}
