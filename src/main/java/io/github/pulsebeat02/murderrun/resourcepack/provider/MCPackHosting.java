package io.github.pulsebeat02.murderrun.resourcepack.provider;

import com.google.gson.Gson;
import io.github.pulsebeat02.murderrun.json.GsonProvider;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// im not dealing with this... checker framework...
@SuppressWarnings("all")
public final class MCPackHosting extends ResourcePackProvider {

  private static final String WEBSITE_URL = "https://mc-packs.net/";
  private static final String DOWNLOAD_REGEX = "input[readonly][value^=https]";

  private final Lock readLock;
  private final Lock writeLock;

  public MCPackHosting() {
    super(ProviderMethod.MC_PACK_HOSTING);
    final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    this.readLock = lock.readLock();
    this.writeLock = lock.writeLock();
  }

  @Override
  String getRawUrl(@UnderInitialization MCPackHosting this) {
    final PackInfo info = this.checkFileUrl();
    return this.updateAndRetrievePackJSON(info == null ? this.createNewPackInfo() : info);
  }

  private PackInfo createNewPackInfo() {
    final Path zip = this.getZip();
    final String name = IOUtils.getName(zip);
    try (final InputStream stream = Files.newInputStream(zip);
        final InputStream fast = new FastBufferedInputStream(stream)) {
      final Response uploadResponse = this.getResponse(name, fast);
      final Document document = uploadResponse.parse();
      final String url = this.getDownloadUrl(document);
      return new PackInfo(url, 0);
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  private String updateAndRetrievePackJSON(final PackInfo info) {
    try {
      final Path path = this.getCachedFilePath();
      final int loads = info.loads + 1;
      final PackInfo updated = new PackInfo(info.url, loads);
      final Gson gson = GsonProvider.getGson();
      this.writeLock.lock();
      gson.toJson(updated, Files.newBufferedWriter(path));
      return updated.url;
    } catch (final IOException e) {
      throw new AssertionError(e);
    } finally {
      this.writeLock.unlock();
    }
  }

  private @Nullable PackInfo checkFileUrl() {

    final Path path = this.getCachedFilePath();
    if (IOUtils.createFolder(path)) {
      return null;
    }

    final Gson gson = GsonProvider.getGson();
    this.readLock.lock();

    try (final Reader reader = Files.newBufferedReader(path)) {

      final PackInfo info = gson.fromJson(reader, PackInfo.class);
      if (info == null) {
        return null;
      }

      final int serverLoads = info.loads;
      return serverLoads > 10 ? null : info;

    } catch (final IOException e) {
      throw new AssertionError(e);
    } finally {
      this.readLock.unlock();
    }
  }

  private Path getCachedFilePath() {
    final Path data = IOUtils.getPluginDataFolderPath();
    return data.resolve("cached-packs.json");
  }

  private String getDownloadUrl(final Document document) {
    final Elements elements = document.select(DOWNLOAD_REGEX);
    final Element element = elements.first();
    if (element == null) {
      throw new IllegalStateException("Download URL not found!");
    }
    return element.val();
  }

  private Response getResponse(final String name, final InputStream fast) throws IOException {
    return Jsoup.connect(WEBSITE_URL)
        .data("file", name, fast)
        .method(Method.POST)
        .execute();
  }

  record PackInfo(String url, int loads) {}
}
