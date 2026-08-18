package com.mrbitches.doomsy.ui.main

fun shouldUseTwoPane(widthDp: Int, heightDp: Int): Boolean {
    return widthDp >= 600 || (widthDp > heightDp && widthDp >= 480)
}
