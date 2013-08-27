package com.rcaloras.roo.addon.timestamp;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.roo.classpath.PhysicalTypeIdentifierNamingUtils;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.details.BeanInfoUtils;
import org.springframework.roo.classpath.details.FieldMetadata;
import org.springframework.roo.classpath.details.FieldMetadataBuilder;
import org.springframework.roo.classpath.details.ItdTypeDetailsBuilder;
import org.springframework.roo.classpath.details.MethodMetadata;
import org.springframework.roo.classpath.details.MethodMetadataBuilder;
import org.springframework.roo.classpath.details.annotations.AnnotatedJavaType;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadataBuilder;
import org.springframework.roo.classpath.itd.AbstractItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.classpath.itd.InvocableMemberBodyBuilder;
import org.springframework.roo.metadata.MetadataIdentificationUtils;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.LogicalPath;

/**
 * This type produces metadata for a new ITD. It uses an {@link ItdTypeDetailsBuilder} provided by
 * {@link AbstractItdTypeDetailsProvidingMetadataItem} to register a field in the ITD and a new method.
 *
 * @since 1.1.0
 */
public class TimestampMetadata extends AbstractItdTypeDetailsProvidingMetadataItem {

    // Constants
    private static final String PROVIDES_TYPE_STRING = TimestampMetadata.class.getName();
    private static final String PROVIDES_TYPE = MetadataIdentificationUtils.create(PROVIDES_TYPE_STRING);

    private static final String CREATED_FIELD="created";
	private static final String UPDATED_FIELD="updated";

    public static final String getMetadataIdentiferType() {
        return PROVIDES_TYPE;
    }

    public static final String createIdentifier(JavaType javaType, LogicalPath path) {
        return PhysicalTypeIdentifierNamingUtils.createIdentifier(PROVIDES_TYPE_STRING, javaType, path);
    }

    public static final JavaType getJavaType(String metadataIdentificationString) {
        return PhysicalTypeIdentifierNamingUtils.getJavaType(PROVIDES_TYPE_STRING, metadataIdentificationString);
    }

    public static final LogicalPath getPath(String metadataIdentificationString) {
        return PhysicalTypeIdentifierNamingUtils.getPath(PROVIDES_TYPE_STRING, metadataIdentificationString);
    }

    public static boolean isValid(String metadataIdentificationString) {
        return PhysicalTypeIdentifierNamingUtils.isValid(PROVIDES_TYPE_STRING, metadataIdentificationString);
    }

    public TimestampMetadata(String identifier, JavaType aspectName, PhysicalTypeMetadata governorPhysicalTypeMetadata) {
        super(identifier, aspectName, governorPhysicalTypeMetadata);
        Validate.isTrue(isValid(identifier), "Metadata identification string '" + identifier + "' does not appear to be a valid");

        FieldMetadata createdField = getTimestampField(CREATED_FIELD);
        FieldMetadata updatedField = getTimestampField(UPDATED_FIELD);
        builder.addField(createdField);
		builder.addField(updatedField);

        // Adding a new sample method definition
		builder.addMethod(getTimestampMethod());

		// Create getters and setters for created and updated
		builder.addMethod(this.getDeclaredGetter(createdField));
		builder.addMethod(this.getDeclaredSetter(createdField));
		builder.addMethod(this.getDeclaredGetter(updatedField));
		builder.addMethod(this.getDeclaredSetter(updatedField));
        // Create a representation of the desired output ITD
        itdTypeDetails = builder.build();
    }


    private FieldMetadata getTimestampField(String fieldName){

    	List<AnnotationMetadataBuilder> list = new ArrayList<AnnotationMetadataBuilder>();
		AnnotationMetadataBuilder dateTimeFormat=new AnnotationMetadataBuilder(new JavaType("org.springframework.format.annotation.DateTimeFormat"));
		dateTimeFormat.addStringAttribute("pattern", "yyyy-MM-dd HH:mm:ss");

		AnnotationMetadataBuilder temporal=new AnnotationMetadataBuilder(new JavaType("javax.persistence.Temporal"));
		temporal.addEnumAttribute("value", "javax.persistence.TemporalType", "TIMESTAMP");

		list.add(temporal);
		list.add(dateTimeFormat);

		// Using the FieldMetadataBuilder to create the field definition.
		FieldMetadataBuilder fieldBuilder = new FieldMetadataBuilder(getId(), // Metadata ID provided by supertype
			Modifier.PRIVATE, // Using package protection rather than private
			list, // No annotations for this field
			new JavaSymbolName(fieldName), // Field name
			new JavaType("java.util.Date")); // Field type

		return fieldBuilder.build(); // Build and return a FieldMetadata instance

    }

    private MethodMetadata getTimestampMethod() {

		// Specify the desired method name
		JavaSymbolName methodName = new JavaSymbolName("onUpdate");

		// Check if a method with the same signature already exists in the target type
		MethodMetadata method = methodExists(methodName, new ArrayList<AnnotatedJavaType>());
		if (method != null) {
			// If it already exists, just return the method and omit its generation via the ITD
			return method;
		}

		AnnotationMetadataBuilder prePersists=new AnnotationMetadataBuilder(new JavaType("javax.persistence.PrePersist"));
		AnnotationMetadataBuilder preUpdate=new AnnotationMetadataBuilder(new JavaType("javax.persistence.PreUpdate"));

		// Define method annotations
		List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();
		annotations.add(prePersists);
		annotations.add(preUpdate);

		// Define method throws types (none in this case)
		List<JavaType> throwsTypes = new ArrayList<JavaType>();

		// Define method parameter types (none in this case)
		List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();

		// Define method parameter names (none in this case)
		List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();

		// Create the method body
		InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
		bodyBuilder.appendFormalLine("if (this.created == null) {");
		bodyBuilder.appendFormalLine("    this.created = new java.util.Date();");
		bodyBuilder.appendFormalLine("}");
		bodyBuilder.appendFormalLine("this.updated = new java.util.Date();");

		// Use the MethodMetadataBuilder for easy creation of MethodMetadata
		MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, JavaType.VOID_PRIMITIVE, parameterTypes, parameterNames, bodyBuilder);
		methodBuilder.setAnnotations(annotations);
		methodBuilder.setThrowsTypes(throwsTypes);
		return methodBuilder.build(); // Build and return a MethodMetadata instance
	}

    private MethodMetadata methodExists(JavaSymbolName methodName, List<AnnotatedJavaType> paramTypes) {
        // We have no access to method parameter information, so we scan by name alone and treat any match as authoritative
        // We do not scan the superclass, as the caller is expected to know we'll only scan the current class
        for (MethodMetadata method : governorTypeDetails.getDeclaredMethods()) {
            if (method.getMethodName().equals(methodName) && method.getParameterTypes().equals(paramTypes)) {
                // Found a method of the expected name; we won't check method parameters though
                return method;
            }
        }
        return null;
    }

    @Override
	public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("identifier", getId());
        builder.append("valid", valid);
        builder.append("aspectName", aspectName);
        builder.append("destinationType", destination);
        builder.append("governor", governorPhysicalTypeMetadata.getId());
        builder.append("itdTypeDetails", itdTypeDetails);
        return builder.toString();
    }

    /**
     * Obtains the specific accessor method that is either contained within the
     * normal Java compilation unit or will be introduced by this add-on via an
     * ITD.
     *
     * @param field that already exists on the type either directly or via
     *            introduction (required; must be declared by this type to be
     *            located)
     * @return the method corresponding to an accessor, or null if not found
     */
    private MethodMetadataBuilder getDeclaredGetter(final FieldMetadata field) {
        Validate.notNull(field, "Field required");

        // Compute the mutator method name
        final JavaSymbolName methodName = BeanInfoUtils
                .getAccessorMethodName(field);

        // See if the type itself declared the accessor
        if (governorHasMethod(methodName)) {
            return null;
        }

        // Decide whether we need to produce the accessor method (see ROO-619
        // for reason we allow a getter for a final field)
        if (!Modifier.isTransient(field.getModifier())
                && !Modifier.isStatic(field.getModifier())) {
            final InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
            bodyBuilder.appendFormalLine("return this."
                    + field.getFieldName().getSymbolName() + ";");

            return new MethodMetadataBuilder(getId(), Modifier.PUBLIC,
                    methodName, field.getFieldType(), bodyBuilder);
        }

        return null;
    }

    /**
     * Obtains the specific mutator method that is either contained within the
     * normal Java compilation unit or will be introduced by this add-on via an
     * ITD.
     *
     * @param field that already exists on the type either directly or via
     *            introduction (required; must be declared by this type to be
     *            located)
     * @return the method corresponding to a mutator, or null if not found
     */
    private MethodMetadataBuilder getDeclaredSetter(final FieldMetadata field) {
        Validate.notNull(field, "Field required");

        // Compute the mutator method name
        final JavaSymbolName methodName = BeanInfoUtils
                .getMutatorMethodName(field);

        // Compute the mutator method parameters
        final JavaType parameterType = field.getFieldType();

        // See if the type itself declared the mutator
        if (governorHasMethod(methodName, parameterType)) {
            return null;
        }

        // Compute the mutator method parameter names
        final List<JavaSymbolName> parameterNames = Arrays.asList(field
                .getFieldName());

        // Decide whether we need to produce the mutator method (disallowed for
        // final fields as per ROO-36)
        if (!Modifier.isTransient(field.getModifier())
                && !Modifier.isStatic(field.getModifier())
                && !Modifier.isFinal(field.getModifier())) {
            final InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
            bodyBuilder.appendFormalLine("this."
                    + field.getFieldName().getSymbolName() + " = "
                    + field.getFieldName().getSymbolName() + ";");

            return new MethodMetadataBuilder(getId(), Modifier.PUBLIC,
                    methodName, JavaType.VOID_PRIMITIVE,
                    AnnotatedJavaType.convertFromJavaTypes(parameterType),
                    parameterNames, bodyBuilder);
        }

        return null;
    }
}
