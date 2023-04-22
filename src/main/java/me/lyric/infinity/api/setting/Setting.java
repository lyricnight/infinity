package me.lyric.infinity.api.setting;

/**
 * @author
 */

public class Setting<T> {

    private final String name;
    private final String description;

    private Setting master;
    private boolean isMaster;
    private boolean isSub;

    private T minimum;
    private T maximum;

    private T value;

    public Setting(String name, String description, T value) {
        this.name = name;
        this.description = description;
        this.value = value;
    }

    public Setting(String name, String description, T value, T minimum, T maximum) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public String getDescription() {
        return description;
    }

    public Setting getMaster() {
        return master;
    }

    public String getName() {
        return name;
    }

    public boolean isVisible() {
        return !this.isSub || (this.master != null && ((Setting<Boolean>) this.master).getValue());
    }

    public Setting<T> withParent(Setting<?> master) {
        this.master = master;
        this.master.setMaster(true);
        this.master.setSub(false);

        this.setMaster(false);
        this.setSub(true);

        return this;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean master) {
        this.isMaster = master;
    }

    public boolean isSub() {
        return isSub;
    }

    public void setSub(boolean sub) {
        isSub = sub;
    }

    public T getMinimum() {
        return minimum;
    }

    public void setMinimum(T minimum) {
        this.minimum = minimum;
    }

    public T getMaximum() {
        return maximum;
    }

    public void setMaximum(T maximum) {
        this.maximum = maximum;
    }

    public void next() {
        if (this.value instanceof Enum) {
            Enum<?>[] array = ((Enum) value).getClass().getEnumConstants();
            int index = ((Enum) value).ordinal() + 1;
            if (index >= array.length) index = 0;
            value = (T) array[index];
        }
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
