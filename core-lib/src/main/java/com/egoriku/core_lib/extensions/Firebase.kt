package com.egoriku.core_lib.extensions

import com.google.firebase.database.DataSnapshot

inline fun <reified T> DataSnapshot.toModelOfType() = getValue(T::class.java)