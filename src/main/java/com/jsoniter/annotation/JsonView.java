/**
 * 
 */
package com.jsoniter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 

/**
 * @author jbauer
 *
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD,
    ElementType.PARAMETER, // since 2.5
    ElementType.TYPE // since 2.9, to indicate "default view" for properties
})
@Retention(RetentionPolicy.RUNTIME) 
public @interface JsonView {
/**
 * View or views that annotated element is part of. Views are identified
 * by classes, and use expected class inheritance relationship: child
 * views contain all elements parent views have, for example.
 */
public Class<?>[] value() default { };
}