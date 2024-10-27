package net.minecraft.client.resources.model;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.texture.IIconCreator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IRegistry;
import net.minecraft.util.RegistrySimple;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ITransformation;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.fml.common.registry.RegistryDelegate;
import net.optifine.CustomItems;
import net.optifine.reflect.Reflector;
import net.optifine.util.StrUtils;
import net.optifine.util.TextureUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelBakery {
   private static final Set<ResourceLocation> LOCATIONS_BUILTIN_TEXTURES = Sets.newHashSet(new ResourceLocation[]{new ResourceLocation("blocks/water_flow"), new ResourceLocation("blocks/water_still"), new ResourceLocation("blocks/lava_flow"), new ResourceLocation("blocks/lava_still"), new ResourceLocation("blocks/destroy_stage_0"), new ResourceLocation("blocks/destroy_stage_1"), new ResourceLocation("blocks/destroy_stage_2"), new ResourceLocation("blocks/destroy_stage_3"), new ResourceLocation("blocks/destroy_stage_4"), new ResourceLocation("blocks/destroy_stage_5"), new ResourceLocation("blocks/destroy_stage_6"), new ResourceLocation("blocks/destroy_stage_7"), new ResourceLocation("blocks/destroy_stage_8"), new ResourceLocation("blocks/destroy_stage_9"), new ResourceLocation("items/empty_armor_slot_helmet"), new ResourceLocation("items/empty_armor_slot_chestplate"), new ResourceLocation("items/empty_armor_slot_leggings"), new ResourceLocation("items/empty_armor_slot_boots")});
   private static final Logger LOGGER = LogManager.getLogger();
   protected static final ModelResourceLocation MODEL_MISSING = new ModelResourceLocation("builtin/missing", "missing");
   private static final Map<String, String> BUILT_IN_MODELS = Maps.newHashMap();
   private static final Joiner JOINER = Joiner.on(" -> ");
   private final IResourceManager resourceManager;
   private final Map<ResourceLocation, TextureAtlasSprite> sprites = Maps.newHashMap();
   private final Map<ResourceLocation, ModelBlock> models = Maps.newLinkedHashMap();
   private final Map<ModelResourceLocation, ModelBlockDefinition.Variants> variants = Maps.newLinkedHashMap();
   private final TextureMap textureMap;
   private final BlockModelShapes blockModelShapes;
   private final FaceBakery faceBakery = new FaceBakery();
   private final ItemModelGenerator itemModelGenerator = new ItemModelGenerator();
   private RegistrySimple<ModelResourceLocation, IBakedModel> bakedRegistry = new RegistrySimple();
   private static final ModelBlock MODEL_GENERATED = ModelBlock.deserialize("{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}");
   private static final ModelBlock MODEL_COMPASS = ModelBlock.deserialize("{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}");
   private static final ModelBlock MODEL_CLOCK = ModelBlock.deserialize("{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}");
   private static final ModelBlock MODEL_ENTITY = ModelBlock.deserialize("{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}");
   private Map<String, ResourceLocation> itemLocations = Maps.newLinkedHashMap();
   private final Map<ResourceLocation, ModelBlockDefinition> blockDefinitions = Maps.newHashMap();
   private Map<Item, List<String>> variantNames = Maps.newIdentityHashMap();
   private static Map<RegistryDelegate<Item>, Set<String>> customVariantNames = Maps.newHashMap();

   static {
      BUILT_IN_MODELS.put("missing", "{ \"textures\": {   \"particle\": \"missingno\",   \"missingno\": \"missingno\"}, \"elements\": [ {     \"from\": [ 0, 0, 0 ],     \"to\": [ 16, 16, 16 ],     \"faces\": {         \"down\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"down\", \"texture\": \"#missingno\" },         \"up\":    { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"up\", \"texture\": \"#missingno\" },         \"north\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"north\", \"texture\": \"#missingno\" },         \"south\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"south\", \"texture\": \"#missingno\" },         \"west\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"west\", \"texture\": \"#missingno\" },         \"east\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"east\", \"texture\": \"#missingno\" }    }}]}");
      MODEL_GENERATED.name = "generation marker";
      MODEL_COMPASS.name = "compass generation marker";
      MODEL_CLOCK.name = "class generation marker";
      MODEL_ENTITY.name = "block entity marker";
   }

   public ModelBakery(IResourceManager p_i46085_1_, TextureMap p_i46085_2_, BlockModelShapes p_i46085_3_) {
      this.resourceManager = p_i46085_1_;
      this.textureMap = p_i46085_2_;
      this.blockModelShapes = p_i46085_3_;
   }

   public IRegistry<ModelResourceLocation, IBakedModel> setupModelRegistry() {
      this.loadVariantItemModels();
      this.loadModelsCheck();
      this.loadSprites();
      this.bakeItemModels();
      this.bakeBlockModels();
      return this.bakedRegistry;
   }

   private void loadVariantItemModels() {
      this.loadVariants(this.blockModelShapes.getBlockStateMapper().putAllStateModelLocations().values());
      this.variants.put(MODEL_MISSING, new ModelBlockDefinition.Variants(MODEL_MISSING.getVariant(), Lists.newArrayList(new ModelBlockDefinition.Variant[]{new ModelBlockDefinition.Variant(new ResourceLocation(MODEL_MISSING.getResourcePath()), ModelRotation.X0_Y0, false, 1)})));
      ResourceLocation resourcelocation = new ResourceLocation("item_frame");
      ModelBlockDefinition modelblockdefinition = this.getModelBlockDefinition(resourcelocation);
      this.registerVariant(modelblockdefinition, new ModelResourceLocation(resourcelocation, "normal"));
      this.registerVariant(modelblockdefinition, new ModelResourceLocation(resourcelocation, "map"));
      this.loadVariantModels();
      this.loadItemModels();
   }

   private void loadVariants(Collection<ModelResourceLocation> p_177591_1_) {
      Iterator var3 = p_177591_1_.iterator();

      while(var3.hasNext()) {
         ModelResourceLocation modelresourcelocation = (ModelResourceLocation)var3.next();

         try {
            ModelBlockDefinition modelblockdefinition = this.getModelBlockDefinition(modelresourcelocation);

            try {
               this.registerVariant(modelblockdefinition, modelresourcelocation);
            } catch (Exception var6) {
               LOGGER.warn("Unable to load variant: " + modelresourcelocation.getVariant() + " from " + modelresourcelocation, var6);
            }
         } catch (Exception var7) {
            LOGGER.warn("Unable to load definition " + modelresourcelocation, var7);
         }
      }

   }

   private void registerVariant(ModelBlockDefinition p_177569_1_, ModelResourceLocation p_177569_2_) {
      this.variants.put(p_177569_2_, p_177569_1_.getVariants(p_177569_2_.getVariant()));
   }

   private ModelBlockDefinition getModelBlockDefinition(ResourceLocation p_177586_1_) {
      ResourceLocation resourcelocation = this.getBlockStateLocation(p_177586_1_);
      ModelBlockDefinition modelblockdefinition = (ModelBlockDefinition)this.blockDefinitions.get(resourcelocation);
      if (modelblockdefinition == null) {
         ArrayList list = Lists.newArrayList();

         try {
            Iterator var6 = this.resourceManager.getAllResources(resourcelocation).iterator();

            while(var6.hasNext()) {
               IResource iresource = (IResource)var6.next();
               InputStream inputstream = null;

               try {
                  inputstream = iresource.getInputStream();
                  ModelBlockDefinition modelblockdefinition1 = ModelBlockDefinition.parseFromReader(new InputStreamReader(inputstream, Charsets.UTF_8));
                  list.add(modelblockdefinition1);
               } catch (Exception var13) {
                  throw new RuntimeException("Encountered an exception when loading model definition of '" + p_177586_1_ + "' from: '" + iresource.getResourceLocation() + "' in resourcepack: '" + iresource.getResourcePackName() + "'", var13);
               } finally {
                  IOUtils.closeQuietly(inputstream);
               }
            }
         } catch (IOException var15) {
            throw new RuntimeException("Encountered an exception when loading model definition of model " + resourcelocation.toString(), var15);
         }

         modelblockdefinition = new ModelBlockDefinition(list);
         this.blockDefinitions.put(resourcelocation, modelblockdefinition);
      }

      return modelblockdefinition;
   }

   private ResourceLocation getBlockStateLocation(ResourceLocation p_177584_1_) {
      return new ResourceLocation(p_177584_1_.getResourceDomain(), "blockstates/" + p_177584_1_.getResourcePath() + ".json");
   }

   private void loadVariantModels() {
      Iterator var2 = this.variants.keySet().iterator();

      while(var2.hasNext()) {
         ModelResourceLocation modelresourcelocation = (ModelResourceLocation)var2.next();
         Iterator var4 = ((ModelBlockDefinition.Variants)this.variants.get(modelresourcelocation)).getVariants().iterator();

         while(var4.hasNext()) {
            ModelBlockDefinition.Variant modelblockdefinition$variant = (ModelBlockDefinition.Variant)var4.next();
            ResourceLocation resourcelocation = modelblockdefinition$variant.getModelLocation();
            if (this.models.get(resourcelocation) == null) {
               try {
                  ModelBlock modelblock = this.loadModel(resourcelocation);
                  this.models.put(resourcelocation, modelblock);
               } catch (Exception var7) {
                  LOGGER.warn("Unable to load block model: '" + resourcelocation + "' for variant: '" + modelresourcelocation + "'", var7);
               }
            }
         }
      }

   }

   private ModelBlock loadModel(ResourceLocation p_177594_1_) throws IOException {
      String s = p_177594_1_.getResourcePath();
      if ("builtin/generated".equals(s)) {
         return MODEL_GENERATED;
      } else if ("builtin/compass".equals(s)) {
         return MODEL_COMPASS;
      } else if ("builtin/clock".equals(s)) {
         return MODEL_CLOCK;
      } else if ("builtin/entity".equals(s)) {
         return MODEL_ENTITY;
      } else {
         Object reader;
         if (s.startsWith("builtin/")) {
            String s1 = s.substring("builtin/".length());
            String s2 = (String)BUILT_IN_MODELS.get(s1);
            if (s2 == null) {
               throw new FileNotFoundException(p_177594_1_.toString());
            }

            reader = new StringReader(s2);
         } else {
            p_177594_1_ = this.getModelLocation(p_177594_1_);
            IResource iresource = this.resourceManager.getResource(p_177594_1_);
            reader = new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8);
         }

         ModelBlock modelblock;
         try {
            ModelBlock modelblock1 = ModelBlock.deserialize((Reader)reader);
            modelblock1.name = p_177594_1_.toString();
            modelblock = modelblock1;
            String s3 = TextureUtils.getBasePath(p_177594_1_.getResourcePath());
            fixModelLocations(modelblock1, s3);
         } finally {
            ((Reader)reader).close();
         }

         return modelblock;
      }
   }

   private ResourceLocation getModelLocation(ResourceLocation p_177580_1_) {
      ResourceLocation resourcelocation = p_177580_1_;
      String s = p_177580_1_.getResourcePath();
      if (!s.startsWith("mcpatcher") && !s.startsWith("optifine")) {
         return new ResourceLocation(p_177580_1_.getResourceDomain(), "models/" + p_177580_1_.getResourcePath() + ".json");
      } else {
         if (!s.endsWith(".json")) {
            resourcelocation = new ResourceLocation(p_177580_1_.getResourceDomain(), s + ".json");
         }

         return resourcelocation;
      }
   }

   private void loadItemModels() {
      this.registerVariantNames();
      Iterator var2 = Item.itemRegistry.iterator();

      while(var2.hasNext()) {
         Item item = (Item)var2.next();
         Iterator var4 = this.getVariantNames(item).iterator();

         while(var4.hasNext()) {
            String s = (String)var4.next();
            ResourceLocation resourcelocation = this.getItemLocation(s);
            this.itemLocations.put(s, resourcelocation);
            if (this.models.get(resourcelocation) == null) {
               try {
                  ModelBlock modelblock = this.loadModel(resourcelocation);
                  this.models.put(resourcelocation, modelblock);
               } catch (Exception var7) {
                  LOGGER.warn("Unable to load item model: '" + resourcelocation + "' for item: '" + Item.itemRegistry.getNameForObject(item) + "'", var7);
               }
            }
         }
      }

   }

   public void loadItemModel(String p_loadItemModel_1_, ResourceLocation p_loadItemModel_2_, ResourceLocation p_loadItemModel_3_) {
      this.itemLocations.put(p_loadItemModel_1_, p_loadItemModel_2_);
      if (this.models.get(p_loadItemModel_2_) == null) {
         try {
            ModelBlock modelblock = this.loadModel(p_loadItemModel_2_);
            this.models.put(p_loadItemModel_2_, modelblock);
         } catch (Exception var5) {
            LOGGER.warn("Unable to load item model: '{}' for item: '{}'", new Object[]{p_loadItemModel_2_, p_loadItemModel_3_});
            LOGGER.warn(var5.getClass().getName() + ": " + var5.getMessage());
         }
      }

   }

   private void registerVariantNames() {
      this.variantNames.clear();
      this.variantNames.put(Item.getItemFromBlock(Blocks.stone), Lists.newArrayList(new String[]{"stone", "granite", "granite_smooth", "diorite", "diorite_smooth", "andesite", "andesite_smooth"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.dirt), Lists.newArrayList(new String[]{"dirt", "coarse_dirt", "podzol"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.planks), Lists.newArrayList(new String[]{"oak_planks", "spruce_planks", "birch_planks", "jungle_planks", "acacia_planks", "dark_oak_planks"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.sapling), Lists.newArrayList(new String[]{"oak_sapling", "spruce_sapling", "birch_sapling", "jungle_sapling", "acacia_sapling", "dark_oak_sapling"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.sand), Lists.newArrayList(new String[]{"sand", "red_sand"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.log), Lists.newArrayList(new String[]{"oak_log", "spruce_log", "birch_log", "jungle_log"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.leaves), Lists.newArrayList(new String[]{"oak_leaves", "spruce_leaves", "birch_leaves", "jungle_leaves"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.sponge), Lists.newArrayList(new String[]{"sponge", "sponge_wet"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.sandstone), Lists.newArrayList(new String[]{"sandstone", "chiseled_sandstone", "smooth_sandstone"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.red_sandstone), Lists.newArrayList(new String[]{"red_sandstone", "chiseled_red_sandstone", "smooth_red_sandstone"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.tallgrass), Lists.newArrayList(new String[]{"dead_bush", "tall_grass", "fern"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.deadbush), Lists.newArrayList(new String[]{"dead_bush"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.wool), Lists.newArrayList(new String[]{"black_wool", "red_wool", "green_wool", "brown_wool", "blue_wool", "purple_wool", "cyan_wool", "silver_wool", "gray_wool", "pink_wool", "lime_wool", "yellow_wool", "light_blue_wool", "magenta_wool", "orange_wool", "white_wool"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.yellow_flower), Lists.newArrayList(new String[]{"dandelion"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.red_flower), Lists.newArrayList(new String[]{"poppy", "blue_orchid", "allium", "houstonia", "red_tulip", "orange_tulip", "white_tulip", "pink_tulip", "oxeye_daisy"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.stone_slab), Lists.newArrayList(new String[]{"stone_slab", "sandstone_slab", "cobblestone_slab", "brick_slab", "stone_brick_slab", "nether_brick_slab", "quartz_slab"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.stone_slab2), Lists.newArrayList(new String[]{"red_sandstone_slab"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.stained_glass), Lists.newArrayList(new String[]{"black_stained_glass", "red_stained_glass", "green_stained_glass", "brown_stained_glass", "blue_stained_glass", "purple_stained_glass", "cyan_stained_glass", "silver_stained_glass", "gray_stained_glass", "pink_stained_glass", "lime_stained_glass", "yellow_stained_glass", "light_blue_stained_glass", "magenta_stained_glass", "orange_stained_glass", "white_stained_glass"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.monster_egg), Lists.newArrayList(new String[]{"stone_monster_egg", "cobblestone_monster_egg", "stone_brick_monster_egg", "mossy_brick_monster_egg", "cracked_brick_monster_egg", "chiseled_brick_monster_egg"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.stonebrick), Lists.newArrayList(new String[]{"stonebrick", "mossy_stonebrick", "cracked_stonebrick", "chiseled_stonebrick"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.wooden_slab), Lists.newArrayList(new String[]{"oak_slab", "spruce_slab", "birch_slab", "jungle_slab", "acacia_slab", "dark_oak_slab"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.cobblestone_wall), Lists.newArrayList(new String[]{"cobblestone_wall", "mossy_cobblestone_wall"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.anvil), Lists.newArrayList(new String[]{"anvil_intact", "anvil_slightly_damaged", "anvil_very_damaged"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.quartz_block), Lists.newArrayList(new String[]{"quartz_block", "chiseled_quartz_block", "quartz_column"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.stained_hardened_clay), Lists.newArrayList(new String[]{"black_stained_hardened_clay", "red_stained_hardened_clay", "green_stained_hardened_clay", "brown_stained_hardened_clay", "blue_stained_hardened_clay", "purple_stained_hardened_clay", "cyan_stained_hardened_clay", "silver_stained_hardened_clay", "gray_stained_hardened_clay", "pink_stained_hardened_clay", "lime_stained_hardened_clay", "yellow_stained_hardened_clay", "light_blue_stained_hardened_clay", "magenta_stained_hardened_clay", "orange_stained_hardened_clay", "white_stained_hardened_clay"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.stained_glass_pane), Lists.newArrayList(new String[]{"black_stained_glass_pane", "red_stained_glass_pane", "green_stained_glass_pane", "brown_stained_glass_pane", "blue_stained_glass_pane", "purple_stained_glass_pane", "cyan_stained_glass_pane", "silver_stained_glass_pane", "gray_stained_glass_pane", "pink_stained_glass_pane", "lime_stained_glass_pane", "yellow_stained_glass_pane", "light_blue_stained_glass_pane", "magenta_stained_glass_pane", "orange_stained_glass_pane", "white_stained_glass_pane"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.leaves2), Lists.newArrayList(new String[]{"acacia_leaves", "dark_oak_leaves"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.log2), Lists.newArrayList(new String[]{"acacia_log", "dark_oak_log"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.prismarine), Lists.newArrayList(new String[]{"prismarine", "prismarine_bricks", "dark_prismarine"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.carpet), Lists.newArrayList(new String[]{"black_carpet", "red_carpet", "green_carpet", "brown_carpet", "blue_carpet", "purple_carpet", "cyan_carpet", "silver_carpet", "gray_carpet", "pink_carpet", "lime_carpet", "yellow_carpet", "light_blue_carpet", "magenta_carpet", "orange_carpet", "white_carpet"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.double_plant), Lists.newArrayList(new String[]{"sunflower", "syringa", "double_grass", "double_fern", "double_rose", "paeonia"}));
      this.variantNames.put(Items.bow, Lists.newArrayList(new String[]{"bow", "bow_pulling_0", "bow_pulling_1", "bow_pulling_2"}));
      this.variantNames.put(Items.coal, Lists.newArrayList(new String[]{"coal", "charcoal"}));
      this.variantNames.put(Items.fishing_rod, Lists.newArrayList(new String[]{"fishing_rod", "fishing_rod_cast"}));
      this.variantNames.put(Items.fish, Lists.newArrayList(new String[]{"cod", "salmon", "clownfish", "pufferfish"}));
      this.variantNames.put(Items.cooked_fish, Lists.newArrayList(new String[]{"cooked_cod", "cooked_salmon"}));
      this.variantNames.put(Items.dye, Lists.newArrayList(new String[]{"dye_black", "dye_red", "dye_green", "dye_brown", "dye_blue", "dye_purple", "dye_cyan", "dye_silver", "dye_gray", "dye_pink", "dye_lime", "dye_yellow", "dye_light_blue", "dye_magenta", "dye_orange", "dye_white"}));
      this.variantNames.put(Items.potionitem, Lists.newArrayList(new String[]{"bottle_drinkable", "bottle_splash"}));
      this.variantNames.put(Items.skull, Lists.newArrayList(new String[]{"skull_skeleton", "skull_wither", "skull_zombie", "skull_char", "skull_creeper"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.oak_fence_gate), Lists.newArrayList(new String[]{"oak_fence_gate"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.oak_fence), Lists.newArrayList(new String[]{"oak_fence"}));
      this.variantNames.put(Items.oak_door, Lists.newArrayList(new String[]{"oak_door"}));
      Iterator var2 = customVariantNames.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<RegistryDelegate<Item>, Set<String>> entry = (Entry)var2.next();
         this.variantNames.put((Item)((RegistryDelegate)entry.getKey()).get(), Lists.newArrayList(((Set)entry.getValue()).iterator()));
      }

      CustomItems.update();
      CustomItems.loadModels(this);
   }

   private List<String> getVariantNames(Item p_177596_1_) {
      List<String> list = (List)this.variantNames.get(p_177596_1_);
      if (list == null) {
         list = Collections.singletonList(((ResourceLocation)Item.itemRegistry.getNameForObject(p_177596_1_)).toString());
      }

      return list;
   }

   private ResourceLocation getItemLocation(String p_177583_1_) {
      ResourceLocation resourcelocation = new ResourceLocation(p_177583_1_);
      if (Reflector.ForgeHooksClient.exists()) {
         resourcelocation = new ResourceLocation(p_177583_1_.replaceAll("#.*", ""));
      }

      return new ResourceLocation(resourcelocation.getResourceDomain(), "item/" + resourcelocation.getResourcePath());
   }

   private void bakeBlockModels() {
      Iterator var2 = this.variants.keySet().iterator();

      while(var2.hasNext()) {
         ModelResourceLocation modelresourcelocation = (ModelResourceLocation)var2.next();
         WeightedBakedModel.Builder weightedbakedmodel$builder = new WeightedBakedModel.Builder();
         int i = 0;
         Iterator var6 = ((ModelBlockDefinition.Variants)this.variants.get(modelresourcelocation)).getVariants().iterator();

         while(true) {
            while(var6.hasNext()) {
               ModelBlockDefinition.Variant modelblockdefinition$variant = (ModelBlockDefinition.Variant)var6.next();
               ModelBlock modelblock = (ModelBlock)this.models.get(modelblockdefinition$variant.getModelLocation());
               if (modelblock != null && modelblock.isResolved()) {
                  ++i;
                  weightedbakedmodel$builder.add(this.bakeModel(modelblock, modelblockdefinition$variant.getRotation(), modelblockdefinition$variant.isUvLocked()), modelblockdefinition$variant.getWeight());
               } else {
                  LOGGER.warn("Missing model for: " + modelresourcelocation);
               }
            }

            if (i == 0) {
               LOGGER.warn("No weighted models for: " + modelresourcelocation);
            } else if (i == 1) {
               this.bakedRegistry.putObject(modelresourcelocation, weightedbakedmodel$builder.first());
            } else {
               this.bakedRegistry.putObject(modelresourcelocation, weightedbakedmodel$builder.build());
            }
            break;
         }
      }

      var2 = this.itemLocations.entrySet().iterator();

      while(true) {
         while(var2.hasNext()) {
            Entry<String, ResourceLocation> entry = (Entry)var2.next();
            ResourceLocation resourcelocation = (ResourceLocation)entry.getValue();
            ModelResourceLocation modelresourcelocation1 = new ModelResourceLocation((String)entry.getKey(), "inventory");
            if (Reflector.ModelLoader_getInventoryVariant.exists()) {
               modelresourcelocation1 = (ModelResourceLocation)Reflector.call(Reflector.ModelLoader_getInventoryVariant, entry.getKey());
            }

            ModelBlock modelblock1 = (ModelBlock)this.models.get(resourcelocation);
            if (modelblock1 != null && modelblock1.isResolved()) {
               if (this.isCustomRenderer(modelblock1)) {
                  this.bakedRegistry.putObject(modelresourcelocation1, new BuiltInModel(modelblock1.getAllTransforms()));
               } else {
                  this.bakedRegistry.putObject(modelresourcelocation1, this.bakeModel(modelblock1, ModelRotation.X0_Y0, false));
               }
            } else {
               LOGGER.warn("Missing model for: " + resourcelocation);
            }
         }

         return;
      }
   }

   private Set<ResourceLocation> getVariantsTextureLocations() {
      Set<ResourceLocation> set = Sets.newHashSet();
      List<ModelResourceLocation> list = Lists.newArrayList(this.variants.keySet());
      Collections.sort(list, new Comparator<ModelResourceLocation>() {
         public int compare(ModelResourceLocation p_compare_1_, ModelResourceLocation p_compare_2_) {
            return p_compare_1_.toString().compareTo(p_compare_2_.toString());
         }
      });
      Iterator var4 = list.iterator();

      while(var4.hasNext()) {
         ModelResourceLocation modelresourcelocation = (ModelResourceLocation)var4.next();
         ModelBlockDefinition.Variants modelblockdefinition$variants = (ModelBlockDefinition.Variants)this.variants.get(modelresourcelocation);
         Iterator var7 = modelblockdefinition$variants.getVariants().iterator();

         while(var7.hasNext()) {
            ModelBlockDefinition.Variant modelblockdefinition$variant = (ModelBlockDefinition.Variant)var7.next();
            ModelBlock modelblock = (ModelBlock)this.models.get(modelblockdefinition$variant.getModelLocation());
            if (modelblock == null) {
               LOGGER.warn("Missing model for: " + modelresourcelocation);
            } else {
               set.addAll(this.getTextureLocations(modelblock));
            }
         }
      }

      set.addAll(LOCATIONS_BUILTIN_TEXTURES);
      return set;
   }

   public IBakedModel bakeModel(ModelBlock modelBlockIn, ModelRotation modelRotationIn, boolean uvLocked) {
      return this.bakeModel(modelBlockIn, (ITransformation)modelRotationIn, uvLocked);
   }

   protected IBakedModel bakeModel(ModelBlock p_bakeModel_1_, ITransformation p_bakeModel_2_, boolean p_bakeModel_3_) {
      TextureAtlasSprite textureatlassprite = (TextureAtlasSprite)this.sprites.get(new ResourceLocation(p_bakeModel_1_.resolveTextureName("particle")));
      SimpleBakedModel.Builder simplebakedmodel$builder = (new SimpleBakedModel.Builder(p_bakeModel_1_)).setTexture(textureatlassprite);
      Iterator var7 = p_bakeModel_1_.getElements().iterator();

      label30:
      while(var7.hasNext()) {
         BlockPart blockpart = (BlockPart)var7.next();
         Iterator var9 = blockpart.mapFaces.keySet().iterator();

         while(true) {
            while(true) {
               if (!var9.hasNext()) {
                  continue label30;
               }

               EnumFacing enumfacing = (EnumFacing)var9.next();
               BlockPartFace blockpartface = (BlockPartFace)blockpart.mapFaces.get(enumfacing);
               TextureAtlasSprite textureatlassprite1 = (TextureAtlasSprite)this.sprites.get(new ResourceLocation(p_bakeModel_1_.resolveTextureName(blockpartface.texture)));
               boolean flag = true;
               if (Reflector.ForgeHooksClient.exists()) {
                  flag = TRSRTransformation.isInteger(p_bakeModel_2_.getMatrix());
               }

               if (blockpartface.cullFace != null && flag) {
                  simplebakedmodel$builder.addFaceQuad(p_bakeModel_2_.rotate(blockpartface.cullFace), this.makeBakedQuad(blockpart, blockpartface, textureatlassprite1, enumfacing, p_bakeModel_2_, p_bakeModel_3_));
               } else {
                  simplebakedmodel$builder.addGeneralQuad(this.makeBakedQuad(blockpart, blockpartface, textureatlassprite1, enumfacing, p_bakeModel_2_, p_bakeModel_3_));
               }
            }
         }
      }

      return simplebakedmodel$builder.makeBakedModel();
   }

   private BakedQuad makeBakedQuad(BlockPart p_177589_1_, BlockPartFace p_177589_2_, TextureAtlasSprite p_177589_3_, EnumFacing p_177589_4_, ModelRotation p_177589_5_, boolean p_177589_6_) {
      return Reflector.ForgeHooksClient.exists() ? this.makeBakedQuad(p_177589_1_, p_177589_2_, p_177589_3_, p_177589_4_, p_177589_5_, p_177589_6_) : this.faceBakery.makeBakedQuad(p_177589_1_.positionFrom, p_177589_1_.positionTo, p_177589_2_, p_177589_3_, p_177589_4_, p_177589_5_, p_177589_1_.partRotation, p_177589_6_, p_177589_1_.shade);
   }

   protected BakedQuad makeBakedQuad(BlockPart p_makeBakedQuad_1_, BlockPartFace p_makeBakedQuad_2_, TextureAtlasSprite p_makeBakedQuad_3_, EnumFacing p_makeBakedQuad_4_, ITransformation p_makeBakedQuad_5_, boolean p_makeBakedQuad_6_) {
      return this.faceBakery.makeBakedQuad(p_makeBakedQuad_1_.positionFrom, p_makeBakedQuad_1_.positionTo, p_makeBakedQuad_2_, p_makeBakedQuad_3_, p_makeBakedQuad_4_, p_makeBakedQuad_5_, p_makeBakedQuad_1_.partRotation, p_makeBakedQuad_6_, p_makeBakedQuad_1_.shade);
   }

   private void loadModelsCheck() {
      this.loadModels();
      Iterator var2 = this.models.values().iterator();

      while(var2.hasNext()) {
         ModelBlock modelblock = (ModelBlock)var2.next();
         modelblock.getParentFromMap(this.models);
      }

      ModelBlock.checkModelHierarchy(this.models);
   }

   private void loadModels() {
      Deque<ResourceLocation> deque = Queues.newArrayDeque();
      Set<ResourceLocation> set = Sets.newHashSet();
      Iterator var4 = this.models.keySet().iterator();

      ResourceLocation resourcelocation2;
      ResourceLocation resourcelocation3;
      while(var4.hasNext()) {
         resourcelocation2 = (ResourceLocation)var4.next();
         set.add(resourcelocation2);
         resourcelocation3 = ((ModelBlock)this.models.get(resourcelocation2)).getParentLocation();
         if (resourcelocation3 != null) {
            deque.add(resourcelocation3);
         }
      }

      while(!deque.isEmpty()) {
         resourcelocation2 = (ResourceLocation)deque.pop();

         try {
            if (this.models.get(resourcelocation2) != null) {
               continue;
            }

            ModelBlock modelblock = this.loadModel(resourcelocation2);
            this.models.put(resourcelocation2, modelblock);
            resourcelocation3 = modelblock.getParentLocation();
            if (resourcelocation3 != null && !set.contains(resourcelocation3)) {
               deque.add(resourcelocation3);
            }
         } catch (Exception var6) {
            LOGGER.warn("In parent chain: " + JOINER.join(this.getParentPath(resourcelocation2)) + "; unable to load model: '" + resourcelocation2 + "'");
         }

         set.add(resourcelocation2);
      }

   }

   private List<ResourceLocation> getParentPath(ResourceLocation p_177573_1_) {
      List<ResourceLocation> list = Lists.newArrayList(new ResourceLocation[]{p_177573_1_});
      ResourceLocation resourcelocation = p_177573_1_;

      while((resourcelocation = this.getParentLocation(resourcelocation)) != null) {
         list.add(0, resourcelocation);
      }

      return list;
   }

   private ResourceLocation getParentLocation(ResourceLocation p_177576_1_) {
      Iterator var3 = this.models.entrySet().iterator();

      Entry entry;
      ModelBlock modelblock;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         entry = (Entry)var3.next();
         modelblock = (ModelBlock)entry.getValue();
      } while(modelblock == null || !p_177576_1_.equals(modelblock.getParentLocation()));

      return (ResourceLocation)entry.getKey();
   }

   private Set<ResourceLocation> getTextureLocations(ModelBlock p_177585_1_) {
      Set<ResourceLocation> set = Sets.newHashSet();
      Iterator var4 = p_177585_1_.getElements().iterator();

      while(var4.hasNext()) {
         BlockPart blockpart = (BlockPart)var4.next();
         Iterator var6 = blockpart.mapFaces.values().iterator();

         while(var6.hasNext()) {
            BlockPartFace blockpartface = (BlockPartFace)var6.next();
            ResourceLocation resourcelocation = new ResourceLocation(p_177585_1_.resolveTextureName(blockpartface.texture));
            set.add(resourcelocation);
         }
      }

      set.add(new ResourceLocation(p_177585_1_.resolveTextureName("particle")));
      return set;
   }

   private void loadSprites() {
      final Set<ResourceLocation> set = this.getVariantsTextureLocations();
      set.addAll(this.getItemsTextureLocations());
      set.remove(TextureMap.LOCATION_MISSING_TEXTURE);
      IIconCreator iiconcreator = new IIconCreator() {
         public void registerSprites(TextureMap iconRegistry) {
            Iterator var3 = set.iterator();

            while(var3.hasNext()) {
               ResourceLocation resourcelocation = (ResourceLocation)var3.next();
               TextureAtlasSprite textureatlassprite = iconRegistry.registerSprite(resourcelocation);
               ModelBakery.this.sprites.put(resourcelocation, textureatlassprite);
            }

         }
      };
      this.textureMap.loadSprites(this.resourceManager, iiconcreator);
      this.sprites.put(new ResourceLocation("missingno"), this.textureMap.getMissingSprite());
   }

   private Set<ResourceLocation> getItemsTextureLocations() {
      Set<ResourceLocation> set = Sets.newHashSet();
      Iterator var3 = this.itemLocations.values().iterator();

      while(true) {
         while(true) {
            ModelBlock modelblock;
            do {
               if (!var3.hasNext()) {
                  return set;
               }

               ResourceLocation resourcelocation = (ResourceLocation)var3.next();
               modelblock = (ModelBlock)this.models.get(resourcelocation);
            } while(modelblock == null);

            set.add(new ResourceLocation(modelblock.resolveTextureName("particle")));
            Iterator var6;
            ResourceLocation resourcelocation2;
            if (this.hasItemModel(modelblock)) {
               for(var6 = ItemModelGenerator.LAYERS.iterator(); var6.hasNext(); set.add(resourcelocation2)) {
                  String s = (String)var6.next();
                  resourcelocation2 = new ResourceLocation(modelblock.resolveTextureName(s));
                  if (modelblock.getRootModel() == MODEL_COMPASS && !TextureMap.LOCATION_MISSING_TEXTURE.equals(resourcelocation2)) {
                     TextureAtlasSprite.setLocationNameCompass(resourcelocation2.toString());
                  } else if (modelblock.getRootModel() == MODEL_CLOCK && !TextureMap.LOCATION_MISSING_TEXTURE.equals(resourcelocation2)) {
                     TextureAtlasSprite.setLocationNameClock(resourcelocation2.toString());
                  }
               }
            } else if (!this.isCustomRenderer(modelblock)) {
               var6 = modelblock.getElements().iterator();

               while(var6.hasNext()) {
                  BlockPart blockpart = (BlockPart)var6.next();
                  Iterator var8 = blockpart.mapFaces.values().iterator();

                  while(var8.hasNext()) {
                     BlockPartFace blockpartface = (BlockPartFace)var8.next();
                     ResourceLocation resourcelocation1 = new ResourceLocation(modelblock.resolveTextureName(blockpartface.texture));
                     set.add(resourcelocation1);
                  }
               }
            }
         }
      }
   }

   private boolean hasItemModel(ModelBlock p_177581_1_) {
      if (p_177581_1_ == null) {
         return false;
      } else {
         ModelBlock modelblock = p_177581_1_.getRootModel();
         return modelblock == MODEL_GENERATED || modelblock == MODEL_COMPASS || modelblock == MODEL_CLOCK;
      }
   }

   private boolean isCustomRenderer(ModelBlock p_177587_1_) {
      if (p_177587_1_ == null) {
         return false;
      } else {
         ModelBlock modelblock = p_177587_1_.getRootModel();
         return modelblock == MODEL_ENTITY;
      }
   }

   private void bakeItemModels() {
      Iterator var2 = this.itemLocations.values().iterator();

      while(var2.hasNext()) {
         ResourceLocation resourcelocation = (ResourceLocation)var2.next();
         ModelBlock modelblock = (ModelBlock)this.models.get(resourcelocation);
         if (this.hasItemModel(modelblock)) {
            ModelBlock modelblock1 = this.makeItemModel(modelblock);
            if (modelblock1 != null) {
               modelblock1.name = resourcelocation.toString();
            }

            this.models.put(resourcelocation, modelblock1);
         } else if (this.isCustomRenderer(modelblock)) {
            this.models.put(resourcelocation, modelblock);
         }
      }

      var2 = this.sprites.values().iterator();

      while(var2.hasNext()) {
         TextureAtlasSprite textureatlassprite = (TextureAtlasSprite)var2.next();
         if (!textureatlassprite.hasAnimationMetadata()) {
            textureatlassprite.clearFramesTextureData();
         }
      }

   }

   private ModelBlock makeItemModel(ModelBlock p_177582_1_) {
      return this.itemModelGenerator.makeItemModel(this.textureMap, p_177582_1_);
   }

   public ModelBlock getModelBlock(ResourceLocation p_getModelBlock_1_) {
      ModelBlock modelblock = (ModelBlock)this.models.get(p_getModelBlock_1_);
      return modelblock;
   }

   public static void fixModelLocations(ModelBlock p_fixModelLocations_0_, String p_fixModelLocations_1_) {
      ResourceLocation resourcelocation = fixModelLocation(p_fixModelLocations_0_.getParentLocation(), p_fixModelLocations_1_);
      if (resourcelocation != p_fixModelLocations_0_.getParentLocation()) {
         Reflector.setFieldValue(p_fixModelLocations_0_, Reflector.ModelBlock_parentLocation, resourcelocation);
      }

      Map<String, String> map = (Map)Reflector.getFieldValue(p_fixModelLocations_0_, Reflector.ModelBlock_textures);
      if (map != null) {
         Iterator var5 = map.entrySet().iterator();

         while(var5.hasNext()) {
            Entry<String, String> entry = (Entry)var5.next();
            String s = (String)entry.getValue();
            String s1 = fixResourcePath(s, p_fixModelLocations_1_);
            if (s1 != s) {
               entry.setValue(s1);
            }
         }
      }

   }

   public static ResourceLocation fixModelLocation(ResourceLocation p_fixModelLocation_0_, String p_fixModelLocation_1_) {
      if (p_fixModelLocation_0_ != null && p_fixModelLocation_1_ != null) {
         if (!p_fixModelLocation_0_.getResourceDomain().equals("minecraft")) {
            return p_fixModelLocation_0_;
         } else {
            String s = p_fixModelLocation_0_.getResourcePath();
            String s1 = fixResourcePath(s, p_fixModelLocation_1_);
            if (s1 != s) {
               p_fixModelLocation_0_ = new ResourceLocation(p_fixModelLocation_0_.getResourceDomain(), s1);
            }

            return p_fixModelLocation_0_;
         }
      } else {
         return p_fixModelLocation_0_;
      }
   }

   private static String fixResourcePath(String p_fixResourcePath_0_, String p_fixResourcePath_1_) {
      p_fixResourcePath_0_ = TextureUtils.fixResourcePath(p_fixResourcePath_0_, p_fixResourcePath_1_);
      p_fixResourcePath_0_ = StrUtils.removeSuffix(p_fixResourcePath_0_, ".json");
      p_fixResourcePath_0_ = StrUtils.removeSuffix(p_fixResourcePath_0_, ".png");
      return p_fixResourcePath_0_;
   }

   /** @deprecated */
   @Deprecated
   public static void addVariantName(Item p_addVariantName_0_, String... p_addVariantName_1_) {
      RegistryDelegate registrydelegate = (RegistryDelegate)Reflector.getFieldValue(p_addVariantName_0_, Reflector.ForgeItem_delegate);
      if (customVariantNames.containsKey(registrydelegate)) {
         ((Set)customVariantNames.get(registrydelegate)).addAll(Lists.newArrayList(p_addVariantName_1_));
      } else {
         customVariantNames.put(registrydelegate, Sets.newHashSet(p_addVariantName_1_));
      }

   }

   public static <T extends ResourceLocation> void registerItemVariants(Item p_registerItemVariants_0_, T... p_registerItemVariants_1_) {
      RegistryDelegate registrydelegate = (RegistryDelegate)Reflector.getFieldValue(p_registerItemVariants_0_, Reflector.ForgeItem_delegate);
      if (!customVariantNames.containsKey(registrydelegate)) {
         customVariantNames.put(registrydelegate, Sets.newHashSet());
      }

      ResourceLocation[] var6 = p_registerItemVariants_1_;
      int var5 = p_registerItemVariants_1_.length;

      for(int var4 = 0; var4 < var5; ++var4) {
         ResourceLocation resourcelocation = var6[var4];
         ((Set)customVariantNames.get(registrydelegate)).add(resourcelocation.toString());
      }

   }
}
