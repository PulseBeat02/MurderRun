package io.github.pulsebeat02.murderrun.utils;

import static java.util.Objects.requireNonNull;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import io.github.pulsebeat02.murderrun.MurderRun;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.creative.base.Writable;

public final class IOUtils {

  private IOUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static Writable getWritableStreamFromResource(final String path) {

    final Path dataFolder = getPluginDataFolderPath();
    final Path filePath = dataFolder.resolve(path);
    if (Files.notExists(filePath)) {
      createFile(filePath);
      try (final InputStream stream = IOUtils.getResourceAsStream(path);
          final FastBufferedInputStream fast = new FastBufferedInputStream(stream)) {
        Files.copy(fast, filePath, StandardCopyOption.REPLACE_EXISTING);
      } catch (final IOException e) {
        throw new AssertionError(e);
      }
    }

    try (final InputStream stream = Files.newInputStream(filePath);
        final FastBufferedInputStream fast = new FastBufferedInputStream(stream)) {
      return Writable.copyInputStream(fast);
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  public static InputStream getResourceAsStream(final String name) {
    final Class<IOUtils> clazz = IOUtils.class;
    final ClassLoader loader = requireNonNull(clazz.getClassLoader());
    final InputStream stream = requireNonNull(loader.getResourceAsStream(name));
    return new FastBufferedInputStream(stream);
  }

  public static String generateFileHash(final Path path)
      throws IOException, NoSuchAlgorithmException {
    final HashFunction function = Hashing.sha1();
    try (final InputStream fileStream = Files.newInputStream(path);
        final InputStream stream = new FastBufferedInputStream(fileStream)) {
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

  public static Path createTemporaryPath(final String prefix, final String suffix)
      throws IOException {

    final String os = System.getProperty("os.name").toLowerCase();

    if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {

      final Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwx------");
      final FileAttribute<Set<PosixFilePermission>> attr =
          PosixFilePermissions.asFileAttribute(permissions);
      return Files.createTempFile(prefix, suffix, attr);

    } else {

      final File parent = new File("murderrun");
      if (!parent.exists()) {
        if (!parent.mkdirs()) {
          throw new IOException("Failed to create parent directory for temporary file!");
        }
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
}
