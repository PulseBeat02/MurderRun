# FAQ

<details>
  <summary>Which Minecraft version does Murder Run support?</summary>
  <p>Murder Run currently supports Minecraft 26.3 on Paper (or a Paper fork) with Java 25. See <a href="setup.html">Setting up a Server</a>.</p>
</details>

<details>
  <summary>When will you support Minecraft version X?</summary>
  <p>Updates to Murder Run do not have any sort of estimate for when they release, ever. Any and all updates will arrive when they are ready, and the only thing to do is wait for them patiently along with everyone else.</p>
</details>

<details>
  <summary>Will you backport?</summary>
  <p>No, Murder Run is actively using new and cool features from latest versions of Minecraft for more fun.</p>
</details>

<details>
  <summary>Why does my server say <code>Unsupported API version 26.3</code>?</summary>
  <p>Your server is running an older Minecraft version than the one this Murder Run build was made for. Update your server to Paper 26.3.</p>
</details>

<details>
  <summary>Murder Run says a required plugin is missing. What do I do?</summary>
  <p>Murder Run downloads WorldEdit, Citizens, and PacketEvents automatically, but that can fail if your server has no internet access or if no build supports your Minecraft version yet. Download a version of the missing plugin that supports 26.3, put it in your plugins folder, and restart the server. See <a href="setup.html#dependencies">Dependencies</a>.</p>
</details>

<details>
  <summary>Nobody can use the Murder Run commands. Why?</summary>
  <p>Murder Run doesn't give any permissions by default, so only operators can use its commands. Grant the permissions from the <a href="commands.html">Commands and Permissions</a> page with a permissions plugin.</p>
</details>

<details>
  <summary><code>/murder game quick-join</code> doesn't do anything. Why?</summary>
  <p>Quick-join is disabled by default. Set <code>enabled: true</code> in <code>quick-join.yml</code> and add an arena-lobby pair. See <a href="configuration.html#quick-join-configuration">Quick Join Configuration</a>.</p>
</details>

<details>
  <summary>Is there a demo I could try?</summary>
  <p><a href="creation.html#using-the-demo">Using the Demo</a></p>
</details>
