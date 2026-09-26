package eu.darkbot.util;

import com.github.manolo8.darkbot.core.api.GameAPI;
import eu.darkbot.utils.KekkaPlayerProxyServer;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.net.ServerSocket;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class KekkaPlayerProxyServerTest {
    @Test
    void bindsOnlyToLoopbackAndPassesPortToNativeHandler() throws Exception {
        GameAPI.Handler handler = mock(GameAPI.Handler.class);
        KekkaPlayerProxyServer proxy = new KekkaPlayerProxyServer(handler);
        Field field = KekkaPlayerProxyServer.class.getDeclaredField("serverSocket");
        field.setAccessible(true);
        try (ServerSocket socket = (ServerSocket) field.get(proxy)) {
            assertTrue(socket.getInetAddress().isLoopbackAddress());
            verify(handler).setLocalProxy(socket.getLocalPort());
        }
    }
}
