package org.yaml.snakeyaml.inspector;

import org.yaml.snakeyaml.nodes.Tag;

public final class UnTrustedTagInspector implements TagInspector {
   public boolean isGlobalTagAllowed(Tag tag) {
      return false;
   }
}
