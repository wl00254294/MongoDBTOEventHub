**MongoDB Event Trigger to Azure Event Hub**

This demo descripted how to use MongoDB event trigger to produce event to Azure Event Hub
![image](https://github.com/wl00254294/MongoDBTOEventHub/assets/43841531/6fcd540f-d487-4a51-940d-dcd414140fa6)

**Step 1:** Create Mongo database trigger
First, I need add module for http request. In the left navigation, 
under the Services section, click Trigger Settings. And click Dependencies page to add new dependency
![image](https://github.com/wl00254294/MongoDBTOEventHub/assets/43841531/01b66c90-02f9-4201-b18b-f91b9b5696cc)

![image](https://github.com/wl00254294/MongoDBTOEventHub/assets/43841531/839065b5-6a69-449c-b3de-9df9d5d9eb42)

Now, I can add Trigger by clicking “Add Trigger”
![image](https://github.com/wl00254294/MongoDBTOEventHub/assets/43841531/f41cdc37-256b-491f-accb-5b29dc872f2b)

In the dialog, I choose Database for Trigger Type,
![image](https://github.com/wl00254294/MongoDBTOEventHub/assets/43841531/c137bf4e-41f7-4729-9659-2f644293a605)

Watch Against : Collection
Cluster Name: select your cluster
Database Name: choose your database
Collection Name: choose your collection
Operation Type: this practices I catch “insert event” means when data insert into collection will trigger event
![image](https://github.com/wl00254294/MongoDBTOEventHub/assets/43841531/fdd44507-b8d2-4955-b44e-45b42bcb60f5)

In Function area push follow code use to trigger azure function name called “DataPost”
![image](https://github.com/wl00254294/MongoDBTOEventHub/assets/43841531/c28d8472-9414-454d-9c84-2a12bd27b613)

**Step 2**: Create Azure Event Hub
We can easily create event hub by using azure portal. In this practice, I create event hub namespace : eventnamesapce
event hub: mongodbhub
![image](https://github.com/wl00254294/MongoDBTOEventHub/assets/43841531/8bd43c39-2f19-4e9c-9e7e-0f3b8f951624)

Be carefully, event hub need set capture with storage container
![image](https://github.com/wl00254294/MongoDBTOEventHub/assets/43841531/948f2032-6a56-41a4-9998-62dc8bbd5233)

**Step 3**: Create Azure Function
We also used azure portal to create Azure Function : mongofunction
![image](https://github.com/wl00254294/MongoDBTOEventHub/assets/43841531/5c59a166-725f-4747-8be1-fb1808c2a2a8)

Now we will write code for the azure function. In this case I will use java to write Http trigger function as showin this
respository.
Finally,deploy to Function App
![image](https://github.com/wl00254294/MongoDBTOEventHub/assets/43841531/72e26099-5868-4439-b3e9-e47f4ea99258)

**Step 4** : Create Event Receiver
Now I will create a event receiver program to confirm event can be consumed. I use java code for a simple receiver.
The sample code shown in /test/java/event/function/Reveiver.java

**Step 5**: Testing
Insert a record to location collection
![image](https://github.com/wl00254294/MongoDBTOEventHub/assets/43841531/c139724f-03ac-45ee-8f8e-db0158acb6c5)
In the console of receiver will receive the record
![image](https://github.com/wl00254294/MongoDBTOEventHub/assets/43841531/c2d8006d-f446-43dc-a8fa-b7946c879a76)

In the MongoDB trigger log
![image](https://github.com/wl00254294/MongoDBTOEventHub/assets/43841531/ecb13da2-0645-4afb-991b-7fbdcc134cf6)

In the event hub site, shows incoming & outgoing message information
![image](https://github.com/wl00254294/MongoDBTOEventHub/assets/43841531/d78ed393-4092-46e7-b1c3-21cad1da9a29)



















