package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;

public final class SequenceStartEvent extends CollectionStartEvent {
   public SequenceStartEvent(String anchor, String tag, boolean implicit, Mark startMark, Mark endMark, DumperOptions.FlowStyle flowStyle) {
      super(anchor, tag, implicit, startMark, endMark, flowStyle);
   }

   public Event.ID getEventId() {
      return Event.ID.SequenceStart;
   }
}