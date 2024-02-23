package event.function;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventProcessorClient;
import com.azure.messaging.eventhubs.EventProcessorClientBuilder;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;
import com.azure.messaging.eventhubs.models.ErrorContext;
import com.azure.messaging.eventhubs.models.EventContext;
import com.azure.messaging.eventhubs.models.PartitionContext;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import java.util.function.Consumer;

public class Receiver {

    // event hub shared key
    private static final String connectionString = "Endpoint=sb://eventnamesapce.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=XXXXXXX";
    private static final String eventHubName = "mongodbhub";

    // storage container shared kry
    private static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=sapsrcdata;AccountKey=XXXX;EndpointSuffix=core.windows.net";
    private static final String storageContainerName = "mongodbdata";

    public static void main(String[] args) throws Exception {
        // Create a blob container client that you use later to build an event processor
        // client to receive and process events
        BlobContainerAsyncClient blobContainerAsyncClient = new BlobContainerClientBuilder()
                .connectionString(storageConnectionString)
                .containerName(storageContainerName)
                .buildAsyncClient();

        // Create a builder object that you will use later to build an event processor
        // client to receive and process events and errors.
        EventProcessorClientBuilder eventProcessorClientBuilder = new EventProcessorClientBuilder()
                .connectionString(connectionString, eventHubName)
                .consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME)
                .processEvent(PARTITION_PROCESSOR)
                .processError(ERROR_HANDLER)
                .checkpointStore(new BlobCheckpointStore(blobContainerAsyncClient));

        // Use the builder object to create an event processor client
        EventProcessorClient eventProcessorClient = eventProcessorClientBuilder.buildEventProcessorClient();

        System.out.println("Starting event processor");
        eventProcessorClient.start();

        System.out.println("Press enter to stop.");
        System.in.read();

        System.out.println("Stopping event processor");
        eventProcessorClient.stop();
        System.out.println("Event processor stopped.");

        System.out.println("Exiting process");
    }

    public static final Consumer<EventContext> PARTITION_PROCESSOR = new Consumer<EventContext>() {
        public void accept(EventContext eventContext) {
            PartitionContext partitionContext = eventContext.getPartitionContext();
            EventData eventData = eventContext.getEventData();

            System.out.printf("Processing event from partition %s with sequence number %d with body: %s%n",
                    partitionContext.getPartitionId(), eventData.getSequenceNumber(), eventData.getBodyAsString());

            // Every 10 events received, it will update the checkpoint stored in Azure Blob
            // Storage.
            if (eventData.getSequenceNumber() % 10 == 0) {
                eventContext.updateCheckpoint();
            }
        }
    };

    public static final Consumer<ErrorContext> ERROR_HANDLER = new Consumer<ErrorContext>() {
        public void accept(ErrorContext errorContext) {
            System.out.printf("Error occurred in partition processor for partition %s, %s.%n",
                    errorContext.getPartitionContext().getPartitionId(),
                    errorContext.getThrowable());
        }
    };
}
