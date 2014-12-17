/*
 *      Java Bindings for the RE2 Library
 *
 *      (c) 2012 Daniel Fiala <danfiala@ucw.cz>
 *
 */

#include <re2/re2.h>
#include <boost/assert.hpp>
#include <new>
#include <cstdio>
#include <string>
#include <map>
#include <iostream>
#include "RE2.h"
#include "op.h"
#include "Options.h"

using re2::StringPiece;
using namespace std;

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

static jfieldID get_field_id_safe(JNIEnv *env, jclass j_cls, const char *name, const char *sig) {
    jfieldID fid = env->GetFieldID(j_cls, name, sig);
    BOOST_VERIFY(fid != NULL);
    return fid;
}

JNIEXPORT void JNICALL Java_com_logentries_re2_Options_setDefaults
  (JNIEnv *env, jobject j_this) {
    RE2::Options options;
    jclass j_cls = env->GetObjectClass(j_this);
    env->SetObjectField(j_this, get_field_id_safe(env, j_cls, "encoding", "Lcom/logentries/re2/Encoding;"), get_j_encoding(env, options.encoding()));
    env->SetBooleanField(j_this, get_field_id_safe(env, j_cls, "posixSyntax", "Z"), options.posix_syntax());
    env->SetBooleanField(j_this, get_field_id_safe(env, j_cls, "longestMatch", "Z"), options.longest_match());
    env->SetBooleanField(j_this, get_field_id_safe(env, j_cls, "logErrors", "Z"), options.log_errors());
    env->SetLongField(j_this, get_field_id_safe(env, j_cls, "maxMem", "J"), safe_cast<jlong>(options.max_mem()));
    env->SetBooleanField(j_this, get_field_id_safe(env, j_cls, "literal", "Z"), options.literal());
    env->SetBooleanField(j_this, get_field_id_safe(env, j_cls, "neverNl", "Z"), options.never_nl());
    env->SetBooleanField(j_this, get_field_id_safe(env, j_cls, "neverCapture", "Z"), options.never_capture());
    env->SetBooleanField(j_this, get_field_id_safe(env, j_cls, "caseSensitive", "Z"), options.case_sensitive());
    env->SetBooleanField(j_this, get_field_id_safe(env, j_cls, "perlClasses", "Z"), options.perl_classes());
    env->SetBooleanField(j_this, get_field_id_safe(env, j_cls, "wordBoundary", "Z"), options.word_boundary());
}

static void cpy_options(RE2::Options &options, JNIEnv *env, jobject j_options) {
    BOOST_VERIFY(j_options != 0);
    jclass j_options_cls = env->GetObjectClass(j_options);
    options.set_encoding(get_re2_encoding(env, env->GetObjectField(j_options, get_field_id_safe(env, j_options_cls, "encoding", "Lcom/logentries/re2/Encoding;"))));
    options.set_posix_syntax(env->GetBooleanField(j_options, get_field_id_safe(env, j_options_cls, "posixSyntax", "Z")));
    options.set_longest_match(env->GetBooleanField(j_options, get_field_id_safe(env, j_options_cls, "longestMatch", "Z")));
    options.set_log_errors(env->GetBooleanField(j_options, get_field_id_safe(env, j_options_cls, "logErrors", "Z")));
    options.set_max_mem(safe_cast<uint64_t>(env->GetLongField(j_options, get_field_id_safe(env, j_options_cls, "maxMem", "J"))));
    options.set_literal(env->GetBooleanField(j_options, get_field_id_safe(env, j_options_cls, "literal", "Z")));
    options.set_never_nl(env->GetBooleanField(j_options, get_field_id_safe(env, j_options_cls, "neverNl", "Z")));
    options.set_never_capture(env->GetBooleanField(j_options, get_field_id_safe(env, j_options_cls, "neverCapture", "Z")));
    options.set_case_sensitive(env->GetBooleanField(j_options, get_field_id_safe(env, j_options_cls, "caseSensitive", "Z")));
    options.set_perl_classes(env->GetBooleanField(j_options, get_field_id_safe(env, j_options_cls, "perlClasses", "Z")));
    options.set_word_boundary(env->GetBooleanField(j_options, get_field_id_safe(env, j_options_cls, "wordBoundary", "Z")));
}

class Options : public RE2::Options {
public:
    Options(JNIEnv *env, jobject j_options) {
        if (j_options != 0) {
            cpy_options(*this, env, j_options);
        }
    }
};

static bool is_empty_arr(JNIEnv *env, jarray j_arr) {
    return j_arr == 0 || env->GetArrayLength(j_arr) == 0;
    
}

static bool throw_RegExprException(JNIEnv *env, const char *msg) {
    const char *class_name = "com/logentries/re2/RegExprException" ;

    jclass j_cls = env->FindClass(class_name);
    if (j_cls == NULL) {
        BOOST_VERIFY(!"Cannot find exception class :-(");
    }

    return env->ThrowNew(j_cls, msg) == 0;
}

JNIEXPORT jlong JNICALL Java_com_logentries_re2_RE2_compileImpl
  (JNIEnv *env, jclass cls, jstring j_str, jobject j_options) {
    Options options(env, j_options);
    const char *str = env->GetStringUTFChars(j_str, 0);
    RE2 *pointer = new RE2(str, options);
    if (pointer->ok()) {
        env->ReleaseStringUTFChars(j_str, str);
        jlong j_pointer = reinterpret_cast<jlong>(pointer);
        BOOST_VERIFY(reinterpret_cast<RE2*>(j_pointer) == pointer);
        return j_pointer;
    } else {
        throw_RegExprException(env, pointer->error().c_str());
        delete pointer;
        return 0;
    }
}

JNIEXPORT void JNICALL Java_com_logentries_re2_RE2_releaseImpl
  (JNIEnv *env, jclass cls, jlong j_pointer) {
    RE2 *pointer = reinterpret_cast<RE2*>(j_pointer);
    //pool.destroy(pointer);
    delete pointer;
}

struct FullMatchCOp {
    const char *str_;
    const RE2 *pattern_;

    FullMatchCOp(const char *str, const RE2 *pattern)
    :   str_(str),
        pattern_(pattern)
    { }

    bool operator()(const RE2::Arg* const args[], const int n) const {
        return RE2::FullMatchN(str_, *pattern_, args, n);
    }
};

JNIEXPORT jboolean JNICALL Java_com_logentries_re2_RE2_fullMatchImpl__Ljava_lang_String_2J_3Ljava_lang_Object_2
  (JNIEnv *env, jclass cls, jstring j_str, jlong j_pointer, jobjectArray j_args) {
    const char *str = env->GetStringUTFChars(j_str, 0);
    RE2 *pointer = reinterpret_cast<RE2*>(j_pointer);
    const bool res = is_empty_arr(env, j_args) ? RE2::FullMatch(str, *pointer) : do_op(env, FullMatchCOp(str, pointer), j_args);
    env->ReleaseStringUTFChars(j_str, str);
    return static_cast<jboolean>(res);
}

struct PartialMatchCOp {
    const char *str_;
    const RE2 *pattern_;

    PartialMatchCOp(const char *str, const RE2 *pattern)
    :   str_(str),
        pattern_(pattern)
    { }

    bool operator()(const RE2::Arg* const args[], const int n) const {
        return RE2::PartialMatchN(str_, *pattern_, args, n);
    }
};

JNIEXPORT jboolean JNICALL Java_com_logentries_re2_RE2_partialMatchImpl__Ljava_lang_String_2J_3Ljava_lang_Object_2
  (JNIEnv *env, jclass cls, jstring j_str, jlong j_pointer, jobjectArray j_args) {
    const char *str = env->GetStringUTFChars(j_str, 0);
    RE2 *pointer = reinterpret_cast<RE2*>(j_pointer);
    const bool res = is_empty_arr(env, j_args) ? RE2::PartialMatch(str, *pointer) : do_op(env, PartialMatchCOp(str, pointer), j_args);
    env->ReleaseStringUTFChars(j_str, str);
    return static_cast<jboolean>(res);
}

struct FullMatchOp {
    const char *str_;
    const char *pattern_;

    FullMatchOp(const char *str, const char *pattern)
    :   str_(str),
        pattern_(pattern)
    { }

    bool operator()(const RE2::Arg* const args[], const int n) const {
        return RE2::FullMatchN(str_, pattern_, args, n);
    }
};

JNIEXPORT jboolean JNICALL Java_com_logentries_re2_RE2_fullMatchImpl__Ljava_lang_String_2Ljava_lang_String_2_3Ljava_lang_Object_2
  (JNIEnv *env, jclass cls, jstring j_str, jstring j_pattern, jobjectArray j_args) {
    const char *str = env->GetStringUTFChars(j_str, 0);
    const char *pattern = env->GetStringUTFChars(j_pattern, 0);
    const bool res = is_empty_arr(env, j_args) ? RE2::FullMatch(str, pattern) : do_op(env, FullMatchOp(str, pattern), j_args);
    env->ReleaseStringUTFChars(j_str, str);
    env->ReleaseStringUTFChars(j_pattern, pattern);
    return static_cast<jboolean>(res);
}

struct PartialMatchOp {
    const char *str_;
    const char *pattern_;

    PartialMatchOp(const char *str, const char *pattern)
    :   str_(str),
        pattern_(pattern)
    { }

    bool operator()(const RE2::Arg* const args[], const int n) const {
        return RE2::PartialMatchN(str_, pattern_, args, n);
    }
};

JNIEXPORT jboolean JNICALL Java_com_logentries_re2_RE2_partialMatchImpl__Ljava_lang_String_2Ljava_lang_String_2_3Ljava_lang_Object_2
  (JNIEnv *env, jclass cls, jstring j_str, jstring j_pattern, jobjectArray j_args) {
    const char *str = env->GetStringUTFChars(j_str, 0);
    const char *pattern = env->GetStringUTFChars(j_pattern, 0);
    const bool res = is_empty_arr(env, j_args) ? RE2::PartialMatch(str, pattern) : do_op(env, PartialMatchOp(str, pattern), j_args);
    env->ReleaseStringUTFChars(j_str, str);
    env->ReleaseStringUTFChars(j_pattern, pattern);
    return static_cast<jboolean>(res);
}

JNIEXPORT jobject JNICALL Java_com_logentries_re2_RE2_getCaptureGroupNamesImpl
  (JNIEnv *env, jclass cls, jlong j_pointer, jobjectArray j_args) {
    RE2 *pointer = reinterpret_cast<RE2*>(j_pointer);

    jclass j_array_list = env->FindClass("java/util/ArrayList");
    if (j_array_list == NULL) return NULL;

    jmethodID arrayListCtor = env->GetMethodID(j_array_list, "<init>", "()V");
    jmethodID add = env->GetMethodID(j_array_list, "add", "(Ljava/lang/Object;)Z");
    jobject java_array_list = env->NewObject(j_array_list, arrayListCtor);

    map<int, string> groupNames = (pointer->CapturingGroupNames());
    map<int, string>::iterator it;

    for (it = groupNames.begin(); it != groupNames.end(); ++it) {
		jstring jvalue = env->NewStringUTF(it->second.c_str());

		env->CallObjectMethod(java_array_list, add, jvalue);
    };

    return java_array_list;
}

JNIEXPORT jint JNICALL Java_com_logentries_re2_RE2_numberOfCapturingGroupsImpl
  (JNIEnv *env, jclass cls, jlong re2_pointer) {

    RE2 *regex = reinterpret_cast<RE2*>(re2_pointer);
    return static_cast<jint>(regex->NumberOfCapturingGroups());
}

JNIEXPORT jlong JNICALL Java_com_logentries_re2_RE2String_createStringBuffer
  (JNIEnv *env, jclass cls, jbyteArray input) {
//    const char *str = env->GetStringUTFChars(input, 0);
    char* str = (char*) env->GetByteArrayElements(input, 0);
    return reinterpret_cast<jlong>(str);
}


JNIEXPORT void JNICALL Java_com_logentries_re2_RE2String_releaseStringBuffer
  (JNIEnv *env, jclass cls, jbyteArray input, jlong j_pointer) {
    char *pointer = reinterpret_cast<char*>(j_pointer);
    env->ReleaseByteArrayElements(input, (jbyte*)pointer, JNI_ABORT);
}

static const int stackSize = 16 + 1; // see 'kVecSize' in re2.cc

JNIEXPORT jboolean JNICALL Java_com_logentries_re2_RE2Matcher_findImpl
  (JNIEnv *env, jclass cls, jobject matcher, jlong re2_pointer, jlong str_pointer, jint ngroups, jint start, jint end) {


    RE2 *regex = reinterpret_cast<RE2*>(re2_pointer);
    char *str = reinterpret_cast<char*>(str_pointer);

    StringPiece* groups;
    StringPiece stackgroups[stackSize];
    StringPiece* heapgroups = NULL;

    if (ngroups <= stackSize) {
        groups = stackgroups;
    } else {
        groups = new StringPiece[ngroups];
        heapgroups = groups;
    }

    StringPiece text(str);
    const bool res = regex->Match(text, start, end, RE2::UNANCHORED, groups, ngroups);
    if (res) {
        jclass matcher_class = env->FindClass("com/logentries/re2/RE2Matcher");
        jmethodID addID = env->GetStaticMethodID(matcher_class, "addGroup", "(Lcom/logentries/re2/RE2Matcher;II)V");
        for (int i=0; i<ngroups; i++) {
            if (groups[i] != NULL) {
                env->CallStaticObjectMethod(
                    matcher_class,
                    addID,
                    matcher,
                    static_cast<jint>(groups[i].data() - str),
                    static_cast<jint>(groups[i].data() - str + groups[i].size())
                );
            } else {
                env->CallStaticObjectMethod(matcher_class, addID,
                    matcher, static_cast<jint>(-1), static_cast<jint>(-1));
            }
        }
    }

    delete[] heapgroups;
    return static_cast<jboolean>(res);
}