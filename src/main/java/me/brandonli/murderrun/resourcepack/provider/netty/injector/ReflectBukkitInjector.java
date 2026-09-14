/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.resourcepack.provider.netty.injector;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import me.brandonli.murderrun.resourcepack.provider.netty.injector.http.ResourcePackInjector;
import org.bukkit.Bukkit;
import org.bukkit.Server;

public final class ReflectBukkitInjector {

  private static final String INJECTOR_SYSTEM_PROPERTY = "murderrun.resourcepack";
  private static final String CRAFT_SERVER_CLASS = "org.bukkit.craftbukkit.CraftServer";
  private static final String MINECRAFT_SERVER_CLASS = "net.minecraft.server.MinecraftServer";
  private static final String SERVER_CONNECTION_CLASS =
      "net.minecraft.server.network.ServerConnectionListener";
  private static final String GET_SERVER_METHOD = "getServer";
  private static final String GET_CONNECTION_METHOD = "getConnection";
  private static final String CHANNELS_FIELD = "channels";

  private static final List<?> CONNECTIONS;

  static {
    try {
      CONNECTIONS = getConnections();
    } catch (final Throwable e) {
      throw new AssertionError(e);
    }
  }

  private static List<?> getConnections() throws Throwable {
    final Class<?> connectionClass = Class.forName(SERVER_CONNECTION_CLASS);
    final Object connection = getConnectionHandle(connectionClass);
    final VarHandle handle = getChannelsVarHandle(connectionClass);
    return (List<?>) handle.get(connection);
  }

  private static VarHandle getChannelsVarHandle(final Class<?> connectionClass)
      throws IllegalAccessException, NoSuchFieldException {
    final MethodHandles.Lookup lookup = MethodHandles.lookup();
    final MethodHandles.Lookup privateLookup =
        MethodHandles.privateLookupIn(connectionClass, lookup);
    return privateLookup.findVarHandle(connectionClass, CHANNELS_FIELD, List.class);
  }

  private static Object getConnectionHandle(final Class<?> connectionClass) throws Throwable {
    final Server craftServer = Bukkit.getServer();
    final Class<?> craftServerClass = Class.forName(CRAFT_SERVER_CLASS);
    final Class<?> minecraftServerClass = Class.forName(MINECRAFT_SERVER_CLASS);
    final MethodHandles.Lookup lookup = MethodHandles.publicLookup();
    final Class<?> dedicatedServerClass =
        craftServerClass.getMethod(GET_SERVER_METHOD).getReturnType();
    final MethodType getServerType = MethodType.methodType(dedicatedServerClass);
    final MethodHandle getServer =
        lookup.findVirtual(craftServerClass, GET_SERVER_METHOD, getServerType);
    final Object minecraftServer = getServer.invoke(craftServer);
    final MethodType getConnectionType = MethodType.methodType(connectionClass);
    final MethodHandle getConnection =
        lookup.findVirtual(minecraftServerClass, GET_CONNECTION_METHOD, getConnectionType);
    return getConnection.invoke(minecraftServer);
  }

  private final Path path;

  public ReflectBukkitInjector(final Path path) {
    this.path = path;
  }

  public void inject() {
    this.setZipProperty();
    this.install(this::installConsumer);
  }

  private void setZipProperty() {
    final Path absolute = this.path.toAbsolutePath();
    final String property = absolute.toString();
    System.setProperty(INJECTOR_SYSTEM_PROPERTY, property);
  }

  private void installConsumer(final Channel channel) {
    final ChannelPipeline pipeline = channel.pipeline();
    final ResourcePackInjector resourcePackInjector = new ResourcePackInjector();
    pipeline.addFirst(resourcePackInjector);
  }

  private void install(final Consumer<Channel> channelConsumer) {
    final ChannelInboundHandler serverHandler = this.injectServerAdapter(channelConsumer);
    for (final Object connection : CONNECTIONS) {
      if (!(connection instanceof final ChannelFuture channelFuture)) {
        continue;
      }
      final Channel channel = channelFuture.channel();
      final ChannelPipeline pipeline = channel.pipeline();
      pipeline.addFirst(serverHandler);
    }
  }

  private ChannelInboundHandlerAdapter injectServerAdapter(
      final Consumer<Channel> channelConsumer) {
    final ChannelInitializer<?> beginInitProtocol = this.getBeginInitializer(channelConsumer);
    return new ChannelInboundHandlerAdapter() {
      @Override
      public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        final Channel channel = (Channel) msg;
        final ChannelPipeline pipeline = channel.pipeline();
        pipeline.addFirst(beginInitProtocol);
        ctx.fireChannelRead(msg);
      }
    };
  }

  private ChannelInitializer<Channel> getBeginInitializer(final Consumer<Channel> channelConsumer) {
    return new ChannelInitializer<>() {
      @Override
      protected void initChannel(final Channel channel) {
        channelConsumer.accept(channel);
      }
    };
  }
}
