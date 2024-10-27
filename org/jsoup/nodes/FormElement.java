package org.jsoup.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.helper.Validate;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

public class FormElement extends Element {
   private final Elements elements = new Elements();

   public FormElement(Tag tag, String baseUri, Attributes attributes) {
      super(tag, baseUri, attributes);
   }

   public Elements elements() {
      return this.elements;
   }

   public FormElement addElement(Element element) {
      this.elements.add(element);
      return this;
   }

   protected void removeChild(Node out) {
      super.removeChild(out);
      this.elements.remove(out);
   }

   public Connection submit() {
      String action = this.hasAttr("action") ? this.absUrl("action") : this.baseUri();
      Validate.notEmpty(action, "Could not determine a form action URL for submit. Ensure you set a base URI when parsing.");
      Connection.Method method = this.attr("method").equalsIgnoreCase("POST") ? Connection.Method.POST : Connection.Method.GET;
      Document owner = this.ownerDocument();
      Connection connection = owner != null ? owner.connection().newRequest() : Jsoup.newSession();
      return connection.url(action).data((Collection)this.formData()).method(method);
   }

   public List<Connection.KeyVal> formData() {
      ArrayList<Connection.KeyVal> data = new ArrayList();
      Iterator var2 = this.elements.iterator();

      while(true) {
         while(true) {
            Element el;
            String name;
            String type;
            do {
               do {
                  do {
                     do {
                        if (!var2.hasNext()) {
                           return data;
                        }

                        el = (Element)var2.next();
                     } while(!el.tag().isFormSubmittable());
                  } while(el.hasAttr("disabled"));

                  name = el.attr("name");
               } while(name.length() == 0);

               type = el.attr("type");
            } while(type.equalsIgnoreCase("button"));

            if ("select".equals(el.normalName())) {
               Elements options = el.select("option[selected]");
               boolean set = false;

               for(Iterator var8 = options.iterator(); var8.hasNext(); set = true) {
                  Element option = (Element)var8.next();
                  data.add(HttpConnection.KeyVal.create(name, option.val()));
               }

               if (!set) {
                  Element option = el.selectFirst("option");
                  if (option != null) {
                     data.add(HttpConnection.KeyVal.create(name, option.val()));
                  }
               }
            } else if (!"checkbox".equalsIgnoreCase(type) && !"radio".equalsIgnoreCase(type)) {
               data.add(HttpConnection.KeyVal.create(name, el.val()));
            } else if (el.hasAttr("checked")) {
               String val = el.val().length() > 0 ? el.val() : "on";
               data.add(HttpConnection.KeyVal.create(name, val));
            }
         }
      }
   }

   public FormElement clone() {
      return (FormElement)super.clone();
   }
}
