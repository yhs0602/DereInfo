#pragma once

#include <jni.h>
#include "../../../cgss_api.h"

void ApplyCipherConfigObject(JNIEnv *env, jobject config, HCA_CIPHER_CONFIG &cfg);
