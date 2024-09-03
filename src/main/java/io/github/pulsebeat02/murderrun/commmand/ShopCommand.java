package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.gui.shop.NPCShopEvent;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.MetadataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.SkinTrait;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public final class ShopCommand implements AnnotationCommandFeature {

  private static final String GUARDIAN_ANGEL_TEXTURE_SIGNATURE =
      "UtWIAWpYWnLB5NHsnk5GZWPlJ53vV3Pkt4RrUxt1NV/PktQRXMSjWfsDMEPWBiXkRATfITH0xH8uuaIgslG0e00xVs4j3yxS59Y6wNP9DGRzlpzu7gxNVo0uJB3zuGM9/VRawvfZEivIcHDN8DbRaZ3hbpaJQhRZnWuzVJZ5gBbqGwhdjZvdiDZqgxMIhttOZauT3IJQr/KPusrs2IgkGE6Jbm0Dt2mVEM8pOjh10O1vfokGDMWA2vOEpdTRRsQThGPl2981AIY+dDoJ0kOHikgDmtKpW97OVy84ECpKCoE4GaD8H39+DAhGnb5cV4gOirxyJJ5qbA1WxUkBFjZ+ZnuXOVYozarr32bIFPoqLxKC8cfKvtj7KLPeHsPGyqVEI6hFuJRe7RMqLrnYM2rhJP54JZkJqlMSb3e2M8TCl7XjMWPzYU6b1dQowAsdXDrWVzp3T2JD93aCCvvid35msuKhs7+2+NDtTW5sgCuT60s1r3Yf9De8aM2NUPMABrB/OAsf0aaP4s4AbE+QCKcSyqwbBpsJUx0kR5DLgEuCMR9N3OT/2CVF1fo54ph2nhy1lK5XznKGw+alWU/W+FBH8uqsAu7BwLTBe25Suj4IRPD3KOarYY2X3ABD30c6bw5BTQFJmbCkYTUdsxXhq+lFQIdDNlxPx4tESRC17jpFFKY=";
  private static final String GUARDIAN_ANGEL_TEXTURE_DATA =
      "ewogICJ0aW1lc3RhbXAiIDogMTcwODY4Mjc5NzI4NCwKICAicHJvZmlsZUlkIiA6ICI2NGRiNmMwNTliOTk0OTM2YTY0M2QwODEwODE0ZmJkMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVTaWx2ZXJEcmVhbXMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDQ3MDZmN2QyYWYyYjk3ZjU1YmZiZTdiZjRmODg4ZjA5N2UxMzE3M2Q3MmI2OGYzMTg5NmQ3ZjI2NmQxZjZhZiIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9";

  private static final String GRIM_REAPER_TEXTURE_SIGNATURE =
      "UmQ5TKdiPotlrIkFdJwGDlQH1HVqOw4wAI0hnXAkluppRRT0fWhHirTVH2m+dTm0sPjRcEyLrFwThAQ3vFXkaYrl17B2QuJjtnalriBPDzJFevOAcErzDixvN0ECVJ28oKA4Qixcdgj+fJNukuq1+U/pv4P95Lp/3EwOyrhn7h/1USob+o8v9qIpCfcT8erC0Uh5NhWk6/caQ0amllm8mHG7LSjgG/VFB2zcW6QywKjx6Y57fAL5mEWU/XORaEsTX9QOq6uy4IfH2mvpQzo9JN3SlNW/hJ/HyI/d9Avyl5iMIY2yJMmHgiU24EFnfCNBGTnv/JE+APiXMmZDsSDi+9tDTtWFFsgGUABhmr6HYeiTVlyQSnQnAfJuot6W80K3EkeFIN83DiC4PkVr1PG5A8aI18jn1DQp3Nc4Eh6nHf0hXV/Gc27E3fdwzJB2IZZmc1P0SJAFAUjMKqZf/O4zosINNUJSNK8twuPuUZrTRnMrgJb1Cka2UWCGA+LR6VLX1RzW+SLiACwg/NexSekdpC/vcIpNpja30lxhpwdYqc0wlTNXlPWN3+hcu+sjnH+IeCTOnYmBchWSg/vC110j+nitnBn2O9IV3RTadSZXdkqpUGVHJdhYYQ2N/7GbhXvuz1dxLDPh0phb5QkR4CofLEopgeF6sGhFqIKvxJCS0Jw=";
  private static final String GRIM_REAPER_TEXTURE_DATA =
      "ewogICJ0aW1lc3RhbXAiIDogMTYxNzIyMTc4NjAxMSwKICAicHJvZmlsZUlkIiA6ICJkZGVkNTZlMWVmOGI0MGZlOGFkMTYyOTIwZjdhZWNkYSIsCiAgInByb2ZpbGVOYW1lIiA6ICJEaXNjb3JkQXBwIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzEwMTAyNWMxYzhmZjMwZmFkYjBiNGRjYjlhZWQ1YmRmMTU1MDQ3ZjMwNjJjYWMwOGRiNzM3ZDQ1ODVkMWYzNWYiCiAgICB9CiAgfQp9";

  private NPCShopEvent event;

  @Override
  public void registerFeature(
      final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    this.event = new NPCShopEvent(plugin);
  }

  @Permission("murderrun.command.npc.spawn.survivor")
  @CommandDescription("murderrun.command.npc.spawn.survivor.info")
  @Command(value = "murder npc spawn survivor", requiredSender = Player.class)
  public void createSurvivorMerchant(final Player sender) {
    final Location location = sender.getLocation();
    this.createNPC(location, true);
  }

  @Permission("murderrun.command.npc.spawn.killer")
  @CommandDescription("murderrun.command.npc.spawn.killer.info")
  @Command(value = "murder npc spawn killer", requiredSender = Player.class)
  public void createKillerMerchant(final Player sender) {
    final Location location = sender.getLocation();
    this.createNPC(location, false);
  }

  private void createNPC(final Location location, final boolean survivor) {

    final Component name =
        (survivor ? Message.GUARDIAN_ANGEL_NPC : Message.GRIM_REAPER_NPC).build();
    final String raw = AdventureUtils.serializeComponentToLegacyString(name);
    final NPCRegistry registry = CitizensAPI.getNPCRegistry();
    final NPC npc = registry.createNPC(EntityType.PLAYER, raw);
    this.setSkin(npc, survivor);
    npc.spawn(location);

    final MetadataStore store = npc.data();
    store.setPersistent("murderrun-gui", survivor);
  }

  private void setSkin(final NPC npc, final boolean survivor) {
    final SkinTrait trait = npc.getOrAddTrait(SkinTrait.class);
    if (survivor) {
      trait.setSkinPersistent(
          "Guardian Angel", GUARDIAN_ANGEL_TEXTURE_SIGNATURE, GUARDIAN_ANGEL_TEXTURE_DATA);
    } else {
      trait.setSkinPersistent(
          "Grim Reaper", GRIM_REAPER_TEXTURE_SIGNATURE, GRIM_REAPER_TEXTURE_DATA);
    }
  }
}
