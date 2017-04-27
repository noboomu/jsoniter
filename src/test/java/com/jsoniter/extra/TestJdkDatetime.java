package com.jsoniter.extra;

import java.util.Date;
import java.util.TimeZone;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;

import junit.framework.TestCase;


public class TestJdkDatetime extends TestCase {

    public void test() {
    	
    	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        JdkDatetimeSupport.enable("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        assertEquals("\"1970-01-01T00:00:00.000+0000\"", JsonStream.serialize(new Date(0)));
        Date obj = JsonIterator.deserialize("\"1970-01-01T00:00:00.000+0000\"", Date.class);
        assertEquals(0, obj.getTime());
    }
}
