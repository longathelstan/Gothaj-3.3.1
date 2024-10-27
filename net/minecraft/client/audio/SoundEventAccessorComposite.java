package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.util.ResourceLocation;

public class SoundEventAccessorComposite implements ISoundEventAccessor<SoundPoolEntry> {
   private final List<ISoundEventAccessor<SoundPoolEntry>> soundPool = Lists.newArrayList();
   private final Random rnd = new Random();
   private final ResourceLocation soundLocation;
   private final SoundCategory category;
   private double eventPitch;
   private double eventVolume;

   public SoundEventAccessorComposite(ResourceLocation soundLocation, double pitch, double volume, SoundCategory category) {
      this.soundLocation = soundLocation;
      this.eventVolume = volume;
      this.eventPitch = pitch;
      this.category = category;
   }

   public int getWeight() {
      int i = 0;

      ISoundEventAccessor isoundeventaccessor;
      for(Iterator var3 = this.soundPool.iterator(); var3.hasNext(); i += isoundeventaccessor.getWeight()) {
         isoundeventaccessor = (ISoundEventAccessor)var3.next();
      }

      return i;
   }

   public SoundPoolEntry cloneEntry() {
      int i = this.getWeight();
      if (!this.soundPool.isEmpty() && i != 0) {
         int j = this.rnd.nextInt(i);
         Iterator var4 = this.soundPool.iterator();

         while(var4.hasNext()) {
            ISoundEventAccessor<SoundPoolEntry> isoundeventaccessor = (ISoundEventAccessor)var4.next();
            j -= isoundeventaccessor.getWeight();
            if (j < 0) {
               SoundPoolEntry soundpoolentry = (SoundPoolEntry)isoundeventaccessor.cloneEntry();
               soundpoolentry.setPitch(soundpoolentry.getPitch() * this.eventPitch);
               soundpoolentry.setVolume(soundpoolentry.getVolume() * this.eventVolume);
               return soundpoolentry;
            }
         }

         return SoundHandler.missing_sound;
      } else {
         return SoundHandler.missing_sound;
      }
   }

   public void addSoundToEventPool(ISoundEventAccessor<SoundPoolEntry> sound) {
      this.soundPool.add(sound);
   }

   public ResourceLocation getSoundEventLocation() {
      return this.soundLocation;
   }

   public SoundCategory getSoundCategory() {
      return this.category;
   }
}
