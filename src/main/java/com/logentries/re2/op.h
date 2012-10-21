#ifndef COM_LOGENTRIES_RE2_OP_H
#   define COM_LOGENTRIES_RE2_OP_H

union JavaRE2_Any {
    jint i_;
    jlong l_;
    jfloat f_;
    jdouble d_;
    char sbuf_[sizeof(re2::StringPiece)]; // union cannot contain member with constructor
                                          // FIXME: Memory alignment

    re2::StringPiece *get_s(void) {
        re2::StringPiece *s = reinterpret_cast<re2::StringPiece*>(sbuf_);
        BOOST_VERIFY( reinterpret_cast<intptr_t>(sbuf_) == reinterpret_cast<intptr_t>(s) );
        return s;
    }

    re2::StringPiece *construct_s(void) {
        re2::StringPiece *s = new(sbuf_) re2::StringPiece();
        BOOST_VERIFY(reinterpret_cast<intptr_t>(s) == reinterpret_cast<intptr_t>(get_s()));
        return s;
    }

    void destroy_s(void) {
       re2::StringPiece *s = get_s();
       s->~StringPiece();
    }
};

enum JavaRE2_AnyType {
    JavaRE2_INT,
    JavaRE2_LONG,
    JavaRE2_FLOAT,
    JavaRE2_DOUBLE,
    JavaRE2_STRING,
};

class JavaRE2_Arg {
private:
    RE2::Arg arg_;
    JavaRE2_Any any_; // arg_ contains pointer to any_
    JavaRE2_AnyType type_;
    jarray j_array_;
    jsize j_index_;

private:
    RE2::Arg init_arg(const JavaRE2_AnyType type) {
        switch (type) {
        case JavaRE2_INT:
            return RE2::Arg(&any_.i_);
        case JavaRE2_LONG:
            return RE2::Arg(&any_.l_);
        case JavaRE2_FLOAT:
            return RE2::Arg(&any_.f_);
        case JavaRE2_DOUBLE:
            return RE2::Arg(&any_.d_);
        case JavaRE2_STRING:
            any_.construct_s();
            return RE2::Arg(any_.get_s());
        default:
            BOOST_VERIFY(0);
        }
    }

public:
    JavaRE2_Arg(JavaRE2_AnyType type, jarray j_array, const jsize j_index)
    :   type_(type),
        arg_(init_arg(type)), // any_ is set here too
        j_array_(j_array),
        j_index_(j_index)
    { }

    void transfer(JNIEnv *env) {
        switch (type_) {
        case JavaRE2_INT: {
                jintArray j_int_arr = static_cast<jintArray>(j_array_);
                env->SetIntArrayRegion(j_int_arr, j_index_, 1, &any_.i_);
            }
            break;
        case JavaRE2_LONG: {
                jlongArray j_long_arr = static_cast<jlongArray>(j_array_);
                env->SetLongArrayRegion(j_long_arr, j_index_, 1, &any_.l_);
            }
            break;
        case JavaRE2_FLOAT: {
                jfloatArray j_float_arr = static_cast<jfloatArray>(j_array_);
                env->SetFloatArrayRegion(j_float_arr, j_index_, 1, &any_.f_);
            }
            break;
        case JavaRE2_DOUBLE: {
                jdoubleArray j_double_arr = static_cast<jdoubleArray>(j_array_);
                env->SetDoubleArrayRegion(j_double_arr, j_index_, 1, &any_.d_);
            }
            break;
        case JavaRE2_STRING: {
                re2::StringPiece *s = any_.get_s();
                jobjectArray j_obj_arr = static_cast<jobjectArray>(j_array_);
                jstring j_str = env->NewStringUTF(s->as_string().c_str());
                env->SetObjectArrayElement(j_obj_arr, j_index_, j_str);
            }
            break;
        default:
            BOOST_VERIFY(0);
        }
    }

    RE2::Arg *get_re2_arg(void) {
        return &arg_;
    }

    ~JavaRE2_Arg(void) {
        if (type_ == JavaRE2_STRING) {
            any_.destroy_s();
        }
    }
};

static bool is_int_arr(JNIEnv *env, jclass j_cls) {
    jclass j_arr_cls = env->FindClass("[I");
    return env->IsAssignableFrom(j_cls, j_arr_cls);
}

static bool is_long_arr(JNIEnv *env, jclass j_cls) {
    jclass j_arr_cls = env->FindClass("[J");
    return env->IsAssignableFrom(j_cls, j_arr_cls);
}

static bool is_float_arr(JNIEnv *env, jclass j_cls) {
    jclass j_arr_cls = env->FindClass("[F");
    return env->IsAssignableFrom(j_cls, j_arr_cls);
}

static bool is_double_arr(JNIEnv *env, jclass j_cls) {
    jclass j_arr_cls = env->FindClass("[D");
    return env->IsAssignableFrom(j_cls, j_arr_cls);
}

static bool is_string_arr(JNIEnv *env, jclass j_cls) {
    jclass j_arr_cls = env->FindClass("[Ljava/lang/String;");
    BOOST_VERIFY(j_arr_cls);
    return env->IsAssignableFrom(j_cls, j_arr_cls);
}

static JavaRE2_AnyType get_type(JNIEnv *env, jobject j_object) {
    jclass j_cls = env->GetObjectClass(j_object);
    if (is_int_arr(env, j_cls)) {
        return JavaRE2_INT;
    }
    if (is_long_arr(env, j_cls)) {
        return JavaRE2_LONG;
    }
    if (is_float_arr(env, j_cls)) {
        return JavaRE2_FLOAT;
    }
    if (is_double_arr(env, j_cls)) {
        return JavaRE2_DOUBLE;
    }
    if (is_string_arr(env, j_cls)) {
        return JavaRE2_STRING;
    }
    BOOST_VERIFY(!"Unexpected parameter supplied"); // Hey, throw exception here ! ;-)
}

static jsize sum_lengths(JNIEnv *env, jobjectArray j_args) {
    jsize j_sum = 0;
    const jsize j_args_length = env->GetArrayLength(j_args);
    for (jsize j_i = 0; j_i < j_args_length; ++j_i) {
        jarray j_arr = static_cast<jarray>(env->GetObjectArrayElement(j_args, j_i));
        j_sum += env->GetArrayLength(j_arr);
    }
    return j_sum;
}

template<typename Op>
static bool do_op(JNIEnv *env, const Op &op, jobjectArray j_args) {
    struct Buf {
        char _[sizeof(JavaRE2_Arg)]; // FIXME: Memory alignment

        JavaRE2_Arg *get_arg(void) {
            JavaRE2_Arg *arg = reinterpret_cast<JavaRE2_Arg*>(_);
            BOOST_VERIFY(reinterpret_cast<intptr_t>(arg) == reinterpret_cast<intptr_t>(_));
            return arg;
        }

        JavaRE2_Arg *construct_arg(JavaRE2_AnyType type, jarray j_array, const jsize j_index) {
            JavaRE2_Arg *arg = new(_) JavaRE2_Arg(type, j_array, j_index);
            BOOST_VERIFY(reinterpret_cast<intptr_t>(arg) == reinterpret_cast<intptr_t>(get_arg()));
            return arg;
        }

        void destroy_arg(void) {
            get_arg()->~JavaRE2_Arg();
        }
    };
    const jsize j_args_len = env->GetArrayLength(j_args);
    const jsize j_total_len = sum_lengths(env, j_args);
    if (j_total_len > 31) { // Magical contant from re2 source code :-)
        BOOST_VERIFY(!"Throw an exception here");
    }

    Buf buf_args[j_total_len];
    RE2::Arg *args[j_total_len];

    for (jsize j_i = 0, j_index = 0; j_i < j_args_len; ++j_i) {
        jarray j_arr = static_cast<jarray>( env->GetObjectArrayElement(j_args, j_i) );
        const jsize j_len = env->GetArrayLength(j_arr);
        const JavaRE2_AnyType type = get_type(env, j_arr);
        for (jsize j_j = 0; j_j < j_len; ++j_j) {
            args[j_index] = buf_args[j_index].construct_arg(type, j_arr, j_j)->get_re2_arg();
            ++j_index;
        }
    }

    const int total_len = static_cast<int>(j_total_len);
    BOOST_VERIFY(static_cast<jsize>(total_len) == j_total_len);
    BOOST_VERIFY(total_len > 0 == j_total_len > 0);
    bool ret = op(args, j_total_len);

    for (jsize j_i = 0; j_i < j_total_len; ++j_i) {
        buf_args[j_i].get_arg()->transfer(env);
        buf_args[j_i].destroy_arg();
    }
    return ret;
}

#endif
