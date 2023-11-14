package Client;

import javax.jms.JMSException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientCell extends JPanel implements SubscribeListener {
	private static final long serialVersionUID = 1L;

	private DefaultListModel<String> listModel;
	private JList<String> list;
	Boolean isQueue = false;
    public ClientCell(String texto, CellListener cellListener) {
    	setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setPreferredSize(new Dimension(500, 181));
        setLayout(null);

        JLabel label = new JLabel(texto);
        label.setBounds(6, 6, 113, 25);
        label.setFont(new Font(label.getFont().getName(), Font.PLAIN, 20)); 

        add(label);
        
        JButton removeTopic = new JButton("sair");
        removeTopic.setBounds(381, 8, 113, 29);
        removeTopic.addActionListener(new ActionListener() {
            @Override
            	public void actionPerformed(ActionEvent e) {
            		try {
            			if (isQueue) {
            				cellListener.removeConsumer(label.getText(), ClientCell.this);
            			} else {
            				cellListener.unsubscribeTopic(label.getText(), ClientCell.this);
            			}
					} catch (JMSException e1) {
						e1.printStackTrace();
					}
                }
           });
        add(removeTopic);
               
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setVisibleRowCount(2);
        list.setBounds(16, 43, 466, 132);
        add(list);
       
    }
    
	@Override
	public void didReceiveMessage(String message) {
		
		System.out.println("ëntrou aqui");
		listModel.addElement(message);
		
	}
}

