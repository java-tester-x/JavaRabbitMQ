import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

/**
 * javac -cp .;lib/* -d bin src/Send.java
 * java  -cp .;lib/*;bin/ Send "Hi,A.B." @Hi, the Beutiful World!"
 */

public class Send {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws java.io.IOException, java.util.concurrent.TimeoutException
    {
        //create a connection to the server:
        ConnectionFactory factory = new ConnectionFactory();
        // If we wanted to connect to a broker on a different machine 
        // we'd simply specify its name or IP address here.
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //To send, we must declare a queue for us to send to;
        //then we can publish a message to the queue:
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        if (argv.length == 0) {
            argv = new String[]{"Hello World!"};
        }
        for(String message : argv) {
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }

        //close the channel and the connection;
        channel.close();
        connection.close();
    }
}