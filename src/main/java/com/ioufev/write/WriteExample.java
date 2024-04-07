//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ioufev.write;

import com.google.common.collect.ImmutableList;
import com.ioufev.conn.ClientExample;
import com.ioufev.conn.ClientExampleRunner;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriteExample implements ClientExample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public WriteExample() {
    }

    public static void main(String[] args) throws Exception {
        WriteExample example = new WriteExample();
        (new ClientExampleRunner(example)).run();
    }

    public void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
        client.connect().get();
        List<NodeId> nodeIds = ImmutableList.of(new NodeId(2, "通道 1.设备 1.标记 2"));

        for(int i = 0; i < 10; ++i) {
            Variant v = new Variant(Unsigned.ushort(i));
            DataValue dv = new DataValue(v, (StatusCode)null, (DateTime)null);
            CompletableFuture<List<StatusCode>> f = client.writeValues(nodeIds, ImmutableList.of(dv));
            List<StatusCode> statusCodes = (List)f.get();
            StatusCode status = (StatusCode)statusCodes.get(0);
            if (status.isGood()) {
                this.logger.info("Wrote '{}' to nodeId={}", v, nodeIds.get(0));
            }
        }

        future.complete(client);
    }
}
