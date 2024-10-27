package org.jsoup.nodes;

import java.util.List;

abstract class LeafNode extends Node {
   Object value;

   protected final boolean hasAttributes() {
      return this.value instanceof Attributes;
   }

   public final Attributes attributes() {
      this.ensureAttributes();
      return (Attributes)this.value;
   }

   private void ensureAttributes() {
      if (!this.hasAttributes()) {
         Object coreValue = this.value;
         Attributes attributes = new Attributes();
         this.value = attributes;
         if (coreValue != null) {
            attributes.put(this.nodeName(), (String)coreValue);
         }
      }

   }

   String coreValue() {
      return this.attr(this.nodeName());
   }

   void coreValue(String value) {
      this.attr(this.nodeName(), value);
   }

   public String attr(String key) {
      if (!this.hasAttributes()) {
         return this.nodeName().equals(key) ? (String)this.value : "";
      } else {
         return super.attr(key);
      }
   }

   public Node attr(String key, String value) {
      if (!this.hasAttributes() && key.equals(this.nodeName())) {
         this.value = value;
      } else {
         this.ensureAttributes();
         super.attr(key, value);
      }

      return this;
   }

   public boolean hasAttr(String key) {
      this.ensureAttributes();
      return super.hasAttr(key);
   }

   public Node removeAttr(String key) {
      this.ensureAttributes();
      return super.removeAttr(key);
   }

   public String absUrl(String key) {
      this.ensureAttributes();
      return super.absUrl(key);
   }

   public String baseUri() {
      return this.hasParent() ? this.parent().baseUri() : "";
   }

   protected void doSetBaseUri(String baseUri) {
   }

   public int childNodeSize() {
      return 0;
   }

   public Node empty() {
      return this;
   }

   protected List<Node> ensureChildNodes() {
      return EmptyNodes;
   }

   protected LeafNode doClone(Node parent) {
      LeafNode clone = (LeafNode)super.doClone(parent);
      if (this.hasAttributes()) {
         clone.value = ((Attributes)this.value).clone();
      }

      return clone;
   }
}
