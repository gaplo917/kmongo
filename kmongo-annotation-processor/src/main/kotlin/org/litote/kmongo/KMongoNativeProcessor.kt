/*
 * Copyright (C) 2017 Litote
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.litote.kmongo

import javax.annotation.processing.RoundEnvironment

/**
 *
 */
internal class KMongoNativeProcessor(val a:KMongoAnnotations) {

    fun processNativeDataClasses(roundEnv: RoundEnvironment): Boolean {
        val classes = a.getAnnotatedClasses<NativeData, NativeDataRegistry>(roundEnv)

        return classes.isNotEmpty()
    }
}