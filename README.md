Spring Music
============

This is a sample application for using database services on [Kubernetes](http://kubernetes.io) with the [Spring Framework](http://spring.io), [Spring Boot](http://projects.spring.io/spring-boot/), and [Spring Cloud Kubernetes](https://spring.io/projects/spring-cloud-kubernetes).

This application has been built to store the same domain objects in one of a variety of different persistence technologies - relational, document, and key-value stores. This is not meant to represent a realistic use case for these technologies, since you would typically choose the one most applicable to the type of data you need to store, but it is useful for testing and experimenting with different types of services on Kubernetes.

The application use Spring Java configuration and [bean profiles](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html) to configure the application and the connection objects needed to use the persistence stores.

## Building

This project requires Java 8 to compile. It will not compile with Java 9 or later.

To build a runnable Spring Boot jar file, run the following command: 

~~~
$ ./gradlew clean assemble
~~~

## Running the application locally

One Spring bean profile should be activated to choose the database provider that the application should use. The profile is selected by setting the system property `spring.profiles.active` when starting the app.

The application can be started locally using the following command:

~~~
$ java -jar -Dspring.profiles.active=<profile> build/libs/spring-music.jar
~~~

where `<profile>` is one of the following values:

* `mysql`
* `postgres`
* `mongodb`
* `redis`

If no profile is provided, an in-memory relational database will be used. If any other profile is provided, the appropriate database server must be started separately. Spring Boot will auto-configure a connection to the database using it's auto-configuration defaults. The connection parameters can be configured by setting the appropriate [Spring Boot properties](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html).

If more than one of these profiles is provided, the application will throw an exception and fail to start.

## Running the application on Kubernetes
When running on Kubernetes you need to first deploy the appropriate service(s) and then use Kubernetes secrets/config maps & set up the appropriate environment variables for the application.

### Environment Variables
- `SPRING_PROFILES_ACTIVE`
    - The profile to activate (i.e. `mysql` / `postgres` / `mongodb` / `redis`)
    - If not set it will default to an in-memory H2 database

#### MySQL / Postgres
- `SPRING_DATASOURCE_USERNAME`
    - The username for the database
    - Should be injected from a Kubernetes secret
- `SPRING_DATASOURCE_PASSWORD`
    - The password for the database
    - Should be injected from a Kubernetes secret
- `SPRING_DATASOURCE_URL`
    - The url for the database
    - Should be relative to the Kubernetes service for the database
    - Should be injected from a Kubernetes `ConfigMap`
    
#### MongoDB
- `SPRING_DATA_MONGODB_USERNAME`
    - The username for the database
    - Should be injected from a Kubernetes secret
- `SPRING_DATA_MONGODB_PASSWORD`
    - The password for the database
    - Should be injected from a Kubernetes secret
- `SPRING_DATA_MONGODB_URI`
    - The URI for the database
    - Should be relative to the Kubernetes service for the database
    - Should be injected from a Kubernetes `ConfigMap`
    
#### Redis
- `SPRING_REDIS_PASSWORD`
    - The password for the Redis instance
    - Should be injected from a Kubernetes secret
- `SPRING_REDIS_HOST`
    - The hostname for the Redis instance
    - Should be relative to the Kubernetes service for the Redis instance
    - Should be injected from a Kubernetes `ConfigMap`
- `SPRING_REDIS_PORT`
    - The port for the Redis instance
    - Should be injected from a Kubernetes `ConfigMap`
    
 ## Running on Red Hat OpenShift Container Platform
 Templates have been set up that will deploy the entire stack depending on the back-end you want to use. The templates can be found in the [misc/templates](misc/templates) directory.
 
 [Spring Cloud Kubernetes](https://spring.io/projects/spring-cloud-kubernetes) requires that the application being deployed needs to have read-only access to the host node so that it can use the Kubernetes REST API. This can be done by using the following command within your project:
 
 `oc adm policy add-role-to-user view system:serviceaccount:<project>:default`
 
 Make sure to substitute your project's name for `<project>`.
 
 Once that is done you can import one (or all) of the [templates](misc/templates) either into the project's registry or you can have your administrator upload them into the `openshift` registry so they are available to all projects. From there you can simply instantiate an instance of the template you would like (i.e. `spring-music-mysql` / `spring-music-postgresql` / `spring-music-mongodb` / `spring-music-redis`). There are some parameters you can customize but otherwise the template will create everything you need to deploy the application along with the appropriate datasource, secrets, config maps, and perform all necessary bindings.

#### Database drivers
Database drivers for MySQL, Postgres, Microsoft SQL Server, MongoDB, and Redis are included in the project.

To connect to an Oracle database, you will need to download the appropriate driver (e.g. from http://www.oracle.com/technetwork/database/features/jdbc/index-091264.html). Then make a `libs` directory in the `spring-music` project, and move the driver, `ojdbc7.jar` or `ojdbc8.jar`, into the `libs` directory.
In `build.gradle`, uncomment the line `compile files('libs/ojdbc8.jar')` or `compile files('libs/ojdbc7.jar')` and run `./gradle assemble`
