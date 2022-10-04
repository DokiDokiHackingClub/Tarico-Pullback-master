package dev.tarico.module.auth.client;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.Client;
import dev.tarico.module.auth.GuiLogin;
import dev.tarico.module.auth.packet.IClientPacketHandler;
import dev.tarico.module.auth.packet.IPacket;
import dev.tarico.module.auth.packet.PacketRegistry;
import dev.tarico.module.auth.packet.client.CDisconnectPacket;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public final class ClientImpl {
    private static final ServerPacketHandler handler = new ServerPacketHandler();

    private static Socket socket;
    private static DataOutputStream writer;
    private static DataInputStream input;

    private static boolean disconnected = false;

    private static boolean connected = false;

    public static void connect() throws IOException {
        if (connected) return;

        socket = new Socket(Client.server, 1314);
        writer = new DataOutputStream(socket.getOutputStream());
        input = new DataInputStream(socket.getInputStream());

        openRead();

        connected = true;
    }
    private static void openRead() {
        final Thread thread = new Thread(() -> {
            while (!disconnected) {
                try {
                    if (input == null)
                        connect();
                    try {
                        final int id = input.readInt();
                        PacketRegistry.readPacket(id, input).processPacket(handler);
                    }catch (Exception e){
                        GuiLogin.status = EnumChatFormatting.RED + "Failed to connect server!";
                    }
                } catch (Throwable e) {
                    if (!disconnected) {
                        e.printStackTrace();
                    }
                }
            }
        }, "Tarico Thread");

        thread.setDaemon(true);
        thread.start();
    }

    public static void sendDisconnect() {
        if (disconnected) return;

        try {
            sendPacket(new CDisconnectPacket());
        } catch (Throwable ignored) {
        }
    }

    public static void disconnect() {
        if (disconnected) return;

        disconnected = true;

        IOUtils.closeQuietly(socket);

        clear();

        connected = false;
    }

    @Native
    public static void clear() {
        socket = null;
        writer = null;
        input = null;
    }

    @SuppressWarnings("unchecked")
    @Native
    public static void sendPacket(IPacket<IClientPacketHandler> packet) {
        check();

        try {
            PacketRegistry.writeHead(writer, (Class<? extends IPacket<IClientPacketHandler>>) packet.getClass());
            packet.writeData(writer);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static void check() {
        if (socket == null) throw new NullPointerException("Socket");
        if (writer == null) throw new NullPointerException("Writer");
        if (input == null) throw new NullPointerException("Input");
    }

    public static Socket getSocket() {
        return socket;
    }

    public static DataOutputStream getWriter() {
        return writer;
    }

    public static DataInputStream getInput() {
        return input;
    }
}
