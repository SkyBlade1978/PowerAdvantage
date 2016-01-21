package cyano.poweradvantage.init;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.events.BucketHandler;
import cyano.poweradvantage.items.RotationTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public abstract class Items {

	public static Item starch;
	public static Item bioplastic_ingot;
	public static Item sprocket;
	public static Item rotator_tool;

	public static ItemBucket bucket_crude_oil;
	public static ItemBucket bucket_refined_oil;
	public static final Map<String,Item> allItems = new HashMap<>();
	
	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		Blocks.init();
		Fluids.init();
		
		starch = addItem("starch", new Item(),"starch");
		bioplastic_ingot = addItem("bioplastic_ingot", new Item(),"plastic","ingotPlastic");
		if(PowerAdvantage.plasticIsAlsoRubber){
			OreDictionary.registerOre("rubber", bioplastic_ingot);
			OreDictionary.registerOre("ingotRubber", bioplastic_ingot);
		}
		sprocket = addItem("sprocket", new Item(),"sprocket","gear","sprocketSteel","gearSteel");
		rotator_tool = addItem("rotator_tool", new RotationTool());
		
		
		bucket_crude_oil = (ItemBucket)addItem("bucket_crude_oil",new ItemBucket(Blocks.crude_oil_block),"bucketOil","bucketCrudeOil");
		FluidContainerRegistry.registerFluidContainer(Fluids.crude_oil, new ItemStack(bucket_crude_oil), new ItemStack(net.minecraft.init.Items.bucket));
		BucketHandler.getInstance().buckets.put(Blocks.crude_oil_block, bucket_crude_oil);
		
		bucket_refined_oil = (ItemBucket)addItem("bucket_refined_oil",new ItemBucket(Blocks.refined_oil_block),"bucketOil","bucketRefinedOil");
		FluidContainerRegistry.registerFluidContainer(Fluids.refined_oil, new ItemStack(bucket_refined_oil), new ItemStack(net.minecraft.init.Items.bucket));
		BucketHandler.getInstance().buckets.put(Blocks.refined_oil_block, bucket_refined_oil);
		
		initDone = true;
	}

	private static Item addItem(String unlocalizedName, Item i,String... oreDictNames){
		Item n = addItem(unlocalizedName,i);
		for(String oreDictName : oreDictNames){
			OreDictionary.registerOre(oreDictName, n);
		}
		return n;
	}
	private static Item addItem(String unlocalizedName, Item i){
		i.setUnlocalizedName(PowerAdvantage.MODID+"."+unlocalizedName);
		GameRegistry.registerItem(i, unlocalizedName);
		i.setCreativeTab(ItemGroups.tab_powerAdvantage);
		allItems.put(unlocalizedName, i);
		return i;
	}
	
	public static Item getItemByName(String unlocalizedName){
		return allItems.get(unlocalizedName);
	}
	
	public static Set<Map.Entry<String, Item>> getAllRegisteredItems(){
		return allItems.entrySet();
	}

	@SideOnly(Side.CLIENT)
	public static void registerItemRenders(FMLInitializationEvent event) {
		for(Map.Entry<String, Item> e :  getAllRegisteredItems()){
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
			.register(e.getValue(), 0, 
					new ModelResourceLocation(PowerAdvantage.MODID+":"+e.getKey(), "inventory"));
		}
	}
	
}
