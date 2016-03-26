package dao;

import java.io.Serializable;
import java.util.List;

import model.Score;

public interface ScoreDao extends Serializable{

	/**
	 * This is the method to be used to create a record in the Score table.
	 */
	public void create(Score score);

	/**
	 * This is the method to be used to list down a record from the Score
	 * table corresponding to a passed Score id.
	 */
	public Score getScore(Integer id);

	/**
	 * This is the method to be used to list down all the records from the
	 * Score table.
	 */
	public List<Score> listScores();

	/**
	 * This is the method to be used to delete a record from the Score table
	 * corresponding to a passed Score id.
	 */
	public void delete(Integer id);

	/**
	 * This is the method to be used to update a record into the Score table.
	 */
	public void update(Score score);
}
