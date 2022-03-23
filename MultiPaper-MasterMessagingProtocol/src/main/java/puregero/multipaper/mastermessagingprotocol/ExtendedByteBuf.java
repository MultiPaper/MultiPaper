package puregero.multipaper.mastermessagingprotocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ByteProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ExtendedByteBuf extends ByteBuf {
    private final ByteBuf parent;

    public ExtendedByteBuf(ByteBuf parent) {
        this.parent = parent;
    }

    public int readVarInt() {
        int i = 0;
        int j = 0;

        byte b0;

        do {
            b0 = this.readByte();
            i |= (b0 & 127) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((b0 & 128) == 128);

        return i;
    }

    public ExtendedByteBuf writeVarInt(int value) {
        while ((value & -128) != 0) {
            this.writeByte(value & 127 | 128);
            value >>>= 7;
        }

        this.writeByte(value);
        return this;
    }

    public String readString() {
        int length = readVarInt();
        String string = this.toString(this.readerIndex(), length, StandardCharsets.UTF_8);
        this.readerIndex(this.readerIndex() + length);
        return string;
    }

    public ExtendedByteBuf writeString(String string) {
        byte[] stringBytes = string.getBytes(StandardCharsets.UTF_8);
        writeVarInt(stringBytes.length);
        writeBytes(stringBytes);
        return this;
    }

    public UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    public ExtendedByteBuf writeUUID(UUID uuid) {
        this.writeLong(uuid.getMostSignificantBits());
        this.writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    public ChunkKey readChunkKey() {
        return new ChunkKey(readString(), readInt(), readInt());
    }

    public ExtendedByteBuf writeChunkKey(ChunkKey chunkKey) {
        this.writeString(chunkKey.world);
        this.writeInt(chunkKey.x);
        this.writeInt(chunkKey.z);
        return this;
    }

    @Override
    public int capacity() {
        return parent.capacity();
    }

    @Override
    public ByteBuf capacity(int i) {
        return parent.capacity(i);
    }

    @Override
    public int maxCapacity() {
        return parent.maxCapacity();
    }

    @Override
    public ByteBufAllocator alloc() {
        return parent.alloc();
    }

    @Override
    @Deprecated
    public ByteOrder order() {
        return parent.order();
    }

    @Override
    @Deprecated
    public ByteBuf order(ByteOrder byteorder) {
        return parent.order(byteorder);
    }

    @Override
    public ByteBuf unwrap() {
        return parent.unwrap();
    }

    @Override
    public boolean isDirect() {
        return parent.isDirect();
    }

    @Override
    public boolean isReadOnly() {
        return parent.isReadOnly();
    }

    @Override
    public ByteBuf asReadOnly() {
        return parent.asReadOnly();
    }

    @Override
    public int readerIndex() {
        return parent.readerIndex();
    }

    @Override
    public ByteBuf readerIndex(int i) {
        return parent.readerIndex(i);
    }

    @Override
    public int writerIndex() {
        return parent.writerIndex();
    }

    @Override
    public ByteBuf writerIndex(int i) {
        return parent.writerIndex(i);
    }

    @Override
    public ByteBuf setIndex(int i, int j) {
        return parent.setIndex(i, j);
    }

    @Override
    public int readableBytes() {
        return parent.readableBytes();
    }

    @Override
    public int writableBytes() {
        return parent.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return parent.maxWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return parent.isReadable();
    }

    @Override
    public boolean isReadable(int i) {
        return parent.isReadable(i);
    }

    @Override
    public boolean isWritable() {
        return parent.isWritable();
    }

    @Override
    public boolean isWritable(int i) {
        return parent.isWritable(i);
    }

    @Override
    public ByteBuf clear() {
        return parent.clear();
    }

    @Override
    public ByteBuf markReaderIndex() {
        return parent.markReaderIndex();
    }

    @Override
    public ByteBuf resetReaderIndex() {
        return parent.resetReaderIndex();
    }

    @Override
    public ByteBuf markWriterIndex() {
        return parent.markWriterIndex();
    }

    @Override
    public ByteBuf resetWriterIndex() {
        return parent.resetWriterIndex();
    }

    @Override
    public ByteBuf discardReadBytes() {
        return parent.discardReadBytes();
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        return parent.discardSomeReadBytes();
    }

    @Override
    public ByteBuf ensureWritable(int i) {
        return parent.ensureWritable(i);
    }

    @Override
    public int ensureWritable(int i, boolean flag) {
        return parent.ensureWritable(i, flag);
    }

    @Override
    public boolean getBoolean(int i) {
        return parent.getBoolean(i);
    }

    @Override
    public byte getByte(int i) {
        return parent.getByte(i);
    }

    @Override
    public short getUnsignedByte(int i) {
        return parent.getUnsignedByte(i);
    }

    @Override
    public short getShort(int i) {
        return parent.getShort(i);
    }

    @Override
    public short getShortLE(int i) {
        return parent.getShortLE(i);
    }

    @Override
    public int getUnsignedShort(int i) {
        return parent.getUnsignedShort(i);
    }

    @Override
    public int getUnsignedShortLE(int i) {
        return parent.getUnsignedShortLE(i);
    }

    @Override
    public int getMedium(int i) {
        return parent.getMedium(i);
    }

    @Override
    public int getMediumLE(int i) {
        return parent.getMediumLE(i);
    }

    @Override
    public int getUnsignedMedium(int i) {
        return parent.getUnsignedMedium(i);
    }

    @Override
    public int getUnsignedMediumLE(int i) {
        return parent.getUnsignedMediumLE(i);
    }

    @Override
    public int getInt(int i) {
        return parent.getInt(i);
    }

    @Override
    public int getIntLE(int i) {
        return parent.getIntLE(i);
    }

    @Override
    public long getUnsignedInt(int i) {
        return parent.getUnsignedInt(i);
    }

    @Override
    public long getUnsignedIntLE(int i) {
        return parent.getUnsignedIntLE(i);
    }

    @Override
    public long getLong(int i) {
        return parent.getLong(i);
    }

    @Override
    public long getLongLE(int i) {
        return parent.getLongLE(i);
    }

    @Override
    public char getChar(int i) {
        return parent.getChar(i);
    }

    @Override
    public float getFloat(int i) {
        return parent.getFloat(i);
    }

    @Override
    public double getDouble(int i) {
        return parent.getDouble(i);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf) {
        return parent.getBytes(i, bytebuf);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j) {
        return parent.getBytes(i, bytebuf, j);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k) {
        return parent.getBytes(i, bytebuf, j, k);
    }

    @Override
    public ByteBuf getBytes(int i, byte[] abyte) {
        return parent.getBytes(i, abyte);
    }

    @Override
    public ByteBuf getBytes(int i, byte[] abyte, int j, int k) {
        return parent.getBytes(i, abyte, j, k);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuffer bytebuffer) {
        return parent.getBytes(i, bytebuffer);
    }

    @Override
    public ByteBuf getBytes(int i, OutputStream outputstream, int j) throws IOException {
        return parent.getBytes(i, outputstream, j);
    }

    @Override
    public int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) throws IOException {
        return parent.getBytes(i, gatheringbytechannel, j);
    }

    @Override
    public int getBytes(int i, FileChannel filechannel, long j, int k) throws IOException {
        return parent.getBytes(i, filechannel, j, k);
    }

    @Override
    public CharSequence getCharSequence(int i, int j, Charset charset) {
        return parent.getCharSequence(i, j, charset);
    }

    @Override
    public ByteBuf setBoolean(int i, boolean flag) {
        return parent.setBoolean(i, flag);
    }

    @Override
    public ByteBuf setByte(int i, int j) {
        return parent.setByte(i, j);
    }

    @Override
    public ByteBuf setShort(int i, int j) {
        return parent.setShort(i, j);
    }

    @Override
    public ByteBuf setShortLE(int i, int j) {
        return parent.setShortLE(i, j);
    }

    @Override
    public ByteBuf setMedium(int i, int j) {
        return parent.setMedium(i, j);
    }

    @Override
    public ByteBuf setMediumLE(int i, int j) {
        return parent.setMediumLE(i, j);
    }

    @Override
    public ByteBuf setInt(int i, int j) {
        return parent.setInt(i, j);
    }

    @Override
    public ByteBuf setIntLE(int i, int j) {
        return parent.setIntLE(i, j);
    }

    @Override
    public ByteBuf setLong(int i, long j) {
        return parent.setLong(i, j);
    }

    @Override
    public ByteBuf setLongLE(int i, long j) {
        return parent.setLongLE(i, j);
    }

    @Override
    public ByteBuf setChar(int i, int j) {
        return parent.setChar(i, j);
    }

    @Override
    public ByteBuf setFloat(int i, float f) {
        return parent.setFloat(i, f);
    }

    @Override
    public ByteBuf setDouble(int i, double d0) {
        return parent.setDouble(i, d0);
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf) {
        return parent.setBytes(i, bytebuf);
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j) {
        return parent.setBytes(i, bytebuf, j);
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k) {
        return parent.setBytes(i, bytebuf, j, k);
    }

    @Override
    public ByteBuf setBytes(int i, byte[] abyte) {
        return parent.setBytes(i, abyte);
    }

    @Override
    public ByteBuf setBytes(int i, byte[] abyte, int j, int k) {
        return parent.setBytes(i, abyte, j, k);
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuffer bytebuffer) {
        return parent.setBytes(i, bytebuffer);
    }

    @Override
    public int setBytes(int i, InputStream inputstream, int j) throws IOException {
        return parent.setBytes(i, inputstream, j);
    }

    @Override
    public int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) throws IOException {
        return parent.setBytes(i, scatteringbytechannel, j);
    }

    @Override
    public int setBytes(int i, FileChannel filechannel, long j, int k) throws IOException {
        return parent.setBytes(i, filechannel, j, k);
    }

    @Override
    public ByteBuf setZero(int i, int j) {
        return parent.setZero(i, j);
    }

    @Override
    public int setCharSequence(int i, CharSequence charsequence, Charset charset) {
        return parent.setCharSequence(i, charsequence, charset);
    }

    @Override
    public boolean readBoolean() {
        return parent.readBoolean();
    }

    @Override
    public byte readByte() {
        return parent.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return parent.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return parent.readShort();
    }

    @Override
    public short readShortLE() {
        return parent.readShortLE();
    }

    @Override
    public int readUnsignedShort() {
        return parent.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return parent.readUnsignedShortLE();
    }

    @Override
    public int readMedium() {
        return parent.readMedium();
    }

    @Override
    public int readMediumLE() {
        return parent.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        return parent.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return parent.readUnsignedMediumLE();
    }

    @Override
    public int readInt() {
        return parent.readInt();
    }

    @Override
    public int readIntLE() {
        return parent.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        return parent.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return parent.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        return parent.readLong();
    }

    @Override
    public long readLongLE() {
        return parent.readLongLE();
    }

    @Override
    public char readChar() {
        return parent.readChar();
    }

    @Override
    public float readFloat() {
        return parent.readFloat();
    }

    @Override
    public double readDouble() {
        return parent.readDouble();
    }

    @Override
    public ByteBuf readBytes(int i) {
        return parent.readBytes(i);
    }

    @Override
    public ByteBuf readSlice(int i) {
        return parent.readSlice(i);
    }

    @Override
    public ByteBuf readRetainedSlice(int i) {
        return parent.readRetainedSlice(i);
    }

    @Override
    public ByteBuf readBytes(ByteBuf bytebuf) {
        return parent.readBytes(bytebuf);
    }

    @Override
    public ByteBuf readBytes(ByteBuf bytebuf, int i) {
        return parent.readBytes(bytebuf, i);
    }

    @Override
    public ByteBuf readBytes(ByteBuf bytebuf, int i, int j) {
        return parent.readBytes(bytebuf, i, j);
    }

    @Override
    public ByteBuf readBytes(byte[] abyte) {
        return parent.readBytes(abyte);
    }

    @Override
    public ByteBuf readBytes(byte[] abyte, int i, int j) {
        return parent.readBytes(abyte, i, j);
    }

    @Override
    public ByteBuf readBytes(ByteBuffer bytebuffer) {
        return parent.readBytes(bytebuffer);
    }

    @Override
    public ByteBuf readBytes(OutputStream outputstream, int i) throws IOException {
        return parent.readBytes(outputstream, i);
    }

    @Override
    public int readBytes(GatheringByteChannel gatheringbytechannel, int i) throws IOException {
        return parent.readBytes(gatheringbytechannel, i);
    }

    @Override
    public CharSequence readCharSequence(int i, Charset charset) {
        return parent.readCharSequence(i, charset);
    }

    @Override
    public int readBytes(FileChannel filechannel, long i, int j) throws IOException {
        return parent.readBytes(filechannel, i, j);
    }

    @Override
    public ByteBuf skipBytes(int i) {
        return parent.skipBytes(i);
    }

    @Override
    public ByteBuf writeBoolean(boolean flag) {
        return parent.writeBoolean(flag);
    }

    @Override
    public ByteBuf writeByte(int i) {
        return parent.writeByte(i);
    }

    @Override
    public ByteBuf writeShort(int i) {
        return parent.writeShort(i);
    }

    @Override
    public ByteBuf writeShortLE(int i) {
        return parent.writeShortLE(i);
    }

    @Override
    public ByteBuf writeMedium(int i) {
        return parent.writeMedium(i);
    }

    @Override
    public ByteBuf writeMediumLE(int i) {
        return parent.writeMediumLE(i);
    }

    @Override
    public ByteBuf writeInt(int i) {
        return parent.writeInt(i);
    }

    @Override
    public ByteBuf writeIntLE(int i) {
        return parent.writeIntLE(i);
    }

    @Override
    public ByteBuf writeLong(long i) {
        return parent.writeLong(i);
    }

    @Override
    public ByteBuf writeLongLE(long i) {
        return parent.writeLongLE(i);
    }

    @Override
    public ByteBuf writeChar(int i) {
        return parent.writeChar(i);
    }

    @Override
    public ByteBuf writeFloat(float f) {
        return parent.writeFloat(f);
    }

    @Override
    public ByteBuf writeDouble(double d0) {
        return parent.writeDouble(d0);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf bytebuf) {
        return parent.writeBytes(bytebuf);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf bytebuf, int i) {
        return parent.writeBytes(bytebuf, i);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf bytebuf, int i, int j) {
        return parent.writeBytes(bytebuf, i, j);
    }

    @Override
    public ByteBuf writeBytes(byte[] abyte) {
        return parent.writeBytes(abyte);
    }

    @Override
    public ByteBuf writeBytes(byte[] abyte, int i, int j) {
        return parent.writeBytes(abyte, i, j);
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer bytebuffer) {
        return parent.writeBytes(bytebuffer);
    }

    @Override
    public int writeBytes(InputStream inputstream, int i) throws IOException {
        return parent.writeBytes(inputstream, i);
    }

    @Override
    public int writeBytes(ScatteringByteChannel scatteringbytechannel, int i) throws IOException {
        return parent.writeBytes(scatteringbytechannel, i);
    }

    @Override
    public int writeBytes(FileChannel filechannel, long i, int j) throws IOException {
        return parent.writeBytes(filechannel, i, j);
    }

    @Override
    public ByteBuf writeZero(int i) {
        return parent.writeZero(i);
    }

    @Override
    public int writeCharSequence(CharSequence charsequence, Charset charset) {
        return parent.writeCharSequence(charsequence, charset);
    }

    @Override
    public int indexOf(int i, int j, byte b0) {
        return parent.indexOf(i, j, b0);
    }

    @Override
    public int bytesBefore(byte b0) {
        return parent.bytesBefore(b0);
    }

    @Override
    public int bytesBefore(int i, byte b0) {
        return parent.bytesBefore(i, b0);
    }

    @Override
    public int bytesBefore(int i, int j, byte b0) {
        return parent.bytesBefore(i, j, b0);
    }

    @Override
    public int forEachByte(ByteProcessor byteprocessor) {
        return parent.forEachByte(byteprocessor);
    }

    @Override
    public int forEachByte(int i, int j, ByteProcessor byteprocessor) {
        return parent.forEachByte(i, j, byteprocessor);
    }

    @Override
    public int forEachByteDesc(ByteProcessor byteprocessor) {
        return parent.forEachByteDesc(byteprocessor);
    }

    @Override
    public int forEachByteDesc(int i, int j, ByteProcessor byteprocessor) {
        return parent.forEachByteDesc(i, j, byteprocessor);
    }

    @Override
    public ByteBuf copy() {
        return parent.copy();
    }

    @Override
    public ByteBuf copy(int i, int j) {
        return parent.copy(i, j);
    }

    @Override
    public ByteBuf slice() {
        return parent.slice();
    }

    @Override
    public ByteBuf retainedSlice() {
        return parent.retainedSlice();
    }

    @Override
    public ByteBuf slice(int i, int j) {
        return parent.slice(i, j);
    }

    @Override
    public ByteBuf retainedSlice(int i, int j) {
        return parent.retainedSlice(i, j);
    }

    @Override
    public ByteBuf duplicate() {
        return parent.duplicate();
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return parent.retainedDuplicate();
    }

    @Override
    public int nioBufferCount() {
        return parent.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return parent.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int i, int j) {
        return parent.nioBuffer(i, j);
    }

    @Override
    public ByteBuffer internalNioBuffer(int i, int j) {
        return parent.internalNioBuffer(i, j);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return parent.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int i, int j) {
        return parent.nioBuffers(i, j);
    }

    @Override
    public boolean hasArray() {
        return parent.hasArray();
    }

    @Override
    public byte[] array() {
        return parent.array();
    }

    @Override
    public int arrayOffset() {
        return parent.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return parent.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return parent.memoryAddress();
    }

    @Override
    public String toString(Charset charset) {
        return parent.toString(charset);
    }

    @Override
    public String toString(int i, int j, Charset charset) {
        return parent.toString(i, j, charset);
    }

    @Override
    public int hashCode() {
        return parent.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return parent.equals(object);
    }

    @Override
    public int compareTo(ByteBuf bytebuf) {
        return parent.compareTo(bytebuf);
    }

    @Override
    public String toString() {
        return parent.toString();
    }

    @Override
    public ByteBuf retain(int i) {
        return parent.retain(i);
    }

    @Override
    public ByteBuf retain() {
        return parent.retain();
    }

    @Override
    public ByteBuf touch() {
        return parent.touch();
    }

    @Override
    public ByteBuf touch(Object object) {
        return parent.touch(object);
    }

    @Override
    public int refCnt() {
        return parent.refCnt();
    }

    @Override
    public boolean release() {
        return parent.release();
    }

    @Override
    public boolean release(int i) {
        return parent.release(i);
    }
}
