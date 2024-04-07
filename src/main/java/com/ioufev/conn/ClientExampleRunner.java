//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ioufev.conn;

import java.io.File;
import java.security.Security;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.Stack;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.util.EndpointUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientExampleRunner {
    private final Logger logger;
    private final CompletableFuture<OpcUaClient> future;
    private final ClientExample clientExample;

    public ClientExampleRunner(ClientExample clientExample) throws Exception {
        this(clientExample, true);
    }

    public ClientExampleRunner(ClientExample clientExample, boolean serverRequired) throws Exception {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.future = new CompletableFuture();
        this.clientExample = clientExample;
    }

    private OpcUaClient createClient() throws Exception {
        String filePath = ClientExampleRunner.class.getResource("/").getPath();
        File file = new File(filePath);
        KeyStoreLoader loader = (new KeyStoreLoader()).load(file.toPath());
        return OpcUaClient.create("opc.tcp://127.0.0.1:49320", (endpoints) -> {
            return endpoints.stream().filter(this.clientExample.endpointFilter()).map((endpoint) -> {
                return EndpointUtil.updateUrl(endpoint, "127.0.0.1");
            }).findFirst();
        }, (configBuilder) -> {
            return configBuilder.setApplicationName(LocalizedText.english("demo")).setApplicationUri("urn:eclipse:milo:examples:client").setKeyPair(loader.getClientKeyPair()).setCertificate(loader.getClientCertificate()).setCertificateChain(loader.getClientCertificateChain()).setIdentityProvider(this.clientExample.getIdentityProvider()).setRequestTimeout(Unsigned.uint(5000)).build();
        });
    }

    public void run() {
        try {
            OpcUaClient client = this.createClient();
            this.future.whenCompleteAsync((c, ex) -> {
                if (ex != null) {
                    this.logger.error("Error running example: {}", ex.getMessage(), ex);
                }

                try {
                    client.disconnect().get();
                    Stack.releaseSharedResources();
                } catch (ExecutionException | InterruptedException var6) {
                    this.logger.error("Error disconnecting: {}", var6.getMessage(), var6);
                }

                try {
                    Thread.sleep(1000L);
                    System.exit(0);
                } catch (InterruptedException var5) {
                    var5.printStackTrace();
                }

            });

            try {
                this.clientExample.run(client, this.future);
                this.future.get(15L, TimeUnit.SECONDS);
            } catch (Throwable var5) {
                this.logger.error("Error running client example: {}", var5.getMessage(), var5);
                this.future.completeExceptionally(var5);
            }
        } catch (Throwable var6) {
            this.logger.error("Error getting client: {}", var6.getMessage(), var6);
            this.future.completeExceptionally(var6);

            try {
                Thread.sleep(1000L);
                System.exit(0);
            } catch (InterruptedException var4) {
                var4.printStackTrace();
            }
        }

        try {
            Thread.sleep(999999999L);
        } catch (InterruptedException var3) {
            var3.printStackTrace();
        }

    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }
}
