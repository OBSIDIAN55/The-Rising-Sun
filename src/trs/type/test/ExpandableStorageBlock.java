package trs.type.test;

import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.StorageBlock;

public class ExpandableStorageBlock extends StorageBlock {
    public int maxClusterBlocks = 16; // лимит на объединение, если потребуется
    public float minModifier = 0.1f;
    public float weakeningFactor = 0.9f; // пока не используется, на будущее

    public ExpandableStorageBlock(String name) {
        super(name);
        update = true;
    }

    public class ExpandableStorageBlockBuild extends StorageBuild {
        /** Ссылка на главный блок группы. Если null — этот блок мастер. */
        public ExpandableStorageBlockBuild masterBlock = null;
        // Флаг для избежания бесконечного цикла при автосборке
        public boolean isRebuilding = false;

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();
            updateMasterBlock();
        }

        // Обновление логики основного блока кластера
        public void updateMasterBlock(){
            if(isRebuilding) return;
            isRebuilding = true;
            ExpandableStorageBlockBuild foundMaster = null;
            int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {-1,1}, {1,-1}, {-1,-1}};
            // ищем внешний мастер
            for(var d : dirs){
                Tile t = mindustry.Vars.world.tile(tile.x + d[0], tile.y + d[1]);
                if(t != null && t.build instanceof ExpandableStorageBlockBuild neighbor && neighbor != this){
                    ExpandableStorageBlockBuild neighborMaster = neighbor.getMaster();
                    if(neighborMaster != null){
                        foundMaster = neighborMaster;
                        break;
                    }
                }
            }
            if(foundMaster != null && foundMaster != this){
                // Переносим предметы в нового мастера
                if(this.items.total() > 0){
                    for(mindustry.type.Item item : mindustry.Vars.content.items()){
                        int count = this.items.get(item);
                        if(count > 0){
                            foundMaster.items.add(item, count);
                            this.items.remove(item, count);
                        }
                    }
                }
                foundMaster = foundMaster.getMaster(); // вдруг выбранный — не финальный мастер
            }else{
                foundMaster = this;
            }
            // Теперь проводим обход всей группы и синхронизируем всем настоящий мастер и items
            var visited = new arc.struct.Seq<ExpandableStorageBlockBuild>();
            var queue = new arc.struct.Seq<ExpandableStorageBlockBuild>();
            queue.add(foundMaster);
            while(queue.size > 0){
                ExpandableStorageBlockBuild curr = queue.pop();
                if(visited.contains(curr)) continue;
                visited.add(curr);
                curr.masterBlock = foundMaster;
                curr.items = foundMaster.items;
                // ищем всех смежных ExpandableStorageBlockBuild
                for(var d : dirs){
                    Tile t = mindustry.Vars.world.tile(curr.tile.x + d[0], curr.tile.y + d[1]);
                    if(t != null && t.build instanceof ExpandableStorageBlockBuild next && !visited.contains(next)){
                        if(next.getMaster() != foundMaster){
                            queue.add(next);
                        }
                    }
                }
            }
            isRebuilding = false;
        }

        // Получить главный блок: если ссылка невалидна или self, вернуть self
        public ExpandableStorageBlockBuild getMaster(){
            return (masterBlock == null) ? this : masterBlock;
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            var master = getMaster();
            if(master != this) return master.acceptItem(source, item);
            return super.acceptItem(source, item);
        }
        @Override
        public void handleItem(Building source, Item item){
            var master = getMaster();
            if(master != this){ master.handleItem(source, item); return; }
            super.handleItem(source, item);
        }
        @Override
        public int getMaximumAccepted(Item item){
            var master = getMaster();
            return (master == this) ? super.getMaximumAccepted(item) : master.getMaximumAccepted(item);
        }
        @Override
        public int removeStack(Item item, int amount){
            var master = getMaster();
            return (master == this) ? super.removeStack(item, amount) : master.removeStack(item, amount);
        }

        @Override
        public void onRemoved(){
            super.onRemoved();
            masterBlock = null;
            // пересобираем кластеры рядом стоящих блоков
            int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {-1,1}, {1,-1}, {-1,-1}};
            for(var d : dirs){
                Tile t = mindustry.Vars.world.tile(tile.x + d[0], tile.y + d[1]);
                if(t != null && t.build instanceof ExpandableStorageBlockBuild neighbor && neighbor != this){
                    neighbor.masterBlock = null;
                    neighbor.updateMasterBlock();
                }
            }
        }
    }
}
