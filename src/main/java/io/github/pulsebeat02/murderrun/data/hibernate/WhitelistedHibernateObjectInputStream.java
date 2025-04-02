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
package io.github.pulsebeat02.murderrun.data.hibernate;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Set;

public final class WhitelistedHibernateObjectInputStream extends ObjectInputStream {

  private static final Set<String> WHITELISTED_CLASSES = Set.of("[Ljava.lang.Long;");

  public WhitelistedHibernateObjectInputStream(final InputStream in) throws IOException {
    super(in);
  }

  @Override
  protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException {
    final String name = desc.getName();
    if (!WHITELISTED_CLASSES.contains(name)) {
      final String msg = "Unauthorized deserialization attempt for class: %s".formatted(name);
      throw new AssertionError(msg);
    }
    return super.resolveClass(desc);
  }
}
