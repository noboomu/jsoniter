package encoder;
public class int_array extends com.jsoniter.spi.EmptyEncoder {
public void encode(Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
stream.write(com.jsoniter.output.JsonStream.ARRAY_START);
encode_((int[])obj, stream);
stream.write(com.jsoniter.output.JsonStream.ARRAY_END);
}
public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
int[] arr = (int[])obj;
if (arr.length == 0) { return; }
int i = 0;
int e = arr[i++];
stream.writeVal((int)e);
while (i < arr.length) {
stream.write(com.jsoniter.output.JsonStream.COMMA);
e = arr[i++];
stream.writeVal((int)e);
}
}
}
