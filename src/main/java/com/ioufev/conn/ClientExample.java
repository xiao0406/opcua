//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ioufev.conn;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.IdentityProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

public interface ClientExample {
    default String getEndpointUrl() {
        return "opc.tcp://localhost:12686/milo";
    }

    default Predicate<EndpointDescription> endpointFilter() {
        return (e) -> {
            return this.getSecurityPolicy().getUri().equals(e.getSecurityPolicyUri());
        };
    }

    default SecurityPolicy getSecurityPolicy() {
        return SecurityPolicy.Basic256Sha256;
    }

    default IdentityProvider getIdentityProvider() {
        return new UsernameProvider("wxy", "wangxiaoyang1999");
    }

    void run(OpcUaClient var1, CompletableFuture<OpcUaClient> var2) throws Exception;
}
