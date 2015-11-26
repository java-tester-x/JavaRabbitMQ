import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;

import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP;

/**
 * javac -cp .;lib/* -d bin src/Recv.java
 * java -cp .;lib/*;bin/ Recv
 * [*] Waiting for messages. To exit press CTRL+C
 * [x] Received 'Hello World!'
 * [x] Received 'Hi,A.B.'
 * [x] Received '@Hi,'
 * [x] Received 'the'
 * [x] Received 'Beutiful'
 * [x] Received 'World!'
 * [x] Received 'Hi,A.B.'
 * [x] Received 'Hi,'
 * [x] Received 'the'
 * [x] Received 'Beutiful'
 * [x] Received 'World!'
 * [x] Received 'Hi,A.B.'
 * [x] Received 'Hi, the Beutiful World!'
 */
public class Recv {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv)
        throws java.io.IOException, java.lang.InterruptedException, java.util.concurrent.TimeoutException
    {
        //open a connection and a channel, and declare the queue from which we're going to consume.
        //Note this matches up with the queue that send publishes to.
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        //Because we might start the receiver before the sender,
        //we want to make sure the queue exists before we try
        //to consume messages from it.
        //We're about to tell the server to deliver us the messages from the queue.
        //Since it will push us messages asynchronously, we provide a callback 
        //in the form of an object that will buffer the messages until we're ready 
        //to use them. That is what a DefaultConsumer subclass does.
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                throws java.io.IOException
            {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            }
        };
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}