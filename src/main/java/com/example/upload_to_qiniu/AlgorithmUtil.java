package com.example.upload_to_qiniu;

import java.io.File;

public class AlgorithmUtil {
    private static AlgorithmUtil mInstance = null;

    static {
        File file = new File("");
        String path = file.getAbsolutePath() + "/jni/libAlgorithm.so";
        System.load(path);
    }

    private AlgorithmUtil() {}

    public static synchronized AlgorithmUtil getInstance() {
        if (mInstance == null) {
            mInstance = new AlgorithmUtil();
        }

        return mInstance;
    }

    public native String run(byte[] decryptData);
}
