/*
this implementations contains significant code from https://github.com/ngs-doo/dsl-json/blob/master/LICENSE

Copyright (c) 2015, Nova Generacija Softvera d.o.o.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.

    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.

    * Neither the name of Nova Generacija Softvera d.o.o. nor the names of its
      contributors may be used to endorse or promote products derived from this
      software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jsoniter.output;

import java.io.IOException;

public class StreamImplString {

    private static final byte[] ITOA = new byte[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};
    private static final boolean[] CAN_DIRECT_WRITE = new boolean[128];

    static {
        for (int i = 0; i < CAN_DIRECT_WRITE.length; i++) {
            if (i > 31 && i < 126 && i != '"' && i != '\\') {
                CAN_DIRECT_WRITE[i] = true;
            }
        }
    }

    public static final void writeString(final JsonStream stream, final String val) throws IOException {
        int i = 0;
        int valLen = val.length();
        int toWriteLen = valLen;
        int bufLengthMinusTwo = stream.buf.length - 2; // make room for the quotes
        if (stream.count + toWriteLen > bufLengthMinusTwo) {
            toWriteLen = bufLengthMinusTwo - stream.count;
        }
        if (toWriteLen < 0) {
            stream.flushBuffer();
            if (stream.count + toWriteLen > bufLengthMinusTwo) {
                toWriteLen = bufLengthMinusTwo - stream.count;
            }
        }
        int n = stream.count;
        stream.buf[n++] = JsonStream.QUOTE;
        // write string, the fast path, without utf8 and escape support
        for (; i < toWriteLen; i++) {
            char c = val.charAt(i);
            try {
                if (CAN_DIRECT_WRITE[c]) {
                    stream.buf[n++] = (byte) c;
                } else {
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
        }
        if (i == valLen) {
            stream.buf[n++] = JsonStream.QUOTE;
            stream.count = n;
            return;
        }
        stream.count = n;
        // for the remaining parts, we process them char by char
        writeStringSlowPath(stream, val, i, valLen);
        stream.write(JsonStream.QUOTE);
    }

    public static final void writeStringWithoutQuote(final JsonStream stream, final String val) throws IOException {
        int i = 0;
        final int valLen = val.length();
        int toWriteLen = valLen;
        final int bufLen = stream.buf.length;
        if (stream.count + toWriteLen > bufLen) {
            toWriteLen = bufLen - stream.count;
        }
        if (toWriteLen < 0) {
            stream.flushBuffer();
            if (stream.count + toWriteLen > bufLen) {
                toWriteLen = bufLen - stream.count;
            }
        }
        int n = stream.count;
        // write string, the fast path, without utf8 and escape support
        for (; i < toWriteLen; i++) {
            char c = val.charAt(i);
            if (c > 31 && c != JsonStream.QUOTE && c != JsonStream.ESCAPE && c < 126) {
                stream.buf[n++] = (byte) c;
            } else {
                break;
            }
        }
        if (i == valLen) {
            stream.count = n;
            return;
        }
        stream.count = n;
        // for the remaining parts, we process them char by char
        writeStringSlowPath(stream, val, i, valLen);
    }

    private static void writeStringSlowPath(final JsonStream stream, final String val,  int i, final int valLen) throws IOException {
        for (; i < valLen; i++) {
        	final int c = val.charAt(i);
            if (c > 125) {
                byte b4 = (byte) (c & 0xf);
                byte b3 = (byte) (c >> 4 & 0xf);
                byte b2 = (byte) (c >> 8 & 0xf);
                byte b1 = (byte) (c >> 12 & 0xf);
                stream.write((byte)  JsonStream.ESCAPE, JsonStream.UNICODE, ITOA[b1], ITOA[b2], ITOA[b3], ITOA[b4]);
            } else {
                switch (c) {
                    case JsonStream.QUOTE:
                        stream.write( JsonStream.ESCAPE, JsonStream.QUOTE);
                        break;
                    case JsonStream.ESCAPE:
                        stream.write(JsonStream.ESCAPE, JsonStream.ESCAPE);
                        break;
                    case JsonStream.BACKSPACE:
                        stream.write(JsonStream.ESCAPE_BACKSPACE);
                        break;
                    case JsonStream.FORMFEED:
                        stream.write(JsonStream.ESCAPE_FORMFEED);
                        break;
                    case JsonStream.NEWLINE:
                        stream.write(JsonStream.ESCAPE_NEWLINE);
                        break;
                    case JsonStream.CARRAIGE_RETURN:
                        stream.write(JsonStream.ESCAPE_CARRIAGE_RETURN);
                        break;
                    case JsonStream.TAB:
                        stream.write(JsonStream.ESCAPE_TAB);
                        break;
                    default:
                        stream.write(c);
                }
            }
        }
    }
}
