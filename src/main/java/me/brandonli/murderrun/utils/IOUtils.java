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
package me.brandonli.murderrun.utils;

import static java.util.Objects.requireNonNull;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Formatter;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import me.brandonli.murderrun.MurderRun;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class IOUtils {

  private static final String IP_URL = "https://ipv4.icanhazip.com/";
  private static final long MAX_FILE_SIZE = 100 * 1024 * 1024L;
  private static final long MAX_TOTAL_SIZE = 500 * 1024 * 1024L;

  private IOUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static boolean deleteFileIfExisting(final Path path) {
    if (Files.exists(path)) {
      try {
        return Files.deleteIfExists(path);
      } catch (final IOException e) {
        throw new AssertionError(e);
      }
    }
    return false;
  }

  public static boolean deleteExistingDirectory(final Path path) {
    try {
      MoreFiles.deleteRecursively(path, RecursiveDeleteOption.ALLOW_INSECURE);
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
    return true;
  }

  public static String getPublicIPAddress() {
    final String ip = Bukkit.getIp();
    return ip.isEmpty() ? getPublicIPAddress0() : ip;
  }

  private static String getPublicIPAddress0() {
    try {
      final URI uri = URI.create(IP_URL);
      try (final HttpClient client = HttpClient.newHttpClient()) {
        final HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
        final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        final HttpResponse<String> response = client.send(request, handler);
        final String address = response.body();
        final String encodedAddress = address.trim();
        final URI check = URI.create(encodedAddress);
        final boolean valid = IOUtils.checkValidUrl(check);
        return valid ? address : "localhost";
      }
    } catch (final IOException | InterruptedException e) {
      final Thread current = Thread.currentThread();
      current.interrupt();
      throw new AssertionError(e);
    }
  }

  public static boolean checkValidUrl(final URI uri) {
    try {
      final URL url = uri.toURL();
      final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setRequestMethod("HEAD");
      urlConnection.setConnectTimeout(5000);
      urlConnection.setReadTimeout(5000);
      final int code = urlConnection.getResponseCode();
      return (code == HttpURLConnection.HTTP_OK);
    } catch (final Exception e) {
      return false;
    }
  }

  public static String getSHA1Hash(final URI uri) {
    try {
      @SuppressWarnings("deprecation")
      final HashFunction function = Hashing.sha1();
      final URL url = uri.toURL();
      final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setConnectTimeout(1000);
      urlConnection.setReadTimeout(1000);
      try (final InputStream stream = urlConnection.getInputStream(); final InputStream fast = new FastBufferedInputStream(stream)) {
        final byte[] bytes = fast.readAllBytes();
        final HashCode code = function.hashBytes(bytes);
        final byte[] hash = code.asBytes();
        return bytesToString(hash);
      }
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  private static String bytesToString(final byte[] arr) {
    final StringBuilder builder = new StringBuilder(arr.length * 2);
    try (final Formatter fmt = new Formatter(builder, Locale.ROOT)) {
      for (final byte b : arr) {
        fmt.format("%02x", b & 0xff);
      }
    }
    return builder.toString();
  }

  public static String getSHA1Hash(final Path path) {
    try {
      @SuppressWarnings("deprecation")
      final HashFunction function = Hashing.sha1();
      try (final InputStream stream = Files.newInputStream(path); final InputStream fast = new FastBufferedInputStream(stream)) {
        final byte[] bytes = fast.readAllBytes();
        final HashCode code = function.hashBytes(bytes);
        final byte[] hash = code.asBytes();
        return bytesToHex(hash);
      }
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  public static String bytesToHex(final byte[] bytes) {
    final int size = bytes.length;
    final StringBuilder hexString = new StringBuilder(2 * size);
    for (final byte b : bytes) {
      final String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }

  public static String getFileName(final String url) {
    return url.substring(url.lastIndexOf('/') + 1);
  }

  public static InputStream getResourceAsStream(final String name) {
    final Class<IOUtils> clazz = IOUtils.class;
    final ClassLoader loader = requireNonNull(clazz.getClassLoader());
    final InputStream stream = requireNonNull(loader.getResourceAsStream(name));
    return new FastBufferedInputStream(stream);
  }

  public static String generateFileHash(final Path path) throws IOException {
    @SuppressWarnings("deprecation")
    final HashFunction function = Hashing.sha1();
    try (final InputStream fileStream = Files.newInputStream(path); final InputStream stream = new FastBufferedInputStream(fileStream)) {
      final byte[] bytes = stream.readAllBytes();
      final HashCode code = function.hashBytes(bytes);
      return code.toString();
    }
  }

  public static boolean createFile(final Path path) {
    final Path parent = requireNonNull(path.getParent());
    try {
      Files.createDirectories(parent);
      if (Files.notExists(path)) {
        Files.createFile(path);
        return true;
      }
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
    return false;
  }

  public static boolean createFolder(final Path path) {
    if (Files.notExists(path)) {
      try {
        Files.createDirectories(path);
        return true;
      } catch (final IOException e) {
        throw new AssertionError(e);
      }
    }
    return false;
  }

  public static Path getPluginDataFolderPath() {
    final Plugin plugin = JavaPlugin.getProvidingPlugin(MurderRun.class);
    final File file = plugin.getDataFolder();
    final Path path = file.toPath();
    return path.toAbsolutePath();
  }

  public static Path createTemporaryPath(final String prefix, final String suffix) throws IOException {
    final String os = System.getProperty("os.name").toLowerCase();

    if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
      final Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwx------");
      final FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(permissions);
      return Files.createTempFile(prefix, suffix, attr);
    } else {
      final File parent = new File("murderrun");
      if (!parent.exists() && !parent.mkdirs()) {
        throw new IOException("Failed to create parent directory for temporary file!");
      }

      final File file = File.createTempFile(prefix, suffix, parent);
      final boolean executable = file.setExecutable(true, true);
      final boolean writable = file.setWritable(true, true);
      final boolean readable = file.setReadable(true, true);
      if (!executable || !writable || !readable) {
        throw new IOException("Failed to set file permissions of non-unix system!");
      }

      return file.toPath();
    }
  }

  public static String getName(final Path path) {
    return requireNonNull(path.getFileName()).toString();
  }

  public static void unzip(final Path src, final Path dest) throws IOException {
    long totalSize = 0;
    try (
      final InputStream fis = Files.newInputStream(src);
      final FastBufferedInputStream fast = new FastBufferedInputStream(fis);
      final ZipInputStream zis = new ZipInputStream(fast)
    ) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        final String name = entry.getName();
        final Path path = dest.resolve(name);
        final Path resolvedPath = path.normalize();
        if (!resolvedPath.startsWith(dest)) {
          final String msg = "Invalid Entry %s".formatted(name);
          throw new AssertionError(msg);
        }
        if (entry.isDirectory()) {
          Files.createDirectories(resolvedPath);
        } else {
          final Path parent = resolvedPath.getParent();
          if (parent != null && Files.notExists(parent)) {
            Files.createDirectories(parent);
          }
          try (final OutputStream os = Files.newOutputStream(resolvedPath)) {
            final byte[] buffer = new byte[8192];
            int bytesRead;
            long fileSize = 0;
            while ((bytesRead = zis.read(buffer)) != -1) {
              fileSize += bytesRead;
              totalSize += bytesRead;
              if (fileSize > MAX_FILE_SIZE) {
                final String msg = "File exceeds maximum size: %s".formatted(name);
                throw new AssertionError(msg);
              }
              if (totalSize > MAX_TOTAL_SIZE) {
                final String msg = "Total extracted size exceeds limit";
                throw new AssertionError(msg);
              }
              os.write(buffer, 0, bytesRead);
            }
          }
        }
        zis.closeEntry();
      }
    }
  }

  public static void zip(final Path src, final Path dest) throws IOException {
    try (
      final OutputStream fis = Files.newOutputStream(src);
      final FastBufferedOutputStream fast = new FastBufferedOutputStream(fis);
      final ZipOutputStream zip = new ZipOutputStream(fast)
    ) {
      {
        Files.walkFileTree(
          src,
          new SimpleFileVisitor<>() {
            @Override
            public @NotNull FileVisitResult preVisitDirectory(final Path dir, final @NotNull BasicFileAttributes attrs) throws IOException {
              final Path rel = src.relativize(dir);
              final String relName = rel.toString();
              if (!relName.isEmpty()) {
                final String replaced = relName.replace("\\", "/");
                final String entryName = replaced + "/";
                final ZipEntry entry = new ZipEntry(entryName);
                zip.putNextEntry(entry);
                zip.closeEntry();
              }
              return FileVisitResult.CONTINUE;
            }

            @Override
            public @NotNull FileVisitResult visitFile(final Path file, final @NotNull BasicFileAttributes attrs) throws IOException {
              final Path rel = src.relativize(file);
              final String relName = rel.toString();
              final String replaced = relName.replace("\\", "/");
              final ZipEntry entry = new ZipEntry(replaced);
              zip.putNextEntry(entry);
              Files.copy(file, zip);
              zip.closeEntry();
              return FileVisitResult.CONTINUE;
            }
          }
        );
      }
    }
  }
}
