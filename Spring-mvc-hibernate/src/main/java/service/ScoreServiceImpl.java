package service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import dao.ScoreDao;
import model.Score;

@Service
public class ScoreServiceImpl implements ScoreService {

	@Autowired
	@Qualifier("scoreDao")
	private ScoreDao dao;
	
	public ScoreServiceImpl(){}
	
	@Override
	public Score findById(int id) {
		return dao.getScore(id);
	}

	@Override
	public void saveScore(Score score) {
		dao.create(score);
	}

	@Override
	public void updateScore(Score score) {
		dao.update(score);
	}

	@Override
	public void deleteScoreById(int id) {
		dao.delete(id);
	}

	@Override
	public List<Score> findAllScores() {
		return dao.listScores();
	}

}
