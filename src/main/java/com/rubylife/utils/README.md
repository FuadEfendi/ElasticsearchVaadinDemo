export HOST_IP=10.31.44.227

### Delete Index:
curl -XDELETE 'http://'$HOST_IP':9200/avid2/?pretty'

### Create Index

curl -XPUT 'http://'$HOST_IP':9200/avid2/?pretty' -d '{
    "settings" : {
        "number_of_shards" : 1,
        "number_of_replicas" : 1
    }
}'

### Disable auto-refresh
curl -XPUT 'http://'$HOST_IP':9200/avid2/_settings?pretty' -d '{
    "index" : {
        "refresh_interval" : "-1"
    } }'

### Run indexer
export ES_HEAP_SIZE=12g

java -Xms2G -Xmx2G -jar ~/avid.jar profile 16 >out.log 2>&1 &

### Optimize    
curl -XPOST 'http://'$HOST_IP':9200/avid2/_forcemerge?max_num_segments=1'

### Enable auto-refresh
curl -XPUT 'http://'$HOST_IP':9200/avid2/_settings?pretty' -d '{
    "index" : {
        "refresh_interval" : "1s"
    } }'

curl -XPUT 'http://'$HOST_IP':9200/avid3/_settings?pretty' -d '{
    "index" : {
        "refresh_interval" : "1s"
    } }'


## Tune index for the optimal performance

### Close index
curl -XPOST 'http://'$HOST_IP':9200/avid2/_close?pretty'

### Tune index settings
curl -XPUT 'http://'$HOST_IP':9200/avid2/_settings?pretty' -d '{
  "settings": {
    "index.store.type": "mmapfs",
    "index.translog.flush_threshold_size": "1024mb",
    "index.translog.flush_threshold_ops": 1000000,
    "index.translog.flush_threshold_period": "30m",
    "index.translog.interval": "5m",
    "index.translog.sync_interval": "5m",
    "index.translog.durability": "async",
    "indices.memory.index_buffer_size": "30%"
  }
}'

### Open index
curl -XPOST 'http://'$HOST_IP':9200/avid2/_open?pretty'

