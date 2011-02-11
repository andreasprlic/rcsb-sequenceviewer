package demo;

import java.awt.Dimension;
import java.awt.Graphics;

import java.awt.image.BufferedImage;
import java.lang.annotation.AnnotationFormatError;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.biojava3.core.util.InputStreamProvider;

import org.rcsb.sequence.biojavadao.BioJavaPubMedFactory;
import org.rcsb.sequence.biojavadao.BioJavaResidueInfoFactory;
import org.rcsb.sequence.biojavadao.BioJavaSequenceCollectionFactory;

import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.conf.AnnotationRegistry;
import org.rcsb.sequence.core.PubMedProvider;
import org.rcsb.sequence.core.ResidueProvider;
import org.rcsb.sequence.core.SequenceCollectionFactory;
import org.rcsb.sequence.core.SequenceCollectionProvider;
import org.rcsb.sequence.model.ResidueInfoFactory;

import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.model.SequenceCollection;

import org.rcsb.sequence.view.html.ChainView;
import org.rcsb.sequence.view.html.ViewParameters;
import org.rcsb.sequence.view.multiline.SequenceImage;
import org.rcsb.sequence.view.oneline.Annotation2SingleLineDrawer;
import org.rcsb.sequence.view.oneline.OneLineView;

public class SequencePanel {
	
	JPanel imgpanel;
	
	public static void main(String[] args){

		String pdbId = "1b0p"; // crosslink2
//		String pdbId = "1A6L"; // crosslink3,4
//		String pdbId = "1UIS"; // crosslink1
//		String pdbId = "3HN3"; // attachment
		//String pdbId = "3MVJ"; // mod. res.
//		String pdbId = "1SU6"; // crosslink6
//		String pdbId = "1CAD"; // crosslink4
//                String pdbId = "3NYH";
		String chainId = "A";

		//String pdbId="7kme";
		//String chainId = "H";

		//String pdbId3 = "3KOB"; //
		//String chainId3 = "B";
		
		// define where PDB files are stored...
		//System.setProperty(AbstractUserArgumentProcessor.PDB_DIR,"/tmp/");
		System.setProperty(InputStreamProvider.CACHE_PROPERTY, "true");
		
	
		SequencePanel panel = new SequencePanel();		

		showSeq(panel, pdbId,chainId);
	

//		showSeq(panel, pdbId2,chainId2);
//		showSeq(panel, pdbId3,chainId3);
	}
	
	public static void showSeq(SequencePanel panel, String pdbId, String chainId){
		BufferedImage img = panel.viewMultiLine(pdbId,chainId);
		
		// alternative:
		//BufferedImage img = panel.viewOneLine(pdbId,chainId);
		
		
		// now wrap the bufferedImage:
		JLabel icon = new JLabel(new ImageIcon(img));
		
		JScrollPane scroll = new JScrollPane(icon);
		
		// display the Pane in a frame
		
		JFrame frame = new JFrame("Display image " + pdbId + "." + chainId);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(scroll);
		frame.setPreferredSize(new Dimension(600,600));
		frame.pack();
		frame.setVisible(true);
	}
	
	
	
	public BufferedImage viewMultiLine(String pdbId, String chainId) {

		SequenceCollection coll = SequenceCollectionProvider.get(pdbId);

		Sequence s = coll.getChainByPDBID(chainId);
		s.ensureAnnotated();
		
		ViewParameters params = new ViewParameters();
		
//		params.setDesiredSequenceRns(ResidueNumberScheme.ATOM);
		
	
		// register the anntation mapper for th emulti line view
		
//		Annotation2MultiLineDrawer a2h = new Annotation2MultiLineDrawer();
		
				
		
		Collection<AnnotationName > annos = params.getAnnotations();
		
		List<AnnotationName> newAnnos = new ArrayList<AnnotationName>();
		for ( AnnotationName anno : annos){
			newAnnos.add(anno);
		}
		
		newAnnos.add(AnnotationRegistry.getAnnotationByName("SITE record"));
		params.setAnnotations(newAnnos);
		
//		view.setAnnotationDrawMapper(a2h);
		
		ChainView view = new ChainView(s, params);
		SequenceImage image = view.getSequenceImage();

		BufferedImage buf = image.getBufferedImage();
		
//		try {
//			javax.imageio.ImageIO.write(buf, "png", 
//					new java.io.File("C:\\Documents and Settings\\gjj\\Desktop\\jj\\"+pdbId+chainId+".png"));
//		} catch (Exception e){}

		return buf;


	}

	
	public BufferedImage viewOneLine(String pdbId, String chainId) {

		SequenceCollection coll = SequenceCollectionProvider.get(pdbId);

		Sequence s = coll.getChainByPDBID(chainId);
		s.ensureAnnotated();
		
		ViewParameters params = new ViewParameters();
		
		//params.setAnnotations(AnnotationRegistry.getAllAnnotations());
		params.setDesiredTopRulerRns(ResidueNumberScheme.ATOM);
		params.setDesiredBottomRulerRns(ResidueNumberScheme.SEQRES);
		
		
		//ChainView view = new ChainView(s, params);
		params.setFontSize(1);
		OneLineView view = new OneLineView(s,params);
		view.setAnnotationDrawMapper(new Annotation2SingleLineDrawer());
		SequenceImage image = view.getSequenceImage();

		BufferedImage buf = image.getBufferedImage();

		return buf;


	}

	public SequencePanel(){
		initBioJavaView();
	}

	/** provide a default view using BioJava.. could be done using some proper configuration managment...
	 * 
	 */
	public void initBioJavaView(){

		// first the Residue Provider
		ResidueInfoFactory refactory = new BioJavaResidueInfoFactory();
		ResidueProvider.setResidueInfoFactory(refactory);

		// next the SequenceCollection Provider
		SequenceCollectionFactory sfactory = new BioJavaSequenceCollectionFactory();
		SequenceCollectionProvider.setSequenceCollectionFactory(sfactory);

		BioJavaPubMedFactory pfactory = new BioJavaPubMedFactory();
		PubMedProvider.setPubMedFactory(pfactory);
		
		
	}

	
	


}

class ShowImage extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6604403623026935415L;
	BufferedImage  image;
	public ShowImage(BufferedImage img) {
		image = img;
	}

	public void paint(Graphics g) {
		g.drawImage( image, 0, 0, null);
	}


}
