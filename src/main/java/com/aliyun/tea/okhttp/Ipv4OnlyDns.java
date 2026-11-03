package com.aliyun.tea.okhttp;

import okhttp3.Dns;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Ipv4OnlyDns implements Dns {
    public static final Ipv4OnlyDns INSTANCE = new Ipv4OnlyDns(Dns.SYSTEM);

    private final Dns delegate;

    public Ipv4OnlyDns(Dns delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        List<InetAddress> ipv4Addresses = new ArrayList<InetAddress>();
        for (InetAddress address : delegate.lookup(hostname)) {
            if (address instanceof Inet4Address) {
                ipv4Addresses.add(address);
            }
        }
        if (ipv4Addresses.isEmpty()) {
            throw new UnknownHostException("ipv4Only is enabled but no IPv4 address was found for " + hostname);
        }
        return ipv4Addresses;
    }
}
