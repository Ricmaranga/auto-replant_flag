package it.pose.autoreplant;

import org.bukkit.Material;

public class CropsLoader {


    public void LoadCrops() {

        CropsListener.ALLOWED_CROPS.clear();

        if (AutoReplant.cropsStrings == null || AutoReplant.cropsStrings.isEmpty()) {
            AutoReplant.getInstance().getLogger().warning("Crops list is empty or uninitialized");
            return;
        }

        for (String crop : AutoReplant.cropsStrings){

            String normalized = crop.trim().replace(" ", "_").toUpperCase();
            Material cropMaterial = Material.matchMaterial(normalized);

            if (cropMaterial != null){
                CropsListener.ALLOWED_CROPS.add(cropMaterial);
            } else {
                AutoReplant.getInstance().getLogger().warning(crop + " is not a valid crop");
            }
        }
    }
}
