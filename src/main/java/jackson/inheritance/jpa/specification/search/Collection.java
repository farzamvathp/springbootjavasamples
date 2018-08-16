package jackson.inheritance.jpa.specification.search;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class Collection<T> extends AbstractSearchViewModel<T> {

	public List<String> values;
	private List<Object> vals;

	@Override
	public Specification<T> action() {
		return (root, query, cb) ->{
			query.distinct(true);
			return  vals.stream().map(v -> cb.isMember(v, root.get(field)))
					.reduce((p1, p2) -> cb.and(p1, p2)).get();
		};
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<Object> values) {
//		if (super.field.equals("offers")) {
//			vals = values;
//		}
		vals = values;
	}
}
