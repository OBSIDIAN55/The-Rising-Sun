package trs.ui;

import arc.Events;
import arc.scene.event.Touchable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.content.TechTree.TechNode;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.ui.Styles;
import trs.content.FulgeraTechTree;

/**
 * Панель выбора категорий для стандартного дерева технологий.
 * При выборе категории ресурсов открывает круговое древо вместо стандартного
 * вида.
 */
public class TechTreeCategoryBar {
    private static Table bar;

    private TechTreeCategoryBar() {
    }

    public static void init() {
        Events.on(ClientLoadEvent.class, e -> build());
    }

    private static void build() {
        if (bar != null)
            return;
        if (Vars.ui == null || Vars.ui.hudGroup == null || FulgeraTechTree.categoryRoots == null)
            return;

        bar = new Table();
        bar.setFillParent(true);
        bar.top().left().margin(12f);
        bar.touchable = Touchable.childrenOnly;
        bar.visible(() -> Vars.ui.research != null && Vars.ui.research.isShown());

        bar.table(t -> {
            t.defaults().pad(3f);
            for (TechNode root : FulgeraTechTree.categoryRoots) {
                if (root == null || root.content == null || root.content.uiIcon == null)
                    continue;
                ImageButton b = new ImageButton(Styles.flati);
                b.resizeImage(26f);
                var drawable = new TextureRegionDrawable(root.content.uiIcon);
                b.getStyle().imageUp = drawable;
                b.getStyle().imageDown = drawable;
                b.getStyle().imageOver = drawable;
                b.getStyle().imageDisabled = drawable;

                String label = root.content.localizedName;
                Vars.ui.addDescTooltip(b, label);
                b.clicked(() -> onCategorySelected(root));
                t.add(b).size(48f);
            }
        });

        Vars.ui.hudGroup.addChild(bar);
        bar.toFront();
    }

    private static void onCategorySelected(TechNode root) {
        if (root == null || Vars.ui == null)
            return;
        if (root == FulgeraTechTree.itemsRoot) {
            if (Vars.ui.research != null)
                Vars.ui.research.hide();
            CircularTechTreeDialog.show(root);
            return;
        }

        if (Vars.ui.research != null) {
            Vars.ui.research.show();
        }
    }
}
