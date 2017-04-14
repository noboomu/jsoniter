/**
 * 
 */
package com.jsoniter.views;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
 
import net.openhft.compiler.CompilerUtils;

/**
 * @author jbauer
 *
 */
public class ObjectViewClassSupport
{

	private static final Map<ObjectViewEntry,Class<?>> OBJECT_VIEW_CLASS_MAP = new HashMap<>();
	
	public static Class<?> lookupObjectViewClass(Class<?> objectClass, Class<?> viewClass)
	{
		ObjectViewEntry entry = new ObjectViewEntry(objectClass,viewClass);
		Class<?> objectViewClass = OBJECT_VIEW_CLASS_MAP.get(entry);
		if(objectViewClass != null)
		{
			return objectViewClass;
		}
		else
		{
			String className = String.format("%s%sView", objectClass.getCanonicalName() , viewClass.getSimpleName()).replaceAll("\\.", "_");
			System.out.println("className: " + className);
			
			TypeSpec objectViewType = TypeSpec.classBuilder(className)
				    .addModifiers(Modifier.PUBLIC, Modifier.FINAL) 
				    .build();

			JavaFile javaFile = JavaFile.builder("com.jsoniter.views", objectViewType)
				    .build();
			
			System.out.println(javaFile.toString());
			
			try
			{
				
				objectViewClass = CompilerUtils.CACHED_COMPILER.loadFromJava("com.jsoniter.views." + className, javaFile.toString());
				OBJECT_VIEW_CLASS_MAP.put(entry, objectViewClass);
				
			} catch (ClassNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return objectViewClass;
		}
		
	}

}
