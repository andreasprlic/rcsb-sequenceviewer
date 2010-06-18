package org.rcsb.sequence.conf;


import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.Chain;
import org.rcsb.sequence.model.PolymerType;
import org.rcsb.sequence.model.Reference;
import org.rcsb.sequence.model.Sequence;

/**
 * class that lists available annotations, and useful constants
 * @author mulvaney
 *
 */
public class AnnotationName implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2701785286389848046L;
	private   AnnotationClassification classification;
	private  String name;
	private  String description;
	private  Reference reference;
	private  Class<? extends AnnotationGroup<?>> annotationGroupClass;
	private  Set<PolymerType> applicablePolymerTypes;



	public AnnotationName(AnnotationClassification ac, 
			String name, 
			String description, 
			Reference reference, 
			Class<? extends AnnotationGroup<?>> annotationGroupClass,
					Set<PolymerType> applicablePolymerTypes)
	{
		this.classification = ac;
		ac.addToAnnotationsClassifiedThus(this); // tell the classification about this annotation
		this.name = name;
		this.description = description;
		this.reference = reference;
		this.annotationGroupClass = annotationGroupClass;
		this.applicablePolymerTypes = applicablePolymerTypes;
	}


	public static final Collection<AnnotationName> DEFAULT_ANNOTATIONS_TO_VIEW;
	static {
		Set<AnnotationName> foo = new LinkedHashSet<AnnotationName>();
		for(AnnotationClassification ac : AnnotationClassification.DEFAULT_CLASSIFICATIONS_TO_VIEW)
		{
			foo.add(ac.getDefaultAnnotation());
		}      
		DEFAULT_ANNOTATIONS_TO_VIEW = Collections.unmodifiableCollection(foo);
	}

	public AnnotationClassification getClassification() {
		return classification;
	}
	public String getDescription() {
		return description;
	}
	public String getName() {
		return name;
	}
	public Reference getReference() {
		return reference;
	}
	public boolean mayAnnotate(Chain chain)
	{
		return mayAnnotate(chain.getPolymerType());
	}
	public boolean mayAnnotate(PolymerType pt)
	{
		return applicablePolymerTypes.contains(pt);
	}
	public Class<? extends AnnotationGroup<?>> getAnnotationClass()
			{
		return annotationGroupClass;
			}
	private static final Class<?>[] CLASS_ARRAY_FOR_ANNOTATION_GROUP_INSTANTIATION = new Class[]{ Sequence.class };

	public AnnotationGroup<?> createAnnotationGroupInstance(Sequence chain)
	{
		try {
			Constructor<? extends AnnotationGroup<?>> c = this.annotationGroupClass.getConstructor(CLASS_ARRAY_FOR_ANNOTATION_GROUP_INSTANTIATION);
			return c.newInstance(new Object[]{ chain });
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("Could not instantiate AnnotationGroup " + this.annotationGroupClass.getSimpleName());
		}
	}
}
