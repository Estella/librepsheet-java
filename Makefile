PATH := ./redis-git/src:${PATH}

define REDIS1_CONF
daemonize yes
port 6379
pidfile /tmp/redis1.pid
logfile /tmp/redis1.log
save ""
appendonly no
client-output-buffer-limit pubsub 256k 128k 5
endef

define REDIS3_CONF
daemonize yes
port 6381
pidfile /tmp/redis3.pid
logfile /tmp/redis3.log
save ""
appendonly no
endef

# SENTINELS
define REDIS_SENTINEL1
port 26379
daemonize yes
sentinel monitor mymaster 127.0.0.1 6379 1
sentinel down-after-milliseconds mymaster 2000
sentinel failover-timeout mymaster 120000
sentinel parallel-syncs mymaster 1
pidfile /tmp/sentinel1.pid
logfile /tmp/sentinel1.log
endef

define REDIS_SENTINEL2
port 26380
daemonize yes
sentinel monitor mymaster 127.0.0.1 6381 1
sentinel down-after-milliseconds mymaster 2000
sentinel parallel-syncs mymaster 1
sentinel failover-timeout mymaster 120000
pidfile /tmp/sentinel2.pid
logfile /tmp/sentinel2.log
endef

define REDIS_SENTINEL3
port 26382
daemonize yes
sentinel monitor mymaster 127.0.0.1 6381 1
sentinel down-after-milliseconds mymaster 2000
sentinel parallel-syncs mymaster 1
sentinel failover-timeout mymaster 120000
pidfile /tmp/sentinel3.pid
logfile /tmp/sentinel3.log
endef

export REDIS1_CONF
export REDIS3_CONF
export REDIS_SENTINEL1
export REDIS_SENTINEL2
export REDIS_SENTINEL3

start: cleanup
	echo "$$REDIS1_CONF" | redis-server -
	echo "$$REDIS3_CONF" | redis-server -
	echo "$$REDIS_SENTINEL1" > /tmp/sentinel1.conf && redis-server /tmp/sentinel1.conf --sentinel
	@sleep 0.5
	echo "$$REDIS_SENTINEL2" > /tmp/sentinel2.conf && redis-server /tmp/sentinel2.conf --sentinel
	@sleep 0.5
	echo "$$REDIS_SENTINEL3" > /tmp/sentinel3.conf && redis-server /tmp/sentinel3.conf --sentinel

cleanup:
	- rm -vf /tmp/redis_cluster_node*.conf 2>/dev/null
	- rm dump.rdb appendonly.aof - 2>/dev/null

stop:
	kill `cat /tmp/redis1.pid`
	kill `cat /tmp/redis3.pid`
	kill `cat /tmp/sentinel1.pid`
	kill `cat /tmp/sentinel2.pid`
	kill `cat /tmp/sentinel3.pid`
	rm -f /tmp/sentinel1.conf
	rm -f /tmp/sentinel2.conf
	rm -f /tmp/sentinel3.conf

test:
	make start
	sleep 2
	mvn -Dtest=${TEST} clean compile test
	make stop

travis-install:
	[ ! -e redis-git ] && git clone https://github.com/antirez/redis.git redis-git || true
	make -C redis-git -j4

.PHONY: test
