## Delete Index
export HOST_IP=10.31.44.227
curl -XDELETE 'http://'$HOST_IP':9200/index_name'


## Create index

Use following URL to run commands:
http://10.31.44.227:9200/_plugin/kopf/#!/rest

Alternatively you can use "curl".
 

To create an index "my_index", you need to use PUT request to URL path "/my_index/" and submit document such as this:

    {
        "settings": { ... any settings ... },
        "mappings": {
            "type_one": { ... any mappings ... },
            "type_two": { ... any mappings ... },
            ...
        }
    }
 

## Naming conventions
This directory contains JSON files where file name corresponds to index name.




##  References:
https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-create-index.html
https://www.elastic.co/guide/en/elasticsearch/guide/current/_creating_an_index.html

