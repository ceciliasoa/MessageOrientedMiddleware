import javax.jms.JMSException;

import manager.ManagerView;

public class Main {

	public static void main(String[] args) throws JMSException {		
		ManagerView view = new ManagerView();
		view.startManager();	
	}
}
