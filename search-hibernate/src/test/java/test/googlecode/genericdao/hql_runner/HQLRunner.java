package test.googlecode.genericdao.hql_runner;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class HQLRunner extends JFrame {
	private static final long serialVersionUID = 7980173937104587551L;
	
	//private static final String SPRING_CONFIG_LOCATION = "C:\\workspace\\eclipse1\\hibernate-generic-dao\\src\\test\\resources\\jUnit-applicationContext.xml";
	private static final String SPRING_CONFIG_LOCATION = "classpath:jUnit-applicationContext.xml";
	private static final String SESSION_FACTORY_BEAN_ID = "sessionFactory";
	
	public static void main(String[] args) {
		HQLRunner instance = new HQLRunner(SPRING_CONFIG_LOCATION, SESSION_FACTORY_BEAN_ID);
		instance.setVisible(true);
	}
	
	private ApplicationContext appContext;
	private String sessionFactoryBeanId;
	
	private JTextArea hqlInput;
	private JTable resultsTable;
	private JButton goButton;
	private TheTableModel tableModel;
	
	private HQLRunner(String springConfigLocation, String sessionFactoryBeanId) {
		super("HQL Runner");
		initAppContext(springConfigLocation, sessionFactoryBeanId);
		buildGUI();
		initDB();
	}
	
	private void buildGUI() {
		hqlInput = new JTextArea(6, 70);
		hqlInput.setFont(new Font("Courier New", Font.BOLD, 14));
		hqlInput.setBorder(new EmptyBorder(4, 4, 4, 4));
		
		resultsTable = new JTable(tableModel = new TheTableModel());
		resultsTable.setPreferredSize(new Dimension(15,400));
		
		goButton = new JButton(new AbstractAction("Execute") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				execute(hqlInput.getText());
			}
		});
		
		this.setLayout(new BorderLayout());
		this.add(hqlInput, BorderLayout.NORTH);
		this.add(goButton, BorderLayout.CENTER);
		this.add(resultsTable, BorderLayout.SOUTH);
		this.pack();
		this.setLocation(200,150);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void initAppContext(String springConfigLocation, String sessionFactoryBeanId) {
		//appContext = new FileSystemXmlApplicationContext(springConfigLocation);
		appContext = new ClassPathXmlApplicationContext(springConfigLocation);
		this.sessionFactoryBeanId = sessionFactoryBeanId;
	}
	
	private SessionFactory getSessionFactory() {
		return (SessionFactory) appContext.getBean(sessionFactoryBeanId);
	}
	
	private void initDB() {
		Session session = getSessionFactory().openSession();
		
		session.doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				Statement statement = null;
				try {
					statement = connection.createStatement();
					
					InputStream is = this.getClass().getResourceAsStream("initDB.sql");
					
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					
					String line = br.readLine();
					while (line != null) {
						if (line.startsWith("insert"))
							statement.executeUpdate(line);
						line = br.readLine();
					}
				} catch (Throwable e) {
					tableModel.setError(e);
					e.printStackTrace();
				} finally {
					if (statement != null) {
						statement.close();
					}
				}
			}
		});
		session.close();
		
	}
	
	private void execute(String hql) {
		Session session = getSessionFactory().openSession();
		try {
			List<?> results = session.createQuery(hql).list();
			tableModel.setResults(results);
		} catch (Throwable e) {
			tableModel.setError(e);
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	private class TheTableModel extends AbstractTableModel {

		String[][] data = new String[0][0];
		
		private String toString(Object o) {
			if (o == null) return "null";
			else return o.toString();
		}
		
		public void setResults(List<?> results) {
			if (results == null || results.size() == 0) {
				data = new String[1][1];
				data[0][0] = "-- No Results --";
			} else {
				Object example = results.get(0);
				if (example instanceof Object[]) {
					data = new String[results.size()][((Object[]) example).length];
					for (int i = 0; i < results.size(); i++) {
						Object[] row = (Object[]) results.get(i);
						for (int j = 0; j < row.length; j++) {
							data[i][j] = toString(row[j]);
						}
					}
				} else if (example instanceof Collection) {
					data = new String[results.size()][((Collection) example).size()];
					for (int i = 0; i < results.size(); i++) {
						Collection row = (Collection) results.get(i);
						int j = 0;
						for (Object o : row)
							data[i][j++] = toString(o);
					}
				} else if (example instanceof Map) {
					Object[] keys = new Object[((Map) example).keySet().size()];
					int k = 0;
					for (Object key : ((Map) example).keySet()) {
						keys[k++] = key;
					}
					
					data = new String[results.size()][keys.length];
					for (int i = 0; i < results.size(); i++) {
						Map row = (Map) results.get(i);
						for (int j = 0; j < keys.length; j++) {
							data[i][j] = toString( row.get(keys[j]) );
						}
					}
				} else {
					data = new String[results.size()][1];
					for (int i = 0; i < results.size(); i++) {
						Object row = results.get(i);
						data[i][0] = toString(row);
					}
				}
			}
			
			this.fireTableStructureChanged();
		}
		
		public void setError(Throwable e) {
			data = new String[1][1];
			data[0][0] = e.toString();
			
			this.fireTableStructureChanged();
		}
		
		public int getColumnCount() {
			return data.length == 0 ? 0 : data[0].length;
		}

		public int getRowCount() {
			return data.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return data[rowIndex][columnIndex];
		}
		
	}

}
