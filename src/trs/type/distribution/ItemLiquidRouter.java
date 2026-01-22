package trs.type.distribution;

import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.Tmp;
import mindustry.content.Blocks;
import mindustry.content.UnitTypes;
import mindustry.gen.BlockUnitc;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import mindustry.world.blocks.ControlBlock;
import mindustry.world.blocks.distribution.Router;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.meta.BlockGroup;

public class ItemLiquidRouter extends LiquidRouter {
    public float speed = 8f;

    public TextureRegion bottomRegion;
    public TextureRegion bottomCenterRegion;
    public TextureRegion borderRegion;
    public TextureRegion liquidRegion;
    public TextureRegion liquidCenterRegion;

    public ItemLiquidRouter(String name) {
        super(name);
        solid = false;
        underBullets = true;
        update = true;
        hasItems = true;
        itemCapacity = 1;
        group = BlockGroup.transportation;
        unloadable = false;
        noUpdateDisabled = true;
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{Core.atlas.find(name+"-icon")};
    }

    @Override
    public void load() {
        super.load();
        bottomCenterRegion = Core.atlas.find(name + "-bottom-center");
        bottomRegion = Core.atlas.find(name + "-bottom");
        borderRegion = Core.atlas.find(name + "-border");
        liquidRegion = Core.atlas.find(name + "-liquid");
        liquidCenterRegion = Core.atlas.find(name + "-liquid-center");
    }
    
    public static void drawTiledFrames(int size, float x, float y, float padLeft, float padRight, float padTop,
            float padBottom, Liquid liquid, float alpha) {
        TextureRegion region = renderer.fluidFrames[liquid.gas ? 1 : 0][liquid.getAnimationFrame()];
        TextureRegion toDraw = Tmp.tr1;

        float leftBounds = size / 2f * tilesize - padRight;
        float bottomBounds = size / 2f * tilesize - padTop;
        Color color = Tmp.c1.set(liquid.color).a(1f);

        for (int sx = 0; sx < size; sx++) {
            for (int sy = 0; sy < size; sy++) {
                float relx = sx - (size - 1) / 2f, rely = sy - (size - 1) / 2f;

                toDraw.set(region);

                //truncate region if at border
                float rightBorder = relx * tilesize + padLeft, topBorder = rely * tilesize + padBottom;
                float squishX = rightBorder + tilesize / 2f - leftBounds,
                        squishY = topBorder + tilesize / 2f - bottomBounds;
                float ox = 0f, oy = 0f;

                if (squishX >= 8 || squishY >= 8)
                    continue;

                //cut out the parts that don't fit inside the padding
                if (squishX > 0) {
                    toDraw.setWidth(toDraw.width - squishX * 4f);
                    ox = -squishX / 2f;
                }

                if (squishY > 0) {
                    toDraw.setY(toDraw.getY() + squishY * 4f);
                    oy = -squishY / 2f;
                }

                Drawf.liquid(toDraw, x + rightBorder + ox, y + topBorder + oy, alpha, color);
            }
        }
    }
    
    public static void drawLiquidFramedBottom(
            TextureRegion bottomRegion,
            float x, float y,
            Liquid liquid,
            float alpha) {
        if (liquid == null || bottomRegion == null)
            return;
        if (alpha <= 0.001f)
            return;

        Color color = Tmp.c1.set(liquid.color);

        Draw.draw(Draw.z(), () -> {
            Draw.color(color);
            Draw.alpha(alpha);

            // рисуем маску жидкости
            Draw.rect(bottomRegion, x, y);

            Draw.reset();
        });
    }

    public class ItemLiquidRouterBuild extends LiquidRouterBuild implements ControlBlock {
        public Item lastItem;
        public Tile lastInput;
        public float time;
        public @Nullable BlockUnitc unit;

        @Override
        public Unit unit(){
            if(unit == null){
                unit = (BlockUnitc) UnitTypes.block.create(team);
                unit.tile(this);
            }
            return (Unit)unit;
        }

        @Override
        public boolean canControl(){
            return size == 1;
        }

        @Override
        public boolean shouldAutoTarget(){
            return false;
        }

        @Override
        public void updateTile(){
            dumpLiquid(liquids.current());
            if(lastItem == null && items.any()){
                lastItem = items.first();
            }

            if(lastItem != null){
                time += 1f / speed * delta();
                Building target = getTileTarget(lastItem, lastInput, false);

                if(target != null && (time >= 1f || !(target.block instanceof Router || target.block.instantTransfer))){
                    getTileTarget(lastItem, lastInput, true);
                    target.handleItem(this, lastItem);
                    items.remove(lastItem, 1);
                    lastItem = null;
                }
            }
        }
        @Override
        public void draw(){
            Building l = left(), r = right(), f = front(), b = back();
            Draw.rect(bottomCenterRegion, x, y);
            if (liquids.currentAmount() > 0.001f) {
                 drawLiquidFramedBottom(liquidCenterRegion,x, y,liquids.current(),liquids.currentAmount() / liquidCapacity);
            }
            //if(liquids.currentAmount() > 0.001f) drawTiledFrames(size,x,y,2,2,2,2,liquids.current(),100);
            if (l != null){
                Draw.rect(bottomRegion, x, y, rotdeg()+90);
                if(liquids.currentAmount() > 0.001f) Drawf.liquid(liquidRegion,x, y, liquids.currentAmount(), liquids.current().color.write(Tmp.c1).a(255f),rotdeg()+90);
                Draw.rect(borderRegion,x,y,rotdeg()+90);
            }
            if (r != null){
                Draw.rect(bottomRegion, x, y, rotdeg()-90);
                if(liquids.currentAmount() > 0.001f) Drawf.liquid(liquidRegion,x, y, liquids.currentAmount(), liquids.current().color.write(Tmp.c1).a(255f),rotdeg()-90);
                Draw.rect(borderRegion,x,y,rotdeg()-90);
            }
            if (f != null){
                Draw.rect(bottomRegion, x, y, rotdeg());
                if(liquids.currentAmount() > 0.001f) Drawf.liquid(liquidRegion,x, y, liquids.currentAmount(), liquids.current().color.write(Tmp.c1).a(255f),rotdeg());
                Draw.rect(borderRegion,x,y, rotdeg());
            }
            if (b != null){
                Draw.rect(bottomRegion, x, y, rotdeg()-180);
                if(liquids.currentAmount() > 0.001f) Drawf.liquid(liquidRegion,x, y, liquids.currentAmount(), liquids.current().color.write(Tmp.c1).a(255f),rotdeg()-180);
                Draw.rect(borderRegion,x,y,rotdeg()-180);

            }
            Draw.rect(region,x,y);
        }

        @Override
        public int acceptStack(Item item, int amount, Teamc source){
            return 0;
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return team == source.team && lastItem == null && items.total() == 0;
        }

        @Override
        public void handleItem(Building source, Item item){
            items.add(item, 1);
            lastItem = item;
            time = 0f;
            lastInput = source.tileOn();
        }

        @Override
        public int removeStack(Item item, int amount){
            int result = super.removeStack(item, amount);
            if(result != 0 && item == lastItem){
                lastItem = null;
            }
            return result;
        }

        public Building getTileTarget(Item item, Tile from, boolean set){
            if(unit != null && isControlled()){
                unit.health(health);
                unit.ammo(unit.type().ammoCapacity * (items.total() > 0 ? 1f : 0f));
                unit.team(team);
                unit.set(x, y);

                int angle = Mathf.mod((int)((angleTo(unit.aimX(), unit.aimY()) + 45) / 90), 4);

                if(unit.isShooting()){
                    Building other = nearby(rotation = angle);
                    if(other != null && other.acceptItem(this, item)){
                        return other;
                    }
                }

                return null;
            }

            int counter = rotation;
            for(int i = 0; i < proximity.size; i++){
                Building other = proximity.get((i + counter) % proximity.size);
                if(set) rotation = ((byte)((rotation + 1) % proximity.size));
                if(other.tile == from && from.block() == Blocks.overflowGate) continue;
                if(other.acceptItem(this, item)){
                    return other;
                }
            }
            return null;
        }
    }
}
