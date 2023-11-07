package me.lyric.infinity.manager.client;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.impl.modules.client.*;
import me.lyric.infinity.impl.modules.combat.*;
import me.lyric.infinity.impl.modules.player.*;
import me.lyric.infinity.impl.modules.misc.*;
import me.lyric.infinity.impl.modules.movement.*;
import me.lyric.infinity.impl.modules.player.Exception;
import me.lyric.infinity.impl.modules.render.*;

import java.util.ArrayList;

public class ModuleManager {

    public static ModuleManager moduleManager;

    private final ArrayList<Module> modules = new ArrayList<>();

    public ModuleManager() {
        moduleManager = this;
    }

    public static ModuleManager getModuleManager() {
        return moduleManager;
    }

    public Module getModuleByString(final String name) {
        Module module = null;

        for (Module modules : moduleManager.getModules()) {
            if (modules.getName().equalsIgnoreCase(name)) {
                module = modules;

                break;
            }
        }

        return module;
    }

    public <T extends Module> T getModuleByClass(Class<T> clazz) {
        for (Module module : this.modules) {
            if (clazz.isInstance(module)) {
                return (T) module;
            }
        }
        return null;
    }

    public void init() {
        // CLIENT
        this.modules.add(new ClickGUI());
        this.modules.add(new Notifications());
        this.modules.add(new HUD());
        this.modules.add(new Internals());

        // RENDER
        this.modules.add(new Aspect());
        this.modules.add(new BlockHighlight());
        this.modules.add(new Chams());
        this.modules.add(new Crosshair());
        this.modules.add(new PlayerChams());
        this.modules.add(new ShaderChams());
        this.modules.add(new Brightness());
        this.modules.add(new CustomTime());
        this.modules.add(new CModifier());
        this.modules.add(new HoleESP());
        this.modules.add(new NoRender());
        this.modules.add(new NameTags());
        this.modules.add(new Swing());


        // PLAYER
        this.modules.add(new NoInterpolation());
        this.modules.add(new AutoReply());
        this.modules.add(new PacketDelay());
        this.modules.add(new Delays());
        this.modules.add(new Announcer());
        this.modules.add(new HitboxDesync());
        this.modules.add(new Exception());


        // MISC
        this.modules.add(new AntiAim());
        this.modules.add(new BetterChat());
        this.modules.add(new AutoRespawn());
        this.modules.add(new ChorusControl());
        this.modules.add(new LiquidInteract());
        this.modules.add(new ChatColours());
        this.modules.add(new NoHandshake());
        this.modules.add(new FakePlayer());
        this.modules.add(new AntiHunger());
        this.modules.add(new SkinBlink());

        // Movement
        this.modules.add(new AutoWalk());
        this.modules.add(new EntitySpeed());
        this.modules.add(new HoleSnap());
        this.modules.add(new InstantSpeed());
        this.modules.add(new Sprint());
        this.modules.add(new WebBypass());

        // COMBAT
        this.modules.add(new Criticals());
        this.modules.add(new HoleFiller());
        this.modules.add(new Burrow());
        this.modules.add(new Arrow());
        this.modules.add(new AntiCev());
        this.modules.add(new AutoCity());
    }

    public ArrayList<Module> getModules() {
        return modules;
    }

    public void onSave() {
        for (Module modules : this.getModules()) {
            modules.onSave();
        }
    }

    public void onLoad() {
        for (Module modules : this.getModules()) {
            modules.onLoad();
        }
    }

    public void onReload() {
        for (Module modules : this.getModules()) {
            modules.reloadListener();
        }
    }

    public void onLogout() {
        modules.stream().filter(Module::isEnabled).forEach(Module::onLogout);
    }

    public void onUpdate() {
        modules.stream().filter(Module::isEnabled).forEach(Module::onUpdate);
    }
    public void onTick()
    {
        modules.stream().filter(Module::isEnabled).forEach(Module::onTick);
    }
}
