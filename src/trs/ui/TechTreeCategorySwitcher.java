package trs.ui;

import arc.Core;
import arc.Events;
import mindustry.Vars;
import mindustry.game.EventType;

public class TechTreeCategorySwitcher {
    public static void init() {
        // Инициализируем кастомный диалог дерева технологий
        Events.on(EventType.ClientLoadEvent.class, event -> {
            // Перехватываем открытие стандартного окна исследований
            // и заменяем его на кастомное
            Core.app.post(() -> {
                if (Vars.ui != null && Vars.ui.research != null) {
                    // Сохраняем ссылку на оригинальное окно
                    var originalResearch = Vars.ui.research;

                    // Перехватываем показ через обновление каждый кадр
                    Events.run(EventType.Trigger.update, () -> {
                        if (originalResearch != null && originalResearch.isShown()) {
                            // Закрываем стандартное окно
                            originalResearch.hide();
                            // Открываем кастомное
                            CustomTechTreeDialog.showDialog();
                        }
                    });
                }
            });
        });
    }
}

