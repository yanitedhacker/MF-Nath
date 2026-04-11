package com.mrbitches.doomsy.llm

class LlamaBridge {

    companion object {
        init {
            System.loadLibrary("doomsy")
        }
    }

    external fun loadModel(modelPath: String, contextSize: Int): Boolean
    external fun generate(prompt: String, maxTokens: Int): String
    external fun unloadModel()
    external fun isLoaded(): Boolean
}
