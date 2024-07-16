package io.github.yaojiqunaer.resttemplate.util;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class SSLSocketUtils {


    public static SSLContext sslContext() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, null, null);
        return sslContext;
    }

    public static SSLSocketFactory sslSocketFactory() throws Exception {
        return sslContext().getSocketFactory();
    }


}
