package com.htc.test.util;

import android.app.Instrumentation;
import android.graphics.Point;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;

public class DragUtil {
    public final static void genDragTrack(int x1, int y1, int x2, int y2,
            int number, int[] xArray, int[] yArray, int[] durationArray) {

        int xDiff = x2 - x1;
        int yDiff = y2 - y1;
        int xDiffSlot = xDiff / (number - 1);
        int yDiffSlot = yDiff / (number - 1);

        for (int i = 0; i < xArray.length && i < yArray.length
                && i < durationArray.length; i++) {
            if (0 == i) {
                xArray[i] = x1;
                yArray[i] = y1;
                durationArray[i] = 1000;
            } else if (xArray.length - 1 == i) {
                xArray[i] = x2;
                yArray[i] = y2;
                durationArray[i] = 1000;
            } else {
                xArray[i] = x1 + xDiffSlot * i;
                yArray[i] = y1 + yDiffSlot * i;
                durationArray[i] = 0;
            }
        }
    }

    public final static void dragTabByTrack(Instrumentation inst, int[] xArray,
            int[] yArray, int[] durationArray) {
        long downTime = SystemClock.uptimeMillis();

        for (int i = 0; i < xArray.length && i < yArray.length
                && i < durationArray.length; i++) {
            int typeMotionEvent;
            if (0 == i)
                typeMotionEvent = MotionEvent.ACTION_DOWN;
            else if (xArray.length - 1 == i)
                typeMotionEvent = MotionEvent.ACTION_UP;
            else
                typeMotionEvent = MotionEvent.ACTION_MOVE;

            long eventTime = SystemClock.uptimeMillis();
            MotionEvent event = MotionEvent.obtain(downTime, eventTime,
                    typeMotionEvent, xArray[i], yArray[i], 0);

            inst.sendPointerSync(event);
            if (0 != durationArray[i]) {
                try {
                    Thread.sleep(durationArray[i]);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                }
            }
        }
    }

    public interface DragCallBack {
        void onBefore(Point[] track, Point p, int iteration, int[] changeEvent);

        void onAfter(Point[] track, Point p, int iteration, int[] changeEvent);
    }

    public final static void dragTabByTrack(Instrumentation inst,
            Point[] track, DragCallBack cb) {
        long downTime = SystemClock.uptimeMillis();

        int[] changeEvent = new int[1];
        for (int i = 0, len = track.length; i < len; i++) {
            int typeMotionEvent;
            if (0 == i) {
                typeMotionEvent = MotionEvent.ACTION_DOWN;
            } else if (len - 1 == i) {
                typeMotionEvent = MotionEvent.ACTION_UP;
            } else {
                typeMotionEvent = MotionEvent.ACTION_MOVE;
            }
            changeEvent[0] = typeMotionEvent;

            if (null != cb)
                cb.onBefore(track, track[i], i, changeEvent);
            long eventTime = SystemClock.uptimeMillis();
            MotionEvent event = MotionEvent.obtain(downTime, eventTime,
                    changeEvent[0], track[i].x, track[i].y, 0);
            inst.sendPointerSync(event);
            if (null != cb)
                cb.onAfter(track, track[i], i, changeEvent);
        }
    }

    public interface GestureCallBack {
        void onBefore(Point[][] track, int trackIndex, int iteration,
                      int[] changeEvent);

        void onAfter(Point[][] track, int trackIndex, int iteration,
                     int[] changeEvent);
    }

    private static void initGestureData(int numOfTrack,
            PointerProperties[] properties, PointerCoords[] pointerCoords) {
        // specify the property for the two touch points
        for (int i = 0; i < numOfTrack; i++) {
            PointerProperties p = new PointerProperties();
            properties[i] = p;
            p.id = i;
            p.toolType = MotionEvent.TOOL_TYPE_FINGER;
        }

        // specify the coordinations of the two touch points
        // NOTE: you MUST set the pressure and size value, or it doesn't work
        for (int i = 0; i < numOfTrack; i++) {
            PointerCoords p = new PointerCoords();
            pointerCoords[i] = p;
            p.x = 0;
            p.y = 0;
            p.pressure = 1;
            p.size = 1;
        }
    }

    public final static void dragTabByTrack(Instrumentation inst,
            Point[][] allTrack, GestureCallBack cb) {

        int[] changeEvent = new int[1];

        int maxTrackLen = -1;
        for (Point[] track : allTrack) {
            maxTrackLen = Math.max(maxTrackLen, track.length);
        }

        final int numOfTrack = allTrack.length;
        PointerProperties[] properties = new PointerProperties[numOfTrack];
        PointerCoords[] pointerCoords = new PointerCoords[numOfTrack];
        initGestureData(numOfTrack, properties, pointerCoords);

        long downTime = SystemClock.uptimeMillis();
        int curNumPoint = 1;
        for (int i = 0, numOfTotalPoints = numOfTrack * maxTrackLen; i < numOfTotalPoints; i++) {
            int track = i % numOfTrack;
            int trackLen = allTrack[track].length;
            int trackPosition = (i / numOfTrack);

            int typeMotionEvent;
            if (0 == i) {
                typeMotionEvent = MotionEvent.ACTION_DOWN;
            } else if (numOfTotalPoints - 1 == i) {
                typeMotionEvent = MotionEvent.ACTION_UP;
            } else {
                if (0 == trackPosition) {
                    typeMotionEvent = MotionEvent.ACTION_POINTER_DOWN;
                    curNumPoint++;
                } else if (trackLen - 1 == trackPosition) {
                    typeMotionEvent = MotionEvent.ACTION_POINTER_UP;
                    curNumPoint--;
                } else if (trackPosition > 0 && trackPosition < trackLen - 1) {
                    typeMotionEvent = MotionEvent.ACTION_MOVE;
                } else {
                    continue;
                }
                typeMotionEvent += (track << MotionEvent.ACTION_POINTER_INDEX_SHIFT);
            }
            if (trackPosition >= trackLen)
                continue;
            PointerCoords pc = pointerCoords[track];
            Point p = allTrack[track][trackPosition];
            pc.x = p.x;
            pc.y = p.y;

            changeEvent[0] = typeMotionEvent;

            if (null != cb)
                cb.onBefore(allTrack, track, trackPosition, changeEvent);
            long eventTime = SystemClock.uptimeMillis();
            MotionEvent event = MotionEvent.obtain(downTime, eventTime,
                    changeEvent[0], curNumPoint, properties, pointerCoords, 0,
                    0, 1, 1, 0, 0, 0, 0);
            inst.sendPointerSync(event);
            if (null != cb)
                cb.onAfter(allTrack, track, trackPosition, changeEvent);
        }
    }

    public final static Point[] genDragTrack(Point p1, Point p2, int number) {

        Point[] track = new Point[number];
        int xDiff = p2.x - p1.x;
        int yDiff = p2.y - p1.y;
        int xDiffSlot = xDiff / (number - 1);
        int yDiffSlot = yDiff / (number - 1);

        for (int i = 0, len = track.length; i < len; i++) {
            if (0 == i) {
                track[i] = p1;
            } else if (len - 1 == i) {
                track[i] = p2;
            } else {
                track[i] = new Point(p1.x + xDiffSlot * i, p1.y + yDiffSlot * i);
            }
        }
        return track;
    }

    private final static Point[] mergeDragTrack(Point[][] data) {
        int mergeLength = 0;
        final int dataLength = data.length;
        for (int i = 0; i < dataLength; i++) {
            final Point[] temp = data[i];
            mergeLength += temp.length;
        }

        final Point[] mergedArray = new Point[mergeLength];

        int offset = 0;
        for (int i = 0; i < dataLength; i++) {
            final Point[] temp = data[i];
            final int tempLength = temp.length;
            System.arraycopy(temp, 0, mergedArray, offset, tempLength);
            offset += tempLength;
        }

        return mergedArray;
    }

    public final static Point[] genDragTrack(Point[] pointArray, int[] numberArray) {
        final int length = pointArray.length - 1;
        Point[][] data = new Point[length][];
        for (int i = 0; i < length; i++) {
            data[i] = genDragTrack(pointArray[i], pointArray[i + 1], numberArray[i]);
        }
        return mergeDragTrack(data);
    }
}
