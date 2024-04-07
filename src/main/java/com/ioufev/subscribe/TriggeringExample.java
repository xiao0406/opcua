//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ioufev.subscribe;

import com.google.common.collect.Lists;
import com.ioufev.conn.ClientExample;
import com.ioufev.conn.ClientExampleRunner;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TriggeringExample implements ClientExample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AtomicLong clientHandles = new AtomicLong(1L);

    public TriggeringExample() {
    }

    public static void main(String[] args) throws Exception {
        TriggeringExample example = new TriggeringExample();
        (new ClientExampleRunner(example)).run();
    }

    public void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
        client.connect().get();
        UaSubscription subscription = (UaSubscription)client.getSubscriptionManager().createSubscription(1000.0).get();
        ReadValueId readValueId1 = new ReadValueId(new NodeId(2, "通道 1.设备 1.标记 5"), AttributeId.Value.uid(), (String)null, QualifiedName.NULL_VALUE);
        UaMonitoredItem reportingItem = this.createMonitoredItem(subscription, readValueId1, MonitoringMode.Reporting);
        ReadValueId readValueId2 = new ReadValueId(Identifiers.Server_ServerStatus_CurrentTime, AttributeId.Value.uid(), (String)null, QualifiedName.NULL_VALUE);
        UaMonitoredItem samplingItem = this.createMonitoredItem(subscription, readValueId2, MonitoringMode.Sampling);
        subscription.addTriggeringLinks(reportingItem, Lists.newArrayList(new UaMonitoredItem[]{samplingItem})).get();
        client.writeValue(new NodeId(2, "通道 1.设备 1.标记 2"), new DataValue(new Variant(1.0F))).get();
        Thread.sleep(5000L);
        future.complete(client);
    }

    private UaMonitoredItem createMonitoredItem(UaSubscription subscription, ReadValueId readValueId, MonitoringMode monitoringMode) throws ExecutionException, InterruptedException {
        UInteger clientHandle = Unsigned.uint(this.clientHandles.getAndIncrement());
        MonitoringParameters parameters = new MonitoringParameters(clientHandle, 1000.0, (ExtensionObject)null, Unsigned.uint(10), true);
        MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(readValueId, monitoringMode, parameters);
        UaSubscription.ItemCreationCallback onItemCreated = (item, id) -> {
            item.setValueConsumer(this::onSubscriptionValue);
        };
        List<UaMonitoredItem> items = (List)subscription.createMonitoredItems(TimestampsToReturn.Both, Lists.newArrayList(new MonitoredItemCreateRequest[]{request}), onItemCreated).get();
        return (UaMonitoredItem)items.get(0);
    }

    private void onSubscriptionValue(UaMonitoredItem item, DataValue value) {
        this.logger.info("subscription value received: item={}, value={}", item.getReadValueId().getNodeId(), value.getValue());
    }
}
