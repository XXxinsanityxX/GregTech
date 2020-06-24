package gregtech.api.unification.material.type;

import com.google.common.collect.ImmutableList;
import gregtech.api.unification.Element;
import gregtech.api.unification.material.MaterialIconSet;

public class BasicMaterial extends Material {
    public BasicMaterial(int subId, String name, int color, MaterialIconSet iconSet) {
        super(subId, name, color, iconSet, ImmutableList.of(), 0L, (Element) null);
    }
}
