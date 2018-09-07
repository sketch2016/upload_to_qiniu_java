#include <stdio.h>
#include "jni.h"
#include "com_example_upload_to_qiniu_AlgorithmUtil.h"

JNIEXPORT jstring JNICALL Java_com_example_upload_1to_1qiniu_AlgorithmUtil_run
  (JNIEnv * env, jobject obj, jbyteArray data) {
  printf("start run algorithm...");
  jstring jstr = env->NewStringUTF("yes");
  //todo

  return jstr;
}
