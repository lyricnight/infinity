package me.lyric.infinity.manager.client;

import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.impl.modules.client.*;
import me.lyric.infinity.impl.modules.combat.*;
import me.lyric.infinity.impl.modules.misc.*;
import me.lyric.infinity.impl.modules.movement.*;
import me.lyric.infinity.impl.modules.player.AutoReply;
import me.lyric.infinity.impl.modules.player.KickPrevent;
import me.lyric.infinity.impl.modules.player.NoInterpolation;
import me.lyric.infinity.impl.modules.render.*;

import java.util.HashSet;
import java.util.Set;

public class ModuleManager {

    public static ModuleManager moduleManager;

    private final Set<Module> modules = new HashSet<>();

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
        this.modules.add(new RPC());

        // RENDER
        this.modules.add(new Aspect());
        this.modules.add(new BlockHighlight());
        this.modules.add(new Chams());
        this.modules.add(new Crosshair());
        this.modules.add(new ShaderChams());
        this.modules.add(new Brightness());
        this.modules.add(new CustomTime());
        this.modules.add(new CritRender());
        this.modules.add(new CrystalModifier());
        this.modules.add(new HoleESP());
        this.modules.add(new CameraClip());
        this.modules.add(new NoRender());
        this.modules.add(new NameTags());
        this.modules.add(new Swing());


        // PLAYER
        this.modules.add(new NoInterpolation());
        this.modules.add(new AutoReply());
        this.modules.add(new KickPrevent());


        // MISC
        this.modules.add(new AntiAim());
        this.modules.add(new BetterChat());
        this.modules.add(new AutoRespawn());
        this.modules.add(new ChorusControl());
        this.modules.add(new LiquidInteract());
        this.modules.add(new NoHandshake());
        this.modules.add(new FakePlayer());
        this.modules.add(new AntiHunger());
        this.modules.add(new SkinBlink());

        // Movement
        this.modules.add(new AutoWalk());
        this.modules.add(new EntitySpeed());
        this.modules.add(new HoleSnap());
        this.modules.add(new AntiLevitation());
        this.modules.add(new InstantSpeed());
        this.modules.add(new Sprint());
        this.modules.add(new WebBypass());

        // COMBAT
        this.modules.add(new Criticals());
        this.modules.add(new HoleFiller());
        this.modules.add(new Clip());
        this.modules.add(new AntiCev());
        this.modules.add(new ClipTest());
        this.modules.add(new Blocker());
        this.modules.add(new AutoCity());
    }

    public Set<Module> getModules() {
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
        modules.forEach(Module::onLogout);
    }

    public void onUpdate() {
        modules.stream().filter(Module::isEnabled).forEach(Module::onUpdate);
    }
}
