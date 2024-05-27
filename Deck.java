package memoryToast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Deck {
	private File file;
    private List<QuizCard> quizCardList = new ArrayList<>();
    private String fileName = "New deck";
    private boolean isModified;
    private boolean isTestRunning;
    private int numCorrect;
    private int numWrong;

    private static final String QUIZ_CARD_TERMINATOR = "\n29rje2r9\n";
    private static final String QUIZ_CARD_QASEPARATOR = "\te23bf0hj\t";
    private static final String QUIZ_CARD_PSEPARATOR = "\t8gb08v7t\t";
    private static final String QUIZ_CARD_FSEPARATOR = "\tuih34dfg\t";
    private static final String QUIZ_CARD_LSEPARATOR = "\t43y8fsu9\t";

    /** addQuizCard - creates and adds a QuizCard to quizCardList */
    void addQuizCard(String q, String a){
        // Prevents any parsing exceptions occurring when opening a file
        if(q.length() == 0){
            q = " ";
        }
        if(a.length() == 0){
            a = " ";
        }
        quizCardList.add(new QuizCard(q, a));
    }
    
    void addQuizCard(String q, String a, double p){
        // Prevents any parsing exceptions occurring when opening a file
        if(q.length() == 0){
            q = " ";
        }
        if(a.length() == 0){
            a = " ";
        }
        quizCardList.add(new QuizCard(q, a, p));
    }
    
    void addQuizCard(String q, String a, double p, boolean isnew){
        // Prevents any parsing exceptions occurring when opening a file
        if(q.length() == 0){
            q = " ";
        }
        if(a.length() == 0){
            a = " ";
        }
        quizCardList.add(new QuizCard(q, a, p, isnew));
    }

    void addQuizCard(String q, String a, double p, long firstTime, long lastTime){
        // Prevents any parsing exceptions occurring when opening a file
        if(q.length() == 0){
            q = " ";
        }
        if(a.length() == 0){
            a = " ";
        }
        quizCardList.add(new QuizCard(q, a, p, firstTime, lastTime));
    }

    /** parseData - parses the data from an input String using specified terminators and separators. */
    private void parseData(String unparsedData) {
        String[] stageOne = unparsedData.split(QUIZ_CARD_TERMINATOR);

        for (String stageTwo : stageOne) {
            String[] stageThree = stageTwo.split(QUIZ_CARD_QASEPARATOR);
            String[] stageFour = stageThree[1].split(QUIZ_CARD_PSEPARATOR);
            String[] stageFive = stageFour[1].split(QUIZ_CARD_FSEPARATOR);
            String[] stageSix = stageFive[1].split(QUIZ_CARD_LSEPARATOR);
            
            addQuizCard(stageThree[0], stageFour[0], Double.parseDouble(stageFive[0]), Long.parseLong(stageSix[0]), Long.parseLong(stageSix[1]));
        }
    }

    /** readFile - loads in the data from a saved deck into quizCardList */
    void readFile(String fileLocation){
        file = new File(fileLocation);
        setFileName(file.getName());
        assert file.canRead();
        try(BufferedReader input = new BufferedReader(new FileReader(file))){
            int letterNumber;
            StringBuilder dataToParse = new StringBuilder();
            while((letterNumber = input.read()) != -1){
                dataToParse.append((char) letterNumber);
            }
            parseData(dataToParse.toString());
        }catch(IOException ioEx){
            ioEx.printStackTrace();
        }
    }

    /** save - saves the Deck to specified file location */
    void save(String fileLocation){
        file = new File(fileLocation);
        assert file.canWrite();
        try (BufferedWriter output = new BufferedWriter(new FileWriter(file))) {
            for(QuizCard quizCard : quizCardList){
                output.write(quizCard.getQuestion() + QUIZ_CARD_QASEPARATOR + quizCard.getAnswer() + QUIZ_CARD_PSEPARATOR + quizCard.getProgress() + QUIZ_CARD_FSEPARATOR +  quizCard.getFirstTime() + QUIZ_CARD_LSEPARATOR + quizCard.getLastTime() + QUIZ_CARD_TERMINATOR);
            }
        }catch(IOException ioEx){
            ioEx.printStackTrace();
        }
    }
    
    void editCardData(int index, String newQuestion, String newAnswer, double newProgress) {
    	file = new File(getFileLocation());
    	String line = null;
    	List<String> lines = new ArrayList<String>();
    	assert file.canRead();
    	assert file.canWrite();
    	
    	try{
    		BufferedReader input = new BufferedReader(new FileReader(file));
    		int i=0;
    		while ((line = input.readLine()) != null) {
                if (i==index*2) {
                	if (newQuestion==null) newQuestion = getQuizCardList().get(index).getQuestion();
                	else getQuizCardList().get(index).setQuestion(newQuestion);
                	if (newAnswer==null) newAnswer = getQuizCardList().get(index).getAnswer();
                	else getQuizCardList().get(index).setAnswer(newAnswer);
                	if (newProgress==-1) newProgress = getQuizCardList().get(index).getProgress();
                	else getQuizCardList().get(index).setProgress(newProgress);
                	line = newQuestion + QUIZ_CARD_QASEPARATOR + newAnswer + QUIZ_CARD_PSEPARATOR + newProgress + QUIZ_CARD_FSEPARATOR + getQuizCardList().get(index).getFirstTime() + QUIZ_CARD_LSEPARATOR + getQuizCardList().get(index).getLastTime();
                }
                lines.add(line);
                i++;
            }
            input.close();
            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            for(String s : lines)
                output.write(s+"\n");;
            output.flush();
            output.close();
        }catch(IOException ioEx){
            ioEx.printStackTrace();
        }
    }
    
    void deleteCard(int index) {
    	file = new File(getFileLocation());
    	String line = null;
    	List<String> lines = new ArrayList<String>();
    	assert file.canRead();
    	assert file.canWrite();
    	
    	try{
    		BufferedReader input = new BufferedReader(new FileReader(file));
    		int i=0;
    		while ((line = input.readLine()) != null) {
                if (i != index*2 && i != index*2+1)
                lines.add(line);
                i++;
            }
            input.close();
            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            for(String s : lines)
                output.write(s+"\n");;
            output.flush();
            output.close();
        }catch(IOException ioEx){
            ioEx.printStackTrace();
        }
    	getQuizCardList().remove(index);
    }

    /** shuffle - Shuffles the deck in place. If saved, the quiz cards will be saved in the new shuffled order. */
    void shuffle(){
        Collections.shuffle(quizCardList);
    }


    // GETTERS
    String getFileLocation(){
        return file.getAbsolutePath();
    }

    String getFileName(){
        return fileName;
    }

    boolean getIsModified(){
        return isModified;
    }

    boolean getIsTestRunning(){
        return isTestRunning;
    }

    int getNumCorrect(){
        return numCorrect;
    }

    int getNumWrong(){
        return numWrong;
    }

    List<QuizCard> getQuizCardList(){
        return quizCardList;
    }
    
    double getProgress() {
    	double sum=0;
    	for(QuizCard q : getQuizCardList()) {
    		sum+=q.getCurrentProgress();
    	}
    	return sum/getQuizCardList().size();
    }

    // SETTERS
    void setFileName(String fileName) {
        if(fileName.contains(".")){
            fileName = fileName.split("\\.")[0];
        }
        this.fileName = fileName;
    }

    void setIsModified(boolean newValue){
        isModified = newValue;
    }

    void setIsTestRunning(boolean newValue){
        isTestRunning = newValue;
    }

    void setNumCorrect(int newValue){
        numCorrect = newValue;
    }

    void setNumWrong(int newValue){
        numWrong = newValue;
    }
}
