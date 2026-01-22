package trs;

import mindustry.mod.Mod;
import trs.content.*;
import trs.type.TrsBulletTypes;
import trs.type.Vars;
import trs.ui.MapEventButtons;
import trs.ui.TechTreeItemsInterceptor;

public class trsMod extends Mod {
    public trsMod() {
    }

    public static boolean debug;

    @Override
    public void init() {
        MapEventButtons.init();
        TechTreeItemsInterceptor.init();
    }

    public void loadContent() {
        debug = false;
        Vars.load();
        Sounds.load();
        trsStatusEffects.load();
        trsLiquids.load();
        trsItems.load();
        trsWeathers.load();
        TrsBulletTypes.load();
        trsUnits.load();
        trsEnv.load();
        trsBlocks.load();
        Planets.load();
        trsSectorPresets.load();
        FulgeraTechTree.load();
    }
}
