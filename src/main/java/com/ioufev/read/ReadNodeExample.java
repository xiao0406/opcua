//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ioufev.read;

import com.ioufev.conn.ClientExample;
import com.ioufev.conn.ClientExampleRunner;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.model.nodes.objects.ServerTypeNode;
import org.eclipse.milo.opcua.sdk.client.model.nodes.variables.ServerStatusTypeNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.enumerated.ServerState;
import org.eclipse.milo.opcua.stack.core.types.structured.ServerStatusDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadNodeExample implements ClientExample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ReadNodeExample() {
    }

    public static void main(String[] args) throws Exception {
        ReadNodeExample example = new ReadNodeExample();
        (new ClientExampleRunner(example)).run();
    }

    public void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
        client.connect().get();
        ServerTypeNode serverNode = (ServerTypeNode)client.getAddressSpace().getObjectNode(Identifiers.Server, Identifiers.ServerType);
        String[] serverArray = serverNode.getServerArray();
        String[] namespaceArray = serverNode.getNamespaceArray();
        this.logger.info("ServerArray={}", Arrays.toString(serverArray));
        this.logger.info("NamespaceArray={}", Arrays.toString(namespaceArray));
        ServerStatusDataType serverStatus = serverNode.getServerStatus();
        this.logger.info("ServerStatus={}", serverStatus);
        ServerStatusTypeNode serverStatusNode = serverNode.getServerStatusNode();
        DateTime startTime = serverStatusNode.getStartTime();
        DateTime currentTime = serverStatusNode.getCurrentTime();
        ServerState state = serverStatusNode.getState();
        this.logger.info("ServerStatus.StartTime={}", startTime);
        this.logger.info("ServerStatus.CurrentTime={}", currentTime);
        this.logger.info("ServerStatus.State={}", state);
        future.complete(client);
    }
}
