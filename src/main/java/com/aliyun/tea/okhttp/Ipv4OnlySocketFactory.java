package com.aliyun.tea.okhttp;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * IPv6 literals in the URL bypass {@link Ipv4OnlyDns}, so the destination is checked again on connect.
 * <p>
 * With a SOCKS proxy the sockets tunnel through it: OkHttp then resolves the target with
 * {@link Ipv4OnlyDns} and only an IPv4 address is handed to the proxy. The SOCKS proxy
 * host must already be resolved to IPv4 by the caller.
 */
public class Ipv4OnlySocketFactory extends SocketFactory {
    public static final Ipv4OnlySocketFactory INSTANCE = new Ipv4OnlySocketFactory(Proxy.NO_PROXY);

    private final Proxy proxy;

    public Ipv4OnlySocketFactory(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public Socket createSocket() {
        return new Ipv4OnlySocket(proxy);
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return connect(new InetSocketAddress(host, port), null);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
        return connect(new InetSocketAddress(host, port), new InetSocketAddress(localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return connect(new InetSocketAddress(host, port), null);
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return connect(new InetSocketAddress(address, port), new InetSocketAddress(localAddress, localPort));
    }

    private Socket connect(SocketAddress endpoint, SocketAddress local) throws IOException {
        Socket socket = createSocket();
        try {
            if (null != local) {
                socket.bind(local);
            }
            socket.connect(endpoint);
            return socket;
        } catch (IOException e) {
            socket.close();
            throw e;
        }
    }

    static class Ipv4OnlySocket extends Socket {
        Ipv4OnlySocket(Proxy proxy) {
            super(proxy);
        }

        @Override
        public void connect(SocketAddress endpoint, int timeout) throws IOException {
            if (endpoint instanceof InetSocketAddress) {
                InetSocketAddress address = (InetSocketAddress) endpoint;
                // an unresolved target would be resolved by a SOCKS proxy, bypassing ipv4Only
                if (address.isUnresolved()) {
                    throw new ConnectException("ipv4Only is enabled, refusing to connect to unresolved address " + endpoint);
                }
                if (address.getAddress() instanceof Inet6Address) {
                    throw new ConnectException("ipv4Only is enabled, refusing to connect to IPv6 address " + endpoint);
                }
            }
            super.connect(endpoint, timeout);
        }
    }
}
