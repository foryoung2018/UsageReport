package com.htc.lib1.exo.wrap;

import android.graphics.Rect;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.lang.Class;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.htc.lib1.exo.utilities.LOG;


/**
 * @hide
 */
public final class ClosedCaption
{

    private HashMap<String, MethodItem> mMethodTable = new HashMap<String, MethodItem>();
/*
    private static final int FIRST_PUBLIC_KEY                 = 1;

    // These keys must be in sync with the keys in TextDescription.h
    public static final int KEY_DISPLAY_FLAGS                 = 1; // int
    public static final int KEY_STYLE_FLAGS                   = 2; // int
    public static final int KEY_BACKGROUND_COLOR_RGBA         = 3; // int
    public static final int KEY_HIGHLIGHT_COLOR_RGBA          = 4; // int
    public static final int KEY_SCROLL_DELAY                  = 5; // int
    public static final int KEY_WRAP_TEXT                     = 6; // int
*/
    public static final int KEY_START_TIME                    = 7; // int
/*
    public static final int KEY_STRUCT_BLINKING_TEXT_LIST     = 8; // List<CharPos>
    public static final int KEY_STRUCT_FONT_LIST              = 9; // List<Font>
    public static final int KEY_STRUCT_HIGHLIGHT_LIST         = 10; // List<CharPos>
    public static final int KEY_STRUCT_HYPER_TEXT_LIST        = 11; // List<HyperText>
    public static final int KEY_STRUCT_KARAOKE_LIST           = 12; // List<Karaoke>
    public static final int KEY_STRUCT_STYLE_LIST             = 13; // List<Style>
    public static final int KEY_STRUCT_TEXT_POS               = 14; // TextPos
    public static final int KEY_STRUCT_JUSTIFICATION          = 15; // Justification
    public static final int KEY_STRUCT_TEXT                   = 16; // Text
    public static final int CC_KEY_STRUCT_REGION_LIST         = 17; // List<Region>
    public static final int CC_KEY_LANGUAGE_ID                = 18; // List <Language>
    public static final int CC_KEY_POPON_NOTIFICATION         = 19; // int
    public static final int CC_KEY_PAINTON_NOTIFICATION       = 20; // List<PaintOnData>
    public static final int CC_LOGO_LIST                      = 21; // Logo region
    public static final int CC_KEY_CELL_POS                   = 22;

    private static final int LAST_PUBLIC_KEY                  = 22;

    private static final int FIRST_PRIVATE_KEY                = 101;

    // The following keys are used between ClosedCaption.java and
    // TextDescription.cpp in order to parce the Parcel.
    public static final int KEY_GLOBAL_SETTING               = 101;
    public static final int KEY_LOCAL_SETTING                = 102;
    public static final int KEY_START_CHAR                   = 103;
    public static final int KEY_END_CHAR                     = 104;
    public static final int KEY_FONT_ID                      = 105;
    public static final int KEY_FONT_SIZE                    = 106;
    public static final int KEY_TEXT_COLOR_RGBA              = 107;
    public static final int CC_KEY_TEXT_PRESENTATION         = 108;
    public static final int CC_KEY_TEXT_OPACITY              = 109;
    public static final int CC_KEY_BACKGROUND_OPACITY        = 110;
*/
    public static final int CC_KEY_END_TIME                  = 111; // int
/*
    public static final int CC_KEY_TEXT_COLOR                = 112; // int
    public static final int CC_KEY_TEXT_ALIGN                = 113; // int
    public static final int CC_KEY_FONT_FAMILY               = 114; // int
    public static final int CC_KEY_FONT_WEIGHT               = 115; // int
    public static final int CC_KEY_LOGO                      = 116; // int
    public static final int CC_KEY_FONT_STYLE                = 117; // int
    public static final int CC_KEY_BACKGROUND_COLOR          = 118; // int
    public static final int CC_KEY_REGION_ID                 = 119; // int
    public static final int CC_KEY_DISPLAY_ALIGN             = 120; // int
    public static final int CC_KEY_ORIGIN                    = 121; // int
    public static final int CC_KEY_EXTENT                    = 122; // int
    public static final int CC_KEY_PADDING                   = 123; // int
    public static final int CC_LOGO_IMAGE                    = 124;
    public static final int CC_LOGO_REGION                   = 125;
    public static final int CC_KEY_CELL_ORIGIN               = 126;
    public static final int CC_KEY_CELL_EXTENT               = 127;
    private static final int CC_KEY_CELL_RESOLUTION          = 128; // Cell Resolution
   
    private static final int LAST_PRIVATE_KEY                = 128;
*/
    private static final String TAG = "Closed Caption";

    public static final class Style extends FieldAdapter{
        public final int startChar;
        public final int endChar;
        public final int fontSize;
        public final boolean isPopOn;
        public final boolean isRollUp;
        public final boolean isPaintOn;
        public final float textOpacity;
        public final float backgroundOpacity;
        public final String textAlign;
        public final String textColor;
        public final String fontStyle;
        public final String fontFamily;
        public final String fontWeight;

        private Object mInstance = null;

        public Style (Object obj)
        {
            if (obj != null)
            {
                Class<?> clazz = obj.getClass();
                LOG.I(TAG,"Style clazz = " + clazz.getName());
                if ("android.media.ClosedCaption$Style".equals(clazz.getName()))
                {
                    mInstance = obj;
                    this.startChar = getInt(mInstance,"startChar",0);
                    this.endChar = getInt(mInstance,"endChar",0);
                    this.fontSize = getInt(mInstance,"fontSize",0);
                    this.isPopOn = getBoolean(mInstance,"isPopOn",false);
                    this.isRollUp = getBoolean(mInstance,"isRollUp",false);
                    this.isPaintOn = getBoolean(mInstance,"isPaintOn",false);
                    this.textOpacity = getFloat(mInstance,"textOpacity",0.0f);
                    this.backgroundOpacity = getFloat(mInstance,"backgroundOpacity",0.0f);
                    this.textAlign = getString(mInstance,"textAlign",null);
                    this.textColor = getString(mInstance,"textColor",null);
                    this.fontStyle = getString(mInstance,"fontStyle",null);
                    this.fontFamily = getString(mInstance,"fontFamily",null);
                    this.fontWeight = getString(mInstance,"fontWeight",null);
                    return;
                }
            }

            this.startChar = 0;
            this.endChar = 0;
            this.fontSize = 0;
            this.isPopOn = false;
            this.isRollUp = false;
            this.isPaintOn = false;
            this.textOpacity = 0.0f;
            this.backgroundOpacity = 0.0f;
            this.textAlign = null;
            this.textColor = null;
            this.fontStyle = null;
            this.fontFamily = null;
            this.fontWeight = null;
        }
        public String toString()
        {
            return "startChar : " + startChar 
                 + ", endChar :" + endChar 
                 + ", fontSize :" + fontSize 
                 + ", isPopOn :" + isPopOn 
                 + ", isRollUp :" + isRollUp 
                 + ", isPaintOn :" + isPaintOn 
                 + ", textOpacity :" + textOpacity 
                 + ", backgroundOpacity :" + backgroundOpacity
                 + ", textAlign :" + textAlign
                 + ", textColor :" + textColor
                 + ", fontStyle :" + fontStyle
                 + ", fontFamily :" + fontFamily
                 + ", fontWeight :" + fontWeight ;
        }
    }

    static private class FieldAdapter
    {

        private HashMap<String, FieldItem> mFieldTable = new HashMap<String, FieldItem>();

        private Field findFieldAndToList(Class<?> clazz, String fieldName)
        {
            try{
                Field field = clazz.getField(fieldName);

                mFieldTable.put(fieldName, new FieldItem(field, true));

                return field;
            }catch( Exception e ){
                mFieldTable.put(fieldName, new FieldItem(null, false));
                LOG.W(TAG, e);
            }
            return null;
        }
        
        public Field getFieldFromList(Class<?> clazz, String fieldName)
        {
        	FieldItem item = mFieldTable.get(fieldName);
        	
            if(item == null)
            {
                return findFieldAndToList(clazz, fieldName);
            }
            else if(item != null && item.isSupport)
        	{
        		return item.field;
        	}
        	return null;
        }

        public int getInt(Object obj,String fieldName, int defaultVal)
        {
            Field field = getFieldFromList(obj.getClass(), fieldName);

            if (field != null)
            {
            	try
            	{
                    return field.getInt(obj);
            	}
            	catch (Exception e)
            	{
            	    LOG.W(TAG, e);	
            	}
            }

            return defaultVal;
        }

        public float getFloat(Object obj,String fieldName, float defaultVal)
        {
            Field field = getFieldFromList(obj.getClass(), fieldName);

            if (field != null)
            {
            	try
            	{
                    return field.getFloat(obj);
            	}
            	catch (Exception e)
            	{
            	    LOG.W(TAG, e);	
            	}
            }

            return defaultVal;
        }

        public String getString(Object obj,String fieldName, String defaultVal)
        {
            Field field = getFieldFromList(obj.getClass(), fieldName);

            if (field != null)
            {
            	try
            	{
                    return (String)field.get(obj);
            	}
            	catch (Exception e)
            	{
            	    LOG.W(TAG, e);	
            	}
            }

            return defaultVal;
        }

        public boolean getBoolean(Object obj,String fieldName, boolean defaultVal)
        {
            Field field = getFieldFromList(obj.getClass(), fieldName);

            if (field != null)
            {
            	try
            	{
                    return field.getBoolean(obj);
            	}
            	catch (Exception e)
            	{
            	    LOG.W(TAG, e);	
            	}
            }

            return defaultVal;
        }

        public class FieldItem
        {
            Field field;
            boolean isSupport;

            public FieldItem(Field field, boolean isSupport)
            {
                this.field = field;
                this.isSupport = isSupport;
            }
        }
    }
    static public final class Region extends FieldAdapter{
        public final String regionID;
        public final String backgroundColor;
        public final String displayAlign;
        public final int xOrigin;
        public final int yOrigin;
        public final int hExtent;
        public final int wExtent;
        public final int beforeEdge;
        public final int afterEdge;
        public final int startEdge;
        public final int endEdge;
        public final float cellxOrigin;
        public final float cellyOrigin;
        public final float cellhExtent;
        public final float cellwExtent;
        public final int cellRow;
        public final int cellColumn;
        public final boolean isCell;

        private Object mInstance = null;

        public Region(Object obj)
        {


            if (obj != null)
            {
                Class<?> clazz = obj.getClass();
                //LOG.I(TAG,"ClosedCaption clazz = " + clazz.getName());
                if ("android.media.ClosedCaption$Region".equals(clazz.getName()))
                {
                    mInstance = obj;
                    this.regionID = getString(mInstance,"regionID",null);
                    this.backgroundColor = getString(mInstance,"backgroundColor",null);
                    this.displayAlign = getString(mInstance,"displayAlign",null);
                    this.xOrigin = getInt(mInstance,"xOrigin",0);
                    this.yOrigin = getInt(mInstance,"yOrigin",0);
                    this.hExtent = getInt(mInstance,"hExtent",0);
                    this.wExtent = getInt(mInstance,"wExtent",0);
                    this.beforeEdge = getInt(mInstance,"beforeEdge",0);
                    this.afterEdge = getInt(mInstance,"afterEdge",0);
                    this.startEdge = getInt(mInstance,"startEdge",0);
                    this.endEdge = getInt(mInstance,"endEdge",0);
                    this.cellxOrigin = getFloat(mInstance,"cellxOrigin",0);
                    this.cellyOrigin = getFloat(mInstance,"cellyOrigin",0);
                    this.cellhExtent = getFloat(mInstance,"cellhExtent",0);
                    this.cellwExtent = getFloat(mInstance,"cellwExtent",0);
                    this.cellColumn = getInt(mInstance,"cellColumn",0);
                    this.cellRow = getInt(mInstance,"cellRow",0);
                    this.isCell = getBoolean(mInstance,"cellwExtent",false);
                    return;
                }
            }
            
            this.regionID = "";
            this.backgroundColor = "";
            this.displayAlign = "";
            this.xOrigin = 0;
            this.yOrigin = 0;
            this.hExtent = 0;
            this.wExtent = 0;
            this.beforeEdge = 0;
            this.afterEdge = 0;
            this.startEdge = 0;
            this.endEdge = 0;
            this.cellxOrigin = 0;
            this.cellyOrigin = 0;
            this.cellhExtent = 0;
            this.cellwExtent = 0;
            this.cellColumn = 0;
            this.cellRow = 0;
            this.isCell = false;
        }

        public String toString()
        {
            return "regionID : " + regionID 
                 + ", backgroundColor :" + backgroundColor 
                 + ", displayAlign :" + displayAlign 
                 + ", xOrigin :" + xOrigin 
                 + ", yOrigin :" + yOrigin 
                 + ", hExtent :" + hExtent 
                 + ", wExtent :" + wExtent 
                 + ", beforeEdge :" + beforeEdge
                 + ", afterEdge :" + afterEdge
                 + ", startEdge :" + startEdge
                 + ", endEdge :" + endEdge
                 + ", cellxOrigin :" + cellxOrigin 
                 + ", cellyOrigin :" + cellyOrigin 
                 + ", cellhExtent :" + cellhExtent 
                 + ", cellwExtent :" + cellwExtent 
                 + ", cellColumn :" + cellColumn
                 + ", cellRow :" + cellRow
                 + ", isCell :" + isCell;
        }
    }

     public static final class CellBound extends FieldAdapter{
        public final float cellLeft;
        public final float cellTop;
        public final float cellWidth;
        public final float cellHeight;
        public final int cellRow;
        public final int cellColumn;
        private Object mInstance = null;

        public CellBound(Object obj)
        {
            if (obj != null)
            {
                Class<?> clazz = obj.getClass();
                //LOG.I(TAG,"ClosedCaption clazz = " + clazz.getName());
                if ("android.media.ClosedCaption$CellBound".equals(clazz.getName()))
                {
                    mInstance = obj;
                    this.cellLeft = getInt(mInstance,"cellLeft",0);
                    this.cellTop = getInt(mInstance,"cellTop",0);
                    this.cellWidth = getInt(mInstance,"cellWidth",0);
                    this.cellHeight = getInt(mInstance,"cellHeight",0);
                    this.cellColumn = getInt(mInstance,"cellColumn",0);
                    this.cellRow = getInt(mInstance,"cellRow",0);
                    return;
                }
            }

            this.cellLeft = 0;
            this.cellTop = 0;
            this.cellWidth = 0;
            this.cellHeight = 0;
            this.cellColumn = 0;
            this.cellRow = 0;
        }
        public String toString()
        {
            return "cellLeft : " + cellLeft 
                 + ", cellTop :" + cellTop 
                 + ", cellWidth :" + cellWidth 
                 + ", cellHeight :" + cellHeight 
                 + ", cellColumn :" + cellColumn 
                 + ", cellRow :" + cellRow ;
        }
    }

    private Object mInstance = null;
    public ClosedCaption(Object obj) {
        if (obj != null)
        {
            Class<?> clazz = obj.getClass();
            //LOG.I(TAG,"ClosedCaption clazz = " + clazz.getName());
            if ("android.media.ClosedCaption".equals(clazz.getName()))
            {
                mInstance = obj;
            }
        }
    }

    private static Class<?> getClosedCaption()
    {
    	try
    	{
            Class<?> clazz = Class.forName("android.media.ClosedCaption");
	        return clazz;
    	}
    	catch(Exception e)
    	{
    		
    	}
    	return null;
    }
    private Method findMethodAndToList(String functionName, Class<?>[] paramTypes)
    {
        try{
            //Method myMethod = getClosedCaption().getDeclaredMethod(functionName, paramTypes);

            Method[] methods = getClosedCaption().getDeclaredMethods();
            Method myMethod = null;
            for(Method method : methods)
            {
                //LOG.I(TAG, "findMethodAndToList() method " + method.getName());
                if (method.getName().equals(functionName))
                {
                    myMethod = method;
                    break;
                }
            }

            mMethodTable.put(functionName, new MethodItem(myMethod, true));

            return myMethod;
        }catch( Exception e ){
            mMethodTable.put(functionName, new MethodItem(null, false));
            LOG.W(TAG, e);
        }
        return null;
    }
    
    private Method getMethodFromList(String functionName, Class<?>[] paramTypes)
    {
    	MethodItem item = mMethodTable.get(functionName);
    	
        if(item == null)
        {
            return findMethodAndToList(functionName, paramTypes);
        }
        else if(item != null && item.isSupport)
    	{
    		return item.method;
    	}
    	return null;
    }

    public String getText() {

        Method method = getMethodFromList("getText", (Class[])null);
        if (method == null)
        {
            LOG.I(TAG , "getText failed , no method");
            return null;
        }
        if (mInstance == null)
        {
            LOG.I(TAG , "getText failed , no mInstance");
            return null;
        }
        String rtn = null;
        try{

          rtn = (String) method.invoke(mInstance);
        } catch (IllegalAccessException x) {
            LOG.W(TAG, "IllegalAccessException");
        } catch (InvocationTargetException x) {
            LOG.W(TAG, "InvocationTargetException");
        }catch( Exception e ){
            LOG.W(TAG, e);
        }
        LOG.I(TAG,"getText rtn = " + rtn);
        return rtn;
    }

    public Rect getBounds() {
        LOG.I(TAG,"getBounds");
        Method method = getMethodFromList("getBounds", (Class[])null);
        if (method == null)
        {
            LOG.I(TAG , "getBounds failed , no method");
            return null;
        }
        if (mInstance == null)
        {
            LOG.I(TAG , "getText failed , no mInstance");
            return null;
        }
        Rect rtn = null;
        try{

          rtn = (Rect) method.invoke(mInstance);
        } catch (IllegalAccessException x) {
            LOG.W(TAG, "IllegalAccessException");
        } catch (InvocationTargetException x) {
            LOG.W(TAG, "InvocationTargetException");
        }catch( Exception e ){
            LOG.W(TAG, e);
        }
        return rtn;
    }

    public CellBound getCellBounds() {
        LOG.I(TAG,"getCellBounds");
        Method method = getMethodFromList("getCellBounds", (Class[])null);
        if (method == null)
        {
            LOG.I(TAG , "getCellBounds failed , no method");
            return null;
        }
        if (mInstance == null)
        {
            LOG.I(TAG , "getText failed , no mInstance");
            return null;
        }
        Object rtn = null;
        try{

          rtn = method.invoke(mInstance);
        } catch (IllegalAccessException x) {
            LOG.W(TAG, "IllegalAccessException");
        } catch (InvocationTargetException x) {
            LOG.W(TAG, "InvocationTargetException");
        }catch( Exception e ){
            LOG.W(TAG, e);
        }
 
        if (rtn != null)
        {
            LOG.I(TAG , "getCellBounds clazz.getName() " + rtn.getClass().getName());
            if ("android.media.ClosedCaption.CellBound".equals(rtn.getClass().getName()))
            {

            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	public List<String> getPaintOnList() {
        LOG.I(TAG,"getPaintOnList");
        Method method = getMethodFromList("getPaintOnList", (Class[]) null);
        if (method == null)
        {
            LOG.I(TAG , "getPaintOnList failed , no method");
            return null;
        }
        if (mInstance == null)
        {
            LOG.I(TAG , "getPaintOnList failed , no mInstance");
            return null;
        }
        List<String> rtn = null;
        try{

          rtn = (List<String>) method.invoke(mInstance);
        } catch (IllegalAccessException x) {
            LOG.W(TAG, "IllegalAccessException");
        } catch (InvocationTargetException x) {
            LOG.W(TAG, "InvocationTargetException");
        }catch( Exception e ){
            LOG.W(TAG, e);
        }

        return rtn;
    }

    @SuppressWarnings("unchecked")
	public List get_Style() {
        LOG.I(TAG,"get_Style");
        Method method = getMethodFromList("get_Style", (Class[]) null);
        if (method == null)
        {
            LOG.I(TAG , "get_Style failed , no method");
            return null;
        }
        if (mInstance == null)
        {
            LOG.I(TAG , "get_Style failed , no mInstance");
            return null;
        }
        List rtn = null;
        try{

          rtn = (List) method.invoke(mInstance);
        } catch (IllegalAccessException x) {
            LOG.W(TAG, "IllegalAccessException");
        } catch (InvocationTargetException x) {
            LOG.W(TAG, "InvocationTargetException");
        }catch( Exception e ){
            LOG.W(TAG, e);
        }

        if (rtn != null)
        {
            LOG.I(TAG,"get_Style in");
            List<Style> newList = new ArrayList<Style>();
            for(Object item : rtn)
            {
                Style style = new Style(item);

                LOG.I(TAG,"get_Style add style " + style.toString());
                newList.add(style);
                return newList;
            }
            LOG.I(TAG,"get_Style out");
        }
        else
        {

        }
        return null;
     }

     @SuppressWarnings("unchecked")
	public List<String> getLogoList() {
        LOG.I(TAG,"getLogoList");
        Method method = getMethodFromList("getLogoList", (Class[]) null);
        if (method == null)
        {
            LOG.I(TAG , "getLogoList failed , no method");
            return null;
        }
        if (mInstance == null)
        {
            LOG.I(TAG , "getLogoList failed , no mInstance");
            return null;
        }
        List<String> rtn = null;
        try{

          rtn = (List<String>) method.invoke(mInstance);
        } catch (IllegalAccessException x) {
            LOG.W(TAG, "IllegalAccessException");
        } catch (InvocationTargetException x) {
            LOG.W(TAG, "InvocationTargetException");
        }catch( Exception e ){
            LOG.W(TAG, e);
        }
        return rtn;
     }

     @SuppressWarnings("unchecked")
	public List<Rect> getLogoPosList() {
        LOG.I(TAG,"getLogoPosList");
        Method method = getMethodFromList("getLogoPosList", (Class[]) null);
        if (method == null)
        {
            LOG.I(TAG , "getLogoPosList failed , no method");
            return null;
        }
        if (mInstance == null)
        {
            LOG.I(TAG , "getLogoPosList failed , no mInstance");
            return null;
        }
        List<Rect> rtn = null;
        try{

          rtn = (List<Rect>) method.invoke(mInstance);
        } catch (IllegalAccessException x) {
            LOG.W(TAG, "IllegalAccessException");
        } catch (InvocationTargetException x) {
            LOG.W(TAG, "InvocationTargetException");
        }catch( Exception e ){
            LOG.W(TAG, e);
        }
        return rtn;
     }

    
     public String getBackgroundColor() {
        LOG.I(TAG,"getBackgroundColor");
        Method method = getMethodFromList("getBackgroundColor", (Class[])null);
        if (method == null)
        {
            LOG.I(TAG , "getBackgroundColor failed , no method");
            return null;
        }
        if (mInstance == null)
        {
            LOG.I(TAG , "getText failed , no mInstance");
            return null;
        }
        String rtn = null;
        try{

          rtn = (String) method.invoke(mInstance);
        } catch (IllegalAccessException x) {
            LOG.W(TAG, "IllegalAccessException");
        } catch (InvocationTargetException x) {
            LOG.W(TAG, "InvocationTargetException");
        }catch( Exception e ){
            LOG.W(TAG, e);
        }
        return rtn;
     }

    public boolean containsKey(final int key) {
        LOG.I(TAG,"containsKey");
        Method method = getMethodFromList("containsKey", new Class[]{Integer.class});
        if (method == null)
        {
            LOG.I(TAG , "containsKey failed , no method");
            return false;
        }
        if (mInstance == null)
        {
            LOG.I(TAG , "containsKey failed , no mInstance");
            return false;
        }
        boolean rtn = false;
        try{

          rtn = (Boolean) method.invoke(mInstance, new Object[]{key});
        } catch (IllegalAccessException x) {
            LOG.W(TAG, "IllegalAccessException");
        } catch (InvocationTargetException x) {
            LOG.W(TAG, "InvocationTargetException");
        }catch( Exception e ){
            LOG.W(TAG, e);
        }
        return rtn;
    }

    public Object getObject(final int key) {
        LOG.I(TAG,"getObject");
        Method method = getMethodFromList("getObject", new Class[]{Integer.class});
        if (method == null)
        {
            LOG.I(TAG , "getObject failed , no method");
            return null;
        }
        if (mInstance == null)
        {
            LOG.I(TAG , "getObject failed , no mInstance");
            return null;
        }
        Object rtn = null;
        try{

          rtn = method.invoke(mInstance, new Object[]{key});
        } catch (IllegalAccessException x) {
            LOG.W(TAG, "IllegalAccessException");
        } catch (InvocationTargetException x) {
            LOG.W(TAG, "InvocationTargetException");
        }catch( Exception e ){
            LOG.W(TAG, e);
        }
        return rtn;
    }

    private class MethodItem
    {
        Method method;
        boolean isSupport;

        public MethodItem(Method method, boolean isSupport)
        {
            this.method = method;
            this.isSupport = isSupport;
        }
    }
}
