package com.invas.enhanced.fc.bert.config;

import com.invas.enhanced.fc.bert.model.IPAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

@Slf4j
@Configuration
public class FetchIPAddress {

    @Bean
    public IPAddress getIP() {
        IPAddress ipAddress = null;
        try {
            // Get all network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            ipAddress = new IPAddress();

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                // Skip loopback and inactive interfaces
                if (networkInterface.isLoopback() || !networkInterface.isUp() || networkInterface.isVirtual()) {
                    continue;
                }

                // Get all IP addresses (both IPv4 & IPv6)
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                ArrayList<String> ipv6 = new ArrayList<>();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();

                    if (address.isLinkLocalAddress() || address.isAnyLocalAddress() || address.isLoopbackAddress()) {
                        continue;
                    }

                    // Print IPv4 and IPv6 addresses
                    if (address.getHostAddress().contains(":")) {
                        ipv6.add(address.getHostAddress());
                        System.out.println("IPv6 Address: " + address.getHostAddress());
                    } else {
                        ipAddress.setIpv4(address.getHostAddress());
                        System.out.println("IPv4 Address: " + address.getHostAddress());
                    }
                }
                ipAddress.setIpv6(ipv6);
            }
        } catch (Exception e) {
            log.error("Failed to fetch IP address: {}", e.getMessage());
        }
        log.info("IP Address: {}", ipAddress);
        return ipAddress;
    }
}
