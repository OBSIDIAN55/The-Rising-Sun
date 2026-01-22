package trs.type;

import static arc.graphics.g2d.Draw.alpha;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.lineAngle;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;
import static arc.math.Mathf.rand;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.ExplosionBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Weapon;
import mindustry.type.unit.MissileUnitType;
import trs.content.trsItems;


public class TrsBulletTypes {
    public static BulletType IonFieldBulletType, TerminationBaseBullet, ExacrimExplosionBulletType;

    public static void load(){
        IonFieldBulletType = new BulletType(){{
            lifetime = 1;
            splashDamage = 1000;
            splashDamageRadius = 30f;
            despawnEffect = hitEffect = new WaveEffect(){{
                lifetime = 60f;
                strokeFrom = 10f;
                sizeFrom = 100f;
                sizeTo = 30f;
            }};
        }};
        TerminationBaseBullet = new BasicBulletType(7.95f, 985, "shell"){{
            hitEffect = new MultiEffect(Fx.titanExplosionSmall, Fx.titanSmokeSmall);
            despawnEffect = Fx.none;
            knockback = 3f;
            lifetime = 70f;
            height = 28f;
            width = 15f;
            splashDamageRadius = 36f;
            splashDamage = 750f;
            rangeChange = 10f*8f;
            reloadMultiplier = 0.8f;
            scaledSplashDamage = true;
            backColor = hitColor = trailColor = Color.valueOf("#e15f55");
            frontColor = Color.white;
            ammoMultiplier = 1f;

            status = StatusEffects.blasted;

            trailLength = 32;
            trailWidth = 3.35f;
            trailSinScl = 2.5f;
            trailSinMag = 0.5f;
            trailEffect = Fx.disperseTrail;
            trailInterval = 2f;
            despawnShake = 7f;

            shootEffect = Fx.shootTitan;
            smokeEffect = Fx.shootSmokeTitan;
            trailRotation = true;

            trailInterp = v -> Math.max(Mathf.slope(v), 0.8f);
            shrinkX = 0.2f;
            shrinkY = 0.1f;
            buildingDamageMultiplier = 0.2f;
            fragLifeMin = 1.5f;

            fragBullets = 15;
            fragBullet = new BasicBulletType(6f, 50, "shell"){{
                hitEffect = new MultiEffect(Fx.titanExplosionFrag, Fx.titanLightSmall, new WaveEffect(){{
                    lifetime = 8f;
                    strokeFrom = 8f;
                    sizeTo = 1f;
                }});

                despawnEffect = Fx.hitBulletColor;
                homingPower = 0.3f;
                homingRange = 500f;
                width = 20f;
                height = 34f;
                lifetime = 40f;
                knockback = 0.5f;
                splashDamageRadius = 57f;
                splashDamage = 85f;
                scaledSplashDamage = true;
                pierceArmor = true;
                backColor = frontColor = hitColor = Color.valueOf("#e15f55");
                frontColor = Color.white;
                buildingDamageMultiplier = 0.25f;
                shrinkY = 0.3f;
            }};
        }};
        ExacrimExplosionBulletType = new BulletType(0f, 0){{
            shootEffect = Fx.shootTitan;
            smokeEffect = Fx.shootSmokeTitan;
            trailRotation = true;

            trailInterp = v -> Math.max(Mathf.slope(v), 0.8f);

            hitEffect = new MultiEffect(Fx.titanExplosionSmall, Fx.titanSmokeSmall);
            despawnEffect = Fx.none;
            knockback = 3f;
            splashDamageRadius = 36f;
            splashDamage = 750f;
            rangeChange = 10f*8f;
            reloadMultiplier = 0.8f;
            scaledSplashDamage = true;
            hitColor = trailColor = Color.blue;
            ammoMultiplier = 1f;


            status = StatusEffects.blasted;

            trailLength = 32;
            trailWidth = 3.35f;
            trailSinScl = 2.5f;
            trailSinMag = 0.5f;
            trailEffect = Fx.disperseTrail;
            trailInterval = 2f;
            despawnShake = 7f;

            spawnUnit = new MissileUnitType("shell"){{
                speed = 7.95f;
                lifetime = 70f;
                range = 1f;
                weapons.add(new Weapon(){{
                    shootCone = 360f;
                    mirror = false;
                    reload = 1f;
                    shootOnDeath = true;
                    bullet = new ExplosionBulletType(3240,25){{
                        hitShake = 6f;
                        hitEffect = despawnEffect =new MultiEffect( new Effect(30, 500f, b -> {
                            float intensity = 6.8f;
                            float baseLifetime = 25f + intensity * 11f;
                            b.lifetime = 50f + intensity * 65f;

                            color(trsItems.exacrim.color);
                            alpha(0.7f);
                            for(int i = 0; i < 4; i++){
                                rand.setSeed(b.id* 2L + i);
                                float lenScl = rand.random(0.4f, 1f);
                                int fi = i;
                                b.scaled(b.lifetime * lenScl, e -> {
                                    randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int)(2.9f * intensity), 22f * intensity, (x, y, in, out) -> {
                                        float fout = e.fout(Interp.pow5Out) * rand.random(0.5f, 1f);
                                        float rad = fout * ((2f + intensity) * 2.35f);

                                        Fill.circle(e.x + x, e.y + y, rad);
                                        Drawf.light(e.x + x, e.y + y, rad * 2.5f, trsItems.exacrim.color, 0.5f);
                                    });
                                });
                            }

                            b.scaled(baseLifetime, e -> {
                                Draw.color();
                                e.scaled(5 + intensity * 2f, i -> {
                                    stroke((3.1f + intensity/5f) * i.fout());
                                    Lines.circle(e.x, e.y, (3f + i.fin() * 14f) * intensity);
                                    Drawf.light(e.x, e.y, i.fin() * 14f * 2f * intensity, Color.white, 0.9f * e.fout());
                                });

                                color(trsItems.exacrim.color, trsItems.exacrim.color, e.fin());
                                stroke((2f * e.fout()));

                                Draw.z(Layer.effect + 0.001f);
                                randLenVectors(e.id + 1, e.finpow() + 0.001f, (int)(8 * intensity), 28f * intensity, (x, y, in, out) -> {
                                    lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + out * 4 * (4f + intensity));
                                    Drawf.light(e.x + x, e.y + y, (out * 4 * (3f + intensity)) * 3.5f, Draw.getColor(), 0.8f);
                                });
                            });
                        }), new Effect(12f, e -> {
                            for (int i = 0; i < 30; i++) {
                                color(trsItems.exacrim.color, trsItems.exacrim.color, e.fin());
                                stroke(e.fout() * 1.2f + 0.5f);

                                randLenVectors(e.id, 7, 25f * e.finpow(), e.rotation, 50f, (x, y) -> {
                                    lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fin() * 5f + 2f);
                                });
                            }
                        }), new Effect(21f, e -> {
                            color(Pal.lancerLaser);


                            for(int i : Mathf.signs){
                                float rot = rand.random(0, 360f);
                                Drawf.tri(e.x, e.y, 4f * e.fout(), 120f, rot * i);
                            }
                        })
                        );
                    }};
                }});
            }};
        }};
    }
}
