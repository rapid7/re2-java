#include <re2/re2.h>
#include <boost/pool/object_pool.hpp>
#include <boost/assert.hpp>
#include <cstdio>
#include "RE2.h"

static boost::object_pool<RE2> pool;

JNIEXPORT jlong JNICALL Java_com_logentries_re2_RE2_compileImpl
  (JNIEnv *env, jclass cls, jstring j_str, jobject j_options) {
    RE2::Options options;
    if (j_options != 0) {
        jclass j_options_cls = env->GetObjectClass(j_options);
        jboolean j_posix_syntax = env->GetBooleanField(j_options, env->GetFieldID(j_options_cls, "posixSyntax", "Z"));
    }
    const char *str = env->GetStringUTFChars(j_str, 0);
    RE2 *pointer = pool.construct(str, options);
    env->ReleaseStringUTFChars(j_str, str);
    jlong j_pointer = reinterpret_cast<jlong>(pointer);
    BOOST_VERIFY(reinterpret_cast<RE2*>(j_pointer) == pointer);
    return j_pointer;
}

JNIEXPORT void JNICALL Java_com_logentries_re2_RE2_releaseImpl
  (JNIEnv *env, jclass cls, jlong j_pointer) {
    RE2 *pointer = reinterpret_cast<RE2*>(j_pointer);
    pool.destroy(pointer);
}

JNIEXPORT jboolean JNICALL Java_com_logentries_re2_RE2_fullMatchImpl__Ljava_lang_String_2J_3Ljava_lang_Object_2
  (JNIEnv *env, jclass cls, jstring j_str, jlong j_pointer, jobjectArray j_args) {
    const char *str = env->GetStringUTFChars(j_str, 0);
    RE2 *pointer = reinterpret_cast<RE2*>(j_pointer);
    const jboolean res = static_cast<jboolean>( RE2::FullMatch(str, *pointer) );
    env->ReleaseStringUTFChars(j_str, str);
    return res;
}

JNIEXPORT jboolean JNICALL Java_com_logentries_re2_RE2_partialMatchImpl__Ljava_lang_String_2J_3Ljava_lang_Object_2
  (JNIEnv *env, jclass cls, jstring j_str, jlong j_pointer, jobjectArray j_args) {
    const char *str = env->GetStringUTFChars(j_str, 0);
    RE2 *pointer = reinterpret_cast<RE2*>(j_pointer);
    const jboolean res = static_cast<jboolean>( RE2::PartialMatch(str, *pointer) );
    env->ReleaseStringUTFChars(j_str, str);
    return res;
}

JNIEXPORT jboolean JNICALL Java_com_logentries_re2_RE2_fullMatchImpl__Ljava_lang_String_2Ljava_lang_String_2_3Ljava_lang_Object_2
  (JNIEnv *env, jclass cls, jstring j_str, jstring j_pattern, jobjectArray j_args) {
    const char *str = env->GetStringUTFChars(j_str, 0);
    const char *pattern = env->GetStringUTFChars(j_pattern, 0);
    const jboolean res = static_cast<jboolean>( RE2::FullMatch(str, pattern) );
    env->ReleaseStringUTFChars(j_str, str);
    env->ReleaseStringUTFChars(j_pattern, pattern);
    return res;
}

JNIEXPORT jboolean JNICALL Java_com_logentries_re2_RE2_partialMatchImpl__Ljava_lang_String_2Ljava_lang_String_2_3Ljava_lang_Object_2
  (JNIEnv *env, jclass cls, jstring j_str, jstring j_pattern, jobjectArray j_args) {
    const char *str = env->GetStringUTFChars(j_str, 0);
    const char *pattern = env->GetStringUTFChars(j_pattern, 0);
    const jboolean res = static_cast<jboolean>( RE2::PartialMatch(str, pattern) );
    env->ReleaseStringUTFChars(j_str, str);
    env->ReleaseStringUTFChars(j_pattern, pattern);
    return res;
}
