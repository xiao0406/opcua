//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ioufev.subscribe;

import com.google.common.collect.Lists;
import com.ioufev.conn.ClientExample;
import com.ioufev.conn.ClientExampleRunner;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscriptionExample implements ClientExample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public SubscriptionExample() {
    }

    public static void main(String[] args) throws Exception {
        SubscriptionExample example = new SubscriptionExample();
        (new ClientExampleRunner(example)).run();
    }

    public void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
        client.connect().get();
        UaSubscription subscription = (UaSubscription)client.getSubscriptionManager().createSubscription(1000.0).get();
        ReadValueId readValueId = new ReadValueId(new NodeId(2, "通道 1.设备 1.标记 1"), AttributeId.Value.uid(), (String)null, QualifiedName.NULL_VALUE);
        UaSubscription.ItemCreationCallback onItemCreated = new UaSubscription.ItemCreationCallback() {
            public void onItemCreated(UaMonitoredItem item, int i) {
                item.setValueConsumer((x$0, x$1) -> {
                    SubscriptionExample.this.onSubscriptionValue(x$0, x$1);
                });
            }
        };
        MonitoringParameters parameters = new MonitoringParameters(subscription.nextClientHandle(), 1000.0, (ExtensionObject)null, Unsigned.uint(10), true);
        MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(readValueId, MonitoringMode.Reporting, parameters);
        List<UaMonitoredItem> items = (List)subscription.createMonitoredItems(TimestampsToReturn.Both, Lists.newArrayList(new MonitoredItemCreateRequest[]{request}), onItemCreated).get();
        Iterator var9 = items.iterator();

        while(var9.hasNext()) {
            UaMonitoredItem item = (UaMonitoredItem)var9.next();
            if (item.getStatusCode().isGood()) {
                this.logger.info("已为节点ID={}创建监视项", item.getReadValueId().getNodeId());
            } else {
                this.logger.warn("无法为节点ID={}创建监视项 (状态={})", item.getReadValueId().getNodeId(), item.getStatusCode());
            }
        }

        Thread.sleep(5000L);
        future.complete(client);
    }

    private void onSubscriptionValue(UaMonitoredItem item, DataValue value) {
        this.logger.info("已接收到值：item={}, value={}", item.getReadValueId().getNodeId(), value.getValue());
    }
}
