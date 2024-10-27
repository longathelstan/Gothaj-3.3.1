package org.jsoup.helper;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpURLConnection;
import java.net.IDN;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.Proxy.Type;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.UncheckedIOException;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.internal.ConstrainableInputStream;
import org.jsoup.internal.Normalizer;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.parser.TokenQueue;

public class HttpConnection implements Connection {
   public static final String CONTENT_ENCODING = "Content-Encoding";
   public static final String DEFAULT_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36";
   private static final String USER_AGENT = "User-Agent";
   public static final String CONTENT_TYPE = "Content-Type";
   public static final String MULTIPART_FORM_DATA = "multipart/form-data";
   public static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";
   private static final int HTTP_TEMP_REDIR = 307;
   private static final String DefaultUploadType = "application/octet-stream";
   private static final Charset UTF_8 = Charset.forName("UTF-8");
   private static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
   private HttpConnection.Request req;
   @Nullable
   private Connection.Response res;

   public static Connection connect(String url) {
      Connection con = new HttpConnection();
      con.url(url);
      return con;
   }

   public static Connection connect(URL url) {
      Connection con = new HttpConnection();
      con.url(url);
      return con;
   }

   public HttpConnection() {
      this.req = new HttpConnection.Request();
   }

   HttpConnection(HttpConnection.Request copy) {
      this.req = new HttpConnection.Request(copy);
   }

   private static String encodeUrl(String url) {
      try {
         URL u = new URL(url);
         return encodeUrl(u).toExternalForm();
      } catch (Exception var2) {
         return url;
      }
   }

   static URL encodeUrl(URL u) {
      u = punyUrl(u);

      try {
         String urlS = u.toExternalForm();
         urlS = urlS.replace(" ", "%20");
         URI uri = new URI(urlS);
         return new URL(uri.toASCIIString());
      } catch (MalformedURLException | URISyntaxException var3) {
         return u;
      }
   }

   private static URL punyUrl(URL url) {
      if (!StringUtil.isAscii(url.getHost())) {
         try {
            String puny = IDN.toASCII(url.getHost());
            url = new URL(url.getProtocol(), puny, url.getPort(), url.getFile());
         } catch (MalformedURLException var2) {
            throw new IllegalArgumentException(var2);
         }
      }

      return url;
   }

   private static String encodeMimeName(String val) {
      return val.replace("\"", "%22");
   }

   public Connection newRequest() {
      return new HttpConnection(this.req);
   }

   private HttpConnection(HttpConnection.Request req, HttpConnection.Response res) {
      this.req = req;
      this.res = res;
   }

   public Connection url(URL url) {
      this.req.url(url);
      return this;
   }

   public Connection url(String url) {
      Validate.notEmptyParam(url, "url");

      try {
         this.req.url(new URL(encodeUrl(url)));
         return this;
      } catch (MalformedURLException var3) {
         throw new IllegalArgumentException(String.format("The supplied URL, '%s', is malformed. Make sure it is an absolute URL, and starts with 'http://' or 'https://'. See https://jsoup.org/cookbook/extracting-data/working-with-urls", url), var3);
      }
   }

   public Connection proxy(@Nullable Proxy proxy) {
      this.req.proxy(proxy);
      return this;
   }

   public Connection proxy(String host, int port) {
      this.req.proxy(host, port);
      return this;
   }

   public Connection userAgent(String userAgent) {
      Validate.notNullParam(userAgent, "userAgent");
      this.req.header("User-Agent", userAgent);
      return this;
   }

   public Connection timeout(int millis) {
      this.req.timeout(millis);
      return this;
   }

   public Connection maxBodySize(int bytes) {
      this.req.maxBodySize(bytes);
      return this;
   }

   public Connection followRedirects(boolean followRedirects) {
      this.req.followRedirects(followRedirects);
      return this;
   }

   public Connection referrer(String referrer) {
      Validate.notNullParam(referrer, "referrer");
      this.req.header("Referer", referrer);
      return this;
   }

   public Connection method(Connection.Method method) {
      this.req.method(method);
      return this;
   }

   public Connection ignoreHttpErrors(boolean ignoreHttpErrors) {
      this.req.ignoreHttpErrors(ignoreHttpErrors);
      return this;
   }

   public Connection ignoreContentType(boolean ignoreContentType) {
      this.req.ignoreContentType(ignoreContentType);
      return this;
   }

   public Connection data(String key, String value) {
      this.req.data(HttpConnection.KeyVal.create(key, value));
      return this;
   }

   public Connection sslSocketFactory(SSLSocketFactory sslSocketFactory) {
      this.req.sslSocketFactory(sslSocketFactory);
      return this;
   }

   public Connection data(String key, String filename, InputStream inputStream) {
      this.req.data(HttpConnection.KeyVal.create(key, filename, inputStream));
      return this;
   }

   public Connection data(String key, String filename, InputStream inputStream, String contentType) {
      this.req.data(HttpConnection.KeyVal.create(key, filename, inputStream).contentType(contentType));
      return this;
   }

   public Connection data(Map<String, String> data) {
      Validate.notNullParam(data, "data");
      Iterator var2 = data.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<String, String> entry = (Entry)var2.next();
         this.req.data(HttpConnection.KeyVal.create((String)entry.getKey(), (String)entry.getValue()));
      }

      return this;
   }

   public Connection data(String... keyvals) {
      Validate.notNullParam(keyvals, "keyvals");
      Validate.isTrue(keyvals.length % 2 == 0, "Must supply an even number of key value pairs");

      for(int i = 0; i < keyvals.length; i += 2) {
         String key = keyvals[i];
         String value = keyvals[i + 1];
         Validate.notEmpty(key, "Data key must not be empty");
         Validate.notNull(value, "Data value must not be null");
         this.req.data(HttpConnection.KeyVal.create(key, value));
      }

      return this;
   }

   public Connection data(Collection<Connection.KeyVal> data) {
      Validate.notNullParam(data, "data");
      Iterator var2 = data.iterator();

      while(var2.hasNext()) {
         Connection.KeyVal entry = (Connection.KeyVal)var2.next();
         this.req.data(entry);
      }

      return this;
   }

   public Connection.KeyVal data(String key) {
      Validate.notEmptyParam(key, "key");
      Iterator var2 = this.request().data().iterator();

      Connection.KeyVal keyVal;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         keyVal = (Connection.KeyVal)var2.next();
      } while(!keyVal.key().equals(key));

      return keyVal;
   }

   public Connection requestBody(String body) {
      this.req.requestBody(body);
      return this;
   }

   public Connection header(String name, String value) {
      this.req.header(name, value);
      return this;
   }

   public Connection headers(Map<String, String> headers) {
      Validate.notNullParam(headers, "headers");
      Iterator var2 = headers.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<String, String> entry = (Entry)var2.next();
         this.req.header((String)entry.getKey(), (String)entry.getValue());
      }

      return this;
   }

   public Connection cookie(String name, String value) {
      this.req.cookie(name, value);
      return this;
   }

   public Connection cookies(Map<String, String> cookies) {
      Validate.notNullParam(cookies, "cookies");
      Iterator var2 = cookies.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<String, String> entry = (Entry)var2.next();
         this.req.cookie((String)entry.getKey(), (String)entry.getValue());
      }

      return this;
   }

   public Connection cookieStore(CookieStore cookieStore) {
      this.req.cookieManager = new CookieManager(cookieStore, (CookiePolicy)null);
      return this;
   }

   public CookieStore cookieStore() {
      return this.req.cookieManager.getCookieStore();
   }

   public Connection parser(Parser parser) {
      this.req.parser(parser);
      return this;
   }

   public Document get() throws IOException {
      this.req.method(Connection.Method.GET);
      this.execute();
      Validate.notNull(this.res);
      return this.res.parse();
   }

   public Document post() throws IOException {
      this.req.method(Connection.Method.POST);
      this.execute();
      Validate.notNull(this.res);
      return this.res.parse();
   }

   public Connection.Response execute() throws IOException {
      this.res = HttpConnection.Response.execute(this.req);
      return this.res;
   }

   public Connection.Request request() {
      return this.req;
   }

   public Connection request(Connection.Request request) {
      this.req = (HttpConnection.Request)request;
      return this;
   }

   public Connection.Response response() {
      if (this.res == null) {
         throw new IllegalArgumentException("You must execute the request before getting a response.");
      } else {
         return this.res;
      }
   }

   public Connection response(Connection.Response response) {
      this.res = response;
      return this;
   }

   public Connection postDataCharset(String charset) {
      this.req.postDataCharset(charset);
      return this;
   }

   private static boolean needsMultipart(Connection.Request req) {
      Iterator var1 = req.data().iterator();

      Connection.KeyVal keyVal;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         keyVal = (Connection.KeyVal)var1.next();
      } while(!keyVal.hasInputStream());

      return true;
   }

   // $FF: synthetic method
   HttpConnection(HttpConnection.Request x0, HttpConnection.Response x1, Object x2) {
      this(x0, x1);
   }

   public static class Request extends HttpConnection.Base<Connection.Request> implements Connection.Request {
      @Nullable
      private Proxy proxy;
      private int timeoutMilliseconds;
      private int maxBodySizeBytes;
      private boolean followRedirects;
      private final Collection<Connection.KeyVal> data;
      @Nullable
      private String body = null;
      private boolean ignoreHttpErrors = false;
      private boolean ignoreContentType = false;
      private Parser parser;
      private boolean parserDefined = false;
      private String postDataCharset;
      @Nullable
      private SSLSocketFactory sslSocketFactory;
      private CookieManager cookieManager;
      private volatile boolean executing;

      Request() {
         super((<undefinedtype>)null);
         this.postDataCharset = DataUtil.defaultCharsetName;
         this.executing = false;
         this.timeoutMilliseconds = 30000;
         this.maxBodySizeBytes = 2097152;
         this.followRedirects = true;
         this.data = new ArrayList();
         this.method = Connection.Method.GET;
         this.addHeader("Accept-Encoding", "gzip");
         this.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36");
         this.parser = Parser.htmlParser();
         this.cookieManager = new CookieManager();
      }

      Request(HttpConnection.Request copy) {
         super(copy, null);
         this.postDataCharset = DataUtil.defaultCharsetName;
         this.executing = false;
         this.proxy = copy.proxy;
         this.postDataCharset = copy.postDataCharset;
         this.timeoutMilliseconds = copy.timeoutMilliseconds;
         this.maxBodySizeBytes = copy.maxBodySizeBytes;
         this.followRedirects = copy.followRedirects;
         this.data = new ArrayList();
         this.data.addAll(copy.data());
         this.body = copy.body;
         this.ignoreHttpErrors = copy.ignoreHttpErrors;
         this.ignoreContentType = copy.ignoreContentType;
         this.parser = copy.parser.newInstance();
         this.parserDefined = copy.parserDefined;
         this.sslSocketFactory = copy.sslSocketFactory;
         this.cookieManager = copy.cookieManager;
         this.executing = false;
      }

      public Proxy proxy() {
         return this.proxy;
      }

      public HttpConnection.Request proxy(@Nullable Proxy proxy) {
         this.proxy = proxy;
         return this;
      }

      public HttpConnection.Request proxy(String host, int port) {
         this.proxy = new Proxy(Type.HTTP, InetSocketAddress.createUnresolved(host, port));
         return this;
      }

      public int timeout() {
         return this.timeoutMilliseconds;
      }

      public HttpConnection.Request timeout(int millis) {
         Validate.isTrue(millis >= 0, "Timeout milliseconds must be 0 (infinite) or greater");
         this.timeoutMilliseconds = millis;
         return this;
      }

      public int maxBodySize() {
         return this.maxBodySizeBytes;
      }

      public Connection.Request maxBodySize(int bytes) {
         Validate.isTrue(bytes >= 0, "maxSize must be 0 (unlimited) or larger");
         this.maxBodySizeBytes = bytes;
         return this;
      }

      public boolean followRedirects() {
         return this.followRedirects;
      }

      public Connection.Request followRedirects(boolean followRedirects) {
         this.followRedirects = followRedirects;
         return this;
      }

      public boolean ignoreHttpErrors() {
         return this.ignoreHttpErrors;
      }

      public SSLSocketFactory sslSocketFactory() {
         return this.sslSocketFactory;
      }

      public void sslSocketFactory(SSLSocketFactory sslSocketFactory) {
         this.sslSocketFactory = sslSocketFactory;
      }

      public Connection.Request ignoreHttpErrors(boolean ignoreHttpErrors) {
         this.ignoreHttpErrors = ignoreHttpErrors;
         return this;
      }

      public boolean ignoreContentType() {
         return this.ignoreContentType;
      }

      public Connection.Request ignoreContentType(boolean ignoreContentType) {
         this.ignoreContentType = ignoreContentType;
         return this;
      }

      public HttpConnection.Request data(Connection.KeyVal keyval) {
         Validate.notNullParam(keyval, "keyval");
         this.data.add(keyval);
         return this;
      }

      public Collection<Connection.KeyVal> data() {
         return this.data;
      }

      public Connection.Request requestBody(@Nullable String body) {
         this.body = body;
         return this;
      }

      public String requestBody() {
         return this.body;
      }

      public HttpConnection.Request parser(Parser parser) {
         this.parser = parser;
         this.parserDefined = true;
         return this;
      }

      public Parser parser() {
         return this.parser;
      }

      public Connection.Request postDataCharset(String charset) {
         Validate.notNullParam(charset, "charset");
         if (!Charset.isSupported(charset)) {
            throw new IllegalCharsetNameException(charset);
         } else {
            this.postDataCharset = charset;
            return this;
         }
      }

      public String postDataCharset() {
         return this.postDataCharset;
      }

      CookieManager cookieManager() {
         return this.cookieManager;
      }

      static {
         System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
      }
   }

   public static class Response extends HttpConnection.Base<Connection.Response> implements Connection.Response {
      private static final int MAX_REDIRECTS = 20;
      private static final String LOCATION = "Location";
      private final int statusCode;
      private final String statusMessage;
      @Nullable
      private ByteBuffer byteData;
      @Nullable
      private InputStream bodyStream;
      @Nullable
      private HttpURLConnection conn;
      @Nullable
      private String charset;
      @Nullable
      private final String contentType;
      private boolean executed = false;
      private boolean inputStreamRead = false;
      private int numRedirects = 0;
      private final HttpConnection.Request req;
      private static final Pattern xmlContentTypeRxp = Pattern.compile("(application|text)/\\w*\\+?xml.*");

      Response() {
         super((<undefinedtype>)null);
         this.statusCode = 400;
         this.statusMessage = "Request not made";
         this.req = new HttpConnection.Request();
         this.contentType = null;
      }

      static HttpConnection.Response execute(HttpConnection.Request req) throws IOException {
         return execute(req, (HttpConnection.Response)null);
      }

      static HttpConnection.Response execute(HttpConnection.Request req, @Nullable HttpConnection.Response previousResponse) throws IOException {
         synchronized(req) {
            Validate.isFalse(req.executing, "Multiple threads were detected trying to execute the same request concurrently. Make sure to use Connection#newRequest() and do not share an executing request between threads.");
            req.executing = true;
         }

         Validate.notNullParam(req, "req");
         URL url = req.url();
         Validate.notNull(url, "URL must be specified to connect");
         String protocol = url.getProtocol();
         if (!protocol.equals("http") && !protocol.equals("https")) {
            throw new MalformedURLException("Only http & https protocols supported");
         } else {
            boolean methodHasBody = req.method().hasBody();
            boolean hasRequestBody = req.requestBody() != null;
            if (!methodHasBody) {
               Validate.isFalse(hasRequestBody, "Cannot set a request body for HTTP method " + req.method());
            }

            String mimeBoundary = null;
            if (req.data().size() > 0 && (!methodHasBody || hasRequestBody)) {
               serialiseRequestUrl(req);
            } else if (methodHasBody) {
               mimeBoundary = setOutputContentType(req);
            }

            long startTime = System.nanoTime();
            HttpURLConnection conn = createConnection(req);
            HttpConnection.Response res = null;

            label422: {
               HttpConnection.Response var14;
               try {
                  conn.connect();
                  if (conn.getDoOutput()) {
                     OutputStream out = conn.getOutputStream();

                     try {
                        writePost(req, out, mimeBoundary);
                     } catch (IOException var26) {
                        conn.disconnect();
                        throw var26;
                     } finally {
                        out.close();
                     }
                  }

                  int status = conn.getResponseCode();
                  res = new HttpConnection.Response(conn, req, previousResponse);
                  String contentType;
                  if (!res.hasHeader("Location") || !req.followRedirects()) {
                     if ((status < 200 || status >= 400) && !req.ignoreHttpErrors()) {
                        throw new HttpStatusException("HTTP error fetching URL", status, req.url().toString());
                     }

                     contentType = res.contentType();
                     if (contentType != null && !req.ignoreContentType() && !contentType.startsWith("text/") && !xmlContentTypeRxp.matcher(contentType).matches()) {
                        throw new UnsupportedMimeTypeException("Unhandled content type. Must be text/*, application/xml, or application/*+xml", contentType, req.url().toString());
                     }

                     if (contentType != null && xmlContentTypeRxp.matcher(contentType).matches() && !req.parserDefined) {
                        req.parser(Parser.xmlParser());
                     }

                     res.charset = DataUtil.getCharsetFromContentType(res.contentType);
                     if (conn.getContentLength() != 0 && req.method() != Connection.Method.HEAD) {
                        res.bodyStream = conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream();
                        Validate.notNull(res.bodyStream);
                        if (res.hasHeaderWithValue("Content-Encoding", "gzip")) {
                           res.bodyStream = new GZIPInputStream(res.bodyStream);
                        } else if (res.hasHeaderWithValue("Content-Encoding", "deflate")) {
                           res.bodyStream = new InflaterInputStream(res.bodyStream, new Inflater(true));
                        }

                        res.bodyStream = ConstrainableInputStream.wrap(res.bodyStream, 32768, req.maxBodySize()).timeout(startTime, (long)req.timeout());
                        break label422;
                     }

                     res.byteData = DataUtil.emptyByteBuffer();
                     break label422;
                  }

                  if (status != 307) {
                     req.method(Connection.Method.GET);
                     req.data().clear();
                     req.requestBody((String)null);
                     req.removeHeader("Content-Type");
                  }

                  contentType = res.header("Location");
                  Validate.notNull(contentType);
                  if (contentType.startsWith("http:/") && contentType.charAt(6) != '/') {
                     contentType = contentType.substring(6);
                  }

                  URL redir = StringUtil.resolve(req.url(), contentType);
                  req.url(HttpConnection.encodeUrl(redir));
                  req.executing = false;
                  var14 = execute(req, res);
               } catch (IOException var29) {
                  if (res != null) {
                     res.safeClose();
                  }

                  throw var29;
               } finally {
                  req.executing = false;
               }

               return var14;
            }

            res.executed = true;
            return res;
         }
      }

      public int statusCode() {
         return this.statusCode;
      }

      public String statusMessage() {
         return this.statusMessage;
      }

      public String charset() {
         return this.charset;
      }

      public HttpConnection.Response charset(String charset) {
         this.charset = charset;
         return this;
      }

      public String contentType() {
         return this.contentType;
      }

      public Document parse() throws IOException {
         Validate.isTrue(this.executed, "Request must be executed (with .execute(), .get(), or .post() before parsing response");
         if (this.byteData != null) {
            this.bodyStream = new ByteArrayInputStream(this.byteData.array());
            this.inputStreamRead = false;
         }

         Validate.isFalse(this.inputStreamRead, "Input stream already read and parsed, cannot re-read.");
         Document doc = DataUtil.parseInputStream(this.bodyStream, this.charset, this.url.toExternalForm(), this.req.parser());
         doc.connection(new HttpConnection(this.req, this));
         this.charset = doc.outputSettings().charset().name();
         this.inputStreamRead = true;
         this.safeClose();
         return doc;
      }

      private void prepareByteData() {
         Validate.isTrue(this.executed, "Request must be executed (with .execute(), .get(), or .post() before getting response body");
         if (this.bodyStream != null && this.byteData == null) {
            Validate.isFalse(this.inputStreamRead, "Request has already been read (with .parse())");

            try {
               this.byteData = DataUtil.readToByteBuffer(this.bodyStream, this.req.maxBodySize());
            } catch (IOException var5) {
               throw new UncheckedIOException(var5);
            } finally {
               this.inputStreamRead = true;
               this.safeClose();
            }
         }

      }

      public String body() {
         this.prepareByteData();
         Validate.notNull(this.byteData);
         String body = (this.charset == null ? DataUtil.UTF_8 : Charset.forName(this.charset)).decode(this.byteData).toString();
         this.byteData.rewind();
         return body;
      }

      public byte[] bodyAsBytes() {
         this.prepareByteData();
         Validate.notNull(this.byteData);
         return this.byteData.array();
      }

      public Connection.Response bufferUp() {
         this.prepareByteData();
         return this;
      }

      public BufferedInputStream bodyStream() {
         Validate.isTrue(this.executed, "Request must be executed (with .execute(), .get(), or .post() before getting response body");
         Validate.isFalse(this.inputStreamRead, "Request has already been read");
         this.inputStreamRead = true;
         return ConstrainableInputStream.wrap(this.bodyStream, 32768, this.req.maxBodySize());
      }

      private static HttpURLConnection createConnection(HttpConnection.Request req) throws IOException {
         Proxy proxy = req.proxy();
         HttpURLConnection conn = (HttpURLConnection)(proxy == null ? req.url().openConnection() : req.url().openConnection(proxy));
         conn.setRequestMethod(req.method().name());
         conn.setInstanceFollowRedirects(false);
         conn.setConnectTimeout(req.timeout());
         conn.setReadTimeout(req.timeout() / 2);
         if (req.sslSocketFactory() != null && conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection)conn).setSSLSocketFactory(req.sslSocketFactory());
         }

         if (req.method().hasBody()) {
            conn.setDoOutput(true);
         }

         CookieUtil.applyCookiesToRequest(req, conn);
         Iterator var3 = req.multiHeaders().entrySet().iterator();

         while(var3.hasNext()) {
            Entry<String, List<String>> header = (Entry)var3.next();
            Iterator var5 = ((List)header.getValue()).iterator();

            while(var5.hasNext()) {
               String value = (String)var5.next();
               conn.addRequestProperty((String)header.getKey(), value);
            }
         }

         return conn;
      }

      private void safeClose() {
         if (this.bodyStream != null) {
            try {
               this.bodyStream.close();
            } catch (IOException var5) {
            } finally {
               this.bodyStream = null;
            }
         }

         if (this.conn != null) {
            this.conn.disconnect();
            this.conn = null;
         }

      }

      private Response(HttpURLConnection conn, HttpConnection.Request request, @Nullable HttpConnection.Response previousResponse) throws IOException {
         super((<undefinedtype>)null);
         this.conn = conn;
         this.req = request;
         this.method = Connection.Method.valueOf(conn.getRequestMethod());
         this.url = conn.getURL();
         this.statusCode = conn.getResponseCode();
         this.statusMessage = conn.getResponseMessage();
         this.contentType = conn.getContentType();
         Map<String, List<String>> resHeaders = createHeaderMap(conn);
         this.processResponseHeaders(resHeaders);
         CookieUtil.storeCookies(this.req, this.url, resHeaders);
         if (previousResponse != null) {
            Iterator var5 = previousResponse.cookies().entrySet().iterator();

            while(var5.hasNext()) {
               Entry<String, String> prevCookie = (Entry)var5.next();
               if (!this.hasCookie((String)prevCookie.getKey())) {
                  this.cookie((String)prevCookie.getKey(), (String)prevCookie.getValue());
               }
            }

            previousResponse.safeClose();
            this.numRedirects = previousResponse.numRedirects + 1;
            if (this.numRedirects >= 20) {
               throw new IOException(String.format("Too many redirects occurred trying to load URL %s", previousResponse.url()));
            }
         }

      }

      private static LinkedHashMap<String, List<String>> createHeaderMap(HttpURLConnection conn) {
         LinkedHashMap<String, List<String>> headers = new LinkedHashMap();
         int i = 0;

         while(true) {
            String key = conn.getHeaderFieldKey(i);
            String val = conn.getHeaderField(i);
            if (key == null && val == null) {
               return headers;
            }

            ++i;
            if (key != null && val != null) {
               if (headers.containsKey(key)) {
                  ((List)headers.get(key)).add(val);
               } else {
                  ArrayList<String> vals = new ArrayList();
                  vals.add(val);
                  headers.put(key, vals);
               }
            }
         }
      }

      void processResponseHeaders(Map<String, List<String>> resHeaders) {
         Iterator var2 = resHeaders.entrySet().iterator();

         while(true) {
            Entry entry;
            String name;
            do {
               if (!var2.hasNext()) {
                  return;
               }

               entry = (Entry)var2.next();
               name = (String)entry.getKey();
            } while(name == null);

            List<String> values = (List)entry.getValue();
            Iterator var6;
            String value;
            if (name.equalsIgnoreCase("Set-Cookie")) {
               var6 = values.iterator();

               while(var6.hasNext()) {
                  value = (String)var6.next();
                  if (value != null) {
                     TokenQueue cd = new TokenQueue(value);
                     String cookieName = cd.chompTo("=").trim();
                     String cookieVal = cd.consumeTo(";").trim();
                     if (cookieName.length() > 0 && !this.cookies.containsKey(cookieName)) {
                        this.cookie(cookieName, cookieVal);
                     }
                  }
               }
            }

            var6 = values.iterator();

            while(var6.hasNext()) {
               value = (String)var6.next();
               this.addHeader(name, value);
            }
         }
      }

      @Nullable
      private static String setOutputContentType(Connection.Request req) {
         String contentType = req.header("Content-Type");
         String bound = null;
         if (contentType != null) {
            if (contentType.contains("multipart/form-data") && !contentType.contains("boundary")) {
               bound = DataUtil.mimeBoundary();
               req.header("Content-Type", "multipart/form-data; boundary=" + bound);
            }
         } else if (HttpConnection.needsMultipart(req)) {
            bound = DataUtil.mimeBoundary();
            req.header("Content-Type", "multipart/form-data; boundary=" + bound);
         } else {
            req.header("Content-Type", "application/x-www-form-urlencoded; charset=" + req.postDataCharset());
         }

         return bound;
      }

      private static void writePost(Connection.Request req, OutputStream outputStream, @Nullable String boundary) throws IOException {
         Collection<Connection.KeyVal> data = req.data();
         BufferedWriter w = new BufferedWriter(new OutputStreamWriter(outputStream, Charset.forName(req.postDataCharset())));
         if (boundary != null) {
            for(Iterator var5 = data.iterator(); var5.hasNext(); w.write("\r\n")) {
               Connection.KeyVal keyVal = (Connection.KeyVal)var5.next();
               w.write("--");
               w.write(boundary);
               w.write("\r\n");
               w.write("Content-Disposition: form-data; name=\"");
               w.write(HttpConnection.encodeMimeName(keyVal.key()));
               w.write("\"");
               InputStream input = keyVal.inputStream();
               if (input != null) {
                  w.write("; filename=\"");
                  w.write(HttpConnection.encodeMimeName(keyVal.value()));
                  w.write("\"\r\nContent-Type: ");
                  String contentType = keyVal.contentType();
                  w.write(contentType != null ? contentType : "application/octet-stream");
                  w.write("\r\n\r\n");
                  w.flush();
                  DataUtil.crossStreams(input, outputStream);
                  outputStream.flush();
               } else {
                  w.write("\r\n\r\n");
                  w.write(keyVal.value());
               }
            }

            w.write("--");
            w.write(boundary);
            w.write("--");
         } else {
            String body = req.requestBody();
            if (body != null) {
               w.write(body);
            } else {
               boolean first = true;
               Iterator var11 = data.iterator();

               while(var11.hasNext()) {
                  Connection.KeyVal keyVal = (Connection.KeyVal)var11.next();
                  if (!first) {
                     w.append('&');
                  } else {
                     first = false;
                  }

                  w.write(URLEncoder.encode(keyVal.key(), req.postDataCharset()));
                  w.write(61);
                  w.write(URLEncoder.encode(keyVal.value(), req.postDataCharset()));
               }
            }
         }

         w.close();
      }

      private static void serialiseRequestUrl(Connection.Request req) throws IOException {
         URL in = req.url();
         StringBuilder url = StringUtil.borrowBuilder();
         boolean first = true;
         url.append(in.getProtocol()).append("://").append(in.getAuthority()).append(in.getPath()).append("?");
         if (in.getQuery() != null) {
            url.append(in.getQuery());
            first = false;
         }

         Connection.KeyVal keyVal;
         for(Iterator var4 = req.data().iterator(); var4.hasNext(); url.append(URLEncoder.encode(keyVal.key(), DataUtil.defaultCharsetName)).append('=').append(URLEncoder.encode(keyVal.value(), DataUtil.defaultCharsetName))) {
            keyVal = (Connection.KeyVal)var4.next();
            Validate.isFalse(keyVal.hasInputStream(), "InputStream data not supported in URL query string.");
            if (!first) {
               url.append('&');
            } else {
               first = false;
            }
         }

         req.url(new URL(StringUtil.releaseBuilder(url)));
         req.data().clear();
      }
   }

   public static class KeyVal implements Connection.KeyVal {
      private String key;
      private String value;
      @Nullable
      private InputStream stream;
      @Nullable
      private String contentType;

      public static HttpConnection.KeyVal create(String key, String value) {
         return new HttpConnection.KeyVal(key, value);
      }

      public static HttpConnection.KeyVal create(String key, String filename, InputStream stream) {
         return (new HttpConnection.KeyVal(key, filename)).inputStream(stream);
      }

      private KeyVal(String key, String value) {
         Validate.notEmptyParam(key, "key");
         Validate.notNullParam(value, "value");
         this.key = key;
         this.value = value;
      }

      public HttpConnection.KeyVal key(String key) {
         Validate.notEmptyParam(key, "key");
         this.key = key;
         return this;
      }

      public String key() {
         return this.key;
      }

      public HttpConnection.KeyVal value(String value) {
         Validate.notNullParam(value, "value");
         this.value = value;
         return this;
      }

      public String value() {
         return this.value;
      }

      public HttpConnection.KeyVal inputStream(InputStream inputStream) {
         Validate.notNullParam(this.value, "inputStream");
         this.stream = inputStream;
         return this;
      }

      public InputStream inputStream() {
         return this.stream;
      }

      public boolean hasInputStream() {
         return this.stream != null;
      }

      public Connection.KeyVal contentType(String contentType) {
         Validate.notEmpty(contentType);
         this.contentType = contentType;
         return this;
      }

      public String contentType() {
         return this.contentType;
      }

      public String toString() {
         return this.key + "=" + this.value;
      }
   }

   private abstract static class Base<T extends Connection.Base<T>> implements Connection.Base<T> {
      private static final URL UnsetUrl;
      URL url;
      Connection.Method method;
      Map<String, List<String>> headers;
      Map<String, String> cookies;

      private Base() {
         this.url = UnsetUrl;
         this.method = Connection.Method.GET;
         this.headers = new LinkedHashMap();
         this.cookies = new LinkedHashMap();
      }

      private Base(HttpConnection.Base<T> copy) {
         this.url = UnsetUrl;
         this.method = Connection.Method.GET;
         this.url = copy.url;
         this.method = copy.method;
         this.headers = new LinkedHashMap();
         Iterator var2 = copy.headers.entrySet().iterator();

         while(var2.hasNext()) {
            Entry<String, List<String>> entry = (Entry)var2.next();
            this.headers.put((String)entry.getKey(), new ArrayList((Collection)entry.getValue()));
         }

         this.cookies = new LinkedHashMap();
         this.cookies.putAll(copy.cookies);
      }

      public URL url() {
         if (this.url == UnsetUrl) {
            throw new IllegalArgumentException("URL not set. Make sure to call #url(...) before executing the request.");
         } else {
            return this.url;
         }
      }

      public T url(URL url) {
         Validate.notNullParam(url, "url");
         this.url = HttpConnection.punyUrl(url);
         return this;
      }

      public Connection.Method method() {
         return this.method;
      }

      public T method(Connection.Method method) {
         Validate.notNullParam(method, "method");
         this.method = method;
         return this;
      }

      public String header(String name) {
         Validate.notNullParam(name, "name");
         List<String> vals = this.getHeadersCaseInsensitive(name);
         return vals.size() > 0 ? StringUtil.join((Collection)vals, ", ") : null;
      }

      public T addHeader(String name, String value) {
         Validate.notEmptyParam(name, "name");
         value = value == null ? "" : value;
         List<String> values = this.headers(name);
         if (((List)values).isEmpty()) {
            values = new ArrayList();
            this.headers.put(name, values);
         }

         ((List)values).add(fixHeaderEncoding(value));
         return this;
      }

      public List<String> headers(String name) {
         Validate.notEmptyParam(name, "name");
         return this.getHeadersCaseInsensitive(name);
      }

      private static String fixHeaderEncoding(String val) {
         byte[] bytes = val.getBytes(HttpConnection.ISO_8859_1);
         return !looksLikeUtf8(bytes) ? val : new String(bytes, HttpConnection.UTF_8);
      }

      private static boolean looksLikeUtf8(byte[] input) {
         int i = 0;
         if (input.length >= 3 && (input[0] & 255) == 239 && (input[1] & 255) == 187 && (input[2] & 255) == 191) {
            i = 3;
         }

         for(int j = input.length; i < j; ++i) {
            int o = input[i];
            if ((o & 128) != 0) {
               int end;
               if ((o & 224) == 192) {
                  end = i + 1;
               } else if ((o & 240) == 224) {
                  end = i + 2;
               } else {
                  if ((o & 248) != 240) {
                     return false;
                  }

                  end = i + 3;
               }

               if (end >= input.length) {
                  return false;
               }

               while(i < end) {
                  ++i;
                  o = input[i];
                  if ((o & 192) != 128) {
                     return false;
                  }
               }
            }
         }

         return true;
      }

      public T header(String name, String value) {
         Validate.notEmptyParam(name, "name");
         this.removeHeader(name);
         this.addHeader(name, value);
         return this;
      }

      public boolean hasHeader(String name) {
         Validate.notEmptyParam(name, "name");
         return !this.getHeadersCaseInsensitive(name).isEmpty();
      }

      public boolean hasHeaderWithValue(String name, String value) {
         Validate.notEmpty(name);
         Validate.notEmpty(value);
         List<String> values = this.headers(name);
         Iterator var4 = values.iterator();

         String candidate;
         do {
            if (!var4.hasNext()) {
               return false;
            }

            candidate = (String)var4.next();
         } while(!value.equalsIgnoreCase(candidate));

         return true;
      }

      public T removeHeader(String name) {
         Validate.notEmptyParam(name, "name");
         Entry<String, List<String>> entry = this.scanHeaders(name);
         if (entry != null) {
            this.headers.remove(entry.getKey());
         }

         return this;
      }

      public Map<String, String> headers() {
         LinkedHashMap<String, String> map = new LinkedHashMap(this.headers.size());
         Iterator var2 = this.headers.entrySet().iterator();

         while(var2.hasNext()) {
            Entry<String, List<String>> entry = (Entry)var2.next();
            String header = (String)entry.getKey();
            List<String> values = (List)entry.getValue();
            if (values.size() > 0) {
               map.put(header, (String)values.get(0));
            }
         }

         return map;
      }

      public Map<String, List<String>> multiHeaders() {
         return this.headers;
      }

      private List<String> getHeadersCaseInsensitive(String name) {
         Validate.notNull(name);
         Iterator var2 = this.headers.entrySet().iterator();

         Entry entry;
         do {
            if (!var2.hasNext()) {
               return Collections.emptyList();
            }

            entry = (Entry)var2.next();
         } while(!name.equalsIgnoreCase((String)entry.getKey()));

         return (List)entry.getValue();
      }

      @Nullable
      private Entry<String, List<String>> scanHeaders(String name) {
         String lc = Normalizer.lowerCase(name);
         Iterator var3 = this.headers.entrySet().iterator();

         Entry entry;
         do {
            if (!var3.hasNext()) {
               return null;
            }

            entry = (Entry)var3.next();
         } while(!Normalizer.lowerCase((String)entry.getKey()).equals(lc));

         return entry;
      }

      public String cookie(String name) {
         Validate.notEmptyParam(name, "name");
         return (String)this.cookies.get(name);
      }

      public T cookie(String name, String value) {
         Validate.notEmptyParam(name, "name");
         Validate.notNullParam(value, "value");
         this.cookies.put(name, value);
         return this;
      }

      public boolean hasCookie(String name) {
         Validate.notEmptyParam(name, "name");
         return this.cookies.containsKey(name);
      }

      public T removeCookie(String name) {
         Validate.notEmptyParam(name, "name");
         this.cookies.remove(name);
         return this;
      }

      public Map<String, String> cookies() {
         return this.cookies;
      }

      // $FF: synthetic method
      Base(Object x0) {
         this();
      }

      // $FF: synthetic method
      Base(HttpConnection.Base x0, Object x1) {
         this(x0);
      }

      static {
         try {
            UnsetUrl = new URL("http://undefined/");
         } catch (MalformedURLException var1) {
            throw new IllegalStateException(var1);
         }
      }
   }
}
