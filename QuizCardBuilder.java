package memoryToast;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class QuizCardBuilder {
	private Deck deck;
	private boolean isCardModified;
	private boolean isCardUpdated;
    private JButton button1, button2;
    private JFileChooser fileChooser = new JFileChooser();
    private JFrame frame;
    private JTextArea answerText = new JTextArea();
    private JTextArea questionText = new JTextArea();
    private JPanel panel;
    private int i;

    private DeckViewer deckViewer;


    public QuizCardBuilder(Deck deck) {
        this.deck = deck;
    }

    /** addCard - adds a QuizCard to the current Deck. */
    private void addCard(){
    	deck.setIsModified(true);
        deck.addQuizCard(getQuestionText().getText(), getAnswerText().getText(),0,true);
        setQuestionText(null);
        setAnswerText(null);
        setCardModified(false);
    }
    
    private void editCard(int index) {
    	deck.editCardData(index, getQuestionText().getText(), getAnswerText().getText(), -1);
    	setCardUpdated(false);
    	close();
    }
    
    private void deleteCard(int index) {
    	int optionChosen = JOptionPane.showConfirmDialog(frame, "Do you want to delete this card?", "Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (optionChosen == JOptionPane.YES_OPTION) {
        	deck.deleteCard(index);
        	close();
        }
    }

    void build() {
        SwingUtilities.invokeLater(
                () -> {
                		
                        buildFrame();
                        buildContentPane();
                        buildLabel(new JLabel("<html>Please don't include any Enter key in the content of<br>the card, I don't have enough energy to fix that<html>"));
                        buildLabel(new JLabel("Question:"));
                        buildTextArea(questionText);
                        questionText.addKeyListener(
                        		new KeyAdapter() {
                                    @Override
                                    public void keyTyped(KeyEvent e) {
                                        setCardModified(true);
                                    }
                                }
                        		);
                        buildLabel(new JLabel("Answer:"));
                        buildTextArea(answerText);
                        questionText.addKeyListener(
                        		new KeyAdapter() {
                                    @Override
                                    public void keyTyped(KeyEvent e) {
                                    	setCardModified(true);
                                    }
                                }
                        		);
                        buildButtonPanel();
                        displayFrame();
                        questionText.requestFocusInWindow();
                }
        );

    }
    
    void build(int index) {
    	i=index;
    	SwingUtilities.invokeLater(
    			() -> {
    				buildFrame(index);
                    buildContentPane();
                    buildLabel(new JLabel("<html>Please don't include any Enter key in the content of<br>the card, I don't have enough energy to fix that<html>"));
                    buildLabel(new JLabel("Progress: " + (int)(deck.getQuizCardList().get(index).getCurrentProgress()*100)+"%"));
                    buildLabel(new JLabel("New question:"));
                    buildTextArea(questionText, deck.getQuizCardList().get(index).getQuestion());
                    questionText.addKeyListener(
                    		new KeyAdapter() {
                                @Override
                                public void keyTyped(KeyEvent e) {
                                    setCardUpdated(true);
                                }
                            }
                    		);
                    buildLabel(new JLabel("New answer:"));
                    buildTextArea(answerText, deck.getQuizCardList().get(index).getAnswer());
                    questionText.addKeyListener(
                    		new KeyAdapter() {
                                @Override
                                public void keyTyped(KeyEvent e) {
                                    setCardUpdated(true);
                                }
                            }
                    		);
                    buildButtonPanel(index);
                    displayFrame();
                    questionText.requestFocusInWindow();
    			}
    	);
    }

    private void buildButtonPanel() {
        button1 = new JButton("Add");
        button1.setAlignmentX(Component.LEFT_ALIGNMENT);
        button1.addActionListener(ev -> addCard());
        panel.add(button1);
    }
    
    private void buildButtonPanel(int index) {
        button1 = new JButton("Update");
        button1.setAlignmentX(Component.LEFT_ALIGNMENT);
        button1.addActionListener(ev -> editCard(index));
        button2 = new JButton("Delete");
        button2.setAlignmentX(Component.LEFT_ALIGNMENT);
        button2.addActionListener(ev -> deleteCard(index));
        panel.add(button1);
        panel.add(button2);
    }

    private void buildContentPane() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 0, 15));
        frame.setContentPane(panel);
    }

    private void buildFrame() {
        frame = new JFrame("Quiz card builder - " + deck.getFileName());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setMinimumSize(new Dimension(400, 400));
        frame.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        close();
                    }
                }
        );
    }
    
    private void buildFrame(int index) {
        frame = new JFrame("Quiz card builder - " + deck.getFileName() + " - " + deck.getQuizCardList().get(index).getQuestion());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setMinimumSize(new Dimension(400, 400));
        frame.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        close();
                    }
                }
        );
    }

    private void buildLabel(JLabel label) {
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setFont(FontConstants.labelFont);
        panel.add(label);
    }

    private void buildTextArea(JTextArea jTextArea) {
        jTextArea.setWrapStyleWord(true);
        jTextArea.setLineWrap(true);
        jTextArea.setFont(FontConstants.textAreaFont);
        JScrollPane jsp = new JScrollPane(jTextArea);
        jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(jsp);
    }
    
    private void buildTextArea(JTextArea jTextArea, String text) {
        jTextArea.setWrapStyleWord(true);
        jTextArea.setLineWrap(true);
        jTextArea.setFont(FontConstants.textAreaFont);
        jTextArea.append(text);
        JScrollPane jsp = new JScrollPane(jTextArea);
        jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(jsp);
    }
    
    private void showConfirm() {
    	int optionChosen = JOptionPane.showConfirmDialog(frame, "Do you want to save this card?", "Save",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (optionChosen == JOptionPane.YES_OPTION) {
            save();
        }
        if (optionChosen != JOptionPane.CANCEL_OPTION) {
        	SwingUtilities.invokeLater(frame::dispose);
        }
    }
    
    private void showCardConfirm() {
    	int optionChosen = JOptionPane.showConfirmDialog(frame, "Do you want to save this card?", "Save",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (optionChosen == JOptionPane.YES_OPTION) {
            editCard(i);
        }
        if (optionChosen != JOptionPane.CANCEL_OPTION) {
        	SwingUtilities.invokeLater(frame::dispose);
        }
    }

    private void close(){
    	if (isCardModified()) {
    		showConfirm();
    	}
    	else if (isCardUpdated()) showCardConfirm();
    	else SwingUtilities.invokeLater(frame::dispose);
    	deckViewer.refreshPanel();
    }

    void registerDeckViewer(DeckViewer newDeckViewer){
        deckViewer = newDeckViewer;
    }
    
    private void displayFrame() {
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    

    /** save - Saves the current Deck under the same name, if previously saved. If the Deck is new,
     * then saveAs is invoked */
    private void save(){
        if(deck.getFileName().equals("New deck")){
            saveAs();
        }else{
            if(getQuestionText().getText().length() > 0){
                addCard();
                System.out.println("From save");
            }
            deck.save(deck.getFileLocation());
            deck.setIsModified(false);
        }
        close();
    }

    /** saveAs - User gets to choose the filename that stores the current Deck */
    private void saveAs(){
        if(fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            if(getQuestionText().getText().length() > 0){
                addCard();
            }
            deck.save(fileChooser.getSelectedFile().getAbsolutePath());
            deck.setFileName(fileChooser.getSelectedFile().getName());
            setTitle(deck.getFileName());
            deck.setIsModified(false);
        }
    }

    // GETTERS
    private JTextArea getAnswerText() {
        return answerText;
    }

    JTextArea getQuestionText() {
        return questionText;
    }
    
    public boolean isCardModified() {
		return isCardModified;
	}
    
    public boolean isCardUpdated() {
		return isCardUpdated;
	}

    // SETTERS
    private void setAnswerText(String text) {
        SwingUtilities.invokeLater(() -> answerText.setText(text));
    }

    void setTextAreaEditability(boolean isEditable){
        questionText.setEditable(isEditable);
        answerText.setEditable(isEditable);
        button1.setEnabled(isEditable);
    }

    private void setTitle(String newTitle){
        SwingUtilities.invokeLater(() -> frame.setTitle("Quiz Card Builder - " + newTitle));
    }

    private void setQuestionText(String text) {
        SwingUtilities.invokeLater(() -> questionText.setText(text));
    }
    
	public void setCardModified(boolean isCardModified) {
		this.isCardModified = isCardModified;
	}

	public void setCardUpdated(boolean isCardUpdated) {
		this.isCardUpdated = isCardUpdated;
	}
}
