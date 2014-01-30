package client_gui;

/*
 * ClientGUI.java
 *
 */
 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
 
public class ClientGUI {
    
    JFrame frame;
    JLabel label;
    
    JLabel connected_label;
    JTextField cname_input;
    JTextField begin_input;
    JTextField end_input;
    
    JPanel results_inner;
    JLabel result_table;
    
    public static void main(String[] args) {
    	ClientGUI gui = new ClientGUI();
    	gui.go();
    }
    
    public void go() {
    	frame = new JFrame();
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	Container a = frame.getContentPane();
    	a.setBackground(new Color(68,68,68));
    	
    	/*
    	 * Build the header.
    	 */
    	JPanel p_header = new JPanel();
    	p_header.setBorder(new EmptyBorder(10,10,10,10));
    	p_header.setBackground(new Color(68,68,68));
        p_header.setLayout(new BorderLayout());
        
        LogoPanel lp = new LogoPanel();
        lp.setPreferredSize(new Dimension(100,64));
        
        p_header.add(BorderLayout.WEST,lp);
        p_header.add(BorderLayout.CENTER,new JLabel("<html><p style='font-size:20px;color:#eeeeee;'>Twitter sentiment analysis application</p></html>"));
    	
    	frame.getContentPane().add(BorderLayout.NORTH,p_header);
    	
    	/*
    	 * Build the content.
    	 */
    	JPanel panel_content = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	panel_content.setBorder(new EmptyBorder(10,10,10,10));
    	panel_content.setBackground(new Color(68,68,68));
    	
    	
    	
    	// the connection panel
    	JPanel connection_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	connection_panel.setPreferredSize(new Dimension(340,300));
    	connection_panel.setBorder(new EmptyBorder(10,10,10,10));
    	connection_panel.setBackground(new Color(68,68,68));
    	
    	JLabel tp1 = new JLabel("<html><p style='font-size:15px;color:#eeeeee;'>Connect to server:</p></html>");
    	tp1.setPreferredSize(new Dimension(300,50));
    	connection_panel.add(tp1);
    	
    	this.connected_label = new JLabel("<html><p style='font-size:15px;color:#ff0000;'>disconnected</p></html>");
    	this.connected_label.setPreferredSize(new Dimension(300,35));
    	connection_panel.add(connected_label);
    	
    	JPanel do_con = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	do_con.setPreferredSize(new Dimension(300,40));
    	do_con.setBackground(new Color(68,68,68));
    	
    	JButton connect =  new JButton("Connect");
    	connect.addActionListener(new ConnectListener());
    	
    	JButton disconnect = new JButton("Disconnect");
    	disconnect.addActionListener(new DisconnectListener());
    	
    	do_con.add(connect);
    	do_con.add(disconnect);
    	
    	connection_panel.add(do_con);
    	
    	panel_content.add(connection_panel);
    	
    	
    	// the data panel
    	JPanel data_panel = new JPanel(new GridLayout(3,1,0,0));
    	data_panel.setPreferredSize(new Dimension(300,300));
    	data_panel.setBackground(new Color(68,68,68));
    	
    	JLabel tp2 = new JLabel("<html><p style='font-size:15px;color:#eeeeee;'>Enter Information for Analysis:</p></html>");
    	tp2.setPreferredSize(new Dimension(300,50));
    	data_panel.add(tp2);
    	
    	// data panel -> user insert
    	
    	JPanel user_insert = new JPanel();
    	user_insert.setPreferredSize(new Dimension(300,200));
    	user_insert.setBackground(new Color(68,68,68));
    	user_insert.setLayout(new GridLayout(3,2,0,17));

    	user_insert.add(new JLabel("<html><p style='font-size:12px;color:#eeeeee;'>Company name:</p></html>"));
    	cname_input = new JTextField(25);
    	user_insert.add(cname_input);
    	
    	user_insert.add(new JLabel("<html><p style='font-size:12px;color:#eeeeee;'>Begin:</p></html>"));
    	begin_input = new JTextField(25);
    	user_insert.add(begin_input);
    	
    	user_insert.add(new JLabel("<html><p style='font-size:12px;color:#eeeeee;'>End:</p></html>"));
    	end_input = new JTextField(25);
    	user_insert.add(end_input);
    	
    	data_panel.add(user_insert);
    	
    	
    	JPanel sa_wrap = new JPanel();
    	sa_wrap.setBorder(new EmptyBorder(15,15,15,15));
    	sa_wrap.setBackground(new Color(68,68,68));
    	sa_wrap.setLayout(new FlowLayout());
    	
    	JButton start_analysis = new JButton("Start analysis");
    	start_analysis.addActionListener(new AnalysisListener());
    	
    	sa_wrap.add(start_analysis);
    	data_panel.add(sa_wrap);
    	
    	panel_content.add(data_panel);
    	
    	frame.getContentPane().add(BorderLayout.CENTER,panel_content);
    	
    	/*
    	 * Build the results area
    	 */
    	JPanel res_panel = new JPanel(new BorderLayout());
    	res_panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(238,238,238)));
    	res_panel.setBackground(new Color(68,68,68));
    	res_panel.setPreferredSize(new Dimension(1000,330));
    	
    	JLabel res = new JLabel("<html><p style='font-size:15px;color:#eeeeee;'>Results:</p></html>");
    	res.setBorder(new EmptyBorder(15,15,15,15));
    	res_panel.add(BorderLayout.NORTH, res);
    	
    	this.results_inner = new JPanel();
    	results_inner.setBorder(new EmptyBorder(15,15,15,15));
    	results_inner.setBackground(new Color(68,68,68));
    	
    	result_table = new JLabel("");
    	results_inner.add(result_table);
    	
    	res_panel.add(BorderLayout.CENTER, results_inner);
    	
    	frame.getContentPane().add(BorderLayout.SOUTH,res_panel);
    	
    	frame.setSize(1024,768);
    	frame.setVisible(true);
    }
    
    class LogoPanel extends JPanel {
    	public void paintComponent(Graphics g) {
    		Image image = new ImageIcon("images/twit.png").getImage();
    		g.drawImage(image,0,0,this);
    	}
    }
    
    class ConnectListener implements ActionListener {
    	public void actionPerformed(ActionEvent event) {
    		// call connection method
    		
    		// on connection success:
    		connected_label.setText("<html><p style='font-size:15px;color:#00ff00;'>connected</p></html>");
    	}
    }
    
    class DisconnectListener implements ActionListener {
    	public void actionPerformed(ActionEvent event) {
    		// call disconnect method
    		
    		// on disconnection:
    		connected_label.setText("<html><p style='font-size:15px;color:#ff0000;'>disconnected</p></html>");
    	}
    }
    
    class AnalysisListener implements ActionListener {
    	public void actionPerformed(ActionEvent event) {
    		// start analysis
    		System.out.println(cname_input.getText()+" "+begin_input.getText()+" "+end_input.getText());
    		
    		
    		// on getting result:
        	String[] row = new String[]{"ID","Company Name","Start Date","End Date","Analysis Result"};
        	
        	for(int i=0;i<5;i++) {
        		row[i] = "<td style='font-size:12px;color:#eeeeee;'>&nbsp;&nbsp;&nbsp;"+row[i]+"&nbsp;&nbsp;&nbsp;</td>";
        	}
    		
        	// insert results here
        	String[] row_res = new String[]{"1","Microschrott","2014-01-01","2014-01-17","0.0"};
        	
        	for(int i=0;i<5;i++) {
        		row_res[i] = "<td style='font-size:12px;color:#eeeeee;'>&nbsp;&nbsp;&nbsp;"+row_res[i]+"&nbsp;&nbsp;&nbsp;</td>";
        	}
        	
        	result_table.setText("<html><table><tr>"+implodeArray(row,"")+"</tr><tr>"+implodeArray(row_res,"")+"</tr></table></html>");
    		
    	}
    }
    
    public static String implodeArray(String[] inputArray, String glueString) {

    	/** Output variable */
    	String output = "";

    	if (inputArray.length > 0) {
    		StringBuilder sb = new StringBuilder();
    		sb.append(inputArray[0]);

    		for (int i=1; i<inputArray.length; i++) {
    			sb.append(glueString);
    			sb.append(inputArray[i]);
    		}

    		output = sb.toString();
    	}

    	return output;
    }
    
}