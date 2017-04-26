package com.jsoniter.output;

import com.jsoniter.spi.JsonException;
import com.jsoniter.any.Any;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.TypeLiteral;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class JsonStream extends OutputStream {

	public static final byte ESCAPE = '\\';

	public static final byte OBJECT_START = '{';
	public static final byte OBJECT_END = '}';
	public static final byte ARRAY_START = '[';
	public static final byte ARRAY_END = ']';
	public static final byte COMMA = ',';
	public static final byte SEMI = ':'; 
	public static final byte SPACE = ' '; 
	public static final byte QUOTE = '"';
	public static final byte UNICODE = 'u';
	public static final byte NEWLINE = '\n';
	public static final byte BACKSPACE = '\b';
	public static final byte TAB = '\t';
	public static final byte CARRAIGE_RETURN = '\r';
	public static final byte FORMFEED = '\f';
	public static final byte MINUS = '-';
	public static final byte ZERO = '0';
	public static final byte PERIOD = '.';

	public static final byte[] ESCAPE_BACKSPACE = new byte[]{ESCAPE, (byte)'b'};
	public static final byte[] ESCAPE_NEWLINE = new byte[]{ESCAPE, (byte)'n'};
	public static final byte[] ESCAPE_TAB = new byte[]{ESCAPE, (byte)'t'};
	public static final byte[] ESCAPE_FORMFEED = new byte[]{ESCAPE, (byte)'f'};
	public static final byte[] ESCAPE_CARRIAGE_RETURN = new byte[]{ESCAPE, (byte)'r'};

 	public static final byte[] NULL = new byte[]{(byte) 'n', (byte) 'u', (byte) 'l', (byte) 'l'};
	public static final byte[] TRUE = new byte[]{(byte) 't', (byte) 'r', (byte) 'u', (byte) 'e'};
	public static final byte[] FALSE = new byte[]{(byte) 'f', (byte) 'a', (byte) 'l', (byte) 's', (byte) 'e'};


    public static int defaultIndentionStep = 0;
    public int indentionStep = defaultIndentionStep;
    private int indention = 0;
    private OutputStream out;
    byte buf[];
    int count;

    public JsonStream(OutputStream out, int bufSize) {
        if (bufSize < 32) {
            throw new JsonException("buffer size must be larger than 32: " + bufSize);
        }
        this.out = out;
        this.buf = new byte[bufSize];
    }
    
//    public JsonStream(int bufSize) {
//        if (bufSize < 32) {
//            throw new JsonException("buffer size must be larger than 32: " + bufSize);
//        }
//        this.out = new ByteArrayOutputStream(bufSize);
//        this.buf = new byte[bufSize];
//    }

    public void reset(OutputStream out) {
        this.out = out;
        this.count = 0;
    }
    
   
    public final void write(int b) throws IOException {
        if (count == buf.length) {
            flushBuffer();
        }
        buf[count++] = (byte) b;
    }

    public final void writeByte(byte b) throws IOException { 
    	 if (count >= buf.length - 1) {
             flushBuffer();
         }
        buf[count++] = b;
    }
    
    public final void writeByte(char c) throws IOException { 
   	 if (count >= buf.length - 1) {
            flushBuffer();
        }
       buf[count++] = (byte)c;
   }
    
    public final void write(byte b1, byte b2) throws IOException {
        if (count >= buf.length - 1) {
            flushBuffer();
        }
        buf[count++] = b1;
        buf[count++] = b2;
    }

    public final void write(byte b1, byte b2, byte b3) throws IOException {
        if (count >= buf.length - 2) {
            flushBuffer();
        }
        buf[count++] = b1;
        buf[count++] = b2;
        buf[count++] = b3;
    }

    public final void write(byte b1, byte b2, byte b3, byte b4) throws IOException {
        if (count >= buf.length - 3) {
            flushBuffer();
        }
        buf[count++] = b1;
        buf[count++] = b2;
        buf[count++] = b3;
        buf[count++] = b4;
    }

    public final void write(byte b1, byte b2, byte b3, byte b4, byte b5) throws IOException {
        if (count >= buf.length - 4) {
            flushBuffer();
        }
        buf[count++] = b1;
        buf[count++] = b2;
        buf[count++] = b3;
        buf[count++] = b4;
        buf[count++] = b5;
    }

    public final void write(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6) throws IOException {
        if (count >= buf.length - 5) {
            flushBuffer();
        }
        buf[count++] = b1;
        buf[count++] = b2;
        buf[count++] = b3;
        buf[count++] = b4;
        buf[count++] = b5;
        buf[count++] = b6;
    }

    public final void write(byte b[], int off, int len) throws IOException {
        if (len >= buf.length - count) {
            if (len >= buf.length) {
            /* If the request length exceeds the size of the output buffer,
               flush the output buffer and then write the data directly.
               In this way buffered streams will cascade harmlessly. */
                flushBuffer();
                out.write(b, off, len);
                return;
            }
            flushBuffer();
        }
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }

    public void flush() throws IOException {
        flushBuffer();
        out.flush();
    }

    @Override
    public void close() throws IOException {
        if (count > 0) {
            flushBuffer();
        }
        out.close();
        this.out = null;
        count = 0;
    }

    final void flushBuffer() throws IOException {
        out.write(buf, 0, count);
        count = 0;
    }

    public final void writeVal(final String val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            StreamImplString.writeString(this, val);
        }
    }
    
    public final void writeString(final String val) throws IOException {
//        if (val == null) {
//            writeNull();
//        } else {
            StreamImplString.writeString(this, val);
      //  }
    }

    public final void writeRaw(final String val) throws IOException {
        writeRaw(val, val.length());
    }

    public final void writeRaw(final String val, int remaining) throws IOException {
        int i = 0;
        for (; ; ) {
           final int available = buf.length - count;
            if (available < remaining) {
                remaining -= available;
                final int j = i + available;
                val.getBytes(i, j, buf, count);
                count = buf.length;
                flushBuffer();
                i = j;
            } else {
            	final int j = i + remaining;
                val.getBytes(i, j, buf, count);
                count += remaining;
                return;
            }
        }
    }

    public final void writeVal(final Boolean val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            if (val) {
                writeTrue();
            } else {
                writeFalse();
            }
        }
    }
    
    public final void writeBoolean(final Boolean val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            if (val) {
                writeTrue();
            } else {
                writeFalse();
            }
        }
    }

    public final void writeVal(final boolean val) throws IOException {
        if (val) {
            writeTrue();
        } else {
            writeFalse();
        }
    }

    public final void writeTrue() throws IOException {
        write(TRUE);
    }

    public final void writeFalse() throws IOException {
        write(FALSE);
    }

    public final void writeVal(final Short val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            writeVal(val.intValue());
        }
    }

    public final void writeVal(final short val) throws IOException {
        writeVal((int) val);
    }

    public final void writeVal(final Integer val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            writeVal(val.intValue());
        }
    }

    public final void writeVal(final int val) throws IOException {
        StreamImplNumber.writeInt(this, val);
    }


    public final void writeVal(final Long val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            writeVal(val.longValue());
        }
    }
 

    public final void writeVal(long val) throws IOException {
        StreamImplNumber.writeLong(this, val);
    }


    public final void writeVal(final Float val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            writeVal(val.floatValue());
        }
    }
    

   

    public final void writeVal(final float val) throws IOException {
        StreamImplNumber.writeFloat(this, val);
    }

    public final void writeVal(final Double val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            writeVal(val.doubleValue());
        }
    }
    
    
    public final void writeLong(final Long val) throws IOException {
    	  if (val == null) {
              writeNull();
          }
    	  else
    	  {
    	     StreamImplNumber.writeLong(this, val); 
    	  }
    }
    
    public final void writeFloat(final Float val) throws IOException {
        if (val == null) {
            writeNull();
        } else {
            StreamImplNumber.writeFloat(this, val);
        }
    }
    
    public final void writeInt(final Integer val) throws IOException {
    	if (val == null) {
            writeNull();
        } else {
        	
            StreamImplNumber.writeInt(this, val);

        }
     }
    
    public final void writeDouble(final Double val) throws IOException {
    	if (val == null) {
            writeNull();
        } else {
        	
            StreamImplNumber.writeDouble(this, val);

        }
     }
    
    public final void writeShort(final Short val) throws IOException {
    	if (val == null) {
            writeNull();
        } else {
        	
            StreamImplNumber.writeInt(this, val);

        }
     }

    public final void writeVal(final double val) throws IOException {
        StreamImplNumber.writeDouble(this, val);
    }

    public final void writeVal(final Any val) throws IOException {
        val.writeTo(this);
    }

    public final void writeNull() throws IOException {
        write(NULL);
    }

    public final void writeEmptyObject() throws IOException {
        write(JsonStream.OBJECT_START, JsonStream.OBJECT_END);
    }

    public final void writeEmptyArray() throws IOException {
        write(JsonStream.ARRAY_START, JsonStream.ARRAY_END);
    }

    public final void writeArrayStart() throws IOException {
        indention += indentionStep;
        write(JsonStream.ARRAY_START);
        writeIndention();
    }

    public final void writeMore() throws IOException {
        write(JsonStream.COMMA);
        writeIndention();
    }

    private void writeIndention() throws IOException {
        writeIndention(0);
    }

    private void writeIndention(final int delta) throws IOException {
        if (indention == 0) {
            return;
        }
        write(NEWLINE);
        final int toWrite = indention - delta;
        int i = 0;
        for (; ; ) {
            for (; i < toWrite && count < buf.length; i++) {
                buf[count++] = SPACE;
            }
            if (i == toWrite) {
                break;
            } else {
                flushBuffer();
            }
        }
    }

    public final void writeArrayEnd() throws IOException {
        writeIndention(indentionStep);
        indention -= indentionStep;
        write(ARRAY_END);
    }

    public final void writeObjectStart() throws IOException {
        indention += indentionStep;
        write(OBJECT_START);
        writeIndention();
    }

    public final void writeObjectField(final String field) throws IOException {
        writeVal(field);
        write(SEMI);
    }

    public final void writeObjectEnd() throws IOException {
        writeIndention(indentionStep);
        indention -= indentionStep;
        write(OBJECT_END);
    }

    public final  void writeVal(final Object obj) throws IOException {
        if (obj == null) {
            writeNull();
            return;
        }
        final Class<?> clazz = obj.getClass();
        final String cacheKey = TypeLiteral.create(clazz).getEncoderCacheKey();
        Codegen.getEncoder(cacheKey, clazz,null).encode(obj, this);
    }

    public final  void writeVal(final TypeLiteral  typeLiteral, final Object obj) throws IOException {
        if (null == obj) {
            writeNull();
        } else {
            Codegen.getEncoder(typeLiteral.getEncoderCacheKey(), typeLiteral.getType(),null).encode(obj, this);
        }
    }
    
    public final  void writeViewVal(final Object obj, final Class<? extends JsonContext> viewClass) throws IOException {
        if (obj == null) {
            writeNull();
            return;
        }
        final Class clazz = obj.getClass();
        final String cacheKey = TypeLiteral.create(clazz,viewClass).getEncoderCacheKey();
    
         Codegen.getEncoder(cacheKey, clazz,viewClass).encode(obj, this);
    }

    public final   void writeViewVal(final TypeLiteral typeLiteral, final Object obj, final Class<? extends JsonContext> viewClass) throws IOException {
        if (null == obj) {
            writeNull();
        } else {
            Codegen.getEncoder(typeLiteral.getEncoderCacheKey(), typeLiteral.getType(),viewClass).encode(obj, this);
        }
    }

    private final static ThreadLocal<JsonStream> tlsStream = new ThreadLocal<JsonStream>() {
        @Override
        protected JsonStream initialValue() {
            return new JsonStream(null, 4096);
        }
    };
    
    public static JsonStream localStream()
    {
    	AsciiOutputStream asciiOutputStream = tlsAsciiOutputStream.get();
        asciiOutputStream.reset();
           
        JsonStream stream = tlsStream.get();
        
        stream.reset(asciiOutputStream);
        
        return stream;
    }

    public static   void  serialize(final Object obj, final OutputStream out) 
    {
    	final JsonStream stream = tlsStream.get();
        try {
            try {
                stream.reset(out);
                stream.writeVal(obj);
            } finally 
            {
                stream.close();
            }
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }
    
    public static   void serialize(final Object obj, final Class<? extends JsonContext> viewClazz, final OutputStream out) {
    	final JsonStream stream = tlsStream.get();
        try {
            try {
                stream.reset(out);
                stream.writeViewVal(obj,viewClazz);
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    private final static ThreadLocal<AsciiOutputStream> tlsAsciiOutputStream = new ThreadLocal<AsciiOutputStream>() {
        @Override
        protected AsciiOutputStream initialValue() {
            return new AsciiOutputStream();
        }
    };
    
    public static  String serialize(Object obj,Class<? extends JsonContext> viewClazz) {
        AsciiOutputStream asciiOutputStream = tlsAsciiOutputStream.get();
        asciiOutputStream.reset();
        serialize(obj, viewClazz, asciiOutputStream);
        return asciiOutputStream.toString();
    }

    public static String serialize(Object obj) {
        AsciiOutputStream asciiOutputStream = tlsAsciiOutputStream.get();
        asciiOutputStream.reset();
        serialize(obj, asciiOutputStream);
        return asciiOutputStream.toString();
    }

    public static void setMode(EncodingMode mode) {
        Codegen.setMode(mode);
    }

    public static void registerNativeEncoder(Class clazz, Encoder encoder) {
        CodegenImplNative.NATIVE_ENCODERS.put(clazz, encoder);
    }
    
    public  String toString() {
        return new String(buf, 0, count);
    }
    
    public OutputStream getOutputStream() {
        return this.out;
    }
    
    public  byte toByteArray()[] {
        return Arrays.copyOf(buf, count);
    }
}
