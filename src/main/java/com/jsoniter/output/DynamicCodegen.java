package com.jsoniter.output;

import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.EmptyEncoder;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.ViewEncoder;
import com.sun.org.apache.xml.internal.utils.StringBufferPool;
import javassist.*;

class DynamicCodegen {

    static ClassPool pool = ClassPool.getDefault();

    static {
        pool.insertClassPath(new ClassClassPath(Encoder.class));
    }

    public static Encoder gen(Class clazz, String cacheKey, CodegenResult source) throws Exception {
        source.flushBuffer();
        CtClass ctClass = pool.makeClass(cacheKey);
        ctClass.setInterfaces(new CtClass[]{pool.get(Encoder.class.getName())});
        ctClass.setSuperclass(pool.get(EmptyEncoder.class.getName()));
        String staticCode = source.toString();
        CtMethod staticMethod = CtNewMethod.make(staticCode, ctClass);
        ctClass.addMethod(staticMethod);
        String wrapperCode = source.generateWrapperCode(clazz);
        if ("true".equals(System.getenv("JSONITER_DEBUG"))) {
            System.out.println(">>> " + cacheKey);
            System.out.println(wrapperCode);
            System.out.println(staticCode);
        }
        
     
         CtMethod interfaceMethod = CtNewMethod.make(wrapperCode, ctClass);
 
        ctClass.addMethod(interfaceMethod);
        return (Encoder) ctClass.toClass().newInstance();
    }
    
    public static Encoder gen(Class clazz, Class<? extends JsonContext> viewClazz, String cacheKey, CodegenResult source) throws Exception {
     
    	if(viewClazz == null)
    	{
    		return gen(clazz,cacheKey,source);
    	}
    	
        source.flushBuffer();
        CtClass ctClass = pool.makeClass(cacheKey);
        ctClass.setInterfaces(new CtClass[]{pool.get(Encoder.class.getName())});
 
        ctClass.setSuperclass(pool.get(EmptyEncoder.class.getName()));
        String staticCode = source.toString();
        CtMethod staticMethod = CtNewMethod.make(staticCode, ctClass);
        ctClass.addMethod(staticMethod);
        String wrapperCode = source.generateWrapperCode(clazz);
        if ("true".equals(System.getenv("JSONITER_DEBUG"))) {
            System.out.println(">>> " + cacheKey);
            System.out.println(wrapperCode);
            System.out.println(staticCode);
        }
         
        CtMethod interfaceMethod = CtNewMethod.make(wrapperCode, ctClass);
        ctClass.addMethod(interfaceMethod);
        
      
        return (Encoder) ctClass.toClass().newInstance();
    }
}
