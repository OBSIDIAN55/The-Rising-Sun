package trs.type.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.type.Liquid;
import mindustry.world.blocks.distribution.OverflowGate;
import mindustry.world.meta.BlockGroup;

public class ItemLiquidOverflowGate extends OverflowGate {

    public TextureRegion bottomRegion;
    public TextureRegion borderRegion;
    public TextureRegion liquidRegion;

    public ItemLiquidOverflowGate(String name) {
        super(name);
        hasItems = true;
        hasLiquids = true;
        underBullets = true;
        update = true;
        destructible = true;
        group = BlockGroup.transportation;
        instantTransfer = true;
        unloadable = false;
        solid = true;
        noUpdateDisabled = true;
        canOverdrive = false;
        floating = true;
        itemCapacity = 0;
    }
    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{Core.atlas.find(name+"-icon")};
    }
    @Override
    public void load(){
        super.load();
        bottomRegion = Core.atlas.find(name+"-bottom");
        borderRegion = Core.atlas.find(name+"-border");
        liquidRegion = Core.atlas.find(name+"-liquid");
    }

    public class ItemLiquidOverflowGateBuild extends OverflowGateBuild {

        @Override
        public void updateTile(){
            dumpLiquid(liquids.current());
        }
        @Override
        public void draw(){
            Building l = left(), r = right(), f = front(), b = back();
            if (l != null){
                Draw.rect(bottomRegion, x, y, rotdeg()+90);
                if(liquids.currentAmount() > 0.001f) Drawf.liquid(liquidRegion,x, y, liquids.currentAmount(), liquids.current().color.write(Tmp.c1).a(1f),
                            rotdeg()+90);
                Draw.rect(borderRegion,x,y, rotdeg()+90);
            }
            if (r != null){
                Draw.rect(bottomRegion, x, y, rotdeg()-90);
                if(liquids.currentAmount() > 0.001f) Drawf.liquid(liquidRegion,x, y, liquids.currentAmount(), liquids.current().color.write(Tmp.c1).a(1f),
                            rotdeg()-90);
                Draw.rect(borderRegion,x,y, rotdeg()-90);
            }
            if (f != null){
                Draw.rect(bottomRegion, x, y, rotdeg());
                if(liquids.currentAmount() > 0.001f) Drawf.liquid(liquidRegion,x, y, liquids.currentAmount(), liquids.current().color.write(Tmp.c1).a(1f),
                            rotdeg());
                Draw.rect(borderRegion,x,y, rotdeg());
            }
            if (b != null){
                Draw.rect(bottomRegion, x, y, rotdeg()-180);
                if(liquids.currentAmount() > 0.001f) Drawf.liquid(liquidRegion,x, y, liquids.currentAmount(), liquids.current().color.write(Tmp.c1).a(1f),
                            rotdeg()-180);
                Draw.rect(borderRegion,x,y, rotdeg()-180);

            }
            Draw.rect(region,x,y);
        }
        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            return (liquids.current() == liquid || liquids.currentAmount() < 0.2f);
        }
    }
}
