package nicholas23;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.google.common.base.CharMatcher;
import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.QDecoderStream;

public class MyMimeUtilityDecode {
	public static String decode(String value) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		StringBuilder sb = new StringBuilder();
		String charset = "";
		int s = -1;
		int e = 0;
		while (true) {
			s = value.indexOf("=?", e);
			if (s > e) {
				String unEncodingString;
				if (e == 0) {
					unEncodingString = value.substring(e, s);
				} else {
					unEncodingString = value.substring(e + 2, s);
				}

				if (!"".equals(unEncodingString.trim())) {
					if (baos.size() > 0) {
						sb.append(new String(baos.toByteArray(), charset));
						baos = new ByteArrayOutputStream();
					}
					sb.append(unEncodingString);
				}
			}
			if (s > -1) {
				int t = s;
				t = value.indexOf("?", s);
				t = value.indexOf("?", t + 1);
				t = value.indexOf("?", t + 1);
				e = value.indexOf("?=", t + 1);
				if (e > -1 && s < e) {
					String inner = value.substring(s, e + 2);
					String[] data = inner.split("\\?");
					if (data.length == 5) {
						if ("".endsWith(charset)) {
							charset = data[1];
							if("big5".equals(charset)){
								charset = "MS950";
							}
							if("gb2312".equals(charset)){
								charset = "gbk";
							}

						}
						data[3] = CharMatcher.JAVA_ISO_CONTROL.removeFrom(data[3]);
						InputStream is = null;
						ByteArrayInputStream  bis = null; 
						if ("q".equalsIgnoreCase(data[2])) {
							data[3] = data[3].replace(" ", "_");
							bis = new ByteArrayInputStream(ASCIIUtility.getBytes(data[3]));
							is = new QDecoderStream(bis);
						}
						if ("b".equalsIgnoreCase(data[2])) {
							data[3] = data[3].replace(" ", "");
							bis = new ByteArrayInputStream(ASCIIUtility.getBytes(data[3]));
							is = new BASE64DecoderStream(bis);
						}
						if (is == null || bis == null) {
							throw new UnsupportedEncodingException("unknown encoding: " + data[2]);
						}
						int count = bis.available();
						byte[] bytes = new byte[count];
						count = is.read(bytes, 0, count);
						baos.write(bytes, 0, count);
					}
				} else {
					if (baos.size() > 0) {
						sb.append(new String(baos.toByteArray(), charset));
					}
					String temp;
					if (e == 0) {
						temp = value.substring(e);
					} else {
						temp = value.substring(e + 2);
					}
					if (!"".endsWith(temp)) {
						sb.append(temp);
					}
					break;
				}
			} else {
				if (baos.size() > 0) {
					sb.append(new String(baos.toByteArray(), charset));
				}
				String temp;
				if (e == 0) {
					temp = value.substring(e);
				} else {
					temp = value.substring(e + 2);
				}
				if (!"".endsWith(temp)) {
					sb.append(temp);
				}
				break;
			}
		}
		baos.close();
		return CharMatcher.JAVA_ISO_CONTROL.removeFrom(sb.toString());
	}
}
