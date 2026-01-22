package trs.ui;

import arc.Core;
import arc.Events;
import arc.math.geom.Vec2;
import arc.scene.event.Touchable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.ImageButton;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Scaling;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.core.GameState;
import mindustry.entities.Damage;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import trs.Sounds;

public class MapEventButtons {
    private static final Seq<EventDescriptor> DESCRIPTORS = Seq.with(
        new EventDescriptor("Тревога", "Проиграть тревожную сирену на карте.", Icon.warning, MapEventButtons::triggerAlarm),
        new EventDescriptor("Пополнение", "Подбросить медь, титант и кремний на ближайшее ядро.", Icon.add, MapEventButtons::grantResources),
        new EventDescriptor("Искра", "Создать эффект вспышки над ядром.", Icon.power, MapEventButtons::flashStorm)
    );

    private static Table overlay;
    private static Label offsetLabel;
    private static Image explosionIndicator;
    private static ImageButton explosionToggle;
    private static float verticalOffset = 200f;
    private static boolean explosionMode = false;
    private static final float OFFSET_STEP = 20f;
    private static final float AREA_RADIUS_BLOCKS = 2.5f;

    public static void init(){
        // Временно отключено, пока не найдена безопасная причина подмены спрайтов.
        // Включается только явным флагом.
        if (!isEnabled()) return;
        createOverlay();
        Events.on(EventType.ClientLoadEvent.class, event -> ensureOverlay());
        Events.on(EventType.PlayEvent.class, event -> ensureOverlay());
        Events.on(EventType.WorldLoadEndEvent.class, event -> ensureOverlay());
    }

    private static void createOverlay(){
        if (overlay != null) return;
        overlay = new Table(Styles.black6);
        overlay.top();
        overlay.left();
        overlay.marginTop(40f);
        overlay.marginLeft(4f);
        overlay.defaults().pad(2f).growX();
        for (EventDescriptor descriptor : DESCRIPTORS){
            overlay.add(createButton(descriptor)).row();
        }
        overlay.add(createControlRow()).growX();
        overlay.setFillParent(false);
        overlay.visible(MapEventButtons::shouldShow);
        overlay.touchable = Touchable.enabled;
        overlay.pack();
    }

    private static void ensureOverlay(){
        if (!isEnabled()) return;
        if (Vars.ui == null || Vars.ui.hudGroup == null) return;
        createOverlay();
        createIndicator();
        if (overlay.parent == null){
            Vars.ui.hudGroup.addChild(overlay);
        }
        overlay.toFront();
        if (explosionIndicator != null){
            explosionIndicator.toFront();
        }
        repositionOverlay();
    }

    private static Table createButton(EventDescriptor descriptor){
        Table container = new Table();
        container.defaults().pad(2f);
        ImageButton button = new ImageButton(Styles.clearTogglei);
        button.clicked(descriptor.action);
        button.getImageCell().size(46f).scaling(Scaling.fit);
        Vars.ui.addDescTooltip(button, descriptor.tooltip != null ? descriptor.tooltip : descriptor.label);

        if (descriptor.icon != null){
            button.getStyle().imageUp = descriptor.icon;
            button.getStyle().imageDown = descriptor.icon;
            button.getStyle().imageOver = descriptor.icon;
            button.getStyle().imageDisabled = descriptor.icon;
        }

        container.add(button).size(58f).padBottom(1f);
        container.row();
        container.add(new Label(descriptor.label, Styles.outlineLabel)).width(68f).wrap().padTop(2f);
        return container;
    }

    private static Table createControlRow(){
        Table controls = new Table();
        controls.defaults().pad(1f);
        ImageButton up = new ImageButton(Styles.squarei);
        up.clicked(() -> adjustOffset(OFFSET_STEP));
        Vars.ui.addDescTooltip(up, "Поднять панель");
        if (Icon.up != null){
            up.getStyle().imageUp = Icon.up;
            up.getStyle().imageDown = Icon.up;
            up.getStyle().imageOver = Icon.up;
            up.getStyle().imageChecked = Icon.up;
            up.getStyle().imageDisabled = Icon.up;
        }
        ImageButton down = new ImageButton(Styles.squarei);
        down.clicked(() -> adjustOffset(-OFFSET_STEP));
        Vars.ui.addDescTooltip(down, "Опустить панель");
        if (Icon.down != null){
            down.getStyle().imageUp = Icon.down;
            down.getStyle().imageDown = Icon.down;
            down.getStyle().imageOver = Icon.down;
            down.getStyle().imageChecked = Icon.down;
            down.getStyle().imageDisabled = Icon.down;
        }

        explosionToggle = new ImageButton(Styles.squarei);
        explosionToggle.clicked(MapEventButtons::toggleExplosionMode);
        Vars.ui.addDescTooltip(explosionToggle, "Вызвать взрыв");
        applyIconToButton(explosionToggle, Icon.warning);
        explosionToggle.update(() -> explosionToggle.setChecked(explosionMode));

        offsetLabel = new Label("", Styles.outlineLabel);
        updateOffsetLabel();

        controls.add(up).size(30f);
        controls.add(down).size(30f).padLeft(2f);
        controls.add(explosionToggle).size(30f).padLeft(4f);
        controls.add(offsetLabel).padLeft(6f);
        return controls;
    }

    private static void adjustOffset(float delta){
        verticalOffset += delta;
        updateOffsetLabel();
        repositionOverlay();
    }

    private static void updateOffsetLabel(){
        if (offsetLabel != null){
            offsetLabel.setText("Смещение: " + (int) verticalOffset);
        }
    }

    private static void repositionOverlay(){
        if (overlay == null) return;
        float groupHeight = (Vars.ui != null && Vars.ui.hudGroup != null)
            ? Vars.ui.hudGroup.getHeight()
            : Core.graphics.getHeight();
        overlay.pack();
        float y = groupHeight / 2f - overlay.getPrefHeight() / 2f + verticalOffset;
        overlay.setPosition(4f, y);
    }

    private static void toggleExplosionMode(){
        explosionMode = !explosionMode;
        if (explosionIndicator == null){
            createIndicator();
        }
        if (explosionIndicator != null){
            explosionIndicator.visible = explosionMode;
        }
        Vars.ui.showInfoToast(explosionMode ? "Режим взрыва включен. Кликните на карте." : "Режим взрыва выключен.", 1f);
    }

    private static void createIndicator(){
        if (explosionIndicator != null || Core.atlas == null) return;
        var circle = Core.atlas.find("circle");
        if (circle == null || !circle.found()) {
            // Fallback на белый квадрат если circle не найден
            circle = Core.atlas.find("white");
            if (circle == null || !circle.found()) return;
        }
        explosionIndicator = new Image(circle);
        explosionIndicator.touchable = Touchable.disabled;
        explosionIndicator.visible = false;
        explosionIndicator.setColor(1f, 0.2f, 0.2f, 0.4f);
        explosionIndicator.update(() -> {
            if (!explosionMode || Vars.renderer == null || Vars.state == null || !Vars.state.isPlaying()) {
                explosionIndicator.visible = false;
                return;
            }
            
            // Проверяем клик
            if (Core.input.justTouched()){
                float wx = Core.input.mouseWorldX();
                float wy = Core.input.mouseWorldY();
                spawnExplosion(wx, wy);
            }
            
            // Обновляем позицию индикатора
            float wx = Core.input.mouseWorldX();
            float wy = Core.input.mouseWorldY();
            float radius = Vars.tilesize * AREA_RADIUS_BLOCKS;
            
            // Преобразуем мировые координаты в экранные
            Vec2 screenPos = Core.camera.project(wx, wy);
            Vec2 screenEdge = Core.camera.project(wx + radius, wy);
            float screenRadius = Math.abs(screenPos.x - screenEdge.x);
            float size = screenRadius * 2f;
            
            explosionIndicator.setSize(size, size);
            // Y координата инвертирована в UI (0 внизу, высота вверху)
            explosionIndicator.setPosition(screenPos.x - screenRadius, Core.graphics.getHeight() - screenPos.y - screenRadius);
            explosionIndicator.visible = true;
        });
        if (Vars.ui != null && Vars.ui.hudGroup != null){
            Vars.ui.hudGroup.addChild(explosionIndicator);
            explosionIndicator.toFront();
        }
    }

    private static void applyIconToButton(ImageButton button, TextureRegionDrawable icon){
        if (icon == null) return;
        button.getStyle().imageUp = icon;
        button.getStyle().imageDown = icon;
        button.getStyle().imageOver = icon;
        button.getStyle().imageDisabled = icon;
    }

    private static void spawnExplosion(float x, float y){
        float radius = Vars.tilesize * AREA_RADIUS_BLOCKS;
        Damage.damage(Team.sharded, x, y, 999_999_999f, radius, true, true);
        Fx.reactorExplosion.at(x, y);
        Vars.ui.showInfoToast("Взрыв создан!", 1f);
    }

    private static boolean shouldShow(){
        return Vars.state != null
            && Vars.state.is(GameState.State.playing)
            && Vars.world != null
            && Vars.ui != null
            && overlay != null;
    }

    private static boolean isEnabled(){
        return Core.settings == null || Core.settings.getBool("trs_enable_event_buttons", false);
    }

    private static Building findCore(){
        if (Vars.state == null || Vars.state.rules == null) return null;
        Team team = Vars.state.rules.defaultTeam;
        if (team == null) return null;
        return team.cores().first();
    }

    private static void triggerAlarm(){
        if (Sounds.alarm != null){
            Sounds.alarm.play(1f);
        }
        Vars.ui.showInfoToast("Сирена тревоги активирована.", 1f);
    }

    private static void grantResources(){
        Building core = findCore();
        if (core == null){
            Vars.ui.showErrorMessage("Ядро не найдено, ресурсы не доставлены.");
            return;
        }
        core.items.add(Items.copper, 40);
        core.items.add(Items.titanium, 15);
        core.items.add(Items.silicon, 8);
        Vars.ui.showInfoToast("На ядро доставлены ресурсы.", 1f);
    }

    private static void flashStorm(){
        Building core = findCore();
        if (core == null){
            Vars.ui.showErrorMessage("Ядро не найдено, эффект не сработал.");
            return;
        }
        float x = core.x;
        float y = core.y;
        Fx.lightning.at(x, y);
        Fx.coreBuildShockwave.at(x, y);
        Vars.ui.showInfoToast("Над ядром вспыхнул разряд.", 1f);
    }

    private static final class EventDescriptor {
        final String label;
        final String tooltip;
        final TextureRegionDrawable icon;
        final Runnable action;

        private EventDescriptor(String label, String tooltip, TextureRegionDrawable icon, Runnable action){
            this.label = label;
            this.tooltip = tooltip;
            this.icon = icon;
            this.action = action;
        }
    }
}
