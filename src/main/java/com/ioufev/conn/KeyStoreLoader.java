//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ioufev.conn;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.eclipse.milo.opcua.sdk.server.util.HostnameUtil;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateBuilder;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class KeyStoreLoader {
    private static final Pattern IP_ADDR_PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    private static final String CLIENT_ALIAS = "test";
    private static final char[] PASSWORD = "12345678".toCharArray();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private X509Certificate[] clientCertificateChain;
    private X509Certificate clientCertificate;
    private KeyPair clientKeyPair;

    KeyStoreLoader() {
    }

    KeyStoreLoader load(Path baseDir) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        Path serverKeyStore = baseDir.resolve("testt.pfx");
        this.logger.info("Loading KeyStore at {}", serverKeyStore);
        if (!Files.exists(serverKeyStore, new LinkOption[0])) {
            keyStore.load((InputStream)null, PASSWORD);
            KeyPair keyPair = SelfSignedCertificateGenerator.generateRsaKeyPair(2048);
            SelfSignedCertificateBuilder builder = (new SelfSignedCertificateBuilder(keyPair)).setCommonName("test").setOrganization("digitalpetri").setOrganizationalUnit("dev").setLocalityName("Folsom").setStateName("CA").setCountryCode("US").setApplicationUri("urn:eclipse:milo:examples:client").addDnsName("localhost").addIpAddress("127.0.0.1");
            Iterator var6 = HostnameUtil.getHostnames("0.0.0.0").iterator();

            while(var6.hasNext()) {
                String hostname = (String)var6.next();
                if (IP_ADDR_PATTERN.matcher(hostname).matches()) {
                    builder.addIpAddress(hostname);
                } else {
                    builder.addDnsName(hostname);
                }
            }

            X509Certificate certificate = builder.build();
            keyStore.setKeyEntry("test", keyPair.getPrivate(), PASSWORD, new X509Certificate[]{certificate});
            OutputStream out = Files.newOutputStream(serverKeyStore);
            Throwable var8 = null;

            try {
                keyStore.store(out, PASSWORD);
            } catch (Throwable var32) {
                var8 = var32;
                throw var32;
            } finally {
                if (out != null) {
                    if (var8 != null) {
                        try {
                            out.close();
                        } catch (Throwable var29) {
                            var8.addSuppressed(var29);
                        }
                    } else {
                        out.close();
                    }
                }

            }
        } else {
            InputStream in = Files.newInputStream(serverKeyStore);
            Throwable var37 = null;

            try {
                keyStore.load(in, PASSWORD);
            } catch (Throwable var31) {
                var37 = var31;
                throw var31;
            } finally {
                if (in != null) {
                    if (var37 != null) {
                        try {
                            in.close();
                        } catch (Throwable var30) {
                            var37.addSuppressed(var30);
                        }
                    } else {
                        in.close();
                    }
                }

            }
        }

        Key clientPrivateKey = keyStore.getKey("test", PASSWORD);
        if (clientPrivateKey instanceof PrivateKey) {
            this.clientCertificate = (X509Certificate)keyStore.getCertificate("test");
            Stream var10001 = Arrays.stream(keyStore.getCertificateChain("test"));
            X509Certificate.class.getClass();
            this.clientCertificateChain = (X509Certificate[])var10001.map(X509Certificate.class::cast).toArray((x$0) -> {
                return new X509Certificate[x$0];
            });
            PublicKey serverPublicKey = this.clientCertificate.getPublicKey();
            this.clientKeyPair = new KeyPair(serverPublicKey, (PrivateKey)clientPrivateKey);
        }

        return this;
    }

    X509Certificate getClientCertificate() {
        return this.clientCertificate;
    }

    public X509Certificate[] getClientCertificateChain() {
        return this.clientCertificateChain;
    }

    KeyPair getClientKeyPair() {
        return this.clientKeyPair;
    }
}
