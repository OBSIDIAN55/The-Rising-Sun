package trs.type.cores;

import static mindustry.Vars.*;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.TargetPriority;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Teams;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.io.TypeIO;
import mindustry.logic.Ranged;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;
import mindustry.world.blocks.ControlBlock;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.*;
import mindustry.world.meta.BlockFlag;

public class BuildTurretRegenGeneratorCoreBlock extends CoreBlock{

    public static Building BuildTurretRegenGeneratorCoreBlockBuild;
    public final int timerTarget = timers++, timerTarget2 = timers++;
    public int targetInterval = 15;

    public boolean isRegen = true;
    public boolean isGenerator = true;
    public float powerProduction = 10f;
    public Stat generationType = Stat.basePowerGeneration;
    public DrawBlock drawer = new DrawDefault();

    public final int timerUse = timers++;
    public Color baseColor = Color.valueOf("84f491");
    public float reload = 250f;
    public static float range = 60f;
    public float healPercent = 12f;
    public float phaseBoost = 12f;
    public float phaseRangeBoost = 50f;
    public float useTime = 400f;

    public float buildRange = 60*tilesize;
    public float placeOverlapMargin = 8 * 7f;
    public float rotateSpeed = 5;
    public float fogRadiusMultiplier = 1f;

    public float buildSpeed = 1f;
    public float buildBeamOffset = 5f;
    //created in init()
    public UnitType unitType;
    public float elevation = -1f;

    public TextureRegion turretRegion;


    public BuildTurretRegenGeneratorCoreBlock(String name) {
        super(name);
        consumesPower = false;
        outputsPower = true;
        hasPower = true;

        solid = true;
        update = true;
        group = BlockGroup.power;
        hasItems = true;
        emitLight = true;
        lightRadius = 50f;
        suppressable = true;
        envEnabled |= Env.space;
        outlineIcon = true;
        attacks = true;
        priority = TargetPriority.core;

        sync = false;
        rotateSpeed = 10f;
    }

    public static void playerSpawn(Tile tile, Player player){
        if(player == null || tile == null || !(tile.build instanceof CoreBuild)) return;
    }

    @Override
    public void init(){
        placeOverlapRange = Math.max(placeOverlapRange, range + placeOverlapMargin);
        fogRadius = Math.max(Mathf.round(range / tilesize * fogRadiusMultiplier), fogRadius);
        lightRadius = 30f + 20f * size;
        fogRadius = Math.max(fogRadius, (int)(lightRadius / 8f * 3f) + 13);
        emitLight = true;

        super.init();
        if(elevation < 0) elevation = size / 2f;

        unitType = new UnitType("turret-unit-" + name){{
            hidden = true;
            internal = true;
            speed = 0f;
            hitSize = 0f;
            health = 1;
            itemCapacity = 5;
            mineFloor = true;
            mineRange = range;
            mineSpeed = 100f;

            rotateSpeed = BuildTurretRegenGeneratorCoreBlock.this.rotateSpeed;
            buildBeamOffset = BuildTurretRegenGeneratorCoreBlock.this.buildBeamOffset;
            buildRange = BuildTurretRegenGeneratorCoreBlock.this.buildRange;
            buildSpeed = BuildTurretRegenGeneratorCoreBlock.this.buildSpeed;
            constructor = BlockUnitUnit::create;
        }};
    }

    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }

    @Override
    public void load(){
        super.load();
        turretRegion = Core.atlas.find(name+"-turret");
        drawer.load(this);
    }

    @Override
    public void setStats(){
        stats.timePeriod = useTime;
        super.setStats();
        if(isGenerator) {
            stats.add(generationType, powerProduction * 60.0f, StatUnit.powerSecond);
        }
        if(isRegen) {
            stats.add(Stat.repairTime, (int) (100f / healPercent * reload / 60f), StatUnit.seconds);
            stats.add(trs.type.Vars.healRadius, range * 2 / tilesize, StatUnit.blocks);
        }

        if(findConsumer(c -> c instanceof ConsumeItems) instanceof ConsumeItems cons){
            stats.remove(Stat.booster);
            stats.add(Stat.booster, StatValues.itemBoosters(
                            "{0}" + StatUnit.timesSpeed.localized(),
                            stats.timePeriod, (phaseBoost + healPercent) / healPercent, phaseRangeBoost,
                            cons.items)
            );
        }
        stats.add(trs.type.Vars.buildRadius, buildRange / tilesize, StatUnit.blocks);
        stats.addPercent(Stat.buildSpeed, buildSpeed);
        stats.remove(Stat.unitType);
    }

    @Override
    public void setBars(){
        super.setBars();
        if(isGenerator){
            if(hasPower && outputsPower){
                addBar("power", (BuildTurretRegenGeneratorCoreBlockBuild entity) -> new Bar(() ->
                    Core.bundle.format("bar.poweroutput",
                            Strings.fixed(entity.getPowerProduction() * 60 * entity.timeScale(), 1)),
                    () -> Pal.powerBar,
                    () -> entity.productionEfficiency));
            }
        }
    }
    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        if(world.tile(x, y) == null) return;

        if(!canPlaceOn(world.tile(x, y), player.team(), rotation)){

            drawPlaceText(Core.bundle.get(
                    isFirstTier ?
                            //TODO better message
                            "bar.corefloor" :
                            (player.team().core() != null && player.team().core().items.has(requirements, state.rules.buildCostMultiplier)) || state.rules.infiniteResources ?
                                    "bar.corereq" :
                                    "bar.noresources"
            ), x, y, valid);
        }

        if(isRegen) {
            Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range * 2, baseColor);
            indexer.eachBlock(player.team(), x * tilesize + offset, y * tilesize + offset, range*2, other -> true, other -> Drawf.selected(other, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f))));
        }
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, buildRange, Pal.placing);

        if(fogRadiusMultiplier < 0.99f && state.rules.fog){
            Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, buildRange * fogRadiusMultiplier, Pal.lightishGray);
        }


    }


    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        drawer.drawPlan(this, plan, list);
    }

    public class BuildTurretRegenGeneratorCoreBlockBuild extends CoreBuild implements Ranged, ControlBlock {

        public BlockUnitc unit = (BlockUnitc) unitType.create(team);
        public @Nullable Unit following;
        public @Nullable Teams.BlockPlan lastPlan;
        public float warmup;

        {
            unit.rotation(90f);
        }

        @Override
        public boolean owns(Building tile){
            return owns(this, tile);
        }

        @Override
        public boolean owns(Building core, Building tile){
            // ядро «владеет» только реальными хранилищами, чтобы последующий каст к StorageBuild был корректным
            return tile instanceof StorageBlock.StorageBuild sb && sb.block.flags.contains(BlockFlag.storage);
        }

        @Override
        public void onControlSelect(Unit unit) {
            if (!unit.isPlayer()) return;
            Player player = unit.getPlayer();

            Fx.spawn.at(player);
            if (net.client() && player == Vars.player) {
                control.input.controlledType = null;
            }

            player.clearUnit();
            player.deathTimer = Player.deathDelay + 1f;
            requestSpawn(player);
        }

        public void requestSpawn(Player player) {
            //do not try to respawn in unsupported environments at all
            if (!unitType.supportsEnv(state.rules.env)) return;

            playerSpawn(tile, player);
        }

        public Unit unit() {
            //make sure stats are correct
            unit.tile(this);
            unit.team(team);
            return (Unit) unit;
        }

        public float rotation = 90;

        public float charge = Mathf.random(reload);
        public float productionEfficiency = 1f;

        public float buildRange() {
            return buildRange;
        }

        @Override
        public void updateTile() {
            unit.tile(this);
            unit.team(team);

            //only cares about where the unit itself is looking
            rotation = unit.rotation();

            if (unit.activelyBuilding()) {
                unit.lookAt(angleTo(unit.buildPlan()));
            }

            if (checkSuppression()) {
                efficiency = potentialEfficiency = 0f;
            }

            unit.buildSpeedMultiplier(potentialEfficiency * timeScale);
            unit.speedMultiplier(potentialEfficiency * timeScale);

            warmup = Mathf.lerpDelta(warmup, unit.activelyBuilding() ? efficiency : 0f, 0.1f);


            if (!isControlled()) {
                unit.updateBuilding(true);

                if (following != null) {
                    //validate follower
                    if (!following.isValid() || !following.activelyBuilding()) {
                        following = null;
                        unit.plans().clear();
                    } else {
                        //set to follower's first build plan, whatever that is
                        unit.plans().clear();
                        unit.plans().addFirst(following.buildPlan());
                        lastPlan = null;
                    }

                } else if (unit.buildPlan() != null) { //validate building
                    BuildPlan req = unit.buildPlan();

                    //clear break plan if another player is breaking something
                    if (!req.breaking && timer.get(timerTarget2, 30f)) {
                        for (Player player : team.data().players) {
                            if (player.isBuilder() && player.unit().activelyBuilding() && player.unit().buildPlan().samePos(req) && player.unit().buildPlan().breaking) {
                                unit.plans().removeFirst();
                                //remove from list of plans
                                team.data().plans.remove(p -> p.x == req.x && p.y == req.y);
                                return;
                            }
                        }
                    }

                    boolean valid =
                            !(lastPlan != null && lastPlan.removed) &&
                                    ((req.tile() != null && req.tile().build instanceof ConstructBlock.ConstructBuild cons && cons.current == req.block) ||
                                            (req.breaking ?
                                                    Build.validBreak(unit.team(), req.x, req.y) :
                                                    Build.validPlace(req.block, unit.team(), req.x, req.y, req.rotation)));

                    if (!valid) {
                        //discard invalid request
                        unit.plans().removeFirst();
                        lastPlan = null;
                    }
                }
            } else { //is being controlled, forget everything
                following = null;
                lastPlan = null;
            }

            //please do not commit suicide
            unit.plans().remove(b -> b.build() == this);

            unit.updateBuildLogic();

            iframes -= Time.delta;
            thrusterTime -= Time.delta / 90f;

            if (isRegen) {
            boolean canHeal = !checkSuppression();

            charge += delta();

                if (canHeal && charge >= reload) {
                    float realRange = range * 2;
                    charge = 0f;

                    indexer.eachBlock(this, realRange, b -> b.damaged() && !b.isHealSuppressed(), other -> {
                        other.heal(other.maxHealth() * healPercent / 100f);
                        other.recentlyHealed();
                        Fx.healBlockFull.at(other.x, other.y, other.block.size, baseColor, other.block);
                    });
                }
            }
        }
        @Override
        public boolean shouldConsume(){
            return unit.plans().size > 0 && !isHealSuppressed();
        }

        @Override
        public void draw(){
            if(thrusterTime > 0){
                float frame = thrusterTime;

                Draw.alpha(1f);
                drawThrusters(frame);
                Draw.rect(block.region, x, y);
                Draw.alpha(Interp.pow4In.apply(frame));
                drawThrusters(frame);
                Draw.reset();

                drawTeamTop();
            }else{
                super.draw();
            }
            drawer.draw(this);

            Draw.color();

            Draw.z(Layer.turret);

            Drawf.shadow(turretRegion, x - elevation, y - elevation, rotation - 90);
            Draw.rect(turretRegion, x, y, rotation - 90);

            if(efficiency > 0){
                unit.drawBuilding();
            }
            //circles(x,y,buildRange, Pal.accent.a(0.25f));
            //circles(x,y,range(), Pal.heal.a(0.25f));
        }
        public void circles(float x, float y, float rad, Color color){
            Lines.stroke(4f, color);
            Lines.circle(x, y, rad);
            Draw.reset();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(rotation);
            TypeIO.writePlans(write, unit.plans().toArray(BuildPlan.class));
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            rotation = read.f();
            unit.rotation(rotation);
            unit.plans().clear();
            var reqs = TypeIO.readPlans(read);
            if(reqs != null){
                for(var req : reqs){
                    unit.plans().add(req);
                }
            }
        }

        @Override
        public void drawSelect(){
            if (isRegen) {
                float realRange = range * 2;
                indexer.eachBlock(this, realRange, other -> true, other -> Drawf.selected(other, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f))));
                Drawf.dashCircle(x, y, realRange, baseColor);
            }
            Drawf.dashCircle(x, y, buildRange(), team.color);

            if(team.cores().size <= 1 && !proximity.contains(storage -> storage.items == items)) return;

            Lines.stroke(1f, Pal.accent);
            Cons<Building> outline = b -> {
                for(int i = 0; i < 4; i++){
                    Point2 p = Geometry.d8edge[i];
                    float offset = -Math.max(b.block.size - 1, 0) / 2f * tilesize;
                    Draw.rect("block-select", b.x + offset * p.x, b.y + offset * p.y, i * 90);
                }
            };
            team.cores().each(core -> {
                outline.get(core);
                core.proximity.each(storage -> storage.items == items, outline);
            });
            Draw.reset();
        }

        @Override
        public void onProximityUpdate(){
            // Безопасно вызываем базовую логику ядра; игнорируем ClassCast, который может возникать при попытке кастовать нестандартные «хранилища»
            try{
                super.onProximityUpdate();
            }catch(ClassCastException ignored){
                // пропускаем падение, продолжим своей логикой
            }

            // Линкуем рядом стоящие CoreLinkVaultUnitFactory как «хранилище ядра», без шаринга items-модуля
            if(proximity != null){
                for(Building other : proximity){
                    if(other instanceof CoreLinkVaultUnitFactory.CoreLinkVaultUnitFactoryBuild b){
                        CoreLinkVaultUnitFactory block = (CoreLinkVaultUnitFactory)b.block;
                        if(block.coreMerge && (b.linkedCore == null || b.linkedCore == this)){
                            b.linkedCore = this;
                        }
                    }
                }
            }
        }

        @Override
        public float range(){
            return range*2;
        }

        @Override
        public float warmup() {
            return enabled ? powerProduction * productionEfficiency :10f;
        }

        @Override
        public void onDestroyed(){
            super.onDestroyed();
        }

        @Override
        public void drawLight() {
            super.drawLight();
            drawer.drawLight(this);
        }

        @Override
        public float ambientVolume() {
            return Mathf.clamp(productionEfficiency);
        }

        @Override
        public float getPowerProduction() {
                return enabled ? powerProduction * productionEfficiency : 10f;
        }

        @Override
        public byte version() {
            return 1;
        }


        

    }
}
