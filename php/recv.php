<?php

require_once("vendor/SplClassLoader.php");
$classLoader = new SplClassLoader('PhpAmqpLib', 'vendor');
$classLoader->register();

use PhpAmqpLib\Connection\AMQPStreamConnection;

//Setting up is the same as the sender; we open a connection and a channel,
//and declare the queue from which we're going to consume.
//Note this matches up with the queue that send publishes to.
$connection = new AMQPStreamConnection('localhost', 5672, 'guest', 'guest');
$channel    = $connection->channel();

$channel->queue_declare('hello', false, false, false, false);
echo ' [*] Waiting for messages. To exit press CTRL+C', "\n";

//Because we might start the receiver before the sender, we want to make sure
//the queue exists before we try to consume messages from it.
//
//We're about to tell the server to deliver us the messages from the queue. 
//We will define a PHP callable that will receive the messages sent by the server.
//Keep in mind that messages are sent asynchronously from the server to the clients.
$callback = function($msg) {
  echo " [x] Received ", $msg->body, "\n";
};

$channel->basic_consume('hello', '', false, true, false, false, $callback);

while(count($channel->callbacks)) {
    $channel->wait();
}

// recv.php