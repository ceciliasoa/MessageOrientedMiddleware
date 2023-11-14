package manager;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

public class ManagerController {
	
	ConnectionFactory connectionFactory;
	Connection connection;
	Session session;
	
	public void startConnection() throws JMSException {
		String url = ActiveMQConnection.DEFAULT_BROKER_URL;
		connectionFactory = new ActiveMQConnectionFactory(url);
		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}
	
	public void createQueue(String name) throws JMSException {
	    Destination destination = session.createQueue(name);      
	    MessageProducer producer = session.createProducer(destination);
	    producer.close();
	}
	
	public void deleteQueue(String name) throws JMSException {
	      Destination destination = session.createQueue(name);      
	      ((ActiveMQConnection) connection).destroyDestination((ActiveMQDestination) destination);
	}
	
	public List<String> getExistingQueuesNames() throws JMSException {
		Set<ActiveMQQueue> queues = ((ActiveMQConnection) connection).getDestinationSource().getQueues();
		List<String> queueNames = new ArrayList<>();
		queues.forEach(fila -> {
			String nome = fila.getPhysicalName();
			queueNames.add(nome);

		});
		return queueNames;
	}
	
	int getAmountOfPendingMessagesFromQueue(String queueName) throws JMSException {
		ActiveMQQueue queue = (ActiveMQQueue) session.createQueue(queueName);
		QueueBrowser browser = session.createBrowser((ActiveMQQueue) queue);
		Enumeration<?> enumeration = browser.getEnumeration();
		int count = 0;
		while (enumeration.hasMoreElements()) {
			enumeration.nextElement();
			count++;
		}
		return count;
	}
	
	public void createTopic(String name) throws JMSException {
	    Destination destination = session.createTopic(name);      
		MessageProducer publisher = session.createProducer(destination);
		publisher.close();
		
	}
	
	public void deleteTopic(String name) throws JMSException {
	      Destination destination = session.createTopic(name);      
	      ((ActiveMQConnection) connection).destroyDestination((ActiveMQDestination) destination);
	}
	
	public List<String> getExistingTopicNames() throws JMSException {
		Set<ActiveMQTopic> topics = ((ActiveMQConnection) connection).getDestinationSource().getTopics();
		List<String> topicsNames = new ArrayList<>();
		topics.forEach(fila -> {
			String nome = fila.getPhysicalName();
			topicsNames.add(nome);

		});
		return topicsNames;
	}
	
	
	
	
}
