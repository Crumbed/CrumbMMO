package com.crumbed.crumbmmo.stats;

import de.tr7zw.nbtapi.iface.NBTHandler;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.jetbrains.annotations.NotNull;

public class BigStat extends Stat.Value {
    public static final NBTHandler<BigStat> HANDLER = new NBTHandler<BigStat>() {
        @Override
        public void set(@NotNull ReadWriteNBT readWriteNBT, @NotNull String s, @NotNull BigStat bigStat) {

        }

        @Override
        public BigStat get(@NotNull ReadableNBT nbt, @NotNull String name) {
            var tag = nbt.getCompound(name);
            if (tag == null) return new BigStat(0, 0, 0);

            return new BigStat(
                tag.getFloat("value"),
                tag.getFloat("max"),
                tag.getFloat("regen")
            );
        }
    };

    public Stat.Value max;
    public Stat.Value regen;

    public BigStat(
        double value,
        double max,
        double regen
    ) {
        super(value);
        this.max = new Stat.Value(max);
        this.regen = new Stat.Value(regen);
    }
}
