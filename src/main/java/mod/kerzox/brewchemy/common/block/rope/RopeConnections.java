package mod.kerzox.brewchemy.common.block.rope;

import net.minecraft.util.StringRepresentable;

public enum RopeConnections implements StringRepresentable {
    NONE("none"),
    CONNECTED("connected");

    private final String name;
    RopeConnections(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    @Override
    public String getSerializedName() {
        return getName();
    }
}
