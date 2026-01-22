package trs.content;

import arc.graphics.Color;
import mindustry.content.StatusEffects;
import mindustry.type.Weather;
import mindustry.world.meta.Attribute;
import trs.type.weather.DamageWeather;
import trs.type.weather.thunderstorm;

public class trsWeathers {
    public static Weather
    hail,hailstorm,thunderstorm;

    public static void load(){
        hail = new DamageWeather("hail"){{
            particleRegion = "particle";
            sizeMax = 5f;
            sizeMin = 2.6f;
            density = 1200f;
            attrs.set(Attribute.light, -0.15f);
            color = Color.white;
            baseSpeed = 35f;
            force = 0.1f;
            yspeed = -20f;
            xspeed = 2.5f;
            status = StatusEffects.wet;
            damage = 40;

            soundVol = 0f;
            soundVolOscMag = 1.5f;
            soundVolOscScl = 1100f;
            soundVolMin = 0.02f;
        }};
        hailstorm = new DamageWeather("hailstorm"){{
            particleRegion = "particle";
            sizeMax = 17f;
            sizeMin = 8f;
            density = 1800f;
            attrs.set(Attribute.light, -0.15f);
            color = Color.white;
            baseSpeed = 35f;
            force = 0.1f;
            yspeed = -20f;
            xspeed = 2.5f;
            status = StatusEffects.wet;
            damage = 80;

            soundVol = 0f;
            soundVolOscMag = 1.5f;
            soundVolOscScl = 1100f;
            soundVolMin = 0.02f;
        }};
        thunderstorm = new thunderstorm("thunderstorm"){{
            attrs.set(Attribute.light, -0.2f);
            attrs.set(Attribute.water, 0.2f);
            status = StatusEffects.wet;
            soundVol = 0.25f;
        }};
    }
}
