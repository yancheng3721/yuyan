package com.yuyan.emall.admin.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerHttpClient {

    private static final Logger log = LoggerFactory.getLogger(CustomerHttpClient.class);

    private static final String CHARSET = HTTP.UTF_8;

    private static HttpClient httpClient = null;

    static {
        httpClient = createHttpClient(1000, 1000, 3000);
    }

    public static HttpClient createHttpClient(long poolTimeout, int connTimeout, int soTimeout) {
        HttpParams params = new BasicHttpParams();
        // 设置一些基本参数
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);
        HttpProtocolParams.setUserAgent(params, "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
                + "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
        // 超时设置
        /* 从连接池中取连接的超时时间 */
        ConnManagerParams.setTimeout(params, poolTimeout);
        /* 连接超时 */
        HttpConnectionParams.setConnectionTimeout(params, connTimeout);
        /* 请求超时 */
        HttpConnectionParams.setSoTimeout(params, soTimeout);

        // MAX_ROUTE_CONNECTIONS为要设置的每个路由最大连接数
        ConnPerRouteBean connPerRoute = new ConnPerRouteBean(32);
        ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);

        ConnManagerParams.setMaxTotalConnections(params, 128);

        // 设置我们的HttpClient支持HTTP和HTTPS两种模式
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        // 使用线程安全的连接管理来创建HttpClient
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
        return new DefaultHttpClient(conMgr, params);
    }

    private CustomerHttpClient() {

    }

    /**
     * 发送form类型的post请求
     * 
     * @param url
     * @param map
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String post(String url, Map map) {
        try {
            // 编码参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                NameValuePair nvp = new BasicNameValuePair(key, value);
                nvps.add(nvp);
            }

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, CHARSET);
            // 创建POST请求
            HttpPost request = new HttpPost(url);
            request.setEntity(entity);
            // 发送请求;
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("请求失败");
            }
            HttpEntity resEntity = response.getEntity();

            String result = null;
            if (resEntity != null) {
                EntityUtils.toString(resEntity, CHARSET);
                resEntity.consumeContent();
            }
            return result;
        } catch (UnsupportedEncodingException e) {
            log.error("http encoding error!", e);
            return null;
        } catch (ClientProtocolException e) {
            log.error("http client protocol error!", e);
            return null;
        } catch (IOException e) {
            throw new RuntimeException("连接失败", e);
        }
    }

    /**
     * 发送字符串做为参数的post请求
     * 
     * @param url
     * @param str
     * @return
     */
    public static String post(String url, String str) {
        try {
            HttpPost httppost = new HttpPost(url);
            StringEntity reqEntity = new StringEntity(str, CHARSET);
            httppost.setEntity(reqEntity);
            HttpResponse response = httpClient.execute(httppost);
            HttpEntity httpEntity = response.getEntity();
            String result = null;
            if (httpEntity != null) {
                result = EntityUtils.toString(httpEntity);
                httpEntity.consumeContent();
            }
            return result;
        } catch (UnsupportedEncodingException e) {
            log.error("http encoding error!", e.getMessage());
            return null;
        } catch (ClientProtocolException e) {
            log.error("http client protocol error!", e.getMessage());
            return null;
        } catch (IOException e) {
            log.error("连接失败!", e.getMessage());
            return null;
        }

    }

    public static String get(String url) {
        HttpResponse response = null;
        try {
            // HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(url);
            response = httpClient.execute(httpget);

            if (response.getStatusLine().getStatusCode() != 200) {
                log.warn(url + " 访问状态！status " + response.getStatusLine().getStatusCode());
                return null;
            }
            HttpEntity httpEntity = response.getEntity();
            String result = null;
            if (httpEntity != null) {
                result = EntityUtils.toString(httpEntity);
            }
            return result;
        } catch (UnsupportedEncodingException e) {
            log.error("http encoding error!", e.getMessage());
            return null;
        } catch (ClientProtocolException e) {
            log.error("http client protocol error!", e.getMessage());
            return null;
        } catch (IOException e) {
            log.error(url + " 连接失败", e.getMessage());
            return null;
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    log.error("http error!", e);
                } // 会自动释放连接
            }
        }

    }

    public static boolean downLoad(String url, String dirPath, String fileName) {
        boolean downSuccess = false;

        if (!dirPath.endsWith("/")) {
            dirPath += "/";
        }

        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        HttpResponse httpResponse = null;
        InputStream in = null;
        OutputStream out = null;
        try {
            File file = new File(dirPath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            HttpClient httpClient = createHttpClient(1000, 1000, 300000);
            log.info("Http get log file: {}", url);
            HttpGet httpGet = new HttpGet(url);
            httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            in = entity.getContent();
            out = new FileOutputStream(file);
            saveTo(in, out);
            downSuccess = true;
        } catch (IOException e) {
            log.error(url + " 连接失败", e);
        } catch (Exception e) {
            log.error(url + " 连接失败", e);
        } finally {
            if (httpResponse != null) {
                try {
                    EntityUtils.consume(httpResponse.getEntity());
                } catch (IOException e) {
                    log.error("http error!", e);
                } // 会自动释放连接
            }
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }

        return downSuccess;
    }

    public static void saveTo(InputStream in, OutputStream out) throws IOException {
        byte[] data = new byte[1024 * 1024];
        int index = 0;
        while ((index = in.read(data)) != -1) {
            out.write(data, 0, index);
        }
        in.close();
        out.close();
    }

    public static void main(String[] args) {
    }
}
