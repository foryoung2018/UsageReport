 package com.morphoinc.util.render.jni;
 
 import android.graphics.Bitmap;
 import android.view.Surface;
 import java.nio.ByteBuffer;
 /**
 * @hide
 * @author Morpho
 *
 */
 public class RendererJni
 {
   static
   {
     System.loadLibrary("morpho_render_util_v6");
   }
   
   public static final native int getInternalFormat(int paramInt);
   
   public static final native int renderOnSurfaceLeftRight(Surface paramSurface, ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
   
   public static final native int renderOnSurface(Surface paramSurface, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3);
   
   public static final native int renderOnSurfaceWithOffset(Surface paramSurface, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
   
   public static final native int renderOnBitmap(Bitmap paramBitmap, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3);
   
   public static final native int flipHorizontal(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3);
   
   public static final native int clear(ByteBuffer paramByteBuffer, byte paramByte1, byte paramByte2, byte paramByte3, int paramInt1, int paramInt2, int paramInt3);
   
   public static final native int convertToNv21(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
   
   public static final native int glReadPixelsToSurface(Surface paramSurface);
   
   public static final native int glReadPixelsToSurfaceInvY(Surface paramSurface, ByteBuffer paramByteBuffer);
 }