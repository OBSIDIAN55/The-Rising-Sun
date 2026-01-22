package trs.content;

import static trs.type.TrsBulletTypes.*;

import arc.graphics.Color;
import arc.math.geom.Rect;
import mindustry.content.Fx;
import mindustry.entities.abilities.ShieldArcAbility;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.part.HaloPart;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.unit.TankUnitType;
import trs.type.construsctors.HealthTrackingTankUnit;
import trs.type.construsctors.HealthTrackingTankUnitType;


public class trsUnits {
    public static UnitType
            disaster,massacre,apocalypse, termination,
            starfall;
    public static void load() {
        massacre = new TankUnitType("massacre"){{
            this.constructor = TankUnit::create;
                        outlineColor = Color.valueOf("332C2CFF");
            hitSize = 46f;
            treadPullOffset = 1;
            speed = 0.48f;
            health = 22000;
            armor = 26f;
            crushDamage = 25f / 5f;
            rotateSpeed = 0.8f;
            float xo = 128f/2f, yo = 128f/2f;
            treadRects = new Rect[]{new Rect(12-xo, 40-yo , 29, 31),new Rect(15-xo, 117-yo , 28, 31)};

            weapons.add(new Weapon(name+"-weapon"){{
                reload = 100f;
                minWarmup = 0.9f;
                layerOffset = 0.1f;
                rotate = true;
                mirror = false;
                x = 0f;
                y = 0f;
                parts.add(new RegionPart("-blade"){{
                    progress = PartProgress.warmup;
                    mirror = true;
                    moveX = -3f/4;
                    moveY = -3f/4;
                    moveRot = 15f;
                    layerOffset = -0.01f;
                    outline = false;

                }});
            }});
        }};
        disaster = new HealthTrackingTankUnitType("disaster"){{
            this.constructor = HealthTrackingTankUnit::create;
                 outlineColor = Color.valueOf("332C2CFF");
                 outlines = false;
            hitSize = 46f;
            speed = 0.48f;
            health = 22000;
            armor = 26f;
            crushDamage = 25f / 5f;
            rotateSpeed = 0.8f;
            treadRects = new Rect[]{new Rect(-53f, 24f, 30, 32),new Rect(-50f, -53f, 30, 32)};


            weapons.add(new Weapon(name+"-weapon"){{
                            reload = 35f;
                            layerOffset = 0.1f;
                            rotate = true;
                            mirror = false;
                            recoils = 2;
                             //shoot = new ShootAlternateSpread(15,10,4);
                inaccuracy = 0.2f;
                velocityRnd = 0.17f;
                shake = 1f;
                shootCone = 40f;
                recoil = 0.5f;
                minWarmup = 0.8f;
                            x = 0f;
                            y = 0f;
                            shootY = 15f;
                            bullet = new BasicBulletType(8f, 41){{
                                knockback = 4f;
                                width = 25f;
                                hitSize = 7f;
                                height = 20f;
                                shootEffect = Fx.shootBigColor;
                                smokeEffect = Fx.shootSmokeSquareSparse;
                                ammoMultiplier = 1;
                                hitColor = backColor = trailColor = Color.valueOf("ea8878");
                                frontColor = Pal.redLight;
                                trailWidth = 6f;
                                trailLength = 3;
                                hitEffect = despawnEffect = Fx.hitSquaresColor;
                                buildingDamageMultiplier = 0.2f;
                            }};
                for(int i = 0; i < 2; i++) {
                    int f = i;
                    parts.add(new RegionPart("-anim" + (i == 0 ? "1" : "2")) {{
                        progress = PartProgress.warmup.delay((2 - f) * 0.3f).blend(PartProgress.recoil, 0.26f);
                                  recoilIndex = f;
                                  under = true;
                                  moveY = -1.5f;
                                  moveX = 2.6f*(f == 0 ? -1 : 1);
                              }});}}});
        }};
        apocalypse = new TankUnitType("apocalypse"){{
            this.constructor = TankUnit::create;
            outlineColor = Color.valueOf("332C2CFF");
            hitSize = 30f;
            treadPullOffset = 1;
            speed = 0.48f;
            health = 22000;
            armor = 26f;
            crushDamage = 25f / 5f;
            rotateSpeed = 0.8f;

            abilities.add(new ShieldArcAbility(){{
                region = "tecta-shield";
                radius = 36f;
                angle = 82f;
                regen = 0.6f;
                cooldown = 60f * 8f;
                max = 2000f;
                y = -15f;
                width = 6f;
                whenShooting = true;
            }}
);

            treadRects = new Rect[]{new Rect(-63f, -59f, 32, 34),new Rect(-69f, -19f, 31, 34),new Rect(-62f, 34f, 30, 34)};
            weapons.add( new Weapon(name+"-weapon"){{
                reload = 100f;
                layerOffset = 0.1f;
                mirror = false;
                x = 0f;
                y = 0f;
                shootY = 5f;
                recoil = 2f;
                minWarmup = 0.9f;
                rotate = true;
                rotateSpeed = 0.8f;



                bullet = new BasicBulletType(60,20){{
                    pierce = true;
                    pierceCap = 3;
                    drag=  0.01f;
                    smokeEffect = Fx.shootBigSmoke;
                    shootEffect = Fx.shootBigColor;
                    width = 10;
                    height = 60;
                    lifetime = 10;
                    hitSize = 4;
                    trailWidth = 4f;
                    trailLength = 13;
                    despawnEffect = Fx.hitBulletColor;
                    trailColor = backColor = frontColor = Color.cyan;
                }};
                for(int i = 1; i <= 3; i++){
                    int fi = i;
                    parts.add(new RegionPart("-blades-"+i){{
                        progress = PartProgress.warmup.blend(PartProgress.recoil, 0.6f);
                        heatColor = new Color(1f, 0.1f, 0.1f);
                        mirror = true;
                        under = true;
                        moveY = -10f * fi/4f;
                        moveX = -5f/4f;
                        layerOffset = -0.002f;

                    }});
                }
                for(int i = 1; i <= 3; i++){
                    int fi = i;
                    parts.add(new RegionPart("-charge-"+i){{
                        progress = PartProgress.warmup.delay((3 - fi) * 0.3f);
                        heatColor = new Color(1f, 0.1f, 0.1f);
                        mirror = true;
                        under = true;
                        moveX = -35f/4f+fi/1.3f;
                        moveY = 13f/4f;
                        moveRot = 30f;
                        layerOffset = -0.002f;

                    }});
                }
                parts.addAll(new RegionPart("-top"){{
                    mirror = false;
                    progress = PartProgress.life;
                    layerOffset = 0.00001f;
                }},new HaloPart(){{
                    tri = false;
                    sides = 4;
                    shapes = 8;
                    shapeRotation = 45f;

                    haloRotateSpeed = 7f;
                    hollow = true;
                    mirror = false;
                    progress = PartProgress.life;
                    color = Color.cyan;
                    radius = 0.4f;
                    haloRadius = 3.5f;
                    y = -7f;
                    stroke = 1f;
                }},new HaloPart(){{
                    tri = false;
                    sides = 4;
                    shapes = 8;
                    shapeRotation = 45f;

                    haloRotateSpeed = 7f;
                    hollow = true;
                    mirror = false;
                    progress = PartProgress.life;
                    color = Color.cyan;
                    radius = 0.4f;
                    haloRadius = 4f;
                    y = -7f;
                    stroke = 1f;
                }});
            }});
        }};
        termination = new TankUnitType("the-end"){{
            this.constructor = TankUnit::create;
            outlineColor = Color.valueOf("332C2CFF");
            hitSize = 65f;
            treadPullOffset = 1;
            speed = 0.48f;
            health = 22000;
            armor = 26f;
            crushDamage = 25f / 5f;
            rotateSpeed = 0.8f;

            treadRects = new Rect[]{ new Rect(-62f, 3f, 32, 144),
                    new Rect(-103f, 44f, 34, 72),
                    new Rect(-100f, -119f, 30, 144)};
            weapons.add( new Weapon(name+"-weapon"){{
                layerOffset = 0.1f;
                mirror = false;
                x = 0f;
                y = 0f;
                shootY = 30f;
                recoil = 2f;
                rotate = true;
                rotateSpeed = 0.8f;

                targetAir = false;
                shake = 4f;
                reload = 60f * 2.3f;
                minWarmup = 0.85f;

                shootWarmupSpeed = 0.07f;
                recoils = 2;

                //shoot = new ShootAlternate(10f);

                bullet = TerminationBaseBullet;
                shoot = new ShootBarrel(){{
                    barrels = new float[]{
                            -8, 0, 0,
                            8, 0, 0
                    };
                    shots = 2;
                    shotDelay = 10f;
                }};
                for(int i = 0; i < 2; i++) {
                    int f = i;
                    parts.add(new RegionPart("-trunk-" + (i==0 ? "l" : "r")) {{
                        progress = PartProgress.recoil;
                        recoilIndex = f;
                        under = true;
                        moveY = -8.5f;
                    }});
                }
            }}, new Weapon(name+"-weapon2"){{
                layerOffset = 0.01f;
                mirror = false;
                x = 0f;
                y = 0f;
                shootY = 30f;
                recoil = 2f;
                rotate = true;
                rotateSpeed = 0.8f;

                targetAir = false;
                shake = 4f;
                reload = 380f;
                minWarmup = 0.97f;

                shootWarmupSpeed = 0.03f;

                parts.add(new RegionPart("-trunk-c") {{
                    progress = PartProgress.recoil;
                    under = true;
                    moveY = -8.5f;
                }});

                bullet = ExacrimExplosionBulletType;

            }});
        }};

        starfall = new UnitType("starfall"){{
            this.constructor = MechUnit::create;
            outlineColor = Color.valueOf("201D26FF");
            hitSize = 30f;
            treadPullOffset = 1;
            speed = 0.48f;
            health = 22000;
            armor = 26f;
            crushDamage = 25f / 5f;
            rotateSpeed = 0.8f;
            drawCell = false;
            

            weapons.add( new Weapon(name+"-weapon"){{
                rotate = false;
                mirror = true;
                x = -28.25f;
                y = -3f;
                shootY = 26f;
                shootX = -3.5f;
                shootCone = 0f;
                recoil = 2f;
                layerOffset = -0.01f;

                targetAir = true;
                shake = 4f;
                reload = 5f * 2.3f;
                minWarmup = 0.85f;

                shootWarmupSpeed = 0.07f;

                //shoot = new ShootAlternate(10f);

                bullet = TerminationBaseBullet;
            }}, new Weapon(name+"-rail"){{
                layerOffset = 0.01f;
                mirror = true;
                x = -18.25f;
                y = -13.5f;
                shootY = 9f;
                shootX = 0f;
                recoil = 2f;
                rotate = true;
                rotateSpeed = 0.8f;

                targetAir = false;
                shake = 4f;
                reload = 10f;
                minWarmup = 0.97f;

                shootWarmupSpeed = 0.03f;

                bullet = ExacrimExplosionBulletType;

            }});
        }};
    }
}
