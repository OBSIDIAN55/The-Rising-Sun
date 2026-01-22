package trs.ui;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.input.KeyCode;
import arc.math.Angles;
import arc.math.Mathf;
import arc.scene.event.InputListener;
import arc.scene.ui.Image;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.scene.ui.layout.WidgetGroup;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Scaling;
import mindustry.Vars;
import mindustry.content.TechTree.TechNode;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

/**
 * Простая диаграмма «кругового» древа технологий.
 * Визуализирует один корневой узел {@link TechNode} и его потомков по слоям
 * вокруг центра.
 * Можно вызывать, например:
 * {@code CircularTechTreeDialog.show(FulgeraTechTree.itemsRoot);}
 */
public class CircularTechTreeDialog {
    private static final float canvasSize = 920f;
    private static final float baseRadius = 70f;
    private static final float ringStep = 50f;
    private static final float nodeSize = 20f;
    private static final Color lockedColor = Pal.darkerMetal;
    private static final Color lineColor = Pal.accent.cpy().mul(1f, 1f, 1f, 0.5f);
    private static final Color gridColor = Pal.accent.cpy().mul(1f, 1f, 1f, 0.08f);
    private static final float sectorPadding = 6f;

    private static BaseDialog dialog;
    private static RadialTree radialTree;
    private static Label title;

    private CircularTechTreeDialog() {
    }

    /**
     * Показать круговое дерево для выбранной категории.
     *
     * @param root корневой узел категории (например,
     *             {@code FulgeraTechTree.itemsRoot})
     */
    public static void show(@Nullable TechNode root) {
        if (root == null || Vars.ui == null) {
            return;
        }
        ensureDialog();
        radialTree.setRoot(root);
        Core.scene.setScrollFocus(radialTree);
        UnlockableContent content = root.content;
        String caption = content != null ? content.localizedName : "Категория";
        title.setText("Категория: " + caption);
        dialog.show();
    }

    private static void ensureDialog() {
        if (dialog != null)
            return;

        dialog = new BaseDialog("Круговое древо технологий");
        radialTree = new RadialTree();
        title = dialog.cont.add("Категория").padTop(6f).style(Styles.outlineLabel).get();
        dialog.cont.row();
        dialog.cont.add(radialTree).size(canvasSize).pad(12f).grow();
        dialog.buttons.button("@close", dialog::hide).size(200f, 54f);
        dialog.addListener(new InputListener() {
            @Override
            public boolean scrolled(arc.scene.event.InputEvent event, float x, float y, float amountX, float amountY) {
                if (radialTree != null)
                    radialTree.applyScroll(amountY);
                return true;
            }

            @Override
            public boolean touchDown(arc.scene.event.InputEvent event, float x, float y, int pointer, KeyCode button) {
                if (radialTree == null || button != KeyCode.mouseLeft)
                    return false;
                radialTree.beginDrag(event.stageX, event.stageY);
                return true;
            }

            @Override
            public void touchDragged(arc.scene.event.InputEvent event, float x, float y, int pointer) {
                if (radialTree == null)
                    return;
                radialTree.dragTo(event.stageX, event.stageY);
            }

            @Override
            public void touchUp(arc.scene.event.InputEvent event, float x, float y, int pointer, KeyCode button) {
                if (radialTree == null)
                    return;
                if (button == KeyCode.mouseLeft)
                    radialTree.endDrag();
            }
        });
    }

    public static boolean isShown() {
        return dialog != null && dialog.isShown();
    }

    /**
     * Виджет, который раскладывает {@link TechNode} по кольцам.
     */
    private static final class RadialTree extends WidgetGroup {
        private final Seq<NodeEntry> entries = new Seq<>();
        private final ObjectMap<TechNode, NodeEntry> byNode = new ObjectMap<>();
        private TechNode root;
        private float centerX;
        private float centerY;
        private int maxDepth;
        private int topCount;
        private float zoom = 1f;
        private float panX, panY;
        private float lastX, lastY;
        private boolean dragging;

        RadialTree() {
            setSize(canvasSize, canvasSize);
            setTransform(false);
            addInputHandler();
        }

        void setRoot(TechNode root) {
            this.root = root;
            rebuild();
        }

        private void rebuild() {
            clearChildren();
            entries.clear();
            byNode.clear();
            maxDepth = 0;
            topCount = 0;
            if (root == null)
                return;

            centerX = canvasSize / 2f;
            centerY = canvasSize / 2f;

            collect(root, 0);
            placeRadial();
            refreshPositions();
        }

        private void collect(TechNode node, int depth) {
            NodeEntry entry = new NodeEntry(node, depth);
            entries.add(entry);
            byNode.put(node, entry);
            maxDepth = Math.max(maxDepth, depth);
            for (TechNode child : node.children) {
                collect(child, depth + 1);
            }
        }

        private void placeRadial() {
            NodeEntry center = byNode.get(root);
            if (center != null) {
                placeNode(center, centerX, centerY);
            }

            Seq<TechNode> top = root.children;
            topCount = top.size;
            if (top.isEmpty())
                return;

            float sector = 360f / top.size;
            float offset = -90f - sector / 2f + sectorPadding;
            for (int i = 0; i < top.size; i++) {
                float start = offset + sector * i;
                float end = start + sector - sectorPadding * 2f;
                placeSector(top.get(i), 1, start, end);
            }
        }

        private void placeSector(TechNode node, int depth, float start, float end) {
            float radius = baseRadius + ringStep * (depth - 1);
            float mid = (start + end) / 2f;
            NodeEntry entry = byNode.get(node);
            if (entry != null) {
                float x = centerX + Angles.trnsx(mid, radius);
                float y = centerY + Angles.trnsy(mid, radius);
                placeNode(entry, x, y);
            }

            if (node.children.isEmpty())
                return;

            int count = node.children.size;
            float childRadius = baseRadius + ringStep * depth;
            float minStep = minAngleForRadius(childRadius);
            if (count == 1) {
                float childMid = clampAngle(mid, start, end);
                placeSector(node.children.first(), depth + 1, childMid - minStep / 2f, childMid + minStep / 2f);
                return;
            }

            float sectorSpan = end - start;
            float borderPad = minStep * 0.6f;
            float usableSpan = Math.max(minStep, sectorSpan - borderPad * 2f);
            float step = Math.max(minStep, usableSpan / (count - 1));
            float fan = step * (count - 1);

            // центрируем внутри сектора
            float baseStart = mid - fan / 2f;
            baseStart = Math.max(start + borderPad, baseStart);
            float baseEnd = baseStart + fan;
            if (baseEnd > end - borderPad) {
                baseStart -= (baseEnd - (end - borderPad));
                baseEnd = baseStart + fan;
            }

            for (int i = 0; i < count; i++) {
                float childAngle = baseStart + step * i;
                float childStart = Math.max(start, childAngle - step * 0.45f);
                float childEnd = Math.min(end, childAngle + step * 0.45f);
                if (childEnd - childStart < minStep * 0.6f) {
                    float midChild = (childStart + childEnd) / 2f;
                    childStart = midChild - minStep * 0.3f;
                    childEnd = midChild + minStep * 0.3f;
                }
                placeSector(node.children.get(i), depth + 1, childStart, childEnd);
            }
        }

        private float clampAngle(float value, float start, float end) {
            if (value < start)
                return start;
            if (value > end)
                return end;
            return value;
        }

        private float minAngleForRadius(float radius) {
            // минимальный угол, чтобы иконки не перекрывались (грубая оценка)
            float arc = nodeSize + 8f;
            return Math.max(4f, (arc / Math.max(1f, radius)) * 57.29578f);
        }

        private float localX(float logicalX) {
            return centerX + (logicalX - centerX + panX) * zoom;
        }

        private float localY(float logicalY) {
            return centerY + (logicalY - centerY + panY) * zoom;
        }

        private float worldX(float logicalX) {
            return x + localX(logicalX);
        }

        private float worldY(float logicalY) {
            return y + localY(logicalY);
        }

        private void positionView(NodeEntry entry) {
            if (entry.view == null)
                return;
            float cardSize = nodeSize * zoom;
            float iconSize = nodeSize * (1f + (zoom - 1f) * 0.35f);
            entry.width = cardSize;
            entry.height = cardSize;
            entry.x = localX(entry.cx) - cardSize / 2f;
            entry.y = localY(entry.cy) - cardSize / 2f;
            entry.view.setSize(cardSize, cardSize);
            entry.view.setPosition(entry.x, entry.y);
            if (entry.iconStack != null) {
                entry.iconStack.setSize(iconSize, iconSize);
                entry.iconStack.setPosition((cardSize - iconSize) / 2f, (cardSize - iconSize) / 2f);
                if (entry.iconStack.getChildren().size > 0 && entry.iconStack.getChildren().first() instanceof Image icon) {
                    icon.setSize(iconSize, iconSize);
                    icon.setPosition(0f, 0f);
                }
            }
        }

        private void refreshPositions() {
            for (NodeEntry entry : entries) {
                positionView(entry);
            }
        }

        private void addInputHandler() {
            addListener(new arc.scene.event.InputListener() {
                @Override
                public boolean scrolled(arc.scene.event.InputEvent event, float x, float y, float amountX,
                        float amountY) {
                    applyScroll(amountY);
                    return true;
                }

                @Override
                public boolean touchDown(arc.scene.event.InputEvent event, float x, float y, int pointer,
                        KeyCode button) {
                    if (button != KeyCode.mouseLeft)
                        return false;
                    Core.scene.setScrollFocus(RadialTree.this);
                    beginDrag(event.stageX, event.stageY);
                    return true;
                }

                @Override
                public void touchDragged(arc.scene.event.InputEvent event, float x, float y, int pointer) {
                    dragTo(event.stageX, event.stageY);
                }

                @Override
                public void touchUp(arc.scene.event.InputEvent event, float x, float y, int pointer, KeyCode button) {
                    if (button == KeyCode.mouseLeft) {
                        endDrag();
                    }
                }
            });
        }

        private void applyScroll(float amountY) {
            float delta = amountY * 0.02f;
            zoom = Mathf.clamp(zoom - delta, 0.5f, 2.5f);
            refreshPositions();
            Core.scene.setScrollFocus(this);
        }

        private void beginDrag(float sx, float sy) {
            dragging = true;
            lastX = sx;
            lastY = sy;
        }

        private void dragTo(float sx, float sy) {
            if (!dragging)
                return;
            float dx = sx - lastX;
            float dy = sy - lastY;
            panX += dx / zoom;
            panY += dy / zoom;
            lastX = sx;
            lastY = sy;
            refreshPositions();
        }

        private void endDrag() {
            dragging = false;
        }

        private void placeNode(NodeEntry entry, float cx, float cy) {
            Table view = buildNodeView(entry.node);
            view.pack();
            if (view.getChildren().size > 0 && view.getChildren().first() instanceof Stack stack) {
                entry.iconStack = stack;
            }
            entry.cx = cx;
            entry.cy = cy;
            entry.view = view;
            positionView(entry);
            addChild(view);
        }

        private Table buildNodeView(TechNode node) {
            UnlockableContent content = node.content;
            boolean unlocked = content != null && content.unlocked();

            Table card = new Table(Styles.grayPanel);
            card.margin(4f);
            card.defaults().pad(1.5f);

            Stack iconStack = new Stack();
            Image icon = new Image(content != null ? content.uiIcon : Icon.box.getRegion());
            icon.setScaling(Scaling.fit);
            icon.setColor(unlocked ? Color.white : lockedColor);
            iconStack.add(icon);

            if (!unlocked) {
                Image dim = new Image(Styles.black);
                dim.setColor(0f, 0f, 0f, 0.35f);
                iconStack.add(dim);
            }

            card.add(iconStack).size(nodeSize);

            if (Vars.ui != null) {
                String name = content != null ? content.localizedName : "Неизвестно";
                String tooltip = content != null && content.description != null && !content.description.isEmpty()
                        ? name + "\n" + content.description
                        : name;
                Vars.ui.addDescTooltip(card, tooltip);
            }

            card.clicked(() -> {
                if (Vars.ui != null && content != null) {
                    Vars.ui.content.show(content);
                }
            });

            return card;
        }

        @Override
        public float getPrefWidth() {
            return canvasSize;
        }

        @Override
        public float getPrefHeight() {
            return canvasSize;
        }

        @Override
        protected void drawChildren() {
            drawRings();
            drawSectors();
            drawConnections();
            super.drawChildren();
        }

        private void drawRings() {
            Draw.color(gridColor);
            Lines.stroke(1.2f);
            for (int d = 1; d <= Math.max(1, maxDepth); d++) {
                float r = (baseRadius + ringStep * (d - 1)) * zoom;
                Lines.circle(worldX(centerX), worldY(centerY), r);
            }
            Draw.reset();
        }

        private void drawSectors() {
            if (topCount <= 1)
                return;
            Draw.color(gridColor);
            Lines.stroke(1.2f);
            float sector = 360f / topCount;
            float offset = -90f - sector / 2f + sectorPadding;
            float maxR = (baseRadius + ringStep * Math.max(0, maxDepth - 1)) * zoom;
            if (maxDepth == 0)
                maxR = baseRadius * zoom;
            for (int i = 0; i < topCount; i++) {
                float start = offset + sector * i;
                float end = start + sector - sectorPadding * 2f;
                drawRadialLine(start, maxR);
                drawRadialLine(end, maxR);
            }
            Draw.reset();
        }

        private void drawRadialLine(float angle, float radius) {
            float x1 = worldX(centerX);
            float y1 = worldY(centerY);
            float x2 = x1 + Angles.trnsx(angle, radius);
            float y2 = y1 + Angles.trnsy(angle, radius);
            Lines.line(x1, y1, x2, y2);
        }

        private void drawConnections() {
            if (entries.isEmpty())
                return;

            Draw.color(lineColor);
            Lines.stroke(2f);

            for (NodeEntry entry : entries) {
                TechNode parent = entry.node.parent;
                if (parent == null)
                    continue;
                NodeEntry parentEntry = byNode.get(parent);
                if (parentEntry == null)
                    continue;

                float x1 = worldX(parentEntry.cx);
                float y1 = worldY(parentEntry.cy);
                float x2 = worldX(entry.cx);
                float y2 = worldY(entry.cy);
                Lines.line(x1, y1, x2, y2);
            }
            Draw.reset();
        }
    }

    private static final class NodeEntry {
        final TechNode node;
        float x, y, width, height;
        float cx, cy;
        Table view;
        Stack iconStack;

        NodeEntry(TechNode node, int depth) {
            this.node = node;
        }

        float centerX() {
            return cx;
        }

        float centerY() {
            return cy;
        }
    }
}
