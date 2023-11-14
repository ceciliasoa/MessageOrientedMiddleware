package Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ClientController implements MessageListener {
    private Map<String, SubscribeListener> subscribeListeners;
    String subscriptionId;
	String clientID = "";
	String lastMessage = "";
	Consumer<String> reactToMessage;

	ConnectionFactory connectionFactory;
	Connection connection;
	Session session;
	MessageConsumer subscriber;
	MessageConsumer consumer;
	
	public ClientController(CellListener listener) {
        this.subscribeListeners = new HashMap<>();

    }
	
	public void startConnection() throws JMSException {
		String url = ActiveMQConnection.DEFAULT_BROKER_URL;
		connectionFactory = new ActiveMQConnectionFactory(url);
		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
	}
	
	public void publishMessage(String topicName, String message) throws JMSException {
		startConnection();

		Destination dest = session.createTopic(topicName);
		
		MessageProducer publisher = session.createProducer(dest);

		TextMessage messageText = session.createTextMessage();
		messageText.setText(message);
		publisher.send(messageText);

	}

	public void createSubscription(String topicName, SubscribeListener subscribeListener) throws JMSException {
		subscriptionId = topicName;		
		startConnection();

		Destination dest = session.createTopic(topicName);

		subscriber = session.createConsumer(dest);
		subscriber.setMessageListener(this);
		subscribeListeners.put(topicName, subscribeListener);
	}

	public void removeSubscription() throws JMSException {
		connection.close();
		session.close();
		subscriber.close();
	}

	@Override
	public void onMessage(Message message) {

		if (message instanceof TextMessage) {
			try {
				System.out.println(((TextMessage) message).getText());
				Destination destination = message.getJMSDestination();
	            if (destination instanceof Topic) {
	                String topicName = ((Topic) destination).getTopicName();
	                SubscribeListener subscribeListener = subscribeListeners.get(topicName);
	                if (subscribeListener != null) {
	                	subscribeListener.didReceiveMessage(((TextMessage) message).getText());
	                }
	            } 

			} catch (Exception e) {
		        System.out.println("erro ao receber mensagem");

			}
		}

	}  

	public void sendMessageToClient(String queueName, String message) throws JMSException {
		startConnection();
         Destination destination = session.createQueue(queueName);

         MessageProducer producer = session.createProducer(destination);
         TextMessage messageText = session.createTextMessage(message);
         messageText.setStringProperty("remetente", clientID);
         messageText.setStringProperty("fila", queueName);
         producer.send(messageText);
         producer.close();
	}
	

	public List<String> getPendingQueueMessage(String queueName) throws JMSException {
		startConnection();
		Destination destination = session.createQueue(queueName);
	    MessageConsumer consumer = session.createConsumer(destination, "remetente <> '" + clientID + "'");

	    List<String> messages = new ArrayList<>();

	    while (true) {
	        Message message = consumer.receive(1000); // Timeout de 1 segundo

	        if (message == null) {
	            break;
	        }

	        if (message instanceof TextMessage) {
	            TextMessage textMessage = (TextMessage) message;
	            messages.add(textMessage.getText());
	        }
	    }
	    consumer.close();
	    return messages;
	}

}
