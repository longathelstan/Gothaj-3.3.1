package org.jsoup.safety;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.jsoup.helper.Validate;
import org.jsoup.internal.Normalizer;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

public class Safelist {
   private final Set<Safelist.TagName> tagNames;
   private final Map<Safelist.TagName, Set<Safelist.AttributeKey>> attributes;
   private final Map<Safelist.TagName, Map<Safelist.AttributeKey, Safelist.AttributeValue>> enforcedAttributes;
   private final Map<Safelist.TagName, Map<Safelist.AttributeKey, Set<Safelist.Protocol>>> protocols;
   private boolean preserveRelativeLinks;

   public static Safelist none() {
      return new Safelist();
   }

   public static Safelist simpleText() {
      return (new Safelist()).addTags("b", "em", "i", "strong", "u");
   }

   public static Safelist basic() {
      return (new Safelist()).addTags("a", "b", "blockquote", "br", "cite", "code", "dd", "dl", "dt", "em", "i", "li", "ol", "p", "pre", "q", "small", "span", "strike", "strong", "sub", "sup", "u", "ul").addAttributes("a", "href").addAttributes("blockquote", "cite").addAttributes("q", "cite").addProtocols("a", "href", "ftp", "http", "https", "mailto").addProtocols("blockquote", "cite", "http", "https").addProtocols("cite", "cite", "http", "https").addEnforcedAttribute("a", "rel", "nofollow");
   }

   public static Safelist basicWithImages() {
      return basic().addTags("img").addAttributes("img", "align", "alt", "height", "src", "title", "width").addProtocols("img", "src", "http", "https");
   }

   public static Safelist relaxed() {
      return (new Safelist()).addTags("a", "b", "blockquote", "br", "caption", "cite", "code", "col", "colgroup", "dd", "div", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6", "i", "img", "li", "ol", "p", "pre", "q", "small", "span", "strike", "strong", "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u", "ul").addAttributes("a", "href", "title").addAttributes("blockquote", "cite").addAttributes("col", "span", "width").addAttributes("colgroup", "span", "width").addAttributes("img", "align", "alt", "height", "src", "title", "width").addAttributes("ol", "start", "type").addAttributes("q", "cite").addAttributes("table", "summary", "width").addAttributes("td", "abbr", "axis", "colspan", "rowspan", "width").addAttributes("th", "abbr", "axis", "colspan", "rowspan", "scope", "width").addAttributes("ul", "type").addProtocols("a", "href", "ftp", "http", "https", "mailto").addProtocols("blockquote", "cite", "http", "https").addProtocols("cite", "cite", "http", "https").addProtocols("img", "src", "http", "https").addProtocols("q", "cite", "http", "https");
   }

   public Safelist() {
      this.tagNames = new HashSet();
      this.attributes = new HashMap();
      this.enforcedAttributes = new HashMap();
      this.protocols = new HashMap();
      this.preserveRelativeLinks = false;
   }

   public Safelist(Safelist copy) {
      this();
      this.tagNames.addAll(copy.tagNames);
      Iterator var2 = copy.attributes.entrySet().iterator();

      Entry protocolsEntry;
      while(var2.hasNext()) {
         protocolsEntry = (Entry)var2.next();
         this.attributes.put((Safelist.TagName)protocolsEntry.getKey(), new HashSet((Collection)protocolsEntry.getValue()));
      }

      var2 = copy.enforcedAttributes.entrySet().iterator();

      while(var2.hasNext()) {
         protocolsEntry = (Entry)var2.next();
         this.enforcedAttributes.put((Safelist.TagName)protocolsEntry.getKey(), new HashMap((Map)protocolsEntry.getValue()));
      }

      var2 = copy.protocols.entrySet().iterator();

      while(var2.hasNext()) {
         protocolsEntry = (Entry)var2.next();
         Map<Safelist.AttributeKey, Set<Safelist.Protocol>> attributeProtocolsCopy = new HashMap();
         Iterator var5 = ((Map)protocolsEntry.getValue()).entrySet().iterator();

         while(var5.hasNext()) {
            Entry<Safelist.AttributeKey, Set<Safelist.Protocol>> attributeProtocols = (Entry)var5.next();
            attributeProtocolsCopy.put((Safelist.AttributeKey)attributeProtocols.getKey(), new HashSet((Collection)attributeProtocols.getValue()));
         }

         this.protocols.put((Safelist.TagName)protocolsEntry.getKey(), attributeProtocolsCopy);
      }

      this.preserveRelativeLinks = copy.preserveRelativeLinks;
   }

   public Safelist addTags(String... tags) {
      Validate.notNull(tags);
      String[] var2 = tags;
      int var3 = tags.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String tagName = var2[var4];
         Validate.notEmpty(tagName);
         this.tagNames.add(Safelist.TagName.valueOf(tagName));
      }

      return this;
   }

   public Safelist removeTags(String... tags) {
      Validate.notNull(tags);
      String[] var2 = tags;
      int var3 = tags.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String tag = var2[var4];
         Validate.notEmpty(tag);
         Safelist.TagName tagName = Safelist.TagName.valueOf(tag);
         if (this.tagNames.remove(tagName)) {
            this.attributes.remove(tagName);
            this.enforcedAttributes.remove(tagName);
            this.protocols.remove(tagName);
         }
      }

      return this;
   }

   public Safelist addAttributes(String tag, String... attributes) {
      Validate.notEmpty(tag);
      Validate.notNull(attributes);
      Validate.isTrue(attributes.length > 0, "No attribute names supplied.");
      Safelist.TagName tagName = Safelist.TagName.valueOf(tag);
      this.tagNames.add(tagName);
      Set<Safelist.AttributeKey> attributeSet = new HashSet();
      String[] var5 = attributes;
      int var6 = attributes.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String key = var5[var7];
         Validate.notEmpty(key);
         attributeSet.add(Safelist.AttributeKey.valueOf(key));
      }

      if (this.attributes.containsKey(tagName)) {
         Set<Safelist.AttributeKey> currentSet = (Set)this.attributes.get(tagName);
         currentSet.addAll(attributeSet);
      } else {
         this.attributes.put(tagName, attributeSet);
      }

      return this;
   }

   public Safelist removeAttributes(String tag, String... attributes) {
      Validate.notEmpty(tag);
      Validate.notNull(attributes);
      Validate.isTrue(attributes.length > 0, "No attribute names supplied.");
      Safelist.TagName tagName = Safelist.TagName.valueOf(tag);
      Set<Safelist.AttributeKey> attributeSet = new HashSet();
      String[] var5 = attributes;
      int var6 = attributes.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String key = var5[var7];
         Validate.notEmpty(key);
         attributeSet.add(Safelist.AttributeKey.valueOf(key));
      }

      if (this.tagNames.contains(tagName) && this.attributes.containsKey(tagName)) {
         Set<Safelist.AttributeKey> currentSet = (Set)this.attributes.get(tagName);
         currentSet.removeAll(attributeSet);
         if (currentSet.isEmpty()) {
            this.attributes.remove(tagName);
         }
      }

      if (tag.equals(":all")) {
         Iterator var10 = this.attributes.keySet().iterator();

         while(var10.hasNext()) {
            Safelist.TagName name = (Safelist.TagName)var10.next();
            Set<Safelist.AttributeKey> currentSet = (Set)this.attributes.get(name);
            currentSet.removeAll(attributeSet);
            if (currentSet.isEmpty()) {
               this.attributes.remove(name);
            }
         }
      }

      return this;
   }

   public Safelist addEnforcedAttribute(String tag, String attribute, String value) {
      Validate.notEmpty(tag);
      Validate.notEmpty(attribute);
      Validate.notEmpty(value);
      Safelist.TagName tagName = Safelist.TagName.valueOf(tag);
      this.tagNames.add(tagName);
      Safelist.AttributeKey attrKey = Safelist.AttributeKey.valueOf(attribute);
      Safelist.AttributeValue attrVal = Safelist.AttributeValue.valueOf(value);
      if (this.enforcedAttributes.containsKey(tagName)) {
         ((Map)this.enforcedAttributes.get(tagName)).put(attrKey, attrVal);
      } else {
         Map<Safelist.AttributeKey, Safelist.AttributeValue> attrMap = new HashMap();
         attrMap.put(attrKey, attrVal);
         this.enforcedAttributes.put(tagName, attrMap);
      }

      return this;
   }

   public Safelist removeEnforcedAttribute(String tag, String attribute) {
      Validate.notEmpty(tag);
      Validate.notEmpty(attribute);
      Safelist.TagName tagName = Safelist.TagName.valueOf(tag);
      if (this.tagNames.contains(tagName) && this.enforcedAttributes.containsKey(tagName)) {
         Safelist.AttributeKey attrKey = Safelist.AttributeKey.valueOf(attribute);
         Map<Safelist.AttributeKey, Safelist.AttributeValue> attrMap = (Map)this.enforcedAttributes.get(tagName);
         attrMap.remove(attrKey);
         if (attrMap.isEmpty()) {
            this.enforcedAttributes.remove(tagName);
         }
      }

      return this;
   }

   public Safelist preserveRelativeLinks(boolean preserve) {
      this.preserveRelativeLinks = preserve;
      return this;
   }

   public Safelist addProtocols(String tag, String attribute, String... protocols) {
      Validate.notEmpty(tag);
      Validate.notEmpty(attribute);
      Validate.notNull(protocols);
      Safelist.TagName tagName = Safelist.TagName.valueOf(tag);
      Safelist.AttributeKey attrKey = Safelist.AttributeKey.valueOf(attribute);
      Object attrMap;
      if (this.protocols.containsKey(tagName)) {
         attrMap = (Map)this.protocols.get(tagName);
      } else {
         attrMap = new HashMap();
         this.protocols.put(tagName, attrMap);
      }

      Object protSet;
      if (((Map)attrMap).containsKey(attrKey)) {
         protSet = (Set)((Map)attrMap).get(attrKey);
      } else {
         protSet = new HashSet();
         ((Map)attrMap).put(attrKey, protSet);
      }

      String[] var8 = protocols;
      int var9 = protocols.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         String protocol = var8[var10];
         Validate.notEmpty(protocol);
         Safelist.Protocol prot = Safelist.Protocol.valueOf(protocol);
         ((Set)protSet).add(prot);
      }

      return this;
   }

   public Safelist removeProtocols(String tag, String attribute, String... removeProtocols) {
      Validate.notEmpty(tag);
      Validate.notEmpty(attribute);
      Validate.notNull(removeProtocols);
      Safelist.TagName tagName = Safelist.TagName.valueOf(tag);
      Safelist.AttributeKey attr = Safelist.AttributeKey.valueOf(attribute);
      Validate.isTrue(this.protocols.containsKey(tagName), "Cannot remove a protocol that is not set.");
      Map<Safelist.AttributeKey, Set<Safelist.Protocol>> tagProtocols = (Map)this.protocols.get(tagName);
      Validate.isTrue(tagProtocols.containsKey(attr), "Cannot remove a protocol that is not set.");
      Set<Safelist.Protocol> attrProtocols = (Set)tagProtocols.get(attr);
      String[] var8 = removeProtocols;
      int var9 = removeProtocols.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         String protocol = var8[var10];
         Validate.notEmpty(protocol);
         attrProtocols.remove(Safelist.Protocol.valueOf(protocol));
      }

      if (attrProtocols.isEmpty()) {
         tagProtocols.remove(attr);
         if (tagProtocols.isEmpty()) {
            this.protocols.remove(tagName);
         }
      }

      return this;
   }

   protected boolean isSafeTag(String tag) {
      return this.tagNames.contains(Safelist.TagName.valueOf(tag));
   }

   protected boolean isSafeAttribute(String tagName, Element el, Attribute attr) {
      Safelist.TagName tag = Safelist.TagName.valueOf(tagName);
      Safelist.AttributeKey key = Safelist.AttributeKey.valueOf(attr.getKey());
      Set<Safelist.AttributeKey> okSet = (Set)this.attributes.get(tag);
      Map attrProts;
      if (okSet != null && okSet.contains(key)) {
         if (!this.protocols.containsKey(tag)) {
            return true;
         } else {
            attrProts = (Map)this.protocols.get(tag);
            return !attrProts.containsKey(key) || this.testValidProtocol(el, attr, (Set)attrProts.get(key));
         }
      } else {
         attrProts = (Map)this.enforcedAttributes.get(tag);
         if (attrProts != null) {
            Attributes expect = this.getEnforcedAttributes(tagName);
            String attrKey = attr.getKey();
            if (expect.hasKeyIgnoreCase(attrKey)) {
               return expect.getIgnoreCase(attrKey).equals(attr.getValue());
            }
         }

         return !tagName.equals(":all") && this.isSafeAttribute(":all", el, attr);
      }
   }

   private boolean testValidProtocol(Element el, Attribute attr, Set<Safelist.Protocol> protocols) {
      String value = el.absUrl(attr.getKey());
      if (value.length() == 0) {
         value = attr.getValue();
      }

      if (!this.preserveRelativeLinks) {
         attr.setValue(value);
      }

      Iterator var5 = protocols.iterator();

      while(var5.hasNext()) {
         Safelist.Protocol protocol = (Safelist.Protocol)var5.next();
         String prot = protocol.toString();
         if (prot.equals("#")) {
            if (this.isValidAnchor(value)) {
               return true;
            }
         } else {
            prot = prot + ":";
            if (Normalizer.lowerCase(value).startsWith(prot)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean isValidAnchor(String value) {
      return value.startsWith("#") && !value.matches(".*\\s.*");
   }

   Attributes getEnforcedAttributes(String tagName) {
      Attributes attrs = new Attributes();
      Safelist.TagName tag = Safelist.TagName.valueOf(tagName);
      if (this.enforcedAttributes.containsKey(tag)) {
         Map<Safelist.AttributeKey, Safelist.AttributeValue> keyVals = (Map)this.enforcedAttributes.get(tag);
         Iterator var5 = keyVals.entrySet().iterator();

         while(var5.hasNext()) {
            Entry<Safelist.AttributeKey, Safelist.AttributeValue> entry = (Entry)var5.next();
            attrs.put(((Safelist.AttributeKey)entry.getKey()).toString(), ((Safelist.AttributeValue)entry.getValue()).toString());
         }
      }

      return attrs;
   }

   static class TagName extends Safelist.TypedValue {
      TagName(String value) {
         super(value);
      }

      static Safelist.TagName valueOf(String value) {
         return new Safelist.TagName(value);
      }
   }

   static class AttributeKey extends Safelist.TypedValue {
      AttributeKey(String value) {
         super(value);
      }

      static Safelist.AttributeKey valueOf(String value) {
         return new Safelist.AttributeKey(value);
      }
   }

   static class AttributeValue extends Safelist.TypedValue {
      AttributeValue(String value) {
         super(value);
      }

      static Safelist.AttributeValue valueOf(String value) {
         return new Safelist.AttributeValue(value);
      }
   }

   static class Protocol extends Safelist.TypedValue {
      Protocol(String value) {
         super(value);
      }

      static Safelist.Protocol valueOf(String value) {
         return new Safelist.Protocol(value);
      }
   }

   abstract static class TypedValue {
      private final String value;

      TypedValue(String value) {
         Validate.notNull(value);
         this.value = value;
      }

      public int hashCode() {
         int prime = true;
         int result = 1;
         int result = 31 * result + (this.value == null ? 0 : this.value.hashCode());
         return result;
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            Safelist.TypedValue other = (Safelist.TypedValue)obj;
            if (this.value == null) {
               return other.value == null;
            } else {
               return this.value.equals(other.value);
            }
         }
      }

      public String toString() {
         return this.value;
      }
   }
}
