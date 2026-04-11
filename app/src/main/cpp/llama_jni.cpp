#include <jni.h>
#include <android/log.h>
#include <string>
#include <vector>
#include <thread>

#include "llama.h"

#define TAG "DoomsyLLM"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

static llama_model * model = nullptr;
static llama_context * ctx = nullptr;
static llama_sampler * sampler = nullptr;

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_mrbitches_doomsy_llm_LlamaBridge_loadModel(
    JNIEnv *env, jobject, jstring modelPath, jint contextSize
) {
    const char *path = env->GetStringUTFChars(modelPath, nullptr);
    LOGI("Loading model from: %s", path);

    llama_model_params model_params = llama_model_default_params();
    model = llama_model_load_from_file(path, model_params);
    env->ReleaseStringUTFChars(modelPath, path);

    if (!model) {
        LOGE("Failed to load model");
        return JNI_FALSE;
    }

    llama_context_params ctx_params = llama_context_default_params();
    ctx_params.n_ctx = contextSize;
    ctx_params.n_batch = 512;
    ctx_params.n_threads = std::max(1, (int)std::thread::hardware_concurrency() - 2);

    ctx = llama_init_from_model(model, ctx_params);
    if (!ctx) {
        LOGE("Failed to create context");
        llama_model_free(model);
        model = nullptr;
        return JNI_FALSE;
    }

    sampler = llama_sampler_chain_init(llama_sampler_chain_default_params());
    llama_sampler_chain_add(sampler, llama_sampler_init_temp(0.8f));
    llama_sampler_chain_add(sampler, llama_sampler_init_top_p(0.9f, 1));
    llama_sampler_chain_add(sampler, llama_sampler_init_dist(LLAMA_DEFAULT_SEED));

    LOGI("Model loaded successfully");
    return JNI_TRUE;
}

JNIEXPORT jstring JNICALL
Java_com_mrbitches_doomsy_llm_LlamaBridge_generate(
    JNIEnv *env, jobject, jstring prompt, jint maxTokens
) {
    if (!model || !ctx || !sampler) {
        return env->NewStringUTF("[Error: model not loaded]");
    }

    const char *prompt_cstr = env->GetStringUTFChars(prompt, nullptr);
    std::string prompt_str(prompt_cstr);
    env->ReleaseStringUTFChars(prompt, prompt_cstr);

    llama_kv_cache_clear(ctx);

    const llama_vocab * vocab = llama_model_get_vocab(model);
    const int n_prompt_max = prompt_str.size() * 2 + 32;
    std::vector<llama_token> tokens(n_prompt_max);
    const int n_tokens = llama_tokenize(vocab, prompt_str.c_str(), prompt_str.size(),
                                         tokens.data(), n_prompt_max, true, true);
    if (n_tokens < 0) {
        LOGE("Tokenization failed: n_tokens=%d", n_tokens);
        return env->NewStringUTF("[Error: tokenization failed]");
    }
    tokens.resize(n_tokens);

    LOGI("Prompt tokens: %d, context size: %d", n_tokens, (int)llama_n_ctx(ctx));

    if (n_tokens >= (int)llama_n_ctx(ctx)) {
        LOGE("Prompt too long for context: %d >= %d", n_tokens, (int)llama_n_ctx(ctx));
        return env->NewStringUTF("[Error: prompt too long]");
    }

    llama_batch batch = llama_batch_get_one(tokens.data(), n_tokens);
    if (llama_decode(ctx, batch) != 0) {
        LOGE("Initial decode failed for %d tokens", n_tokens);
        return env->NewStringUTF("[Error: decode failed]");
    }

    std::string result;

    for (int i = 0; i < maxTokens; i++) {
        llama_token new_token = llama_sampler_sample(sampler, ctx, -1);

        if (llama_vocab_is_eog(vocab, new_token)) {
            break;
        }

        char buf[256];
        int n = llama_token_to_piece(vocab, new_token, buf, sizeof(buf), 0, true);
        if (n > 0) {
            result.append(buf, n);
        }

        llama_batch next = llama_batch_get_one(&new_token, 1);
        if (llama_decode(ctx, next) != 0) {
            LOGE("Decode failed during generation");
            break;
        }
    }

    LOGI("Generated %zu chars", result.size());
    return env->NewStringUTF(result.c_str());
}

JNIEXPORT void JNICALL
Java_com_mrbitches_doomsy_llm_LlamaBridge_unloadModel(
    JNIEnv *env, jobject
) {
    if (sampler) { llama_sampler_free(sampler); sampler = nullptr; }
    if (ctx) { llama_free(ctx); ctx = nullptr; }
    if (model) { llama_model_free(model); model = nullptr; }
    LOGI("Model unloaded");
}

JNIEXPORT jboolean JNICALL
Java_com_mrbitches_doomsy_llm_LlamaBridge_isLoaded(
    JNIEnv *env, jobject
) {
    return (model != nullptr && ctx != nullptr) ? JNI_TRUE : JNI_FALSE;
}

} // extern "C"
