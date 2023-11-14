package Client;

import javax.jms.JMSException;
import javax.swing.JPanel;

public interface CellListener {
	void unsubscribeTopic(String topicName, JPanel cell) throws JMSException ;
	void removeConsumer(String queueName, JPanel cell) throws JMSException ;
	void updateQueueMessage(String queueName,String message);
}

