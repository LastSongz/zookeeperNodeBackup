#!/bin/bash
port=8080
version=es65

cd $(dirname $0)

#批处理
batch() {
  url=localhost:${port}/${version}/batchSave
  curl --request GET -sL \
  --url $url/$1
}

#删除
delete() {
  url=localhost:${port}/${version}/deleteAll
  curl --request GET -sL \
  --url $url
}

#分页查询
query() {
  url=localhost:${port}/${version}/query
  curl --request POST -sL $url \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'page='${1}'' \
  --data-urlencode 'size='${2}''
}

case "$1" in
batch)
  batch $2
  ;;
delete)
  delete
  ;;
query)
  query $2 $3
  ;;
*)
  echo $"Usage: $0 {batch|delete|query}"
  exit 1
  ;;
esac
