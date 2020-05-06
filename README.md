[![Contribute](https://che.openshift.io/factory/resources/factory-contribute.svg)](https://che.openshift.io/f?url=https://github.com/edeandrea/summit-lab-spring-music.git)

The source code from this was adapted from the well-known [Spring Music](https://github.com/cloudfoundry-samples/spring-music) sample application.

The application has been refactored to use [Spring Cloud Kubernetes](https://spring.io/projects/spring-cloud-kubernetes) as well as adopting the [Event Sourcing](https://martinfowler.com/eaaDev/EventSourcing.html) and [Outbox](https://debezium.io/blog/2020/02/10/event-sourcing-vs-cdc/) patterns.

# Outbox
All events are captured in an Outbox table called `outbox_events`. There is a [Debezium](https://debezium.io) connector [config file](misc/templates/debezium-connector-config.json) which will emit events into the `outbox.${aggregateType}.events` Kafka topic, where `${aggregateType}` is driven by the domain event itself. Currently in this application there is only a single aggregate type, namely `Album`.

The outbox table is defined in the [`OutboxEvent` JPA class](src/main/java/com/redhat/springmusic/domain/jpa/OutboxEvent.java). The resulting table should look like:

```
+-----------------+--------------+------+-----+---------+----------------+
| Field           | Type         | Null | Key | Default | Extra          |
+-----------------+--------------+------+-----+---------+----------------+
| event_id        | bigint       | NO   | PRI | NULL    | auto_increment |
| aggregate_id    | varchar(255) | NO   |     | NULL    |                |
| aggregate_type  | varchar(255) | NO   |     | NULL    |                |
| event_timestamp | datetime(6)  | NO   |     | NULL    |                |
| event_type      | varchar(255) | NO   |     | NULL    |                |
| payload         | json         | YES  |     | NULL    |                |
+-----------------+--------------+------+-----+---------+----------------+
```

Events will look something like
```json
{
  "schema": {
    "type": "struct",
    "fields": [
      {
        "type": "string",
        "optional": true,
        "name": "io.debezium.data.Json",
        "version": 1,
        "field": "payload"
      },
      {
        "type": "string",
        "optional": true,
        "field": "eventType"
      },
      {
        "type": "int64",
        "optional": false,
        "field": "eventId"
      },
      {
        "type": "int64",
        "optional": true,
        "name": "io.debezium.time.MicroTimestamp",
        "version": 1,
        "field": "eventTimestamp"
      },
      {
        "type": "string",
        "optional": true,
        "field": "aggregateId"
      },
      {
        "type": "string",
        "optional": true,
        "field": "aggregateType"
      }
    ],
    "optional": false
  },
  "payload": {
    "payload": "{\"album\":{\"id\":\"5cc93f56-bc12-4e53-aeab-74c17ba027fa\",\"genre\":\"Blues\",\"title\":\"Couldn't Stand The Weather\",\"artist\":\"Stevie Ray Vaughan\",\"albumId\":null,\"trackCount\":0,\"releaseYear\":\"1984\"},\"eventType\":\"ALBUM_CREATED\"}",
    "eventType": "ALBUM_CREATED",
    "eventId": 29,
    "eventTimestamp": 1583344123573000,
    "aggregateId": "5cc93f56-bc12-4e53-aeab-74c17ba027fa",
    "aggregateType": "Album"
  }
}
```

```json
{
  "schema": {
    "type": "struct",
    "fields": [
      {
        "type": "string",
        "optional": true,
        "name": "io.debezium.data.Json",
        "version": 1,
        "field": "payload"
      },
      {
        "type": "string",
        "optional": true,
        "field": "eventType"
      },
      {
        "type": "int64",
        "optional": false,
        "field": "eventId"
      },
      {
        "type": "int64",
        "optional": true,
        "name": "io.debezium.time.MicroTimestamp",
        "version": 1,
        "field": "eventTimestamp"
      },
      {
        "type": "string",
        "optional": true,
        "field": "aggregateId"
      },
      {
        "type": "string",
        "optional": true,
        "field": "aggregateType"
      }
    ],
    "optional": false
  },
  "payload": {
    "payload": "{\"eventType\":\"ALBUM_DELETED\",\"deletedAlbum\":{\"id\":\"0d2fc154-5734-4ee4-9c37-231f2c95877b\",\"genre\":\"Blues\",\"title\":\"Texas Flood\",\"artist\":\"Stevie Ray Vaughan\",\"albumId\":null,\"trackCount\":0,\"releaseYear\":\"1983\"}}",
    "eventType": "ALBUM_DELETED",
    "eventId": 30,
    "eventTimestamp": 1583344138851000,
    "aggregateId": "0d2fc154-5734-4ee4-9c37-231f2c95877b",
    "aggregateType": "Album"
  }
}
```
```json
{
  "schema": {
    "type": "struct",
    "fields": [
      {
        "type": "string",
        "optional": true,
        "name": "io.debezium.data.Json",
        "version": 1,
        "field": "payload"
      },
      {
        "type": "string",
        "optional": true,
        "field": "eventType"
      },
      {
        "type": "int64",
        "optional": false,
        "field": "eventId"
      },
      {
        "type": "int64",
        "optional": true,
        "name": "io.debezium.time.MicroTimestamp",
        "version": 1,
        "field": "eventTimestamp"
      },
      {
        "type": "string",
        "optional": true,
        "field": "aggregateId"
      },
      {
        "type": "string",
        "optional": true,
        "field": "aggregateType"
      }
    ],
    "optional": false
  },
  "payload": {
    "payload": "{\"after\":{\"id\":\"0e44c7d9-f861-4823-a95e-64fe6bd23727\",\"genre\":\"Rock\",\"title\":\"Hotel California\",\"artist\":\"The Eagles\",\"albumId\":null,\"trackCount\":0,\"releaseYear\":\"1977\"},\"before\":{\"id\":\"0e44c7d9-f861-4823-a95e-64fe6bd23727\",\"genre\":\"Rock\",\"title\":\"Hotel California\",\"artist\":\"The Eagles\",\"albumId\":null,\"trackCount\":0,\"releaseYear\":\"1976\"},\"eventType\":\"ALBUM_UPDATED\"}",
    "eventType": "ALBUM_UPDATED",
    "eventId": 31,
    "eventTimestamp": 1583344145139000,
    "aggregateId": "0e44c7d9-f861-4823-a95e-64fe6bd23727",
    "aggregateType": "Album"
  }
}
```
Next will be working on wiring this together with a schema registry.

If you want to provision this app with all the Kafka clusters/Debezium setup on an OpenShift cluster, you can run the [Ansible Playbook](https://github.com/edeandrea/debezium-demo-apb).
