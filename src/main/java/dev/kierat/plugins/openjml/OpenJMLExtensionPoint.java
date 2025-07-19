// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package dev.kierat.plugins.openjml;

import com.intellij.openapi.extensions.ExtensionPointName;

public interface OpenJMLExtensionPoint {
  ExtensionPointName<OpenJMLExtensionPoint> EP_NAME = ExtensionPointName.create("dev.kierat.plugins.openjml.openjmlExtensionPoint");
}
