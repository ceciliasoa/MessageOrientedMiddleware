package Client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.jms.JMSException;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.awt.Font;


public class ClientView implements CellListener {
	private ClientController controller;
	private JFrame frame;
	private JPanel topicPanel;
	private JScrollPane scrollPane;
	private JPanel topicSpace;
	private JPanel queueSpace;
	private JButton sendMessageButton;
	
    public Map<String, ClientCell> myTopics = new HashMap<>();
    public Map<String, ClientCell> myQueues = new HashMap<>();
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private JPanel queuePanel;
    private JScrollPane scrollQueue;
    private JButton addNewChat;
    private JLabel lblMinhasFilas;
    private JButton sendMessageQueue;


	public void startClient() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize();
					frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	public ClientView(String clientId) {
		
		controller = new ClientController(this);
		controller.clientID = clientId;
   
	}
	
//	
//	void setupClientView() throws JMSException {
//		controller = new ClientController();
//	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 587, 582);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(6, 6, 575, 543);
		frame.getContentPane().add(tabbedPane);
		
		queuePanel = new JPanel();
		queuePanel.setLayout(null);
		tabbedPane.addTab("Filas", null, queuePanel, null);
		
		scrollQueue = new JScrollPane();
		scrollQueue.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollQueue.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollQueue.setBounds(21, 59, 511, 391);
		queuePanel.add(scrollQueue);
		
		queueSpace = new JPanel();
		queueSpace.setLayout(new BoxLayout(queueSpace, BoxLayout.Y_AXIS));
		scrollQueue.setViewportView(queueSpace);
		
		addNewChat = new JButton("consumir fila");
		addNewChat.setBounds(21, 462, 183, 29);
		addNewChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	showConsumerDialog();
            }
        });
		queuePanel.add(addNewChat);
		
		lblMinhasFilas = new JLabel("Minhas Filas");
		lblMinhasFilas.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		lblMinhasFilas.setBounds(209, 22, 139, 25);
		queuePanel.add(lblMinhasFilas);
		
		sendMessageQueue = new JButton("enviar mensagem a cliente");
		sendMessageQueue.setBounds(299, 462, 233, 29);
		sendMessageQueue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	showQueueDialog();
            }
        });
		queuePanel.add(sendMessageQueue);
		
		topicPanel = new JPanel();
		tabbedPane.addTab("Tópicos", null, topicPanel, null);
		topicPanel.setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(21, 59, 511, 391);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		topicPanel.add(scrollPane);
		
		topicSpace = new JPanel();
		topicSpace.setLayout(new BoxLayout(topicSpace, BoxLayout.Y_AXIS));
		scrollPane.setViewportView(topicSpace);
		
		JButton btnNewButton = new JButton("acompanhar tópico");
		btnNewButton.setBounds(21, 462, 183, 29);
		btnNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	showSubscriberDialog();
            }
        });
		topicPanel.add(btnNewButton);
		
		JLabel lblNewLabel = new JLabel("Meus Tópicos");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		lblNewLabel.setBounds(209, 22, 139, 25);
		topicPanel.add(lblNewLabel);
		
		sendMessageButton = new JButton("enviar mensagem ao tópico");
		sendMessageButton.setBounds(299, 462, 233, 29);
		sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	showPublisherDialog();
            }
        });
		topicPanel.add(sendMessageButton);
	}
	
	 private void showQueueDialog() {
	        JPanel panel = new JPanel();
	        JTextField queueNameField = new JTextField(10);
	        JTextField otherField = new JTextField(10);

	        panel.add(new JLabel("Nome da fila:"));
	        panel.add(queueNameField);
	        panel.add(new JLabel("mensagem:"));
	        panel.add(otherField);

	        int result = JOptionPane.showConfirmDialog(
	                frame,
	                panel,
	                "Dois Campos de Entrada",
	                JOptionPane.OK_CANCEL_OPTION,
	                JOptionPane.PLAIN_MESSAGE);

	        if (result == JOptionPane.OK_OPTION) {
	            String queueName = queueNameField.getText();
	            String otherValue = otherField.getText();

	            if (!queueName.isEmpty()) {
	                try {
	                	sendQueueMessage(queueName, otherValue);
	                } catch (JMSException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }
	 
		 private void showConsumerDialog() {
		        String inputName = JOptionPane.showInputDialog(
		                frame,
		                "Digite o nome da Fila:",
		                "Nova Fila",
		                JOptionPane.QUESTION_MESSAGE);
	
		        if (inputName != null && !inputName.isEmpty()) {
		            try {
		            	addConsumerQueueCard(inputName);
		            } catch (JMSException e) {
		                e.printStackTrace();
		            }
		        }
		    }
	
	
	 private void showSubscriberDialog() {
	        String selectedTopic = JOptionPane.showInputDialog(
	                frame,
	                "Digite o nome do tópico:",
	                "Novo Tópico",
	                JOptionPane.QUESTION_MESSAGE);

	        if (selectedTopic != null && !selectedTopic.isEmpty()) {
	            try {
	                addSubscribedTopicCard(selectedTopic);
	            } catch (JMSException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	 
	 private void showPublisherDialog() {
	        JPanel panel = new JPanel();
	        JTextField topicNameField = new JTextField(10);
	        JTextField otherField = new JTextField(10);

	        panel.add(new JLabel("Nome do Tópico:"));
	        panel.add(topicNameField);
	        panel.add(new JLabel("mensagem:"));
	        panel.add(otherField);

	        int result = JOptionPane.showConfirmDialog(
	                frame,
	                panel,
	                "Dois Campos de Entrada",
	                JOptionPane.OK_CANCEL_OPTION,
	                JOptionPane.PLAIN_MESSAGE);

	        if (result == JOptionPane.OK_OPTION) {
	            String topicName = topicNameField.getText();
	            String otherValue = otherField.getText();

	            if (!topicName.isEmpty()) {
	                try {
	                	publishInTopic(topicName, otherValue);
	                } catch (JMSException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }
	 
	
	 public void updateTopicItem(String item) throws JMSException {
		 System.out.println(myTopics);
	     if (myTopics.containsKey(item)) {
	    	System.out.println("äquii");
	      	ClientCell cellToRemove = myTopics.get(item);
	        myTopics.remove(item);
	        unsubscribeTopic(item, cellToRemove);
	        System.out.println("äquii");
	    }
	 }
	 
	 private void addSubscribedTopicCard(String topicName) throws JMSException {
		 ClientCell cell = new ClientCell(topicName, this);
		 controller.createSubscription(topicName, cell);

		 myTopics.put(topicName, cell);
		 System.out.println(myTopics);
	     topicSpace.add(cell);
	     frame.revalidate();
	     frame.repaint();
	 }
	 
	 private void publishInTopic(String topicName, String message) throws JMSException {
		 controller.publishMessage(topicName, message);
	     frame.revalidate();
	     frame.repaint();
	 }

	@Override
	public void unsubscribeTopic(String topicName, JPanel cell) throws JMSException {
        System.out.println("Cartão excluído: " + topicName);
        controller.removeSubscription(); 
        myTopics.remove(topicName);
        topicSpace.remove(cell);

        frame.revalidate();
        frame.repaint();		
	}
	 
	 private void addConsumerQueueCard(String queueName) throws JMSException {
		 ClientCell cell = new ClientCell(queueName, this);
		 cell.isQueue = true;
		 getPendingMessages(cell, queueName);
		 
		 myQueues.put(queueName, cell);

	     queueSpace.add(cell);
	     startPeriodicCheckNewMessagesTask(queueName);
	     frame.revalidate();
	     frame.repaint();
	 }
	 
	 private void getPendingMessages(ClientCell cell, String queueName) throws JMSException {

			List<String> pendingMessages = controller.getPendingQueueMessage(queueName);

			pendingMessages.forEach(message -> {
				cell.didReceiveMessage(message);
				
			});
	 }
	  
	 private void sendQueueMessage(String queueName, String message) throws JMSException {
		 controller.sendMessageToClient(queueName, message);
	 }

	@Override
	public void removeConsumer(String queueName, JPanel cell) throws JMSException {
		 System.out.println("Cartão excluído: " + queueName);
	     queueSpace.remove(cell);

	     frame.revalidate();
	     frame.repaint();	
		
	}

	@Override
	public void updateQueueMessage(String queueName, String message) {
		 ClientCell clientCell = myQueues.get(queueName);
		 System.out.println(message);
		 if (clientCell != null) {
	           clientCell.didReceiveMessage(message);
		 }
		
	}
	
	public void startPeriodicCheckNewMessagesTask(String queueName) {
	    executor.scheduleAtFixedRate(() -> {
	        try {
	            checkNewMessagesInBackground(queueName);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }, 0, 5, TimeUnit.SECONDS);
	}

	private void checkNewMessagesInBackground(String queueName) {
	    SwingUtilities.invokeLater(() -> {
	        try {
	            List<String> newMessages = controller.getPendingQueueMessage(queueName);
	            for (String message : newMessages) {
	            	updateQueueMessage(queueName, message);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });
	}
}

