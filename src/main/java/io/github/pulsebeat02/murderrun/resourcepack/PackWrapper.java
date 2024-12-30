/*

MIT License

Copyright (c) 2024 Brandon Li

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
package io.github.pulsebeat02.murderrun.resourcepack;

import static java.util.Objects.requireNonNull;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PackWrapper {

  private final Path path;

  public PackWrapper(final Path path) {
    this.path = path;
  }

  public void wrapPack() throws IOException {
    final Path pack = this.extractPackResource();
    IOUtils.zipFolderContents(pack, this.path);
  }

  private Path extractPackResource() throws IOException {
    final Path data = IOUtils.getPluginDataFolderPath();
    final Path pack = data.resolve("pack");
    if (IOUtils.createFolder(pack)) {
      final Class<?> clazz = this.getClass();
      final ClassLoader loader = requireNonNull(clazz.getClassLoader());
      final ClassGraph graph = new ClassGraph().addClassLoader(loader);
      final ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();
      try (final ScanResult result = graph.scan(service, 64)) {
        this.scanJarResources(result, pack);
      }
    }
    return pack;
  }

  private void scanJarResources(final ScanResult result, final Path pack) throws IOException {
    final ResourceList list = result.getAllResources();
    final Path parent = requireNonNull(pack.getParent());
    for (final Resource resource : list) {
      final String path = resource.getPath();
      if (path.startsWith("pack/"))  {
        try (final InputStream stream = IOUtils.getResourceAsStream(path)) {
          final Path file = parent.resolve(path);
          final Path fileParent = requireNonNull(file.getParent());
          Files.createDirectories(fileParent);
          Files.copy(stream, file, StandardCopyOption.REPLACE_EXISTING);
        }
      }
    }
  }
}
