package dev.tarico.injection.mixins;

import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.render.Xray;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.ByteOrder;
import java.nio.IntBuffer;

@Mixin({WorldRenderer.class})
public abstract class MixinWorldRenderer {
    int iR;
    int iG;
    int iB;
    @Shadow
    private boolean noColor;
    @Shadow
    private IntBuffer rawIntBuffer;
    @Shadow
    private int vertexCount;
    @Shadow
    private VertexFormat vertexFormat;
    @Shadow
    private int vertexFormatIndex;

    @Shadow
    public abstract int getColorIndex(final int p0);


    /**
     * @author Czf_233
     * @reason Xray
     */
    @Overwrite
    public void putColorMultiplier(float red, float green, float blue, int p_178978_4_) {
        int i = this.getColorIndex(p_178978_4_);
        int j = -1;

        if (!this.noColor) {
            j = this.rawIntBuffer.get(i);

            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                int k = (int) ((float) (j & 255) * red);
                int l = (int) ((float) (j >> 8 & 255) * green);
                int i1 = (int) ((float) (j >> 16 & 255) * blue);
                j &= ModuleManager.instance.getModule(Xray.class).getState() ? 1073741824 : -16777216;
                j = j | i1 << 16 | l << 8 | k;
            } else {
                int j1 = (int) ((float) (j >> 24 & 255) * red);
                int k1 = (int) ((float) (j >> 16 & 255) * green);
                int l1 = (int) ((float) (j >> 8 & 255) * blue);
                j = j & 255;
                j = j | j1 << 24 | k1 << 16 | l1 << 8;
            }
        }

        this.rawIntBuffer.put(i, j);
    }
}
