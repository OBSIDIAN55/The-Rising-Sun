package trs.ui;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.scene.event.Touchable;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.TechTree.TechNode;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import trs.content.FulgeraTechTree;

public class CustomTechTreeDialog extends BaseDialog {
    private static CustomTechTreeDialog instance;

    private Stack nodeStack;
    private Table nodeContainer;

    public CustomTechTreeDialog() {
        super("Дерево технологий");
        setup();
    }

    private void setup() {
        cont.clear();

        // Добавляем кнопку закрытия
        addCloseButton();

        // Контейнер для узлов дерева (используем Stack для абсолютного
        // позиционирования)
        nodeStack = new Stack();
        nodeStack.setFillParent(true);
        nodeStack.touchable = Touchable.enabled;

        nodeContainer = new Table();
        nodeContainer.setFillParent(true);
        nodeContainer.touchable = Touchable.disabled;

        nodeStack.add(nodeContainer);

        // Основной контейнер - занимает весь экран
        cont.add(nodeStack).grow();

        // Строим дерево
        rebuildNodes();
    }

    private void rebuildNodes() {
        // Очищаем все дочерние элементы кроме фонового контейнера
        if (nodeStack != null) {
            nodeStack.clearChildren();
            nodeStack.add(nodeContainer);
        }

        // Очищаем список соединений
        connectionElements.clear();

        // Используем первый доступный корень категории для отображения
        // Показываем все узлы из категории "Защита" как пример
        mindustry.content.TechTree.TechNode root = FulgeraTechTree.defenseRoot;

        // Если defenseRoot null, пробуем другие категории
        if (root == null) {
            root = FulgeraTechTree.turretsRoot;
        }
        if (root == null) {
            root = FulgeraTechTree.productionRoot;
        }
        if (root == null) {
            root = FulgeraTechTree.distributionRoot;
        }

        if (root == null) {
            return;
        }

        // Сохраняем root в финальную переменную для использования в лямбде
        final mindustry.content.TechTree.TechNode finalRoot = root;

        // Ждем следующего кадра, чтобы контейнер успел получить размеры
        Core.app.post(() -> {
            Core.app.post(() -> {
                if (nodeStack != null) {
                    float width = nodeStack.getWidth();
                    float height = nodeStack.getHeight();

                    // Если размеры еще не установлены, используем размеры по умолчанию
                    if (width <= 0 || height <= 0) {
                        width = Core.graphics.getWidth();
                        height = Core.graphics.getHeight();
                    }

                    // Сначала создаем все линии и узлы, потом добавляем в правильном порядке
                    drawRadialTree(finalRoot);

                    // Добавляем все линии после nodeContainer
                    for (arc.scene.Element line : connectionElements) {
                        nodeStack.add(line);
                    }
                }
            });
        });
    }

    private void drawRadialTree(TechNode root) {
        if (root == null || nodeStack == null)
            return;

        // Получаем размеры контейнера
        float containerWidth = nodeStack.getWidth();
        float containerHeight = nodeStack.getHeight();

        // Если размеры еще не установлены, используем значения по умолчанию
        if (containerWidth <= 0 || containerHeight <= 0) {
            containerWidth = 800f;
            containerHeight = 600f;
        }

        // Используем координаты относительно контейнера диалога
        // Центр экрана
        float centerX = containerWidth / 2f;
        float centerY = containerHeight / 2f;
        // Используем большую часть экрана для размещения узлов
        float baseRadius = Math.min(centerX, centerY) * 0.4f;

        // Отладочная информация
        // System.out.println("Container size: " + containerWidth + "x" +
        // containerHeight);
        // System.out.println("Center: " + centerX + ", " + centerY);
        // System.out.println("Base radius: " + baseRadius);

        // Сначала рисуем все соединения, потом узлы
        // Используем стандартный API Mindustry - поле children публичное
        arc.struct.Seq<TechNode> children = getChildren(root);

        // Отладочная информация
        if (children == null) {
            // Если children null, рисуем только корневой узел
            drawNode(root, centerX, centerY, 0, 0, true);
            return;
        }

        if (children.size == 0) {
            // Если нет дочерних узлов, рисуем только корневой узел
            drawNode(root, centerX, centerY, 0, 0, true);
            return;
        }

        // Сначала рисуем все соединения
        int childCount = children.size;
        float angleStep = 360f / childCount;

        for (int i = 0; i < childCount; i++) {
            TechNode child = children.get(i);
            float angle = i * angleStep;
            float rad = Mathf.degRad * angle;
            float x = centerX + Mathf.cos(rad) * baseRadius;
            float y = centerY + Mathf.sin(rad) * baseRadius;

            // Рисуем линию от центра к узлу (сначала линии)
            drawConnection(centerX, centerY, x, y);

            // Рекурсивно рисуем соединения для дочерних узлов
            drawChildConnections(child, x, y, 2, baseRadius * 1.5f, angle);
        }

        // Теперь рисуем узлы поверх линий
        // Рисуем корневой узел в центре
        drawNode(root, centerX, centerY, 0, 0, true);

        // Рисуем дочерние узлы
        for (int i = 0; i < childCount; i++) {
            TechNode child = children.get(i);
            if (child == null || child.content == null) {
                continue; // Пропускаем null узлы
            }

            float angle = i * angleStep;
            float rad = Mathf.degRad * angle;
            float x = centerX + Mathf.cos(rad) * baseRadius;
            float y = centerY + Mathf.sin(rad) * baseRadius;

            // Отладочная информация - выводим координаты каждого узла
            // System.out.println("Drawing child " + i + " at: " + x + ", " + y + " angle: "
            // + angle);

            // Рисуем узел с правильными координатами
            drawNode(child, x, y, 1, i, false);

            // Рекурсивно рисуем дочерние узлы
            drawChildNodes(child, x, y, 2, baseRadius * 1.5f, angle);
        }
    }

    private void drawChildConnections(TechNode node, float parentX, float parentY, int depth, float radius,
            float parentAngle) {
        if (node == null || depth > 3) {
            return;
        }

        arc.struct.Seq<TechNode> children = getChildren(node);
        if (children == null || children.size == 0) {
            return;
        }

        int childCount = children.size;
        float angleSpread = Math.min(60f, 30f * childCount);
        float angleStep = childCount > 1 ? angleSpread / (childCount - 1) : 0;
        float startAngle = parentAngle - angleSpread / 2f;

        for (int i = 0; i < childCount; i++) {
            TechNode child = children.get(i);
            float angle = startAngle + i * angleStep;
            float rad = Mathf.degRad * angle;
            float x = parentX + Mathf.cos(rad) * radius;
            float y = parentY + Mathf.sin(rad) * radius;

            // Рисуем линию от родителя к узлу
            drawConnection(parentX, parentY, x, y);

            // Рекурсивно рисуем соединения для дочерних узлов
            drawChildConnections(child, x, y, depth + 1, radius * 1.3f, angle);
        }
    }

    // Используем стандартный API Mindustry - TechNode имеет публичное поле children
    private arc.struct.Seq<TechNode> getChildren(TechNode node) {
        if (node == null)
            return null;

        // В Mindustry TechNode имеет публичное поле children
        // Пробуем прямой доступ
        try {
            arc.struct.Seq<TechNode> children = node.children;
            if (children != null) {
                return children;
            }
        } catch (Exception e) {
            // Игнорируем ошибки доступа
        }

        // Если прямой доступ не работает, возвращаем null
        return null;
    }

    private void drawChildNodes(TechNode node, float parentX, float parentY, int depth, float radius,
            float parentAngle) {
        if (node == null || depth > 3) {
            return;
        }

        arc.struct.Seq<TechNode> children = getChildren(node);
        if (children == null || children.size == 0) {
            return;
        }

        int childCount = children.size;
        float angleSpread = Math.min(60f, 30f * childCount);
        float angleStep = childCount > 1 ? angleSpread / (childCount - 1) : 0;
        float startAngle = parentAngle - angleSpread / 2f;

        for (int i = 0; i < childCount; i++) {
            TechNode child = children.get(i);
            float angle = startAngle + i * angleStep;
            float rad = Mathf.degRad * angle;
            float x = parentX + Mathf.cos(rad) * radius;
            float y = parentY + Mathf.sin(rad) * radius;

            // Рисуем линию от родителя к узлу
            drawConnection(parentX, parentY, x, y);

            // Рисуем узел
            drawNode(child, x, y, depth, i, false);

            // Рекурсивно рисуем дочерние узлы
            drawChildNodes(child, x, y, depth + 1, radius * 1.3f, angle);
        }
    }

    private final Seq<arc.scene.Element> connectionElements = new Seq<>();

    // Вспомогательный метод для рисования шестиугольника
    private void drawHexagon(float x, float y, float radius) {
        for (int i = 0; i < 6; i++) {
            float angle1 = (i * 60f - 90f) * Mathf.degRad;
            float angle2 = ((i + 1) % 6 * 60f - 90f) * Mathf.degRad;
            float x1 = x + Mathf.cos(angle1) * radius;
            float y1 = y + Mathf.sin(angle1) * radius;
            float x2 = x + Mathf.cos(angle2) * radius;
            float y2 = y + Mathf.sin(angle2) * radius;
            Lines.line(x1, y1, x2, y2);
        }
    }

    private void drawConnection(float x1, float y1, float x2, float y2) {
        if (nodeStack == null)
            return;

        // Создаем элемент для отрисовки линии с голографическим свечением
        final float fx1 = x1, fy1 = y1, fx2 = x2, fy2 = y2;
        arc.scene.Element lineElement = new arc.scene.Element() {
            @Override
            public void draw() {
                // Рисуем свечение вокруг линии
                Draw.color(new Color(0.2f, 0.6f, 1f, 0.3f));
                Lines.stroke(4f);
                Lines.line(fx1, fy1, fx2, fy2);

                // Основная линия
                Draw.color(new Color(0.4f, 0.8f, 1f, 0.8f));
                Lines.stroke(2f);
                Lines.line(fx1, fy1, fx2, fy2);

                Draw.reset();
            }
        };
        lineElement.setPosition(0, 0);
        lineElement.setSize(nodeStack.getWidth(), nodeStack.getHeight());
        lineElement.touchable = Touchable.disabled; // Линии не должны блокировать клики
        connectionElements.add(lineElement);
    }

    private void drawNode(TechNode node, float x, float y, int depth, int index, boolean isRoot) {
        if (node == null || node.content == null || nodeStack == null)
            return;

        float size = isRoot ? 80f : Math.max(40f, 60f - depth * 10f);
        boolean unlocked = node.content.unlocked();
        final float nodeSize = size;
        final boolean nodeUnlocked = unlocked;
        final CustomTechTreeDialog dialog = this; // Для доступа к drawHexagon

        // Отладочная информация - проверяем координаты
        // System.out.println("Drawing node at: " + x + ", " + y + " depth: " + depth);

        // Создаем кастомный элемент для отрисовки узла с шестиугольной формой
        arc.scene.Element nodeElement = new arc.scene.Element() {
            @Override
            public void draw() {
                // В методе draw() координаты относительно самого элемента
                // Центр элемента - это половина его размера
                float absX = getWidth() / 2f;
                float absY = getHeight() / 2f;
                float radius = nodeSize / 2f;

                // Рисуем свечение для разблокированных узлов
                if (nodeUnlocked) {
                    // Внешнее свечение
                    Draw.color(new Color(0.2f, 0.6f, 1f, 0.4f));
                    Lines.stroke(3f);
                    dialog.drawHexagon(absX, absY, radius * 1.2f);

                    // Среднее свечение
                    Draw.color(new Color(0.3f, 0.7f, 1f, 0.6f));
                    Lines.stroke(2f);
                    dialog.drawHexagon(absX, absY, radius * 1.1f);
                } else {
                    // Бледный контур для заблокированных
                    Draw.color(new Color(0.3f, 0.5f, 0.7f, 0.3f));
                    Lines.stroke(1.5f);
                    dialog.drawHexagon(absX, absY, radius * 1.05f);
                }

                // Фон узла (круг для простоты, можно заменить на шестиугольник)
                if (nodeUnlocked) {
                    Draw.color(new Color(0.1f, 0.2f, 0.3f, 0.8f));
                } else {
                    Draw.color(new Color(0.05f, 0.1f, 0.15f, 0.6f));
                }
                Fill.circle(absX, absY, radius);

                // Обводка узла (шестиугольник)
                if (nodeUnlocked) {
                    Draw.color(new Color(0.4f, 0.8f, 1f, 0.9f));
                } else {
                    Draw.color(new Color(0.3f, 0.5f, 0.7f, 0.5f));
                }
                Lines.stroke(2f);
                dialog.drawHexagon(absX, absY, radius);

                Draw.reset();
            }
        };
        // Устанавливаем позицию относительно контейнера
        // В Stack позиция устанавливается относительно родителя
        nodeElement.setPosition(x - size / 2, y - size / 2);
        nodeElement.setSize(size, size);
        nodeElement.touchable = Touchable.disabled; // Фон не должен блокировать клики

        // Отладочная информация
        // System.out.println("Node element position: " + (x - size / 2) + ", " + (y -
        // size / 2) + " size: " + size);

        // Создаем Table для иконки и интерактивности поверх узла
        Table nodeTable = new Table();
        nodeTable.touchable = Touchable.enabled;
        nodeTable.setBackground(null); // Прозрачный фон, так как фон рисуется в nodeElement

        // Иконка контента
        if (node.content.uiIcon != null) {
            arc.scene.ui.Image icon = new arc.scene.ui.Image(node.content.uiIcon);
            icon.setScaling(arc.util.Scaling.fit);
            // Делаем иконку белой для контраста
            icon.setColor(Color.white);
            nodeTable.add(icon).size(size * 0.6f);
        } else {
            // Если иконки нет, показываем текст
            Label textLabel = new Label(node.content.localizedName, Styles.outlineLabel);
            textLabel.setColor(Color.white);
            textLabel.setFontScale(0.4f);
            nodeTable.add(textLabel).width(size * 0.9f).wrap();
        }

        // Индикатор замка для заблокированных узлов
        if (!unlocked && !canUnlock(node)) {
            if (Icon.lock != null) {
                arc.scene.ui.Image lockIcon = new arc.scene.ui.Image(Icon.lock);
                lockIcon.setColor(new Color(0.7f, 0.7f, 0.7f, 0.8f));
                lockIcon.setScaling(arc.util.Scaling.fit);
                nodeTable.add(lockIcon).size(size * 0.3f).padTop(-size * 0.4f);
            }
        }

        // Устанавливаем позицию относительно контейнера
        // В Stack позиция устанавливается относительно родителя
        nodeTable.setPosition(x - size / 2, y - size / 2);
        nodeTable.setSize(size, size);
        nodeTable.pack();

        // Отладочная информация
        // System.out.println("Node table position: " + (x - size / 2) + ", " + (y -
        // size / 2) + " size: " + size);

        // Убеждаемся, что таблица кликабельна и на переднем плане
        nodeTable.toFront();

        // Обработчик клика
        nodeTable.clicked(() -> {
            if (!unlocked && canUnlock(node)) {
                unlockNode(node);
            }
        });

        // Подсказка
        Vars.ui.addDescTooltip(nodeTable, node.content.localizedName);

        // Добавляем сначала элемент с фоном, потом таблицу с иконкой (таблица должна
        // быть поверх)
        nodeStack.add(nodeElement);
        nodeStack.add(nodeTable);
        nodeTable.toFront(); // Убеждаемся, что таблица поверх элемента
    }

    private boolean canUnlock(TechNode node) {
        // Проверяем, можно ли разблокировать узел
        if (node.parent != null && !node.parent.content.unlocked()) {
            return false;
        }
        // Проверяем, что контент еще не разблокирован
        if (node.content.unlocked()) {
            return false;
        }
        // Проверяем требования - проверяем, что все родители разблокированы
        TechNode parent = node.parent;
        while (parent != null) {
            if (!parent.content.unlocked()) {
                return false;
            }
            parent = parent.parent;
        }
        return true;
    }

    private void unlockNode(TechNode node) {
        if (node.content != null && canUnlock(node)) {
            node.content.unlock();
            rebuildNodes();
        }
    }

    public static void showDialog() {
        if (instance == null) {
            instance = new CustomTechTreeDialog();
        }
        instance.show();
    }
}
