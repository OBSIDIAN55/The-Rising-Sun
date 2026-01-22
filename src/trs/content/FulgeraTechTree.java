package trs.content;

import static mindustry.content.TechTree.*;

import arc.struct.Seq;
import mindustry.content.TechTree.TechNode;

public class FulgeraTechTree {
    // Хранилище для независимых деревьев категорий
    public static Seq<TechNode> categoryRoots = new Seq<>();

    public static TechNode defenseRoot,
            turretsRoot,
            productionRoot,
            distributionRoot,
            powerRoot,
            unitsRoot,
            itemsRoot,
            fractionsRoot;

    public static void load() {
        // Сначала создаем все категории как независимые деревья
        // Затем они будут добавлены в главное дерево как дочерние узлы

        itemsRoot = nodeRoot("items", trsBlocks.Case, () -> {
            nodeRoot("fractions", Planets.fulgera, () -> {
            node(trsBlocks.acronyx);
            node(trsBlocks.arha);
            node(trsBlocks.hronos);
            node(trsBlocks.phoenix);
            });
            node(trsBlocks.splash, () -> {
                node(trsBlocks.ash);
                node(trsBlocks.lucidity);
                node(trsBlocks.hallucination);
            });
            node(trsBlocks.clinovalveWall, () -> {
                node(trsBlocks.clinovalveWallLarge, () -> {
                    // Ветка 1: Металлические стены
                    node(trsBlocks.zincWall, () -> {
                        node(trsBlocks.zincWallLarge, () -> {
                            node(trsBlocks.steelWall, () -> {
                                node(trsBlocks.steelWallLarge, () -> {
                                    node(trsBlocks.exacrimWall, () -> {
                                        node(trsBlocks.exacrimWallLarge);
                                    });
                                });
                            });
                        });
                    });
                    // Ветка 2: Углеродные стены
                    node(trsBlocks.carbonWall, () -> {
                        node(trsBlocks.carbonWallLarge);
                    });
                });
            });
            node(trsBlocks.carbonBiomassReactor, () -> {
                node(trsBlocks.variableNode, () -> {
                    node(trsBlocks.largeVariableNode);
                });
            });
            node(trsBlocks.componentsFactory, () -> {
                node(trsBlocks.clinovalvePayloadConveyor, () -> {
                    node(trsBlocks.clinovalvePayloadRouter, () -> {
                        node(trsBlocks.universalCollectorUnits);
                    });
                });
            });
            node(trsBlocks.hydraulicDrill, () -> {
                // Буры
                node(trsBlocks.deepDrill, () -> {
                    node(trsBlocks.clusterDrill);
                });
                // Фабрики
                node(trsBlocks.melter, () -> {
                    node(trsBlocks.brazier);
                    node(trsBlocks.rubidiumSmelter);
                    node(trsBlocks.atmosphericCondenser);
                    node(trsBlocks.crusher, () -> {
                        node(trsBlocks.carbonGlassClin);
                    });
                });
            });
        
            node(trsBlocks.clinovalveDuct, () -> {
                node(trsBlocks.clinovalveRouter, () -> {
                    node(trsBlocks.clinovalveSorter);
                    node(trsBlocks.clinovalveInvertedSorter);
                    node(trsBlocks.clinovalveOverflowGate);
                    node(trsBlocks.clinovalveUnderflowGate);
                });
                node(trsBlocks.clinovalveJunction);
                node(trsBlocks.clinovalveDuctBridge);
            });
            node(trsItems.tin, () -> {
            // Ветка 1: Кварц
            node(trsItems.quartz, () -> {
                node(trsItems.quartzDust);
            });
            // Ветка 2: Биомасса и углерод
            node(trsItems.biomass, () -> {
                node(trsItems.carbon, () -> {
                    node(trsItems.carbonDust, () -> {
                        node(trsItems.carbonGlass);
                    });
                });
            });
            // Ветка 3: Металлы
                node(trsItems.clinovalve, () -> {
                    node(trsItems.zinc, () -> {
                        node(trsItems.rubidium, () -> {
                            node(trsItems.barium, () -> {
                                node(trsItems.steel, () -> {
                                    node(trsItems.chrome);
                                });
                            });
                        });
                    });
                });
            });
        });

        // Добавляем все корни в список категорий
        categoryRoots.addAll(
                defenseRoot,
                turretsRoot,
                productionRoot,
                distributionRoot,
                powerRoot,
                unitsRoot,
                itemsRoot,
                fractionsRoot);

    }
}
