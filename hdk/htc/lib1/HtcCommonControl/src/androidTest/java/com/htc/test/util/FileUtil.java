
package com.htc.test.util;

import android.view.View;

import com.robotium.solo.Solo;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FileUtil {

    private static void saveInfo(String info, String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(info);
            bw.flush();
            bw.close();
            fw.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    private static String loadInfo(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return null;
            }
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String info = br.readLine();
            br.close();
            fr.close();
            return info;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

    public static void AssertInfoEqualBefore(Solo solo,
            final View view, final String info, TestCase testcase) {

        if (null != solo && null != view)
        {
            ScreenShotUtil.setResolutionInformation(solo, view);
        }

        String name = ScreenShotUtil.getScreenShotName(testcase);

        String expectedPath = ScreenShotUtil.getExpectSetFilePath(false, name,
                ScreenShotUtil.FILETYPE_TXT);
        String expectedResolutionPath = ScreenShotUtil.getExpectSetFilePath(true, name,
                ScreenShotUtil.FILETYPE_TXT);
        String currentPath = ScreenShotUtil.getScreenShotFilePath(name,
                ScreenShotUtil.FILETYPE_TXT);

        String currentInfo = info;
        FileUtil.saveInfo(currentInfo, currentPath);

        String expectedInfo = null;
        if (null != expectedPath)
        {
            expectedInfo = FileUtil.loadInfo(expectedPath);
        }

        String expectedRsolutionInfo = null;
        if (null != expectedResolutionPath)
        {
            expectedRsolutionInfo = FileUtil.loadInfo(expectedResolutionPath);
        }

        Assert.assertTrue("Both of " + expectedPath + " and " + expectedResolutionPath
                + " are null ", (null != expectedInfo || null != expectedRsolutionInfo));

        if (null != expectedInfo) {
            Assert.assertEquals(
                    "\"" + expectedInfo + "\" are not the same \"" + currentInfo + "\"",
                    expectedInfo, currentInfo);
        }

        if (null != expectedRsolutionInfo) {
            Assert.assertEquals("\"" + expectedRsolutionInfo + "\" are not the same \""
                    + currentInfo + "\"", expectedRsolutionInfo, currentInfo);
        }
    }
}
