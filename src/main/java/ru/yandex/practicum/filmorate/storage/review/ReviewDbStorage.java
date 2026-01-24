package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.review.Review;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewDbStorage extends BaseRepository implements ReviewStorage {
    private final RowMapper<Review> mapper;
    private static final String INSERT_QUERY =
            "INSERT INTO reviews (content, is_positive, user_id, film_id, useful, created_at) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE reviews SET content = ?, is_positive = ?, user_id = ?, film_id = ?, useful = ? WHERE id = ?";
    private static final String SELECT_QUERY =
            "SELECT * FROM reviews WHERE id = ?";
    private static final String DELETE_QUERY =
            "DELETE FROM reviews WHERE id = ?";
    private static final String FIND_BY_FILM_ID =
			"SELECT id, content, is_positive, user_id, film_id, useful, created_at " +
            "FROM reviews " +
            "WHERE film_id = ? " +
			"ORDER BY useful DESC " +
            "LIMIT ?";
    private static final String FIND_All =
			"SELECT id, content, is_positive, user_id, film_id, useful, created_at " +
            "FROM reviews " +
			"ORDER BY useful DESC " +
            "LIMIT ?";

	public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
		super(jdbc);
		this.mapper = mapper;
	}


	@Override
	public Review createReview(Review review) {
		var id = super.insert(INSERT_QUERY,
			review.getContent(),
			review.isPositive(),
			review.getUserId(),
			review.getFilmId(),
			0,
			review.getCreatedAt());

		review.setId(id);
		return review;
	}

	@Override
	public Optional<Review> getReview(Long reviewId) {
		return jdbc.query(SELECT_QUERY, mapper, reviewId).stream().findFirst();
	}

	@Override
	public void deleteReview(Long id) {
		super.update(DELETE_QUERY, id);
	}

    @Override
    public Review updateReview(Review review) {
        super.update(UPDATE_QUERY,
                review.getContent(),
                review.isPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful(),
                review.getId());

		return getReview(review.getId()).get();
	}

	@Override
	public List<Review> getReviewsByFilmId(Long filmId, int limit) {
		return jdbc.query(FIND_BY_FILM_ID, mapper, filmId, limit);
	}

	@Override
	public List<Review> getAllReviews(int limit) {
		return jdbc.query(FIND_All, mapper, limit);
	}

}
