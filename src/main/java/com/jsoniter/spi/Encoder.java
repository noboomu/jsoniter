package com.jsoniter.spi;

import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;

import java.io.IOException;

public interface Encoder<T> {

    void encode(T obj, JsonStream stream) throws IOException;
 

    Any wrap(Object obj);

    public abstract class BooleanEncoder implements Encoder<Boolean> {
        @Override
        public void encode(Boolean obj, JsonStream stream) throws IOException {
            encodeBoolean((Boolean) obj, stream);
        }

        public abstract void encodeBoolean(boolean obj, JsonStream stream) throws IOException;
    }

    public abstract class ShortEncoder implements Encoder<Short> {

        @Override
        public void encode(Short obj, JsonStream stream) throws IOException {
            encodeShort((Short) obj, stream);
        }

        @Override
        public Any wrap(Object obj) {
            Short val = (Short) obj;
            return Any.wrap((int) val);
        }

        public abstract void encodeShort(short obj, JsonStream stream) throws IOException;
    }

    public class StringShortEncoder extends ShortEncoder {

        @Override
        public void encodeShort(short obj, JsonStream stream) throws IOException {
            stream.write('"');
            stream.writeVal(obj);
            stream.write('"');
        }
    }

    public abstract class IntEncoder implements Encoder<Integer> {
        @Override
        public void encode(Integer obj, JsonStream stream) throws IOException {
            encodeInt((Integer) obj, stream);
        }

        @Override
        public Any wrap(Object obj) {
            Integer val = (Integer) obj;
            return Any.wrap((int)val);
        }

        public abstract void encodeInt(int obj, JsonStream stream) throws IOException;
    }

    public class StringIntEncoder extends IntEncoder {

        @Override
        public void encodeInt(int obj, JsonStream stream) throws IOException {
            stream.write('"');
            stream.writeVal(obj);
            stream.write('"');
        }
    }

    public abstract class LongEncoder implements Encoder<Long> {
        @Override
        public void encode(Long obj, JsonStream stream) throws IOException {
            encodeLong((Long) obj, stream);
        }

        @Override
        public Any wrap(Object obj) {
            Long val = (Long) obj;
            return Any.wrap((long)val);
        }

        public abstract void encodeLong(long obj, JsonStream stream) throws IOException;
    }

    public class StringLongEncoder extends LongEncoder {

        @Override
        public void encodeLong(long obj, JsonStream stream) throws IOException {
            stream.write('"');
            stream.writeVal(obj);
            stream.write('"');
        }
    }

    public abstract class FloatEncoder implements Encoder<Float> {
        @Override
        public void encode(Float obj, JsonStream stream) throws IOException {
            encodeFloat(  obj, stream);
        }

        @Override
        public Any wrap(Object obj) {
            Float val = (Float) obj;
            return Any.wrap((float)val);
        }

        public abstract void encodeFloat(float obj, JsonStream stream) throws IOException;
    }

    public class StringFloatEncoder extends FloatEncoder {

        @Override
        public void encodeFloat(float obj, JsonStream stream) throws IOException {
            stream.write('"');
            stream.writeVal(obj);
            stream.write('"');
        }
    }

    public abstract class DoubleEncoder implements Encoder<Double> {
        @Override
        public void encode(Double obj, JsonStream stream) throws IOException {
            encodeDouble(  obj, stream);
        }

        @Override
        public Any wrap(Object obj) {
            Double val = (Double) obj;
            return Any.wrap((double)val);
        }

        public abstract void encodeDouble(double obj, JsonStream stream) throws IOException;
    }

    public class StringDoubleEncoder extends DoubleEncoder {

        @Override
        public void encodeDouble(double obj, JsonStream stream) throws IOException {
            stream.write('"');
            stream.writeVal(obj);
            stream.write('"');
        }
    }
}
