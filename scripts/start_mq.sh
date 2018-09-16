#!/bin/bash
RABBIT_MQ_HOST="localhost"
rabbitmqctl ping
if [[ -z $? ]]
then
	echo "Rabbit MQ is not running, will start."
	rabbitmq-server &
    set -e

	until timeout 1 bash -c "cat < /dev/null > /dev/tcp/${RABBIT_MQ_HOST}/5672"; do
		>&2 echo "Rabbit MQ not up yet on ${RABBIT_MQ_HOST}"
  	sleep 1
	done	
fi
rabbitmqctl start_app
rabbitmqctl await_online_nodes 1
echo "Rabbit MQ is up"
exit 0;
