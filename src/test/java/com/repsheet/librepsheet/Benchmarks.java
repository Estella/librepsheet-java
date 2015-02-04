package com.repsheet.librepsheet;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class Benchmarks {
    private Connection connection = new Connection("localhost");

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureLookupWhitelistIP() {
        Actor actor = connection.lookup(connection, Actor.Type.IP, "1.1.1.1");
        actor.getStatus();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureLookupWhitelistCIDR() {
        Actor actor = connection.lookup(connection, Actor.Type.IP, "2.2.2.15");
        actor.getStatus();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureLookupBlacklistIP() {
        Actor actor = connection.lookup(connection, Actor.Type.IP, "1.1.1.2");
        actor.getStatus();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureLookupBlacklistCIDR() {
        Actor actor = connection.lookup(connection, Actor.Type.IP, "2.2.3.15");
        actor.getStatus();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureLookupMarkedIP() {
        Actor actor = connection.lookup(connection, Actor.Type.IP, "1.1.1.3");
        actor.getStatus();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureLookupMarkedCIDR() {
        Actor actor = connection.lookup(connection, Actor.Type.IP, "2.2.4.15");
        actor.getStatus();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureLookupOKIP() {
        Actor actor = connection.lookup(connection, Actor.Type.IP, "1.1.1.254");
        actor.getStatus();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureLookupWhitelistUser() {
        Actor actor = connection.lookup(connection, Actor.Type.USER, "whitelist-benchmark");
        actor.getStatus();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureLookupBlacklistUser() {
        Actor actor = connection.lookup(connection, Actor.Type.USER, "blacklist-benchmark");
        actor.getStatus();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureLookupMarkedUser() {
        Actor actor = connection.lookup(connection, Actor.Type.USER, "marked-benchmark");
        actor.getStatus();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureLookupWhitelistedUserAgent() {
        Actor actor = connection.lookup(connection, Actor.Type.USERAGENT, "curl/7.35.0-w");
        actor.getStatus();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureLookupBlacklistedUserAgent() {
        Actor actor = connection.lookup(connection, Actor.Type.USERAGENT, "curl/7.35.0-b");
        actor.getStatus();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureLookupMarkedUserAgent() {
        Actor actor = connection.lookup(connection, Actor.Type.USERAGENT, "curl/7.35.0-m");
        actor.getStatus();
    }
}
