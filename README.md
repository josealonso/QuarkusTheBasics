## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```
> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

As for the database, you can start dev mode using a custom profile:

```shell script
quarkus dev -Dquarkus.profile=dev-with-data
```

## Format of import.sql

When importing a import.sql to set up your database, keep in mind that Quarkus reconfigures Hibernate ORM so to require
a semicolon (;) to terminate each statement. The default in Hibernate is to have a statement per line, without requiring
a terminator other than newline: remember to convert your scripts to use the ; terminator character if you’re reusing
existing scripts. This is useful so to allow multi-line statements and human friendly formatting.

## Caching


## Interceotors

Quarkus provides an interceptor that can be used to intercept the execution of a method.
Either extends org.hibernate.EmptyInterceptor or implements org.hibernate.Interceptor directly.

```java
@PersistenceUnitExtension
public static class MyInterceptor extends EmptyInterceptor {
    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state,
                          String[] propertyNames, Type[] types) {
        // ...
        return false;
    }
}
```

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/code-with-quarkus-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
