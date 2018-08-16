package jackson.inheritance.jpa.specification.search;

import com.blito.exceptions.NotAllowedException;

import javax.persistence.criteria.*;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OperationService {

	public static <T> Predicate doOperation(Operation operation, Object value, CriteriaBuilder cb, Root<T> root,
											String field) {

		switch (operation) {
		case eq:
			return cb.equal(joinQueryBuilder(field, root), value);
		case ge:
			return cb.ge(joinQueryBuilder(field, root), (Number) value);
		case gt:
			return cb.gt(joinQueryBuilder(field, root),  (Number) value);
		case le:
			return cb.le(joinQueryBuilder(field, root), (Number) value);
		case like:
			return cb.like(joinQueryBuilder(field, root), '%' + value.toString() + '%');
		case lt:
			return cb.lt(joinQueryBuilder(field, root), (Number) value);
		case neq:
			return cb.notEqual(joinQueryBuilder(field, root), value);
		default:
			return cb.or();
		}
	}

	public static <T> Predicate doOperation(Operation operation, Timestamp value, CriteriaBuilder cb, Root<T> root,
			String field) {
		switch (operation) {
		case ge:
			return cb.greaterThanOrEqualTo(joinQueryBuilder(field, root), value);
		case gt:
			return cb.greaterThan(joinQueryBuilder(field, root), value);
		case le:
			return cb.lessThanOrEqualTo(joinQueryBuilder(field, root), value);
		case lt:
			return cb.lessThan(joinQueryBuilder(field, root), value);
		default:
			throw new NotAllowedException("Operation not allowed");
		}
	}

	private static <T, U> Path<U> joinQueryBuilder(String field, Root<T> root) {
		List<String> splitted = Arrays.stream(field.split("-")).collect(Collectors.toList());
		if (splitted.size() == 1)
			return root.get(field);
		String query_field = splitted.remove(splitted.size() - 1);
		return recursiveJoin(root, splitted.stream().reduce((s1, s2) -> String.valueOf(new StringBuilder().append(s1).append("-").append(s2))).get()).get(query_field);

	}
	
	private static <Z,X,T> Join<Z,X> recursiveJoin(Root<T> root,String query)
	{
		List<String> splitted = Arrays.stream(query.split("-")).collect(Collectors.toList());
		if (splitted.size() == 1)
			return root.join(query);
		String query_field = splitted.remove(splitted.size() - 1);
		return recursiveJoin(root, splitted.stream().reduce((s1, s2) -> String.valueOf(new StringBuilder().append(s1).append("-").append(s2))).get()).join(query_field);
	
	}

}
