package com.jsoniter.output;

import com.jsoniter.spi.JsonException;
import com.jsoniter.any.Any;
import com.jsoniter.spi.*;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class CodegenImplNative {
    public static final Map<Type, Encoder> NATIVE_ENCODERS = new IdentityHashMap<Type, Encoder>() {{
        put(boolean.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Boolean) obj);
            }

            @Override
            public Any wrap(Object obj) {
                Boolean val = (Boolean) obj;
                return Any.wrap((boolean) val);
            }
        });
        put(Boolean.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Boolean) obj);
            }

            @Override
            public Any wrap(Object obj) {
                Boolean val = (Boolean) obj;
                return Any.wrap((boolean) val);
            }
        });
        put(byte.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal(((Byte) obj).shortValue());
            }

            @Override
            public Any wrap(Object obj) {
                Byte val = (Byte) obj;
                return Any.wrap((int) val);
            }
        });
        put(Byte.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal(((Byte) obj).shortValue());
            }

            @Override
            public Any wrap(Object obj) {
                Byte val = (Byte) obj;
                return Any.wrap((int) val);
            }
        });
        put(short.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Short) obj);
            }

            @Override
            public Any wrap(Object obj) {
                Short val = (Short) obj;
                return Any.wrap((int) val);
            }
        });
        put(Short.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Short) obj);
            }

            @Override
            public Any wrap(Object obj) {
                Short val = (Short) obj;
                return Any.wrap((int) val);
            }
        });
        put(int.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Integer) obj);
            }

            @Override
            public Any wrap(Object obj) {
                Integer val = (Integer) obj;
                return Any.wrap((int) val);
            }
        });
        put(Integer.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Integer) obj);
            }

            @Override
            public Any wrap(Object obj) {
                Integer val = (Integer) obj;
                return Any.wrap((int) val);
            }
        });
        put(char.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal(((Character) obj).charValue());
            }

            @Override
            public Any wrap(Object obj) {
                Character val = (Character) obj;
                return Any.wrap((int) val);
            }
        });
        put(Character.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal(((Character) obj).charValue());
            }

            @Override
            public Any wrap(Object obj) {
                Character val = (Character) obj;
                return Any.wrap((int) val);
            }
        });
        put(long.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Long) obj);
            }

            @Override
            public Any wrap(Object obj) {
                Long val = (Long) obj;
                return Any.wrap((long) val);
            }
        });
        put(Long.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Long) obj);
            }

            @Override
            public Any wrap(Object obj) {
                Long val = (Long) obj;
                return Any.wrap((long) val);
            }
        });
        put(float.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Float) obj);
            }

            @Override
            public Any wrap(Object obj) {
                Float val = (Float) obj;
                return Any.wrap((float) val);
            }
        });
        put(Float.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Float) obj);
            }

            @Override
            public Any wrap(Object obj) {
                Float val = (Float) obj;
                return Any.wrap((float) val);
            }
        });
        put(double.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Double) obj);
            }

            @Override
            public Any wrap(Object obj) {
                Double val = (Double) obj;
                return Any.wrap((double) val);
            }
        });
        put(Double.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Double) obj);
            }

            @Override
            public Any wrap(Object obj) {
                Double val = (Double) obj;
                return Any.wrap((double) val);
            }
        });
        put(String.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((String) obj);
            }

            @Override
            public Any wrap(Object obj) {
                String val = (String) obj;
                return Any.wrap(val);
            }
        });
        put(Object.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                if (obj != null && obj.getClass() == Object.class) {
                    stream.writeEmptyObject();
                    return;
                }
                stream.writeVal(obj);
            }

            @Override
            public Any wrap(Object obj) {
                if (obj != null && obj.getClass() == Object.class) {
                    return Any.rewrap(new HashMap<String, Any>());
                }
                return CodegenAccess.wrap(obj);
            }
        });

        put(BigDecimal.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                BigDecimal val = (BigDecimal) obj;
                stream.writeRaw(val.toString());
            }

            @Override
            public Any wrap(Object obj) {
                return Any.wrap(obj.toString());
            }
        });
        put(BigInteger.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                BigInteger val = (BigInteger) obj;
                stream.writeRaw(val.toString());
            }

            @Override
            public Any wrap(Object obj) {
                return Any.wrap(obj.toString());
            }
        });
    }};

    public static void genWriteOp(CodegenResult ctx, String code, Type valueType,  Class<? extends JsonContext> viewClazz,boolean isNullable) {
        genWriteOp(ctx, code, valueType,viewClazz,  isNullable, true);
    }

    public static void genWriteOp(CodegenResult ctx, String code, Type valueType, Class<? extends JsonContext> viewClazz, boolean isNullable, boolean isCollectionValueNullable) {
    	 
    	final Type type = valueType;
    	 
        String cacheKey = TypeLiteral.create(type,viewClazz).getEncoderCacheKey();
        if (JsoniterSpi.getEncoder(cacheKey) == null) {
            if (!isNullable && String.class == type) {
                ctx.buffer('"');
                ctx.append(String.format("com.jsoniter.output.CodegenAccess.writeStringWithoutQuote((java.lang.String)%s, stream);", code));
                ctx.buffer('"');
                return;
            }
            if (NATIVE_ENCODERS.containsKey(type)) {
                ctx.append(String.format("stream.writeVal((%s)%s);", getTypeName(type), code));
                return;
            }
        }

        if (!isCollectionValueNullable) {
            cacheKey = cacheKey + "__value_not_nullable";
        }

        Codegen.getEncoder(cacheKey, type,viewClazz);
        CodegenResult generatedSource = Codegen.getGeneratedSource(cacheKey);
        if (generatedSource != null) {
            if (isNullable) {
                ctx.appendBuffer();
                ctx.append(CodegenResult.bufferToWriteOp(generatedSource.prelude));
                ctx.append(String.format("%s.encode_((%s)%s, stream);", cacheKey, getTypeName(type), code));
                ctx.append(CodegenResult.bufferToWriteOp(generatedSource.epilogue));
            } else {
                ctx.buffer(generatedSource.prelude);
                ctx.append(String.format("%s.encode_((%s)%s, stream);", cacheKey, getTypeName(type), code));
                ctx.buffer(generatedSource.epilogue);
            }
        } else {
            ctx.append(String.format("com.jsoniter.output.CodegenAccess.writeVal(\"%s\", (%s)%s, stream);", cacheKey, getTypeName(type), code));
        }
    }

    public static String getTypeName(Type fieldType) {
        if (fieldType instanceof Class) {
            Class clazz = (Class) fieldType;
            return clazz.getCanonicalName();
        } else if (fieldType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) fieldType;
            Class clazz = (Class) pType.getRawType();
            return clazz.getCanonicalName();
        } else {
            throw new JsonException("unsupported type: " + fieldType);
        }
    }
    public static CodegenResult genEnum(Class clazz) {
        CodegenResult ctx = new CodegenResult();
        ctx.append(String.format("public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {", clazz.getCanonicalName()));
        ctx.append("if (obj == null) { stream.writeNull(); return; }");
        ctx.buffer('"');
        ctx.append("stream.writeRaw(obj.toString());");
        ctx.buffer('"');
        ctx.append("}");
        return ctx;
    }
}
