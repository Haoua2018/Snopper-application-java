import java.sql.*;
import java.util.StringTokenizer;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

class Snooper extends JPanel implements ActionListener {
	JLabel index;
	JComboBox tableList;
	String tableNames[], tableNamesAsOne;
	JLabel id;
	TextArea content;

    Connection link;
    String myURL = "jdbc:odbc:";
    java.sql.DatabaseMetaData patrol;
    
	/*	***********	*/
	/*	CONSTRUCTOR	*/
	/*	***********	*/
    
    public Snooper(String sourceDB) throws SQLException {
    	super(new BorderLayout());
        connect(sourceDB);
    	inspect(sourceDB);
    }
    
	/*	***************************	*/
	/*	CONNECTING TO THE DATA BASE	*/
	/*	***************************	*/
    
  /*  public void connect(String source) {
    	try {
    		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            //DriverManager.registerDriver(new java.sql.DriverManager());
    		
                link = DriverManager.getConnection(myURL+source);
    	} catch (SQLException e) {
    		System.out.println("Connection error: " + e.getMessage());
    	}   catch (ClassNotFoundException ex) {
                Logger.getLogger(Snooper.class.getName()).log(Level.SEVERE, null, ex);
            }
    }*/
    public void connect(String source) {
        try {
    String protocol = "jdbc:mysql:";
    String sourceDB = "/snooper?useLegacyDatetimeCode=false&serverTimezone=Europe/Paris";
            link = DriverManager.getConnection(this.protocol+"//localhost"+this.sourceDB, this.username, this.password);
            inspect();
        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }

	/*	***********************************	*/
	/*	INSPECTING THE DATA BASE STRUCTURE	*/
	/*	***********************************	*/
    
    public void inspect(String source) throws SQLException {
        patrol = link.getMetaData();
        id = new JLabel("DATA BASE " + source + " (User: " + patrol.getUserName() + ")");
        add(id,BorderLayout.NORTH);
        add(new JLabel("TABLES: "),BorderLayout.WEST);
        ResultSet answer = patrol.getTables(null, null, null, null);
        while (answer.next()) {
                if (answer.wasNull() == false) {
                	tableNamesAsOne = tableNamesAsOne + answer.getString("TABLE_NAME") + " ";
                }
        }
        answer.close();
        StringTokenizer st = new StringTokenizer(tableNamesAsOne," ");
        tableNames = new String[st.countTokens()];
    	while (st.hasMoreTokens()) {
    		tableNames[st.countTokens()-1] = st.nextToken();
    	}
    	tableList = new JComboBox(tableNames);
        tableList.setSelectedIndex(0);
        tableList.addActionListener(this);
    	add(tableList, BorderLayout.EAST);
    	content = new TextArea();
    	add(content, BorderLayout.SOUTH);
    	updateFields(tableNames[tableList.getSelectedIndex()]);
    }
    
	/*	***************************	*/
	/*	LISTENING TO THE COMBO BOX	*/
	/*	***************************	*/
    
    public void actionPerformed(ActionEvent e) {
    	JComboBox cb = (JComboBox)e.getSource();
    	String tableName = (String)cb.getSelectedItem();
    	updateFields(tableName);
    }
        
	/*	***********************	*/
	/*	UPDATING THE FIELD LIST	*/
	/*	***********************	*/
    
    protected void updateFields(String name) {
    	content.setText("");	// empty the content first
    	try {
    		ResultSet answer = patrol.getTables(null, null, null, null);
    		while (answer.next()) {
                if (answer.wasNull() == false) {
                	if (name.equalsIgnoreCase(answer.getString("TABLE_NAME")) == true) {
                		content.append("CATEGORY = " + answer.getString("TABLE_CAT") + '\n');
                		content.append("TYPE = " + answer.getString("TABLE_TYPE") + '\n');
                		content.append("SCHEMA = " + answer.getString("TABLE_SCHEM") + '\n');
                		content.append("REMARKS = " + answer.getString("REMARKS"));
                	}
                }
    		}
    	}catch (SQLException e) {
    		System.out.println("Meta data error");
        }
    }
    
    private static void createAndShowGUI(String source) throws SQLException {
    	//Create and set up the window.
    	//Create and set up the window.
     JFrame frame = new JFrame("Snooper");
     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     frame.setSize(500,500);
     //zone de saisie de la source
         TextField nomsource= new TextField("Nom de la source de donnée",20);
      //  création d'un panneau
         JPanel Pan= new JPanel();  
         Pan.setSize(50,50);
    //ajout de la zone de texte au panneau
         Pan.add(nomsource);    
    //Création du bouton qui ferme la base de donnée
   Button   close= new Button("close");  
   //ajout du bouton au panneau
   Pan.add(close);
   
   close.addActionListener(frame.d);
        //(JFrame.EXIT_ON_CLOSE);
   //ajout du panneau au frame
         frame.setContentPane(Pan);
      //Create and set up the content pane.
     JComponent newContentPane = new Snooper(source);
     newContentPane.setOpaque(true);
     frame.setContentPane(newContentPane);
     
        //FRAME Name 
frame.setTitle(source);
        //Display the window.
     frame.pack();
     frame.setVisible(true);
 }
    
    //appel à destroy
    
    
	/*	***********	*/
	/*	LAUNCHING	*/
	/*	***********	*/
    
    
    public static void main(String[] args) throws SQLException {
		createAndShowGUI(args[0]);
    }
}
