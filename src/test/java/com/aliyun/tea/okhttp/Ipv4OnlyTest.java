package com.aliyun.tea.okhttp;

import com.aliyun.tea.TeaException;
import okhttp3.Dns;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ipv4OnlyTest {
    private static final InetAddress V4 = address("127.0.0.1");
    private static final InetAddress V6 = address("::1");

    private static InetAddress address(String literal) {
        try {
            return InetAddress.getByName(literal);
        } catch (UnknownHostException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Dns fixedDns(final InetAddress... addresses) {
        return new Dns() {
            @Override
            public List<InetAddress> lookup(String hostname) {
                return Arrays.asList(addresses);
            }
        };
    }

    /**
     * Minimal HTTP server answering every connection with 200 "ok".
     */
    private static ServerSocket startServer(InetAddress bindAddress) throws IOException {
        final ServerSocket server = new ServerSocket(0, 50, bindAddress);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!server.isClosed()) {
                    try (Socket socket = server.accept()) {
                        InputStream in = socket.getInputStream();
                        byte[] buf = new byte[4096];
                        StringBuilder sb = new StringBuilder();
                        int n;
                        while (!sb.toString().contains("\r\n\r\n") && (n = in.read(buf)) > 0) {
                            sb.append(new String(buf, 0, n, StandardCharsets.ISO_8859_1));
                        }
                        OutputStream out = socket.getOutputStream();
                        out.write("HTTP/1.1 200 OK\r\nContent-Length: 2\r\nConnection: close\r\n\r\nok"
                                .getBytes(StandardCharsets.ISO_8859_1));
                        out.flush();
                    } catch (IOException ignored) {
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        return server;
    }

    private static ServerSocket startIpv6ServerOrSkip() {
        try {
            return startServer(V6);
        } catch (IOException e) {
            Assume.assumeNoException("IPv6 loopback is not available", e);
            return null;
        }
    }

    private static String get(OkHttpClient client, String url) throws IOException {
        try (Response response = client.newCall(new Request.Builder().url(url).build()).execute()) {
            return response.body().string();
        }
    }

    private static String messages(Throwable e) {
        StringBuilder sb = new StringBuilder();
        for (Throwable t = e; t != null; t = t.getCause()) {
            sb.append(t.getMessage()).append('\n');
        }
        return sb.toString();
    }

    private static Map<String, Object> runtime(Object ipv4Only) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("connectTimeout", 2000);
        map.put("readTimeout", 2000);
        map.put("ipv4Only", ipv4Only);
        return map;
    }

    @Test
    public void dnsKeepsOnlyIpv4Addresses() throws Exception {
        Assert.assertEquals(Arrays.asList(V4), new Ipv4OnlyDns(fixedDns(V6, V4)).lookup("dual.example"));
        Assert.assertEquals(Arrays.asList(V4), new Ipv4OnlyDns(fixedDns(V4)).lookup("v4.example"));
        try {
            new Ipv4OnlyDns(fixedDns(V6)).lookup("v6.example");
            Assert.fail("expected UnknownHostException");
        } catch (UnknownHostException e) {
            Assert.assertEquals("ipv4Only is enabled but no IPv4 address was found for v6.example", e.getMessage());
        }
        Assert.assertEquals(Arrays.asList(V4), Ipv4OnlyDns.INSTANCE.lookup("127.0.0.1"));
    }

    @Test
    public void socketFactoryRefusesIpv6() throws Exception {
        ServerSocket server = startServer(V4);
        try {
            int port = server.getLocalPort();
            Ipv4OnlySocketFactory factory = Ipv4OnlySocketFactory.INSTANCE;

            Socket socket = factory.createSocket();
            socket.connect(new InetSocketAddress(V4, port));
            Assert.assertTrue(socket.isConnected());
            socket.close();

            socket = factory.createSocket();
            try {
                socket.connect(new InetSocketAddress(V6, port));
                Assert.fail("expected ConnectException");
            } catch (ConnectException e) {
                Assert.assertTrue(e.getMessage().startsWith("ipv4Only is enabled, refusing to connect to IPv6 address"));
            } finally {
                socket.close();
            }

            factory.createSocket("127.0.0.1", port).close();
            factory.createSocket(V4, port).close();
            factory.createSocket("127.0.0.1", port, V4, 0).close();
            factory.createSocket(V4, port, V4, 0).close();

            try {
                factory.createSocket("::1", port);
                Assert.fail("expected ConnectException");
            } catch (ConnectException e) {
                Assert.assertTrue(e.getMessage().contains("ipv4Only"));
            }
            try {
                factory.createSocket(V6, port, V6, 0);
                Assert.fail("expected ConnectException");
            } catch (ConnectException e) {
                Assert.assertTrue(e.getMessage().contains("ipv4Only"));
            } catch (IOException e) {
                // binding the IPv6 loopback can fail on hosts without IPv6; the connect guard is covered above
                Assume.assumeNoException(e);
            }
        } finally {
            server.close();
        }
    }

    @Test
    public void builderOnlyAppliesWhenEnabled() {
        OkHttpClient client = new OkHttpClientBuilder().ipv4Only(runtime(true)).buildOkHttpClient();
        Assert.assertSame(Ipv4OnlyDns.INSTANCE, client.dns());
        Assert.assertSame(Ipv4OnlySocketFactory.INSTANCE, client.socketFactory());

        client = new OkHttpClientBuilder().ipv4Only(runtime("true")).buildOkHttpClient();
        Assert.assertSame(Ipv4OnlyDns.INSTANCE, client.dns());

        for (Object value : new Object[]{false, null, "false"}) {
            client = new OkHttpClientBuilder().ipv4Only(runtime(value)).buildOkHttpClient();
            Assert.assertSame(Dns.SYSTEM, client.dns());
            Assert.assertNotSame(Ipv4OnlySocketFactory.INSTANCE, client.socketFactory());
        }
        client = new OkHttpClientBuilder().ipv4Only(new HashMap<String, Object>()).buildOkHttpClient();
        Assert.assertSame(Dns.SYSTEM, client.dns());
    }

    @Test
    public void clientKeySeparatesIpv4OnlyClients() throws Exception {
        OkHttpClient dualStack = ClientHelper.getOkHttpClient("ipv4only-key.test", 80, runtime(null));
        Assert.assertSame(dualStack, ClientHelper.getOkHttpClient("ipv4only-key.test", 80, runtime(false)));
        OkHttpClient ipv4 = ClientHelper.getOkHttpClient("ipv4only-key.test", 80, runtime(true));
        Assert.assertNotSame(dualStack, ipv4);
        Assert.assertSame(Dns.SYSTEM, dualStack.dns());
        Assert.assertSame(Ipv4OnlyDns.INSTANCE, ipv4.dns());
        Assert.assertSame(ipv4, ClientHelper.clients.get("ipv4only-key.test:80:2000:2000:ipv4Only"));
        Assert.assertSame(dualStack, ClientHelper.clients.get("ipv4only-key.test:80:2000:2000"));
    }

    @Test
    public void ipv4OnlyClientConnectsOverIpv4() throws Exception {
        ServerSocket server = startServer(V4);
        try {
            OkHttpClient client = ClientHelper.getOkHttpClient("127.0.0.1", server.getLocalPort(), runtime(true));
            Assert.assertEquals("ok", get(client, "http://127.0.0.1:" + server.getLocalPort() + "/"));
        } finally {
            server.close();
        }
    }

    @Test
    public void ipv4OnlyClientDoesNotFallBackToIpv6() throws Exception {
        ServerSocket server = startIpv6ServerOrSkip();
        try {
            int port = server.getLocalPort();
            String url = "http://[::1]:" + port + "/";

            OkHttpClient dualStack = ClientHelper.getOkHttpClient("::1", port, runtime(null));
            Assert.assertEquals("ok", get(dualStack, url));

            OkHttpClient ipv4 = ClientHelper.getOkHttpClient("::1", port, runtime(true));
            try {
                get(ipv4, url);
                Assert.fail("expected IPv6 connection to be refused");
            } catch (IOException e) {
                Assert.assertTrue(messages(e), messages(e).contains("ipv4Only is enabled"));
            }

            Assert.assertEquals("ok", get(dualStack, url));
        } finally {
            server.close();
        }
    }

    /**
     * Minimal SOCKS5 server (no auth) that records each CONNECT target as "ipv4:addr" or
     * "domain:name" and then answers the tunnelled HTTP request itself with 200 "ok".
     */
    private static ServerSocket startSocks5Server(final List<String> targets) throws IOException {
        final ServerSocket server = new ServerSocket(0, 50, V4);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!server.isClosed()) {
                    try (Socket socket = server.accept()) {
                        DataInputStream in = new DataInputStream(socket.getInputStream());
                        OutputStream out = socket.getOutputStream();
                        in.readUnsignedByte();
                        in.readFully(new byte[in.readUnsignedByte()]);
                        out.write(new byte[]{5, 0});
                        in.readFully(new byte[3]);
                        int atyp = in.readUnsignedByte();
                        if (atyp == 1) {
                            byte[] addr = new byte[4];
                            in.readFully(addr);
                            targets.add("ipv4:" + InetAddress.getByAddress(addr).getHostAddress());
                        } else {
                            byte[] name = new byte[atyp == 3 ? in.readUnsignedByte() : 16];
                            in.readFully(name);
                            targets.add(atyp == 3 ? "domain:" + new String(name, StandardCharsets.ISO_8859_1) : "ipv6");
                        }
                        in.readUnsignedShort();
                        out.write(new byte[]{5, 0, 0, 1, 127, 0, 0, 1, 0, 0});
                        StringBuilder sb = new StringBuilder();
                        byte[] buf = new byte[4096];
                        int n;
                        while (!sb.toString().contains("\r\n\r\n") && (n = in.read(buf)) > 0) {
                            sb.append(new String(buf, 0, n, StandardCharsets.ISO_8859_1));
                        }
                        out.write("HTTP/1.1 200 OK\r\nContent-Length: 2\r\nConnection: close\r\n\r\nok"
                                .getBytes(StandardCharsets.ISO_8859_1));
                        out.flush();
                    } catch (IOException ignored) {
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        return server;
    }

    private static OkHttpClient proxiedClient(Map<String, Object> map) {
        return new OkHttpClientBuilder().connectTimeout(map).readTimeout(map)
                .proxy(map).proxyAuthenticator(map).ipv4Only(map).buildOkHttpClient();
    }

    @Test
    public void socks5ProxyGetsResolvedIpv4TargetWhenIpv4Only() throws Exception {
        List<String> targets = Collections.synchronizedList(new ArrayList<String>());
        ServerSocket socks = startSocks5Server(targets);
        try {
            String proxy = "socks5://127.0.0.1:" + socks.getLocalPort();
            Map<String, Object> dualStack = runtime(null);
            dualStack.put("socks5Proxy", proxy);
            Assert.assertEquals("ok", get(proxiedClient(dualStack), "http://localhost:1/"));
            Assert.assertEquals(Arrays.asList("domain:localhost"), targets);

            Map<String, Object> ipv4 = runtime(true);
            ipv4.put("socks5Proxy", proxy);
            OkHttpClient client = proxiedClient(ipv4);
            Assert.assertSame(Proxy.NO_PROXY, client.proxy());
            Assert.assertSame(Ipv4OnlyDns.INSTANCE, client.dns());
            Assert.assertEquals("ok", get(client, "http://localhost:1/"));
            Assert.assertEquals(Arrays.asList("domain:localhost", "ipv4:127.0.0.1"), targets);

            try {
                get(client, "http://[::1]:1/");
                Assert.fail("expected IPv6 target to be refused");
            } catch (IOException e) {
                Assert.assertTrue(messages(e), messages(e).contains("ipv4Only is enabled"));
            }
            Assert.assertEquals(2, targets.size());
        } finally {
            socks.close();
        }
    }

    @Test
    public void httpProxyHopIsPinnedToIpv4WhenIpv4Only() throws Exception {
        ServerSocket proxy = startServer(V4);
        try {
            Map<String, Object> map = runtime(true);
            map.put("httpProxy", "http://127.0.0.1:" + proxy.getLocalPort());
            OkHttpClient client = proxiedClient(map);
            Assert.assertSame(Ipv4OnlyDns.INSTANCE, client.dns());
            Assert.assertSame(Ipv4OnlySocketFactory.INSTANCE, client.socketFactory());
            InetSocketAddress address = (InetSocketAddress) client.proxy().address();
            Assert.assertFalse(address.isUnresolved());
            Assert.assertTrue(address.getAddress() instanceof java.net.Inet4Address);
            Assert.assertEquals("ok", get(client, "http://ipv4only-proxy.test:1/"));
        } finally {
            proxy.close();
        }
    }

    @Test
    public void httpProxyIpv6OnlyAddressFailsWhenIpv4Only() throws Exception {
        ServerSocket proxy = startIpv6ServerOrSkip();
        try {
            Map<String, Object> map = runtime(true);
            map.put("httpProxy", "http://[::1]:" + proxy.getLocalPort());
            try {
                proxiedClient(map);
                Assert.fail("expected IPv6-only HTTP proxy to be refused");
            } catch (TeaException e) {
                Assert.assertTrue(messages(e), messages(e).contains("ipv4Only is enabled"));
            }
        } finally {
            proxy.close();
        }
    }

    @Test
    public void socks5ProxyHopIsPinnedToIpv4WhenIpv4Only() throws Exception {
        List<String> targets = Collections.synchronizedList(new ArrayList<String>());
        ServerSocket socks = startSocks5Server(targets);
        try {
            Map<String, Object> ipv4 = runtime(true);
            ipv4.put("socks5Proxy", "socks5://127.0.0.1:" + socks.getLocalPort());
            OkHttpClient client = proxiedClient(ipv4);
            Assert.assertTrue(client.socketFactory() instanceof Ipv4OnlySocketFactory);
            Assert.assertEquals("ok", get(client, "http://localhost:1/"));
            Assert.assertEquals(Collections.singletonList("ipv4:127.0.0.1"), targets);
        } finally {
            socks.close();
        }
    }

    @Test
    public void proxyHostNameIsPinnedToIpv4LiteralWhenIpv4Only() throws Exception {
        InetAddress resolved = OkHttpClientBuilder.resolveProxyToIpv4("localhost");
        Assert.assertTrue(resolved instanceof java.net.Inet4Address);
        Assert.assertEquals(resolved.getHostAddress(), new InetSocketAddress(resolved, 1).getHostString());

        List<String> targets = Collections.synchronizedList(new ArrayList<String>());
        ServerSocket socks = startSocks5Server(targets);
        try {
            Map<String, Object> ipv4 = runtime(true);
            ipv4.put("socks5Proxy", "socks5://localhost:" + socks.getLocalPort());
            OkHttpClient client = proxiedClient(ipv4);
            java.lang.reflect.Field field = Ipv4OnlySocketFactory.class.getDeclaredField("proxy");
            field.setAccessible(true);
            InetSocketAddress address = (InetSocketAddress) ((Proxy) field.get(client.socketFactory())).address();
            Assert.assertEquals("127.0.0.1", address.getHostString());
            Assert.assertEquals("ok", get(client, "http://localhost:1/"));
            Assert.assertEquals(Collections.singletonList("ipv4:127.0.0.1"), targets);
        } finally {
            socks.close();
        }

        ServerSocket proxy = startServer(V4);
        try {
            Map<String, Object> map = runtime(true);
            map.put("httpProxy", "http://localhost:" + proxy.getLocalPort());
            OkHttpClient client = proxiedClient(map);
            InetSocketAddress address = (InetSocketAddress) client.proxy().address();
            Assert.assertEquals("127.0.0.1", address.getHostString());
            Assert.assertEquals("ok", get(client, "http://ipv4only-proxy.test:1/"));
        } finally {
            proxy.close();
        }
    }

    @Test
    public void socks5ProxyIpv6OnlyAddressFailsWhenIpv4Only() throws Exception {
        ServerSocket proxy = startIpv6ServerOrSkip();
        try {
            Map<String, Object> map = runtime(true);
            map.put("socks5Proxy", "socks5://[::1]:" + proxy.getLocalPort());
            try {
                proxiedClient(map);
                Assert.fail("expected IPv6-only SOCKS proxy to be refused");
            } catch (TeaException e) {
                Assert.assertTrue(messages(e), messages(e).contains("ipv4Only is enabled"));
            }
        } finally {
            proxy.close();
        }
    }

    @Test
    public void ipv4OnlySocketRefusesUnresolvedTarget() throws Exception {
        Socket socket = new Ipv4OnlySocketFactory(Proxy.NO_PROXY).createSocket();
        try {
            socket.connect(InetSocketAddress.createUnresolved("example.com", 80));
            Assert.fail("expected ConnectException");
        } catch (ConnectException e) {
            Assert.assertTrue(e.getMessage().startsWith("ipv4Only is enabled, refusing to connect to unresolved address"));
        } finally {
            socket.close();
        }

        Map<String, Object> map = runtime(true);
        map.put("socks5Proxy", "socks5://bad host:1");
        try {
            new OkHttpClientBuilder().ipv4Only(map);
            Assert.fail("expected TeaException");
        } catch (TeaException e) {
            Assert.assertTrue(e.getCause() instanceof URISyntaxException);
        }
    }
}
