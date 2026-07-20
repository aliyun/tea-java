package com.aliyun.tea.okhttp;

import com.aliyun.tea.TeaException;
import com.aliyun.tea.utils.DefaultHostnameVerifier;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class OkHttpClientBuilderTest {
    private Map<String, Object> map = new HashMap<>();

    /**
     * Build an ephemeral self-signed CA PEM at runtime (CN under *.local / UnitTestFixture).
     * No production certificates or private keys are committed to the repository.
     */
    private static String createEphemeralSelfSignedCaPem(String commonName) throws Exception {
        Path workDir = Files.createTempDirectory("tea-java-test-ca-");
        Path keystore = workDir.resolve("test-ca.p12");
        Path pemFile = workDir.resolve("test-ca.pem");
        try {
            Path keytool = resolveKeytool();
            runKeytool(keytool,
                    "-genkeypair",
                    "-alias", "testca",
                    "-keyalg", "RSA",
                    "-keysize", "2048",
                    "-validity", "1",
                    "-dname", "CN=" + commonName + ",OU=UnitTestFixture,O=AliyunTeaJavaTest,C=CN",
                    "-keystore", keystore.toString(),
                    "-storetype", "PKCS12",
                    "-storepass", "changeit",
                    "-keypass", "changeit",
                    "-noprompt");
            runKeytool(keytool,
                    "-exportcert",
                    "-alias", "testca",
                    "-keystore", keystore.toString(),
                    "-storetype", "PKCS12",
                    "-storepass", "changeit",
                    "-rfc",
                    "-file", pemFile.toString());
            byte[] pemBytes = Files.readAllBytes(pemFile);
            return new String(pemBytes, StandardCharsets.US_ASCII);
        } finally {
            deleteRecursively(workDir);
        }
    }

    private static Path resolveKeytool() {
        Path keytool = java.nio.file.Paths.get(System.getProperty("java.home"), "bin", "keytool");
        if (!Files.isExecutable(keytool)) {
            keytool = java.nio.file.Paths.get(System.getProperty("java.home"), "bin", "keytool.exe");
        }
        Assert.assertTrue("keytool not found under java.home", Files.isExecutable(keytool));
        return keytool;
    }

    private static void runKeytool(Path keytool, String... args) throws Exception {
        String[] command = new String[args.length + 1];
        command[0] = keytool.toString();
        System.arraycopy(args, 0, command, 1, args.length);
        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (InputStream in = process.getInputStream()) {
            byte[] buf = new byte[1024];
            int n;
            while ((n = in.read(buf)) >= 0) {
                output.write(buf, 0, n);
            }
        }
        if (!process.waitFor(60, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            Assert.fail("keytool timed out");
        }
        if (process.exitValue() != 0) {
            Assert.fail("keytool failed: " + new String(output.toByteArray(), StandardCharsets.UTF_8));
        }
    }

    private static void deleteRecursively(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(root)) {
            walk.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    // best-effort cleanup of ephemeral fixtures
                }
            });
        }
    }

    @Test
    public void timeOutTest() {
        map.clear();
        OkHttpClientBuilder clientBuilder = new OkHttpClientBuilder();
        OkHttpClient client = clientBuilder.connectTimeout(map).readTimeout(map).buildOkHttpClient();
        Assert.assertEquals(10000, client.connectTimeoutMillis());
        Assert.assertEquals(10000, client.readTimeoutMillis());

        map.clear();
        clientBuilder = new OkHttpClientBuilder();
        map.put("connectTimeout", 666);
        map.put("readTimeout", 888);
        client = clientBuilder.connectTimeout(map).readTimeout(map).buildOkHttpClient();
        Assert.assertEquals(666, client.connectTimeoutMillis());
        Assert.assertEquals(888, client.readTimeoutMillis());
    }

    @Test
    public void callTimeoutTest() {
        // 未设置 callTimeout 时，默认应为 0（不限制），且不影响 readTimeout 默认值
        map.clear();
        OkHttpClientBuilder clientBuilder = new OkHttpClientBuilder();
        OkHttpClient client = clientBuilder.callTimeout(map).buildOkHttpClient();
        Assert.assertEquals(0, client.callTimeoutMillis());
        Assert.assertEquals(10000, client.readTimeoutMillis());

        // 显式设置 callTimeout，应只作用于 callTimeout，readTimeout 保持默认
        map.clear();
        clientBuilder = new OkHttpClientBuilder();
        map.put("callTimeout", 1500);
        client = clientBuilder.callTimeout(map).buildOkHttpClient();
        Assert.assertEquals(1500, client.callTimeoutMillis());
        Assert.assertEquals(10000, client.readTimeoutMillis());

        // callTimeout 为 null 时按未设置处理，默认为 0
        map.clear();
        clientBuilder = new OkHttpClientBuilder();
        map.put("callTimeout", null);
        client = clientBuilder.callTimeout(map).buildOkHttpClient();
        Assert.assertEquals(0, client.callTimeoutMillis());

        // callTimeout 与 readTimeout 互不干扰，分别独立生效
        map.clear();
        clientBuilder = new OkHttpClientBuilder();
        map.put("callTimeout", 2000);
        map.put("readTimeout", 888);
        client = clientBuilder.readTimeout(map).callTimeout(map).buildOkHttpClient();
        Assert.assertEquals(2000, client.callTimeoutMillis());
        Assert.assertEquals(888, client.readTimeoutMillis());

        // callTimeout 为非法字符串时抛出 NumberFormatException
        map.clear();
        final OkHttpClientBuilder badBuilder = new OkHttpClientBuilder();
        map.put("callTimeout", "str");
        try {
            badBuilder.callTimeout(map);
            Assert.fail();
        } catch (NumberFormatException e) {
            Assert.assertTrue(e.getMessage().contains("For input string: \"str\""));
        }
    }

    @Test
    public void connectionPoolTest() {
        map.clear();
        OkHttpClientBuilder clientBuilder = Mockito.spy(new OkHttpClientBuilder());
        clientBuilder.connectionPool(map);
        map.clear();
        map.put("maxIdleConns", 666);
        clientBuilder.connectionPool(map);
        Mockito.verify(clientBuilder, Mockito.times(2)).connectionPool(map);

        map.put("keepAliveDuration", null);
        clientBuilder.connectionPool(map);

        map.put("keepAliveDuration", "");
        try {
            clientBuilder.connectionPool(map);
            Assert.fail();
        } catch (NumberFormatException e) {
            Assert.assertTrue(e.getMessage().contains("For input string: \"\""));
        }

        map.put("keepAliveDuration", "str");
        try {
            clientBuilder.connectionPool(map);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("For input string: \"str\""));
        }

        map.put("keepAliveDuration", 0);
        try {
            clientBuilder.connectionPool(map);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("keepAliveDuration <= 0: 0"));
        }

        map.put("keepAliveDuration", 100L);
        clientBuilder.connectionPool(map);
        Mockito.verify(clientBuilder, Mockito.times(7)).connectionPool(map);
    }

    @Test
    public void certificateTest() throws Exception {
        map.clear();
        OkHttpClientBuilder clientBuilder = Mockito.spy(new OkHttpClientBuilder());
        clientBuilder.certificate(map);
        map.clear();
        map.put("ignoreSSL", true);
        clientBuilder.certificate(map);
        Mockito.verify(clientBuilder, Mockito.times(2)).certificate(map);

        map.clear();
        map.put("ignoreSSL", false);
        map.put("ca", "-----BEGIN CERTIFICATE-----\nwrong ca-----END CERTIFICATE-----");
        try {
            new OkHttpClientBuilder().certificate(map);
            Assert.fail();
        } catch (TeaException e) {
            Assert.assertTrue(e.getMessage().contains("Unable to initialize"));
        }

        map.put("ca", "-----BEGIN CERTIFICATE-----\nwrong ca-----END CERTIFICATE-----\n\n-----BEGIN CERTIFICATE-----\nwrong ca-----END CERTIFICATE-----");
        try {
            new OkHttpClientBuilder().certificate(map);
            Assert.fail();
        } catch (TeaException e) {
            Assert.assertTrue(e.getMessage().contains("Unable to initialize"));
        }

        map.put("ca", null);
        new OkHttpClientBuilder().certificate(map);
        map.put("ca", "");
        new OkHttpClientBuilder().certificate(map);

        map.put("ca", System.getenv("CA"));
        OkHttpClientBuilder builder = new OkHttpClientBuilder().certificate(map);

        OkHttpClient client = builder.buildOkHttpClient();
        Assert.assertFalse(client.hostnameVerifier() instanceof DefaultHostnameVerifier);
        Assert.assertNotNull(client.sslSocketFactory());

        map.put("key", null);
        map.put("cert", null);
        new OkHttpClientBuilder().certificate(map);

        map.put("key", "");
        map.put("cert", "");
        new OkHttpClientBuilder().certificate(map);

        map.put("ca", "-----BEGIN CERTIFICATE-----\nwrong ca-----END CERTIFICATE-----");
        map.put("key", "wrong key");
        map.put("cert", "-----BEGIN CERTIFICATE-----\nwrong cert-----END CERTIFICATE-----");
        try {
            new OkHttpClientBuilder().certificate(map);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof TeaException);
        }

        // ephemeral self-signed fixtures only — covers split + trust store without committed PEMs / CI CA secret
        map.clear();
        map.put("ignoreSSL", false);
        String singleCa = createEphemeralSelfSignedCaPem("tea-java-unit-test.local");
        map.put("ca", singleCa);
        OkHttpClient clientWithCa = new OkHttpClientBuilder().certificate(map).buildOkHttpClient();
        Assert.assertNotNull(clientWithCa.sslSocketFactory());

        String dualCa = createEphemeralSelfSignedCaPem("tea-java-unit-test-a.local")
                + "\n"
                + createEphemeralSelfSignedCaPem("tea-java-unit-test-b.local");
        map.put("ca", dualCa);
        Assert.assertNotNull(new OkHttpClientBuilder().certificate(map).buildOkHttpClient().sslSocketFactory());

        // non-PEM CA still enters splitPemCertificates else-branch
        map.put("ca", "not-a-pem-certificate");
        try {
            new OkHttpClientBuilder().certificate(map);
            Assert.fail();
        } catch (TeaException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void proxyAuthenticatorTest() throws Exception {
        map.clear();
        OkHttpClientBuilder clientBuilder = new OkHttpClientBuilder();
        map.put("httpsProxy", "https://user:password@127.0.0.1:8080");
        OkHttpClient client = clientBuilder.proxy(map).proxyAuthenticator(map).buildOkHttpClient();
        Assert.assertNotNull(client.proxyAuthenticator());

        Request request = new Request.Builder().url("http://example.com").build();
        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(407)
                .message("Proxy Authentication Required")
                .build();
        Request authenticated = client.proxyAuthenticator().authenticate((Route) null, response);
        Assert.assertNotNull(authenticated);
        Assert.assertNotNull(authenticated.header("Proxy-Authorization"));

        map.clear();
        map.put("socks5Proxy", "socks5://user:password@127.0.0.1:1080");
        Assert.assertNotNull(new OkHttpClientBuilder().proxy(map).proxyAuthenticator(map).buildOkHttpClient());

        map.clear();
        map.put("httpProxy", "://bad-url");
        try {
            new OkHttpClientBuilder().proxy(map);
            Assert.fail();
        } catch (TeaException e) {
            Assert.assertNotNull(e.getMessage());
        }

        map.clear();
        map.put("httpProxy", "://bad-url");
        try {
            new OkHttpClientBuilder().proxyAuthenticator(map);
            Assert.fail();
        } catch (TeaException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void proxyTest() throws Exception {
        map.clear();
        OkHttpClientBuilder clientBuilder = Mockito.spy(new OkHttpClientBuilder());
        clientBuilder.proxy(map);
        map.clear();
        map.put("httpProxy", "http://127.0.0.1:8080");
        clientBuilder.proxy(map);
        map.clear();
        map.put("httpsProxy", "https://user:password@127.0.0.1:8080");
        clientBuilder.proxy(map);
        Mockito.verify(clientBuilder, Mockito.times(3)).proxy(map);
        map.put("socks5Proxy", "socks5://user:password@127.0.0.1:1080");
        clientBuilder.proxy(map);
        Mockito.verify(clientBuilder, Mockito.times(4)).proxy(map);
        map.clear();
        map.put("socks5Proxy", "socks5://user:password@127.0.0.1:1080");
        clientBuilder.proxy(map);
        Mockito.verify(clientBuilder, Mockito.times(5)).proxy(map);
    }

    @Test
    public void protocolsTest() {
        map.clear();
        OkHttpClientBuilder clientBuilder = new OkHttpClientBuilder();
        OkHttpClient client = clientBuilder.protocols(map).buildOkHttpClient();
        Assert.assertEquals(2, client.protocols().size());
        Assert.assertTrue(client.protocols().contains(Protocol.HTTP_2));
        Assert.assertTrue(client.protocols().contains(Protocol.HTTP_1_1));
        // HTTP_1_0 在 OkHttp 中已不支持
        Assert.assertFalse(client.protocols().contains(Protocol.HTTP_1_0));

        map.clear();
        clientBuilder = new OkHttpClientBuilder();
        map.put("disableHttp2", true);
        client = clientBuilder.protocols(map).buildOkHttpClient();
        Assert.assertEquals(1, client.protocols().size());
        Assert.assertFalse(client.protocols().contains(Protocol.HTTP_2));
        Assert.assertTrue(client.protocols().contains(Protocol.HTTP_1_1));
        // HTTP_1_0 在 OkHttp 中已不支持
        Assert.assertFalse(client.protocols().contains(Protocol.HTTP_1_0));
    }
}
