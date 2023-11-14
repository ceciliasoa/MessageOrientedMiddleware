package manager;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.jms.JMSException;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import javax.swing.table.DefaultTableModel;

import Client.ClientView;

import java.awt.Font;


public class ManagerView {
	
	private ManagerController controller;
	DefaultTableModel queueTableModel = new DefaultTableModel();
    private JFrame frame;
	private final JButton addClient = new JButton("Adicionar cliente");
	private JTextField addQueueTextField;
	private JTable queueTable;
	private JPanel queue;
	private JTextField topicTextField;
	private JTable topicTable;
	
	private List<ClientView> clients =  new ArrayList<>();
	

	
	public void startManager() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ManagerView window = new ManagerView();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ManagerView() {
		
		try {
            setupManagerController(); // Initialize the controller
        } catch (JMSException e) {
            e.printStackTrace();
        }
		initialize();
	}
	
	void setupManagerController() throws JMSException {
		controller = new ManagerController();
		controller.startConnection();

	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 621, 428);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 621, 371);
		tabbedPane.setForeground(Color.BLACK);
		frame.getContentPane().add(tabbedPane);
		
		// ------------ SETUP QUEUE  -------------
		queue = new JPanel();
		tabbedPane.addTab("Fila", null, queue, null);
		queue.setLayout(null);
		
		JButton addQueueButton = new JButton("Criar fila");
		addQueueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addQueueItem();
			}
		});
		addQueueButton.setBounds(16, 290, 117, 29);
		queue.add(addQueueButton);
		
		addQueueTextField = new JTextField();
		addQueueTextField.setToolTipText("digite o nome");
		addQueueTextField.setBounds(125, 290, 130, 26);
		queue.add(addQueueTextField);
		addQueueTextField.setColumns(10);
		
		JButton removeQueue = new JButton("remover fila");
		removeQueue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeQueueItem();
			}
		});
		removeQueue.setBounds(447, 49, 153, 29);
		queue.add(removeQueue);
		
		queueTable = new JTable();
		queueTable.setBorder(null);
		queueTable.setBackground(Color.WHITE);
		queueTable.setCellSelectionEnabled(true);
		queueTable.setDefaultEditor(Object.class, null);
		queueTable.setBounds(21, 39, 420, 245);
		updateQueueTable();
		queue.add(queueTable);
		
		JLabel lblNewLabel = new JLabel("Filas");
		lblNewLabel.setFont(new Font("PT Serif", Font.BOLD, 20));
		lblNewLabel.setBounds(179, 16, 52, 16);
		queue.add(lblNewLabel);
		
		// --------- TOPIC ----------------
		JPanel topic = new JPanel();
		topic.setLayout(null);
		tabbedPane.addTab("Tópico", null, topic, null);
		
		JButton addTopicButton = new JButton("Criar tópico");
		addTopicButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addTopicItem();
			}
		});
		addTopicButton.setBounds(16, 290, 117, 29);
		topic.add(addTopicButton);
		
		topicTextField = new JTextField();
		topicTextField.setToolTipText("digite o nome");
		topicTextField.setColumns(10);
		topicTextField.setBounds(125, 290, 130, 26);
		topic.add(topicTextField);
		
		JButton removeTopic = new JButton("remover tópico");
		removeTopic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					removeTopicItem();
				} catch (JMSException e1) {
					e1.printStackTrace();
				}
			}
		});
		removeTopic.setBounds(447, 49, 153, 29);
		topic.add(removeTopic);
		
		topicTable = new JTable();
		topicTable.setBorder(null);
		topicTable.setBackground(Color.WHITE);
		topicTable.setCellSelectionEnabled(true);
		topicTable.setDefaultEditor(Object.class, null);
		topicTable.setBounds(21, 39, 420, 245);
		updateTopicTable();
		topic.add(topicTable);
		
		JLabel topicLabel = new JLabel("tópicos");
		topicLabel.setFont(new Font("PT Serif", Font.BOLD, 20));
		topicLabel.setBounds(190, 6, 76, 29);
		topic.add(topicLabel);
		
		// ------------ Client ---------------------------------
		addClient.setBounds(0, 371, 621, 29);
		addClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showClientDialog();
			}
		});
		frame.getContentPane().add(addClient);
		
		startPeriodicUpdateTableTask();
	
	}
	
	private void updateQueueTable() {
		try {
	        queueTableModel.setRowCount(0);
	        if (queueTableModel.getColumnCount() == 0) {
	            queueTableModel.addColumn("Nome da Fila");
	            queueTableModel.addColumn("Mensagens Pendentes");
	        }

	        for (String queueName : controller.getExistingQueuesNames()) {
	            int pendingMessages = controller.getAmountOfPendingMessagesFromQueue(queueName);

	            queueTableModel.addRow(new Object[]{queueName, pendingMessages});
	        }

	        queueTable.setModel(queueTableModel);
	    } catch (JMSException e) {
	        e.printStackTrace();
	    }	
	}
	
	private void addQueueItem() {
		String queueName = addQueueTextField.getText(); 
		if (!queueName.isEmpty()) {
			try {
				controller.createQueue(queueName);
				updateQueueTable();
			} catch (JMSException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private void removeQueueItem() {
		int selectedRow = queueTable.getSelectedRow();
		int selectedColumn = queueTable.getSelectedColumn();
		System.out.println(selectedColumn);
		System.out.println(selectedRow);

		if (selectedRow >= 0 && selectedColumn >= 0) {
		    Object selectedValue = queueTable.getValueAt(selectedRow, 0);
		    String queueName = selectedValue.toString();
				try {
					controller.deleteQueue(queueName);
					updateQueueTable();
				} catch (JMSException e1) {
					e1.printStackTrace();
				}
		}
	}
	
	
	private void updateTopicTable() {
		DefaultTableModel tableModel = new DefaultTableModel();
		tableModel.addColumn("Nome do Topico"); // Adicione as colunas necessárias
		try {

			tableModel.setRowCount(0);
		    for (String topicName : controller.getExistingTopicNames()) {
		    	System.out.println(topicName);
		        tableModel.addRow(new Object[]{topicName});
		    }

		    topicTable.setModel(tableModel);
		} catch (JMSException e) {
		    e.printStackTrace();
		}	
	}
	
	private void addTopicItem() {
		String topicName = topicTextField.getText(); 
		if (!topicName.isEmpty()) {
			try {
				controller.createTopic(topicName);
				updateTopicTable();
			} catch (JMSException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private void removeTopicItem() throws JMSException {
		int selectedRow = topicTable.getSelectedRow();
		int selectedColumn = topicTable.getSelectedColumn();
		
		if (selectedRow >= 0 && selectedColumn >= 0) {
			
		    Object selectedValue = topicTable.getValueAt(selectedRow, 0);
		    String topicName = selectedValue.toString();
		    try {
		    	updateClients(topicName);
				controller.deleteTopic(topicName);
				updateTopicTable();
			} catch (JMSException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private void showClientDialog() {
	     String selectedTopic = JOptionPane.showInputDialog(
	                frame,
	                "Digite o nome do client:",
	                "Novo Tópico",
	                JOptionPane.QUESTION_MESSAGE);

	        if (selectedTopic != null && !selectedTopic.isEmpty()) {
	            addClient(selectedTopic);
	        }
	    }
	
	private void addClient(String clientID) {
		ClientView client = new ClientView(clientID);
		clients.add(client);
		client.startClient();
		
	}
	
	 
	 private void updateClients(String name) throws JMSException {
			for (ClientView client : clients) {
	            client.updateTopicItem(name);
	        }
		}
	 
//	 background
	 public void startPeriodicUpdateTableTask() {
		    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		    
		    executor.scheduleAtFixedRate(() -> {
		        try {
		            updateTableInBackground();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }, 0, 5, TimeUnit.SECONDS); 
	}

	private void updateTableInBackground() {
		    SwingUtilities.invokeLater(() -> {
		        try {
		        	updateTopicTable();
		            updateQueueTable(); 
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    });
	}	

}