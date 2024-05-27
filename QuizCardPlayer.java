package memoryToast;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import static javax.swing.SwingUtilities.invokeLater;
public class QuizCardPlayer {
	private static final Dimension FRAME_SIZE = new Dimension(300, 300);
    private static final Dimension MINIMUM_FRAME_SIZE = new Dimension(200, 200);

    private int deckIndex;
    private boolean isAnswerShown;
    private Deck deck;
    private JButton correctButton, showAnswerButton, wrongButton;
    private JFrame frame;
    private JLabel label;
    private JPanel contentPane;
    private JTextArea textArea;
    
    private DeckViewer deckViewer;


    public QuizCardPlayer(Deck deck){
        this.deck = deck;
    }

    void build(){
        SwingUtilities.invokeLater(
                () -> {
                    buildFrame();
                    buildContentPane();
                    try {
                    	buildLabel();
                    	buildTextArea();
                        buildButtonPanel();
                        displayFrame();
                        showAnswerButton.requestFocusInWindow();
                    }
                    catch (IndexOutOfBoundsException e) {
                    	buildDoneLabel();
                    	displayFrame();
                    }
                }
        );
    }

    private void buildButtonPanel(){
            showAnswerButton = new JButton("Show answer");
            showAnswerButton.addActionListener(ev -> invokeLater(getAction()));
            correctButton = new JButton();
            correctButton.addActionListener(ev -> {
            	deck.setNumCorrect(deck.getNumCorrect() + 1);
            	deck.editCardData(deckIndex, null, null, 1);
                invokeLater(getAction());
            });
            correctButton.setText("<html>Right<br>" + getCard().nextStudyTime(1) + " day(s)<html>");
            correctButton.setVisible(false);
            wrongButton = new JButton();
            wrongButton.addActionListener(ev -> {
            	deck.setNumWrong(deck.getNumWrong() + 1);
            	deck.editCardData(deckIndex, null, null, 0.5);
                invokeLater(getAction());
            });
            wrongButton.setText("<html>Wrong<br>" + getCard().nextStudyTime(0.5) + " day(s)<html>");
            wrongButton.setVisible(false);

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(showAnswerButton);
            buttonPanel.add(correctButton);
            buttonPanel.add(wrongButton);
            buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPane.add(BorderLayout.SOUTH, buttonPanel);
    }

    private void buildContentPane(){
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 15, 0, 15));
        frame.setContentPane(contentPane);
    }

    private void buildFrame(){
        frame = new JFrame("Quiz card Player - " + deck.getFileName());
        frame.setMinimumSize(MINIMUM_FRAME_SIZE);
        frame.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        closeFrame();
                    }
                }
        );
    }

    private void buildLabel(){
        label = new JLabel("<html>Current card's progress: "+(int)(getCard().getCurrentProgress()*100)+"%<br>Question:<html>");
        label.setFont(FontConstants.labelFont);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPane.add(BorderLayout.NORTH, label);
    }
    
    private void buildDoneLabel(){
        label = new JLabel("You have memorized this deck for now.");
        label.setFont(FontConstants.labelFont);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPane.add(BorderLayout.NORTH, label);
    }

    private void buildTextArea(){
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        deckIndex=0;
        textArea.setText(getCard().getQuestion());
        textArea.setFont(FontConstants.textAreaFont);
        JScrollPane jsp = new JScrollPane(textArea);
        jsp.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPane.add(BorderLayout.CENTER, jsp);

    }
    
    private void closeFrame(){
        frame.dispose();
        deck.setIsTestRunning(false);
        deck.setNumCorrect(0);
        deck.setNumWrong(0);
        deckViewer.refreshPanel();
    }

    private void displayFrame(){
        frame.setSize(FRAME_SIZE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private QuizCard getCard() {
    	if (deck.getQuizCardList().get(deckIndex).studyYet()) return deck.getQuizCardList().get(deckIndex);
    	else {
    		deckIndex++;
    		return getCard();
    	}
    }
    
    void registerDeckViewer(DeckViewer newDeckViewer){
        deckViewer = newDeckViewer;
    }
    
    /** toFront - brings this frame in the JVM to the front. */
    void toFront(){
        SwingUtilities.invokeLater(frame::toFront);
    }

    private Runnable getAction() {
        if (deckIndex == deck.getQuizCardList().size())
            return QuizCardPlayer.this::closeFrame;

        if ((deckIndex == deck.getQuizCardList().size()-1) && isAnswerShown)
            return this::showResults;
        
        if (isAnswerShown)
        	return this::showNextCard;

        return this::showAnswer;
    }
    
    private void showAnswer(){
    	label.setText("<html>Current card's progress: "+(int)(getCard().getCurrentProgress()*100)+"%<br>Answer:<html>");
        textArea.setText(getCard().getAnswer());
        isAnswerShown = true;
        showAnswerButton.setVisible(false);
        correctButton.setVisible(true);
        correctButton.requestFocusInWindow();
        wrongButton.setVisible(true);
    }

    private void showNextCard(){
    	try {
    		deckIndex++;
        	textArea.setText(getCard().getQuestion());
        	label.setText("Question:");
        	isAnswerShown = false;
        	showAnswerButton.setText("Show answer");
        	showAnswerButton.setVisible(true);
        	showAnswerButton.requestFocusInWindow();
        	correctButton.setVisible(false);
        	wrongButton.setVisible(false);
    	}
    	catch (IndexOutOfBoundsException e) {
    		deckIndex--;
    		showResults();
    	}
    }

    private void showResults(){
    	label.setText("Results:");
    	textArea.setText("You got " + deck.getNumCorrect() + " correct and " + deck.getNumWrong() + " wrong.");
    	showAnswerButton.setText("End");
    	showAnswerButton.setVisible(true);
    	showAnswerButton.requestFocusInWindow();
    	correctButton.setVisible(false);
    	wrongButton.setVisible(false);
    	deckIndex++;
    }
}
