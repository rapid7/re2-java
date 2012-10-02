#include <re2/re2.h>
#include <boost/pool/object_pool.hpp>
#include <boost/assert.hpp>
#include <cstdio>
#include "RE2.h"
#include "Options.h"

static boost::object_pool<RE2> pool;

template<typename Dst, typename Src>
static Dst safe_cast(Src src) {
    Dst dst = static_cast<Dst>(src);
    BOOST_VERIFY(static_cast<Src>(dst) == src);
    BOOST_VERIFY(dst > 0 == src > 0);
    return dst;
}

RE2::Options::Encoding get_re2_encoding(JNIEnv *env, jobject j_encoding) {
    jclass j_cls = env->FindClass("com/logentries/re2/Encoding");

    jmethodID equals_id = env->GetMethodID(j_cls, "equals", "(Ljava/lang/Object;)Z");

    const char *fields[] = {"UTF8", "Latin1", };
    const RE2::Options::Encoding enc_fields[] = {RE2::Options::EncodingUTF8, RE2::Options::EncodingLatin1, };
    for (int i = 0; i < sizeof(fields)/sizeof(*fields); ++i) {
        jfieldID fid = env->GetStaticFieldID(j_cls, fields[i], "Lcom/logentries/re2/Encoding;");
        jobject item = env->GetStaticObjectField(j_cls, fid);
        if (env->CallBooleanMethod(item, equals_id, j_encoding)) {
            return enc_fields[i];
        }
    }
    BOOST_VERIFY(0);
}

jobject get_j_encoding(JNIEnv *env, RE2::Options::Encoding enc) {
    const char *fields[] = {"UTF8", "Latin1", };
    const RE2::Options::Encoding enc_fields[] = {RE2::Options::EncodingUTF8, RE2::Options::EncodingLatin1, };
    for (int i = 0; i < sizeof(enc_fields)/sizeof(*enc_fields); ++i) {
        RE2::Options::Encoding enc_item = enc_fields[i];
        if (enc_item == enc) {
            jclass j_cls = env->FindClass("com/logentries/re2/Encoding");
            jfieldID fid = env->GetStaticFieldID(j_cls, fields[i], "Lcom/logentries/re2/Encoding;");
            jobject item = env->GetStaticObjectField(j_cls, fid);
            return item;
        }
    }
    BOOST_VERIFY(0);
}


JNIEXPORT void JNICALL Java_com_logentries_re2_Options_setDefaults
  (JNIEnv *env, jobject j_this) {
    RE2::Options options;
    jclass j_cls = env->GetObjectClass(j_this);
    env->SetObjectField(j_this, env->GetFieldID(j_cls, "encoding", "Lcom/logentries/re2/Encoding;"), get_j_encoding(env, options.encoding()));
    env->SetBooleanField(j_this, env->GetFieldID(j_cls, "posixSyntax", "Z"), options.posix_syntax());
    env->SetBooleanField(j_this, env->GetFieldID(j_cls, "longestMatch", "Z"), options.longest_match());
    env->SetBooleanField(j_this, env->GetFieldID(j_cls, "logErrors", "Z"), options.log_errors());
    env->SetLongField(j_this, env->GetFieldID(j_cls, "maxMem", "J"), safe_cast<jlong>(options.max_mem()));
    env->SetBooleanField(j_this, env->GetFieldID(j_cls, "literal", "Z"), options.log_errors());
    env->SetBooleanField(j_this, env->GetFieldID(j_cls, "neverNl", "Z"), options.log_errors());
    env->SetBooleanField(j_this, env->GetFieldID(j_cls, "neverCapture", "Z"), options.log_errors());
    env->SetBooleanField(j_this, env->GetFieldID(j_cls, "caseSensitive", "Z"), options.log_errors());
    env->SetBooleanField(j_this, env->GetFieldID(j_cls, "perlClasses", "Z"), options.log_errors());
    env->SetBooleanField(j_this, env->GetFieldID(j_cls, "wordBoundary", "Z"), options.log_errors());
}

static void cpy_options(RE2::Options &options, JNIEnv *env, jobject j_options) {
    BOOST_VERIFY(j_options != 0);
    jclass j_options_cls = env->GetObjectClass(j_options);
    options.set_encoding(get_re2_encoding(env, env->GetObjectField(j_options, env->GetFieldID(j_options_cls, "encoding", "Lcom/logentries/re2/Encoding;"))));
    options.set_posix_syntax(env->GetBooleanField(j_options, env->GetFieldID(j_options_cls, "posixSyntax", "Z")));
    options.set_longest_match(env->GetBooleanField(j_options, env->GetFieldID(j_options_cls, "longestMatch", "Z")));
    options.set_log_errors(env->GetBooleanField(j_options, env->GetFieldID(j_options_cls, "logErrors", "Z")));
    options.set_max_mem(safe_cast<uint64_t>(env->GetLongField(j_options, env->GetFieldID(j_options_cls, "maxMem", "J"))));
    options.set_literal(env->GetBooleanField(j_options, env->GetFieldID(j_options_cls, "literal", "Z")));
    options.set_log_errors(env->GetBooleanField(j_options, env->GetFieldID(j_options_cls, "neverNl", "Z")));
    options.set_never_capture(env->GetBooleanField(j_options, env->GetFieldID(j_options_cls, "neverCapture", "Z")));
    options.set_case_sensitive(env->GetBooleanField(j_options, env->GetFieldID(j_options_cls, "caseSensitive", "Z")));
    options.set_perl_classes(env->GetBooleanField(j_options, env->GetFieldID(j_options_cls, "perlClasses", "Z")));
    options.set_word_boundary(env->GetBooleanField(j_options, env->GetFieldID(j_options_cls, "wordBoundary", "Z")));
}

class Options : public RE2::Options {
public:
    Options(JNIEnv *env, jobject j_options) {
        if (j_options != 0) {
            cpy_options(*this, env, j_options);
        }
    }
};

JNIEXPORT jlong JNICALL Java_com_logentries_re2_RE2_compileImpl
  (JNIEnv *env, jclass cls, jstring j_str, jobject j_options) {
    Options options(env, j_options);
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
