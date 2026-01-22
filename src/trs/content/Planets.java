package trs.content;

import arc.graphics.Color;
import mindustry.game.Team;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.graphics.g3d.NoiseMesh;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.type.Planet;

public class Planets {
    public static Planet
    fulgera;

    public static void load(){
        fulgera = new Planet("fulgera",mindustry.content.Planets.sun, 1.3f,2){{
            meshLoader = ()-> new MultiMesh(
                    new NoiseMesh(this,7,5,1.229f,4,1.1f,1,1,Color.valueOf("587d89"),Color.valueOf("5e95a8"),1,1,1,1),
                    new NoiseMesh(this,94,5,1.22f,4,0.6f,1,1,Color.valueOf("e9e9de"),Color.valueOf("c2c1b4"),1,1,1,1),
                    new NoiseMesh(this,101,6,1.2441f,5,0.8f,1,1,Color.valueOf("dcd67e"),Color.valueOf("d0c95b"),1,1,1,1),
                    new NoiseMesh(this,69,5,1.212f,4,1,0.75f,1,Color.valueOf("b8dc7e"),Color.valueOf("3e5d54"),1,1,1,1),
                    new NoiseMesh(this,19,5,1.247f,4,1.1f,1,1,Color.valueOf("f8efe6"),Color.valueOf("6d685a"),1,1,1,1),
                    new NoiseMesh(this,61,6,1.248f,4,1.1f,1,1,Color.valueOf("6fc9b5"),Color.valueOf("49602c"),1,1,1,1),
                    new NoiseMesh(this,17,6,1.2514f,6,0.9f,1,1,Color.valueOf("91501d"),Color.valueOf("731236"),1,1,1,1)
            );
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 11, 0.15f, 0.13f, 5, Color.white.a(0.75f), 2, 0.45f, 0.9f, 0.38f),
                    new HexSkyMesh(this, 1, 0.6f, 0.16f, 5, Color.white.a(0.75f), 2, 0.45f, 1f, 0.41f)
            );
            generator = new SerpuloPlanetGenerator() ;
            launchCapacityMultiplier = 0.5f;
            sectorSeed = 2;
            allowWaves = true;
            allowSectorInvasion = true;
            enemyCoreSpawnReplace = true;
            allowLaunchLoadout = true;
            prebuildBase = true;
            allowLaunchSchematics = false;
            clearSectorOnLose = true;
            defaultCore = trsBlocks.Case;
            ruleSetter = r -> {
                r.waveTeam = Team.malis;
                r.placeRangeCheck = false;
                r.showSpawns = true;
                r.coreDestroyClear = true;
            };
            allowLaunchToNumbered = false;
            iconColor = Color.valueOf("4abeff");
            atmosphereColor = Color.valueOf("3650d8");
            atmosphereRadIn = 0.02f;
            atmosphereRadOut = 0.3f;
            startSector = 0;
            alwaysUnlocked = true;
            landCloudColor = Pal.spore.cpy().a(0.5f);

            unlockedOnLand.add(trsBlocks.Case);
        }};
    }
}
