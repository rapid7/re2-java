#include "Options.h"

#include <re2/re2.h>

JNIEXPORT void JNICALL Java_com_logentries_re2_Options_setDefaults
  (JNIEnv *env, jobject j_this) {
    RE2::Options options;
    jclass j_cls = env->GetObjectClass(j_this);
    env->SetBooleanField(j_this, env->GetFieldID(j_cls, "posixSyntax", "Z"), options.posix_syntax());

#if 0
    Encoding encoding_;
    bool posix_syntax_; //*
    bool longest_match_;
    bool log_errors_;
    int64_t max_mem_;
    bool literal_;
    bool never_nl_;
    bool never_capture_;
    bool case_sensitive_;
    bool perl_classes_;
    bool word_boundary_;
    bool one_line_;
#endif    
}

