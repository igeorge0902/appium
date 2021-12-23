package main.java.qa.ios.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.*;

import org.testng.internal.ClassHelper;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;

public class CreateXml extends JFrame{
	
	private  XmlSuite suite = new XmlSuite ();
    private XmlTest test = new XmlTest (suite);
	   
    private static String value6 = new String();
    private static XmlClass testClass;
    private static XmlInclude included;

    private static Map<String, XmlInclude> includes = new HashMap<String, XmlInclude>();
	private Set<String> includesSet = Collections.synchronizedSet(new HashSet<String>());
    
    private static Set<String> testSet_ = Collections.synchronizedSet(new HashSet<String>());
    private ArrayList<XmlClass> classes = new ArrayList<XmlClass>();

    private static Map<String, String> parameters = new HashMap<String, String>();
    private static Map<String, XmlClass> testClasses_ = new HashMap<String, XmlClass>();

	private Set<String> setlisteners = new HashSet<String>();
	
	private Set<String> excludesSet = Collections.synchronizedSet(new HashSet<String>());
	private List<String> excludes = new ArrayList<String>();
	
	private List<XmlInclude> includesList = new ArrayList<XmlInclude>();
	
	private Class<?> m_class = null;
	private Method[] methods = null;
	
    ADDButtonHandler addhandler = new ADDButtonHandler();
    SHOWButtonHandler showhandler = new SHOWButtonHandler();
    REMOVEButtonHandler removehandler = new REMOVEButtonHandler();

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JButton ADD;
	JButton SHOW;
	JButton REMOVE;
	JButton CREATE;
	JPanel panel;
    JFrame frame;
    JLabel label;
	
	JLabel label1,label2,label3,label4,label5,label6,label7,label8;

	final JTextField text1,text2,text3,text4,text5,text6,text7,text8;

	CreateXml(){
		
        setTitle("TestNG xml creator");
		
	setLayout(null);
	
	label1 = new JLabel();
	label1.setText("Suite Name:");
	text1 = new JTextField(20);
	
	label2 = new JLabel();
	label2.setText("Listener:");
	text2 = new JTextField(20);
	
	label3 = new JLabel();
	label3.setText("Test name:");
	text3 = new JTextField(20);
	
	label4 = new JLabel();
	label4.setText("Parameter name:");
	text4 = new JTextField(20);
	
	label5 = new JLabel();
	label5.setText("Parameter value:");
	text5 = new JTextField(20);
	
	label6 = new JLabel();
	label6.setText("Test class name:");
	text6 = new JTextField(20);
	
	label7 = new JLabel();
	label7.setText("Include methods:");
	text7 = new JTextField(20);
	
	label8 = new JLabel();
	label8.setText("Exclude methods:");
	text8 = new JTextField(20);
	
	ADD=new JButton("ADD");
	SHOW=new JButton("Show");
	REMOVE=new JButton("Remove");
	CREATE=new JButton("Create xml");
	
	label1.setBounds(20,20,150,20);
	text1.setBounds(200,20,200,20);
	
	label2.setBounds(20,50,150,20);
	text2.setBounds(200,50,200,20);
	
	label3.setBounds(20,80,150,20);
	text3.setBounds(200,80,200,20);
	
	label4.setBounds(20,110,150,20);
	text4.setBounds(200,110,200,20);
	
	label5.setBounds(20,140,150,20);
	text5.setBounds(200,140,200,20);
	
	label6.setBounds(20,170,150,20);
	text6.setBounds(200,170,200,20);
	
	label7.setBounds(20,200,150,20);
	text7.setBounds(200,200,200,20);
	
	label8.setBounds(20,230,150,20);
	text8.setBounds(200,230,200,20);
	
	ADD.setBounds(20,300,100,20);
	REMOVE.setBounds(110,300,100,20);
	SHOW.setBounds(220,300,100,20);
	CREATE.setBounds(330,300,100,20);
	
	add(label1);
	add(text1);
	add(label2);
	add(text2);
	add(label3);
	add(text3);
	add(label4);
	add(text4);
	add(label5);
	add(text5);
	add(label6);
	add(text6);
	add(label7);
	add(text7);
	add(label8);
	add(text8);
	add(ADD);
	add(SHOW);
	add(REMOVE);
	add(CREATE);	

	ADD.addActionListener(addhandler);
	SHOW.addActionListener(showhandler);
	REMOVE.addActionListener(removehandler);
	
}
	
    //This is the internal class that defines what the above Action Listener
    //will do when the test button is pressed.
    class ADDButtonHandler implements ActionListener{
           public void actionPerformed(ActionEvent e){

        	String value1=text1.getText();
        	String value2=text2.getText();
        	String value3=text3.getText();
        	String value4=text4.getText();
        	String value5=text5.getText();
        	value6=text6.getText();
        	String value7=text7.getText();
        	String value8=text8.getText();
        		
    	    	suite.setVerbose(10);
        	    suite.setName (value1);

				if (!value2.isEmpty()){     
				setlisteners.add(value2);
				suite.getListeners().removeAll(suite.getListeners());					
    	    	suite.getListeners().addAll(setlisteners);
    	    	suite.setListeners(suite.getListeners());
        	    }
				
				else suite.setListeners(suite.getListeners());
        	    
				if (!value3.isEmpty()) {
        	    test.setName (value3);
				}
				
        	    if (!value4.isEmpty() || !value5.isEmpty()) {
        	    parameters.put(value4, value5);        
        	    test.setParameters(parameters);
        	    
        	    } 
        	    
        	    if (!value6.isEmpty()) {
        	    	m_class = ClassHelper.forName(value6); 
        	    	
        	    	if (m_class !=null){  
        				testClass = new XmlClass(value6);

                    	if (testSet_.contains(value6) == false) {
                    		
                    	testSet_.add(value6);
        				testClasses_.put(testClass.getName(), testClass);
        				
        				classes.clear();        				
                    	classes.addAll(testClasses_.values());
                    	System.out.println(testClasses_.keySet().size());
                               	
                    	test.setXmlClasses(classes);
                    	}
        	    	}
                    	
            			else if (m_class==null) {
                    	    
            				JOptionPane.showMessageDialog(null, "No matching Test Class to add", "What happened?", JOptionPane. INFORMATION_MESSAGE);        				

            			}
                    	
        			}       	    
        	    
        	    
        		if (!value7.isEmpty()){
        	    	included = new XmlInclude(value7);
        			m_class = ClassHelper.forName(value6);       	    	
        			
        			if (m_class !=null){
        		
        				methods = m_class.getDeclaredMethods();
        				
        				
        				for (Method method : methods) {
        					
        					if (method.getName().equals(included.getName())) {
        						
        						if (includesSet.contains(value7) == false) {
        					
        						includesSet.add(value7);
        						System.out.println("IncSet: "+includesSet);
        						
        						includes.put(included.getName(), included);
        						System.out.println("IncMap: "+includes.values() +", keys:" + includes.keySet());
        						        						
        						excludesSet.remove(method.getName());
        						
        						includesList.clear();
        		        	    includesList.addAll(includes.values());
        						
        		        	    for (int i = 0;i<test.getClasses().size();i++) {
        						
        		        	    	test.getClasses().get(i).setIncludedMethods(includesList);
        		        		
        		        			}
        						}
        					} 
        				}

        			}
        			else if (!value7.isEmpty() && m_class==null) {
                	    
        				JOptionPane.showMessageDialog(null, "No matching Test Class to add", "What happened?", JOptionPane. INFORMATION_MESSAGE);        				

        			}
            	    
        		} 
       	    
        		if (value8.isEmpty()){
            	    	m_class = ClassHelper.forName(value6);       	    	
            			if (m_class !=null){
            				
        				methods = m_class.getDeclaredMethods();
            				
        				for (Method method : methods) {
        					        					
        					if(method.getName().equals(value7)==false) {
        				        						
        				excludesSet.add(method.getName());
        				System.out.println("ExSet_: "+excludesSet);
        				
        				if (value7.isEmpty()) {
        				
            				excludes.clear();
            				excludes.addAll(excludesSet);
            				excludes.remove(includesList);           				
                				            				
            				for (int i = 0; i<testClasses_.keySet().size();i++){
            					if (testClasses_.keySet().contains(testClass.getName())) {
            					
            						classes.get(classes.size()-1).setExcludedMethods(excludes);
                					System.out.println(classes.get(classes.size()-1));

            					}
            					//test.getClasses().get(i).setExcludedMethods(excludes);
            				}
        					
        				} else {
        				
        				excludesSet.removeAll(includesSet);
        				System.out.println("ExSet: "+excludesSet);
        				
        				excludes.clear();
        				excludes.addAll(excludesSet);

            				
        				for (int i = 0; i<testClasses_.keySet().size();i++){
        					
    						classes.get(classes.size()-1).setExcludedMethods(excludes);
        					System.out.println(classes.get(classes.size()-1));

        					//test.getClasses().get(i).setExcludedMethods(excludes);
        				}

        			}
        		} 
        	}
    	}
            			
            			else if (m_class==null) {
                    	    
            				JOptionPane.showMessageDialog(null, "No matching Test Class to add", "What happened?", JOptionPane. INFORMATION_MESSAGE);        				

            				}
            			
	}
        		
        		if (!value8.isEmpty()){
        			m_class = ClassHelper.forName(value6);       	    	
        			if (m_class !=null){
        				
        				methods = m_class.getDeclaredMethods();
        				
        				for (Method method : methods) {
        					        					
        					if(method.getName().equals(value8)) {
        			    				
						includesSet.remove(value8);
						includes.keySet().remove(value8);
						
						includesList.clear();
		        	    includesList.addAll(includes.values());
		        	    
		        	    excludesSet.add(value8);
		        	    excludes.clear();
        				excludes.addAll(excludesSet);
		        	    
						
		        	    for (int i = 0;i<test.getClasses().size();i++) {
						
		        	    	test.getClasses().get(i).setIncludedMethods(includesList);
		        	    	test.getClasses().get(i).setExcludedMethods(excludes);
		        		
		        				}
        					
        					}
        				}
        			
        			}
        		}

		JOptionPane.showMessageDialog(null, "Done", "What happened?", JOptionPane. INFORMATION_MESSAGE);        				
           
           }
    }
    
    class SHOWButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e){
			System.out.println(suite.toXml ());
        }
    }
    
    class REMOVEButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e){

	//TaskDialog taskDialog = new TaskDialog(frame, "");

    final JDialog dialog = new JDialog(frame, "Click a button", true);
  
    dialog.setLocationRelativeTo(frame); 
    dialog.setContentPane(createSimpleDialogBox());
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    
    dialog.pack();
    dialog.setVisible(true);

        }
    }
    
    private JPanel createSimpleDialogBox() {
    	
    	final int numButtons = 4;
        JRadioButton[] radioButtons = new JRadioButton[numButtons];
        final ButtonGroup group = new ButtonGroup();

        JButton showItButton = null;

        final String testClasses = "testClasses";
        final String parameter = "parameters";
        final String includedMethod = "includedMethods";
        final String excludedMethod = "excludedMethods";

        radioButtons[0] = new JRadioButton("Remove Test Classes");
        radioButtons[0].setActionCommand(testClasses);

        radioButtons[1] = new JRadioButton("Remove Parameters");
        radioButtons[1].setActionCommand(parameter);

        radioButtons[2] = new JRadioButton("Remove Included Methods");
        radioButtons[2].setActionCommand(includedMethod);

        radioButtons[3] = new JRadioButton("Remove Excluded Methods");
        radioButtons[3].setActionCommand(excludedMethod);

        for (int i = 0; i < numButtons; i++) {
            group.add(radioButtons[i]);
        }
        radioButtons[0].setSelected(true);

        showItButton = new JButton("Do it!");
        showItButton.addActionListener(new ActionListener() {
        	
            public void actionPerformed(ActionEvent e) {
            	
                String command = group.getSelection().getActionCommand();

                if (command == testClasses) {
                	
                	value6 = JOptionPane.showInputDialog("Enter the Test Class you want to remove:");
                	
                	if (null == value6) {
        				
                		JOptionPane.showMessageDialog(null, "Enter Test Class name to remove", "What happened?", JOptionPane. INFORMATION_MESSAGE);        				
                		}

                	
                	else if (ClassHelper.forName(value6) == null){
        				
        				JOptionPane.showMessageDialog(null, "No Test Classes found", "What happened?", JOptionPane. INFORMATION_MESSAGE);        				        				
        			}                     	

        			else if (!testClasses_.keySet().contains(value6)){
        				
        				JOptionPane.showMessageDialog(null, "No Test Classes found to remove", "What happened?", JOptionPane. INFORMATION_MESSAGE);        				        				

        			} 
        			else {
        				
        				//testClass = new XmlClass(value6);
            	    	m_class = ClassHelper.forName(value6);       	    	
            	    	
        				testSet_.remove(value6);
        				testClasses_.keySet().remove(testClass.getName());
        				
        				classes.clear();
        				classes.addAll(testClasses_.values());
        				
        				test.setXmlClasses(classes); 
        				
        				methods = m_class.getDeclaredMethods();
        				
        				for (Method method : methods) {
        					
        					if(includesSet.contains(method.getName()) ) {
        			    				
						includesSet.remove(method.getName());
						includes.keySet().remove(method.getName());
						
						includesList.clear();
		        	    includesList.addAll(includes.values());
		        	    
		        	    for (int i = 0;i<test.getClasses().size();i++) {
							
		        	    	test.getClasses().get(i).setIncludedMethods(includesList);
		        		
		        				}
        					}
        					
		        	    excludesSet.remove(method.getName());
		        	    excludes.clear();
        				excludes.addAll(excludesSet);
        				System.out.println("ExSet: "+excludesSet);
        				        				
		        	    for (int i = 0;i<test.getClasses().size();i++) {
							
		        	    	test.getClasses().get(i).setExcludedMethods(excludes);
		        		
		        				}

        					}
        				
                	    JOptionPane.showMessageDialog(null, "Test Class removed", "What happened?", JOptionPane. INFORMATION_MESSAGE);
                    	value6 = JOptionPane.showInputDialog("Enter the Test Class you want to remove:");
                    	
        				} 
        			}

                 if (command == parameter) {
                	String parameterName = JOptionPane.showInputDialog("Enter the name of parameter you would like to remove: ");
        			if (parameters.values().contains(parameterName) == false){
        				
        				JOptionPane.showMessageDialog(null, "No Parameters found", "What happened?", JOptionPane. INFORMATION_MESSAGE);        				
        				
        			}
        			else {
        				parameters.values().remove(parameterName);      				
        				JOptionPane.showMessageDialog(null, "Parameter removed", "What happened?", JOptionPane. INFORMATION_MESSAGE);        				
        			}

                } if (command == includedMethod) {

                	

                } if (command == excludedMethod) {
                	
                }

                return;

            }
        });

        return createPane(testClasses + ":", radioButtons, showItButton);
    }
    
    /** Sets the text displayed at the bottom of the frame. */
    void setLabel(String newText) {
        label.setText(newText);
    }
    
    /**
     * Used by createSimpleDialogBox and createFeatureDialogBox
     * to create a pane containing a description, a single column
     * of radio buttons, and the Show it! button.
     */
    private JPanel createPane(String description,
                              JRadioButton[] radioButtons,
                              JButton showButton) {

        int numChoices = radioButtons.length;
        JPanel box = new JPanel();
        JLabel label = new JLabel(description);

        box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));
        box.add(label);

        for (int i = 0; i < numChoices; i++) {
            box.add(radioButtons[i]);
        }

        JPanel pane = new JPanel(new BorderLayout());
        pane.add(box, BorderLayout.PAGE_START);
        pane.add(showButton, BorderLayout.PAGE_END);
        return pane;
    }


    
    public static void main(String arg[]){

	CreateXml frame=new CreateXml();
    // set up the jframe, then display it
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setPreferredSize(new Dimension(500, 400));
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
	
	}

}
