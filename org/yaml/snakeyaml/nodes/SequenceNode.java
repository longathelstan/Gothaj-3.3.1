package org.yaml.snakeyaml.nodes;

import java.util.Iterator;
import java.util.List;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;

public class SequenceNode extends CollectionNode<Node> {
   private final List<Node> value;

   public SequenceNode(Tag tag, boolean resolved, List<Node> value, Mark startMark, Mark endMark, DumperOptions.FlowStyle flowStyle) {
      super(tag, startMark, endMark, flowStyle);
      if (value == null) {
         throw new NullPointerException("value in a Node is required.");
      } else {
         this.value = value;
         this.resolved = resolved;
      }
   }

   public SequenceNode(Tag tag, List<Node> value, DumperOptions.FlowStyle flowStyle) {
      this(tag, true, value, (Mark)null, (Mark)null, flowStyle);
   }

   public NodeId getNodeId() {
      return NodeId.sequence;
   }

   public List<Node> getValue() {
      return this.value;
   }

   public void setListType(Class<? extends Object> listType) {
      Iterator var2 = this.value.iterator();

      while(var2.hasNext()) {
         Node node = (Node)var2.next();
         node.setType(listType);
      }

   }

   public String toString() {
      StringBuilder buf = new StringBuilder();

      for(Iterator var2 = this.getValue().iterator(); var2.hasNext(); buf.append(",")) {
         Node node = (Node)var2.next();
         if (node instanceof CollectionNode) {
            buf.append(System.identityHashCode(node));
         } else {
            buf.append(node.toString());
         }
      }

      if (buf.length() > 0) {
         buf.deleteCharAt(buf.length() - 1);
      }

      return "<" + this.getClass().getName() + " (tag=" + this.getTag() + ", value=[" + buf + "])>";
   }
}
