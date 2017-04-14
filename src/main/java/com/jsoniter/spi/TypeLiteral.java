package com.jsoniter.spi;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.jsoniter.any.Any;
import com.jsoniter.output.JsonContext;

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

    private volatile static Map<Pair<Type,Class<? extends JsonContext>>,TypeLiteral> contextTypeLiteralCache = new HashMap<>();
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
            		 
	                Class clazz = (Class) rType;
	                decoderClassName.append(clazz.getCanonicalName().replace("[]", "_array"));
	                for (int i = 0; i < pType.getActualTypeArguments().length; i++) {
	                    String typeName = formatTypeWithoutSpecialCharacter(pType.getActualTypeArguments()[i]);
	                      
 	                    	if( pType.getActualTypeArguments()[i] instanceof JsonContext)
	                    	{	
//	                    		Class<?> decoderArgClass = (Class<?>)pType.getActualTypeArguments()[i];
//	                    		
//	                    		if(((JsonContext.class).isAssignableFrom(decoderArgClass)))
//	                    		{
	                    			//System.out.println("Skipping class " + decoderArgClass);
	                    			continue;
	                    		//} 
	                    	} 
	                    
	                    decoderClassName.append('_');
	                    decoderClassName.append(typeName);
	                }
            	}
            	else
            	{
              
                Type[] typeArgs = pType.getActualTypeArguments();
                Type contextType = null;
              
                for( Type t : typeArgs )
                {
                 	if( t instanceof JsonContext)
                	{ 
//                		Class<?> clazz = (Class<?>) t;
//                		if(((JsonContext.class).isAssignableFrom(clazz)))
//                    	{
                    		 contextType = t; 
                    		 break;
                    	//}
                	} 
                }
            
                if(contextType != null)
                { 
                    if(rType instanceof ParameterizedTypeImpl)
                    {
           			 	ParameterizedTypeImpl pTypeImpl = (ParameterizedTypeImpl)rType;
           			 	
           			 	typeArgs =  pTypeImpl.getActualTypeArguments(); 
                     }
                      
               		Type[] newArgs = new Type[typeArgs.length+1]; 
             		System.arraycopy(typeArgs, 0, newArgs, 0, typeArgs.length);
            		newArgs[typeArgs.length] = pType.getActualTypeArguments()[0];
            		typeArgs = newArgs;  
                }
                
                if(rType instanceof ParameterizedTypeImpl)
                {
       			 	ParameterizedTypeImpl pTypeImpl = (ParameterizedTypeImpl)rType;
       			 	
       			 	rType = Class.forName(pTypeImpl.getRawTypeName()); 
                 }
           
                typeArgs = Arrays.stream(typeArgs).distinct().toArray(Type[]::new);
                
                Class clazz = (Class) rType;
                decoderClassName.append(clazz.getCanonicalName().replace("[]", "_array"));
                for (int i = 0; i < typeArgs.length; i++) {
                    String typeName = formatTypeWithoutSpecialCharacter(typeArgs[i]);
                    decoderClassName.append('_');
                    decoderClassName.append(typeName);
                }
                
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

    public static TypeLiteral create(final Type valueType, final Class<? extends JsonContext> viewClazz) {
    	
    	//final Type type;
    	
//    	if(valueType instanceof ParameterizedTypeImpl)
//    	{
//    		ParameterizedTypeImpl pType = (ParameterizedTypeImpl)valueType;
//    		if( pType.hasJsonContext() )
//    		{
//    			viewClazz = null;
//    		}
//    	}
    	
     	 
    	if(viewClazz != null)
    	{
    		final TypeLiteral typeLiteral = contextTypeLiteralCache.get(Pair.of(valueType, viewClazz));
    		
    		if(typeLiteral != null)
    		{
    			return typeLiteral;
    		}
    		
          //  type = new ParameterizedTypeImpl(new Type[]{valueType},null, viewClazz);
    	}
//    	else
//    	{
//    		type = valueType;
//    	}
    	 
    	final TypeLiteral  typeLiteral = typeLiteralCache.get(valueType);
        if (typeLiteral != null) {
            return typeLiteral;
        }
        
       
        return createNew(valueType, viewClazz);
    }
    
    public static TypeLiteral create( final Type valueType ) {
    	
    	final TypeLiteral typeLiteral = typeLiteralCache.get(valueType );
        if (typeLiteral != null) {
            return typeLiteral;
        }
        return createNew(valueType, null);
    }
    
     


    private synchronized static TypeLiteral createNew(final Type valueType, final Class<? extends JsonContext> viewClazz) {
    	 
    	final Type type;
    	
    	if(viewClazz != null)
    	{
            type = new ParameterizedTypeImpl(new Type[]{valueType},null, viewClazz);
    	}
    	else
    	{
    		type = valueType;
    	}
    	
//        TypeLiteral typeLiteral = typeLiteralCache.get(type);
//        if (typeLiteral != null) {
//            return typeLiteral;
//        }
    	final HashMap<Type, TypeLiteral> copy = new HashMap<Type, TypeLiteral>(typeLiteralCache);
    	final TypeLiteral typeLiteral = new TypeLiteral(type,
                generateDecoderCacheKey(type),
                generateEncoderCacheKey(type));
        
        copy.put(type, typeLiteral);
        typeLiteralCache = copy;
        
        if(viewClazz != null)
        {
        	final Map<Pair<Type,Class<? extends JsonContext>>,TypeLiteral> contextCopy = new HashMap<>(contextTypeLiteralCache);
        	contextCopy.put(Pair.of(valueType, viewClazz), typeLiteral);
        	contextTypeLiteralCache = contextCopy;
        }
        
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