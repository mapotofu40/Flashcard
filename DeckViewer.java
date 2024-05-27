package memoryToast;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class DeckViewer {
	private static final Dimension MINIMUM_FRAME_SIZE = new Dimension(400, 400);
	
	private Deck deck;
	private JFrame frame;
	private JFileChooser fileChooser = new JFileChooser();
	private JPanel panel;
	private JLabel label;
	private JButton button1, button2, button3;
	private JList<String> list;
	private DefaultListModel<String> listModel;
	
	private QuizCardBuilder quizCardBuilder;
	private QuizCardPlayer quizCardPlayer;
	
	public DeckViewer(Deck deck) {
        this.deck = deck;
    }

	
	void build() {
        SwingUtilities.invokeLater(
                () -> {
                        buildFrame();
                        buildContentPane();
                        buildMenuBar();
                        buildButtonPanel();
                        displayFrame();
                }
        );

    }
	
	private void buildFrame() {
        frame = new JFrame("Deck viewer - " + deck.getFileName());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setMinimumSize(MINIMUM_FRAME_SIZE);
        frame.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        close();
                    }
                }
        );
    }
	
	private void buildMenuBar() {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        if (!deck.getFileName().equals("New deck"))
        file.add(New);
        file.add(Open);
        file.add(Exit);

        JMenu card = new JMenu("Deck");
        card.add(ShuffleDeck);
        card.add(Play);

        jMenuBar.add(file);
        jMenuBar.add(card);
        frame.setJMenuBar(jMenuBar);
    }
	
	private void buildButtonPanel() {
        button2 = new JButton("New card");
        button2.setAlignmentX(Component.LEFT_ALIGNMENT);
        button2.addActionListener(ev -> newCard());
        panel.add(button2);
        if (deck.getIsModified()) {
        	button3 = new JButton("Save deck");
            button3.setAlignmentX(Component.LEFT_ALIGNMENT);
            button3.addActionListener(ev -> save());
            panel.add(button3);
        }
    }
	
	private void buildList() {
		listModel = new DefaultListModel<>();
		updateCardList();
		list = new JList<>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 1) {
                    int index = list.locationToIndex(evt.getPoint());
                    if (index >= 0) {
                        editCard(index);
                    }
                }
            }
        });
		list.setAlignmentX(Component.LEFT_ALIGNMENT);
		JScrollPane jsp = new JScrollPane(list);
        jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(jsp);
	}
	
	private void buildContentPane() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 0, 15));
        frame.setContentPane(panel);
    }
	
	private void buildLabel() {
		label = new JLabel("Overall progress: "+(int) (deck.getProgress()*100)+"%");
        label.setFont(FontConstants.labelFont);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(BorderLayout.NORTH, label);
	}
	
	private void createQuizCardBuilder(){
        quizCardBuilder = new QuizCardBuilder(deck);
        quizCardBuilder.registerDeckViewer(this);   // registers the callback
    }
	
	private void createQuizCardPlayer(){
        quizCardPlayer = new QuizCardPlayer(deck);
        quizCardPlayer.registerDeckViewer(this);   // registers the callback
    }
	
	private void updateCardList() {
        listModel.clear();
        for (QuizCard card : deck.getQuizCardList()) {
            listModel.addElement("<html>"+card.getQuestion()+"\t"+(int)(card.getCurrentProgress()*100)+"%<html>");
        }
    }
	
	private void displayFrame() {
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
	
	void refreshPanel() {
		buildMenuBar();
		buildContentPane();
        buildLabel();
        buildList();
        buildButtonPanel();
        frame.setTitle("Deck viewer - " + deck.getFileName());
        displayFrame();
	}
	
	private void newDeck() {
		Runnable newd = new Runnable() {
			@Override
			public void run() {
				int optionChosen = JOptionPane.YES_OPTION;

		        if(optionChosen != JOptionPane.CANCEL_OPTION){
		            deck = new Deck();
		            setTitle("New deck");
		        }
		        refreshPanel();
			}
		};
		if (deck.getIsModified()) showConfirm(newd);
		else newd.run();
	}
	
	private void openFile(){
		Runnable open = new Runnable() {
			@Override
			public void run() {
				int optionChosen = JOptionPane.YES_OPTION;
		        if(optionChosen != JOptionPane.CANCEL_OPTION && fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION){
		            deck = new Deck();
		            deck.readFile(fileChooser.getSelectedFile().getAbsolutePath());
		            refreshPanel();
		        }
			}
		};
		if (deck.getIsModified()) showConfirm(open);
		else open.run();
    }
	
	private void newCard() {
		createQuizCardBuilder();
        quizCardBuilder.build();
	}
	
	private void editCard(int index) {
		createQuizCardBuilder();
		quizCardBuilder.build(index);
	}
	
	private void setTitle(String newTitle){
        SwingUtilities.invokeLater(() -> frame.setTitle("Deck viewer - " + newTitle));
    }
	
	private Action New = new AbstractAction("New deck"){
        @Override
        public void actionPerformed(ActionEvent ev){
            newDeck();
        }
    };
    
    private Action Open = new AbstractAction("Open"){
        @Override
        public void actionPerformed(ActionEvent ev){
            openFile();
        }
    };
    
    private Action Play = new AbstractAction("Begin test"){
        @Override
        public void actionPerformed(ActionEvent ev){
        	Runnable play = new Runnable() {
        		@Override
        		public void run() {
        			// Allows the user to open a file if no file is already open
                    if(deck.getQuizCardList().size() == 0) {
                        openFile();
                    }

                    // Prevents window from popping up if there's no QuizCards to use
                    if(deck.getQuizCardList().size() > 0) {
                        if (deck.getIsTestRunning()) {
                            Toolkit.getDefaultToolkit().beep();
                            quizCardPlayer.toFront();
                        } else {
                            deck.setIsTestRunning(true);
                            createQuizCardPlayer();
                            quizCardPlayer.build();
                        }
                    }
        		}
        		
        	};
        	if (deck.getIsModified()) showConfirm(play);
        	else play.run();
        }
    };
    
    private Action ShuffleDeck = new AbstractAction("Shuffle deck"){
        @Override
        public void actionPerformed(ActionEvent ev){
            deck.shuffle();
        }
    };
    
    private Action Exit = new AbstractAction("Quit"){
        @Override
        public void actionPerformed(ActionEvent ev){
            close();
        }
    };
    
    private void save(){
        if(deck.getFileName().equals("New deck")){
            saveAs();
        }else{
            deck.save(deck.getFileLocation());
            deck.setIsModified(false);
        }
        refreshPanel();
    }
    
    private void saveAs(){
        if(fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            deck.save(fileChooser.getSelectedFile().getAbsolutePath());
            deck.setFileName(fileChooser.getSelectedFile().getName());
            setTitle(deck.getFileName());
            deck.setIsModified(false);
        }
    }
    
    private void showConfirm(Runnable onCancel) {
    	int optionChosen = JOptionPane.showConfirmDialog(frame, "Do you want to save this deck?", "Save",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (optionChosen == JOptionPane.YES_OPTION) {
            save();
        }
        if (optionChosen != JOptionPane.CANCEL_OPTION) {
        	onCancel.run();
        }
    }
    
	public void close() {
		if (deck.getIsModified()) showConfirm( () -> System.exit(0) );
		else System.exit(0);
	}
}
