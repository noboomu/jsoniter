package com.jsoniter.spi;

import com.jsoniter.any.Any;
import com.jsoniter.output.JsonContext;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TypeLiteral<T> {

    public enum NativeType {
        FLOAT,
        DOUBLE,
        BOOLEAN,
        BYTE,
        SHORT,
        INT,
        CHAR,
        LONG,
        BIG_DECIMAL,
        BIG_INTEGER,
        STRING,
        OBJECT,
        ANY,
    }

    public static Map<Type, NativeType> nativeTypes = new HashMap<Type, NativeType>() {{
        put(float.class, NativeType.FLOAT);
        put(Float.class, NativeType.FLOAT);
        put(double.class, NativeType.DOUBLE);
        put(Double.class, NativeType.DOUBLE);
        put(boolean.class, NativeType.BOOLEAN);
        put(Boolean.class, NativeType.BOOLEAN);
        put(byte.class, NativeType.BYTE);
        put(Byte.class, NativeType.BYTE);
        put(short.class, NativeType.SHORT);
        put(Short.class, NativeType.SHORT);
        put(int.class, NativeType.INT);
        put(Integer.class, NativeType.INT);
        put(char.class, NativeType.CHAR);
        put(Character.class, NativeType.CHAR);
        put(long.class, NativeType.LONG);
        put(Long.class, NativeType.LONG);
        put(BigDecimal.class, NativeType.BIG_DECIMAL);
        put(BigInteger.class, NativeType.BIG_INTEGER);
        put(String.class, NativeType.STRING);
        put(Object.class, NativeType.OBJECT);
        put(Any.class, NativeType.ANY);
    }};

    private volatile static Map<Type, TypeLiteral> typeLiteralCache = new HashMap<Type, TypeLiteral>();
    final Type type;
    final String decoderCacheKey;
    final String encoderCacheKey;
    final NativeType nativeType;

    /**
     * Constructs a new type literal. Derives represented class from type parameter.
     * Clients create an empty anonymous subclass. Doing so embeds the type parameter in the
     * anonymous class's type hierarchy so we can reconstitute it at runtime despite erasure.
     */
    @SuppressWarnings("unchecked")
    protected TypeLiteral() {
        this.type = getSuperclassTypeParameter(getClass());
        nativeType = nativeTypes.get(this.type);
        decoderCacheKey = generateDecoderCacheKey(type);
        encoderCacheKey = generateEncoderCacheKey(type);
    }

    public TypeLiteral(Type type, String decoderCacheKey, String encoderCacheKey) {
        this.type = type;
        nativeType = nativeTypes.get(this.type);
        this.decoderCacheKey = decoderCacheKey;
        this.encoderCacheKey = encoderCacheKey;
    }

    private static String generateDecoderCacheKey(Type type) {
        return generateCacheKey(type, "decoder.");
    }

    private static String generateEncoderCacheKey(Type type) {
        return generateCacheKey(type, "encoder.");
    }

    private static String generateCacheKey(Type type, String prefix ) {
        StringBuilder decoderClassName = new StringBuilder(prefix);
        if (type instanceof Class) {
            Class clazz = (Class) type;
            if (clazz.isAnonymousClass()) {
                throw new JsonException("anonymous class not supported: " + clazz);
            }
            if (clazz.isArray()) {
                decoderClassName.append(clazz.getCanonicalName().replace("[]", "_array"));
            } else {
                // for nested class $
                decoderClassName.append(clazz.getName().replace("[]", "_array"));
            }
        } else if (type instanceof ParameterizedType) {
            try {
                ParameterizedType pType = (ParameterizedType) type;
                Type rType = pType.getRawType();

            	if(prefix.equals("decoder."))
            	{
            		if(rType instanceof ParameterizedTypeImpl)
                    {
           			 	ParameterizedTypeImpl pTypeImpl = (ParameterizedTypeImpl)rType;
           			 	
           			 	rType = Class.forName(pTypeImpl.getRawTypeName());
           			 	
                     }
            		
//            		Type[] typeArgs = Arrays.stream(pType.getActualTypeArguments()).toArray(Type[]::new);
            		
            		System.out.println("Decoder Type args: " + Arrays.toString(pType.getActualTypeArguments()));
            		
	                Class clazz = (Class) rType;
	                decoderClassName.append(clazz.getCanonicalName().replace("[]", "_array"));
	                for (int i = 0; i < pType.getActualTypeArguments().length; i++) {
	                    String typeName = formatTypeWithoutSpecialCharacter(pType.getActualTypeArguments()[i]);
	                     
	                    
	                    System.out.println("decoder class name: " + decoderClassName + " adding " + pType.getActualTypeArguments()[i]);
	                    	if( pType.getActualTypeArguments()[i] instanceof Class)
	                    	{	
	                    		Class<?> decoderArgClass = (Class<?>)pType.getActualTypeArguments()[i];
	                    		
	                    		if(((JsonContext.class).isAssignableFrom(decoderArgClass)))
	                    		{
	                    			System.out.println("Skipping class " + decoderArgClass);
	                    			continue;
	                    		} 
	                    	} 
	                    
	                    decoderClassName.append('_');
	                    decoderClassName.append(typeName);
	                }
            	}
            	else
            	{
             
               
                 
                 Type[] typeArgs = pType.getActualTypeArguments();
                Type contextType = null;
                
              //  System.err.println("pType: " + pType);
             //   System.err.println("rType: " + rType);
                
                for( Type t : typeArgs )
                {
                	Class<?> clazz = (Class<?>) t;
               // 	System.err.println("Type arg " + clazz + " is context: " + ((JsonContext.class).isAssignableFrom(clazz)));
                	if(((JsonContext.class).isAssignableFrom(clazz)))
                	{
                		 contextType = t;
                	 
                		 
                		 break;
                	}
                }
             //   System.err.println("args before: " + Arrays.toString(typeArgs) + " context: " + contextType);

                if(contextType != null)
                {
               //     System.err.println("context: " + contextType);
                    
                    if(rType instanceof ParameterizedTypeImpl)
                    {
           			 	ParameterizedTypeImpl pTypeImpl = (ParameterizedTypeImpl)rType;
           			 	
           			 	typeArgs =  pTypeImpl.getActualTypeArguments();
           			 	
                     }
                    
                    boolean insertType = false;
                    
                    for( Type t : typeArgs )
                    {
                    	if(t.equals(contextType))
                    	{
                    		insertType = false;
                    	}
                    }
                    
                    
               		Type[] newArgs = new Type[typeArgs.length+1];
               		
               		
             		System.arraycopy(typeArgs, 0, newArgs, 0, typeArgs.length);
            		newArgs[typeArgs.length] = pType.getActualTypeArguments()[0];
            		typeArgs = newArgs; 
//            		
//            		 pType = (ParameterizedType) pType.getRawType();
//            		 rType = pType.getRawType();
//            		 
            		//pType = (ParameterizedType) pType.getRawType();
                }
                
               // System.err.println("args after: " + Arrays.toString(typeArgs));
                /*
                 * 
                 		Type[] newArgs = new Type[typeArgs.length+1];
                		System.arraycopy(typeArgs, 0, newArgs, 0, typeArgs.length);
                		newArgs[typeArgs.length] = pType.getActualTypeArguments()[0];
                		pType = (ParameterizedType) pType.getRawType();
                		break;
                 */
                
                if(rType instanceof ParameterizedTypeImpl)
                {
       			 	ParameterizedTypeImpl pTypeImpl = (ParameterizedTypeImpl)rType;
       			 	
       			 	rType = Class.forName(pTypeImpl.getRawTypeName());
       			 	
                 }
                
             //   System.err.println("pType: " + pType);
             //   System.err.println("rType: " + rType);
                
                typeArgs = Arrays.stream(typeArgs).distinct().toArray(Type[]::new);
                
                Class clazz = (Class) rType;
                decoderClassName.append(clazz.getCanonicalName().replace("[]", "_array"));
                for (int i = 0; i < typeArgs.length; i++) {
                    String typeName = formatTypeWithoutSpecialCharacter(typeArgs[i]);
                    decoderClassName.append('_');
                    decoderClassName.append(typeName);
                }
                
                System.err.println(decoderClassName.toString());
            	}
            } catch (Exception e) {
                throw new JsonException("failed to generate cache key for: " + type, e);
            }
        } else if (type instanceof GenericArrayType) {
            GenericArrayType gaType = (GenericArrayType) type;
            Type compType = gaType.getGenericComponentType();
            decoderClassName.append(formatTypeWithoutSpecialCharacter(compType));
            decoderClassName.append("_array");
        }
        else {
         
            throw new UnsupportedOperationException("do not know how to handle: " + type);
        }
        
//        if(viewClazz != null)
//        {
//        	decoderClassName.append("_"+viewClazz.getSimpleName() + "View");
//        }
        return decoderClassName.toString().replace("$", "_");
    }

    private static String formatTypeWithoutSpecialCharacter(Type type) {
        if (type instanceof Class) {
            Class clazz = (Class) type;
            return clazz.getCanonicalName();
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            String typeName = formatTypeWithoutSpecialCharacter(pType.getRawType());
            for (Type typeArg : pType.getActualTypeArguments()) {
                typeName += "_";
                typeName += formatTypeWithoutSpecialCharacter(typeArg);
            }
            return typeName;
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType gaType = (GenericArrayType) type;
            return formatTypeWithoutSpecialCharacter(gaType.getGenericComponentType()) + "_array";
        }
        throw new JsonException("unsupported type: " + type + ", of class " + type.getClass());
    }

    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new JsonException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return parameterized.getActualTypeArguments()[0];
    }

    public static TypeLiteral create(Type valueType, Class viewClazz) {
    	
    	final Type type;
    	if(viewClazz != null)
    	{
            type = new ParameterizedTypeImpl(new Type[]{valueType},null, viewClazz);
    	}
    	else
    	{
    		type = valueType;
    	}
    	
    	System.out.println("\nCreating literal for " + type + " typeName: " + type.getTypeName() + " with view " + viewClazz);

 
        TypeLiteral typeLiteral = typeLiteralCache.get(type);
        if (typeLiteral != null) {
            return typeLiteral;
        }
        
       
        return createNew(valueType, viewClazz);
    }
    
    public static TypeLiteral create( Type valueType ) {
    	
         TypeLiteral typeLiteral = typeLiteralCache.get(valueType );
        if (typeLiteral != null) {
            return typeLiteral;
        }
        return createNew(valueType, null);
    }
    
     


    private synchronized static TypeLiteral createNew(Type valueType, Class viewClazz) {
    	
    	System.out.println("\nCreating literal for " + valueType + " typeName: " + valueType.getTypeName() + " with view " + viewClazz);

    	final Type type;
    	if(viewClazz != null)
    	{
            type = new ParameterizedTypeImpl(new Type[]{viewClazz},null, valueType);
    	}
    	else
    	{
    		type = valueType;
    	}
    	
        TypeLiteral typeLiteral = typeLiteralCache.get(type);
        if (typeLiteral != null) {
            return typeLiteral;
        }
        HashMap<Type, TypeLiteral> copy = new HashMap<Type, TypeLiteral>(typeLiteralCache);
        typeLiteral = new TypeLiteral(type,
                generateDecoderCacheKey(type),
                generateEncoderCacheKey(type));
        
        copy.put(type, typeLiteral);
        typeLiteralCache = copy;
        return typeLiteral;
    }

    public Type getType() {
        return type;
    }

    public String getDecoderCacheKey() {
        return decoderCacheKey;
    }

    public String getEncoderCacheKey() {
        return encoderCacheKey;
    }

    public NativeType getNativeType() {
        return nativeType;
    }

    @Override
    public String toString() {
        return "TypeLiteral{" +
                "type=" + type +
                ", decoderCacheKey='" + decoderCacheKey + '\'' +
                ", encoderCacheKey='" + encoderCacheKey + '\'' +
                '}';
    }
}