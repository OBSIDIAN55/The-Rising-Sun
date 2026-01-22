package trs.content;

import static mindustry.type.ItemStack.with;
import static trs.trsMod.debug;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.entities.bullet.ContinuousFlameBulletType;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ContinuousLiquidTurret;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.payloads.Constructor;
import mindustry.world.blocks.payloads.PayloadConveyor;
import mindustry.world.blocks.payloads.PayloadRouter;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.draw.*;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;
import trs.multicraft.IOEntry;
import trs.multicraft.MultiCrafter;
import trs.multicraft.Recipe;
import trs.type.*;
import trs.type.Draw.RandomDrawGlowRegion;
import trs.type.Drills.ClusterDrill;
import trs.type.cores.BuildTurretRegenGeneratorCoreBlock;
import trs.type.cores.CoreLinkVaultUnitFactory;
import trs.type.defense.turrets.TRSItemTurret;
import trs.type.defense.walls.ExacrimWall;
import trs.type.distribution.*;
import trs.type.power.LargeVariableNode;
import trs.type.power.VariableNode;
import trs.type.test.ExpandableStorageBlock;
import trs.type.units.AnimatedUnitAssembler;

public class trsBlocks {
    public static Block
    //cores
            Case,incedent,Signal,
            perseverance,fortitude,stability,a,b,c,col,
            cellFactory,
    //storage
            testExpCVault,
    //fractions
    acronyx,arha,hronos,phoenix,
    //prod
            rubidiumSmelter,melter,crusher,atmosphericCondenser,carbonGlassClin,test,brazier,
    //distribution
            clinovalveDuct,
            clinovalveJunction,
            clinovalveRouter,
            clinovalveSorter,
            clinovalveInvertedSorter,
            clinovalveDuctBridge,
            clinovalveOverflowGate,
            clinovalveUnderflowGate,
    //walls
            clinovalveWall,clinovalveWallLarge,zincWall,zincWallLarge,steelWall,steelWallLarge,carbonWall,carbonWallLarge,exacrimWall,exacrimWallLarge,
    //drills
            hydraulicDrill,deepDrill,clusterDrill,hui,huiPart,
    //turrets
        splash,artery, wire,ash,lucidity,hallucination,
    //power
        variableNode,largeVariableNode,carbonBiomassReactor,zincGenerator,
    //units parts
        componentsFactory,clinovalvePayloadRouter,clinovalvePayloadConveyor,universalCollectorUnits,
        detailBody, exacrimCatalyst,modularTrunk,shockMechanism,skeleton,
    //effect
        bariumLightSource, alarmSystem,


    //test
    testGen,testNode;
    
    // Массив для хранения 256 colider блоков
    public static Block[] coliderBlocks = new Block[256];
    


    public static void load(){
        acronyx = new Block("acronyx");
        arha = new Block("arha");
        hronos = new Block("hronos");
        phoenix = new Block("phoenix");


        /**
        c = new CountForceProjector("shield"){{
            requirements(Category.effect, with(Items.copper,1));
            size = 3;
            sides = 40;
        }};
        b = new ItemLiquidContainer("b"){{
            requirements(Category.effect, with(Items.copper,1));
            liquidCapacity = 100f;
            itemCapacity = 100;
        }};

        test = new OverclockGenericCrafter("test"){{
            requirements(Category.crafting, with(Items.copper,1));
            consumeItem(Items.copper, 2);
            consumeItem(Items.thorium, 0).optional(true,false);
            baseCraftTime = 60f;
            outputItem = new ItemStack(Items.lead, 2);
            consumePower(1f);
            consumePowerPhase(1f);
        }};
        a = new ExplosiveCharge("a"){{
           requirements(Category.effect, with(Items.copper,1));
           consumeLiquid(Liquids.water, 1f);
           rotate = true;
        }};
         **/
        Case = new BuildTurretRegenGeneratorCoreBlock("case"){{
            requirements(Category.effect, with(Items.copper, 15));
            outlineColor = Color.valueOf("00000000");

            health = 4500;
            itemCapacity = 2000;
            thrusterLength = 34/4f;
            armor = 5f;
            alwaysUnlocked = true;
            requiresCoreZone = true;

            buildCostMultiplier = 0.7f;

            unitCapModifier = 10;
            researchCostMultiplier = 0.07f;
            powerProduction = 10f;
            incinerateNonBuildable = false;
            isFirstTier = true;
            size = 3;
            isRegen = true;
            isGenerator = true;
            consumePowerBuffered(4000f);
            squareSprite = false;


        }};

        cellFactory = new CoreLinkVaultUnitFactory("cell-fabricator"){{
            requirements(Category.effect, with(Items.copper, 15));
            plans = Seq.with(
                    new UnitPlan(UnitTypes.dagger, 60f * 2, with(Items.silicon, 10, Items.lead, 10)),
                    new UnitPlan(UnitTypes.crawler, 60f * 2, with(Items.silicon, 8, Items.coal, 10)),
                    new UnitPlan(UnitTypes.nova, 60f * 2, with(Items.silicon, 30, Items.lead, 20, Items.titanium, 20))
            );
            size = 3;
            consumePower(1.2f);
            researchCostMultiplier = 0.5f;

            itemCapacity = 1000;
            scaledHealth = 55;

            coreMerge = true;



        }};
        perseverance = new BuildTurretRegenGeneratorCoreBlock("perseverance"){{
            requirements(Category.effect, with(Items.copper, 15));
            outlineColor = Color.valueOf("00000000");

            size = 4;

            health = 4500;
            itemCapacity = 2000;
            thrusterLength = 34/4f;
            armor = 5f;
            alwaysUnlocked = true;
            requiresCoreZone = true;

            buildCostMultiplier = 0.7f;

            unitCapModifier = 15;
            researchCostMultiplier = 0.07f;
            powerProduction = 10f;
            incinerateNonBuildable = false;
            isFirstTier = true;
            squareSprite = false;


            drawer = new DrawMulti(
                new DrawRegion("-bottom"){{
                    layer = 29.7f;
                }},
                    new DrawRegion("-rotator"){{
                        spinSprite = true;
                        rotateSpeed = 3;
                        layer = 29.8f;
                    }},
                    new RandomDrawGlowRegion("-rotator-glow"){{
                        rotateSpeed = 3;
                        layer = 29.9f;
                        color = Color.cyan;
                        glowScale = 5f;
                        glowIntensity = 2f;
                    }},
                    new DrawDefault()
            );
        }};
        fortitude = new BuildTurretRegenGeneratorCoreBlock("fortitude"){{
            requirements(Category.effect, with(Items.copper, 15));
            outlineColor = Color.valueOf("00000000");

            size = 5;

            health = 4500;
            itemCapacity = 3000;
            thrusterLength = 34/4f;
            armor = 5f;
            alwaysUnlocked = true;
            incinerateNonBuildable = false;

            buildCostMultiplier = 0.7f;

            unitCapModifier = 15;
            researchCostMultiplier = 0.07f;
            powerProduction = 10f;
            squareSprite = false;



            drawer = new DrawMulti(
                    new DrawRegion("-bottom"){{
                        layer = 29.7f;
                    }},
                    new DrawRegion("-rotator"){{
                        spinSprite = true;
                        rotateSpeed = 3;
                        layer = 29.8f;
                    }},
                    new RandomDrawGlowRegion("-rotator-glow"){{
                        rotateSpeed = 3;
                        layer = 29.9f;
                        color = Color.cyan;
                        glowScale = 5f;
                        glowIntensity = 2f;
                    }},
                    new DrawDefault()
            );
        }};
        stability = new BuildTurretRegenGeneratorCoreBlock("stability"){{
            requirements(Category.effect, with(Items.copper, 15));
            outlineColor = Color.valueOf("00000000");

            size = 6;

            health = 4500;
            itemCapacity = 3000;
            thrusterLength = 34/4f;
            armor = 5f;
            alwaysUnlocked = true;
            incinerateNonBuildable = false;

            buildCostMultiplier = 0.7f;

            unitCapModifier = 15;
            researchCostMultiplier = 0.07f;
            powerProduction = 10f;
            squareSprite = false;


            drawer = new DrawMulti(
                    new DrawRegion("-bottom"){{
                        layer = 29.7f;
                    }},
                    new DrawRegion("-rotator1"){{
                        spinSprite = true;
                        rotateSpeed = 3;
                        layer = 29.8f;
                    }},
                    new RandomDrawGlowRegion("-rotator1-glow"){{
                        rotateSpeed = 3;
                        layer = 29.9f;
                        color = Color.cyan;
                        glowScale = 5f;
                        glowIntensity = 2f;
                    }},
                    new DrawRegion("-rotator2"){{
                        spinSprite = true;
                        rotateSpeed = -3;
                        layer = 29.8f;
                    }},
                    new RandomDrawGlowRegion("-rotator2-glow"){{
                        rotateSpeed = -3;
                        layer = 29.9f;
                        color = Color.cyan;
                        glowScale = 5f;
                        glowIntensity = 2f;
                    }},
                    new DrawDefault()
            );
        }};

        testExpCVault = new ExpandableStorageBlock("test"){{
            requirements(Category.effect, with(Items.graphite, 12, Items.silicon, 8, Items.lead, 8));
            size=4;
            itemCapacity = 100;
        }};

        bariumLightSource = new ChemicalLightSource("bariumLightSource"){{
            requirements(Category.effect, with(Items.graphite, 12, Items.silicon, 8, Items.lead, 8));
            squareSprite = true;
            brightness = 0.75f;
            radius = 140f;
            sourceLightColor = Color.valueOf("96037c");
        }};
        alarmSystem = new GenericCrafter("alarm-system"){{
            requirements(Category.effect, with(Items.graphite, 12, Items.silicon, 8, Items.lead, 8));
            size = 2;
            drawer = new DrawMulti(new DrawDefault(), new DrawGlowRegion(){{
                suffix = "-rotator";
                color = Color.red;
                rotate = true;
                rotateSpeed = 3f;
            }});

            ambientSound = trs.Sounds.alarm;
            ambientSoundVolume = 10000;
            buildVisibility = BuildVisibility.editorOnly;

        }};
        rubidiumSmelter = new GenericCrafter("rubidium-smelter"){{
            requirements(Category.crafting, with(Items.graphite, 12, Items.silicon, 8, Items.lead, 8));
            size = 4;

            squareSprite = false;
            itemCapacity = 15;

            outputItem = new ItemStack(trsItems.rubidium, 4);
            craftTime = 40f;
            hasPower = true;
            hasLiquids = false;

            consumeItems(with(trsItems.clinovalve, 6, trsItems.tin, 4));
            consumePower(0.50f);

            drawer = new DrawMulti(new DrawDefault(), new RandomDrawGlowRegion(){{
                color = Color.valueOf("b17702");
            }});
        }};

        melter = new MultiCrafter("melter"){{
            squareSprite = false;
            requirements(Category.crafting, with(Items.graphite, 12, Items.silicon, 8, Items.lead, 8));
            size = 3;
            itemCapacity = 10;
            drawer = new DrawMulti(new DrawRegion(""){{layer = 31;}}, new RandomDrawGlowRegion(32){{color = Pal.engine.cpy();}});
            resolvedRecipes = Seq.with(
                    new Recipe(){{
                        input = new IOEntry() {{
                            items = ItemStack.with(
                                    trsItems.clinovalve, 4,
                                    trsItems.tin, 2
                            );
                            power = 2f;
                        }};
                        output = new IOEntry(){{
                            items = ItemStack.with(
                                    trsItems.rubidium, 3
                            );
                        }};
                        craftTime = 60f;
                    }},
                    new Recipe(){{
                        input = new IOEntry() {{
                            items = ItemStack.with(
                                    trsItems.chrome, 1,
                                    trsItems.barium,1
                            );
                        }};
                        output = new IOEntry(){{
                            items = ItemStack.with(
                                    // trsItems.carbonDust, 0
                            );
                        }};
                        craftTime = 60f;
                    }},
                    new Recipe(){{
                        input = new IOEntry() {{
                            items = ItemStack.with(
                                    trsItems.chrome, 1,
                                    trsItems.zinc,1
                            );
                        }};
                        output = new IOEntry(){{
                            items = ItemStack.with(
                                    // trsItems.carbonDust, 0
                            );
                        }};
                        craftTime = 60f;
                    }},
                    new Recipe(){{
                        input = new IOEntry() {{
                            items = ItemStack.with(
                                    trsItems.biomass, 1
                            );
                        }};
                        output = new IOEntry(){{
                            items = ItemStack.with(
                                    trsItems.carbon, 1
                            );
                        }};
                        craftTime = 60f;
                    }},
                    new Recipe(){{
                        input = new IOEntry() {{
                            items = ItemStack.with(
                                    trsItems.carbonDust, 2,
                                    trsItems.quartzDust,2
                            );
                        }};
                        output = new IOEntry(){{
                            items = ItemStack.with(
                                    trsItems.carbonGlass, 1
                            );
                        }};
                        craftTime = 60f;
                    }}
            );
        }};

        brazier = new GenericCrafter("brazier"){{
            requirements(Category.crafting, with(Items.graphite, 12, Items.silicon, 8, Items.lead, 8));
            size = 3;
            itemCapacity = 10;
            consumeItem(trsItems.biomass,4);
            outputItem = new ItemStack(trsItems.carbon, 3);
            consumePower(240f/60f);
            drawer = new DrawMulti(new DrawDefault(),new RandomDrawGlowRegion(){{color = Color.valueOf("8c583e");}});
        }};
        carbonGlassClin = new GenericCrafter("carbon-glass-clin"){{
            requirements(Category.crafting, with(Items.graphite, 12, Items.silicon, 8, Items.lead, 8));
            squareSprite = false;
            size = 3;
            itemCapacity = 10;
            consumeItems(with(trsItems.carbonDust, 5, trsItems.quartzDust, 5));
            outputItem = new ItemStack(trsItems.carbonGlass, 2);
            consumePower(0.50f);

            drawer = new DrawMulti(new DrawDefault(), new RandomDrawGlowRegion(){{
                color = Color.valueOf("56bff1");
            }});
        }};
        crusher = new MultiCrafter("crusher"){{
            requirements(Category.crafting, with(Items.graphite, 12, Items.silicon, 8, Items.lead, 8));
            squareSprite = false;
            size = 3;
            itemCapacity = 10;
            drawer = new DrawMulti(new DrawRegion("-bottom"),new DrawPistons(){{
                sides = 1;
                angleOffset = -90f;
                sinMag = -4f;
                sinScl = 10f;
                lenOffset = 0.1f;
                sinOffset = 30f;

            }},new DrawPistons(){{
                suffix = "-piston0";
                sides = 1;
                angleOffset = -90f;
                sinMag = 4f;
                sinScl = 10f;
                lenOffset = 0.1f;
                sinOffset = 30f;
            }},new DrawDefault(),new DrawGlowRegion(){{
                color = trsItems.quartzDust.color;
                glowIntensity = 0.35f;
                glowScale = 10.5f;
            }});
            resolvedRecipes = Seq.with(
                    new Recipe(){{
                        input = new IOEntry() {{
                           items = ItemStack.with(
                                   trsItems.quartz, 5
                           );
                        }};
                        output = new IOEntry(){{
                           items = ItemStack.with(
                                   trsItems.quartzDust, 4
                           );
                        }};
                        craftTime = 150f;
                    }},
                    new Recipe(){{
                    input = new IOEntry() {{
                        items = ItemStack.with(
                                trsItems.carbon, 6
                        );
                    }};
                    output = new IOEntry(){{
                        items = ItemStack.with(
                                trsItems.carbonDust, 4
                        );
                    }};
                    craftTime = 120f;
                    }}
            );
        }};
        atmosphericCondenser = new MultiCrafter("atmospheric-condenser"){{
            requirements(Category.crafting, with(Items.graphite, 12, Items.silicon, 8, Items.lead, 8));
            squareSprite = false;
            size = 3;
            liquidCapacity = 20f;
            drawer = new DrawMulti(new DrawRegion("-bottom"),new DrawLiquidTile(trsLiquids.argon), new DrawLiquidTile(trsLiquids.metan), new DrawDefault());
                resolvedRecipes = Seq.with(
                    new Recipe(){{
                        input = new IOEntry() {{
                           power = 6f;
                        }};
                        output = new IOEntry() {{
                            fluids = LiquidStack.with(trsLiquids.metan, 1f);
                        }};
                    }},
                    new Recipe(){{
                        input = new IOEntry() {{
                            power = 8f;
                        }};
                        output = new IOEntry() {{
                            fluids = LiquidStack.with(trsLiquids.argon, 1f);
                        }};
                    }}
                );

        }};
        clinovalveDuct = new ItemLiquidDuct("clinovalve-duct"){{
            requirements(Category.distribution, with(Items.copper, 1));
            speed = 4f;
            liquidCapacity = 10f;
            leaks = false;
            buildCostMultiplier = 100f;

        }};
        clinovalveRouter = new ItemLiquidRouter("clinovalve-router"){{
            requirements(Category.distribution, with(Items.copper, 3));
            buildCostMultiplier = 4f;
            liquidCapacity = 10f;
        }};
        clinovalveJunction = new ItemLiquidJunction("clinovalve-junction"){{
            requirements(Category.distribution, with(Items.copper, 2));
            speed = 10;
            capacity = 3;
            health = 30;
            buildCostMultiplier = 6f;
        }};
        clinovalveSorter = new ItemLiquidSorter("clinovalve-sorter"){{
            requirements(Category.distribution, with(Items.lead, 2, Items.copper, 2));
            buildCostMultiplier = 3f;
            liquidCapacity = 10f;
        }};
        clinovalveInvertedSorter = new ItemLiquidSorter("clinovalve-inverted-sorter"){{
            requirements(Category.distribution, with(Items.lead, 2, Items.copper, 2));
            buildCostMultiplier = 3f;
            invert = true;
            liquidCapacity = 10f;
        }};
        clinovalveDuctBridge = new ItemLiquidDuctBridge("clinovalve-duct-bridge"){{
            requirements(Category.distribution, with(Items.beryllium, 20));
            health = 90;
            speed = 4f;
            buildCostMultiplier = 2f;
            researchCostMultiplier = 0.3f;
        }};
        clinovalveOverflowGate = new ItemLiquidOverflowGate("clinovalve-overflow-gate"){{
            requirements(Category.distribution, with(Items.lead, 2, Items.copper, 4));
            buildCostMultiplier = 3f;
            liquidCapacity = 10f;
        }};

        clinovalveUnderflowGate = new ItemLiquidOverflowGate("clinovalve-underflow-gate"){{
            requirements(Category.distribution, with(Items.lead, 2, Items.copper, 4));
            buildCostMultiplier = 3f;
            invert = true;
            liquidCapacity = 10f;
        }};


        //drills
        hydraulicDrill = new Drill("hydraulic-drill"){{
            requirements(Category.production, with(Items.lead, 2, Items.copper, 2));
            tier = 2;
            drillTime = 600;
            size = 2;
            envEnabled ^= Env.space;
            researchCost = with(Items.copper, 10);

            consumeLiquid(Liquids.water, 0.05f).boost();
        }};
        deepDrill = new Drill("deep-drill"){{
            requirements(Category.production, with(Items.lead, 2, Items.copper, 2));
            tier = 3;
            drillTime = 400;
            size = 2;
            //mechanical drill doesn't work in space
            envEnabled ^= Env.space;
            researchCost = with(Items.copper, 10);

            consumeLiquid(Liquids.water, 0.05f).boost();
        }};
        clusterDrill = new ClusterDrill("cluster-drill"){{
            requirements(Category.production, with(Items.lead, 2, Items.copper, 2));
            drillTime = 281.25f;
            size = 3;
            tier = 4;
            consumeCoolant(0.05f);
        }};
        /**
        hui = new MultiBlockDrill("hui-drill"){{
            requirements(Category.production, with(Items.lead, 2, Items.copper, 4));
            size = 4;
            customShadow = true;
        }};
        huiPart = new Block("hui-part"){{
            requirements(Category.production, with(Items.lead, 2, Items.copper, 4));
            size = 2;
            customShadow = true;
        }};**/
        //walls

        clinovalveWall = new Wall("clinovalve-wall"){{
            requirements(Category.defense, with(Items.lead, 2, Items.copper, 4));
        }};
        clinovalveWallLarge = new Wall("clinovalve-wall-large"){{
            size = 2;
            requirements(Category.defense, with(Items.lead, 2, Items.copper, 4));
        }};
        zincWall = new Wall("zinc-wall"){{
            requirements(Category.defense, with(Items.lead, 2, Items.copper, 4));
        }};
        zincWallLarge = new Wall("zinc-wall-large"){{
            requirements(Category.defense, with(Items.lead, 2, Items.copper, 4));
            size = 2;
        }};
        steelWall = new Wall("steel-wall"){{
            requirements(Category.defense, with(Items.lead, 2, Items.copper, 4));
        }};
        steelWallLarge = new Wall("steel-wall-large"){{
            requirements(Category.defense, with(Items.lead, 2, Items.copper, 4));
            size = 2;
        }};
        carbonWall = new Wall("carbon-wall"){{
            requirements(Category.defense, with(Items.lead, 2, Items.copper, 4));
        }};
        carbonWallLarge = new Wall("carbon-wall-large"){{
            requirements(Category.defense, with(Items.lead, 2, Items.copper, 4));
            size = 2;
        }};
        exacrimWall = new ExacrimWall("exacrim-wall"){{
            requirements(Category.defense, with(Items.lead, 2, Items.copper, 4));
            size = 2;
            drawer = new DrawMulti(new DrawDefault(), new DrawGlowRegion("-glow"){{
                color = Color.cyan;
                alpha = 0.8F;
                glowScale = 10F;
                glowIntensity = 0.7F;
            }});
        }};
        exacrimWallLarge = new ExacrimWall("exacrim-wall-large"){{
            requirements(Category.defense, with(Items.lead, 2, Items.copper, 4));
            size = 3;
            drawer = new DrawMulti(new DrawDefault(), new DrawGlowRegion("-glow"){{
                color = Color.cyan;
                alpha = 0.8F;
                glowScale = 9F;
                glowIntensity = 0.7F;
            }});
        }};

        //units parts

        detailBody = new Wall("detail-body"){{
            requirements(Category.units, with(Items.lead, 2, Items.copper, 4));
            size = 2;
            hideDatabase = true;
            buildVisibility = BuildVisibility.sandboxOnly;
        }};
        skeleton = new Wall("skeleton"){{
            requirements(Category.units, with(Items.lead, 2, Items.copper, 4));
            size = 2;
            hideDatabase = true;
            buildVisibility = BuildVisibility.sandboxOnly;
        }};
        shockMechanism = new Wall("shock-mechanism"){{
            requirements(Category.units, with(Items.lead, 2, Items.copper, 4));
            size = 2;
            hideDatabase = true;
            buildVisibility = BuildVisibility.sandboxOnly;
        }};
        modularTrunk = new Wall("modular-trunk-tank"){{
            requirements(Category.units, with(Items.lead, 2, Items.copper, 4));
            size = 2;
            hideDatabase = true;
            buildVisibility = BuildVisibility.sandboxOnly;
        }};
        exacrimCatalyst = new Wall("exacrim-catalyst"){{
            requirements(Category.units, with(Items.lead, 2, Items.copper, 4));
            size = 2;
            hideDatabase = true;
            buildVisibility = BuildVisibility.sandboxOnly;
        }};

        componentsFactory = new Constructor("components-factory"){{
            requirements(Category.units, with(Items.lead, 2, Items.copper, 4));
            size = 2;
            hasPower = true;
            buildSpeed = 0.6f;
            consumePower(2.5f);
            filter = Seq.with(trsBlocks.detailBody);
        }};

        clinovalvePayloadConveyor = new PayloadConveyor("reinforced-clinovalve-conveyor"){{
            requirements(Category.units, with(Items.graphite, 10, Items.copper, 10));
            canOverdrive = false;
            size = 2;
        }};

        clinovalvePayloadRouter = new PayloadRouter("reinforced-clinovalve-router"){{
            requirements(Category.units, with(Items.graphite, 15, Items.copper, 10));
            canOverdrive = false;
            size = 2;
        }};
        universalCollectorUnits = new AnimatedUnitAssembler("universal-collector-units"){{
            requirements(Category.units, with(Items.graphite, 15, Items.copper, 10));
            size = 6;
            plans.add(
                    new AssemblerUnitPlan(trsUnits.apocalypse, 60f * 60f, PayloadStack.list(trsBlocks.detailBody, 20, trsBlocks.shockMechanism,8, trsBlocks.modularTrunk, 3,trsBlocks.exacrimCatalyst,3,trsBlocks.steelWallLarge,15)),
                    new AssemblerUnitPlan(trsUnits.disaster, 60f * 95, PayloadStack.list(trsBlocks.detailBody, 17, trsBlocks.shockMechanism,6, trsBlocks.modularTrunk, 2,trsBlocks.exacrimCatalyst,2,trsBlocks.steelWallLarge,10))
            );
            areaSize = 13;

            consumePower(3f);
            consumeLiquid(Liquids.cyanogen, 12f / 60f);
            
             // Настройки анимации манипуляторов
             manipulatorCount = 4;
             manipulatorSpeed = 0.1f;
             manipulatorRadius = 3f;
             weldingTime = 2f;
             weldingColor = Color.orange;

        }};



        //turrets
        //phoenix
        ash = new ContinuousLiquidTurret("ash"){{
                requirements(Category.turret, with(Items.copper, 1));
                size = 4;
                float r = range = 130f;
                ammo(
                        trsLiquids.metan, new ContinuousFlameBulletType(){{
                            damage = 130f;
                            rangeChange = 70f;
                            ammoMultiplier = 1.5f;
                            length = r + rangeChange;
                            knockback = 2f;
                            pierceCap = 3;
                            buildingDamageMultiplier = 0.3f;
                            timescaleDamage = true;
                            width = 5f;

                            colors = new Color[]{Color.valueOf("#1E90FF").a(0.55f), Color.valueOf("#87CEFA").a(0.7f), Color.valueOf("#ADD8E6").a(0.8f), Color.valueOf("#B0C4DE"), Color.white};
                            drawFlare = false;
                            lightColor = hitColor = flareColor;
                        }}
                );
                recoil = 0.5f;
                reload = 6;
                coolantMultiplier = 1.5f;
                shootY = 0;
                health = 400;
                minWarmup = 0.9f;
                ammoUseEffect = Fx.lightningShoot;
            }};
        //akronix
        lucidity = new ItemTurret("lucidity"){{
            requirements(Category.turret, with(Items.copper, 1));
            size = 4;
            inaccuracy = 2;

            ammo(
                    trsItems.steel, new BasicBulletType(13f,100){{
                        drag = 0.04f;
                        reload = 100f;
                        width = 6f;
                        trailLength = 10;
                        weaveScale = 8f;
                        weaveMag = 1f;


                        frontColor = backColor = trailColor = Color.valueOf("D8CAE2FF");
                        shoot = new ShootBarrel(){{
                            barrels = new float[]{
                                    -6, 0, 0,
                                    6, 0, 0
                            };
                            shots = 6;
                            shotDelay = 4f;
                        }};
                        fragBullets = 7;
                        fragAngle = 360;
                        delayFrags = true;
                        intervalDelay = 18f;
                        fragBullet = new LaserBulletType(){{
                            colors = new Color[]{Color.valueOf("D8CAE2FF"), Color.valueOf("D8CAE2FF")};
                            length = 50f;
                            width = 10f;
                        }};
                    }}
            );
            //arch
            hallucination = new ItemTurret("hallucination"){{
               requirements(Category.turret, with(Items.copper, 1));
               size = 6;
               ammo(
                       Items.copper, new BasicBulletType(7,0,"circle"){{
                           drag = 0.04f;
                           reload = 100f;
                           width = 20;
                           height = 20;
                       }}
               );
            }};

        }};
        splash = new TRSItemTurret("splash"){{
            requirements(Category.turret, with(Items.copper,1));
            isHeating = true;
            heating = 0.5f;
            size = 2;
            fraction = "Chronos";
            heatDamage = 1f;
            ammo(
                    trsItems.clinovalve,  new BasicBulletType(2.5f, 9){{
                        width = 7f;
                        height = 9f;
                        lifetime = 60f;
                        ammoMultiplier = 2;
                        frontColor = backColor = Color.valueOf("c52603");
                    }},
                    trsItems.carbonGlass, new BasicBulletType(3.5f, 12){{
                        frontColor = backColor = Color.valueOf("c52603");
                        width = 9f;
                        height = 12f;
                        reloadMultiplier =1.25f;
                        ammoMultiplier = 4;
                        lifetime = 60f;
                        fragBullets = 6;
                        fragBullet = new BasicBulletType(3f, 5){{
                            frontColor = backColor = Color.valueOf("c52603");
                            width = 5f;
                            height = 12f;
                            shrinkY = 1f;
                            lifetime = 20f;
                            despawnEffect = Fx.none;
                            collidesGround = true;
                        }};
                    }},
                    trsItems.carbon, new BasicBulletType(3f, 20){{
                        frontColor = backColor = Color.valueOf("c52603");
                        width = 7f;
                        height = 8f;
                        shrinkY = 0f;
                        homingPower = 0.08f;
                        splashDamageRadius = 20f;
                        splashDamage = 30f * 1.5f;
                        makeFire = true;
                        ammoMultiplier = 5f;
                        hitEffect = Fx.blastExplosion;
                        status = StatusEffects.burning;
                    }}
            );
            shoot = new ShootAlternate(3.5f);

            recoil = 0.5f;
            shootY = 6.5f;
            reload = 7f;
            range = 110;
            shootCone = 15f;
            ammoUseEffect = Fx.casing1;
            health = 250;
            inaccuracy = 2f;
            rotateSpeed = 10f;
            coolant = consumeCoolant(0.1f);

            limitRange();
        }};

        //power
        variableNode = new VariableNode("variable-node"){{
            requirements(Category.power, with(Items.graphite, 15, Items.copper, 10));
            baseLaserRange = 12;
            baseMaxNodes = 16;
            farLaserRange = 50;
            farMaxNodes = 3;
            closeLaserRange = 6;
        }};
        largeVariableNode = new LargeVariableNode("large-variable-node"){{
            requirements(Category.power, with(Items.graphite, 15, Items.copper, 10));
            baseLaserRange = 12;
            baseMaxNodes = 16;
            farLaserRange = 50;
            farMaxNodes = 3;
            closeLaserRange = 6;
            size = 2;
        }};
        carbonBiomassReactor = new ConsumeGenerator("carbon-biomass-reactor"){{
            requirements(Category.power, with(Items.copper,1));
            size = 5;

            powerProduction = 100f;
            itemDuration = 90f;
            consumeLiquid(trsLiquids.crystalWater, 0.1f);
            hasLiquids = true;
            consumeItems(with(trsItems.carbon, 2, trsItems.biomass,2));
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidRegion(trsLiquids.crystalWater),new DrawCultivator(){{
                radius = 2f;
                bubbles = 40;
                sides  =10;
                spread = 10f;
            }},new DrawDefault());
            }
        };
        zincGenerator = new ConsumeGenerator("zinc-generator") {{
            
                requirements(Category.power, with(Items.copper, 1));
                size = 3;

                powerProduction = 100f;
                itemDuration = 90f;
                consumeItems(with(trsItems.carbonDust, 1));
            }};

        if (debug) {
            // Создание 256 colider блоков с названиями colider-0000001 до colider-0000256
            for (int i = 0; i < 256; i++) {
                String blockName = String.format("colider-%07d", i + 1);
                coliderBlocks[i] = new GenericCrafter(blockName) {{
                    requirements(Category.effect, with(Items.graphite, 15, Items.copper, 10));
                    size = 16;
                    hideDatabase = true;
                }};
            }
        }
    }
}
