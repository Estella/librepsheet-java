# librepsheet [![Build Status](https://secure.travis-ci.org/repsheet/librepsheet-java.png)](http://travis-ci.org/repsheet/librepsheet-java?branch=master) [![Coverity Status](https://scan.coverity.com/projects/4082/badge.svg?flat=1)](https://scan.coverity.com/projects/4082)

This is the core logic library for Repsheet. It provides the abstraction on top of the Redis cache used by Repsheet.
The idea is that this library will supply everything needed to implement Repsheet in any program. This is the Java
version of the core Library. It is meant to be dropped into any program that wants to take advantage of Repsheet.
The common use case is inside of a filter/handler/interceptor/etc. inside of a Java web application.

## Installation

```xml
<dependency>
    <groupId>com.aaronbedra</groupId>
    <artifactId>librepsheet</artifactId>
    <version>0.1.2</version>
</dependency>
```

## Usage

#### Connections

librepsheet provides a `Connection` class. This is simply a wrapper over the [Jedis](https://github.com/xetorthio/jedis)
connection pool. This is used because the standard Jedis connection isn't thread safe. Using the provided `Connection`
class will deal with this for you so you don't have to think about how the cache is working underneath. To get a
connection simply use the following:

#Connect to Single Redis Host

```java
import org.repsheet.librepsheet.Connection;

...

public void someFunc() {
  Connection connection = new Connection("localhost", 6379, 5);
}
```

The three arguments to the constructor are the Redis host, port, and connection timeout in milliseconds. This will
create the connection pool for you to pass along to the `Actor` class when querying the cache.

#Connect to Redis Sentinel Hosts

```java
import org.repsheet.librepsheet.Connection;

...

public void someFunc() {
    Set<String> sentinels = new HashSet<>;
    sentinels.add("localhost:26379");
    sentinels.add("localhost:26380");

    Connection connection = Connection.new("mymaster", sentinels);
}
```

The two arguments to the constructor are the Redis Sentinel master name, and a set of "host:port" strings.

#### Actors

Once you have a connection, you can ask the status of an actor. You do this by creating an instance of the `Actor`
class and asking its status:

```java
import org.repsheet.librepsheet.Actor;

public void someFunc() {
  Connection connection = Connection.new("localhost", 6379);
  Actor actor = connection.lookup(connection, Actor.Types.IP, "1.1.1.1");

  switch(actor.getStatus()) {
    case WHITELISTED:
      System.out.println("Whitelisted: " + actorStatus.getReason());
      break;
    case BLACKLISTED:
      System.out.println("Blacklisted: " + actorStatus.getReason());
      break;
    case MARKED:
      System.out.println("Marked: " + actorStatus.getReason());
      break;
    default:
      System.out.println("Actor not found on Repsheet");
      break;
  }
}
```

There are three different actor types. They are `IP`, `CIDR`, and `USER`. This tells librepsheet how to locate the
actor in the cache. There are four actor statuses. They are `WHITELISTED`, `BLACKLISTED`, `MARKED`, and `OK`.
White and black list should be straight forward. `MARKED` is used to identify actors who are suspected of malicious
activity but haven't been blacklisted yet. `OK` means that Repsheet knows nothing about this IP and it should be
considered fine for further requests.

#### X-Forwarded-For

If you are integrating this directly into a web handler, chances are you will need to parse the `X-Forwraded-For`
header to get the real source IP address. You can use `XFF.getSourceAddress` and pass it the header value.

```java
String address = XFF.getSourceAddress("1.1.1.1, 2.2.2.2, 3.3.3.3")
```

This will return either an IP address or null if the header is not valid. This method also works with IPv6 addresses.
