package trs.ui;

import arc.Events;
import arc.scene.event.Touchable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.content.TechTree.TechNode;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import trs.content.FulgeraTechTree;

/**
 * Кнопка на HUD, открывающая круговое древо ресурсов (категория items).
 */
public class ResourceTreeButton {
    private static Table container;

    private ResourceTreeButton() {
    }

    public static void init() {
        Events.on(ClientLoadEvent.class, e -> ensureButton());
    }

    private static void ensureButton() {
        if (container != null)
            return;
        if (Vars.ui == null || Vars.ui.hudGroup == null)
            return;

        container = new Table();
        container.setFillParent(true);
        container.top().right().margin(6f);
        container.touchable = Touchable.childrenOnly;

        ImageButton button = new ImageButton(Styles.flati);
        button.resizeImage(22f);
        button.getStyle().imageUp = Icon.book;
        button.getStyle().imageDown = Icon.book;
        button.getStyle().imageOver = Icon.book;
        button.getStyle().imageDisabled = Icon.book;
        button.clicked(ResourceTreeButton::openDialog);

        Vars.ui.addDescTooltip(button, "Круговое древо: ресурсы");

        container.add(button).size(42f);
        Vars.ui.hudGroup.addChild(container);
        container.toFront();
    }

    private static void openDialog() {
        TechNode root = FulgeraTechTree.itemsRoot;
        if (root == null) {
            Vars.ui.showErrorMessage("Дерево ресурсов не загружено.");
            return;
        }
        CircularTechTreeDialog.show(root);
    }
}
