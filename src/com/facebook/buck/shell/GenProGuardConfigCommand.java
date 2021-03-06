/*
 * Copyright 2012-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.facebook.buck.shell;

import com.facebook.buck.util.AndroidPlatformTarget;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class GenProGuardConfigCommand extends ShellCommand {

  private final String androidManifestPath;
  private final Set<String> resDirectories;
  private final String proguardConfigurationPath;

  public GenProGuardConfigCommand(
      String androidManifestPath,
      Set<String> resDirectories,
      String proguardConfigurationPath) {
    this.androidManifestPath = Preconditions.checkNotNull(androidManifestPath);
    this.resDirectories = ImmutableSet.copyOf(resDirectories);
    this.proguardConfigurationPath = Preconditions.checkNotNull(proguardConfigurationPath);
  }

  @Override
  public String getShortName(ExecutionContext context) {
    return "generate proguard.txt";
  }

  @Override
  protected ImmutableList<String> getShellCommandInternal(ExecutionContext context) {
    ImmutableList.Builder<String> args = ImmutableList.builder();
    AndroidPlatformTarget androidPlatformTarget = context.getAndroidPlatformTarget().get();

    args.add(androidPlatformTarget.getAaptExecutable().getAbsolutePath()).add("package");

    // Specify where the ProGuard config should be written.
    args.add("-G").add(proguardConfigurationPath);

    // Add all of the res/ directories.
    for (String res : resDirectories) {
      args.add("-S").add(res);
    }

    // Add the remaining flags.
    args.add("-M").add(androidManifestPath);
    args.add("--auto-add-overlay");
    args.add("-I").add(androidPlatformTarget.getAndroidJar().getAbsolutePath());

    return args.build();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof GenProGuardConfigCommand)) {
      return false;
    }
    GenProGuardConfigCommand that = (GenProGuardConfigCommand) obj;

    return Objects.equal(androidManifestPath, that.androidManifestPath) &&
        Objects.equal(resDirectories, that.resDirectories) &&
        Objects.equal(proguardConfigurationPath, that.proguardConfigurationPath);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(androidManifestPath, resDirectories, proguardConfigurationPath);
  }
}
