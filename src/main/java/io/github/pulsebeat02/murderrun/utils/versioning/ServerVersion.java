/*

MIT License

Copyright (c) 2025 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.utils.versioning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ServerVersion {
  V_1_7_10,
  V_1_8,
  V_1_8_3,
  V_1_8_8,
  V_1_9,
  V_1_9_1,
  V_1_9_2,
  V_1_9_4,
  V_1_10,
  V_1_10_1,
  V_1_10_2,
  V_1_11,
  V_1_11_2,
  V_1_12,
  V_1_12_1,
  V_1_12_2,
  V_1_13,
  V_1_13_1,
  V_1_13_2,
  V_1_14,
  V_1_14_1,
  V_1_14_2,
  V_1_14_3,
  V_1_14_4,
  V_1_15,
  V_1_15_1,
  V_1_15_2,
  V_1_16,
  V_1_16_1,
  V_1_16_2,
  V_1_16_3,
  V_1_16_4,
  V_1_16_5,
  V_1_17,
  V_1_17_1,
  V_1_18,
  V_1_18_1,
  V_1_18_2,
  V_1_19,
  V_1_19_1,
  V_1_19_2,
  V_1_19_3,
  V_1_19_4,
  V_1_20,
  V_1_20_1,
  V_1_20_2,
  V_1_20_3,
  V_1_20_4,
  V_1_20_5,
  V_1_20_6,
  V_1_21,
  V_1_21_1,
  V_1_21_2,
  V_1_21_3,
  V_1_21_4,
  V_1_21_5,
  ERROR;

  private static final ServerVersion[] REVERSED;

  static {
    final ServerVersion[] values = ServerVersion.values();
    final List<ServerVersion> list = Arrays.asList(values);
    final List<ServerVersion> sublist = list.subList(0, list.size() - 1);
    final List<ServerVersion> reversed = new ArrayList<>(sublist);
    Collections.reverse(reversed);
    REVERSED = reversed.toArray(new ServerVersion[0]);
  }

  private final String name;

  ServerVersion() {
    final String name = this.name();
    final String sub = name.substring(2);
    this.name = sub.replace("_", ".");
  }

  public String getReleaseName() {
    return this.name;
  }

  public static ServerVersion[] getReversed() {
    return REVERSED;
  }
}
