package memoryToast;
public class QuizCard {
	private String question;
    private String answer;
    private double progress;
    private long firstTime;
    private long lastTime;
    
    public QuizCard(String f, String b){
        setQuestion(f);
        setAnswer(b);
    }
    
    public QuizCard(String f, String b, double p){
        setQuestion(f);
        setAnswer(b);
        setProgress(p);
    }

	public QuizCard(String f, String b, double p, boolean isnew){
        setQuestion(f);
        setAnswer(b);
        setProgress(p);
        if (isnew) setFirstTime(System.currentTimeMillis());
    }
	
	public QuizCard(String f, String b, double p, long firstTime, long lastTime){
        setQuestion(f);
        setAnswer(b);
        setProgress(p);
        setFirstTime(firstTime);
        setLastTime(lastTime);
    }
	
	public double getCurrentProgress() {
		double ans = Math.pow(Math.E, (-0.63*(System.currentTimeMillis()-getLastTime())/86400000)/(Math.pow(1.1,(getLastTime()-getFirstTime())/86400000)))-1+progress;
		if (ans<0) return 0;
		return ans;
	}
	
	public boolean studyYet() {
		if(getCurrentProgress()<0.15) return true;
		return false;
	}
	
	public long nextStudyTime(double progress) {
		return Math.round((Math.pow(1.1,(System.currentTimeMillis()-getFirstTime())/86400000)*Math.log(1.15-progress))/-0.63);
	}
	
    // GETTERS
    String getAnswer(){
        return answer;
    }

    String getQuestion(){
        return question;
    }

    double getProgress() {
    	return progress;
    }
    
    public long getFirstTime() {
		return firstTime;
	}
    
    public long getLastTime() {
		return lastTime;
	}
    
    // SETTERS
    void setAnswer(String text){
        answer = text;
    }

    void setQuestion(String text){
        question = text;
    }
    
    void setProgress(double progress) {
    	this.progress=progress;
    	lastTime=System.currentTimeMillis();
    }
    
    public void setFirstTime(long firstTime) {
		this.firstTime = firstTime;
	}
    
    public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}
}
