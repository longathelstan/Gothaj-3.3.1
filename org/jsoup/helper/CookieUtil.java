package org.jsoup.helper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.jsoup.Connection;
import org.jsoup.internal.StringUtil;

class CookieUtil {
   private static final Map<String, List<String>> EmptyRequestHeaders = Collections.unmodifiableMap(new HashMap());
   private static final String Sep = "; ";
   private static final String CookieName = "Cookie";
   private static final String Cookie2Name = "Cookie2";

   static void applyCookiesToRequest(HttpConnection.Request req, HttpURLConnection con) throws IOException {
      Set<String> cookieSet = requestCookieSet(req);
      Set<String> cookies2 = null;
      Map<String, List<String>> storedCookies = req.cookieManager().get(asUri(req.url), EmptyRequestHeaders);
      Iterator var5 = storedCookies.entrySet().iterator();

      while(true) {
         List cookies;
         Object set;
         while(true) {
            Entry entry;
            do {
               do {
                  if (!var5.hasNext()) {
                     if (cookieSet.size() > 0) {
                        con.addRequestProperty("Cookie", StringUtil.join((Collection)cookieSet, "; "));
                     }

                     if (cookies2 != null && ((Set)cookies2).size() > 0) {
                        con.addRequestProperty("Cookie2", StringUtil.join((Collection)cookies2, "; "));
                     }

                     return;
                  }

                  entry = (Entry)var5.next();
                  cookies = (List)entry.getValue();
               } while(cookies == null);
            } while(cookies.size() == 0);

            String key = (String)entry.getKey();
            if ("Cookie".equals(key)) {
               set = cookieSet;
               break;
            }

            if ("Cookie2".equals(key)) {
               set = new HashSet();
               cookies2 = set;
               break;
            }
         }

         ((Set)set).addAll(cookies);
      }
   }

   private static LinkedHashSet<String> requestCookieSet(Connection.Request req) {
      LinkedHashSet<String> set = new LinkedHashSet();
      Iterator var2 = req.cookies().entrySet().iterator();

      while(var2.hasNext()) {
         Entry<String, String> cookie = (Entry)var2.next();
         set.add((String)cookie.getKey() + "=" + (String)cookie.getValue());
      }

      return set;
   }

   static URI asUri(URL url) throws IOException {
      try {
         return url.toURI();
      } catch (URISyntaxException var3) {
         MalformedURLException ue = new MalformedURLException(var3.getMessage());
         ue.initCause(var3);
         throw ue;
      }
   }

   static void storeCookies(HttpConnection.Request req, URL url, Map<String, List<String>> resHeaders) throws IOException {
      req.cookieManager().put(asUri(url), resHeaders);
   }
}
