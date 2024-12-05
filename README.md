
# QStash Spring Boot Starter

This project is a Spring Boot Starter for QStash. It provides a simple way to integrate QStash into your Spring Boot application. It supports Scheduled, Listener and Client.

## Getting Started

### Prerequisites
- Spring Boot 3.0.0 or later
- Java 21 or later
-  org.springframework.boot:spring-boot-starter-web for  `@QStashListener` and `@QStashScheduler` annotation

### Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.tdilber</groupId>
    <artifactId>upstash-qstash-spring-boot-starter</artifactId>
    <version>0.0.1</version>
</dependency>
```

### Configuration

Add the following properties to your `application.yaml`:

```yaml
spring:
  qstash:
    endpoint: https://qstash.upstash.io
    apiKey: <your-qstash-api-key>
    appEndpoint: <your-app-endpoint> 
    #  <your-app-endpoint>  => if prod environment then use prod endpoint 
    # IF u are using localhost then use tunnel like ngrok. 
    # for more info visit qstash docs How to section
```

### Usage

#### Enable QStash 

Enable Annotation in your Spring Boot Application:

```java
@EnableQStash
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

#### Use Client

```java
import com.beyt.upstash.model.SendProperties;

@Service
public class MyService {
    @Autowired
    private QStashClient qStashClient;

    public void sendQueueMessage(String queueName, Object payload) {
        qStashClient.enqueueMessage(QUEUE_NAME, payload, SendProperties.builder().build());
    }
}
```

#### Use Enqueue Listener

System automatically sync listeners when app started. When message comes then system automatically call the method.

When u send message like `qStashClient.enqueueMessage("my-queue", payload, SendProperties.builder().build());` then system automatically call the method with payload.

```java
import com.beyt.upstash.annotation.QStashListener;

public class MyListener {
/
    @QStashListener(queueName = "my-queue") 
    public void onMessage(Object payload) {
        // process the message
    }
}
```

#### Use Scheduler

System automatically sync schedules when app started. When trigger schedule then system automatically call the method.

```java
import com.beyt.upstash.annotation.QStashScheduler;

public class MyScheduler {
    
    @QStashSchedule(scheduleName = SCHEDULE_NAME, cron = "* * * * *")
    public void schedule() {
        // process the message
    }
}
```
## License
Apache License 2.0

## TODO
- [x] Complete Client
- [x] Create Listener system
- [x] create QStashSchduler annotation (when app started then automatic sync schedules)
- [x] create test
- [x] listen and schedule method find operation make best practice
- [ ] Batch requests support
- [ ] dead letter queue listener support if exists
- [ ] Extend QStashClient for more operations
- [ ] REadme.md update
