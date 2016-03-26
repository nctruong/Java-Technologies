package service;

import java.util.List;

import model.Score;


public interface ScoreService {
	Score findById(int id);
	
	void saveScore(Score score);
	
	void updateScore(Score score);
	
	void deleteScoreById(int id);

	List<Score> findAllScores(); 
	
}
