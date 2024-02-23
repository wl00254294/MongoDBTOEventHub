package event.function;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.EventHubTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

    // Posting event to event hub
    @FunctionName("DataPost")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        // Item list
        context.getLogger().info("Request body is: " + request.getBody().orElse(""));

        // Check request body
        if (!request.getBody().isPresent()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Document not found.")
                    .build();
        } else {

            final String body = request.getBody().get();
            // produce event test
            publishEvents(body);
            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(body + " Posted!")
                    .build();
        }
    }

    // Testing event hub trigger azure function
    @FunctionName("ehprocessor")
    public void eventHubProcessor(
            @EventHubTrigger(name = "msg", eventHubName = "mongodbhub", connection = "myconnvarname") String message,
            final ExecutionContext context) {
        publishEvents(message + "==TRIGGER");
        context.getLogger().info(message);
    }

    public void publishEvents(String message) {
        String connectionString = "Endpoint=sb://eventnamesapce.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=7Lsishd4EhvoeFuiFD0mj+XJ61sMH4z8I+AEhMtTSnI=";
        String eventHubName = "mongodbhub";

        // create a producer client
        EventHubProducerClient producer = new EventHubClientBuilder()
                .connectionString(connectionString, eventHubName)
                .buildProducerClient();

        // sample events in an array
        List<EventData> allEvents = Arrays.asList(new EventData(message));
        System.out.println("start create a batch");
        // create a batch
        EventDataBatch eventDataBatch = producer.createBatch();
        System.out.println("finished create a batch");
        for (EventData eventData : allEvents) {
            // try to add the event from the array to the batch
            if (!eventDataBatch.tryAdd(eventData)) {
                // if the batch is full, send it and then create a new batch
                producer.send(eventDataBatch);
                eventDataBatch = producer.createBatch();

                // Try to add that event that couldn't fit before.
                if (!eventDataBatch.tryAdd(eventData)) {
                    throw new IllegalArgumentException("Event is too large for an empty batch. Max size: "
                            + eventDataBatch.getMaxSizeInBytes());
                }
            }
        }
        // send the last batch of remaining events
        if (eventDataBatch.getCount() > 0) {
            producer.send(eventDataBatch);
        }
        producer.close();
    }

}
