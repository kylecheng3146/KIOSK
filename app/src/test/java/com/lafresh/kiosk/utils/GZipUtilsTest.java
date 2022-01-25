package com.lafresh.kiosk.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class GZipUtilsTest {

    @Test
    public void compress() {
        String testString = "我是胖虎我是孩子王，我天下無敵";
        String expectString = "H4sIAAAAAAAAAHvWMfHZjPUvmqe9mNn3DMx+unbl07UTnvd1v9/TAxR5um" +
                "Tlkx3dz1sWPpu6FQB5\nOuRiLQAAAA==\n";
        Assert.assertEquals(expectString, GZipUtils.compress(testString));
    }

    @Test
    public void uncompress() {
        String testString = "H4sIAAAAAAAAAHvWMfHZjPUvmqe9mNn3DMx+unbl07UTnvd1v9/TAxR5um" +
                "Tlkx3dz1sWPpu6FQB5\nOuRiLQAAAA==\n";
        String expectString = "我是胖虎我是孩子王，我天下無敵";
        Assert.assertEquals(expectString, GZipUtils.uncompress(testString));
    }
}