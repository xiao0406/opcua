//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ioufev.read;

import com.google.common.collect.ImmutableList;
import com.ioufev.conn.ClientExample;
import com.ioufev.conn.ClientExampleRunner;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.enumerated.ServerState;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadExample implements ClientExample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ReadExample() {
    }

    public static void main(String[] args) throws Exception {
        ReadExample example = new ReadExample();
        (new ClientExampleRunner(example, true)).run();
    }

    public void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
        client.connect().get();
        NodeId nodeId = new NodeId(2, "通道 1.设备 1.标记 1");
        DataValue dataValue = (DataValue)client.readValue(0.0, TimestampsToReturn.Both, nodeId).get();
        System.out.println("-----读取-----");
        System.out.println("-----通道 1.设备 1.标记 1：" + dataValue.getValue().getValue());
        NodeId nodeId1 = new NodeId(2, "通道 1.设备 1");
        List<? extends UaNode> nodes = client.getAddressSpace().browseNodes(nodeId1);
        Iterator var7 = nodes.iterator();

        while(var7.hasNext()) {
            UaNode node = (UaNode)var7.next();
            NodeId nodeId2 = node.getNodeId();
            if (node.getNodeClass() == NodeClass.Variable) {
                DataValue value2 = (DataValue)client.readValue(0.0, TimestampsToReturn.Both, nodeId2).get();
                System.out.println(nodeId2.getIdentifier().toString() + ": " + value2.getValue().getValue());
            }
        }

        UaVariableNode node = client.getAddressSpace().getVariableNode(Identifiers.Server_ServerStatus_StartTime);
        DataValue value = node.readValue();
        this.logger.info("StartTime={}", value.getValue().getValue());
        this.readServerStateAndTime(client).thenAccept((values) -> {
            DataValue v0 = (DataValue)values.get(0);
            DataValue v1 = (DataValue)values.get(1);
            this.logger.info("State={}", ServerState.from((Integer)v0.getValue().getValue()));
            this.logger.info("CurrentTime={}", v1.getValue().getValue());
            future.complete(client);
        });
    }

    private CompletableFuture<List<DataValue>> readServerStateAndTime(OpcUaClient client) {
        List<NodeId> nodeIds = ImmutableList.of(Identifiers.Server_ServerStatus_State, Identifiers.Server_ServerStatus_CurrentTime);
        return client.readValues(0.0, TimestampsToReturn.Both, nodeIds);
    }
}
