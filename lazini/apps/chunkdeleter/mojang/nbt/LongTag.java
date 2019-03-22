package lazini.apps.chunkdeleter.mojang.nbt;

/**
 * Copyright Mojang AB.
 * 
 * Don't do evil.
 */

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LongTag extends Tag {
    public long data;

    public LongTag(String name) {
        super(name);
    }

    public LongTag(String name, long data) {
        super(name);
        this.data = data;
    }

    @Override
	void write(DataOutput dos) throws IOException {
        dos.writeLong(data);
    }

    @Override
	void load(DataInput dis) throws IOException {
        data = dis.readLong();
    }

    @Override
	public byte getId() {
        return TAG_Long;
    }

    @Override
	public String toString() {
        return "" + data;
    }

    @Override
    public Tag copy() {
        return new LongTag(getName(), data);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            LongTag o = (LongTag) obj;
            return data == o.data;
        }
        return false;
    }

}
