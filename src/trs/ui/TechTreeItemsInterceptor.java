package trs.ui;

import arc.Events;
import arc.util.Log;
import arc.util.Reflect;
import java.lang.reflect.Field;
import mindustry.Vars;
import mindustry.content.TechTree.TechNode;
import mindustry.game.EventType.Trigger;
import trs.content.FulgeraTechTree;

/**
 * Перехватывает выбор категории ресурсов в стандартном TechTree
 * и показывает круговое древо вместо обычного вида.
 */
public class TechTreeItemsInterceptor {
    private static boolean handledItems;
    private static boolean scannedOnce;
    private static Field nodeField;
    private static int failStreak;

    private TechTreeItemsInterceptor() {
    }

    public static void init() {
        Events.run(Trigger.update, TechTreeItemsInterceptor::tick);
    }

    private static void tick() {
        if (Vars.ui == null || Vars.ui.research == null)
            return;

        boolean researchShown = Vars.ui.research.isShown();
        if (!researchShown) {
            handledItems = false;
            return;
        }

        TechNode current = getCurrentNode();
        TechNode itemsRoot = FulgeraTechTree.itemsRoot;
        if (current == null || itemsRoot == null)
            return;

        boolean inItems = belongsTo(current, itemsRoot);
        if (inItems && !handledItems) {
            handledItems = true;
            Vars.ui.research.hide();
            CircularTechTreeDialog.show(itemsRoot);
        } else if (!inItems) {
            handledItems = false;
        }
    }

    private static TechNode getCurrentNode() {
        try {
            return Reflect.get(Vars.ui.research, "node");
        } catch (Throwable ignored) {
            // возможно поле переименовано
        }
        try {
            return Reflect.get(Vars.ui.research, "selected");
        } catch (Throwable ignored) {
        }
        // попробовать запомненное поле
        TechNode byField = tryField();
        if (byField != null)
            return byField;

        // одноразовый перебор полей
        if (!scannedOnce) {
            scannedOnce = true;
            var fields = Vars.ui.research.getClass().getDeclaredFields();
            for (var f : fields) {
                if (!TechNode.class.isAssignableFrom(f.getType()))
                    continue;
                f.setAccessible(true);
                nodeField = f;
                TechNode found = tryField();
                if (found != null)
                    return found;
            }
        }

        failStreak++;
        if (failStreak % 180 == 0) {
            Log.warn(
                    "TechTreeItemsInterceptor: не удалось получить текущий узел TechTree, перехват временно не работает.");
        }
        return null;
    }

    private static TechNode tryField() {
        if (nodeField == null)
            return null;
        try {
            Object val = nodeField.get(Vars.ui.research);
            if (val instanceof TechNode tn) {
                failStreak = 0;
                return tn;
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static boolean belongsTo(TechNode node, TechNode targetRoot) {
        TechNode cursor = node;
        while (cursor != null) {
            if (cursor == targetRoot)
                return true;
            cursor = cursor.parent;
        }
        return false;
    }
}
