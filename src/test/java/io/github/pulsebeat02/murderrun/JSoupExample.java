package io.github.pulsebeat02.murderrun;

import static java.util.Objects.requireNonNull;

import java.nio.file.Files;
import java.nio.file.Path;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class JSoupExample {

  public static void main(final String[] args) throws Exception {
    final Path path = Path.of("C:\\Users\\brand\\Downloads\\resourcepack.zip");
    final String name = requireNonNull(path.getFileName()).toString();
    final Connection.Response uploadResponse = Jsoup.connect("https://mc-packs.net/")
      .data("file", name, Files.newInputStream(path))
      .method(Connection.Method.POST)
      .execute();
    final Document document = uploadResponse.parse();
    final Element downloadUrlElement = document.select("input[readonly][value^=https]").first();
    final String downloadUrl = downloadUrlElement != null ? downloadUrlElement.val() : null;
    System.out.println("Download URL: " + downloadUrl);
  }
}
