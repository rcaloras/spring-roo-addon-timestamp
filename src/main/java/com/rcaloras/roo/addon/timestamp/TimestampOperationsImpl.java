package com.rcaloras.roo.addon.timestamp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.Validate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.classpath.TypeLocationService;
import org.springframework.roo.classpath.TypeManagementService;
import org.springframework.roo.classpath.details.ClassOrInterfaceTypeDetails;
import org.springframework.roo.classpath.details.ClassOrInterfaceTypeDetailsBuilder;
import org.springframework.roo.classpath.details.ImportMetadata;
import org.springframework.roo.classpath.details.MemberFindingUtils;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadataBuilder;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.Dependency;
import org.springframework.roo.project.DependencyScope;
import org.springframework.roo.project.DependencyType;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.project.Repository;
import org.springframework.roo.support.util.XmlUtils;
import org.w3c.dom.Element;

/**
 * Implementation of operations this add-on offers.
 *
 * @since 1.1
 */
@Component // Use these Apache Felix annotations to register your commands class in the Roo container
@Service
public class TimestampOperationsImpl implements TimestampOperations {

	/**
	 * Use ProjectOperations to install new dependencies, plugins, properties, etc into the project configuration
	 */
	@Reference private ProjectOperations projectOperations;

	/**
	 * Use TypeLocationService to find types which are annotated with a given annotation in the project
	 */
	@Reference private TypeLocationService typeLocationService;

	/**
	 * Use TypeManagementService to change types
	 */
	@Reference private TypeManagementService typeManagementService;





	Repository snapshotRepo = new Repository("roo-timestamp-addon-snapshots", "Timestamp Roo add-on snapshot repository", 
			"https://raw.github.com/rcaloras/rcaloras-mvn-repo/master/snapshots");

	Repository releaseRepo = new Repository("roo-timestamp-addon-releases", "Timestamp Roo add-on release repository", 
			"https://raw.github.com/rcaloras/rcaloras-mvn-repo/master/releases");

	Dependency timestampDependency;
	
	
	String version;

	/** {@inheritDoc} */
	public boolean isCommandAvailable() {
		// Check if a project has been created
		return projectOperations.isFocusedProjectAvailable();
	}

	/** {@inheritDoc} */
	public void annotateType(JavaType javaType) {
		// Use Roo's Assert type for null checks
		Validate.notNull(javaType, "Java type required");

		// Obtain ClassOrInterfaceTypeDetails for this java type
		ClassOrInterfaceTypeDetails existing = typeLocationService.getTypeDetails(javaType);

		// Test if the annotation already exists on the target type
		if (existing != null && MemberFindingUtils.getAnnotationOfType(existing.getAnnotations(), new JavaType(RooTimestamp.class.getName())) == null) {
			ClassOrInterfaceTypeDetailsBuilder classOrInterfaceTypeDetailsBuilder = new ClassOrInterfaceTypeDetailsBuilder(existing);

			// Create JavaType instance for the add-ons trigger annotation
			JavaType rooRooTimestamp = new JavaType(RooTimestamp.class.getName());

			// Create Annotation metadata
			AnnotationMetadataBuilder annotationBuilder = new AnnotationMetadataBuilder(rooRooTimestamp);

			// Add annotation to target type
			classOrInterfaceTypeDetailsBuilder.addAnnotation(annotationBuilder.build());

			// Save changes to disk
			typeManagementService.createOrUpdateTypeOnDisk(classOrInterfaceTypeDetailsBuilder.build());
		}
	}

	public void removeAnnotation(JavaType javaType){

		// Use Roo's Assert type for null checks
		Validate.notNull(javaType, "Java type required");

		// Obtain ClassOrInterfaceTypeDetails for this java type
		ClassOrInterfaceTypeDetails existing = typeLocationService.getTypeDetails(javaType);

		// Test if the annotation is present
		if (existing != null && MemberFindingUtils.getAnnotationOfType(existing.getAnnotations(), new JavaType(RooTimestamp.class.getName())) != null) {

			ClassOrInterfaceTypeDetailsBuilder classOrInterfaceTypeDetailsBuilder = new ClassOrInterfaceTypeDetailsBuilder(existing);

			JavaType rooTimestamp = new JavaType(RooTimestamp.class.getName());

			// Add annotation to target type
			classOrInterfaceTypeDetailsBuilder.removeAnnotation(rooTimestamp);
		
			// Save changes to disk
			typeManagementService.createOrUpdateTypeOnDisk(classOrInterfaceTypeDetailsBuilder.build());
		}
	}

	/** {@inheritDoc} */
	public void annotateAll() {
		// Use the TypeLocationService to scan project for all types with a specific annotation
		for (JavaType type: typeLocationService.findTypesWithAnnotation(new JavaType("org.springframework.roo.addon.javabean.RooJavaBean"))) {
			annotateType(type);
		}
	}

	public void removeAllAnnotations(){
		// Use the TypeLocationService to scan project for all types with a specific annotation
		for (JavaType type: typeLocationService.findTypesWithAnnotation(new JavaType(RooTimestamp.class.getName()))) {
			removeAnnotation(type);
		}
	}



	/** {@inheritDoc} */
	public void setup() {
		
		initialize();
		
		//Add the appropriate repository 
		if(version.toLowerCase().contains("snapshot")){
			projectOperations.addRepository("", snapshotRepo);
		}
		else{
			projectOperations.addRepository("", releaseRepo);
		}

		List<Dependency> dependencies = new ArrayList<Dependency>();

		// Install the dependency on the add-on jar (
		dependencies.add(timestampDependency);

		// Install dependencies defined in external XML file
		for (Element dependencyElement : XmlUtils.findElements("/configuration/batch/dependencies/dependency", XmlUtils.getConfiguration(getClass()))) {
			dependencies.add(new Dependency(dependencyElement));
		}

		// Add all new dependencies to pom.xml
		projectOperations.addDependencies("", dependencies);
	}

	
	private void initialize(){
		
		Properties properties = new Properties();
		try {
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("/project.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		version = properties.getProperty("version");
		timestampDependency = new Dependency("com.rcaloras.roo.addon.timestamp", "com.rcaloras.roo.addon.timestamp",
				version, DependencyType.JAR, DependencyScope.PROVIDED);
	}
	public void remove(){
		
		initialize();
		removeAllAnnotations();

		//Remove the appropriate repository 
		if(version.toLowerCase().contains("snapshot")){
			projectOperations.removeRepository("", snapshotRepo);
		}
		else{
			projectOperations.removeRepository("", releaseRepo);
		}
		projectOperations.removeDependency("", timestampDependency);

	}
}