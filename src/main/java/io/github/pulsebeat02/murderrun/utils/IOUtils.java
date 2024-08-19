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
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class IOUtils {

  private IOUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
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

  public static Path createFile(final Path path) {
    final Path parent = requireNonNull(path.getParent());
    try {
      Files.createDirectories(parent);
      if (Files.notExists(path)) {
        Files.createFile(path);
      }
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
    return path;
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
}
