package lazini.apps.chunkdeleter.mojang.nbt;

/**
 * Copyright Mojang AB.
 * 
 * Don't do evil.
 */

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DoubleTag extends Tag {
    public double data;

    public DoubleTag(String name) {
        super(name);
    }

    public DoubleTag(String name, double data) {
        super(name);
        this.data = data;
    }

    @Override
	void write(DataOutput dos) throws IOException {
        dos.writeDouble(data);
    }

    @Override
	void load(DataInput dis) throws IOException {
        data = dis.readDouble();
    }

    @Override
	public byte getId() {
        return TAG_Double;
    }

    @Override
	public String toString() {
        return "" + data;
    }

    @Override
    public Tag copy() {
        return new DoubleTag(getName(), data);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            DoubleTag o = (DoubleTag) obj;
            return data == o.data;
        }
        return false;
    }

}
